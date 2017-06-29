//David Govorko, 04/04/2017
package starter;

import java.util.Arrays;

//A regular polygon generation tool that generates vertexes of the polygon and calculates some useful dimensions
//TODO:Optimize min/max distance across polygon && is shortest distance across necessary??
public class RegPolygonGen {
		
		private int numberSides;
		private double lengthSide;
		private double longDistAcross;
		private double shortDistAcross;
		private  Vector2d[] vertexes;
		
		//A RegPolygonGen is constructed by giving the number of sides, side length, angle of rotation around center, a center point, and a direction vector
		public RegPolygonGen() {
			numberSides = 3;
			lengthSide = 10;	
			vertexes = this.generateVerts();
			longDistAcross = this.longestDist();
			shortDistAcross = this.shortestDist();
		}

		public RegPolygonGen(int numofSides, double lengthofSide, double angleSizeRad, Vector2d centerPoint) {
			numberSides = numofSides;
			lengthSide = lengthofSide;		
			vertexes = this.generateVerts();
			longDistAcross = this.longestDist();
			shortDistAcross = this.shortestDist();
		}
		
		public RegPolygonGen(int numofSides, double lengthofSide, double angleSizeRad, Vector2d centerPoint, Vector2d directionVector) {
			numberSides = numofSides;
			lengthSide = lengthofSide;		
			vertexes = this.generateVerts();
			longDistAcross = this.longestDist();
			shortDistAcross = this.shortestDist();
		}

		public double getLongDistAcross() {
			return longDistAcross;
		}

		public double getShortDistAcross() {
			return shortDistAcross;
		}

		public Vector2d[] getVertexes() {
			return vertexes;
		}

		public void setVertexes(Vector2d[] vertexes) {
			this.vertexes = vertexes;
		}

		public int getNumberSides() {
			return numberSides;
		}

		public void setNumberSides(int numberSides) {
			this.numberSides = numberSides;
		}

		public double getLengthSide() {
			return lengthSide;
		}

		public void setLengthSide(double lengthSide) {
			this.lengthSide = lengthSide;
		}	
		
		public String toString() {
			return "RegPolygonGen [numberSides=" + numberSides + ", lengthSide=" + lengthSide + ", longDistAcross="
					+ longDistAcross + ", shortDistAcross=" + shortDistAcross + ", vertexes="
					+ Arrays.toString(vertexes) + "]";
		}
		
		private Vector2d[] generateVerts(){ //Does not update polygon object's vertexes, polygon.setVertexes(vert2d[]) required
			Vector2d[] verts = new Vector2d[this.getNumberSides()];//make it so that it starts in "regular" position
			double interiorlength = this.getLengthSide()/Math.sqrt(2-2*Math.cos(2*Math.PI/this.getNumberSides()));
			for(int i = 1; i <= this.getNumberSides() ; i++){
				//This splits a circle into all the angles needed to make the vertexes, and rotates the object to the generic "start" position
				verts[i-1] = new Vector2d(interiorlength*Math.cos((2*Math.PI)/this.getNumberSides()*i - Math.PI*(this.getNumberSides() - 2)/(2*this.getNumberSides())), 
						interiorlength*Math.sin((2*Math.PI)/this.getNumberSides()*i - Math.PI*(this.getNumberSides() - 2)/(2*this.getNumberSides())));
			}
			return verts;
		}

		private double longestDist(){//can be faster?
			double longest = -1;

			for (int i = 0; i < this.getVertexes().length; i++)
				for (int k = this.getVertexes().length - 1; k > i; k--)
					if (this.getVertexes()[i].dist(this.getVertexes()[k]) > longest)
						longest = this.getVertexes()[i].dist(this.getVertexes()[k]);

			return longest;
		}

		private double shortestDist(){//can be faster?
			double shortest = Double.MAX_VALUE;

			for (int i = 0; i < this.getVertexes().length; i++)
				for (int k = this.getVertexes().length - 1; k > i; k--)
					if (this.getVertexes()[i].dist(this.getVertexes()[k]) < shortest)
						shortest = this.getVertexes()[i].dist(this.getVertexes()[k]);

			return shortest;
		}
}
