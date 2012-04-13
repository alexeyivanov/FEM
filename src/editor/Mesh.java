package editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.j3d.Appearance;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.jcae.opencascade.jni.ShapeUpgrade_Tool;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;

import core.Coordinate;
import core.Element;
import core.Elements;
import core.Identificator;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;


public class Mesh implements Visible {	
	
//************************* 
//	   Internal classes
//************************

	/**
	 *  Mesh point with result value
	 */
	public static class MeshPoint {	    
		
		protected int index;
		protected double result = 0;
		protected int resCount = 0;
		protected boolean selected = false;
		
		private CoordArrayList coords;
				
		protected MeshPoint(int index, CoordArrayList coords) {
			this.index = index;		
			this.coords = coords;
		} 
		
		public double getResult() {
			return result;
		}	

		public void setResult(double result) {
			this.result = result;
			resCount = 1;
		}
		
		protected void addResult(MeshPoint p) {
			if (!p.hasResult()) return;
			this.result += p.result;
			resCount += p.resCount;
		}
		
		protected void averageResult() {
			if (resCount <= 0) return;
			result /= resCount;
			resCount = 1;
		}
		
		public boolean hasResult() {
			return (resCount != 0);
		}
		
		
		public void select() {
			selected = !selected;
			setSelected(selected);
		}

		public void setSelected(boolean v) {		
			selected = v;		
		}
		
		public boolean isSelected() {
			return selected;
		}
		
		protected double[] getXYZ() {
			return coords.getXYZ(index);
		}
	}
	
	/**
	 * Array list of coordinates {x,y,z,x,y,z....}
	 */
	private static final int sizeStep = 100;
	private class CoordArrayList {
		int i = 0;
		double[] coords = new double[sizeStep*3];
		
		public void add(double d) {
			coords[i] = d;
			if (i+1 >= coords.length) {
				double[] newCoords = new double[coords.length*2];
				System.arraycopy(coords, 0, newCoords, 0, coords.length);
				coords = newCoords;
			}
			i++;
		}
		
		public double[] getXYZ(int index) {
			return new double[] {coords[index*3+0], coords[index*3+1], coords[index*3+2]};
		}
		
		public void add(double[] array) {
			for (double d: array) add(d);
		}
		
		public int size() {
			return i/3;
		}
		
		public double[] getArray() {
			double[] newCoords = new double[i];
			System.arraycopy(coords, 0, newCoords, 0, i);
			return newCoords;
		}	
	}
	
	/**
	 * Array list of indices
	 */
	public static class IndArrayList {
		int i = 0;
		int[] indices = new int[sizeStep];
		
		public int add(int ind) {
			indices[i] = ind;			
			if (i+1 >= indices.length) {
				int[] newInd = new int[indices.length*2];
				System.arraycopy(indices, 0, newInd, 0, indices.length);
				indices = newInd;
			}
			return i++;
		}
		
		public int size() {
			return i;
		}
		
		public int[] getArray() {
			int[] newInd = new int[i];
			System.arraycopy(indices, 0, newInd, 0, i);
			return newInd;
		}
	}
	
	double minResult = Double.MAX_VALUE, maxResult = -Double.MAX_VALUE;
	
	
	//************************
	// 	Mesh data
	//************************
	
	private KDTree<MeshPoint> pointsSet = null;
	private CoordArrayList coords = new CoordArrayList();
	private ArrayList<MeshPoint> points = new ArrayList<MeshPoint>();
	private int indexOrigin = 0;
	private double[] coordsArray = null;
	private Color3f[] colors = null;
	private IndArrayList faceInd = new IndArrayList();
	private IndArrayList lineInd = new IndArrayList();
	
	private Shape3D facesShape = null, linesShape = null;
	BranchGroup facesBG = null, edgesBG = null, textBG = null;
	private boolean selected = false;
	private VisualSettings vs;
	
	public Mesh() {	
		vs = new VisualSettings(Color.GREEN, Color.BLACK, 1, null, 0);
	}
	
	public Mesh(VisualSettings vs) {	
		this.vs = new VisualSettings(vs);
	}
	
	public Mesh(Elements elements) {
		this();
		for (Element e : elements) {
			Mesh m = e.draw(null);
			if (m != null) addMesh(m);		
		}
	}
	
	public Mesh(Elements elements, Identificator resultId) {
		this();
		for (Element e : elements) {
			Mesh m = e.draw(resultId);
			if (m != null) addMesh(m);		
		}
	}
		
	public int point(double x, double y, double z) {
		int index = points.size();
		MeshPoint p = new MeshPoint(index, coords);		
		points.add(p);
		coords.add(x);
		coords.add(y);
		coords.add(z);
		return index;
	}	
	
	public int point(Coordinate c) {
		return point(c.getX(), c.getY(), c.getZ());
	}	
	
	public void addPoints(double[] coords) {
		if (coords.length % 3 != 0) throw new IllegalArgumentException("Points array must contain 3*pointsNumber of double values (x,y,z)");
		indexOrigin = points.size();
		for (int i = 0; i < coords.length; i += 3) {
			point(coords[i],coords[i+1],coords[i+2]);			
		}
	}
	
	public void addLines(int[] lines) {
		if (lines.length % 2 != 0) throw new IllegalArgumentException("Lines array must contain 2*linesNumber of indices");
		for (int i = 0; i < lines.length; i += 2) {
			line(lines[i]+indexOrigin,lines[i+1]+indexOrigin);			
		}
	}
	
	public void addFaces(int[] faces) {
		if (faces.length % 3 != 0) throw new IllegalArgumentException("Faces array must contain 2*trianglesNumber of indices");
		for (int i = 0; i < faces.length; i += 3) {
			face(faces[i]+indexOrigin,faces[i+1]+indexOrigin,faces[i+2]+indexOrigin);			
		}
	}
	
	public void addTriangles(int[] triangles) {
		if (triangles.length % 3 != 0) throw new IllegalArgumentException("Triangles array must contain 2*trianglesNumber of indices");
		for (int i = 0; i < triangles.length; i += 3) {
			triangle(triangles[i]+indexOrigin,triangles[i+1]+indexOrigin,triangles[i+2]+indexOrigin);			
		}
	}
	
	public void addTetrahedrons(int[] tetrahedrons) {
		if (tetrahedrons.length % 4 != 0) throw new IllegalArgumentException("Tetrahedrons array must contain 2*tetrahedronsNumber of indices");
		for (int i = 0; i < tetrahedrons.length; i += 4) {
			tetrahedron(tetrahedrons[i]+indexOrigin,tetrahedrons[i+1]+indexOrigin,tetrahedrons[i+2]+indexOrigin,tetrahedrons[i+3]+indexOrigin);			
		}
	}
	
	public void addMesh(Mesh m) {
		m.assemble();
		addPoints(m.coords.getArray());
		for (int i = 0; i < m.points.size(); i++) {
			if (m.points.get(i).hasResult()) 
				points.get(i+indexOrigin).setResult(m.points.get(i).getResult());
		}
		addLines(m.lineInd.getArray());
		addFaces(m.faceInd.getArray());
	}
	
	private MeshPoint add(MeshPoint p) {
		if (pointsSet == null) throw new NullPointerException("Mesh did not assembled");

		MeshPoint p1 = p;	
		try {
			pointsSet.insert(p.getXYZ(), p);
		} catch (KeySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyDuplicateException e) {
			if (p.hasResult()) {
				try {
					p1 = pointsSet.nearest(p.getXYZ());
				} catch (KeySizeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				p1.addResult(p);	
			}						
		}		
		if (p.hasResult()) {			
			if (p.result > maxResult) maxResult = p.result;
			if (p.result < minResult) minResult = p.result;
		}		
		return p1;
	}
	
	public void selectNearestPoint(double x, double y, double z) {
		if (pointsSet == null) throw new NullPointerException("Mesh did not assembled");
		MeshPoint p = null;
		try {
			p = pointsSet.nearest(new double[] {x, y, z});
		} catch (KeySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		if (p != null) p.select();
	}
	
	public double getResult(double x, double y, double z) {
		if (pointsSet == null) throw new NullPointerException("Mesh did not assembled");		
		MeshPoint p = null;
		try {
			p = pointsSet.nearest(new double[] {x, y, z});
		} catch (KeySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (p != null) return p.getResult(); else return 0;		
	}
	
	public void setResult(int i, double result) {
		points.get(i).setResult(result);
	}
	
	public void line(int i1, int i2) {
		lineInd.add(i1);		
		lineInd.add(i2);				
	}
	
	public void line(int[] indices, int i1, int i2) {
		lineInd.add(indices[i1]);		
		lineInd.add(indices[i2]);				
	}
		
	public void face(int i1, int i2, int i3) {
		faceInd.add(i1);
		faceInd.add(i2);
		faceInd.add(i3);
	}
	
	public void face(int[] indices, int i1, int i2, int i3) {
		faceInd.add(indices[i1]);
		faceInd.add(indices[i2]);
		faceInd.add(indices[i3]);
	}		
	
	public void triangle(int i1, int i2, int i3) {
		face(i1,i2,i3);
//		face(i1,i3,i2);
		line(i1,i2);
		line(i2,i3);
		line(i1,i3);
	}
	
	public void quadrangle(int i1, int i2, int i3, int i4) {
		int[] ind = {i1,i2,i3,i4};
		face(ind,0,1,2);
		face(ind,2,3,1);
		face(ind,0,2,1);
		face(ind,2,1,3);
		line(ind,0,1);
		line(ind,1,2);
		line(ind,2,3);
		line(ind,3,1);
	}
	
	public void tetrahedron(int i1, int i2, int i3, int i4) {
		if (i1>points.size()-1 || i2>points.size()-1 || i3>points.size()-1 || i4>points.size()-1)
			throw new ArrayIndexOutOfBoundsException("Indexes " + i1+" "+i2+" "+i3+ " "+i4+" are wrong in tetrahedron");
		face(i1,i2,i3);
		face(i1,i2,i4);
		face(i2,i3,i4);
		face(i1,i3,i4);
		line(i1,i2);
		line(i2,i3);
		line(i1,i3);
		line(i1,i4);
		line(i2,i4);
		line(i3,i4);
	}
	
	public void hexahedron(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
		int[] ind = {i1,i2,i3,i4,i5,i6,i7,i8};
		face(ind,0,2,1);
		face(ind,2,0,3);		
		
		face(ind,4,5,6);
		face(ind,6,7,4);
		
		face(ind,0,1,5);
		face(ind,5,4,0);
		
		face(ind,1,2,6);
		face(ind,6,5,1);
		
		face(ind,2,3,7);
		face(ind,7,6,2);
		
		face(ind,3,0,4);
		face(ind,4,7,3);
		
		line(ind,0,1);
		line(ind,1,2);
		line(ind,2,3);
		line(ind,0,3);
		
		line(ind,4,5);
		line(ind,5,6);
		line(ind,6,7);
		line(ind,4,7);
		
		line(ind,0,4);
		line(ind,1,5);
		line(ind,2,6);
		line(ind,3,7);		
	}
	
	public void setFaceColor(Color c) {
		vs.setFaceColor(c);
	}
	
	public void setLineColor(Color c) {
		vs.setLineColor(c);		
	}
	
	public void setLineWidth(float w) {
		vs.setLineWidth(w);		
	}	
	
	protected void createEdges() {
		if (lineInd.size() == 0) return;
		if (coordsArray == null) coordsArray = coords.getArray();		
		IndexedLineArray ila=new IndexedLineArray(coordsArray.length/3,	GeometryArray.COORDINATES,lineInd.size());
		ila.setCoordinates(0, coordsArray);
		ila.setCoordinateIndices(0, lineInd.getArray());
		Shape3D shape3d=new Shape3D(ila);
		shape3d.setAppearance(vs.getLineAppearance());
		shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shape3d.setCapability(Node.ALLOW_PICKABLE_WRITE);
		linesShape = shape3d;
		edgesBG = new BranchGroup();
		edgesBG.addChild(shape3d);
	}
	
	protected void createFaces() {
		if (faceInd.size() == 0) return;
		if (coordsArray == null) coordsArray = coords.getArray();
		
		GeometryInfo gi=new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
		gi.setCoordinates(coordsArray);	
		int[] faces = faceInd.getArray();
		gi.setCoordinateIndices(faces);
		if (colors != null) {
			gi.setColors(colors);
			gi.setColorIndices(faces);
		}
					
//		NormalGenerator ng = new NormalGenerator(0);
//		ng.generateNormals(gi);
		
        
		GeometryArray g=gi.getGeometryArray();
		g.setCapability(GeometryArray.ALLOW_COUNT_READ);
		g.setCapability(GeometryArray.ALLOW_FORMAT_READ);
		g.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
		g.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_READ);	
		
		Shape3D shape3d=new Shape3D(g);
		shape3d.setAppearance(vs.getMeshAppearance());
		shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);			
		shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shape3d.setCapability(Node.ALLOW_PICKABLE_WRITE);
		facesShape = shape3d;	
		facesBG = new BranchGroup();
		facesBG.addChild(shape3d);
		facesBG.setCapability(BranchGroup.ALLOW_DETACH);
	}
	
	protected void createTextLabels() {
		        
		Appearance a=new Appearance();
		Material m=new Material();
		Color faceColor = Color.BLUE;
		Color3f color=new Color3f(
				((float)faceColor.getRed())/255
				,((float)faceColor.getGreen())/255
				,((float)faceColor.getBlue())/255);
		
		Color3f gray=new Color3f(0.1f,0.1f,0.1f);

		
		m.setAmbientColor(gray);
		m.setDiffuseColor(color);
		m.setSpecularColor(gray);
		m.setShininess(1000f);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);			
		a.setMaterial(m);
        
        
        double scale = 0.3;        
        Font3D font3D = new Font3D(new Font("Helvetica", Font.PLAIN, 1),
                                   new FontExtrusion(new Line2D.Double(0, 0, .05, 0)));                
        
                    	
        textBG = new BranchGroup();          
        
        for (MeshPoint p : points) {
        	if (!p.isSelected()) continue;
        	Text3D textGeom = new Text3D(font3D, Double.toString((double)(Math.round(p.result*1000))/1000));        	
            textGeom.setAlignment(Text3D.ALIGN_FIRST);
         
            
            Shape3D textShape = new Shape3D(textGeom);
            
            
            textShape.setAppearance(a);
            Transform3D t = new Transform3D();            
            t.setScale(scale);                                   
            t.setTranslation(new Vector3d(p.getXYZ()));            
            TransformGroup tg = new TransformGroup(t);
            textBG.addChild(tg);
            tg.addChild(textShape);
            
        }          
        	
	}
		
	protected void assemblePoints() {
		System.out.print("Assembling...");
		
		
		for (int i = 0; i < points.size(); i++) {
			MeshPoint p = points.get(i);			
			points.set(i, add(p));
		}
		
		System.out.println("done.");
	}
	
	public void create() {		
		colors = new Color3f[points.size()];
		for (int i = 0; i < points.size(); i++) {
			MeshPoint p = points.get(i);
			p.averageResult();
			if (p.hasResult()) 				
				colors[i] = vs.getScaleColor((float)((p.result-minResult)/(maxResult - minResult)));
			else 
				colors[i] = vs.getJ3DColor(vs.getFaceColor());			
		}		
		createEdges();
		createFaces();
		createTextLabels();
		
	}
	
	public void assemble() {
		int tableSize = points.size()/10;
		if (tableSize < 100) tableSize = 100;
		pointsSet = new KDTree<MeshPoint>(3);		
		assemblePoints();
		create();
	}

	
	@Override
	public BranchGroup getFaces() {
		return facesBG;
	}

	@Override
	public BranchGroup getEdges() {		
		return edgesBG;
	}
	
	@Override
	public BranchGroup getText() {		
		return textBG;
	}

	@Override
	public void select() {
		selected = !selected;
		setSelected(selected);
	}

	@Override
	public void setSelected(boolean v) {		
		selected = v;
		if (selected) linesShape.setAppearance(vs.getSelectedLineAppearance());
		else linesShape.setAppearance(vs.getLineAppearance());
		
		if (selected) facesShape.setAppearance(vs.getSelectedMeshAppearance());
		else facesShape.setAppearance(vs.getMeshAppearance());
		
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	
	public Bounds getBounds() {		
		Bounds b = null;
		if (facesBG != null) b = facesBG.getBounds();
		if (edgesBG != null) b.combine(edgesBG.getBounds());
		if (textBG != null) b.combine(textBG.getBounds());
		return b;
	}
	
	
	
}
