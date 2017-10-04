// David Govorko, 09/28/2017
package starter;
// Debug for GeomGuesserThread()
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;

// Need to add an NGeom Guesser to determine which ID to set
/*GuesserThreadDebug contains the following:
 * FIELDS:
 *  THREAD t;
 *  STRING threadName;
 *  HASHSET<GEOM> collisionData
 *  GEOM geomOG
 *  GEOM Calculated
 *  INT id (0 = Failed to identify, 1 = Line, 2 = Circle, 3 = Regular NGon, 4 Line Segment) list subject to change
 *   
 * CONSTRUCTORS:
 *  GuesserThreadDebug(String name, HashSet<Geom> collisionData, geomOG, ID): Default
 *  GuesserThreadDebug(String name, HashSet<Geom> collisionData, geomOG): If id is not known    
 *     
 * METHODS:
 *  Int geussID() : Guesses the ID based on NGeom data
 *  Geom kasaCircleGuessDebug() : as the name implies	
 *  Geom leastSquareLineGuessDebug() :  as the name implies
 *  Call(): Where the magic happens
 */
public class GuesserThreadDebug implements Callable<Geom>{// Loosely Based on http://www.concretepage.com/java/java-callable-example
	private String threadName;
	private HashSet<Geom> collisionData;
	private Geom geomOG; // Remove from non-debug
	private Geom calculated;
	private int id; // Make a class that will guess the id#)
	
	public GuesserThreadDebug(String name, HashSet<Geom> CollisionPoints, Geom OriginalGeom, int NGeomIdNumber){// Remove OriginalGeom for non-debug
		threadName = name;
		System.out.println("Creating " +  threadName );
		collisionData = CollisionPoints;
		geomOG = OriginalGeom;
		id = NGeomIdNumber;
	}
	
	public GuesserThreadDebug(String name, HashSet<Geom> CollisionPoints, Geom OriginalGeom){// This is in case one doesn't know what NGeom is being used
		threadName = name;
		System.out.println("Creating " +  threadName );
		collisionData = CollisionPoints;
		geomOG = OriginalGeom;
		id = guessID();
	}

	public int guessID(){// Some crazy math needs to happen here to figure out the NGeom based on a bunch of points
		return 0;
	}
	
	public Geom kasaCircleGuessDebug(){// Debug version that cheats by comparing the results to the actual Circle
		System.out.println("kasaCircleGuess IS IN DEGUB MODE!");//Debug only
		
		double aM,bM,rK = 0,A,B,C,D,E;//http://people.cas.uab.edu/~mosya/cl/geomOGFitByKasa.cpp
		double sumX2 = 0,sumX = 0, sumY2 = 0,sumY = 0,sumXY = 0,sumXY2 = 0,sumX3 = 0,sumX2Y = 0,sumY3 = 0;
		int iterate = this.collisionData.size();
		System.out.println("Number of Collision Points: " + iterate);//Debug only
		
		Geom kasaCircle = new Geom();
		Vector2d temp; //Iterating vector
		
		Iterator<Geom> iterator0 = this.collisionData.iterator();
		while (iterator0.hasNext()) {
			temp = iterator0.next().getOffset();
			System.out.println("Iterating vector: " + temp);//Debug only
			
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
		
		System.out.println("sumX      : " + sumX);//Debug only
		System.out.println("sumX2     : " + sumX2);//Debug only
		System.out.println("sumX3     : " + sumX3);//Debug only
		System.out.println("sumY      : " + sumY);//Debug only
		System.out.println("sumY2     : " + sumY2);//Debug only
		System.out.println("sumY3     : " + sumY3);//Debug only
		System.out.println("sumXY     : " + sumXY);//Debug only
		System.out.println("sumX2Y    : " + sumX2Y);//Debug only
		System.out.println("sumXY2    : " + sumXY2);//Debug only

		A = iterate*sumX2-sumX*sumX;
		B = iterate*sumXY-sumX*sumY;
		C = iterate*sumY2-sumY*sumY;
		D =  0.5*(iterate*sumXY2-sumX*sumY2+iterate*sumX3-sumX*sumX2);
		E =  0.5*(iterate*sumX2Y-sumX2*sumY+iterate*sumY3-sumY*sumY2);
		
		System.out.println("A  iterate*sumX2-sumX*sumX : " + A);// Debug only
		System.out.println("B  iterate*sumXY-sumX*sumY  : " + B);// Debug only
		System.out.println("C  iterate*sumY2-sumY*sumY  : " + C);// Debug only
		System.out.println("D  0.5*(iterate*sumXY2-sumX*sumY2+iterate*sumX3-sumX*sumX2)  : " + D);//Debug only
		System.out.println("E  0.5*(iterate*sumX2Y-sumX2*sumY+iterate*sumY3-sumY*sumY2)  : " + E);//Debug only

		aM = (D*C-B*E)/(A*C-B*B);
		bM = (A*E-B*D)/(A*C-B*B);

		Iterator<Geom> iterator1 = this.collisionData.iterator();
		while (iterator1.hasNext()) {
			temp = iterator1.next().getOffset();
			rK += (Math.pow(temp.getX() - aM, 2) + Math.pow(temp.getY() - bM, 2))/iterate;
		}
		rK = Math.sqrt(rK);
		System.out.println(2*rK + "   should be  " + geomOG.getGeometry().getLDA());//Debug only
		System.out.println(aM + "   should be  " + geomOG.getOffset().getX());//Debug only
		System.out.println(bM + "   should be  " + geomOG.getOffset().getY());//Debug only

		kasaCircle.getGeometry().setLDA(2*rK);//Circle Lda = diameter so 2 * radius
		kasaCircle.setOffset(new Vector2d(aM,bM));

		// input  apache math library for covariance and see if it is faster.
		return kasaCircle;
	}
	
	public Geom leastSquareLineGuessDebug(){// Debug version that cheats by comparing the results to the actual Line
		System.out.println("leastSquareLineGuess IS IN DEBUG MODE!");//Debug only

		double mX = 0,mY = 0,sumX2 = 0,sumXY = 0;// hotmath.com/hotmath_help/topics/line-of-best-fit.html
		Geom lsLine = new Geom();
		Vector2d temp; // Iterating vector
		
		
		Iterator<Geom> iterator0 = collisionData.iterator();
		System.out.println("Number of Collision Points: " + collisionData.size());//Debug only
		
		while (iterator0.hasNext()) {
			temp = iterator0.next().getOffset();
			System.out.println("Iterating vector: " + temp);//Debug only
			
			mX += temp.getX();
			mY += temp.getY();
		}

		mX = mX/collisionData.size();//Averages of x and y
		mY = mY/collisionData.size();
					
		System.out.println("mX      : " + mX);//Debug only
		System.out.println("mY      : " + mY);//Debug only
		
		Iterator<Geom> iterator1 = collisionData.iterator();
		while (iterator1.hasNext()) {
			lsLine.setOffset(iterator1.next().getOffset());
			sumX2 +=  Math.pow(lsLine.getOffset().getX() - mX, 2);
			sumXY += (lsLine.getOffset().getX() - mX)*(lsLine.getOffset().getY() - mY);
		}
		
		System.out.println("sumX2      : " + sumX2);//Debug only
		System.out.println("sumXY      : " + sumXY);//Debug only
		
		temp = new Vector2d(sumX2, sumXY);
		
		lsLine.setGeometry(new NGeom(-100*temp.magnitude(), new Vector2d[] {temp}));
		
		if (sumX2 == 0)//This in case the actual line is vertical so directionally x = 0 but y = almost infinity
			lsLine.setGeometry(new NGeom(-123456780, new Vector2d[] {new Vector2d(0.0,1.0)}));
		
		System.out.println(lsLine.getGeometry().getLDA() + "   should be  " + geomOG.getGeometry().getLDA());//Debug only
		System.out.println(lsLine.getOffset() + "   should be  " + geomOG.getOffset());//Debug only
		System.out.println(lsLine.getGeometry().getVertexes()[0] + "    should be  " + geomOG.getGeometry().getVertexes()[0]);
		
		return lsLine;
	}
	
	public Geom call() {// Debug version just has more strings for info 
	      System.out.println("Running " +  threadName );// Debug only
	      
	      if (id == 0) {
	    	  System.out.print("Sorry Geom Guessing has failed to identify the Geom");
	    	  calculated = new Geom();
	      }
	      if (id == 1) calculated = leastSquareLineGuessDebug();
	      if (id == 2) calculated = kasaCircleGuessDebug();	
	      if (id == 3) {
	    	  System.out.print("Sorry no NGon Geom Guessing has not been implemented yet");
	    	  calculated = new Geom();
	      }
	      if (id > 3 || id < 0) {
	    	  System.out.print("ERROR IN GEOM GUESSER THREAD An incorrect ID was assigned");
	    	  calculated = new Geom();
	      }
	      
	      System.out.println("Thread " +  threadName + " exiting.");// Debug only
	      
	      return calculated;    
	   }
	   
}
