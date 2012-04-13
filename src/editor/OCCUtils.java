package editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import org.jcae.opencascade.jni.BRepAlgoAPI_Common;
import org.jcae.opencascade.jni.BRepAlgoAPI_Cut;
import org.jcae.opencascade.jni.BRepBndLib;
import org.jcae.opencascade.jni.BRepMesh_IncrementalMesh;
import org.jcae.opencascade.jni.BRep_Tool;
import org.jcae.opencascade.jni.Bnd_Box;
import org.jcae.opencascade.jni.GCPnts_UniformDeflection;
import org.jcae.opencascade.jni.GeomAdaptor_Curve;
import org.jcae.opencascade.jni.Geom_Curve;
import org.jcae.opencascade.jni.Poly_Triangulation;
import org.jcae.opencascade.jni.TopAbs_Orientation;
import org.jcae.opencascade.jni.TopAbs_ShapeEnum;
import org.jcae.opencascade.jni.TopExp_Explorer;
import org.jcae.opencascade.jni.TopLoc_Location;
import org.jcae.opencascade.jni.TopoDS_Edge;
import org.jcae.opencascade.jni.TopoDS_Face;
import org.jcae.opencascade.jni.TopoDS_Shape;
import org.jcae.opencascade.jni.TopoDS_Vertex;

import editor.Shape.Edge;
import editor.Shape.FaceMesh;
import editor.Shape.Vertex;


public class OCCUtils {

	public static boolean isEmpty(TopoDS_Shape s) {
		if (s == null) return true;
		TopExp_Explorer explorer = new TopExp_Explorer();
		explorer.init(s, TopAbs_ShapeEnum.FACE); 
		boolean haveFaces = explorer.more();
		explorer.init(s, TopAbs_ShapeEnum.EDGE);
		boolean haveEdges = explorer.more();
		return !(haveFaces || haveEdges);
	}
	
	public static TopoDS_Shape haveCommon(final TopoDS_Shape currentShape, final int currentType, final TopoDS_Shape s, final int sType) {
		
		TopoDS_Shape common = new BRepAlgoAPI_Common(currentShape, s).shape();
		
		if (OCCUtils.isEmpty(common)) return null;
		
		TopoDS_Shape someShape = common;
		
		if (sType > currentType) {
			someShape = new BRepAlgoAPI_Cut(currentShape, s).shape();
		}
		
		if (sType < currentType) {
			someShape = new BRepAlgoAPI_Cut(s, currentShape).shape();
		}
		
		if (OCCUtils.isEmpty(someShape)) {
			return null;
		}
		
		return common;
	}
	
//	public boolean isIntersecting(final Shape currentShape, final Shape s) {
//		return OCCUtils.haveCommon(currentShape, s) != null;
//	}
	
	public static List<float[]> createEdgeArrays(final TopoDS_Shape topoDS_Shape) {	
		
		final List edgeArrays = new ArrayList<float[]>();
		
		TopExp_Explorer explorer = new TopExp_Explorer();
		HashSet<TopoDS_Edge> alreadyDone=new HashSet<TopoDS_Edge>();
	    Bnd_Box box = new Bnd_Box(); 
		BRepBndLib.add(topoDS_Shape, box);
		
		double[] bbox = box.get();
	    //double[] bbox=computeBoundingBox();
		
		double boundingBoxDeflection=0.0005*
			Math.max(Math.max(bbox[3]-bbox[0], bbox[4]-bbox[1]), bbox[5]-bbox[2]);

		for (explorer.init(topoDS_Shape, TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
		{
		    TopoDS_Shape s = explorer.current();		    
		    if (!(s instanceof TopoDS_Edge)) continue; // should not happen!
		    TopoDS_Edge e = (TopoDS_Edge)s;
		    
		    if(!alreadyDone.add(e))
		    	continue;
						
			double[] range = BRep_Tool.range(e);
		    Geom_Curve gc = BRep_Tool.curve(e, range);
		    float[] array;
		    if(gc!=null)
		    {
			    GeomAdaptor_Curve adaptator = new GeomAdaptor_Curve(gc);
				GCPnts_UniformDeflection deflector = new GCPnts_UniformDeflection();

				deflector.initialize(adaptator, boundingBoxDeflection, range[0], range[1]);
				int npts = deflector.nbPoints();
				
				// Allocate one additional point at each end  = parametric value 0, 1
				array = new float[(npts+2)*3];		    
			    int j=0;
			    double[] values = adaptator.value(range[0]);
			    array[j++] = (float) values[0];
			    array[j++] = (float) values[1];
			    array[j++] = (float) values[2];
			    // All intermediary points
				for (int i=0; i<npts; ++i) {
				    values = adaptator.value(deflector.parameter(i+1));
				    array[j++] = (float) values[0];
				    array[j++] = (float) values[1];
				    array[j++] = (float) values[2];
				}
				// Add last point
			    values = adaptator.value(range[1]);
			    array[j++] = (float) values[0];
			    array[j++] = (float) values[1];
			    array[j++] = (float) values[2];
			    edgeArrays.add(array);
		    }
		    else
		    {
		    	if (!BRep_Tool.degenerated(e))
		    	{
				    // So, there is no curve, and the edge is not degenerated?
				    // => draw lines between the vertices and ignore curvature  
				    // best approximation we can do
					ArrayList<double[]> aa = new ArrayList<double[]>(); // store points here
					for (TopExp_Explorer explorer2 = new TopExp_Explorer(s, TopAbs_ShapeEnum.VERTEX);
						explorer2.more(); explorer2.next())
					{
					    TopoDS_Shape sv = explorer2.current();
					    if (!(sv instanceof TopoDS_Vertex)) continue; // should not happen!
					    TopoDS_Vertex v = (TopoDS_Vertex)sv;
					    aa.add(BRep_Tool.pnt(v));
					}
					array = new float[aa.size()*3];
					for(int i=0, j=0; i<aa.size(); i++)
					{
						double[] f=aa.get(i);
						array[j++]=(float) f[0];
						array[j++]=(float) f[1];
						array[j++]=(float) f[2];
					}
					edgeArrays.add(array);
				}
		    }
		}
		
		return edgeArrays;
	}
	
	public static List<FaceMesh> createFaceMeshes(final TopoDS_Shape topoDS_Shape) {	
		
		int meshIter = 3;
		TopExp_Explorer explorer = new TopExp_Explorer();
		TopLoc_Location loc = new TopLoc_Location();
		
		List<FaceMesh> faceMeshes = new ArrayList<FaceMesh>();
				
		for (explorer.init(topoDS_Shape, TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
		{						
			TopoDS_Shape s = explorer.current();
			if (!(s instanceof TopoDS_Face)) continue; // should not happen!
			TopoDS_Face face = (TopoDS_Face)s;
			Poly_Triangulation pt = BRep_Tool.triangulation(face,loc);
			
			float error=0.001f*getMaxBound(s)*4;
			//float error=0.0001f;
			int iter=0;
			while((pt==null)&(iter<meshIter)){
				new BRepMesh_IncrementalMesh(face,error, false);
				//new BRepMesh_IncrementalMesh(face,error, true);
				pt = BRep_Tool.triangulation(face,loc);				
				error/=10;
				iter++;
			}
						
			if (pt==null)
			{
				System.err.println("Triangulation failed for face "+face+". Trying other mesh parameters.");
				faceMeshes.add(new FaceMesh(new float[0], new int[0]));
				continue;
	
			}		
			
			double[] dnodes = pt.nodes();
			final int[] itriangles = pt.triangles();						
			if(face.orientation()==TopAbs_Orientation.REVERSED)
			{
				reverseMesh(itriangles);
			}
			

			final float[] fnodes=new float[dnodes.length];			
			
			if(loc.isIdentity())
			{
				for(int i=0; i<dnodes.length; i++)
				{
					fnodes[i]=(float) dnodes[i];
				}				
			}
			else
				transformMesh(loc, dnodes, fnodes);
				
			faceMeshes.add(new FaceMesh(fnodes, itriangles));
		}
		
		return faceMeshes;
	}
	
	/**
	 * Compute the bounding box of the shape and
	 * return the maximum bound value
	 * @param shape
	 * @return
	 */
	public static float getMaxBound(final TopoDS_Shape shape){
		Bnd_Box box = new Bnd_Box(); 
		BRepBndLib.add(shape,box);
		double[] bbox = box.get();
		double minBoundingBox=
			Math.max(Math.max(bbox[3]-bbox[0], bbox[4]-bbox[1]), bbox[5]-bbox[2]);
		return (float)minBoundingBox;
	}
	
	/**
	 * @param itriangles
	 */
	public static void reverseMesh(final int[] itriangles)
	{
		int tmp;
		for(int i=0; i<itriangles.length; i+=3)
		{
			tmp=itriangles[i];
			itriangles[i]=itriangles[i+1];
			itriangles[i+1]=tmp;
		}
	}
	
	public static void transformMesh(final TopLoc_Location loc, double[] src, float[] dst)
	{
		double[] matrix=new double[16];
		loc.transformation().getValues(matrix);
		
		Matrix4d m4d=new Matrix4d(matrix);
		Point3d p3d=new Point3d();
		
		for(int i=0; i<src.length; i+=3)
		{
			p3d.x=src[i+0];
			p3d.y=src[i+1];
			p3d.z=src[i+2];
			m4d.transform(p3d);
			dst[i+0]=(float) p3d.x;
			dst[i+1]=(float) p3d.y;
			dst[i+2]=(float) p3d.z;
		}		
	}
	
	public static List<Vertex> getShapeVertices(final TopoDS_Shape shape, final double meshSize) {
		
		final TopExp_Explorer explorer = new TopExp_Explorer();
		
		final List<Vertex> vertexMeshSize = new ArrayList<Vertex>();
		
		for (explorer.init(shape, TopAbs_ShapeEnum.VERTEX); explorer.more(); explorer.next()) {						
			TopoDS_Shape sh = explorer.current();			
			TopoDS_Vertex vertex = (TopoDS_Vertex)sh;				
			vertexMeshSize.add(new Vertex(vertex, meshSize));				
		}
		
		return vertexMeshSize;
	}
	
	public static List<Edge> getShapeEdges(final TopoDS_Shape shape) {
		
		final TopExp_Explorer explorer = new TopExp_Explorer();
		
		final List<Edge> edgeMeshSize = new ArrayList<Edge>();
		
		for (explorer.init(shape, TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next()) {						
			TopoDS_Shape sh = explorer.current();			
			TopoDS_Edge edge = (TopoDS_Edge)sh;				
			edgeMeshSize.add(new Edge(edge));				
		}
		
		return edgeMeshSize;
	}
	
	
}
