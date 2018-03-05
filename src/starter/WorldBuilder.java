// David Govorko, 03/02/2018
package starter;
import java.util.List;
import java.util.ArrayList;

// An object to store all the geometries and keep them organized for easy modification by user 
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
	
	public WorldBuilder(int worldDimX, int worldDimY, int localDimX) {
		wdX = worldDimX;
		wdY = worldDimY;
		ldX = localDimX;
		
		ngPoint = new ArrayList<NGeom>();
		ngLine = new ArrayList<NGeom>();
		ngCircle = new ArrayList<NGeom>();
		ngRegPoly = new ArrayList<NGeom>();
		
		gPoint = new ArrayList<Geom>();
		gLine = new ArrayList<Geom>();
		gCircle = new ArrayList<Geom>();
		gRegPoly = new ArrayList<Geom>();
		
		worldGeom = new ArrayList<List<Geom>>();
		
		initializeNGeom();
		initializeGeom();
		nGeomFill();
		geomFill();
		worldGeom.add(gPoint);// 0
		worldGeom.add(gLine);// 1
		worldGeom.add(gCircle);// 2
		worldGeom.add(gRegPoly);// 3
	}
	
	public List<List<Geom>> getworldGeom(){
		return worldGeom;
	}
	
	public void initializeNGeom(){// NGeom variable instances
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
	
	public void initializeGeom(){// Geom Variable instances
		Geom COLLISIONPOINT = new Geom();// Point used to render all collision points
		gPoint.add(COLLISIONPOINT);// Point 0
		// Circle Collide Point 1
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
		// Temps
		Geom tempCIRCLE = new Geom();// KeyPress Circle Render
		gCircle.add(tempCIRCLE);// Circle 1
		Geom tempLINEtop = new Geom();// KeyPress Line Render Top
		gLine.add(tempLINEtop);// Line 4
		Geom tempLINEright = new Geom();// KeyPress Line Render Right
		gLine.add(tempLINEright);// Line 5
		Geom tempLINEbottom = new Geom();// KeyPress Line Render Bottom
		gLine.add(tempLINEbottom);// Line 6
		Geom tempLINEleft = new Geom();// KeyPress Line Render Left
		gLine.add(tempLINEleft);// Line 7
	}
	
	public void nGeomFill() {// Fills in NGeom data
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
	
	public void geomFill() {
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
		
		//tempLINE.setGeometry(new NGeom(0, new Vector2d[]{new Vector2d (0,0)}));
	}
}
