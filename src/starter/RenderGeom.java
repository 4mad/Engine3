//David Govorko, 3/16/2017
package starter;

import org.lwjgl.opengl.GL11;
//This object just renders geometry no colors, or fancy effects, rectangles/polygons are filled though.
//Currently Circle, line, point, rectangle, and Polygons are supported.
//??Add a rotation in relation to arbitrary point method?
//??To support RegPolygon Generator object

public class RenderGeom {

	double Angle;
	Vector2d Origin;
	
	public RenderGeom(){
		Angle = 0;
		Origin = new Vector2d(0,0);
		GL11.glTranslated(Origin.getX(), Origin.getY(), 0);
		GL11.glRotated(Angle, 0, 0, 1);
		GL11.glTranslated(-Origin.getX(), -Origin.getY(), 0);
	}
	
	public RenderGeom(double angle, Vector2d origin){
		Angle = angle;
		Origin = origin;
		GL11.glTranslated(Origin.getX(), Origin.getY(), 0);
		GL11.glRotated(Angle, 0, 0, 1);
		GL11.glTranslated(-Origin.getX(), -Origin.getY(), 0);
	}
	
	//circle with center points coordinates and radius given as doubles  
	//??make variable "side" resolution
	public void circle(double CenterX, double CenterY, double Radius) {
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for(int i = 0; i<250; i++) {
			double angle = (i*2*Math.PI/250);
			GL11.glVertex2d(CenterX + (Math.cos(angle) * Radius), CenterY + (Math.sin(angle) * Radius)); 
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//circle with center given as Vector and radius as double 	
	//??make variable "side" resolution
	public void circle(Vector2d center, double Radius) {
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for(int i = 0; i<250; i++) {
			double angle = (i*2*Math.PI/250);
			GL11.glVertex2d(center.getX() + (Math.cos(angle) * Radius), center.getY() + (Math.sin(angle) * Radius)); 
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//Rectangle made by vectors, origin is the bottom left point 
	//??make a mixed rectangle, vector2d and doubles are mixed
	//??make a variable corner origin rectangle
	public void rectangleCorner(Vector2d origin, Vector2d size ) {
		GL11.glPushMatrix();

		//drawing the rectangle
		GL11.glBegin(GL11.GL_QUADS);	
		GL11.glVertex2d(origin.getX() , origin.getY());
		GL11.glVertex2d(origin.getX() + size.getX(), origin.getY());
		GL11.glVertex2d(origin.getX() + size.getX(), origin.getY() + size.getY());
		GL11.glVertex2d(origin.getX(), origin.getY() + size.getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//Rectangle made by vectors, origin is the bottom left point rotated around origin
	public void rectangleCorner(Vector2d origin, Vector2d size, double angle ) {
		GL11.glPushMatrix();
		
		GL11.glTranslated(origin.getX(), origin.getY(), 0);
		GL11.glRotated(angle, 0, 0, 1);
		GL11.glTranslated(-origin.getX(), -origin.getY(), 0);
		//drawing the rectangle
		GL11.glBegin(GL11.GL_QUADS);	
		GL11.glVertex2d(origin.getX() , origin.getY());
		GL11.glVertex2d(origin.getX() + size.getX(), origin.getY());
		GL11.glVertex2d(origin.getX() + size.getX(), origin.getY() + size.getY());
		GL11.glVertex2d(origin.getX(), origin.getY() + size.getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//rectangle made by center point and size vectors.
	public void rectangleCenter(Vector2d center, Vector2d size ) {
		GL11.glPushMatrix();

		//drawing the rectangle
		GL11.glBegin(GL11.GL_QUADS);	
		GL11.glVertex2d(center.getX() - size.getX()/2, center.getY() - size.getY()/2);
		GL11.glVertex2d(center.getX() + size.getX()/2, center.getY() - size.getY()/2);
		GL11.glVertex2d(center.getX() + size.getX()/2, center.getY() + size.getY()/2);
		GL11.glVertex2d(center.getX() - size.getX()/2, center.getY() + size.getY()/2);
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//rectangle made by center point and size vectors rotated around center.
	public void rectangleCenter(Vector2d center, Vector2d size, double angle ) {
		GL11.glPushMatrix();
		
		GL11.glTranslated(center.getX(), center.getY(), 0);
		GL11.glRotated(angle, 0, 0, 1);
		GL11.glTranslated(-center.getX(), -center.getY(), 0);
		//drawing the rectangle
		GL11.glBegin(GL11.GL_QUADS);	
		GL11.glVertex2d(center.getX() - size.getX()/2, center.getY() - size.getY()/2);
		GL11.glVertex2d(center.getX() + size.getX()/2, center.getY() - size.getY()/2);
		GL11.glVertex2d(center.getX() + size.getX()/2, center.getY() + size.getY()/2);
		GL11.glVertex2d(center.getX() - size.getX()/2, center.getY() + size.getY()/2);
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//Polygon made by a list of Vertexes
	//??What guarantees points are in order
	public void polygon(Vector2d[] vertexes){
		GL11.glPushMatrix();

		//Drawing the Polygon
		GL11.glBegin(GL11.GL_POLYGON);
		for (int i = 0; i < vertexes.length; i++)
			GL11.glVertex2d(vertexes[i].getX() , vertexes[i].getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//Polygon made by a list of Vertexes and an offset (polygon in relation to point)
	//??What guarantees points are in order
	public void polygon(Vector2d[] vertexes, Vector2d offset){
		GL11.glPushMatrix();
		
		GL11.glTranslated(offset.getX(), offset.getY(), 0);
		//Drawing the Polygon
		GL11.glBegin(GL11.GL_POLYGON);
		for (int i = 0; i < vertexes.length; i++)
			GL11.glVertex2d(vertexes[i].getX(), vertexes[i].getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	//Polygon made by first point of a list of final Vertexes rotated by this angle (degrees)
	//??What guarantees points are in order
	public void polygon(Vector2d[] vertexes, double angle){
		GL11.glPushMatrix();
		
		//openGL commands are stacked that's why last command first
		GL11.glTranslated(vertexes[0].getX(), vertexes[0].getY(), 0);
		//OpenGl applies following rotation to entire matrix of points
		GL11.glRotated(angle, 0, 0, 1);
		GL11.glTranslated(-vertexes[0].getX(), -vertexes[0].getY(), 0);
		//Drawing the Polygon
		GL11.glBegin(GL11.GL_POLYGON);
		for (int i = 0; i < vertexes.length; i++)
			GL11.glVertex2d(vertexes[i].getX(), vertexes[i].getY());
		//end
		GL11.glEnd();
			
		GL11.glPopMatrix();
	}
	
	//Polygon made by first point of a list of relative Vertexes rotated by this angle (degrees) around first point
	//??What guarantees points are in order
	public void polygon(Vector2d[] vertexes, Vector2d offset, double angle){
		GL11.glPushMatrix();
		
		//openGL commands are stacked that's why last command first
		GL11.glTranslated(offset.getX(), offset.getY(), 0);
		//OpenGl applies following rotation to entire matrix of points
		GL11.glRotated(angle, 0, 0, 1);
		//Drawing the Polygon
		GL11.glBegin(GL11.GL_POLYGON);
		for (int i = 0; i < vertexes.length; i++)
			GL11.glVertex2d(vertexes[i].getX(), vertexes[i].getY());
		//end
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}
	
	//Line made by 2 vertexes
	public void line(Vector2d origin, Vector2d end){
		GL11.glPushMatrix();
		
		//Drawing the Line
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(origin.getX() , origin.getY());
		GL11.glVertex2d(end.getX() , end.getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();		
	}
	
	//Line made by 2 sets of doubles
	//??make a double vector line mix
	public void line(double originX, double originY, double endX, double endY){
		GL11.glPushMatrix();

		//Drawing the Line
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(originX, originY);
		GL11.glVertex2d(endX, endY);
		//end
		GL11.glEnd();
		GL11.glPopMatrix();		
	}
	
	//Points made by list of Vertexes
	//??does it matter in what order the points are rendered
	public void points(Vector2d[] vertexes){
		GL11.glPushMatrix();

		//Drawing the line
		GL11.glBegin(GL11.GL_POINTS);
		for (int i = 0; i < vertexes.length; i++)
			GL11.glVertex2d(vertexes[i].getX(), vertexes[i].getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//Points made by list of Vertexes and offset by an offset Vector
	//??does it matter in what order the points are rendered?
	public void points(Vector2d[] vertexes, Vector2d offset, int max){
		GL11.glPushMatrix();

		GL11.glTranslated(offset.getX(), offset.getY(), 0);
		//Drawing the Points with an offset
		GL11.glBegin(GL11.GL_POINTS);
		for (int i = 0; i < max; i++)
			GL11.glVertex2d(vertexes[i].getX(), vertexes[i].getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//Point made by a Vector
	public void point(Vector2d vertex){
		GL11.glPushMatrix();

		//Drawing the Point
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex2d(vertex.getX(), vertex.getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	//Point made by two doubles
	public void point(double x, double y){
		GL11.glPushMatrix();
		
		//Drawing the Point
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex2d(x, y);
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	//!!Rotates whole screen at the moment
	public void rotate( Vector2d origin, double angle){
		GL11.glTranslated(origin.getX(), origin.getY(), 0);
		GL11.glRotated(angle, 0, 0, 1);
		GL11.glTranslated(-origin.getX(), -origin.getY(), 0);
		
	}
	
	/* To include RegPolygonGenerator objects later
	//Polygon made by a list of Vertexes and a Center then rotated
	public void polygon(RegPolyGen r){
		GL11.glPushMatrix();
		GL11.glTranslated(r.getCenter().getX(),r.getCenter().getY(),0);
		GL11.glRotated(Math.toDegrees(r.getAngleRads()), 0, 0, 1); 
		//Drawing the Polygon
		GL11.glBegin(GL11.GL_LINE_LOOP); //LINE_LOOP does outline, POLYGON does fill
		for (int i = 0; i < r.getVertexes().length; i++)
			GL11.glVertex2d(r.getVertexes()[i].getX(), r.getVertexes()[i].getY());
		//end
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	*/
}
