//David Govorko, 06/28/2017
package starter;
import java.util.Arrays;

//NGeom is geometry with origin at 0 and no rotation represented by vertexes and the value for the longest distance across.
/* The Following is the breakdown of how each geometry type is represented
 * POINT: Vertexes = [0], LDA = 0
 * LINE SEGMENT: Vertexes = [1], LDA = line segment length
 * LINE: Vertexes = [1], LDA = -1*(very long length E.G. diagonal of window)
 * CIRCLE: Vertexes = [0], LDA = diameter
 * REGULAR POLYGON: Vertexes = [#>2], LDA = calculated length 
 * ARC/IREGULAR POLYGONS???
 */
public class NGeom {

	private double LDA; //longest distance across
	private Vector2d[] Vertexes; //All Vertexes
	
	public NGeom(){//Default is a point
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
	
	public double getSideLength(){ //Returns calculated side length if geometry is a regular polygon.
		if (Vertexes.length > 2){
			return LDA*Math.sin((Vertexes.length-2)*Math.PI/(Vertexes.length)/2); //LDA*sin(internal angle/2)
		} else {
			return 0;
		}
	}
	
}
