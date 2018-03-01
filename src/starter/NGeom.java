// David Govorko, 06/28/2017
package starter;
// NGeom is geometry with origin at 0 and no rotation represented by vertexes and the value for the longest distance across.
import java.util.Arrays;

/* The Following is the breakdown of how each geometry type is represented
 *  POINT: Vertexes = [0], LDA = 0
 *  LINE SEGMENT: Vertexes = [1], LDA = line segment length
 *  LINE: Vertexes = [1], LDA = -1*(2*diagonal of window)
 *  CIRCLE: Vertexes = [0], LDA = diameter
 *  REGULAR POLYGON: Vertexes = [#>2], LDA = calculated length 
 *  ARC/IREGULAR POLYGONS???
 * ----------------------------------------------------------------------------------------------------------------------------
 *NGeom contains the following:
 * Fields:
 *  DOUBLE LDA: Longest distance across
 *  VECTOR2D[] Vertexes: A Vector2d matrix with all the vertexes in the forms of pairs of doubles
 *  
 * Constructors:
 *  NGeom(): Default point
 *  NGeom(double lda, Vector2d[] vertexes): all fields can be set
 *  
 * Methods:
 *  Getters/setters: for all fields
 *  String toString(): To String
 *  Double getSideLength(): Side Length calculated (assuming regular polygon)
 */
public class NGeom {

	private double LDA;// Longest distance across
	private Vector2d[] Vertexes;// All Vertexes
	
	public NGeom(){// Default is a point
		LDA = 0;
		Vertexes = new Vector2d[0];
	}
	
	public NGeom(double lda, Vector2d[] vertexes){
		LDA = lda;
		Vertexes = vertexes;
	}

	public double getLDA() {
		return LDA;
	}

	public void setLDA(double lda) {
		this.LDA = lda;
	}

	public Vector2d[] getVertexes() {
		return Vertexes;
	}

	public void setVert(Vector2d[] vertexes) {
		this.Vertexes = vertexes;
	}

	public String toString() {
		return "NGeom [LDA = " + LDA + ", Vertexes = " + Arrays.toString(Vertexes) + "]";
	}
	
	public double getSideLength(){// Returns calculated side length if geometry is a regular polygon.
		if (Vertexes.length > 2){
			return LDA*Math.sin((Vertexes.length-2)*Math.PI/(Vertexes.length)/2);// LDA*sin(internal angle/2)
		} else {
			System.out.println("NGeometry Mismatch Error: getSideLength() does not work for Ngeometry "
					+ "with less than 3 sides such as this NGeom: " + this.toString());
			return 0;
		}
	}
	
}
