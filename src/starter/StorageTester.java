// David Govorko, 10/03/2017
package starter;
// Copy paste lwjgl set up from https://www.lwjgl.org/guide
// More documentation on GLFW http://www.glfw.org/docs/latest/window_guide.html

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class StorageTester {// A place to test the storage function of the storage class
		final Boolean debug = true;
	
		// Window & Buffer dimensions
		static final int worldDimX = 1306;// The extra 6 pixels are buffer for the "Computer Vision" Screen
		static final int worldDimY = 700;
		static final int localDimX = 650;
		
		// NGeom Initialize
		public NGeom linear0 = new NGeom();// Line Top
		public NGeom linear1 = new NGeom();// Line Right
		public NGeom linear2 = new NGeom();// Line Bottom
		public NGeom linear3 = new NGeom();// Line Left
		public NGeom circular = new NGeom();// Circle (Immobile)
		public NGeom squarical = new NGeom();// Square (Moves)
		
		// Geom Initialize
		public Geom COLLISIONPOINT = new Geom();// Point used to render circle collisions
		public Geom LINETOP = new Geom();
		public Geom LINERIGHT = new Geom();
		public Geom LINEBOTTOM = new Geom();
		public Geom LINELEFT = new Geom();
		public Geom CIRCLE = new Geom();
		public Geom SQUARE = new Geom();
		public Geom tempCIRCLE = new Geom();// The temps are for the geometries drawn after the geometries are guessed
		public Geom tempLINE = new Geom();
		
		// Regular polygon generator
		public RegPolygonGen square = new RegPolygonGen(4, 50);
		
		// Collision detection HashSets
		HashSet<Geom> circleCollide = new HashSet<Geom>();// Contains points of circle collision
		HashSet<Geom> lineRightCollide = new HashSet<Geom>();// Contains points of RightLine collision
		
		// GuesserThread creation
		GuesserThreadDebug guesserThreadDebug;
		GuesserThread guesserThread;
		
		// Concurrency Setup -- See this section in GeomGuesser for more detailed info
		ExecutorService service =  Executors.newSingleThreadExecutor();
		Future<Geom> futureLine; 
		Future<Geom> futureCircle;
		boolean futureLineRightLive = false;// These trigger true immediately after a key is pressed
		boolean futureCircleLive = false;
		
		// Keyboard Input Handlers
		int slashKey = 0;
		int periodKey = 0;
		
		// The window handle
		private long window;
		
		public void run(){
			System.out.println("Hello LWJGL " + Version.getVersion() + "!");

			init();// Initialize GLFW
			loop();// Where all the actual code goes
			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);

			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
		
		private void init(){
			GLFWErrorCallback.createPrint(System.err).set();

			// Initialize GLFW. Most GLFW functions will not work before doing this.
			if ( !glfwInit() )
				throw new IllegalStateException("Unable to initialize GLFW");

			// Configure GLFW
			glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
			glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will NOT be resizable
			glfwWindowHint(GLFW_REFRESH_RATE, 60);// Ignored during windowed mode
			
			// Create the window only change stuff in windowHints
			if (debug)
				window = glfwCreateWindow(worldDimX, worldDimY, "DEBUG Geom Tester", NULL, NULL);
			else
				window = glfwCreateWindow(worldDimX, worldDimY, "Geom Tester", NULL, NULL);
			
			if ( window == NULL )
				throw new RuntimeException("Failed to create the GLFW window");

			// Get the thread stack and push a new frame
			try ( MemoryStack stack = stackPush() ){
				IntBuffer pWidth = stack.mallocInt(1);
				IntBuffer pHeight = stack.mallocInt(1);

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
			glfwSwapInterval(1);// 0 = max, 1 = 60, 2 = 30, 3 = 20, ....

			// Make the window visible
			glfwShowWindow(window);
		}
		
		private void loop(){
			GL.createCapabilities();
			
			initGL();// Rendering screen creation
			geomInit();// Populates the Geoms and NGeoms
			
			// For Fps Counter
			double previousTime = 0;
			double currentTime = glfwGetTime();
			int frameCount = 0;
			
			// Set the clear color
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black
					
			// Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key.
			while ( !glfwWindowShouldClose(window) ){
					
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer
				glPointSize(1);// Regular point size
				glColor3d(0.8,0.5,0.7);// Gold
				LINETOP.renderLine();
				LINERIGHT.renderLine();
				LINEBOTTOM.renderLine();
				LINELEFT.renderLine();
				glColor3d(0,0,1);// Blue
				CIRCLE.renderCircle();
				glColor3d(1,0.6,0.4);// Salmon
				tempCIRCLE.renderCircle();
				tempLINE.renderLine();
				glColor3d(0.9,0.5,0.9);// Ugly pale purple
				SQUARE.renderPolygon();
				
				glColor3d(1,1,1);// White
				glPointSize(2);
				Iterator<Geom> iterator0 = circleCollide.iterator();// Circle detection collision
				while (iterator0.hasNext()) {// Renders all the collision points on the "computer vision" screen
					COLLISIONPOINT.setOffset(iterator0.next().getOffset().add(new Vector2d(localDimX, 0)));
					COLLISIONPOINT.renderPoint();
				}
				
				glPointSize(3);
				Iterator<Geom> iterator1 = lineRightCollide.iterator();// Line Detection collision
				while (iterator1.hasNext()) {//Renders all the collision points on the "computer vision" screen
					COLLISIONPOINT.setOffset(iterator1.next().getOffset().add(new Vector2d(localDimX, 0)));
					COLLISIONPOINT.renderPoint();
				}
				
				keyInput();// Should it be here or after geom Move but before concurrencyHandlingDebug()
				geomMove();// Moves the Geoms
				collision();// Handles collision parameters
				if (debug) concurrencyHandlingDebug();// Handles concurrency values
				else concurrencyHandling();
				
				glfwSwapBuffers(window);// Swap the color buffers

				// Poll for window events. The key callback above will only be invoked during this call.
				glfwPollEvents();
				
				// Measure frames
			    currentTime = glfwGetTime();
			    frameCount++;
			    // If a second has passed.
			    if ( currentTime - previousTime >= 1.0 )
			    {
			        // Display the frame count here any way you want.
			    	if (debug) glfwSetWindowTitle(window, "DEBUG Geom Tester FPS: " + frameCount);
			    	else glfwSetWindowTitle(window, "Geom Tester FPS: " + frameCount);
			    	
			        frameCount = 0;
			        previousTime = glfwGetTime();
			    }
				
			}// While loop
			
		}
		
		private void initGL(){// Generic clear screen and initiate the screen
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, worldDimX, 0, worldDimY, 1, -1);
			// GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}
		
		public void geomInit(){
			linear0.setVert(new Vector2d[] {new Vector2d(1,0)});
			linear0.setLDA(-1*localDimX/linear0.getVertexes()[0].magnitude());
			
			LINETOP.setGeometry(linear0);
			LINETOP.setOffset(new Vector2d(0,worldDimY-1));// Top Wall
			
			linear1.setVert(new Vector2d[] {new Vector2d(0,1)});
			linear1.setLDA(-1*worldDimY/linear1.getVertexes()[0].magnitude());
			
			LINERIGHT.setGeometry(linear1);
			LINERIGHT.setOffset(new Vector2d(localDimX,0));// Right Wall
			
			linear2.setVert(new Vector2d[] {new Vector2d(1,0)});
			linear2.setLDA(-1*localDimX/linear2.getVertexes()[0].magnitude());
			
			LINEBOTTOM.setGeometry(linear2);
			LINEBOTTOM.setOffset(new Vector2d(0,0));// Bottom Wall
			
			linear3.setVert(new Vector2d[] {new Vector2d(0,1)});
			linear3.setLDA(-1*worldDimY/linear0.getVertexes()[0].magnitude());
			
			LINELEFT.setGeometry(linear3);
			LINELEFT.setOffset(new Vector2d(1,0));// Left Wall

			circular.setLDA(250);
			
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
		}
		
		public void geomMove(){// Handles changes in Geom's positions/angles
			SQUARE.update();
		}
		
		public void collision(){// Renders the collision point only if collision is 10 frames away from occurring.
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
				if (debug)
					circleColide.renderPoint(); //Debugging
				
				if (circleColide.getOffset().dist(CIRCLE.getOffset()) <= CIRCLE.getGeometry().getLDA()/2) {// Detection when distance to vertex is shorter than radius
					if (debug){
						System.out.println("Circle Square Collision Point: " + circleColide);//Debug
						circleColide = CIRCLE.collisionSquareVsCircleRefineDebug(SQUARE, 10);
						System.out.println("Refined circle square Collision point : " + circleColide);
					}
					else 
						circleColide = CIRCLE.collisionSquareVsCircleRefine(SQUARE, 10);
					
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
				if (debug)
					lineColide.blindRenderGeom();// Debug
				
				if (L0/LINETOP.minDistPointToLine(lineColide.getOffset()) <= 0) {// Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
					if (debug)
						System.out.println("Top Line Poly Collision Geometry : " + lineColide);//Debug
					
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1)); // Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
				}
			} 
			if (Math.abs(L1) < SQUARE.getGeometry().getLDA()/2 + SQUARE.getVelocity().magnitude()*10) {
				Geom lineColide = LINERIGHT.collisionRegPolyVsLine(SQUARE);
				glColor3d(1,0.6,0.1);//orange
				glPointSize(3);
				if (debug)	
					lineColide.blindRenderGeom();// Debugging
				
				if (L1/LINERIGHT.minDistPointToLine(lineColide.getOffset()) <= 0) {// Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
					if (debug)
						System.out.println("Right Line Poly Collision Geometry : " + lineColide);// Debug
					
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1));// Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
					lineColide.getOffset().trim(1);
					if (debug)
						System.out.println("Right Line Poly Collision Geometry after Trimming : " + lineColide);// Debug
						
					lineRightCollide.add(lineColide);
				}
			}
			if (Math.abs(L2) < SQUARE.getGeometry().getLDA()/2 + SQUARE.getVelocity().magnitude()*10) {
				Geom lineColide = LINEBOTTOM.collisionRegPolyVsLine(SQUARE);
				glColor3d(1,0.6,0.1);// Orange
				glPointSize(3);
				if (debug)	
					lineColide.blindRenderGeom();// Debugging
				
				if (L2/LINEBOTTOM.minDistPointToLine(lineColide.getOffset()) <= 0) {// Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
					if (debug)
						System.out.println("Bottom Line Poly Collision Geometry : " + lineColide);// Debug
					
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1));// Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
				}
			}
			if (Math.abs(L3) < SQUARE.getGeometry().getLDA()/2 + SQUARE.getVelocity().magnitude()*10) {
				Geom lineColide = LINELEFT.collisionRegPolyVsLine(SQUARE);
				glColor3d(1,0.6,0.1);// Orange
				glPointSize(3);
				if (debug)
					lineColide.blindRenderGeom();// Debugging
				
				if (L3/LINELEFT.minDistPointToLine(lineColide.getOffset()) <= 0) {// Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
					if (debug)
						System.out.println("Left Line Poly Collision Geometry : " + lineColide);// Debug
					
					SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1));// Basic visual response to collision
					SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
					SQUARE.setDeltaAngle(SQUARE.getDeltaAngle()*-1);
				}
			}			
			
		}
		
		public void keyInput(){// Handles key inputs where GLFW_PRESS = 1 and GLFW_RELEASE = 0
			if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS){
				SQUARE.setOffset(SQUARE.getOffset().add(new Vector2d(0,1).scalarMulti(SQUARE.getVelocity().magnitude()*2)));
			}
			if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS){
				SQUARE.setOffset(SQUARE.getOffset().add(new Vector2d(0,-1).scalarMulti(SQUARE.getVelocity().magnitude()*2)));
			}
			if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS){
				SQUARE.setOffset(SQUARE.getOffset().add(new Vector2d(-1,0).scalarMulti(SQUARE.getVelocity().magnitude()*2)));
			}
			if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS){
				SQUARE.setOffset(SQUARE.getOffset().add(new Vector2d(1,0).scalarMulti(SQUARE.getVelocity().magnitude()*2)));
			}
			if ((glfwGetKey(window, GLFW_KEY_SLASH) == GLFW_PRESS && (circleCollide.size() >= 4))){//Updates kasa circle guesser
				if (slashKey == 0){// This part is only activated when the button is first pressed.
					slashKey = 1;
					if (debug) {// Debug
						guesserThreadDebug = new GuesserThreadDebug("Thread: Circle", circleCollide, CIRCLE, 2);
						System.out.println("Circle Data passed to GusserThreadDebug");
						futureCircle = service.submit(guesserThreadDebug);// Maybe have a condition to check if future is in use atm?
						futureCircleLive = true;
						System.out.println("Initial Future Circle status check: " + futureCircle.isDone());
						
						System.out.println("CIRCLE THIS IS PROOF THAT THE GUESSER THREAD IS RUNNNNING ON ANOTHER THREAD AND THE MAIN THREAD DOES NOT CARE AND MOVES ON!");
					} else {// Activates the guessing thread here, data is collected in concurrency() method
						slashKey = 1;
						guesserThread = new GuesserThread(circleCollide, 2);
						futureCircle = service.submit(guesserThread);// Maybe have a condition to check if future is in use atm?
						futureCircleLive = true;
					}
				}
				tempCIRCLE.renderCircle();
			} 
			if ((glfwGetKey(window, GLFW_KEY_PERIOD) == 1 && (lineRightCollide.size() >= 4))){// Updates least square line guesser
				if (periodKey == 0){// This part is only activated when the button is first pressed.
					periodKey = 1;
					if (debug) {// Debug
						guesserThreadDebug = new GuesserThreadDebug("Thread: Line", lineRightCollide, LINERIGHT, 1);
						System.out.println("Line Data passed to GusserThreadDebug");
						futureLine = service.submit(guesserThreadDebug);// Maybe have a condition to check if future is in use atm?
						futureLineRightLive = true;
						System.out.println(futureLine.isDone());
						
						System.out.println("LINE THIS IS PROOF THAT THE GUESSER THREAD IS RUNNNNING ON ANOTHER THREAD AND THE MAIN THREAD DOES NOT CARE AND MOVES ON!");
					} else {// Activates the guessing thread here, data is collected in concurrency() method
						guesserThread = new GuesserThread(lineRightCollide, 1);
						futureLine = service.submit(guesserThread);// Maybe have a condition to check if future is in use atm?
						futureLineRightLive = true;
					}
				}
				tempLINE.renderLine();
			} 
			if (glfwGetKey(window, GLFW_KEY_LEFT_BRACKET) == 1){// Decrease Speed
				SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(0.95));
			}
			if (glfwGetKey(window, GLFW_KEY_RIGHT_BRACKET) == 1){// Increase Speed
				SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(1.05));
			}
			
			slashKey *= glfwGetKey(window, GLFW_KEY_SLASH);// After each key release slashKey = 0 so it can be ready for the next key press
			periodKey *= glfwGetKey(window, GLFW_KEY_PERIOD);
		}
		
		public void concurrencyHandlingDebug(){// Handles receiving data from callback threads.
			if (futureCircleLive && futureCircle.isDone()){// This is to check if the futureCircle has content and is running.
				System.out.println(futureCircle.isDone());
				try {
					tempCIRCLE = futureCircle.get();
					tempCIRCLE.setOffset(tempCIRCLE.getOffset().add(new Vector2d(localDimX, 0)));
				} catch (InterruptedException e) {
					System.out.println("Circle Interrupted Excpetion");
					e.printStackTrace();
				} catch (ExecutionException e) {
					System.out.println("Circle Excecution Excpetion");
					e.printStackTrace();
				}
				futureCircleLive = false;
			}
			if (futureLineRightLive && futureLine.isDone()){// This is to check if the futureLine has content and is running.
				System.out.println(futureLine.isDone());
				try {
					tempLINE = futureLine.get();
					tempLINE.setOffset(tempLINE.getOffset().add(new Vector2d(localDimX, 0)));
				} catch (InterruptedException e) {
					System.out.println("Line Interrupted Excpetion");
					e.printStackTrace();
				} catch (ExecutionException e) {
					System.out.println("Line Excecution Excpetion");
					e.printStackTrace();
				}
				futureLineRightLive = false;
			}
		}
		
		public void concurrencyHandling(){// Handles receiving data from callback threads.
			if (futureCircleLive && futureCircle.isDone()){// This is to check if the futureCircle has content and is running.
				try {
					tempCIRCLE = futureCircle.get();
					tempCIRCLE.setOffset(tempCIRCLE.getOffset().add(new Vector2d(localDimX, 0)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				futureCircleLive = false;
			}
			if (futureLineRightLive && futureLine.isDone()){// This is to check if the futureLine has content and is running.
				try {
					tempLINE = futureLine.get();
					tempLINE.setOffset(tempLINE.getOffset().add(new Vector2d(localDimX, 0)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				futureLineRightLive = false;
			}
		}
				
		public static void main(String[] args){
			new StorageTester().run();
		}
	
}