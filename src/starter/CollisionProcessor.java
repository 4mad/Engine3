// David Govorko 03/06/2018
package starter;

/*CollisionProcessor contains the following:
 * FIELDS:
 *  List<List<HashSet<Geom>>> CollisionGlobal : Container of GeomType, with HashSets per Geom type, and geoms in each HashSet
 *  List <HashSet<Geom>> pointColliders : Point Geom Type List
 *  List <HashSet<Geom>> lineColliders : Line Geom Type List:
 *  List <HashSet<Geom>> circleColliders : CircleGeom Type List
 *  List <HashSet<Geom>> regPolyColliders : regPoly Geom Type List
 *   
 * CONSTRUCTORS:     
 *  CollisionProcessor() : Instances CollisionGlobal and fills it with instances of the List<HashSet<Geoms>>
 *     
 * METHODS: 
 * 	void collisionDistanceCompareDebug(Geom collide, double minSpread) : Adds new points to any HashSet that has points within the minSpread distance
 *  void collisionDistanceCompareDebug(Geom collide, double minSpread) : Debug of above
 *  void walkthorughString(): Walks-through the CollisionGlobal Container and prints out each Geom, which HashSet is it from, and which GeomType is it from.
 *  void baseSort() : Goes through individual HashSets and identifies each Geom then cut and pastes it in its respective Geom Type List
 *  void baseSortDebug() : Debug of above
 *  List<List<HashSet<Geom>>> getCollisionGlobal() : returns CollisionGlobal
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CollisionProcessor {
	// Maybe CollisionsGlobal is just a List of HashSets, Does classifying the HashSets help?
	public List<List<HashSet<Geom>>> CollisionGlobal;
	public List <HashSet<Geom>> pointColliders;
	public List <HashSet<Geom>> lineColliders;
	public List <HashSet<Geom>> circleColliders;
	public List <HashSet<Geom>> regPolyColliders;
	
	public CollisionProcessor() {// Normal Constructor sets up hashSets for each geom Group which is being collided into.
		CollisionGlobal = new ArrayList<List<HashSet<Geom>>>();
		List <HashSet<Geom>> pointColliders = new ArrayList<HashSet<Geom>>();
		List <HashSet<Geom>> lineColliders = new ArrayList<HashSet<Geom>>();
		List <HashSet<Geom>> circleColliders = new ArrayList<HashSet<Geom>>();
		List <HashSet<Geom>> regPolyColliders = new ArrayList<HashSet<Geom>>();
		// Collision detection lists
		CollisionGlobal.add(pointColliders);
		CollisionGlobal.add(lineColliders);
		CollisionGlobal.add(circleColliders);
		CollisionGlobal.add(regPolyColliders);
	}
	
	// Compares collision distance to all points in store and then adds it to respective HashSet or makes a new HashSet altogether
	public void collisionDistanceCompareDebug(Geom collide, double minSpread) { 
		System.out.println("{{{{{CollisionDistanceCompareDebug Started}}}}}");
		int listLevel = 0;// Debug only
		int hashLevel = 0;// Debug only
		int listMem = 0;
		int hashMem = 0;
		boolean added = false;
		for (List<HashSet<Geom>> listOfGeoms: this.CollisionGlobal) {
			System.out.println("List Level: " + listLevel);
			for (HashSet<Geom> listOfHashSets: listOfGeoms) {
				System.out.println("Hash Level: " + hashLevel);	
				for (Geom collideWith: listOfHashSets) {
					if (collideWith.getOffset().dist(collide.getOffset()) <= minSpread) {
						System.out.println(collideWith.getOffset() + "  at a distance of " + collideWith.getOffset().dist(collide.getOffset()));
						System.out.println("Geom added to HashSet # " + hashLevel + " of Geom type " + listLevel + " . Geom is as follows. " + collide);
						
						added = true;
						listMem = listLevel;
						hashMem = hashLevel;
					}
				}// Individual Geoms
				hashLevel++;
			}// List of Hashes
			hashLevel = 0;
			listLevel++;
		}// Data Walkthrough
		
		if (added) {
			this.getCollisionGlobal().get(listMem).get(hashMem).add(collide);
		}else {
			HashSet<Geom> addTemp = new HashSet<Geom>();
			addTemp.add(collide);
			this.getCollisionGlobal().get(0).add(addTemp);
			System.out.println("HashSet added to Geom type 0. Geom is as follows. " + collide);
		}
	}
	
	// Compares collision distance to all points in store and then adds it to respective HashSet or makes a new HashSet altogether
	public void collisionDistanceCompare(Geom collide, double minSpread) { 
		int listLevel = 0;
		int hashLevel = 0;
		int listMem = 0;
		int hashMem = 0;
		boolean added = false;
		for (List<HashSet<Geom>> listOfGeoms: this.CollisionGlobal) {
			System.out.println("List Level: " + listLevel);
			for (HashSet<Geom> listOfHashSets: listOfGeoms) {
				System.out.println("Hash Level: " + hashLevel);	
				for (Geom collideWith: listOfHashSets) {
					if (collideWith.getOffset().dist(collide.getOffset()) <= minSpread) {
						added = true;
						listMem = listLevel;
						hashMem = hashLevel;
					}
				}// Individual Geoms
				hashLevel++;
			}// List of Hashes
			hashLevel = 0;
			listLevel++;
		}// Data Walkthrough
		
		if (added) {
			this.getCollisionGlobal().get(listMem).get(hashMem).add(collide);
		}else {
			HashSet<Geom> addTemp = new HashSet<Geom>();
			addTemp.add(collide);
			this.getCollisionGlobal().get(0).add(addTemp);
		}
	}
	
	public void walkthorughString() {
		int listLevel = 0;// Debug only
		int hashLevel = 0;// Debug only
			for (List<HashSet<Geom>> listOfGeoms: this.CollisionGlobal) {
				for (HashSet<Geom> listOfHashSets: listOfGeoms) {
					for (Geom collideWith: listOfHashSets)
						System.out.println("Geom " + collideWith.getOffset() + " at HashSet Level " + hashLevel + " at a GeomType Level of " + listLevel);
						
					hashLevel++;
				}// Individual Hashes
				hashLevel = 0;
				listLevel++;
			}// List of Geoms Types
				
		//return new Geom Box;
	}
	
	public void baseSortDebug() {// Sorts through the points from the base collision data and puts them into their respective categories. 
		List<HashSet<Geom>> sortingRecord = new ArrayList<HashSet<Geom>>();// Records the HashSetID# so that add and remove can be performed later
		for (HashSet<Geom> listOfHashSets: this.getCollisionGlobal().get(0)) {// Iterates through the Hashes and checks for "guessable" hashSets
			if (listOfHashSets.size() >= 4) {// A bit of buffer for the Guesser, ideally this should be 3
				HashSet<Geom> tempHashSet = new HashSet<Geom>();
				tempHashSet = listOfHashSets;// Deep Copy Is this good?
				System.out.println("Sorting found the HashSet below to move!");
				System.out.println(listOfHashSets.toString());// Debug Help
				sortingRecord.add(tempHashSet);// This data is important
			}// If >= 4
		}// For loop HashSets
		GuesserThread guess;
		System.out.println("Let the Sorting Begin!");
		for (int i = 0; i < sortingRecord.size(); i++) {
			guess = new GuesserThread(sortingRecord.get(i));
			System.out.println("GusserID is " + guess.guessID());
			this.getCollisionGlobal().get(guess.guessID()).add(sortingRecord.get(i));// Adds HashSet to it's respective GeomID List
			this.getCollisionGlobal().get(0).remove(sortingRecord.get(i));// Remove points from first list
		}
		System.out.println("Final Sorting Result");
		this.walkthorughString();
	}
	
	public void baseSort() {// Sorts through the points from the base collision data and puts them into their respective categories. 
		List<HashSet<Geom>> sortingRecord = new ArrayList<HashSet<Geom>>();
		for (HashSet<Geom> listOfHashSets: this.getCollisionGlobal().get(0)) {
			if (listOfHashSets.size() >= 4) {
				HashSet<Geom> tempHashSet = new HashSet<Geom>();
				tempHashSet = listOfHashSets;
				sortingRecord.add(tempHashSet);
			}// If >= 4
		}// For loop HashSets
		GuesserThread guess;
		for (int i = 0; i < sortingRecord.size(); i++) {
			guess = new GuesserThread(sortingRecord.get(i));
			this.getCollisionGlobal().get(guess.guessID()).add(sortingRecord.get(i));
			this.getCollisionGlobal().get(0).remove(sortingRecord.get(i));
		}
	}
	
	public List<List<HashSet<Geom>>> getCollisionGlobal() {// Getter
		return CollisionGlobal;
	}
		
}
