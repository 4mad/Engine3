//David Govorko, 03/16/2017
package starter;
//A 2-d Vector object and a number of mathematical operations.

public class Vector2d {

	double x,y;
	
	public Vector2d() {
	  x=0;
	  y=0;		
	}
	
	public Vector2d(double X, double Y) {
		x = X;
		y = Y;		
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public String toString() {
		return "Vector2d: " + " <" + getX() + "," + getY() + ">";
	}
	//Distance
	public double dist(Vector2d a) {
		return Math.sqrt(Math.pow(this.x-a.x,2)+Math.pow(this.y-a.y,2));	
	}	
	//Dot product
	public double dot(Vector2d a)	{
		return this.x*a.getX() + this.y*a.getY();
	}
	//Vector Addition
	public Vector2d add(Vector2d a)	{
		double i = this.x + a.getX();
		double j = this.y + a.getY();
		return new Vector2d(i, j); 
	}
	//Vector subtraction
	public Vector2d subtract(Vector2d a) {
		double i = this.x - a.getX();
		double j = this.y - a.getY();
		return new Vector2d(i, j);	 
	}
	//Vector magnitude
	public double magnitude() {
		return Math.sqrt(Math.pow(this.x,2) + Math.pow(this.y,2));
	}
	//Vector cross product	
	public double cross(Vector2d a) {
			return this.x*a.getY() - a.getX()*this.y;			
	}
	//Angle between Vector and positive X axis
	public double angleX() {
		return  Math.acos(this.x/this.magnitude());
	}
	//Other Angle between Vector and negative X axis
	public double angleOtherX() {
		return Math.PI - this.angleX(); 
	}
	//Angle between Vector and positive Y axis
	public double angleY() {
		return  Math.acos(this.y/this.magnitude());
	}
	//Other Angle between Vector and negative Y axis
	public double angleOtherY() {
		return Math.PI - this.angleY(); 
	}
	//Turn Vector into a Unit vector
	public Vector2d unitize()	{
		if (this.x == 0 & this.y == 0) return this;
		return new Vector2d(this.x/this.magnitude(), this.y/this.magnitude());			
	}
	//Scalar multiplication of Vector
	public Vector2d scalarMulti(double a) {
		return new Vector2d(a*this.x, a*this.y);
	}
	//Angle between two vectors
	public double angleBetween(Vector2d a){
		return Math.acos(this.dot(a)/(this.magnitude()*a.magnitude()));
	}
	//Create tangent vector object
	public Vector2d tangent(){
		return new Vector2d(-this.y, this.x);
	}
	//Deep Copy = Make new vector object
	public Vector2d deepCopy() {
		return new Vector2d(x, y);
	}
	//Keep as doubles but remove decimals and floor
	public Vector2d integizerFloor() {
		return new Vector2d(Math.floor(this.x), Math.floor(this.y));
	}
	//Keep as doubles but remove decimals and ceil
	public Vector2d integizerCeil() {
		return new Vector2d(Math.ceil(this.x), Math.ceil(this.y));
	}
	//Returns new rotated vector object
	public Vector2d rotate(double angle) {
		double xPrime = this.x*Math.cos(angle) - this.y*Math.sin(angle);
		double yPrime = this.x*Math.sin(angle) + this.y*Math.cos(angle);
		return new Vector2d(xPrime, yPrime);
	}
	
}