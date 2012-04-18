package editor;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Window;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Group;
import javax.media.j3d.HiResCoord;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PickInfo;
import javax.media.j3d.PickShape;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyInterpolator;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.pickfast.PickIntersection;
import com.sun.j3d.utils.pickfast.PickTool;
import com.sun.j3d.utils.pickfast.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickingCallback;
import com.sun.j3d.utils.universe.SimpleUniverse;

import core.Element;
import core.Identificator;



import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Viewer extends Canvas3D {
	
	private SimpleUniverse universe = null;
	private BranchGroup root = null, view = null;
	private TransformGroup rotation;
	private BoundingSphere bounds;
	private float angleV = 0, angleH = 0, angleStep = (float)Math.PI / 20;
	private float lastAngleV = 0, lastAngleH = 0;
	private Point3d center = new Point3d(0,0,0);	
	private float scale = 1;
	private Drawing drawing;
	private DrawModel drawModel;
	private ArrayList<TransformGroup> textLabels = new ArrayList<TransformGroup>();
	
	
	private class KeyListener extends KeyAdapter
	{
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			boolean found=true;
			switch(e.getKeyCode())
			{
				case KeyEvent.VK_RIGHT: rotateV(angleStep); break;
				case KeyEvent.VK_LEFT: rotateV(-angleStep); break;
				case KeyEvent.VK_UP: rotateH(-angleStep); break;
				case KeyEvent.VK_DOWN: rotateH(angleStep); break;
				case KeyEvent.VK_DELETE: drawing.deleteSelected(); draw(drawing); break;
				default: found=false;
			}
			if(found)
			{
			
			}
		}
				
	}
	
	public class PickMouse extends PickMouseBehavior {

//		protected int x_last, y_last;
		
		public PickMouse(Canvas3D canvas, BranchGroup root, Bounds bounds) {
			super(canvas, root, bounds);
			pickCanvas.setMode(PickInfo.PICK_GEOMETRY);			
			this.setSchedulingBounds(bounds);
		}

		@Override
		public void updateScene(int xpos, int ypos) {
			// TODO Auto-generated method stub
//			
//			if ((mevent.getID() == MouseEvent.MOUSE_DRAGGED) &&
//					!mevent.isAltDown() && mevent.isMetaDown()) {
//					
////			if (mevent.getID() == MouseEvent.MOUSE_DRAGGED) {
//					int x = mevent.getX();
//					int y = mevent.getY();
//					
//					int dx = x - x_last;
//					int dy = y - y_last;
//					
//					center.x += dx * 0.02;
//					center.y -= dy * 0.02;
//					
//					x_last = x;
//					y_last = y;
//					updateView();
//			}
//			else if (mevent.getID() == MouseEvent.MOUSE_PRESSED) {
//				x_last = mevent	.getX();
//				y_last = mevent.getY();
//			}
//			
//			
		    
			if (!mevent.isMetaDown() && !mevent.isAltDown()){
				BranchGroup bg = null;
				Shape3D s = null;
				pickCanvas.setFlags(PickInfo.NODE | PickInfo.SCENEGRAPHPATH | PickInfo.CLOSEST_INTERSECTION_POINT);
				pickCanvas.setShapeLocation(xpos, ypos);
				
			
				PickInfo pickInfo = pickCanvas.pickClosest();				
				if(pickInfo != null) {
//					if (drawing instanceof Mesh) {
//						Point3d p = pickInfo.getClosestIntersectionPoint();
//						
//						Mesh m = (Mesh)drawing;
//						MeshPoint mp = m.find(p.x, p.y, p.z);
//						if (mp != null) mp.select();
//						
//						m.setLineColor(Color.GREEN);
//						m.setLineWidth(5);
////						createSceneGraph(drawing);
//						
////						view.detach();						
//						draw(drawing);
////						root.addChild(view);
//						return;
//					}
					
					System.out.println(pickInfo.getClosestIntersectionPoint());					
					
					bg = (BranchGroup) pickCanvas.getNode(pickInfo, PickTool.TYPE_BRANCH_GROUP);
//					s = (Shape3D) pickCanvas.getNode(pickInfo, PickTool.TYPE_SHAPE3D);					
					
					if (bg != null) {
						System.out.println("Shape "+bg.getName()+" selected");
						if (bg.getUserData() != null) {
							Shape sh = (Shape) bg.getUserData();
							Mesh m = sh.getMesh();
							if (m != null) {
								Point3d p3d = pickInfo.getClosestIntersectionPoint();
								m.selectNearestPoint(p3d.x, p3d.y, p3d.z);
								m.createTextLabels();
								System.out.println("Result = " + m.getResult(p3d.x, p3d.y, p3d.z));
								draw(drawing);
							}
							else {
								sh.select();
//								drawing.meshShape(sh);
//								draw(drawing);
							}
							
//							Visible v = (Visible) bg.getUserData();
//							v.select();							
						}						
					}
					
//					if (s != null) {
//						System.out.println("Shape "+s.getName()+" selected");
//						if (s.getUserData() != null) {
//							Visible v = (Visible) s.getUserData();
//							v.select();	
//						}
//						
//					}
					
			    } 
			}
		}
		
	}
	
	private void updateView() {
		Transform3D rotateXY = new Transform3D();
	
	
		Transform3D rotateY = new Transform3D();
		rotateXY.rotX(angleH);
		rotateY.rotY(angleV);
		rotateXY.mul(rotateY);
		rotateXY.setScale(scale);
		
		Vector3d trans = new Vector3d(-center.x,-center.y,-center.z);
		Transform3D translation = new Transform3D();
		translation.set(trans);
	    rotateXY.mul(translation);	    
		
		rotation.setTransform(rotateXY);
		for (TransformGroup tg: textLabels) {
			Transform3D t = new Transform3D();
			tg.getTransform(t);			
			Transform3D rotX = new Transform3D();
			Transform3D rotY = new Transform3D();
						
			rotY.rotY(lastAngleV-angleV);
			rotX.rotX(lastAngleH-angleH);
			t.mul(rotX);			
			t.mul(rotY);			
			    					
			tg.setTransform(t);
		}
		lastAngleH = angleH;
		lastAngleV = angleV;
	}
	
	private void rotateV(float angle) {
		angleV += angle;
		updateView();		
	}
	
	private void rotateH(float angle) {
		angleH += angle;
		updateView();		
	}
	
	public Viewer(Window window, Drawing drawing) {
		
		super(getPreferredConfiguration(window));
		universe = new SimpleUniverse(this);		
		universe.getViewingPlatform().setNominalViewingTransform();
		root = new BranchGroup();
		root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);		
		createSceneGraph(drawing);
		universe.addBranchGraph(root);
		
	}
	
	public Viewer(Window window, DrawModel drawing) {
		
		super(getPreferredConfiguration(window));
		universe = new SimpleUniverse(this);		
		universe.getViewingPlatform().setNominalViewingTransform();
		root = new BranchGroup();
		root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);		
		createSceneGraph(drawing);
		universe.addBranchGraph(root);
		
	}
	
	public void createSceneGraph(Drawing drawing) {
		// Branch group which can be deattached for changing scene;
		view = new BranchGroup();			
		view.setCapability(BranchGroup.ALLOW_DETACH);
		//BranchGroup for rotation	
		rotation = new TransformGroup();
    	rotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    	rotation.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);    		
    	view.addChild(rotation);    			
		bounds = new BoundingSphere();		
		DirectionalLight lightD = new DirectionalLight();
        lightD.setDirection(new Vector3f(0.0f,0.0f,-0.7f));
        lightD.setInfluencingBounds(bounds);
        view.addChild(lightD);


        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bounds);
        view.addChild(lightA);
		
		Background background = new Background();
		background.setColor(1.0f, 1.0f, 1.0f);
		background.setApplicationBounds(bounds);
		view.addChild(background);
		
//		MouseRotate myMouseRotate = new MouseRotate();
//        myMouseRotate.setTransformGroup(rotation);
//        myMouseRotate.setSchedulingBounds(bounds);
//        root.addChild(myMouseRotate);
//
//        MouseTranslate myMouseTranslate = new MouseTranslate();
//        myMouseTranslate.setTransformGroup(rotation);
//        myMouseTranslate.setSchedulingBounds(new BoundingSphere());
//        root.addChild(myMouseTranslate);

//        MouseZoom myMouseZoom = new MouseZoom();
//        myMouseZoom.setTransformGroup(rotation);        
//        myMouseZoom.setSchedulingBounds(new BoundingSphere());
//        root.addChild(myMouseZoom);
        		//		
		PickMouse pick = new PickMouse(this, view, bounds);
		view.addChild(pick);        
		
		addKeyListener(new KeyListener());
		
		angleV = (float)Math.PI/4;
		angleH = (float)Math.PI/5;
		
		draw(drawing);
		zoomAll();
	}	
	
	public void createSceneGraph(DrawModel drawing) {
		// Branch group which can be deattached for changing scene;
		view = new BranchGroup();			
		view.setCapability(BranchGroup.ALLOW_DETACH);
		//BranchGroup for rotation	
		rotation = new TransformGroup();
    	rotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    	rotation.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);    		
    	view.addChild(rotation);    			
		bounds = new BoundingSphere();		
		DirectionalLight lightD = new DirectionalLight();
        lightD.setDirection(new Vector3f(0.0f,0.0f,-0.7f));
        lightD.setInfluencingBounds(bounds);
        view.addChild(lightD);


        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bounds);
        view.addChild(lightA);
		
		Background background = new Background();
		background.setColor(1.0f, 1.0f, 1.0f);
		background.setApplicationBounds(bounds);
		view.addChild(background);
		
//		MouseRotate myMouseRotate = new MouseRotate();
//        myMouseRotate.setTransformGroup(rotation);
//        myMouseRotate.setSchedulingBounds(bounds);
//        root.addChild(myMouseRotate);
//
//        MouseTranslate myMouseTranslate = new MouseTranslate();
//        myMouseTranslate.setTransformGroup(rotation);
//        myMouseTranslate.setSchedulingBounds(new BoundingSphere());
//        root.addChild(myMouseTranslate);

//        MouseZoom myMouseZoom = new MouseZoom();
//        myMouseZoom.setTransformGroup(rotation);        
//        myMouseZoom.setSchedulingBounds(new BoundingSphere());
//        root.addChild(myMouseZoom);
        		//		
		PickMouse pick = new PickMouse(this, view, bounds);
		view.addChild(pick);        
		
		addKeyListener(new KeyListener());
		
		angleV = (float)Math.PI/4;
		angleH = (float)Math.PI/5;
		
		draw(drawing);
		zoomAll();
	}
	
	public void draw(DrawModel d) {
    	view.detach();
    	rotation.removeAllChildren();    	
    	drawModel = d;
    	if (drawModel.getPicture() == null) return;
    	Integer i = 0;
    	for (VisualShape v : drawModel.getPicture()) {
    		TransformGroup shapeGroup = new TransformGroup();
    		rotation.addChild(shapeGroup);
    		BranchGroup faces = v.getFaces2(BranchGroup.class);
    		BranchGroup edges = v.getEdges2(BranchGroup.class);    		    		
    		BranchGroup text = v.getText2(BranchGroup.class);
    		if (faces == null && edges == null) continue;
    		i++;
    		if (faces != null) {    			
    			faces.detach();
    			rotation.addChild(faces);
    			faces.setPickable(true);
        		faces.setUserData(v);
        		faces.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        		faces.setName(i.toString());
   
    		}
    		if (edges != null) {
    			edges.detach();
    			rotation.addChild(edges);
    			edges.setPickable(true);
        		edges.setUserData(v);
        		edges.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
         		edges.setName(i.toString());
    		}
    		
    		if (text != null) {
    			text.detach();
    			rotation.addChild(text);
    			text.setPickable(false);
    			if (text instanceof BranchGroup) {
    				BranchGroup bg = (BranchGroup)text;
    				for (int j = 0; j < bg.numChildren(); j++) {
    					Node child = bg.getChild(j);
    					if (child instanceof TransformGroup) {
    						TransformGroup tg = (TransformGroup)child;
    						tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    						tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    						textLabels.add(tg);    						
    					}
    				}
    			}
    			 
    			
//        		edges.setUserData(v);
//        		edges.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
//         		edges.setName(i.toString());
    		}    		
    	} 
    	root.addChild(view);
    }
	
	
	 /**
     * From https://java3d.dev.java.net/issues/show_bug.cgi?id=89
     * Finds the preferred <code>GraphicsConfiguration</code> object
     * for the system.  This object can then be used to create the
     * Canvas3D object for this system.
     * @param window the window in which the Canvas3D will reside 
     *
     * @return The best <code>GraphicsConfiguration</code> object for
     *  the system.
     */
    private static GraphicsConfiguration getPreferredConfiguration(Window window)
    {
    	if(window==null)
    		return SimpleUniverse.getPreferredConfiguration();
    	GraphicsDevice device = window.getGraphicsConfiguration().getDevice();
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        String stereo;

        // Check if the user has set the Java 3D stereo option.
        // Getting the system properties causes appletviewer to fail with a
        //  security exception without a try/catch.

        stereo = (String) java.security.AccessController.doPrivileged(
           new java.security.PrivilegedAction() {
           public Object run() {
               return System.getProperty("j3d.stereo");
           }
        });

        // update template based on properties.
        if (stereo != null) {
            if (stereo.equals("REQUIRED"))
                template.setStereo(GraphicsConfigTemplate.REQUIRED);
            else if (stereo.equals("PREFERRED"))
                template.setStereo(GraphicsConfigTemplate.PREFERRED);
        }
        // Return the GraphicsConfiguration that best fits our needs.
        return device.getBestConfiguration(template);
    }
    
    
    public void draw(Drawing d) {
    	view.detach();
    	rotation.removeAllChildren();    	
    	drawing = d;
    	if (drawing.getPicture() == null) return;
    	Integer i = 0;
    	for (Visible v : drawing.getPicture()) {
    		TransformGroup shapeGroup = new TransformGroup();
    		rotation.addChild(shapeGroup);
    		BranchGroup faces = v.getFaces();
    		BranchGroup edges = v.getEdges();    		    		
    		BranchGroup text = v.getText();
    		if (faces == null && edges == null) continue;
    		i++;
    		if (faces != null) {    			
    			faces.detach();
    			rotation.addChild(faces);
    			faces.setPickable(true);
        		faces.setUserData(v);
        		faces.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        		faces.setName(i.toString());
   
    		}
    		if (edges != null) {
    			edges.detach();
    			rotation.addChild(edges);
    			edges.setPickable(true);
        		edges.setUserData(v);
        		edges.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
         		edges.setName(i.toString());
    		}
    		
    		if (text != null) {
    			text.detach();
    			rotation.addChild(text);
    			text.setPickable(false);
    			if (text instanceof BranchGroup) {
    				BranchGroup bg = (BranchGroup)text;
    				for (int j = 0; j < bg.numChildren(); j++) {
    					Node child = bg.getChild(j);
    					if (child instanceof TransformGroup) {
    						TransformGroup tg = (TransformGroup)child;
    						tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    						tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    						textLabels.add(tg);    						
    					}
    				}
    			}
    			 
    			
//        		edges.setUserData(v);
//        		edges.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
//         		edges.setName(i.toString());
    		}    		
    	} 
    	root.addChild(view);
    }
    
    private Bounds getBounds(Visible v) {
    	Bounds b = null;
    	if (v == null) return null;
		if (v.getFaces() != null) b = v.getFaces().getBounds();
		if (v.getEdges() != null) b.combine(v.getEdges().getBounds());
		return b;
    }
    
    public void zoomAll() {
    	bounds = new BoundingSphere();
    	boolean first = true;
    	for (Visible v : drawing.getPicture()) {
    		Bounds b = getBounds(v);
    		if (first) bounds.set(b); else bounds.combine(getBounds(v));
    		first = false;
    	} 
    	bounds.getCenter(center);
    	scale = 1.0f/1.3f/(float)bounds.getRadius();
    	updateView();
    }
    
    public static void viewAWT(Drawing d) {
		JFrame frame=new JFrame();
		frame.setBounds(0, 0, 500, 400);
		Viewer viewer=new Viewer(frame, d);		
		frame.getContentPane().add(viewer);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
	}
    
    public static void view(ArrayList<Element> elements) {
//    	Mesh m = new Mesh(elements);
//    	Drawing d = new Drawing();		
//		d.addMesh(m);
//		Viewer.view(d);
    }
    
    public static void view(ArrayList<Element> elements, Identificator resultId) {
//    	Mesh m = new Mesh(elements, resultId);
//    	m.assemble();
//    	Drawing d = new Drawing();		
//		d.addMesh(m);
//		view(d);
    }
    
    public static void view(Mesh mesh) {    	
    	Drawing d = new Drawing();		
		d.addMesh(mesh);
		view(d);
    }
    
    public static void view(Drawing d)
	{
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		Composite composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame baseFrame = SWT_AWT.new_Frame(composite);
	    
		baseFrame.setBounds(0, 0, 800, 600);

		Viewer viewer=new Viewer(baseFrame, d);
		baseFrame.add(viewer);
	    
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		baseFrame.dispose();
		shell.dispose();
		composite.dispose();
		display.dispose();
		System.exit(0);
		
	}
		
    public static void view(DrawModel d)
	{
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		Composite composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame baseFrame = SWT_AWT.new_Frame(composite);
	    
		baseFrame.setBounds(0, 0, 800, 600);

		Viewer viewer=new Viewer(baseFrame, d);
		baseFrame.add(viewer);
	    
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		baseFrame.dispose();
		shell.dispose();
		composite.dispose();
		display.dispose();
		System.exit(0);
		
	}


}
