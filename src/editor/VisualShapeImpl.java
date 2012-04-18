package editor;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

import org.jcae.opencascade.jni.TopoDS_Edge;
import org.jcae.opencascade.jni.TopoDS_Shape;
import org.jcae.opencascade.jni.TopoDS_Vertex;

import editor.Shape.Edge;
import editor.Shape.FaceMesh;
import editor.Shape.Vertex;

public class VisualShapeImpl implements VisualShape {

	protected int type;
	
	static final int EDGE = 1, FACE = 2, SOLID = 3, COMPOUND = 4, MESH = 5;
	
	private TopoDS_Shape shape = null;  //OCC base shape. Can be null if we have MESH type 

	public TopoDS_Shape getShape() {
		return shape;
	}


//	private List<FaceMesh> faceMeshes; // face meshes of OCC shape
//	private List<float[]> edgeArrays; // edge arrays of OCC shape
	
	private BranchGroup facesNode = null, edgesNode = null, textNode = null; // Java3D nodes of faces edges and text
	private ArrayList<Shape3D> faces;  // shape faces, edges and text members
	private ArrayList<Shape3D> edges;
	private ArrayList<Shape3D> text;
	private boolean selected = false; // selected state of shape
	protected boolean cutted = false; // used by logical operations
	private boolean drawMesh = false; // true if we draw FE mesh instead of shape
	private Mesh mesh = null; // FE mesh
	
	private double meshSize = 1; // gloobal mesh size in shape
	
	
	/**
	 * Storage for vertex and edge mesh info (without any special information = null)
	 */
	private List<Vertex> vertexMeshSize = null;
	private List<Edge> edgeMeshSize = null;
	
	VisualSettings vs;
		
	public VisualShapeImpl(int type, TopoDS_Shape shape, VisualSettings vs) {
		this.type = type;
		this.vs = new VisualSettings(vs);		
		setShape(shape);
		
	}
	
	private GeometryShape commonShape; 
	
	public VisualShapeImpl(int type, GeometryShape shape, VisualSettings vs) {
		this.type = type;
		this.vs = new VisualSettings(vs);
		
		setShape(shape);
		
	}
	
	public void setShape(GeometryShape shape) {
		
		this.commonShape = shape;
		
		vertexMeshSize = null;
		edgeMeshSize = null;
		this.shape = shape.getIntertalShape(TopoDS_Shape.class);
		
		faces = new ArrayList<Shape3D>();
		facesNode = Java3DUtils.createFaces(shape.getFaceMeshes(), faces, vs);

		edges = new ArrayList<Shape3D>();
		edgesNode = Java3DUtils.createEdges(shape.getEdgeArrays(), edges, vs);
	}
	
	public void setShape(TopoDS_Shape shape) {
		
		
		vertexMeshSize = null;
		edgeMeshSize = null;
		this.shape = shape;
		
		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(shape);
		faces = new ArrayList<Shape3D>();
		facesNode = Java3DUtils.createFaces(faceMeshes, faces, vs);


		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(shape);
		edges = new ArrayList<Shape3D>();
		edgesNode = Java3DUtils.createEdges(edgeArrays, edges, vs);
	}
	

	public VisualShapeImpl(int type, Mesh mesh, VisualSettings vs) {
		this.type = type;		
		this.vs = new VisualSettings(vs);
		this.mesh = mesh;
		mesh.create();
		drawMesh = true;		
	}
	
	public void setMesh(Mesh m) {		
		mesh = m;
		mesh.create();
		drawMesh();
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public void drawMesh() {
		if (mesh == null) return;
		drawMesh = true;		
	}
	
	public void drawShape() {
		if (getShape() == null) return;
		drawMesh = false;		
	}
	
	public void setVisualSettings(VisualSettings vs) {
		this.vs = vs; 
	}
	
	public VisualSettings getVisualSettings() {
		return vs; 
	}
		
	public int getType() {
		return type;
	}
	
	public BranchGroup getFaces() {
		if (drawMesh) return mesh.getFaces(); else return facesNode;
	}
	
	public BranchGroup getEdges() {
		if (drawMesh) return mesh.getEdges(); else 	return edgesNode;
	}
	
	public BranchGroup getText() {
		if (drawMesh) return mesh.getText(); else return null;
	}
	
	public Bounds getBounds() {
		if (drawMesh) return mesh.getBounds();
		Bounds b = null;
		if (facesNode != null) b = facesNode.getBounds();
		if (edgesNode != null) b.combine(edgesNode.getBounds());
		if (textNode != null) b.combine(textNode.getBounds());
		return b;
	}

	public void select() {
		selected = !selected;
		setSelected(selected);
	}
	
	public void setSelected(boolean v) {
		if (drawMesh) {
			mesh.setSelected(v);
			return;
		}
		selected = v;
		for (Shape3D s : faces) {			
			if (selected) s.setAppearance(vs.getSelectedFaceAppearance());
			else s.setAppearance(vs.getFaceAppearance());
		}
		for (Shape3D s : edges) {			
			if (selected) s.setAppearance(vs.getSelectedLineAppearance());
			else s.setAppearance(vs.getLineAppearance());
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setMeshSize(double size) {
		meshSize = size;
	}
	
	public double getMeshSize() {
		return meshSize;
	}
	
	public void setMeshSize(double x, double y, double z, double size) {

		if (vertexMeshSize == null) {
			vertexMeshSize = OCCUtils.getShapeVertices(getShape(), meshSize);
		}
		
		double minDist = Double.MAX_VALUE;
		Vertex vert = null;
		for (Vertex v : vertexMeshSize) {
			double d = v.distance(x, y, z);
			if (d < minDist) {
				minDist = d;
				vert = v;
			}
		}		
		if (vert != null) {
			vert.size = size;
		}
	}
	
	public TopoDS_Shape haveCommon(final Shape s) {
		
		if (!this.getBounds().intersect(s.getBounds())) {
			return null;		
		}
		
		return OCCUtils.haveCommon(this.getShape(), this.getType(), s.getShape(), s.getType());
	}
	
	public void setMeshSize(Point3d p, double size) {
		setMeshSize(p.x, p.y, p.z, size);
	}
	
	public void setMeshSize(Point3d p1, Point3d p2, double size) {

	}
	
	public void setMeshSize(double x, double y, double z, int n) {
		if (edgeMeshSize == null) {
			edgeMeshSize = OCCUtils.getShapeEdges(getShape());
		}

		double minDist = Double.MAX_VALUE;
		Edge edge = null;
		for (Edge e : edgeMeshSize) {
			double d = e.distance(x, y, z);
			if (d < minDist) {
				minDist = d;
				edge = e;
			}
		}
		if (edge != null) {
			edge.numElements = n;
		}
	}
	
	public void mesh() {
		Mesh m = new Mesh(vs);
		Mesher.CreateGmodel(TopoDS_Shape.getCPtr(getShape()));
		Mesher.SetMeshSize(meshSize);
		if (vertexMeshSize != null) {
			
			for (Vertex v : vertexMeshSize) {
				if (v.size != meshSize)
					Mesher.SetMeshSizeInPoint(TopoDS_Vertex.getCPtr(v.vertex), v.size);
			}
		}
					
		if (edgeMeshSize != null) {
			for (Edge e : edgeMeshSize) {
				if (e.numElements != -1)
					Mesher.SetPointsTransfiniteInEdge(TopoDS_Edge.getCPtr(e.edge), e.numElements);
			}
		}			
		double[] points = Mesher.MeshShape();
		int[] indtetr = Mesher.Indtetr();
		int[] indtrian = Mesher.Indtrian();
		int[] indline = Mesher.Indline();
		m.addPoints(points);
		m.addTetrahedrons(indtetr);
		m.addTriangles(indtrian);
		m.addLines(indline);			
		setMesh(m);
	}

	@Override
	public <T> T getFaces2(Class<T> clazz) {
		// TODO Auto-generated method stub
		return (T) getFaces();
	}

	@Override
	public <T> T getEdges2(Class<T> clazz) {
		// TODO Auto-generated method stub
		return (T) getEdges();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> K getShape2(Class<K> clazz) {
		// TODO Auto-generated method stub
		return (K) getShape();
	}

	@Override
	public <T> T getText2(Class<T> clazz) {
		// TODO Auto-generated method stub
		return (T) getText();
	}

	@Override
	public void setCutted(boolean cutted) {
		this.cutted = cutted;
		
	}

	@Override
	public <T> T haveCommon(VisualShape s, Class<T> clazz) {
		// TODO Auto-generated method stub
		
		if (!this.getBounds().intersect(s.getBounds(Bounds.class))) {
			return null;		
		}
		
		return (T) OCCUtils.haveCommon(this.getShape2(TopoDS_Shape.class), this.getType(), s.getShape2(TopoDS_Shape.class), s.getType());
	}

	@Override
	public <T> T getBounds(Class<T> clazz) {
		if (drawMesh) return (T) mesh.getBounds();
		Bounds b = null;
		if (facesNode != null) b = facesNode.getBounds();
		if (edgesNode != null) b.combine(edgesNode.getBounds());
		if (textNode != null) b.combine(textNode.getBounds());
		return (T) b;
	}

	@Override
	public <K> void setShape(K shape, Class<K> clazz) {
		vertexMeshSize = null;
		edgeMeshSize = null;
		this.shape = (TopoDS_Shape) shape;
		
		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes((TopoDS_Shape) shape);
		faces = new ArrayList<Shape3D>();
		facesNode = Java3DUtils.createFaces(faceMeshes, faces, vs);


		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays((TopoDS_Shape) shape);
		edges = new ArrayList<Shape3D>();
		edgesNode = Java3DUtils.createEdges(edgeArrays, edges, vs);
		
	}

	@Override
	public GeometryShape getGeometryShape() {
		return commonShape;
	}
	
}
