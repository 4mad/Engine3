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
	
	public CollisionProcessor(boolean debug) {// Debug constructor with debug data
		CollisionGlobal = new ArrayList<List<HashSet<Geom>>>();
		List <HashSet<Geom>> pointColliders = new ArrayList<HashSet<Geom>>();
		List <HashSet<Geom>> lineColliders = new ArrayList<HashSet<Geom>>();
		List <HashSet<Geom>> circleColliders = new ArrayList<HashSet<Geom>>();
		List <HashSet<Geom>> regPolyColliders = new ArrayList<HashSet<Geom>>();
		// Collision detection geom lists
		CollisionGlobal.add(pointColliders);
		CollisionGlobal.add(lineColliders);
		CollisionGlobal.add(circleColliders);
		CollisionGlobal.add(regPolyColliders);
		if (debug) {
			// Collision detection HashSets
			//HashSet<Geom> circleCollide = new HashSet<Geom>();// Contains points of circle collision
			HashSet<Geom> topLineCollide = new HashSet<Geom>();// Contains points of Top line collision
			HashSet<Geom> rightLineCollide = new HashSet<Geom>();// Contains points of Right line collision
			HashSet<Geom> bottomLineCollide = new HashSet<Geom>();// Contains points of Bottom line collision
			HashSet<Geom> leftLineCollide = new HashSet<Geom>();// Contains points of Left line collision
			// HashSets put into their respective geoms
			CollisionGlobal.get(1).add(topLineCollide);// Line 0
			CollisionGlobal.get(1).add(rightLineCollide);// Line 1
			CollisionGlobal.get(1).add(bottomLineCollide);// Line 2
			CollisionGlobal.get(1).add(leftLineCollide);// Line 3
			//CollisionGlobal.get(2).add(circleCollide);// Circle 0
		}
	}

	public void collisionDistanceCompareDebug() {
		
	}
	
	
	public List<List<HashSet<Geom>>> getCollisionGlobal() {// Getter
		return CollisionGlobal;
	}
		
}
