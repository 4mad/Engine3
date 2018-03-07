// David Govorko, 03/02/2018 
package starter;
//Copy paste lwjgl set up from https://www.lwjgl.org/guide
//More documentation on GLFW http://www.glfw.org/docs/latest/window_guide.html

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
//A test bed for identifying geometries in collision data and sorting them into their respective groups
public class SortingTester {
	final Boolean debug = true;
	
	// Window & Buffer dimensions
	static final int localDimX = 650;
	static final int worldDimX = localDimX*2 +1;
	static final int worldDimY = 650;
	
	// World Builder
	public WorldBuilder realWorldBuilt = new WorldBuilder(localDimX, worldDimY);
	public WorldBuilder computerWorldBuilt = new WorldBuilder(worldDimX, worldDimY, localDimX);
	public List<List<Geom>> wG = realWorldBuilt.getWorldGeometries();
	public List<List<Geom>> cG = computerWorldBuilt.getWorldGeometries();
	
	public CollisionProcessor CollisionCollector = new CollisionProcessor(debug);
	public List<List<HashSet<Geom>>> CC = CollisionCollector.getCollisionGlobal();
	
	// GuesserThread creation
	public GuesserThreadDebug guesserThreadDebug;
	public GuesserThread guesserThread;
	
	// Concurrency Setup // http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
	public ExecutorService service =  Executors.newSingleThreadExecutor(); // https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executor.html
	// Concurrency individual Geoms
	public Future<Geom> futureCircle;// https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Future.html
	public Future<Geom> futureLineTop; 
	public Future<Geom> futureLineRight;
	public Future<Geom> futureLineBottom;
	public Future<Geom> futureLineLeft;
	// Concurrency Geom Groups
	public List <Future<Geom>> futureLines = new ArrayList<Future<Geom>>();
	public List <Future<Geom>> futureCircles = new ArrayList<Future<Geom>>();
	// Concurrency megaList
	public List<List<Future<Geom>>> fG = new ArrayList<List<Future<Geom>>>();
	// Concurrency true and false statements (Should they be wrapped up with the Futures?) 
	public boolean futureCircleLive = false;// https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Callable.html
	public boolean futureLineTopLive = false;
	public boolean futureLineRightLive = false;
	public boolean futureLineBottomLive = false;
	public boolean futureLineLeftLive = false;
	// Boolean Geom Groups
	public List <Boolean> futureBoolLine = new ArrayList<Boolean>();
	public List <Boolean> futureBoolCircle = new ArrayList<Boolean>();
	// Boolean megaList
	public List <List<Boolean>> fB = new ArrayList<List<Boolean>>();
	
	// Keyboard Input Handlers
	//? Move them inside the inputHanlding method?
	int slashKey = 0;
	int periodKey = 0;
	
	// The window handle
	private long window;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "! : SortingTester Running");

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
		if (debug)
			window = glfwCreateWindow(worldDimX, worldDimY, "DEBUG Geom Tester", NULL, NULL);
		else
			window = glfwCreateWindow(worldDimX, worldDimY, "Geom Tester", NULL, NULL);
		
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		// Put the following in a future sorting object
		futureBoolLine.add(0,futureLineTopLive);
		futureBoolLine.add(1,futureLineRightLive);
		futureBoolLine.add(2,futureLineBottomLive);
		futureBoolLine.add(3,futureLineLeftLive);
		futureBoolCircle.add(0,futureCircleLive);
		
		fB.add(0,futureBoolLine);// Replace with point Geom boolean list
		fB.add(1,futureBoolLine);
		fB.add(2, futureBoolCircle);
		
		fG.add(0,futureLines);// Replace with point Geom futures list
		fG.add(1,futureLines);
		fG.add(2,futureCircles);
		
		fG.get(1).add(0, futureLineTop);
		fG.get(1).add(1, futureLineRight);
		fG.get(1).add(2, futureLineBottom);
		fG.get(1).add(3, futureLineLeft);
		fG.get(2).add(0, futureCircle);
		
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
		
		//For Fps Counter
		double previousTime = 0;
		double currentTime = glfwGetTime();
		int frameCount = 0;
		
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //Black
				
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
				
			glfwPollEvents();
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			renderEverything();// Renders everything			
			keyInput();// should it be here or after geom Move but before concurrencyHandlingDebug()
			geomMove();// moves the Geoms
			collision();// handles collision parameters
			
			if (debug) concurrencyHandlingDebug();// Handles concurrency values
			else concurrencyHandling();
			
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			
			// Measure frames
		    currentTime = glfwGetTime();
		    frameCount++;
		    // If a second has passed.
		    if ( currentTime - previousTime >= 1.0 ){
		        // Display the frame count here any way you want.
		    	if (debug) glfwSetWindowTitle(window, "DEBUG Geom Tester FPS: " + frameCount);
		    	else glfwSetWindowTitle(window, "Geom Tester FPS: " + frameCount);
		    	
		        frameCount = 0;
		        previousTime = glfwGetTime();
		    }
			
		} //While loop
		
	}
	
	public void renderEverything(){
		glPointSize(1);//regular point size
		for (int i = 0; i < 4; i++) {// Renders Points, Lines, Circles, and Squares
			switch(i) {
				case 0: Geom throwAwayPointRenderer = new Geom();
						glPointSize(3);// Change it so its a blind render between a line/point
						for (int j = 0;j < 4; j++) {
							Iterator<Geom> iterator0 = CC.get(1).get(j).iterator();// Line Detection collision
							while (iterator0.hasNext()) {// Renders all the collision points on the "computer vision" screen
								throwAwayPointRenderer.setOffset(iterator0.next().getOffset().add(new Vector2d(localDimX, 0)));
								throwAwayPointRenderer.renderPoint();
							}
						}
						
						glColor3d(1,1,1);// white
						glPointSize(2);
						Iterator<Geom> iterator2 = CC.get(2).get(0).iterator();// Circle detection collision
						while (iterator2.hasNext()) {//Renders all the collision points on the "computer vision" screen
							throwAwayPointRenderer.setOffset(iterator2.next().getOffset().add(new Vector2d(localDimX, 0)));
							throwAwayPointRenderer.renderPoint();
						}
						break;
				case 1: for (int j = 0; j < 4; j++) {
							glColor3d(0.8,0.5,0.7);//gold
							wG.get(1).get(j).renderLine();// Side Lines
							glColor3d(1,0.6,0.4);//salmon
							cG.get(1).get(j).blindRenderGeom();// Computer Lines
						}
						break;
				case 2: glColor3d(0,0,1);//blue
						wG.get(2).get(0).renderCircle();// Circle
						glColor3d(1,0.6,0.4);//salmon
						cG.get(2).get(0).renderCircle();// Computer Circle
						break;
				case 3: glColor3d(0.9,0.5,0.9);//ugly pale purple
						wG.get(3).get(0).renderPolygon();// Square
						break;
			}
		}
		
	}
	
	private void initGL() { //generic clear screen and initiate the screen
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, worldDimX, 0, worldDimY, 1, -1);
		//GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	public void geomMove(){ //Handles changes in Geom's positions/angles
		wG.get(3).get(0).update();
	}
	
	public void collision(){ //Renders the collision point only if collision is 10 frames away from occurring.
		for (int i = 0; i < 3; i++) {
			switch(i) {
				case 0: // Point collisions, there are none atm 
						break;
				case 1: // Line Collisions
						for (int j = 0; j < 4; j++) {
							double minToLine = wG.get(1).get(j).minDistPointToLine(wG.get(3).get(0).getOffset());
							double bufferDist = wG.get(3).get(0).getGeometry().getLDA()/2 + wG.get(3).get(0).getVelocity().magnitude()*10;
							// BufferDist is square diagonal/2 + 10 Velocity steps away (Consider making this a function of distance?)
							if (Math.abs(minToLine) < bufferDist) {
								Geom lineColide = wG.get(1).get(j).collisionRegPolyVsLine(wG.get(3).get(0));// Determines closest point/side of square to line
								glColor3d(1,0.6,0.1);// ORANGE
								glPointSize(3);
								if (debug)
									lineColide.blindRenderGeom();// Debugging
								// The bellow collision check only applies to points over the line not sides (just check if both vertexes of the side are over)
								if (minToLine/wG.get(1).get(j).minDistPointToLine(lineColide.getOffset()) <= 0) {// Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
									if (debug) {
										System.out.println("Right Line Poly Collision Geometry : " + lineColide);// Debug
										lineColide = wG.get(1).get(j).collisionRegPolyVsLineRefineDebug(wG.get(3).get(0), 10);// Refined collision at 1/10 speed (10x Accuracy)
										System.out.println("Refined circle square Collision point : " + lineColide);
									} else {
										lineColide = wG.get(1).get(j).collisionRegPolyVsLineRefine(wG.get(3).get(0), 10);// Refined collision at 1/10 speed (10x Accuracy)
									}
									// Path-Finding below, needs a lot of work
									wG.get(3).get(0).setVelocity(wG.get(3).get(0).getVelocity().scalarMulti(-1)); // Basic visual response to collision
									wG.get(3).get(0).setOffset(wG.get(3).get(0).getOffset().add(wG.get(3).get(0).getVelocity()));
									wG.get(3).get(0).setDeltaAngle(wG.get(3).get(0).getDeltaAngle()*-1);

									if (debug)
										System.out.println("Right Line Poly Collision Geometry after Refinement : " + lineColide);//Debug
										
									CC.get(1).get(j).add(lineColide);
								}
							}
							
						}
						break;
				case 2: // Square Circle collisions
						double S2CDist  = wG.get(3).get(0).getOffset().dist(wG.get(2).get(0).getOffset());
						double bufferDist = wG.get(3).get(0).getGeometry().getLDA()/2 + wG.get(2).get(0).getGeometry().getLDA()/2 + wG.get(3).get(0).getVelocity().magnitude()*10;
						// BufferDist is 1/2*(Diagonal + diameter) + 10 Velocity steps away (Consider making this a function of distance?)  
						if (S2CDist < bufferDist){
							Geom circleColide = wG.get(2).get(0).collisionSquareVsCircle(wG.get(3).get(0));
							glColor3d(1,0.6,0);//orange
							glPointSize(5);
							if (debug)
								circleColide.renderPoint(); //Debugging
						
							if (circleColide.getOffset().dist(wG.get(2).get(0).getOffset()) <= wG.get(2).get(0).getGeometry().getLDA()/2) {//Detection when distance to vertex is shorter than radius
								if (debug){
									System.out.println("Circle Square Collision Point: " + circleColide);//Debug
									circleColide = wG.get(2).get(0).collisionSquareVsCircleRefineDebug(wG.get(3).get(0), 10);
									System.out.println("Refined circle square Collision point : " + circleColide);
								}// If Debug
								else 
									circleColide = wG.get(2).get(0).collisionSquareVsCircleRefine(wG.get(3).get(0), 10);
							
								wG.get(3).get(0).setVelocity(wG.get(3).get(0).getVelocity().scalarMulti(-1));// Basic visual response to collision
								wG.get(3).get(0).setOffset(wG.get(3).get(0).getOffset().add(wG.get(3).get(0).getVelocity()));
								wG.get(3).get(0).setDeltaAngle(wG.get(3).get(0).getDeltaAngle()*-1);
								
								CC.get(2).get(0).add(circleColide);// Update circleCollide data set
							}// If Collision Happened
						}// If in buffer zone 
						
				case 3: // RegPolygon collisions, none atm
						break;
			}// Switch
		}// Collision type cycle		
	}
	
	public void keyInput(){ //Handles key inputs where GLFW_PRESS = 1 and GLFW_RELEASE = 0
		if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)// Move square UP by 2x velocity magnitude
			wG.get(3).get(0).setOffset(wG.get(3).get(0).getOffset().add(new Vector2d(0,1).scalarMulti(wG.get(3).get(0).getVelocity().magnitude()*2)));
		if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)// Move square DOWN by 2x velocity magnitude
			wG.get(3).get(0).setOffset(wG.get(3).get(0).getOffset().add(new Vector2d(0,-1).scalarMulti(wG.get(3).get(0).getVelocity().magnitude()*2)));
		if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)// Move square LEFT by 2x velocity magnitude
			wG.get(3).get(0).setOffset(wG.get(3).get(0).getOffset().add(new Vector2d(-1,0).scalarMulti(wG.get(3).get(0).getVelocity().magnitude()*2)));
		if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)// Move square RIGHT by 2x velocity magnitude
			wG.get(3).get(0).setOffset(wG.get(3).get(0).getOffset().add(new Vector2d(1,0).scalarMulti(wG.get(3).get(0).getVelocity().magnitude()*2)));
		if ((glfwGetKey(window, GLFW_KEY_SLASH) == GLFW_PRESS && (CC.get(2).get(0).size() >= 4))){//Updates kasa circle guesser
			if (slashKey == 0){// This part is only activated when the button is first pressed.
				slashKey = 1;
				if (debug) {// Debug
					guesserThreadDebug = new GuesserThreadDebug("Thread: Circle", CC.get(2).get(0), wG.get(2).get(0));
					System.out.println("Circle Data passed to GuesserThreadDebug");
					fG.get(2).set(0, service.submit(guesserThreadDebug));// Maybe have a condition to check if future is in use atm?
					fB.get(2).set(0, true);
					System.out.println("Initial Future Circle status check: " + fG.get(2).get(0).isDone());
					
					System.out.println("CIRCLE THIS IS PROOF THAT THE GUESSER THREAD IS RUNNNNING ON ANOTHER THREAD AND THE MAIN THREAD DOES NOT CARE AND MOVES ON!");
				} else {// Activates the guessing thread here, data is collected in concurrency() method
					slashKey = 1;
					guesserThread = new GuesserThread(CC.get(2).get(0), 2);
					fG.get(2).set(0, service.submit(guesserThread));// Maybe have a condition to check if future is in use atm?
					fB.get(2).set(0, true);
				}
			}
			cG.get(2).get(0).renderCircle();
		} 
		//Updates least square line guesser based on which line you want (1 = top, 2 = right, 3 = bottom, 4 = left)
		for (int i = 0; i < 4; i++) { 
			if ((glfwGetKey(window, GLFW_KEY_PERIOD) == 1 && glfwGetKey(window, 49 + i) == 1 && (CC.get(1).get(i).size() >= 4))){
				if (periodKey == 0){// This part is only activated when the button is first pressed.
					periodKey = 1;
					if (debug) {// Debug
						guesserThreadDebug = new GuesserThreadDebug("Thread: Line ID: " + i + " | ", CC.get(1).get(i), wG.get(1).get(i));
						System.out.println("Line Data passed to GuesserThreadDebug");
						fG.get(1).set(i, service.submit(guesserThreadDebug));// Maybe have a condition to check if future is in use atm?
						fB.get(1).set(i, true);
						System.out.println("Initial Future Line status check: " + fG.get(1).get(i).isDone());
						
						System.out.println("LINE THIS IS PROOF THAT THE GUESSER THREAD IS RUNNNNING ON ANOTHER THREAD AND THE MAIN THREAD DOES NOT CARE AND MOVES ON!");
					} else {// Activates the guessing thread here, data is collected in concurrency() method
						guesserThread = new GuesserThread(CC.get(1).get(i), 1);
						fG.get(1).set(i, service.submit(guesserThread));// Maybe have a condition to check if future is in use atm?
						fB.get(1).set(i, true);
					}
				}
				cG.get(1).get(i).blindRenderGeom();
			} 
		}
		if (glfwGetKey(window, GLFW_KEY_LEFT_BRACKET) == 1){//Decrease Speed
			wG.get(3).get(0).setVelocity(wG.get(3).get(0).getVelocity().scalarMulti(0.95));
		}
		if (glfwGetKey(window, GLFW_KEY_RIGHT_BRACKET) == 1){//Increase Speed
			wG.get(3).get(0).setVelocity(wG.get(3).get(0).getVelocity().scalarMulti(1.05));
		}
		
		slashKey *= glfwGetKey(window, GLFW_KEY_SLASH);// After each key release slashKey = 0 so it can be ready for the next key press
		periodKey *= glfwGetKey(window, GLFW_KEY_PERIOD);
	}
	
	public void concurrencyHandlingDebug(){// Handles receiving data from callback threads.
		if (fB.get(2).get(0) && fG.get(2).get(0).isDone()){// This is to check if the futureCircle has content and is running.
			System.out.println("&&&&FutureCircle is Done?: " + fG.get(2).get(0).isDone() + " &&&&&");
			try {
				cG.get(2).set(0, fG.get(2).get(0).get()); 
				cG.get(2).get(0).setOffset(cG.get(2).get(0).getOffset().add(new Vector2d(localDimX, 0)));
			} catch (InterruptedException e) {
				System.out.println("Circle Interrupted Excpetion");
				e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println("Circle Excecution Excpetion");
				e.printStackTrace();
			}
			fB.get(2).set(0, false);
		}
		for (int i = 0; i < 4; i++) {
			if (fB.get(1).get(i) && fG.get(1).get(i).isDone()){// This is to check if the futureLine has content and is running.
				System.out.println("&&&&FutureLine is Done?: " + fG.get(1).get(i).isDone() + " &&&&&");
				try {
					cG.get(1).set(i, fG.get(1).get(i).get());
					cG.get(1).get(i).setOffset(cG.get(1).get(i).getOffset().add(new Vector2d(localDimX, 0)));
				} catch (InterruptedException e) {
					System.out.println("Line Interrupted Excpetion");
					e.printStackTrace();
				} catch (ExecutionException e) {
					System.out.println("Line Excecution Excpetion");
					e.printStackTrace();
				}
				fB.get(1).set(i, false);
			}
		}
	}
	
	public void concurrencyHandling(){// Handles receiving data from callback threads.
		if (fB.get(2).get(0) && fG.get(2).get(0).isDone()){// This is to check if the futureCircle has content and is running.
			try {
				cG.get(2).set(0, fG.get(2).get(0).get()); 
				cG.get(2).get(0).setOffset(cG.get(2).get(0).getOffset().add(new Vector2d(localDimX, 0)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			fB.get(2).set(0, false);
		}
		for (int i = 0; i < 4; i++) {
			if (fB.get(1).get(i) && fG.get(1).get(i).isDone()){// This is to check if the futureLine has content and is running.
				try {
					cG.get(1).set(i, fG.get(1).get(i).get());
					cG.get(1).get(i).setOffset(cG.get(1).get(i).getOffset().add(new Vector2d(localDimX, 0)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				fB.get(1).set(i, false);
			}
		}
	}
			
	public static void main(String[] args) {
		new SortingTester().run();
	}

}
