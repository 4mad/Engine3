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
		
		//A RegPolygonGen is constructed by giving the number of sides and side length.
		public RegPolygonGen() {
			numberSides = 4;
			lengthSide = 10;	
			vertexes = this.generateVerts();
			longDistAcross = this.longestDist();
			shortDistAcross = this.shortestDist();
		}
		
		public RegPolygonGen(int numofSides, double lengthofSide) {
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
			return "RegPolygonGen [numberSides = " + numberSides + ", lengthSide = " + lengthSide + ", longDistAcross = "
					+ longDistAcross + ", shortDistAcross = " + shortDistAcross + ", vertexes = "
					+ Arrays.toString(vertexes) + "]";
		}
		
		private Vector2d[] generateVerts(){ //Does not update polygon object's vertexes, polygon.setVertexes(vert2d[]) required
			Vector2d[] verts = new Vector2d[numberSides];//make it so that it starts in "regular" position
			double interiorlength = lengthSide/Math.sqrt(2-2*Math.cos(2*Math.PI/numberSides));
			for(int i = 1; i <= this.numberSides ; i++){
				//This splits a circle into all the angles needed to make the vertexes, and rotates the object to the generic "start" position
				verts[i-1] = new Vector2d(interiorlength*Math.cos((2*Math.PI)/numberSides*i - Math.PI*(numberSides - 2)/(2*numberSides)), 
						interiorlength*Math.sin((2*Math.PI)/numberSides*i - Math.PI*(numberSides - 2)/(2*numberSides)));
			}
			return verts;
		}

		private double longestDist(){//can be faster?
			double longest = -1;

			for (int i = 0; i < vertexes.length; i++)
				for (int k = vertexes.length - 1; k > i; k--)
					if (vertexes[i].dist(vertexes[k]) > longest)
						longest = vertexes[i].dist(vertexes[k]);

			return longest;
		}

		private double shortestDist(){//can be faster?
			double shortest = Double.MAX_VALUE;

			for (int i = 0; i < vertexes.length; i++)
				for (int k = vertexes.length - 1; k > i; k--)
					if (this.vertexes[i].dist(vertexes[k]) < shortest)
						shortest = vertexes[i].dist(vertexes[k]);

			return shortest;
		}
}
