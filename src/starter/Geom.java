//David Govorko, 06/28/2017
package starter;

import java.util.HashSet;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

/*Geom contains the following fields and methods:
 * FIELDS:
 * 	ANGLE: Angle of rotation in degrees
 * 	DELTA ANGLE: Change in the angle of rotation
 * 	OFFSET: Offset of the origin of the NGeom
 * 	VELOCITY: Direction vector
 * 	NGEOM: The base geometric unit 
 * METHODS:
 * 	RENDER: Renders the geometry
 *  COLLISION: Collision between the geometry and another
 *  GEOMGUESSER: Guesses the full geometry based on limited collision data.
 */
//TODO: should color be a field && should velocity/deltaAngle be here???
public class Geom {
	private double Angle;
	private double DeltaAngle;
	private Vector2d Offset;
	private Vector2d Velocity;
	private NGeom Geometry;
	
	public Geom(){//Default is a stationary point at 0
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

	public double getAngle() { //Angle in degrees
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
	
	public void update(){ //Just adds velocity and DeltaAngle to original angle and offset
		Offset = Offset.add(Velocity);
		Angle += DeltaAngle;
		if (Math.abs(Angle) == 360){ Angle = 0;} //to avoid int overflow
	}
	
	public double minDistPointToLine(Vector2d point){ //returns the minimum distance from a line to a point;
		double distance = 0;
		if (this.getGeometry().getVertexes().length == 1){ // Checks to see if the geom is a line.
			Vector2d pointAtOrigin  = point.subtract(this.getOffset());			
			Vector2d lineOrigin = this.getGeometry().getVertexes()[0];
			
			return pointAtOrigin.subtract(lineOrigin).dot(lineOrigin.tangent())/lineOrigin.magnitude();
		} else {
			System.out.println("Sorry but this is not a line, this has " +  this.getGeometry().getVertexes().length + " many sides.");
			return distance;
		}
		
	}
	
	public Vector2d getVectorPos(int i){ //Gets specific vector's position.
		return this.getGeometry().getVertexes()[i].rotate(this.getAngleRad()).add(this.getOffset());
	}
	
	//Renders the geometry by guessing the geometry type first from values of NGeom's fields.
	//TODO: Optimize order of if statements --> Lazy conditionals
	public void blindRenderGeom(){
		
		if (Geometry.getVertexes().length == 0 && Geometry.getLDA() == 0) {//POINT V = 0 & LDA = 0
			this.renderPoint();
			
		}//LINE SEGMENT V = 1 & LDA = segment length
		else if (Geometry.getVertexes().length == 1 && Geometry.getLDA() > 0){
			this.renderLineSegment();
				
		}//LINE V = 1 & LDA = -1*(segment length)
		else if (Geometry.getVertexes().length == 1 && Geometry.getLDA() < 0){
			this.renderLine();
			
		} //CIRCLE V = 0 & LDA = Diameter
		else if (Geometry.getVertexes().length == 0 && Geometry.getLDA() > 0){
			this.renderCircle();
			
		} //REG. POLYGON V > 2 & LDA > 0
		else if (Geometry.getVertexes().length > 2 && Geometry.getLDA() > 0){
			this.renderPolygon();
			
		} else { //Catch for errors
			System.out.println("Geometry properties do not match any renderer yet, "
					+ "here is an attempt to print it out: " + Geometry.toString());
		}
		
	} // renderGeom()
	
	public void renderPoint(){ //Renders a point
		GL11.glPushMatrix();
		
		//Rotation is not needed for a point
		//Drawing the Point at the offset
		GL11.glBegin(GL11.GL_POINTS);
		//Offset
		GL11.glVertex2d(Offset.getX(), Offset.getY());
		//end
		GL11.glEnd(); 
		GL11.glPopMatrix();
	}
	
	public void renderLineSegment(){ //Renders a Line Segment
		GL11.glPushMatrix();
		//GL11.glLineWidth(3); //Really only used for debugging.
		//Offset
		GL11.glTranslated(Offset.getX(), Offset.getY(), 0);
		//Rotate
		GL11.glRotated(Angle, 0, 0, 1);
		//Drawing the Line Segment
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(0 , 0);
		GL11.glVertex2d(Geometry.getVertexes()[0].getX() , Geometry.getVertexes()[0].getY());
		//end
		GL11.glEnd();
		//GL11.glLineWidth(1);
		GL11.glPopMatrix();	
	}
	
	public void renderLine(){ //Renders a Line
		GL11.glPushMatrix();
		//GL11.glLineWidth(3); //Really only used for debugging.
		//Offset
		GL11.glTranslated(Offset.getX(), Offset.getY(), 0);
		//Rotate
		GL11.glRotated(Angle, 0, 0, 1);
		//Drawing the Line
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(Geometry.getVertexes()[0].getX()*Geometry.getLDA() , Geometry.getVertexes()[0].getY()*Geometry.getLDA()); //Starts at negative value
		GL11.glVertex2d(-1*Geometry.getVertexes()[0].getX()*Geometry.getLDA() , -1*Geometry.getVertexes()[0].getY()*Geometry.getLDA());
		//end
		GL11.glEnd();
		//GL11.glLineWidth(1);
		GL11.glPopMatrix();	
	}
	
	 //TODO: Maybe change the circle's edge resolution?
	public void renderCircle(){ //Renders a Circle
		GL11.glPushMatrix();
		
		//Circle rotating excluded for now
		//Drawing the Circle
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for(int i = 0; i<250; i++) { //250 = edge resolution
			double angle = (i*2*Math.PI/250);
			GL11.glVertex2d(Offset.getX() + (Math.cos(angle) * Geometry.getLDA()/2), 
					Offset.getY() + (Math.sin(angle) * Geometry.getLDA()/2)); //Offset accounted for here
		}
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void renderPolygon(){ //Renders a polygon assuming vertexes are in order
		GL11.glPushMatrix();
		
		//Offset
		GL11.glTranslated(Offset.getX(), Offset.getY(), 0);
		//Rotate
		GL11.glRotated(Angle, 0, 0, 1);
		//Drawing the Regular Polygon
		GL11.glBegin(GL11.GL_POLYGON);
		for (int i = 0; i < Geometry.getVertexes().length; i++)
			GL11.glVertex2d(Geometry.getVertexes()[i].getX() , Geometry.getVertexes()[i].getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public Geom blindCollision(Geom B){ //Returns the hypothetical collision point as a NGeom
		//maybe only have it return the vertexes and the offset, and let the other program decide weather or not to create a new Geom or just update one
		//  Square colliding into Circle
		if ((Geometry.getLDA() > 0 & Geometry.getVertexes().length == 0) & (B.getGeometry().getVertexes().length == 4 )){ 
			return collisionSquareVsCircle(B);
		} //RegPolygon Colliding into Line
		else if ((B.getGeometry().getVertexes().length > 2) & (getGeometry().getVertexes().length == 1) & (getGeometry().getLDA() < 0)) {
			return collisionRegPolyVsLine(B);	
		} else { //no collision detection code is made yet for this scenario
			System.out.println("There is no Collision Detection code yet for this scenario");
			return null;
		}
		
	}
	
	public Geom blindCollisionDebug(Geom B){ //Debug Version
		System.out.println("blindCollision IS IN DEBUG MODE!");
		System.out.print("This is the Geom in Question: " + B);
		
		//  Square colliding into Circle
		if ((Geometry.getLDA() > 0 & Geometry.getVertexes().length == 0) & (B.getGeometry().getVertexes().length == 4 )){ 
			return collisionSquareVsCircleDebug(B);
		} //RegPolygon Colliding into Line
		else if ((B.getGeometry().getVertexes().length > 2) & (getGeometry().getVertexes().length == 1) & (getGeometry().getLDA() < 0)) {
			return collisionRegPolyVsLineDebug(B);	
		} else { //no collision detection code is made yet for this scenario
			System.out.println("There is no Collision Detection code yet for this scenario");
			return null;
		}
		
	}
	
	public Geom collisionRegPolyVsLine(Geom RegPolygon){ //Performs a RegPolygon Versus Line collision detection that spits out colliding line/point.
		double temp = Integer.MAX_VALUE;
		Geom collideVert = new Geom();
		
		for (int i = 0; i < RegPolygon.getGeometry().getVertexes().length; i++) {//returns closest vertex to line
			
			if (Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i))) <= temp) {
				
				if (Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i))) == temp){
					collideVert.getGeometry().setVert(new Vector2d[]{collideVert.getOffset().subtract(RegPolygon.getVectorPos(i))});
					collideVert.getGeometry().setLDA(collideVert.getGeometry().getVertexes()[0].magnitude());
					
				}//if current == temp	
				collideVert.setOffset(RegPolygon.getVectorPos(i));
				temp = Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i)));
				
			}//if <= temp
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
	
	public Geom collisionSquareVsCircleRefine(Geom Square, Vector2d OverEstimatedPoint, int Accuracy){
		Geom ReverseSquare = new Geom();
		ReverseSquare.setGeometry(Square.getGeometry());
		Geom collideVert = new Geom();
		collideVert.setOffset(new Vector2d(-1*this.getGeometry().getLDA(), -1*this.getGeometry().getLDA()));
		double radiusComp = 0;
		
		ReverseSquare.setOffset(Square.getOffset().subtract(Square.getVelocity()));//Reset position to before collision.
		ReverseSquare.setAngle(Square.getAngle() - Square.getDeltaAngle());
		
		ReverseSquare.setVelocity((Square.getVelocity()).scalarMulti(1.0/Accuracy));//Increase step by step accuracy
		ReverseSquare.setDeltaAngle(Square.getDeltaAngle()/Accuracy);
		
		for (int i = 0; i < Accuracy; i++){//Find the closest point to the circle by comparing radius to point's distance to circle.
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
	
	public Geom collisionRegPolyVsLineDebug(Geom RegPolygon){ //Debug version
		System.out.println("collsiionRegPolyVsLine IS IN DEBUG MODE!");
		
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
					
				}//if current == temp	
				collideVert.setOffset(RegPolygon.getVectorPos(i));
				temp = Math.abs(this.minDistPointToLine(RegPolygon.getVectorPos(i)));
				System.out.println("Temp min Distance: " + temp);//Debug
				System.out.println("Collision point position: " + collideVert.getOffset());//Debug
				
			}//if <= temp
		}//For loop
		return collideVert;
	}
	
	public Geom collisionSquareVsCircleDebug(Geom Square){//Debug Version
		System.out.println("collisionSquareVsCircle IS IN DEBUG MODE!");//Debug only
		
		Vector2d Dir = Offset.subtract(Square.getOffset());
		System.out.println("Direction vector from square to circle center: " + Dir);//Debug only
		
		Geom collideVert = new Geom();
		double tempX;
		double tempY;
		
		double baX = Dir.getX()*Math.cos(-Square.getAngleRad()) - Dir.getY()*Math.sin(-Square.getAngleRad());
		double baY = Dir.getX()*Math.sin(-Square.getAngleRad()) + Dir.getY()*Math.cos(-Square.getAngleRad());
		System.out.println("baX : " + baX);//Debug only
		System.out.println("baY : " + baY);//Debug only
		
		double halfLength = Square.getGeometry().getSideLength()/2;		
		System.out.println("Half of the side's length : " + halfLength);//Debug only

		if ( baX < - halfLength) tempX = -halfLength;
		else if (baX >  halfLength) tempX = halfLength;
		else tempX = baX;

		if (baY < - halfLength) tempY = - halfLength;
		else if (baY > halfLength) tempY = halfLength;
		else tempY = baY;
		
		Vector2d temp = new Vector2d(tempX, tempY);
		System.out.println("temp vector: " + temp);//Debug only
		
		collideVert = new Geom(0, 0, temp.rotate(Square.getAngleRad()).add(Square.getOffset()), new Vector2d(0,0), new NGeom());
		System.out.println("Collision Vertex : " + collideVert);
		
		return collideVert;
	}
	
	public Geom collisionSquareVsCircleRefineDebug(Geom Square, Vector2d OverEstimatedPoint, int Accuracy){
		System.out.println("collisionSquareVsCircleRefineDebug IS IN DEBUG MODE!");//Debug Only
		System.out.println("With an accuracy of : " + Accuracy);//Debug Only
		
		Geom ReverseSquare = new Geom();
		ReverseSquare.setGeometry(Square.getGeometry());
		Geom collideVert = new Geom();
		collideVert.setOffset(new Vector2d(-1*this.getGeometry().getLDA(), -1*this.getGeometry().getLDA()));
		double radiusComp = 0;
		
		System.out.println("Velocity Magnitude is: " + Square.getVelocity().magnitude());//Debug Only
		
		ReverseSquare.setOffset(Square.getOffset().subtract(Square.getVelocity()));//Reset position to before collision.
		ReverseSquare.setAngle(Square.getAngle() - Square.getDeltaAngle());
		
		ReverseSquare.setVelocity((Square.getVelocity()).scalarMulti(1.0/Accuracy));//Increase step by step accuracy
		ReverseSquare.setDeltaAngle(Square.getDeltaAngle()/Accuracy);
		System.out.println("Reverse Square Info : " + ReverseSquare);//Debug Only
		System.out.println("Square Info : " + Square);//Debug Only
		
		for (int i = 0; i < Accuracy; i++){//Find the closest point to the circle by comparing radius to point's distance to circle.
			ReverseSquare.update();
			System.out.println("Start");
			System.out.println("Reverse Square Info Begin Loop : " + ReverseSquare);//Debug Only
			System.out.println("Loop Number : " + i);
			
			Vector2d Dir = this.getOffset().subtract(ReverseSquare.getOffset());
			System.out.println("Direction vector from Reverse square to circle center: " + Dir);//Debug only
			
			double tempX;
			double tempY; 
			
			double baX = Dir.getX()*Math.cos(-ReverseSquare.getAngleRad()) - Dir.getY()*Math.sin(-ReverseSquare.getAngleRad());
			double baY = Dir.getX()*Math.sin(-ReverseSquare.getAngleRad()) + Dir.getY()*Math.cos(-ReverseSquare.getAngleRad());
			System.out.println("baX : " + baX);//Debug only
			System.out.println("baY : " + baY);//Debug only
			
			double halfLength = ReverseSquare.getGeometry().getSideLength()/2;
			System.out.println("Half of the side's length : " + halfLength);//Debug only
			
			if ( baX < - halfLength) tempX = -halfLength;
			else if (baX >  halfLength) tempX = halfLength;
			else tempX = baX;

			if (baY < - halfLength) tempY = - halfLength;
			else if (baY > halfLength) tempY = halfLength;
			else tempY = baY;
			
			Vector2d temp = new Vector2d(tempX, tempY);
			System.out.println("temp vector: " + temp);//Debug only
			
			radiusComp = (temp.rotate(ReverseSquare.getAngleRad()).add(ReverseSquare.getOffset())).dist(this.getOffset());
			System.out.println("radius Comparison : " + radiusComp);//Debug only
			
			collideVert = new Geom(0, 0, temp.rotate(ReverseSquare.getAngleRad()).add(ReverseSquare.getOffset()), new Vector2d(0,0), new NGeom());
			
			if (radiusComp <= this.getGeometry().getLDA()/2)
				i = Accuracy;
				
			System.out.println("Collision Vertex : " + collideVert);//Debug only
		}
		System.out.println("Final COLLISION POINT: " + collideVert);
		System.out.println("End");
		
		return collideVert;
	}
	
	/*
	public Geom blindGeomGuess(HashSet<Geom> collisionData){ //Returns the guessed geometry by analazying what geometry the collision points look most like
		
	}
	*/
	
	public Geom kasaCircleGuess(HashSet<Geom> circleCollisionData) {//Guesses a circle's center and radius based on collision data
		double aM,bM,rK = 0,A,B,C,D,E;//http://people.cas.uab.edu/~mosya/cl/CircleFitByKasa.cpp
		double sumX2 = 0,sumX = 0, sumY2 = 0,sumY = 0,sumXY = 0,sumXY2 = 0,sumX3 = 0,sumX2Y = 0,sumY3 = 0;
		int iterate = circleCollisionData.size();
		Geom kasaCircle = new Geom();
		Vector2d temp; //Iterating vector
		
		Iterator<Geom> iterator0 = circleCollisionData.iterator();
		while (iterator0.hasNext()) {
			temp = iterator0.next().getOffset();
			
			sumX2 +=  Math.pow(temp.getX(),2);
			sumX += temp.getX();
			sumY2 +=  Math.pow(temp.getY(),2);
			sumY += temp.getY();
			sumXY += temp.getX()*temp.getY();
			sumXY2 += temp.getX()* Math.pow(temp.getY(),2);
			sumX3 +=  Math.pow(temp.getX(),3);
			sumX2Y += temp.getY()* Math.pow(temp.getX(),2);
			sumY3 +=  Math.pow(temp.getY(),3);	
		}

		A = iterate*sumX2-sumX*sumX;
		B = iterate*sumXY-sumX*sumY;
		C = iterate*sumY2-sumY*sumY;
		D =  0.5*(iterate*sumXY2-sumX*sumY2+iterate*sumX3-sumX*sumX2);
		E =  0.5*(iterate*sumX2Y-sumX2*sumY+iterate*sumY3-sumY*sumY2);
		
		aM = (D*C-B*E)/(A*C-B*B);
		bM = (A*E-B*D)/(A*C-B*B);

		Iterator<Geom> iterator1 = circleCollisionData.iterator();
		while (iterator1.hasNext()) {
			temp = iterator1.next().getOffset();
			rK += (Math.pow(temp.getX() - aM, 2) + Math.pow(temp.getY() - bM, 2))/iterate;
		}
		rK = Math.sqrt(rK);

		kasaCircle.getGeometry().setLDA(2*rK);//Circle Lda = diameter so 2 * radius
		kasaCircle.setOffset(new Vector2d(aM,bM));

		// input  apache math library for covariance and see if it is faster.
		return kasaCircle;
	}
	
	public Geom kasaCircleGuessDebug(HashSet<Geom> circleCollisionData, Geom Circle) {//Debug Version
		System.out.println("kasaCircleGuess IS IN DEGUB MODE!");//Debug only
		
		double aM,bM,rK = 0,A,B,C,D,E;//http://people.cas.uab.edu/~mosya/cl/CircleFitByKasa.cpp
		double sumX2 = 0,sumX = 0, sumY2 = 0,sumY = 0,sumXY = 0,sumXY2 = 0,sumX3 = 0,sumX2Y = 0,sumY3 = 0;
		int iterate = circleCollisionData.size();
		System.out.println("Number of Collision Points: " + iterate);//Debug only
		
		Geom kasaCircle = new Geom();
		Vector2d temp; //Iterating vector
		
		Iterator<Geom> iterator0 = circleCollisionData.iterator();
		while (iterator0.hasNext()) {
			temp = iterator0.next().getOffset();
			System.out.println("Iterating vector: " + temp);//Debug only
			
			sumX2 +=  Math.pow(temp.getX(),2);
			sumX += temp.getX();
			sumY2 +=  Math.pow(temp.getY(),2);
			sumY += temp.getY();
			sumXY += temp.getX()*temp.getY();
			sumXY2 += temp.getX()* Math.pow(temp.getY(),2);
			sumX3 +=  Math.pow(temp.getX(),3);
			sumX2Y += temp.getY()* Math.pow(temp.getX(),2);
			sumY3 +=  Math.pow(temp.getY(),3);	
		}
		
		System.out.println("sumX      : " + sumX);//Debug only
		System.out.println("sumX2     : " + sumX2);//Debug only
		System.out.println("sumX3     : " + sumX3);//Debug only
		System.out.println("sumY      : " + sumY);//Debug only
		System.out.println("sumY2     : " + sumY2);//Debug only
		System.out.println("sumY3     : " + sumY3);//Debug only
		System.out.println("sumXY     : " + sumXY);//Debug only
		System.out.println("sumX2Y    : " + sumX2Y);//Debug only
		System.out.println("sumXY2    : " + sumXY2);//Debug only

		A = iterate*sumX2-sumX*sumX;
		B = iterate*sumXY-sumX*sumY;
		C = iterate*sumY2-sumY*sumY;
		D =  0.5*(iterate*sumXY2-sumX*sumY2+iterate*sumX3-sumX*sumX2);
		E =  0.5*(iterate*sumX2Y-sumX2*sumY+iterate*sumY3-sumY*sumY2);
		
		System.out.println("A  iterate*sumX2-sumX*sumX : " + A);//Debug only
		System.out.println("B  iterate*sumXY-sumX*sumY  : " + B);//Debug only
		System.out.println("C  iterate*sumY2-sumY*sumY  : " + C);//Debug only
		System.out.println("D  0.5*(iterate*sumXY2-sumX*sumY2+iterate*sumX3-sumX*sumX2)  : " + D);//Debug only
		System.out.println("E  0.5*(iterate*sumX2Y-sumX2*sumY+iterate*sumY3-sumY*sumY2)  : " + E);//Debug only

		aM = (D*C-B*E)/(A*C-B*B);
		bM = (A*E-B*D)/(A*C-B*B);

		Iterator<Geom> iterator1 = circleCollisionData.iterator();
		while (iterator1.hasNext()) {
			temp = iterator1.next().getOffset();
			rK += (Math.pow(temp.getX() - aM, 2) + Math.pow(temp.getY() - bM, 2))/iterate;
		}
		rK = Math.sqrt(rK);
		System.out.println(2*rK + "   should be  " + Circle.getGeometry().getLDA());//Debug only
		System.out.println(aM + "   should be  " + Circle.getOffset().getX());//Debug only
		System.out.println(bM + "   should be  " + Circle.getOffset().getY());//Debug only

		kasaCircle.getGeometry().setLDA(2*rK);//Circle Lda = diameter so 2 * radius
		kasaCircle.setOffset(new Vector2d(aM,bM));

		// input  apache math library for covariance and see if it is faster.
		return kasaCircle;
	}
	
	public Geom leastSquareLineGuess(HashSet<Geom> lineCollisionData){//Guesses a line based on collision data provided
		double mX = 0,mY = 0,sumX2 = 0,sumXY = 0;//hotmath.com/hotmath_help/topics/line-of-best-fit.html
		Geom lsLine = new Geom();
		Vector2d temp; //Iterating vector
		
		Iterator<Geom> iterator0 = lineCollisionData.iterator();
		while (iterator0.hasNext()) {
			temp = iterator0.next().getOffset();
			mX += temp.getX();
			mY += temp.getY();
		}

		mX = mX/lineCollisionData.size();//Averages of x and y
		mY = mY/lineCollisionData.size();
					
		Iterator<Geom> iterator1 = lineCollisionData.iterator();
		while (iterator1.hasNext()) {
			lsLine.setOffset(iterator1.next().getOffset());
			sumX2 +=  Math.pow(lsLine.getOffset().getX() - mX, 2);
			sumXY += (lsLine.getOffset().getX() - mX)*(lsLine.getOffset().getY() - mY);
		}
		
		temp = new Vector2d(sumX2, sumXY);
		
		lsLine.setGeometry(new NGeom(-100*temp.magnitude(), new Vector2d[] {temp}));
		
		if (sumX2 == 0)//This in case the actual line is vertical so directonially x = 0 but y = almost infinity
			lsLine.setGeometry(new NGeom(-123456780, new Vector2d[] {new Vector2d(0.0,1.0)}));
				
		return lsLine;
	}
	
	public Geom leastSquareLineGuessDebug(HashSet<Geom> lineCollisionData, Geom Line){//Debug version
		System.out.println("leastSquareLineGuess IS IN DEBUG MODE!");//Debug only

		double mX = 0,mY = 0,sumX2 = 0,sumXY = 0;//hotmath.com/hotmath_help/topics/line-of-best-fit.html
		Geom lsLine = new Geom();
		Vector2d temp; //Iterating vector
		
		
		Iterator<Geom> iterator0 = lineCollisionData.iterator();
		System.out.println("Number of Collision Points: " + lineCollisionData.size());//Debug only
		
		while (iterator0.hasNext()) {
			temp = iterator0.next().getOffset();
			System.out.println("Iterating vector: " + temp);//Debug only
			
			mX += temp.getX();
			mY += temp.getY();
		}

		mX = mX/lineCollisionData.size();//Averages of x and y
		mY = mY/lineCollisionData.size();
					
		System.out.println("mX      : " + mX);//Debug only
		System.out.println("mY      : " + mY);//Debug only
		
		Iterator<Geom> iterator1 = lineCollisionData.iterator();
		while (iterator1.hasNext()) {
			lsLine.setOffset(iterator1.next().getOffset());
			sumX2 +=  Math.pow(lsLine.getOffset().getX() - mX, 2);
			sumXY += (lsLine.getOffset().getX() - mX)*(lsLine.getOffset().getY() - mY);
		}
		
		System.out.println("sumX2      : " + sumX2);//Debug only
		System.out.println("sumXY      : " + sumXY);//Debug only
		
		temp = new Vector2d(sumX2, sumXY);
		
		lsLine.setGeometry(new NGeom(-100*temp.magnitude(), new Vector2d[] {temp}));
		
		if (sumX2 == 0)//This in case the actual line is vertical so directonially x = 0 but y = almost infinity
			lsLine.setGeometry(new NGeom(-123456780, new Vector2d[] {new Vector2d(0.0,1.0)}));
		
		System.out.println(lsLine.getGeometry().getLDA() + "   should be  " + Line.getGeometry().getLDA());//Debug only
		System.out.println(lsLine.getOffset() + "   should be  " + Line.getOffset());//Debug only
		System.out.println(lsLine.getGeometry().getVertexes()[0] + "    should be  " + Line.getGeometry().getVertexes()[0]);
		
		return lsLine;
	}
	
}