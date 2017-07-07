//David Govorko, 3/24/2017
package starter;
//Copy paste lwjgl set up from https://www.lwjgl.org/guide
//More documentation on GLFW http://www.glfw.org/docs/latest/window_guide.html
//LWJGL 3 switched to GLFW and now all previous lwjgl 2 (non public) versions are trash

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

//Simple way to test renderings of geometries
//Accuracy of collision is dependent on speed and distance to center of geometry being collided into.
public class RenderTester {
	//Window & Buffer dimensions
	static final int worldDimX = 1300;
	static final int worldDimY = 700;
	
	//NGeom Initialize
	public NGeom linear = new NGeom();//Line
	public NGeom linear2 = new NGeom();//Line 2
	public NGeom circular = new NGeom();//Circle
	public NGeom circular2 = new NGeom();//Circle 2
	public NGeom polygonal = new NGeom();//Polygon
	public NGeom polygonal2 = new NGeom();//Polygon 2
	public NGeom triangular = new NGeom();//Triangle
	public NGeom squarical = new NGeom();//Square
	
	
	//Geom Initialize
	public Geom POINT = new Geom();//Point NGeom is incorporated by default
	public Geom LINE = new Geom();
	public Geom LINE2 = new Geom();
	public Geom CIRCLE = new Geom();
	public Geom POLYGON = new Geom();
	public Geom POLYGON2 = new Geom();
	public Geom TRIANGLE = new Geom();
	public Geom COLLISION = new Geom();
	public Geom SQUARE = new Geom();
	public Geom CIRCLE2 = new Geom();
	
	//Regular polygon generator
	public RegPolygonGen octagon = new RegPolygonGen(8, 20);
	public RegPolygonGen nonagon = new RegPolygonGen(9, 60);
	public RegPolygonGen triangle = new RegPolygonGen(3, 150);
	public RegPolygonGen square = new RegPolygonGen(4, 75);
	
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
				
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			glPointSize(1);//regular point size
			glColor3d(1,1,1);//white
			POINT.blindRenderGeom();
			glColor3d(0.8,0.5,0.2);//gold
			LINE.blindRenderGeom();
			glColor3d(1,0.4,0.7);//hot pink
			LINE2.blindRenderGeom();
			glColor3d(0,0,1);//blue
			CIRCLE.blindRenderGeom();
			glColor3d(1,0,0);//red
			POLYGON.blindRenderGeom();
			glColor3d(1,0.6,0.4);//salmon
			TRIANGLE.blindRenderGeom();
			glColor3d(0.9,0.5,0.9);//ugly pale purple
			SQUARE.blindRenderGeom();
			glColor3d(0,1,0);//green
			CIRCLE2.blindRenderGeom();
			glColor3d(1,1,0);//Yellow
			POLYGON2.blindRenderGeom();
			/*Colors listed below for reference	
				glColor3d(1,1,1);//white
				glColor3d(0.8,0.5,0.2);//gold
				glColor3d(0,0,1);//blue
				glColor3d(1,0,0);//red
				glColor3d(1,0.6,0.4);//salmon
				glColor3d(0.9,0.5,0.9);//ugly pale purple
				glColor3d(0,1,0);//green
				glColor3d(0.9,0.5,0.9);//fuchsia
				glColor3d(0,1,1);//aqua
				glColor3d(0.4,0.4,0.6);//steel
				glColor3d(1,0.4,0.7);//hot pink
				glColor3d(1,1,0);//Yellow
				glColor3d(0,0.3,0);//Dark green
				glColor3d(0.5,0.9,0);//lime green
				glColor3d(1,0.6,0);//orange
				glColor3d(0,0.6,1);//some form of blue
			*/
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
		POINT.setOffset(new Vector2d(60,600));
		POINT.setVelocity(new Vector2d(0.5,-1.2));
		
		linear.setLDA(300);
		linear.setVert(new Vector2d[] {new Vector2d(90,10)});
		
		LINE.setGeometry(linear);
		LINE.setOffset(new Vector2d(100,200));
		LINE.setAngle(900);
		LINE.setDeltaAngle(-1);
		
		linear2.setVert(new Vector2d[] { new Vector2d(-1*(worldDimX + worldDimY), worldDimY)});
		linear2.setLDA(linear2.getVertexes()[0].magnitude());
		
		LINE2.setGeometry(linear2);
		LINE2.setOffset(new Vector2d(worldDimX,1));
		
		circular.setLDA(250);
		
		CIRCLE.setGeometry(circular);
		CIRCLE.setOffset(new Vector2d(1150,500));
		
		polygonal.setVert(octagon.getVertexes());
		polygonal.setLDA(octagon.getLongDistAcross());
			
		POLYGON.setGeometry(polygonal);
		POLYGON.setOffset(new Vector2d(100,450));
		POLYGON.setAngle(0);
		POLYGON.setDeltaAngle(0);
		POLYGON.setVelocity(new Vector2d(1,-.1));
		
		polygonal2.setVert(nonagon.getVertexes());
		polygonal2.setLDA(nonagon.getLongDistAcross());
		
		POLYGON2.setGeometry(polygonal2);
		POLYGON2.setOffset(new Vector2d(1100, 500));
		POLYGON2.setAngle(20);
		POLYGON2.setDeltaAngle(1);
		POLYGON2.setVelocity(new Vector2d(-1,-0.));
		
		
		triangular.setLDA(250);
		triangular.setVert(triangle.getVertexes());
		triangular.setLDA(triangle.getLongDistAcross());
		
		TRIANGLE.setGeometry(triangular);
		TRIANGLE.setOffset(new Vector2d(500,500));
		
		squarical.setVert(square.getVertexes());
		squarical.setLDA(square.getLongDistAcross());
		
		SQUARE.setGeometry(squarical);
		SQUARE.setOffset(new Vector2d(650,120));
		SQUARE.setVelocity(new Vector2d(1,0.1));
		SQUARE.setDeltaAngle(1);
		SQUARE.setAngle(0);
		
		circular2.setLDA(100);
		
		CIRCLE2.setGeometry(circular2);
		CIRCLE2.setOffset(new Vector2d(400,100));
	}
	
	//Handles changes in Geom's positions/angles
	public void geomMove(){
		POINT.update();
		LINE.update();
		circular.setLDA(POINT.getOffset().getX());
		POLYGON.update();
		SQUARE.update();
		POLYGON2.update();
	}
	//TODO: Save all Collision points and have them updated by the results of the collision method
	public void collision(){ //Renders the collision point only if collision is almost imminent.
		double D = SQUARE.getOffset().dist(CIRCLE2.getOffset());
		double AB =  SQUARE.getGeometry().getLDA()/2*Math.sqrt(2) + CIRCLE2.getGeometry().getLDA()/2;
		double L1 = Math.abs(LINE2.minDistPointToLine(SQUARE.getOffset()));
		double L2 =  Math.abs(LINE2.minDistPointToLine(POLYGON2.getOffset()));
		
		if (D < (AB + 150)){ 
			glColor3d(1,0.6,0);//orange
			glPointSize(5);
			CIRCLE2.blindCollision(SQUARE).blindRenderGeom();
			if (CIRCLE2.blindCollision(SQUARE).getOffset().dist(CIRCLE2.getOffset()) <= CIRCLE2.getGeometry().getLDA()/2) {//Detection when distance to vertex is shorter than radius
				System.out.println(CIRCLE2.blindCollision(SQUARE));
				SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1));
				SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
			}
		} 
		if (L1 < SQUARE.getGeometry().getLDA()/2 + 150) {
			glColor3d(1,0.6,0.1);//orange
			glPointSize(3);
			LINE2.blindCollision(SQUARE).blindRenderGeom();
			
			double centerDist = LINE2.minDistPointToLine(SQUARE.getOffset());
			//System.out.println("Min distance to center: " + centerDist);
			if (centerDist/LINE2.minDistPointToLine(LINE2.blindCollision(SQUARE).getOffset()) <= 0) {
				System.out.println(LINE2.blindCollision(SQUARE)); //Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
				SQUARE.setVelocity(SQUARE.getVelocity().scalarMulti(-1));
				SQUARE.setOffset(SQUARE.getOffset().add(SQUARE.getVelocity()));
			}
		}
		if (L2 < POLYGON2.getGeometry().getLDA()/2 + 150) {
			glColor3d(1,0,0.1);//orange
			glPointSize(5);
			LINE2.blindCollision(POLYGON2).blindRenderGeom();
			
			double centerDist = LINE2.minDistPointToLine(POLYGON2.getOffset());
			//System.out.println("Min distance to center: " + centerDist);
			if (centerDist/LINE2.minDistPointToLine(LINE2.blindCollision(POLYGON2).getOffset()) <= 0) {
				System.out.println(LINE2.blindCollision(POLYGON2)); //Detection when vertex is on the opposite side of the line that the center is on, allows for collision from both sides of line
				POLYGON2.setVelocity(POLYGON2.getVelocity().scalarMulti(-1));
				POLYGON2.setOffset(POLYGON2.getOffset().add(POLYGON2.getVelocity()));
			}
		}
	}
	
	public static void main(String[] args) {
		new RenderTester().run();
	}

}