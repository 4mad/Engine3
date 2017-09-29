// David Govorko, 09/28/2017
package starter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;

// Make a normal version without the println's
// Need to add an NGeom Guesser to determine which geusser to use
// Make a numbered parameter that forces a certain guesser for pre-determined NGeoms
// E.G. 0 = NGeom guesser, 1 = kasaCircle, 2 = leastSquareline
/*GuesserThreadDebug contains the following fields and methods:
 *  FIELDS:
 *  Thread t;
 *  String threadName;
 *  HashSet<Geom> collisionData
 *  Geom geomOG
 *  Geom Calcualated
 *    
 *  METHODS:
 *  GuesserThreadDebug(String name, HashSet<Geom> collisionData, geomOG): Instancing
 * 	Call(): Where the magic happens
 * 	Geom kasaCircleGuessDebug() : as the name implies
 *  
 */
public class GuesserThreadDebug implements Callable<Geom>{// Loosely Based on http://www.concretepage.com/java/java-callable-example
	private String threadName;
	private HashSet<Geom> circleCollisionData; // Change to collision data once line guesser is added
	private Geom Circle; // Change to geomOG once line guesser is added
	private Geom calculated;
	
	public GuesserThreadDebug(String name, HashSet<Geom> circCollisionData, Geom Circles){ // Change to circleCollisionData||geomOG once line guesser is added
		threadName = name;
		System.out.println("Creating " +  threadName );
		circleCollisionData = circCollisionData;
		Circle = Circles;
	}
	
	public Geom kasaCircleGuessDebug(){
		System.out.println("kasaCircleGuess IS IN DEGUB MODE!");//Debug only
		
		double aM,bM,rK = 0,A,B,C,D,E;//http://people.cas.uab.edu/~mosya/cl/CircleFitByKasa.cpp
		double sumX2 = 0,sumX = 0, sumY2 = 0,sumY = 0,sumXY = 0,sumXY2 = 0,sumX3 = 0,sumX2Y = 0,sumY3 = 0;
		int iterate = this.circleCollisionData.size();
		System.out.println("Number of Collision Points: " + iterate);//Debug only
		
		Geom kasaCircle = new Geom();
		Vector2d temp; //Iterating vector
		
		Iterator<Geom> iterator0 = this.circleCollisionData.iterator();
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
		
		System.out.println("A  iterate*sumX2-sumX*sumX : " + A);//Debug only
		System.out.println("B  iterate*sumXY-sumX*sumY  : " + B);//Debug only
		System.out.println("C  iterate*sumY2-sumY*sumY  : " + C);//Debug only
		System.out.println("D  0.5*(iterate*sumXY2-sumX*sumY2+iterate*sumX3-sumX*sumX2)  : " + D);//Debug only
		System.out.println("E  0.5*(iterate*sumX2Y-sumX2*sumY+iterate*sumY3-sumY*sumY2)  : " + E);//Debug only

		aM = (D*C-B*E)/(A*C-B*B);
		bM = (A*E-B*D)/(A*C-B*B);

		Iterator<Geom> iterator1 = this.circleCollisionData.iterator();
		while (iterator1.hasNext()) {
			temp = iterator1.next().getOffset();
			rK += (Math.pow(temp.getX() - aM, 2) + Math.pow(temp.getY() - bM, 2))/iterate;
		}
		rK = Math.sqrt(rK);
		System.out.println(2*rK + "   should be  " + Circle.getGeometry().getLDA());//Debug only
		System.out.println(aM + "   should be  " + Circle.getOffset().getX());//Debug only
		System.out.println(bM + "   should be  " + Circle.getOffset().getY());//Debug only

		kasaCircle.getGeometry().setLDA(2*rK);//Circle Lda = diameter so 2 * radius
		kasaCircle.setOffset(new Vector2d(aM,bM));

		// input  apache math library for covariance and see if it is faster.
		return kasaCircle;
	}
	
	public Geom call() {
	      System.out.println("Running " +  threadName );
	      
	      calculated = kasaCircleGuessDebug();	
	     	     
	      System.out.println("Thread " +  threadName + " exiting.");
	      
	      return calculated;	     
	   }
	   
}
