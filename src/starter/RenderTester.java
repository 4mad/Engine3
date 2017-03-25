//David Govorko, 3/24/2017
package starter;
//copy paste lwjgl set up from https://www.lwjgl.org/guide
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
public class RenderTester {
	//Window & Buffer dimensions
	static final int worldDimX = 1336;
	static final int worldDimY = 768;	
	
	//Geometry
	public Vector2d centerC = new Vector2d(400,300);
	double radius = 100;
	public Vector2d cornerR = new Vector2d(800,600);
	public Vector2d sizeR = new Vector2d(100,50);
	public Vector2d[] vertexesP = new Vector2d[5];
	public Vector2d[] vertexesPO = new Vector2d[5];
	public Vector2d offsetPO = new Vector2d(900,100);
	public Vector2d offsetPA = new Vector2d(1100,100);
	public double angle = 0;
	public Vector2d lineS = new Vector2d(500,100);
	public Vector2d lineE = new Vector2d(1,99);
	public Vector2d point = new Vector2d(5,5);
	public Vector2d[] points = new Vector2d[7];
	public Vector2d offsetP = new Vector2d(800,300);
	
	
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
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		initGL();//Rendering screen creation
		vertexGen();//populates polygon vertex data
		//renderGeom object
		RenderGeom A = new RenderGeom();
		
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //Black
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			//circles
			glColor3d(0.8,0.5,0.2);//gold
			A.circle(centerC, radius);
			glColor3d(1,1,1);//white
			A.circle(centerC.getX(), centerC.getY(), radius/2);
			//rectangles
			glColor3d(0,0,1);//blue
			A.rectangleCorner(cornerR.add(sizeR), sizeR);
			glColor3d(1,0,0);//red
			A.rectangleCenter(cornerR, sizeR.scalarMulti(0.5));
			glColor3d(1,0.6,0.4);//salmon
			A.rectangleCenter(cornerR, sizeR.scalarMulti(0.5), angle);
			glColor3d(0.9,0.5,0.9);//ugly pale purple
			A.rectangleCorner(cornerR.subtract(sizeR), sizeR, angle);
			//polygons
			glColor3d(0,1,0);//green
			A.polygon(vertexesP);
			glColor3d(0.9,0.5,0.9);//fuchsia
			A.polygon(vertexesPO, offsetPO);
			glColor3d(0,1,1);//aqua
			A.polygon(vertexesPO, offsetPA, angle);
			glColor3d(0.4,0.4,0.6);//steel
			A.polygon(vertexesP, angle);
			//lines
			glColor3d(1,0.4,0.7);//hot pink
			A.line(10, 100, 900, 500);
			glColor3d(1,1,0);//Yellow
			A.line(lineS, lineE);
			//point
			glColor3d(0,0.3,0);//Dark green
			glPointSize(10);
			A.point(50,50);
			glColor3d(0.5,0.9,0);//lime green
			glPointSize(4);
			A.point(point);
			//points
			glColor3d(1,0.6,0);//orange
			glPointSize(6);
			A.points(points);
			glColor3d(0,0.6,1);//some form of blue
			glPointSize(8);
			A.points(points, offsetP, points.length);
			glColor3d(0.5,0,0);//maroon 0.5
			glPointSize(10);
			//A.rotate(A.points(points, offsetP, points.length), sizeR, angle);
			//A.points(points, offsetP, points.length);
			
			
			angle += 0.5;
			
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
		
	}
	
	private void initGL() {
		//generic clears screen and initiates the screen
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, worldDimX, 0, worldDimY, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	public void vertexGen(){
		//populates each polygon render type
		//this polygon is in full coordinates
		vertexesP[0] = new Vector2d(700,100);
		vertexesP[1] = new Vector2d(700,150);
		vertexesP[2] = new Vector2d(750,150);
		vertexesP[3] = new Vector2d(750,100);
		vertexesP[4] = new Vector2d(725,50);
		//this polygon is in relative coordinates
		vertexesPO[0] = new Vector2d(0,0);
		vertexesPO[1] = new Vector2d(0,50);
		vertexesPO[2] = new Vector2d(50,50);
		vertexesPO[3] = new Vector2d(50,0);
		vertexesPO[4] = new Vector2d(25,-75);
		//points vector
		points[0] = new Vector2d(50,70);
		points[1] = new Vector2d(40,75);
		points[2] = new Vector2d(18,12);
		points[3] = new Vector2d(80,50);
		points[4] = new Vector2d(32,5);
		points[5] = new Vector2d(77,90);
		points[6] = new Vector2d(16,68);
		
	}
	
	public static void main(String[] args) {
		new RenderTester().run();

	}

}
