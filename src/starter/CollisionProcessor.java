// David Govorko 03/06/2018
package starter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CollisionProcessor {
	// Maybe CollisionsGlobal is just a List of HashSets, Does classifying the HashSets help?
	public List<List<HashSet<Geom>>> CollisionGlobal;
	List <HashSet<Geom>> pointColliders;
	List <HashSet<Geom>> lineColliders;
	List <HashSet<Geom>> circleColliders;
	List <HashSet<Geom>> regPolyColliders;
	
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
	
	
	public List<List<HashSet<Geom>>> getCollisionGlobal() {// Getter
		return CollisionGlobal;
	}
		
}
