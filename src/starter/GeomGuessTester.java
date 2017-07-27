//David Govorko, 7/15/2017
package starter;
//Copy paste lwjgl set up from https://www.lwjgl.org/guide
//More documentation on GLFW http://www.glfw.org/docs/latest/window_guide.html


import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.HashSet;
import java.util.Iterator;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GeomGuessTester {//A way to test the geometry guessing software and collisions with the guessed geometry.
		//Window & Buffer dimensions
		static final int worldDimX = 1301;
		static final int worldDimY = 700;
		static final int localDimX = 650;
		
		//NGeom Initialize
		public NGeom linear0 = new NGeom();//Line
		public NGeom linear1 = new NGeom();//Line
		public NGeom linear2 = new NGeom();//Line
		public NGeom linear3 = new NGeom();//Line
		public NGeom circular = new NGeom();//Circle
		//public NGeom polygonal = new NGeom();//Polygon
		//public NGeom triangular = new NGeom();//Triangle
		public NGeom squarical = new NGeom();//Square
		
		//Geom Initialize
		public Geom CIRCLECOLLISIONPOINT = new Geom();//Point used to render circle collisions
		public Geom LINECOLLISINOPOINT = new Geom();
		public Geom LINETOP = new Geom();
		public Geom LINERIGHT = new Geom();
		public Geom LINEBOTTOM = new Geom();
		public Geom LINELEFT = new Geom();
		public Geom CIRCLE = new Geom();
		//public Geom POLYGON = new Geom();
		//public Geom TRIANGLE = new Geom();
		public Geom SQUARE = new Geom();
		public Geom tempCIRCLE = new Geom();
		public Geom tempLINE = new Geom();
		
		//Regular polygon generator
		//public RegPolygonGen octagon = new RegPolygonGen(8, 20);
		//public RegPolygonGen triangle = new RegPolygonGen(3, 150);
		public RegPolygonGen square = new RegPolygonGen(4, 50);
		
		//Collision detection set
		HashSet<Geom> circleCollide = new HashSet<Geom>();//Contains points of circle collision
		HashSet<Geom> lineCollide = new HashSet<Geom>();//Contains points of line collision
		
		// The window handle
		private long window;
		
		public void run() {
			System.out.println("Hello LWJGL " + Version.getVersion() + "!");

			init();
			loop();
			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);

			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
		
		private void init() {
			// Setup an error callback. The default implementation
			// will print the error message in System.err.
			GLFWErrorCallback.createPrint(System.err).set();

			// Initialize GLFW. Most GLFW functions will not work before doing this.
			if ( !glfwInit() )
				throw new IllegalStateException("Unable to initialize GLFW");

			// Configure GLFW
			//glfwDefaultWindowHints(); // optional, the current window hints are already the default
			glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
			glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will NOT be resizable
			glfwWindowHint(GLFW_REFRESH_RATE, 60);// Ignored during windowed mode
			
			// Create the window only change stuff in windowHints
			window = glfwCreateWindow(worldDimX, worldDimY, "Render Tester", NULL, NULL);
			if ( window == NULL )
				throw new RuntimeException("Failed to create the GLFW window");

			// Get the thread stack and push a new frame
			try ( MemoryStack stack = stackPush() ) {
				IntBuffer pWidth = stack.mallocInt(1); // int*
				IntBuffer pHeight = stack.mallocInt(1); // int*

				// Get the window size passed to glfwCreateWindow
				glfwGetWindowSize(window, pWidth, pHeight);

				// Get the resolution of the primary monitor
				GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
				
				// Center the window on monitor screen
				glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
				);

			} // the stack frame is popped automatically

			// Make the OpenGL context current
			glfwMakeContextCurrent(window);
			// Enable v-sync
			glfwSwapInterval(1);//1 = 60, 2 = 30, 3 = 20, ....

			// Make the window visible
			glfwShowWindow(window);
			
		}
		
		private void loop() {
			// This line is critical for LWJGL's interpolation with GLFW's
			// OpenGL context, or any context that is managed externally.
			// LWJGL detects the context that is current in the current thread,
			// creates the GLCapabilities instance and makes the OpenGL
			// bindings available for use.
			GL.createCapabilities();
			
			initGL();//Rendering screen creation
			geomInit();//populates the Geoms and NGeoms
			
			// Set the clear color
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //Black
					
			// Run the rendering loop until the user has attempted to close
			// the window or has pressed the ESCAPE key.
			while ( !glfwWindowShouldClose(window) ) {
					
				glfwPollEvents();
				
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
				glPointSize(1);//regular point size
				glColor3d(0.8,0.5,0.7);//gold
				LINETOP.renderLine();
				LINERIGHT.renderLine();
				LINEBOTTOM.renderLine();
				LINELEFT.renderLine();
				glColor3d(0,0,1);//blue
				CIRCLE.renderCircle();
				//glColor3d(1,0,0);//red
				//POLYGON.renderPolygon();
				//glColor3d(1,0.6,0.4);//salmon
				//TRIANGLE.renderPolygon();
				glColor3d(1,0.6,0.4);//salmon
				tempCIRCLE.renderCircle();
				tempLINE.renderLine();
				glColor3d(0.9,0.5,0.9);//ugly pale purple
				SQUARE.renderPolygon();
				
				glColor3d(1,1,1);//white
				glPointSize(2);
				Iterator<Geom> iterator0 = circleCollide.iterator();//Circle detection collision
				while (iterator0.hasNext()) {//Renders all the collision points on the "computer vision" screen
					CIRCLECOLLISIONPOINT.setOffset(iterator0.next().getOffset().add(new Vector2d(localDimX, 0)));
					CIRCLECOLLISIONPOINT.renderPoint();
				}
				
				glPointSize(3);
				Iterator<Geom> iterator1 = lineCollide.iterator();//Line Detection collision
				while (iterator1.hasNext()) {//Renders all the collision points on the "computer vision" screen
					CIRCLECOLLISIONPOINT.setOffset(iterator1.next().getOffset().add(new Vector2d(localDimX, 0)));
					CIRCLECOLLISIONPOINT.renderPoint();
				}
				
				keyInput();//should it be here or after geom Move.
				geomMove();//moves the Geoms
				collision();
				
				glfwSwapBuffers(window); // swap the color buffers

				// Poll for window events. The key callback above will only be
				// invoked during this call.
				glfwPollEvents();
			} //While loop
			
		}
		
		private void initGL() { //generic clear screen and initiate the screen
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, worldDimX, 0, worldDimY, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}
		
		public void geomInit(){
			linear0.setVert(new Vector2d[] {new Vector2d(1,0)});
			linear0.setLDA(-1*localDimX/linear0.getVertexes()[0].magnitude());
			
			LINETOP.setGeometry(linear0);
			LINETOP.setOffset(new Vector2d(0,worldDimY-1));//Top Wall
			
			linear1.setVert(new Vector2d[] {new Vector2d(0,1)});
			linear1.setLDA(-1*worldDimY/linear1.getVertexes()[0].magnitude());
			
			LINERIGHT.setGeometry(linear1);
			LINERIGHT.setOffset(new Vector2d(localDimX-50,0));//Right Wall
			
			linear2.setVert(new Vector2d[] {new Vector2d(1,0)});
			linear2.setLDA(-1*localDimX/linear2.getVertexes()[0].magnitude());
			
			LINEBOTTOM.setGeometry(linear2);
			LINEBOTTOM.setOffset(new Vector2d(0,0));//Bottom Wall
			
			linear3.setVert(new Vector2d[] {new Vector2d(0,1)});
			linear3.setLDA(-1*worldDimY/linear0.getVertexes()[0].magnitude());
			
			LINELEFT.setGeometry(linear3);
			LINELEFT.setOffset(new Vector2d(1,0));//Left Wall

			circular.setLDA(200);
			
			CIRCLE.setGeometry(circular);
			CIRCLE.setOffset(new Vector2d(300,450));

			squarical.setVert(square.getVertexes());
			squarical.setLDA(square.getLongDistAcross());
			
			SQUARE.setGeometry(squarical);
			SQUARE.setOffset(new Vector2d(500,420));
			SQUARE.setVelocity(new Vector2d(-1,0.1));
			SQUARE.setDeltaAngle(1);
			SQUARE.setAngle(0);
			
			tempLINE.setGeometry(new NGeom(0, new Vector2d[]{new Vector2d (0,0)}));
			/*//Additional stuff for fun?
			polygonal.setVert(octagon.getVertexes());
			polygonal.setLDA(octagon.getLongDistAcross());
			
			POLYGON.setGeometry(polygonal);
			POLYGON.setOffset(new Vector2d(100,450));
			POLYGON.setAngle(0);
			POLYGON.setDeltaAngle(0);
			POLYGON.setVelocity(new Vector2d(1,-.1));
			
			triangular.setLDA(250);
			triangular.setVert(triangle.getVertexes());
			triangular.setLDA(triangle.getLongDistAcross());
			
			TRIANGLE.setGeometry(triangular);W
			TRIANGLE.setOffset(new Vector2d(500,500));
			*/
		}
		
		//Handles changes in Geom's positions/angles
		public void geomMove(){
			SQUARE.update();
		}
		
		public void collision(){ //Renders the collision point only if collision is 10 frames away from occuring.
			double D = SQUARE.getOffset().dist(CIRCLE.getOffset());
			double AB = SQUARE.getGeometry().getLDA()/2*Math.sqrt(2) + CIRCLE.getGeometry().getLDA()/2;
			double L0 = LINETOP.minDistPointToLine(SQUARE.getOffset());
			double L1 = LINERIGHT.minDistPointToLine(SQUARE.getOffset());
			double L2 = LINEBOTTOM.minDistPointToLine(SQUARE.getOffset());
			double L3 = LINELEFT.minDistPointToLine(SQUARE.getOffset());
			
			if (D < (AB + SQUARE.getVelocity().magnitude()*10)){
				Geom circleColide = CIRCLE.collisionSquareVsCircle(SQUARE);
				glColor3d(1,0.6,0);//orange
				glPointSize(5);
				circleColide.renderPoint(); //Debugging
				
				if (circleColide.getOffset().dist(CIRCLE.getOffset()) <= CIRCLE.getGeometry().getLDA()/2) {//Detection when distance to vertex is shorter than radius
					//System.out.println(CIRCLE.collisionSquareVsCircle(SQUARE));
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1)); // Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
					circleCollide.add(circleColide);
				}
			} 
			if (Math.abs(L0) < SQUARE.getGeometry().getLDA()/2 + SQUARE.getVelocity().magnitude()*10) {
				Geom lineColide = LINETOP.collisionRegPolyVsLine(SQUARE);
				glColor3d(1,0.6,0.1);//orange
				glPointSize(3);
				lineColide.blindRenderGeom(); //Debugging
				
				//System.out.println("Min distance to center: " + centerDist);
				if (L0/LINETOP.minDistPointToLine(lineColide.getOffset()) <= 0) {//Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
					//System.out.println(lineColide); 
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1)); // Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
				}
			} 
			if (Math.abs(L1) < SQUARE.getGeometry().getLDA()/2 + SQUARE.getVelocity().magnitude()*10) {
				Geom lineColide = LINERIGHT.collisionRegPolyVsLine(SQUARE);
				glColor3d(1,0.6,0.1);//orange
				glPointSize(3);
				lineColide.blindRenderGeom(); //Debugging
				
				if (L1/LINERIGHT.minDistPointToLine(lineColide.getOffset()) <= 0) {//Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
					
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1)); // Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
					lineColide.getOffset().trim();
					System.out.println(lineColide); 
					lineCollide.add(lineColide);
				}
			}
			if (Math.abs(L2) < SQUARE.getGeometry().getLDA()/2 + SQUARE.getVelocity().magnitude()*10) {
				Geom lineColide = LINEBOTTOM.collisionRegPolyVsLine(SQUARE);
				glColor3d(1,0.6,0.1);//orange
				glPointSize(3);
				lineColide.blindRenderGeom(); //Debugging
				
				if (L2/LINEBOTTOM.minDistPointToLine(lineColide.getOffset()) <= 0) {//Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
					//System.out.println(lineColide); 
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1)); // Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
				}
			}
			if (Math.abs(L3) < SQUARE.getGeometry().getLDA()/2 + SQUARE.getVelocity().magnitude()*10) {
				Geom lineColide = LINELEFT.collisionRegPolyVsLine(SQUARE);
				glColor3d(1,0.6,0.1);//orange
				glPointSize(3);
				lineColide.blindRenderGeom(); //Debugging
				
				if (L3/LINELEFT.minDistPointToLine(lineColide.getOffset()) <= 0) {//Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
					//System.out.println(lineColide); 
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1)); // Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
				}
			}			
			
		}
		
		public void keyInput(){//Handles key inputs
			if (glfwGetKey(window, GLFW_KEY_UP) == 1){
				SQUARE.setOffset(SQUARE.getOffset().add(new Vector2d(0,1).scalarMulti(SQUARE.getVelocity().magnitude()*2)));
			}
			if (glfwGetKey(window, GLFW_KEY_DOWN) == 1){
				SQUARE.setOffset(SQUARE.getOffset().add(new Vector2d(0,-1).scalarMulti(SQUARE.getVelocity().magnitude()*2)));
			}
			if (glfwGetKey(window, GLFW_KEY_LEFT) == 1){
				SQUARE.setOffset(SQUARE.getOffset().add(new Vector2d(-1,0).scalarMulti(SQUARE.getVelocity().magnitude()*2)));
			}
			if (glfwGetKey(window, GLFW_KEY_RIGHT) == 1){
				SQUARE.setOffset(SQUARE.getOffset().add(new Vector2d(1,0).scalarMulti(SQUARE.getVelocity().magnitude()*2)));
			}
			if ((glfwGetKey(window, GLFW_KEY_SLASH) == 1 && (circleCollide.size() >= 4))){//Updates kasa circle guesser
				tempCIRCLE = kasaCircleGuess(circleCollide);
				tempCIRCLE.renderCircle();
				tempCIRCLE.setOffset(tempCIRCLE.getOffset().add(new Vector2d(localDimX, 0)));
			} 
			if ((glfwGetKey(window, GLFW_KEY_PERIOD) == 1 && (circleCollide.size() >= 4))){//Updates least square line guesser
				tempLINE = leastSquareLineGuess(lineCollide);
				tempLINE.renderLine();
				tempLINE.setOffset(tempLINE.getOffset().add(new Vector2d(localDimX, 0)));
			} 
			if (glfwGetKey(window, GLFW_KEY_LEFT_BRACKET) == 1){//Decrease Speed
				SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(0.95));
			}
			if (glfwGetKey(window, GLFW_KEY_RIGHT_BRACKET) == 1){//Increase Speed
				SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(1.05));
			}
		}
		
		public Geom kasaCircleGuess(HashSet<Geom> circleCollisionData) {//Guesses a circle's center and radius based on collision data
			double aM,bM,rK = 0,A,B,C,D,E;//http://people.cas.uab.edu/~mosya/cl/CircleFitByKasa.cpp
			double sumX2 = 0,sumX = 0, sumY2 = 0,sumY = 0,sumXY = 0,sumXY2 = 0,sumX3 = 0,sumX2Y = 0,sumY3 = 0;
			int iterate = circleCollisionData.size();
			Geom kasaCircle = new Geom();
			Vector2d temp; //Iterating vector
			
			Iterator<Geom> iterator0 = circleCollisionData.iterator();
			while (iterator0.hasNext()) {
				temp = iterator0.next().getOffset();
				//System.out.println(temp); //Debug
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
			
			/*
			System.out.println("A    : " + A);//Debug only
			System.out.println("B    : " + B);//Debug only
			System.out.println("C    : " + C);//Debug only
			System.out.println("D    : " + D);//Debug only
			System.out.println("E    : " + E);//Debug only
			*/

			aM = (D*C-B*E)/(A*C-B*B);
			bM = (A*E-B*D)/(A*C-B*B);

			Iterator<Geom> iterator1 = circleCollisionData.iterator();
			while (iterator1.hasNext()) {
				temp = iterator1.next().getOffset();
				rK += (Math.pow(temp.getX() - aM, 2) + Math.pow(temp.getY() - bM, 2))/iterate;
			}
			rK = Math.sqrt(rK);
			System.out.println(2*rK + "   should be  " + CIRCLE.getGeometry().getLDA());//Debug only
			System.out.println(aM + "   should be  " + CIRCLE.getOffset().getX());//Debug only
			System.out.println(bM + "   should be  " + CIRCLE.getOffset().getY());//Debug only

			kasaCircle.getGeometry().setLDA(2*rK);//Circle Lda = diameter so 2 * radius
			kasaCircle.setOffset(new Vector2d(aM,bM));

			// input  apache math library for covariance and see if it is faster.
			return kasaCircle;
		}
		
		public Geom leastSquareLineGuess(HashSet<Geom> lineCollisionData){
			double mX = 0,mY = 0,sumX2 = 0,sumXY = 0;//hotmath.com/hotmath_help/topics/line-of-best-fit.html
			Geom lsLine = new Geom();
			Vector2d temp; //Iterating vector
			
			Iterator<Geom> iterator0 = lineCollisionData.iterator();
			while (iterator0.hasNext()) {
				temp = iterator0.next().getOffset();
				mX += temp.getX();
				mY += temp.getY();
			}

			mX = mX/lineCollisionData.size();//Averages of x and y
			mY = mY/lineCollisionData.size();
						
			Iterator<Geom> iterator1 = lineCollisionData.iterator();
			while (iterator1.hasNext()) {
				lsLine.setOffset(iterator1.next().getOffset());
				sumX2 +=  Math.pow(lsLine.getOffset().getX() - mX, 2);
				sumXY += (lsLine.getOffset().getX() - mX)*(lsLine.getOffset().getY() - mY);
			}
			
			temp = new Vector2d(sumX2, sumXY);
			
			lsLine.setGeometry(new NGeom(-100*temp.magnitude(), new Vector2d[] {temp}));
			
			if (sumX2 == 0)//This in case the actual line is vertical so directonially x = 0 but y = almost infinity
				lsLine.setGeometry(new NGeom(-123456780, new Vector2d[] {new Vector2d(0.0,1.0)}));
			
			System.out.println(lsLine);
			
			return lsLine;
		}
		
		public static void main(String[] args) {
			new GeomGuessTester().run();
		}
		
}
