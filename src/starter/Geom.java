//David Govorko, 06/28/2017
package starter;
// Geom combines an NGeom with velocity and acceleration vectors, also angle and delta angle in degrees
import org.lwjgl.opengl.GL11;

/*Geom contains the following fields and methods:
 * FIELDS:
 * 	DOUBLE Angle: Angle of rotation in DEGREES
 * 	DOUBLE DeltaAngle: Change in the angle of rotation
 * 	VECTOR2D Offset: Offset of the origin of the NGeom
 * 	VECTOR2D Velocity: Direction vector
 * 	NGeom Geometry: The base geometric unit 
 * 
 * CONSTRUCTORS:
 * 	Geom(): Default stationary point at 0,0
 *  Geom(double angle, double deltaAngle, Vector2d offset, Vector2d velocity, NGeom geometry): All fields fillable
 *    
 * METHODS:
 *  Getters/setters: For all fields
 * 	String toString(): To string
 * 	Void update(): Velocity and rotation incrementing
 *  Double minDistPointToLine(Vector2d point): Finds min distance from a point to a line (needed for collision)
 *  Vector2d getVectorPos(int i): Calculates the desired vector's position (rotation applied)
 *  Void blindRenderGeom(): Figures out what the NGeom is then uses the appropriate renderer
 *  Void render[Insert Geometry Here](): Renders the geometry
 *  Geom blindCollision(Geom B): Figures out what the NGeom is then uses the appropriate collision() to find collision point
 *  Geom blindCollisionDebug(Geom B): Debug of above
 *  Geom collisionRegPolyVsLine(Geom RegPolygon): Regular Polygon versus Line collision, will return a Point or a Line
 *  Geom collisionRegPolyVsLineDebug(Geom RegPolygon): Debug of above
 *  Geom collisionSquareVsCircle(Geom Square): Square versus Circle collision, will return Point
 *  Geom collisionSquareVsCircleDebug(Geom Square): Debug of above
 *  Geom collisionSquareVsCircleRefine(Geom Square, Vector2d OverEstimatedPoint, int Accuracy): Essentially it repeats the normal
 *       Square vs. Circle collision but at smaller time increments (lower velocity) to find a more accurate collision point
 *  Geom collisionSquareVsCircleRefineDebug(Geom Square, Vector2d OverEstimatedPoint, int Accuracy): Debug of above
 */
//TODO: should color be a field && should velocity/deltaAngle be here???
public class Geom {
	private double Angle;
	private double DeltaAngle;
	private Vector2d Offset;
	private Vector2d Velocity;
	private NGeom Geometry;
	
	public Geom(){// Default is a stationary point at 0,0
		Angle = 0;
		DeltaAngle = 0;
		Offset = new Vector2d(0,0);
		Velocity = new Vector2d(0,0);
		Geometry = new NGeom(0,new Vector2d[0]);		
	}
	
	public Geom(double angle, double deltaAngle, Vector2d offset, Vector2d velocity, NGeom geometry){
		Angle = angle;
		DeltaAngle = deltaAngle;
		Offset = offset;
		Velocity = velocity;
		Geometry = geometry;		
	}

	public double getAngle() {// Angle in degrees
		return Angle;
	}
	
	public double getAngleRad(){
		return Angle*Math.PI/180;
	}

	public void setAngle(double angle) {
		Angle = angle;
	}
	
	public void setAngleRad(double angle) {
		Angle = angle*180/Math.PI;
	}

	public double getDeltaAngle() {
		return DeltaAngle;
	}

	public void setDeltaAngle(double deltaAngle) {
		DeltaAngle = deltaAngle;
	}

	public Vector2d getOffset() {
		return Offset;
	}

	public void setOffset(Vector2d offset) {
		Offset = offset;
	}

	public Vector2d getVelocity() {
		return Velocity;
	}

	public void setVelocity(Vector2d velocity) {
		Velocity = velocity;
	}

	public NGeom getGeometry() {
		return Geometry;
	}

	public void setGeometry(NGeom geometry) {
		Geometry = geometry;
	}

	public String toString() {
		return "Geom [Angle = " + Angle + ", DeltaAngle = " + DeltaAngle + ", Offset = " + Offset + ", Velocity = " + Velocity
				+ ", Geometry = " + Geometry + "]";
	}
	
	public void update(){// Just adds velocity and DeltaAngle to original angle and offset
		Offset = Offset.add(Velocity);
		Angle += DeltaAngle;
		if (Math.abs(Angle) == 360){ Angle = 0;}// To avoid Int overflow
	}
	
	public double minDistPointToLine(Vector2d point){// Returns the minimum distance from a line to a point
		double distance = 0;
		if (this.getGeometry().getVertexes().length == 1){// Checks to see if the Geom is a line.
			Vector2d pointAtOrigin  = point.subtract(this.getOffset());			
			Vector2d lineOrigin = this.getGeometry().getVertexes()[0];
			
			return pointAtOrigin.subtract(lineOrigin).dot(lineOrigin.tangent())/lineOrigin.magnitude();
		} else {
			System.out.println("Geometry Mismatch Error, Line needed for minDistPointToLine() "
					+ "this has " +  this.getGeometry().getVertexes().length + " many sides.");
			return distance;
		}
	}
	
	public Vector2d getVectorPos(int i){// Gets specific vector's position.
		if (i < this.getGeometry().getVertexes().length && i > -1)// Safety check for vertexes values that are inside the matrix.
			return this.getGeometry().getVertexes()[i].rotate(this.getAngleRad()).add(this.getOffset());
		else if (i > this.getGeometry().getVertexes().length) {
			System.out.println("Vertex # Error, getVectorPos() vertex requested is > than # of vertexes in geometry!");
			return new Vector2d();
		}else {
			System.out.println("Vertex # Error, getVectorPos() vertex requested is negative!");
			return new Vector2d();
		}	
	}
	
	//TODO: Optimize order of if statements --> Lazy conditionals
	public void blindRenderGeom(){// Renders the geometry by guessing the geometry type first from values of NGeom's fields.
		
		if (Geometry.getVertexes().length == 0 && Geometry.getLDA() == 0) {//POINT V = 0 & LDA = 0
			this.renderPoint();
			
		}// LINE SEGMENT V = 1 & LDA = segment length
		else if (Geometry.getVertexes().length == 1 && Geometry.getLDA() > 0){
			this.renderLineSegment();
				
		}// LINE V = 1 & LDA = -1*(segment length)
		else if (Geometry.getVertexes().length == 1 && Geometry.getLDA() < 0){
			this.renderLine();
			
		}// CIRCLE V = 0 & LDA = Diameter
		else if (Geometry.getVertexes().length == 0 && Geometry.getLDA() > 0){
			this.renderCircle();
			
		}// REG. POLYGON V > 2 & LDA > 0
		else if (Geometry.getVertexes().length > 2 && Geometry.getLDA() > 0){
			this.renderPolygon();
			
		} else {// Catch for errors
			System.out.println("Geometry properties do not match any renderer yet, "
					+ "here is an attempt to print it out: " + Geometry.toString());
		}
		
	}// renderGeom()
	
	public void renderPoint(){// Renders a point
		GL11.glPushMatrix();
		
		// Rotation is not needed for a point
		// Drawing the Point at the offset
		GL11.glBegin(GL11.GL_POINTS);
		// Offset
		GL11.glVertex2d(Offset.getX(), Offset.getY());
		// End
		GL11.glEnd(); 
		GL11.glPopMatrix();
	}
	
	public void renderLineSegment(){// Renders a Line Segment
		GL11.glPushMatrix();
		//GL11.glLineWidth(3);0// Really only used for debugging.
		// Offset
		GL11.glTranslated(Offset.getX(), Offset.getY(), 0);
		// Rotate
		GL11.glRotated(Angle, 0, 0, 1);
		// Drawing the Line Segment
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(0 , 0);
		GL11.glVertex2d(Geometry.getVertexes()[0].getX() , Geometry.getVertexes()[0].getY());
		// End
		GL11.glEnd();
		// GL11.glLineWidth(1);// Really only used for debugging.
		GL11.glPopMatrix();	
	}
	
	public void renderLine(){// Renders a Line
		GL11.glPushMatrix();
		//GL11.glLineWidth(3);// Really only used for debugging.
		// Offset
		GL11.glTranslated(Offset.getX(), Offset.getY(), 0);
		// Rotate
		GL11.glRotated(Angle, 0, 0, 1);
		// Drawing the Line
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(Geometry.getVertexes()[0].unitize().getX()*Geometry.getLDA() , Geometry.getVertexes()[0].unitize().getY()*Geometry.getLDA()); //Starts at negative value
		GL11.glVertex2d(-1*Geometry.getVertexes()[0].unitize().getX()*Geometry.getLDA() , -1*Geometry.getVertexes()[0].unitize().getY()*Geometry.getLDA());
		// End
		GL11.glEnd();
		//GL11.glLineWidth(1);// Really only used for debugging.
		GL11.glPopMatrix();
	}
	
	//TODO: Maybe change the circle's edge resolution?
	public void renderCircle(){// Renders a Circle
		GL11.glPushMatrix();
		
		// Circle rotating excluded for now
		// Drawing the Circle
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for(int i = 0; i<250; i++) {// 250 = edge resolution (Maybe scale this to radius?)
			double angle = (i*2*Math.PI/250);
			GL11.glVertex2d(Offset.getX() + (Math.cos(angle) * Geometry.getLDA()/2), 
					Offset.getY() + (Math.sin(angle) * Geometry.getLDA()/2));// Offset accounted for here
		}
		// End
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void renderPolygon(){// Renders a polygon assuming vertexes are in order and first vertex becomes the origin
		GL11.glPushMatrix();
		
		// Offset
		GL11.glTranslated(Offset.getX(), Offset.getY(), 0);
		// Rotate
		GL11.glRotated(Angle, 0, 0, 1);
		// Drawing the Regular Polygon
		GL11.glBegin(GL11.GL_POLYGON);
		for (int i = 0; i < Geometry.getVertexes().length; i++)
			GL11.glVertex2d(Geometry.getVertexes()[i].getX() , Geometry.getVertexes()[i].getY());
		// End
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public Geom blindCollision(Geom B){// Returns the hypothetical collision point as a NGeom
		// maybe only have it return the vertexes and the offset, 
		// and let the other program decide weather or not to create a new Geom or just update one
		
		//  Square colliding into Circle
		if ((Geometry.getLDA() > 0 & Geometry.getVertexes().length == 0) & (B.getGeometry().getVertexes().length == 4 )){ 
			return collisionSquareVsCircle(B);
		}// RegPolygon Colliding into Line
		else if ((B.getGeometry().getVertexes().length > 2) & (getGeometry().getVertexes().length == 1) & (getGeometry().getLDA() < 0)) {
			return collisionRegPolyVsLine(B);	
		} else {// No collision detection code is made yet for this scenario
			System.out.println("BlindCollision Geom Error, there is no Collision Detection code yet for this geom: " + B.toString());
			return new Geom();
		}
		
	}
	
	public Geom blindCollisionDebug(Geom B){// Debug Version
		System.out.println("()()()()()blindCollision IS IN DEBUG MODE!()()()()()");
		System.out.print("This is the Geom in Question: " + B);
		
		//  Square colliding into Circle
		if ((Geometry.getLDA() > 0 & Geometry.getVertexes().length == 0) & (B.getGeometry().getVertexes().length == 4 )){ 
			return collisionSquareVsCircleDebug(B);
		} //RegPolygon Colliding into Line
		else if ((B.getGeometry().getVertexes().length > 2) & (getGeometry().getVertexes().length == 1) & (getGeometry().getLDA() < 0)) {
			return collisionRegPolyVsLineDebug(B);	
		} else { //no collision detection code is made yet for this scenario
			System.out.println("()()()()()There is no Collision Detection code yet for this scenario()()()()()");
			return new Geom();
		}
		
	}
	
	public Geom collisionRegPolyVsLine(Geom RegPolygon){// Performs a RegPolygon Versus Line collision detection that spits out colliding line/point.
		double temp = Integer.MAX_VALUE;
		Geom collideVert = new Geom();
		
		for (int i = 0; i < RegPolygon.getGeometry().getVertexes().length; i++) {// Returns closest vertex to line
			
			if (Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i))) <= temp) {
				
				if (Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i))) == temp){
					collideVert.getGeometry().setVert(new Vector2d[]{collideVert.getOffset().subtract(RegPolygon.getVectorPos(i))});
					collideVert.getGeometry().setLDA(collideVert.getGeometry().getVertexes()[0].magnitude());
					
				}// if current == temp	
				collideVert.setOffset(RegPolygon.getVectorPos(i));
				temp = Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i)));
				
			}// if <= temp
		}// For loop
		return collideVert;
	}
	
	public Geom collisionRegPolyVsLineDebug(Geom RegPolygon){// Debug version
		System.out.println("|||||collsiionRegPolyVsLine IS IN DEBUG MODE!|||||");
		
		double temp = Integer.MAX_VALUE;
		Geom collideVert = new Geom();
		
		for (int i = 0; i < RegPolygon.getGeometry().getVertexes().length; i++) {//returns closest vertex to line
			System.out.println("Min to vertex: " + this.minDistPointToLine(RegPolygon.getVectorPos(i)));//Debug
			
			if (Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i))) <= temp) {
				if (Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i))) == temp){
					System.out.println("Full Side of Polygon Collision Detected");//Debug
					
					collideVert.getGeometry().setVert(new Vector2d[]{collideVert.getOffset().subtract(RegPolygon.getVectorPos(i))});
					collideVert.getGeometry().setLDA(collideVert.getGeometry().getVertexes()[0].magnitude());
					System.out.println("Line LDA: " + collideVert.getGeometry().getLDA());//Debug
					System.out.println("Line direction vertex: " + collideVert.getGeometry().getVertexes()[0]);//Debug
					
				}// If current == temp	
				collideVert.setOffset(RegPolygon.getVectorPos(i));
				temp = Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i)));
				System.out.println("Temp min Distance: " + temp);//Debug
				System.out.println("|||||Collision point position: " + collideVert.getOffset() + " |||||");//Debug
				
			}// If current <= temp
		}//For loop
		return collideVert;
	}
	
	public Geom collisionSquareVsCircle(Geom Square){
		Vector2d Dir = Offset.subtract(Square.getOffset());
		Geom collideVert = new Geom();
		double tempX;
		double tempY;
		double baX = Dir.getX()*Math.cos(-Square.getAngleRad()) - Dir.getY()*Math.sin(-Square.getAngleRad());
		double baY = Dir.getX()*Math.sin(-Square.getAngleRad()) + Dir.getY()*Math.cos(-Square.getAngleRad());
		
		double halfLength = Square.getGeometry().getSideLength()/2;

		if ( baX < - halfLength) tempX = -halfLength;
		else if (baX >  halfLength) tempX = halfLength;
		else tempX = baX;

		if (baY < - halfLength) tempY = - halfLength;
		else if (baY > halfLength) tempY = halfLength;
		else tempY = baY;
		
		Vector2d temp = new Vector2d(tempX, tempY);
		
		collideVert = new Geom(0, 0, temp.rotate(Square.getAngleRad()).add(Square.getOffset()), new Vector2d(0,0), new NGeom());
		return collideVert;
	}
	
	public Geom collisionSquareVsCircleDebug(Geom Square){// Debug Version
		System.out.println("00000collisionSquareVsCircle IS IN DEBUG MODE!00000");// Debug only
		
		Vector2d Dir = Offset.subtract(Square.getOffset());
		System.out.println("Direction vector from square to circle center: " + Dir);// Debug only
		
		Geom collideVert = new Geom();
		double tempX;
		double tempY;
		
		double baX = Dir.getX()*Math.cos(-Square.getAngleRad()) - Dir.getY()*Math.sin(-Square.getAngleRad());
		double baY = Dir.getX()*Math.sin(-Square.getAngleRad()) + Dir.getY()*Math.cos(-Square.getAngleRad());
		System.out.println("baX : " + baX);// Debug only
		System.out.println("baY : " + baY);// Debug only
		
		double halfLength = Square.getGeometry().getSideLength()/2;		
		System.out.println("Half of the side's length : " + halfLength);// Debug only

		if ( baX < - halfLength) tempX = -halfLength;
		else if (baX >  halfLength) tempX = halfLength;
		else tempX = baX;

		if (baY < - halfLength) tempY = - halfLength;
		else if (baY > halfLength) tempY = halfLength;
		else tempY = baY;
		
		Vector2d temp = new Vector2d(tempX, tempY);
		System.out.println("temp vector: " + temp);// Debug only
		
		collideVert = new Geom(0, 0, temp.rotate(Square.getAngleRad()).add(Square.getOffset()), new Vector2d(0,0), new NGeom());
		System.out.println("00000Collision Vertex : " + collideVert + "00000");
		
		return collideVert;
	}
	
	public Geom collisionSquareVsCircleRefine(Geom Square, Vector2d OverEstimatedPoint, int Accuracy){// Accuracy = # of increments
		Geom ReverseSquare = new Geom();
		ReverseSquare.setGeometry(Square.getGeometry());
		Geom collideVert = new Geom();
		collideVert.setOffset(new Vector2d(-1*this.getGeometry().getLDA(), -1*this.getGeometry().getLDA()));
		double radiusComp = 0;
		
		ReverseSquare.setOffset(Square.getOffset().subtract(Square.getVelocity()));// Reset position to before collision.
		ReverseSquare.setAngle(Square.getAngle() - Square.getDeltaAngle());
		
		ReverseSquare.setVelocity((Square.getVelocity()).scalarMulti(1.0/Accuracy));// Increase step by step accuracy
		ReverseSquare.setDeltaAngle(Square.getDeltaAngle()/Accuracy);
		
		for (int i = 0; i < Accuracy; i++){// Find the closest point to the circle by comparing radius to point's distance to circle.
			ReverseSquare.update();
			
			Vector2d Dir = this.getOffset().subtract(ReverseSquare.getOffset());
			
			double tempX;
			double tempY; 
			
			double baX = Dir.getX()*Math.cos(-ReverseSquare.getAngleRad()) - Dir.getY()*Math.sin(-ReverseSquare.getAngleRad());
			double baY = Dir.getX()*Math.sin(-ReverseSquare.getAngleRad()) + Dir.getY()*Math.cos(-ReverseSquare.getAngleRad());
			
			double halfLength = ReverseSquare.getGeometry().getSideLength()/2;
			
			if ( baX < - halfLength) tempX = -halfLength;
			else if (baX >  halfLength) tempX = halfLength;
			else tempX = baX;

			if (baY < - halfLength) tempY = - halfLength;
			else if (baY > halfLength) tempY = halfLength;
			else tempY = baY;
			
			Vector2d temp = new Vector2d(tempX, tempY);
			
			radiusComp = (temp.rotate(ReverseSquare.getAngleRad()).add(ReverseSquare.getOffset())).dist(this.getOffset());
			
			collideVert = new Geom(0, 0, temp.rotate(ReverseSquare.getAngleRad()).add(ReverseSquare.getOffset()), new Vector2d(0,0), new NGeom());
			
			if (radiusComp <= this.getGeometry().getLDA()/2)
				i = Accuracy;
		}

		return collideVert;
	}
	
	public Geom collisionSquareVsCircleRefineDebug(Geom Square, Vector2d OverEstimatedPoint, int Accuracy){// Debug Version
		System.out.println("~~~~~collisionSquareVsCircleRefineDebug IS IN DEBUG MODE!~~~~~");// Debug Only
		System.out.println("With an accuracy of : " + Accuracy);// Debug Only
		
		Geom ReverseSquare = new Geom();
		ReverseSquare.setGeometry(Square.getGeometry());
		Geom collideVert = new Geom();
		collideVert.setOffset(new Vector2d(-1*this.getGeometry().getLDA(), -1*this.getGeometry().getLDA()));
		double radiusComp = 0;
		
		System.out.println("Velocity Magnitude is: " + Square.getVelocity().magnitude());// Debug Only
		
		ReverseSquare.setOffset(Square.getOffset().subtract(Square.getVelocity()));// Reset position to before collision.
		ReverseSquare.setAngle(Square.getAngle() - Square.getDeltaAngle());
		
		ReverseSquare.setVelocity((Square.getVelocity()).scalarMulti(1.0/Accuracy));// Increase step by step accuracy
		ReverseSquare.setDeltaAngle(Square.getDeltaAngle()/Accuracy);
		System.out.println("Reverse Square Info : " + ReverseSquare);// Debug Only
		System.out.println("Square Info : " + Square);// Debug Only
		
		for (int i = 0; i < Accuracy; i++){// Find the closest point to the circle by comparing radius to point's distance to circle.
			ReverseSquare.update();
			System.out.println("Start");
			System.out.println("Reverse Square Info Begin Loop : " + ReverseSquare);// Debug Only
			System.out.println("Loop Number : " + i);
			
			Vector2d Dir = this.getOffset().subtract(ReverseSquare.getOffset());
			System.out.println("Direction vector from Reverse square to circle center: " + Dir);// Debug only
			
			double tempX;
			double tempY; 
			
			double baX = Dir.getX()*Math.cos(-ReverseSquare.getAngleRad()) - Dir.getY()*Math.sin(-ReverseSquare.getAngleRad());
			double baY = Dir.getX()*Math.sin(-ReverseSquare.getAngleRad()) + Dir.getY()*Math.cos(-ReverseSquare.getAngleRad());
			System.out.println("baX : " + baX);// Debug only
			System.out.println("baY : " + baY);// Debug only
			
			double halfLength = ReverseSquare.getGeometry().getSideLength()/2;
			System.out.println("Half of the side's length : " + halfLength);// Debug only
			
			if ( baX < - halfLength) tempX = -halfLength;
			else if (baX >  halfLength) tempX = halfLength;
			else tempX = baX;

			if (baY < - halfLength) tempY = - halfLength;
			else if (baY > halfLength) tempY = halfLength;
			else tempY = baY;
			
			Vector2d temp = new Vector2d(tempX, tempY);
			System.out.println("temp vector: " + temp);// Debug only
			
			radiusComp = (temp.rotate(ReverseSquare.getAngleRad()).add(ReverseSquare.getOffset())).dist(this.getOffset());
			System.out.println("radius Comparison : " + radiusComp);// Debug only
			
			collideVert = new Geom(0, 0, temp.rotate(ReverseSquare.getAngleRad()).add(ReverseSquare.getOffset()), new Vector2d(0,0), new NGeom());
			
			if (radiusComp <= this.getGeometry().getLDA()/2)
				i = Accuracy;
				
			System.out.println("Collision Vertex : " + collideVert);// Debug only
		}
		System.out.println("Final COLLISION POINT: " + collideVert);
		System.out.println("~~~~~End~~~~~");
		
		return collideVert;
	}
	
	public Geom collisionRegPolyVsLineRefine(Geom RegPolygon, int Accuracy){// Slower (more accurate) step by step check for polygon crossing line
		Geom ReverseRegPolygon= new Geom();// See Debug for detailed comments
		ReverseRegPolygon.setGeometry(RegPolygon.getGeometry());
		Geom collideGeom = new Geom();
		collideGeom.setOffset(new Vector2d(-1*this.getGeometry().getLDA(), -1*this.getGeometry().getLDA()));
		
		double linePolyCenterDist;
		double tempMin = Integer.MAX_VALUE;
		
		ReverseRegPolygon.setOffset(RegPolygon.getOffset().subtract(RegPolygon.getVelocity()));
		ReverseRegPolygon.setAngle(RegPolygon.getAngle() - RegPolygon.getDeltaAngle());

		ReverseRegPolygon.setVelocity((RegPolygon.getVelocity()).scalarMulti(1.0/Accuracy));
		ReverseRegPolygon.setDeltaAngle(RegPolygon.getDeltaAngle()/Accuracy); 
		
		for (int i = 0; i < Accuracy; i++){
			ReverseRegPolygon.update();
			linePolyCenterDist = this.minDistPointToLine(ReverseRegPolygon.getOffset());
			
			for (int j = 0; j < ReverseRegPolygon.getGeometry().getVertexes().length; j++) {
				
				if (Math.abs(this.minDistPointToLine(ReverseRegPolygon.getVectorPos(j))) <= tempMin) {
					if (Math.abs(this.minDistPointToLine(ReverseRegPolygon.getVectorPos(j))) == tempMin){
						collideGeom.getGeometry().setVert(new Vector2d[]{collideGeom.getOffset().subtract(ReverseRegPolygon.getVectorPos(j))});
						collideGeom.getGeometry().setLDA(collideGeom.getGeometry().getVertexes()[0].magnitude());
					}
					collideGeom.setOffset(ReverseRegPolygon.getVectorPos(j));
					tempMin = Math.abs(this.minDistPointToLine(ReverseRegPolygon.getVectorPos(j)));
				}
			if (linePolyCenterDist/this.minDistPointToLine(collideGeom.getOffset()) <= 0) 
				i = Accuracy;
			}
		}
		return collideGeom;
	}
	
	public Geom collisionRegPolyVsLineRefineDebug(Geom RegPolygon, int Accuracy){// Slower (more accurate) step by step check for polygon crossing line
		System.out.println("=====collisionRegPolyVsLineRefineDebug IS IN DEBUG MODE!=====");// Debug Only
		System.out.println("With an accuracy of : " + Accuracy);// Debug Only
		
		Geom ReverseRegPolygon= new Geom();// Time iterating polygon
		ReverseRegPolygon.setGeometry(RegPolygon.getGeometry());// Copy current square state
		Geom collideGeom = new Geom();// Point or polygon side that will collide
		collideGeom.setOffset(new Vector2d(-1*this.getGeometry().getLDA(), -1*this.getGeometry().getLDA()));// Sets collision out of the system for error checking
		
		System.out.println("Velocity Magnitude is: " + RegPolygon.getVelocity().magnitude());// Debug 
		
		double linePolyCenterDist;// Distance from polygon center to line, distance to compare collision point to line distance
		double tempMin = Integer.MAX_VALUE;// Needed for minimum distance comparison
		
		ReverseRegPolygon.setOffset(RegPolygon.getOffset().subtract(RegPolygon.getVelocity()));// Reset RegPolygon's position to last position before collision.
		ReverseRegPolygon.setAngle(RegPolygon.getAngle() - RegPolygon.getDeltaAngle());// Reset square's rotation to last position before collision

		ReverseRegPolygon.setVelocity((RegPolygon.getVelocity()).scalarMulti(1.0/Accuracy));// Set RegPolygon's velocity to move at lower speed (more accurate)
		ReverseRegPolygon.setDeltaAngle(RegPolygon.getDeltaAngle()/Accuracy);// Same as above but with angle 
		System.out.println("Reverse RegPolygon Info : " + ReverseRegPolygon);// Debug Only
		System.out.println("RegPolygon Info : " + RegPolygon);// Debug Only
		
		for (int i = 0; i < Accuracy; i++){// find the closest point to the circle by comparing radius to point's distance to circle. this point will be a point on the side or a corner
			ReverseRegPolygon.update();// Increments the square by 1 time step (1/accuracy)*(velocity && angle)
			System.out.println("Start");
			System.out.println("Reverse RegPolygon Info Begin Loop : " + ReverseRegPolygon);// Debug Only
			System.out.println("Loop Number : " + i);
			System.out.println("|||||collsiionRegPolyVsLine IS IN DEBUG MODE!|||||");
			
			linePolyCenterDist = this.minDistPointToLine(ReverseRegPolygon.getOffset());// Distance from RegPolygon center to line
			
			for (int j = 0; j < ReverseRegPolygon.getGeometry().getVertexes().length; j++) {// Iterates through every corner but only saves the closest corner/side
				System.out.println("Min to vertex: " + this.minDistPointToLine(ReverseRegPolygon.getVectorPos(j)));// Debug
				
				if (Math.abs(this.minDistPointToLine(ReverseRegPolygon.getVectorPos(j))) <= tempMin) {// Checks if the corner to line distance is <= to previous data
					if (Math.abs(this.minDistPointToLine(ReverseRegPolygon.getVectorPos(j))) == tempMin){// If the previous distance == current distance save the side
						System.out.println("Full Side of RegPolygon Collision Detected");// Debug
						// Below is the creation of a line segment of the collision line
						collideGeom.getGeometry().setVert(new Vector2d[]{collideGeom.getOffset().subtract(ReverseRegPolygon.getVectorPos(j))});
						collideGeom.getGeometry().setLDA(collideGeom.getGeometry().getVertexes()[0].magnitude());
						System.out.println("Temp min Distance: " + tempMin);//Debug
						System.out.println("Line LDA: " + collideGeom.getGeometry().getLDA());// Debug
						System.out.println("|||||Line direction vertex: " + collideGeom.getGeometry().getVertexes()[0]+ " |||||");// Debug
						
					}
					collideGeom.setOffset(ReverseRegPolygon.getVectorPos(j));// Corner is collision point
					tempMin = Math.abs(this.minDistPointToLine(ReverseRegPolygon.getVectorPos(j)));// Sets this distance as the new collision point
					System.out.println("Temp min Distance: " + tempMin);//Debug
					System.out.println("|||||Collision point position: " + collideGeom.getOffset() + " |||||");//Debug
				}
			if (linePolyCenterDist/this.minDistPointToLine(collideGeom.getOffset()) <= 0)/// Detection when vertex is on the opposite side of the line that the center is on, 
				i = Accuracy;//allows for collision from both sides of line which that means a collision occurred and the FOR loop is killed
				
			System.out.println("Collision Vertex : " + collideGeom);// Debug only
			}
		System.out.println("Final COLLISION Geom: " + collideGeom);
		System.out.println("~~~~~End~~~~~");
		}
		return collideGeom;
	}
	
}