// David Govorko, 09/29/2017
package starter;
// A Callable thread that performs geometry guessing for circles and lines
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;

// Need to add an NGeom Guesser to determine which ID to set
/*GuesserThread contains the following:
 * FIELDS:
 *  HASHSET<GEOM> collisionData
 *  GEOM Calculated
 *  INT id (0 = Failed to identify, 1 = Line, 2 = Circle, 3 = Regular NGon, 4 = Line Segment) list subject to change
 *   
 * CONSTRUCTORS:     
 *  GuesserThread(HashSet<Geom> collisionData, ID): Default
 *  GuesserThread(HashSet<Geom> collisionData): If Id is not known
 *     
 * METHODS: 
 *  Int geussID() : Guesses the ID based on NGeom data
 *  Geom kasaCircleGuess() : as the name implies	
 *  Geom leastSquareLineGuess() :  as the name implies
 *  Call(): Where the magic happens
 */
public class GuesserThread implements Callable<Geom>{// Loosely Based on http://www.concretepage.com/java/java-callable-example
	private HashSet<Geom> collisionData;
	private Geom calculated;
	private int id; // Make a class that will guess the id#)
	
	public GuesserThread(HashSet<Geom> CollisionPoints, int NGeomIdNumber){
		collisionData = CollisionPoints;
		id = NGeomIdNumber;
	}
	
	public GuesserThread(HashSet<Geom> CollisionPoints){ // This is in case one doesn't know what NGeom is being used
		collisionData = CollisionPoints;
		id = guessID();
	}

	public int guessID(){//Able to discern between a line and a circle from discrete data points
		int idCalc = 0; //Default unknown geom value
		Geom tempGeom = this.kasaCircleGuess();
		
		if(tempGeom.getGeometry().getLDA() >= 5000) {//Dif. between line & circle is line = circle with radius > 100,0000
			idCalc = 1;
		} else if (tempGeom.getGeometry().getLDA() < 5000){//Circle assumed when radius < 100,000
			idCalc = 2;
		}//REGPolygon guesser needed here
				
		return idCalc;
	}
	
	public Geom kasaCircleGuess(){// Guesses a circle's center and radius based on collision data
		double aM,bM,rK = 0,A,B,C,D,E;//http://people.cas.uab.edu/~mosya/cl/geomOGFitByKasa.cpp
		double sumX2 = 0,sumX = 0, sumY2 = 0,sumY = 0,sumXY = 0,sumXY2 = 0,sumX3 = 0,sumX2Y = 0,sumY3 = 0;
		int iterate = this.collisionData.size();
		Geom kasaCircle = new Geom();
		Vector2d temp; //Iterating vector
		
		Iterator<Geom> iterator0 = this.collisionData.iterator();
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

		Iterator<Geom> iterator1 = this.collisionData.iterator();
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
	
	public Geom leastSquareLineGuess(){// Guesses a line based on collision data provided
		double mX = 0,mY = 0,sumX2 = 0,sumXY = 0;// hotmath.com/hotmath_help/topics/line-of-best-fit.html
		Geom lsLine = new Geom();
		Vector2d temp; // Iterating vector
		
		Iterator<Geom> iterator0 = collisionData.iterator();
		while (iterator0.hasNext()) {
			temp = iterator0.next().getOffset();
			mX += temp.getX();
			mY += temp.getY();
		}

		mX = mX/collisionData.size();//Averages of x and y
		mY = mY/collisionData.size();
		
		Iterator<Geom> iterator1 = collisionData.iterator();
		while (iterator1.hasNext()) {
			lsLine.setOffset(iterator1.next().getOffset());
			sumX2 +=  Math.pow(lsLine.getOffset().getX() - mX, 2);
			sumXY += (lsLine.getOffset().getX() - mX)*(lsLine.getOffset().getY() - mY);
		}
		
		temp = new Vector2d(sumX2, sumXY);
		
		lsLine.setGeometry(new NGeom(-5000, new Vector2d[] {temp}));//-5000 since - = LINE and 5000 = max diag possible
		
		if (sumX2 <= 0.01)// This in case the actual line is vertical so directionally x = 0 but y = almost infinity
			lsLine.setGeometry(new NGeom(-5000, new Vector2d[] {new Vector2d(0.0,1.0)}));
		
		return lsLine;
	}
	
	public Geom call() {// What gets called back to the main thread
	      if (id == 0) {
	    	  System.out.print("Sorry Geom Guessing has failed to identify the Geom");
	    	  calculated = new Geom();
	      }
	      if (id == 1) calculated = leastSquareLineGuess();
	      if (id == 2) calculated = kasaCircleGuess();	
	      if (id == 3) {
	    	  System.out.print("Sorry no NGon Geom Guessing has not been implemented yet");
	    	  calculated = new Geom();
	      }
	      if (id > 3 || id < 0) {
	    	  System.out.print("ERROR IN GEOM GUESSER THREAD An incorrect ID was assigned");
	    	  calculated = new Geom();
	      }
	      return calculated;    
	   }
}
