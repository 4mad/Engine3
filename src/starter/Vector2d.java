// David Govorko, 03/16/2017
package starter;
// A 2-d Vector object and a number of mathematical operations.
import java.math.RoundingMode;
import java.text.*;

/*Vector2d contains the following:
 * FIELDS:
 *  DOUBLE x: X coordinate
 *  DOUBLE y: Y coordinate
 * 
 * CONTRUCTORs:
 *  Vector2d(): Default 0,0
 *  Vector2d(double x, double y): User fills all fields
 *  
 * Methods:
 *  String toString(): To String
 *  Double dist(Vector2d a): Distance calculation
 *  Double dot(Vector2d a): Dot product
 *  Vector2d add(Vector2d a): Vector addition
 *  Vector2d subtract(Vector2d a): Vector subtraction
 *  Double magnitude(): Magnitude of vector
 *  Double cross(Vector2d a): Magnitude of vector multiplication
 *  Double angleX(): Angle (radians) between Vector and positive X axis
 *  Double angleOtherX(): Other Angle (radians) between Vector and negative X axis
 *  Double angleY(): Angle (radians) between Vector and positive Y axis
 *  Double angleOtherY(): Other Angle (radians) between Vector and negative Y axis
 *  Vector2d unitize(): Turn Vector into a Unit vector
 *  Vector2d scalarMulti(double a): Scalar multiplication of Vector
 *  Double angleBetween(Vector2d a): Angle (radians) between two vectors
 *  Vector2d tangent(): Create tangent vector object
 *  Vector2d deepCopy(): Deep Copy = Make new vector object
 *  Vector2d integizerFloor(): Keep as doubles but remove decimals by flooring
 *  Vector2d integizerCeil(): Keep as doubles but remove decimals by ceiling
 *  Vector2d rotate(double angle): Returns new rotated (radians) vector object
 *  Void trim(int i): Cuts down the vector to inputed decimal point up to 4 decimal points
 *  Boolean similar(Vector2d a, double accuracy): Checks magnitude of vector difference to given accuracy.
 */
public class Vector2d {

	private double X,Y;
	
	public Vector2d(){
	  X = 0;
	  Y = 0;		
	}
	
	public Vector2d(double x, double y){
		X = x;
		Y = y;		
	}
	
	public double getX(){
		return X;
	}

	public void setX(double x){
		this.X = x;
	}

	public double getY(){
		return Y;
	}

	public void setY(double y){
		this.Y = y;
	}
	
	public String toString(){
		return "<" + getX() + " , " + getY() + ">";
	}
	
	public double dist(Vector2d a){// Distance
		return Math.sqrt(Math.pow(X-a.X,2)+Math.pow(Y-a.Y,2));	
	}	
	
	public double dot(Vector2d a){// Dot product
		return X*a.getX() + Y*a.getY();
	}
	
	public Vector2d add(Vector2d a){// Vector addition
		double i = X + a.getX();
		double j = Y + a.getY();
		return new Vector2d(i, j); 
	}
	
	public Vector2d subtract(Vector2d a){// Vector subtraction
		double i = X - a.getX();
		double j = Y - a.getY();
		return new Vector2d(i, j);	 
	}
	
	public double magnitude(){// Vector magnitude
		return Math.sqrt(Math.pow(X,2) + Math.pow(Y,2));
	}
	
	public double cross(Vector2d a){// Vector cross product	
			return X*a.getY() - a.getX()*X;			
	}
	
	public double angleX(){// Angle (radians) between Vector and positive X axis
		return  Math.acos(X/this.magnitude());
	}
	
	public double angleOtherX(){// Other Angle (radians) between Vector and negative X axis
		return Math.PI - this.angleX(); 
	}
	
	public double angleY(){// Angle (radians) between Vector and positive Y axis
		return  Math.acos(Y/this.magnitude());
	}
	
	public double angleOtherY(){// Other Angle (radians) between Vector and negative Y axis
		return Math.PI - angleY(); 
	}
	
	public Vector2d unitize(){// Turn Vector into a Unit vector
		if (X == 0 & Y == 0) return this;
		return new Vector2d(X/this.magnitude(), Y/this.magnitude());			
	}
	
	public Vector2d scalarMulti(double a){// Scalar multiplication of Vector
		return new Vector2d(a*X, a*Y);
	}
	
	public double angleBetween(Vector2d a){// Angle (radians) between two vectors
		return Math.acos(this.dot(a)/(this.magnitude()*a.magnitude()));
	}
	
	public Vector2d tangent(){// Create tangent vector object
		return new Vector2d(-Y, X);
	}
	
	public Vector2d deepCopy(){// Deep Copy = Make new vector object
		return new Vector2d(X, Y);
	}
	
	public Vector2d integizerFloor(){// Keep as doubles but remove decimals by flooring
		return new Vector2d(Math.floor(X), Math.floor(Y));
	}
	
	public Vector2d integizerCeil(){// Keep as doubles but remove decimals by ceiling
		return new Vector2d(Math.ceil(X), Math.ceil(Y));
	}
	
	public Vector2d rotate(double angle){// Returns new rotated (radians) vector object
		double xPrime = X*Math.cos(angle) - Y*Math.sin(angle);
		double yPrime = X*Math.sin(angle) + Y*Math.cos(angle);
		return new Vector2d(xPrime, yPrime);
	}
	
	public void trim(int i){// Cuts down the vector to inputed decimal point up to 4 decimal points
		
		if (i < 1){
			DecimalFormat df = new DecimalFormat("#.");
			df.setRoundingMode(RoundingMode.DOWN);
			X = Double.valueOf(df.format(X));
			Y = Double.valueOf(df.format(Y));
		} else if (i == 1){
			DecimalFormat df = new DecimalFormat("#.#");
			df.setRoundingMode(RoundingMode.DOWN);
			X = Double.valueOf(df.format(X));
			Y = Double.valueOf(df.format(Y));
		} else if (i == 2){
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.DOWN);
			X = Double.valueOf(df.format(X));
			Y = Double.valueOf(df.format(Y));
		} else if (i == 3){
			DecimalFormat df = new DecimalFormat("#.###");
			df.setRoundingMode(RoundingMode.DOWN);
			X = Double.valueOf(df.format(X));
			Y = Double.valueOf(df.format(Y));
		} else {
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.DOWN);
			X = Double.valueOf(df.format(X));
			Y = Double.valueOf(df.format(Y));
		}
	}
	
	public boolean similar(Vector2d a, double accuracy){// Checks magnitude of vector difference to given accuracy.
		return this.subtract(a).magnitude() <= accuracy;
	}
}