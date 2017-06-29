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

	public double getAngle() {
		return Angle;
	}

	public void setAngle(double angle) {
		Angle = angle;
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
	
	//Renders the geometry by guessing the geometry type first from values of NGeom's fields.
	//TODO: Optimize order of if statements --> Lazy conditionals
	public void renderGeom(){
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
			GL11.glPopMatrix();	
			
		} //CIRCLE V = 0 & LDA = Diameter
		  //TODO: Maybe change the cricle's edge resolution?
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
	
}
