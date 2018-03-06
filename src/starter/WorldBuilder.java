// David Govorko, 03/02/2018
package starter;
import java.util.List;
import java.util.ArrayList;

// An object to store all the real world and computer vision geometries and keep them organized for easy modification by user 
// Computer vision means geometries that have been established as fully guessed geometries, not any collision specific geometries
/*Geom contains the following fields and methods:
 * FIELDS:
 * 	List<NGeom> ngPoint: Contains all the NGeoms for Points
 * 	List<NGeom> ngLine: Contains all the NGeoms for Lines
 * 	List<NGeom> ngCircle: Contains all the NGeoms for Circles
 * 	List<NGeom> ngRegPoly: Contains all the NGeoms for Regular Polygons
 *  List<Geom> gPoint: Contains all the Geoms for Points
 *  List<Geom> gLine: Contains all the Geoms for Lines
 *  List<Geom> gCircle: Contains all the Geoms for Circles
 *  List<Geom> gRegPoly: Contains all the Geoms for Regular Polygons
 *  int wdX: world Dimension X useful for some geom properties
 *  int wdY: world Dimension Y useful for some geom properties
 *  int ldX: local Dimension X useful for some geom properties
 *  List<List<Geom>> worldGeom: The data structure holding all the data points
 * 
 * CONSTRUCTORS:
 * 	WorldBuilder(int localDimX, int worldDimY): Builds the real world using any methods*RealWorld()
 *  WorldBuilder(int worldDimX, int worldDimY, int localDimX): Builds the computer world using any methods*ComputerWorld(), usually empty unless Debug
 *    
 * METHODS:
 *  initializeNGeomRealWorld(): NGeom Custom real world instances
 *  initializeNGeomComputerWorld(): NGeom Custom Computer vision instances
 *  initializeGeomRealWorld(): Geom Custom real world instances
 *  initializeGeomComputerWorld(): Geom Custom Computer vision instances
 *  nGeomFillRealWorld(): Fills in custom NGeom data for real world
 *  nGeomFillComputerWorld(): Fill in NGeom Custom Computer vision data
 *  geomFillRealWorld(): Fill in custom Geom real world data
 *  geomFillComputerWorld(): Fill in custom Geom computer world data
 *  List<List<Geom>> getWorldGeometries(): Returns the field
 */
public class WorldBuilder {
	public List<NGeom> ngPoint;
	public List<NGeom> ngLine;
	public List<NGeom> ngCircle;
	public List<NGeom> ngRegPoly;
	
	public List<Geom> gPoint;
	public List<Geom> gLine;
	public List<Geom> gCircle;
	public List<Geom> gRegPoly;
	
	public int wdX;
	public int wdY;
	public int ldX;
	
	public List<List<Geom>> worldGeom;
	
	public WorldBuilder(int localDimX, int worldDimY) {// Builds the real world
		// World Dimensions
		//wdX = worldDimX;
		wdY = worldDimY;
		ldX = localDimX;// Effective size of real world
		// NGeom List instancing
		ngPoint = new ArrayList<NGeom>();
		ngLine = new ArrayList<NGeom>();
		ngCircle = new ArrayList<NGeom>();
		ngRegPoly = new ArrayList<NGeom>();
		// Geom List instancing
		gPoint = new ArrayList<Geom>();
		gLine = new ArrayList<Geom>();
		gCircle = new ArrayList<Geom>();
		gRegPoly = new ArrayList<Geom>();
		// Global List instance
		worldGeom = new ArrayList<List<Geom>>();
		// Customizable List populators
		initializeNGeomRealWorld();
		initializeGeomRealWorld();
		nGeomFillRealWorld();
		geomFillRealWorld();
		// Filling the global list
		worldGeom.add(gPoint);// 0
		worldGeom.add(gLine);// 1
		worldGeom.add(gCircle);// 2
		worldGeom.add(gRegPoly);// 3
	}
	
	public WorldBuilder(int worldDimX, int worldDimY, int localDimX) {// ComputerVision World Builder which is offset from the regular world
		// World Dimensions
		wdX = worldDimX;
		wdY = worldDimY;
		ldX = localDimX;
		// NGeom List instancing
		ngPoint = new ArrayList<NGeom>();
		ngLine = new ArrayList<NGeom>();
		ngCircle = new ArrayList<NGeom>();
		ngRegPoly = new ArrayList<NGeom>();
		// Geom List instancing
		gPoint = new ArrayList<Geom>();
		gLine = new ArrayList<Geom>();
		gCircle = new ArrayList<Geom>();
		gRegPoly = new ArrayList<Geom>();
		// Global list
		worldGeom = new ArrayList<List<Geom>>();
		// Customizable List populators
		initializeNGeomComputerWorld();
		initializeGeomComputerWorld();
		nGeomFillComputerWorld();
		geomFillComputerWorld();
		// Populating the global list
		worldGeom.add(gPoint);// 0
		worldGeom.add(gLine);// 1
		worldGeom.add(gCircle);// 2
		worldGeom.add(gRegPoly);// 3
	}
	
	public void initializeNGeomRealWorld(){// NGeom Custom real world instances
		NGeom linear0 = new NGeom();// Top Line 0
		ngLine.add(linear0);
		NGeom linear1 = new NGeom();// Right Line 1
		ngLine.add(linear1);
		NGeom linear2 = new NGeom();// Bottom Line 2
		ngLine.add(linear2);
		NGeom linear3 = new NGeom();// Left Line 3
		ngLine.add(linear3);
		NGeom circular = new NGeom();// Circle 0
		ngCircle.add(circular);
		NGeom squarical = new NGeom();// Square 0
		ngRegPoly.add(squarical); 
	}

	public void initializeNGeomComputerWorld(){// NGeom Custom Computer vision instances
		// None usually, unless being populated with debug data
	}
	
	public void initializeGeomRealWorld(){// Geom Custom real world instances
		Geom LINETOP = new Geom();
		gLine.add(LINETOP);// TopLine 0
		Geom LINERIGHT = new Geom();
		gLine.add(LINERIGHT);// RightLine 1
		Geom LINEBOTTOM = new Geom();
		gLine.add(LINEBOTTOM);// BottomLine 2
		Geom LINELEFT = new Geom();
		gLine.add(LINELEFT);// BotttomLine 3
		Geom CIRCLE = new Geom();
		gCircle.add(CIRCLE);// Circle 0
		Geom SQUARE = new Geom();
		gRegPoly.add(SQUARE);// RegPoly 0
	}
	
	public void initializeGeomComputerWorld(){// Geom Custom Computer vision instances
		// Ideally this list should be zero at the start and the guessing algortihm popultes this.
		Geom computerCIRCLE = new Geom();// KeyPress Circle Render
		gCircle.add(computerCIRCLE);// Circle 0
		Geom computerLINEtop = new Geom();// KeyPress Line Render Top
		gLine.add(computerLINEtop);// Line 0
		Geom computerLINEright = new Geom();// KeyPress Line Render Right
		gLine.add(computerLINEright);// Line 1
		Geom computerLINEbottom = new Geom();// KeyPress Line Render Bottom
		gLine.add(computerLINEbottom);// Line 2
		Geom computerLINEleft = new Geom();// KeyPress Line Render Left
		gLine.add(computerLINEleft);// Line 3
	}
	
	public void nGeomFillRealWorld() {// Fills in custom NGeom data for real world
		// Top Line
		ngLine.get(0).setVert(new Vector2d[] {new Vector2d(1,0)});
		ngLine.get(0).setLDA(-1*ldX/ngLine.get(0).getVertexes()[0].magnitude());
		// Right line
		ngLine.get(1).setVert(new Vector2d[] {new Vector2d(0,1)});
		ngLine.get(1).setLDA(-1*wdY/ngLine.get(1).getVertexes()[0].magnitude());
		// Bottom Line
		ngLine.get(2).setVert(new Vector2d[] {new Vector2d(1,0)});
		ngLine.get(2).setLDA(-1*ldX/ngLine.get(2).getVertexes()[0].magnitude());
		// Left line
		ngLine.get(3).setVert(new Vector2d[] {new Vector2d(0,1)});
		ngLine.get(3).setLDA(-1*wdY/ngLine.get(3).getVertexes()[0].magnitude());
		// Circle
		ngCircle.get(0).setLDA(250);// Diameter = 250
		//Regular polygon generator
		RegPolygonGen square = new RegPolygonGen(4, 50);// 4 sides each being 50
		ngRegPoly.get(0).setVert(square.getVertexes());
		ngRegPoly.get(0).setLDA(square.getLongDistAcross());
	}
	
	public void nGeomFillComputerWorld() {// Fill in NGeom Custom Computer vision data
		// None usually, unless being populated with debug data
	}
	
	public void geomFillRealWorld() {// Fill in custom Geom real world data
		// Top Line
		gLine.get(0).setGeometry(ngLine.get(0));
		gLine.get(0).setOffset(new Vector2d(0,wdY-1));
		// Right Line
		gLine.get(1).setGeometry(ngLine.get(1));
		gLine.get(1).setOffset(new Vector2d(ldX,0));
		// Bottom Line
		gLine.get(2).setGeometry(ngLine.get(2));
		gLine.get(2).setOffset(new Vector2d(0,0));
		// Left line
		gLine.get(3).setGeometry(ngLine.get(3));
		gLine.get(3).setOffset(new Vector2d(1,0));
		// Circle
		gCircle.get(0).setGeometry(ngCircle.get(0));
		gCircle.get(0).setOffset(new Vector2d(300,450));
		// Square
		gRegPoly.get(0).setGeometry(ngRegPoly.get(0));
		gRegPoly.get(0).setOffset(new Vector2d(500,420));
		gRegPoly.get(0).setVelocity(new Vector2d(-1,0.1));
		gRegPoly.get(0).setDeltaAngle(1);
		gRegPoly.get(0).setAngle(0);
	}
	
	public void geomFillComputerWorld() {// Fill in custom Geom computer world data
		// Should be empty unless for debug 
	}
	
	public List<List<Geom>> getWorldGeometries(){
		return worldGeom;
	}
}
