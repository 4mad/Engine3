//David Govorko, 06/28/2017
package starter;

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
 */
//TODO: COLLISION method && should color be a field && should velocity/deltaAngle be here???
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
	
	public double minDistPointToLine(Vector2d point){ //returns the minimum distance from a Geom's center to a line;
		double distance = 0;
		if (this.getGeometry().getVertexes().length == 1){ 
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
		//POINT V = 0 & LDA = 0
		if (Geometry.getVertexes().length == 0 && Geometry.getLDA() == 0) {
			GL11.glPushMatrix();
			
			//Rotation is not needed for a point
			//Drawing the Point at the offset
			GL11.glBegin(GL11.GL_POINTS);
			GL11.glVertex2d(Offset.getX(), Offset.getY());//Offset accounted for here
			//end
			GL11.glEnd();
			GL11.glPopMatrix();
			
		} //LINE SEGMENT V = 1 & LDA = segment length
		else if (Geometry.getVertexes().length == 1 && Geometry.getLDA() > 0){
			GL11.glPushMatrix();
			//GL11.glLineWidth(3);
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
			
		} //CIRCLE V = 0 & LDA = Diameter
		  //TODO: Maybe change the circle's edge resolution?
		else if (Geometry.getVertexes().length == 0 && Geometry.getLDA() > 0){
			GL11.glPushMatrix();
			
			//Circle rotating excluded for now
			//Drawing the Circle
			GL11.glBegin(GL11.GL_LINE_LOOP);
			for(int i = 0; i<250; i++) { //250 = edge resolution
				double angle = (i*2*Math.PI/250);
				GL11.glVertex2d(Offset.getX() + (Math.cos(angle) * Geometry.getLDA()/2), 
						Offset.getY() + (Math.sin(angle) * Geometry.getLDA()/2)); //Offset accounted for here
			}
			GL11.glEnd();
			GL11.glPopMatrix();
			
		} //REG. POLYGON V > 2 & LDA > 0
		else if (Geometry.getVertexes().length > 2 && Geometry.getLDA() > 0){
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
		} //Catch for errors
		  //TODO: Add the other geometry types
		else {
			System.out.println("Geometry properties do not match any renderer yet, "
					+ "here is an attempt to print it out: " + Geometry.toString());
		}
	} // renderGeom()
	
	public Geom blindCollision(Geom B){ //Returns the hypothetical collision point as a NGeom
		//maybe only have it return the vertexes and the offset, and let the other program decide weather or not to create a new Geom or just update one
		Geom collideVert = null; // returned collision geom
		//  Square colliding into Circle
		if ((Geometry.getLDA() > 0 & Geometry.getVertexes().length == 0) & (B.getGeometry().getVertexes().length == 4 )){ 
			//B is the Square
			Vector2d Dir = Offset.subtract(B.getOffset());
			Vector2d CollisionP; // collision point
			double tempX;
			double tempY;
			double baX = Dir.getX()*Math.cos(-B.getAngleRad()) - Dir.getY()*Math.sin(-B.getAngleRad());
			double baY = Dir.getX()*Math.sin(-B.getAngleRad()) + Dir.getY()*Math.cos(-B.getAngleRad());
			
			double halfLength = B.getGeometry().getSideLength()/2;

			if ( baX < - halfLength) tempX = -halfLength;
			else if (baX >  halfLength) tempX = halfLength;
			else tempX = baX;

			if (baY < - halfLength) tempY = - halfLength;
			else if (baY > halfLength) tempY = halfLength;
			else tempY = baY;
			
			Vector2d temp = new Vector2d(tempX, tempY);
			CollisionP = temp.rotate(B.getAngleRad()).add(B.getOffset());
			
			collideVert = new Geom(0, 0, CollisionP, new Vector2d(0,0), new NGeom());
			return collideVert;
		} //RegPolygon Colliding into Line
		else if ((B.getGeometry().getVertexes().length > 2) & (getGeometry().getVertexes().length == 1)) {
			//B is the RegPolygon
			double temp = Integer.MAX_VALUE;
			Geom collideVert2 = new Geom();
			for (int i = 0; i < B.getGeometry().getVertexes().length; i++) {//returns closest vertex to line
				//System.out.println("Min to vertex: " + this.minDistPointToLine(B.getVectorPos(i)));
				if (Math.abs(this.minDistPointToLine(B.getVectorPos(i))) <= temp) {
					if (Math.abs(this.minDistPointToLine(B.getVectorPos(i))) == temp){
						collideVert2.getGeometry().setVert(new Vector2d[]{collideVert2.getOffset().subtract(B.getVectorPos(i))});
						collideVert2.getGeometry().setLDA(collideVert2.getGeometry().getVertexes()[0].magnitude());
					}//if = temp	
					collideVert2.setOffset(B.getVectorPos(i));
					temp = Math.abs(this.minDistPointToLine(B.getVectorPos(i)));
				}//if <= temp
			}//For loop
			return collideVert2;		
		} else { //no collision detection code is made yet for this scenario
			System.out.println("There is no Collision Detection code yet for this scenario");
			return null;
		}
		
	}//Collision
	
}