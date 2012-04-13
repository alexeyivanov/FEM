package editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

import editor.Mesh.IndArrayList;
import editor.Mesh.MeshPoint;
import editor.Shape.FaceMesh;

public class Java3DUtils {

	public static BranchGroup createFaces(final List<FaceMesh> faceMeshes, final List<Shape3D> faces, final VisualSettings vs) {
		
		Iterator<FaceMesh> it = faceMeshes.iterator();
		
		BranchGroup toReturn=new BranchGroup();
		toReturn.setCapability(BranchGroup.ALLOW_DETACH);
		int n=0;
		
		while(it.hasNext())
		{			
			FaceMesh fm=it.next();
			
			//Case of an unmeshed face
			if(fm.getNodes().length==0){
				n++;
				continue;
			}
			
			int[] reversed = fm.getMesh().clone();
			
			OCCUtils.reverseMesh(reversed);
			
			GeometryInfo gi=new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
			gi.setCoordinates(fm.getNodes());
			gi.setCoordinateIndices(fm.getMesh());			
			NormalGenerator ng = new NormalGenerator();
			ng.generateNormals(gi);
			Stripifier st = new Stripifier();
			st.stripify(gi);
	        
			GeometryArray g=gi.getGeometryArray();
			g.setCapability(GeometryArray.ALLOW_COUNT_READ);
			g.setCapability(GeometryArray.ALLOW_FORMAT_READ);
			g.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
			g.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_READ);	
			 
			Shape3D shape3d=new Shape3D(g);
			shape3d.setAppearance(vs.getFaceAppearance());
			shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);			
			shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
			shape3d.setCapability(Node.ALLOW_PICKABLE_WRITE);
			toReturn.addChild(shape3d);
			faces.add(shape3d);
			
			
			gi=new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
			gi.setCoordinates(fm.getNodes());
			gi.setCoordinateIndices(reversed);
			ng.generateNormals(gi);
			st = new Stripifier();
			st.stripify(gi);
			g=gi.getGeometryArray();
			
			shape3d=new Shape3D(g);
			shape3d.setAppearance(vs.getFaceAppearance());
			shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
			shape3d.setCapability(Node.ALLOW_PICKABLE_WRITE);
			toReturn.addChild(shape3d);
			faces.add(shape3d);

			n++;
		}
		
		return toReturn;
	}
	
	public static BranchGroup createEdges(final List<float[]> edgeArrays, final List<Shape3D> edges, final VisualSettings vs) {
		
		Iterator<float[]> it = edgeArrays.iterator();
		
		BranchGroup toReturn=new BranchGroup();
		toReturn.setCapability(BranchGroup.ALLOW_DETACH);
		int n=0;
		
		while(it.hasNext())
		{
			float[] coordinates=it.next();			
			LineStripArray lsa=new LineStripArray(coordinates.length/3,
				GeometryArray.COORDINATES,
				new int[]{coordinates.length/3});
			
			lsa.setCapability(GeometryArray.ALLOW_COLOR_READ);
			lsa.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
			lsa.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
			lsa.setCapability(GeometryArray.ALLOW_COUNT_READ);
			lsa.setCapability(GeometryArray.ALLOW_FORMAT_READ);
			
			lsa.setCoordinates(0, coordinates);
			Shape3D shape3d=new Shape3D(lsa);
			
			shape3d.setAppearance(vs.getLineAppearance());
			shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
			shape3d.setCapability(Node.ALLOW_PICKABLE_WRITE);
			toReturn.addChild(shape3d);
			edges.add(shape3d);
			n++;
		}
		
		return toReturn;
	}
	
	public static BranchGroup createTextLabels(final ArrayList<MeshPoint> points) {
        
		final Appearance a = new Appearance();
		final Material m = new Material();
		final Color faceColor = Color.BLUE;
		
		final Color3f color = new Color3f(
				((float)faceColor.getRed())/255
				,((float)faceColor.getGreen())/255
				,((float)faceColor.getBlue())/255);
		
		final Color3f gray = new Color3f(0.1f,0.1f,0.1f);

		m.setAmbientColor(gray);
		m.setDiffuseColor(color);
		m.setSpecularColor(gray);
		m.setShininess(1000f);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);			
		a.setMaterial(m);
        
        double scale = 0.3;        
        
        final Font3D font3D = new Font3D(new Font("Helvetica", Font.PLAIN, 1),
                                   new FontExtrusion(new Line2D.Double(0, 0, .05, 0)));                
                    	
        final BranchGroup textBG = new BranchGroup();          
        
        for (final MeshPoint p : points) {
        	
        	if (!p.isSelected()) {
        		continue;
        	}
        	
        	final Text3D textGeom = new Text3D(font3D, Double.toString((double)(Math.round(p.result*1000))/1000));        	
            textGeom.setAlignment(Text3D.ALIGN_FIRST);
         
            final Shape3D textShape = new Shape3D(textGeom);
            textShape.setAppearance(a);
            
            final Transform3D t = new Transform3D();            
            t.setScale(scale);                                   
            t.setTranslation(new Vector3d(p.getXYZ()));            
            
            final TransformGroup tg = new TransformGroup(t);
            textBG.addChild(tg);
            tg.addChild(textShape);
        }
        
        return textBG;
	}
	
	public static Shape3D createFaces(final IndArrayList faceInd, final double[] coordsArray, final Color3f[] colors, final VisualSettings vs) {
		
		final GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
		gi.setCoordinates(coordsArray);	
		int[] faces = faceInd.getArray();
		gi.setCoordinateIndices(faces);
		
		if (colors != null) {
			gi.setColors(colors);
			gi.setColorIndices(faces);
		}
					
//		NormalGenerator ng = new NormalGenerator(0);
//		ng.generateNormals(gi);
		
        
		final GeometryArray g = gi.getGeometryArray();
		g.setCapability(GeometryArray.ALLOW_COUNT_READ);
		g.setCapability(GeometryArray.ALLOW_FORMAT_READ);
		g.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
		g.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_READ);	
		
		final Shape3D shape3d = new Shape3D(g);
		shape3d.setAppearance(vs.getMeshAppearance());
		shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);			
		shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shape3d.setCapability(Node.ALLOW_PICKABLE_WRITE);
		
		return shape3d;
	}
	
	public static Shape3D createEdges(final double[] coordsArray, final IndArrayList lineInd, final VisualSettings vs) {
		
		final IndexedLineArray ila = new IndexedLineArray(coordsArray.length/3,	GeometryArray.COORDINATES,lineInd.size());
		ila.setCoordinates(0, coordsArray);
		ila.setCoordinateIndices(0, lineInd.getArray());
		
		final Shape3D shape3d = new Shape3D(ila);
		shape3d.setAppearance(vs.getLineAppearance());
		shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shape3d.setCapability(Node.ALLOW_PICKABLE_WRITE);
		
		return shape3d;
	}
	
	public static BranchGroup createBranchGroupByShape3D(final Shape3D shape) {
		
		final BranchGroup bg = new BranchGroup();
		bg.addChild(shape);
		
		return bg;
	}
	
	public static Color3f[] createColor3f(final List<MeshPoint> points, final VisualSettings vs, final double minResult, final double maxResult) {
		
		final Color3f[] colors = new Color3f[points.size()];
		
		for (int i = 0; i < points.size(); i++) {
			MeshPoint p = points.get(i);
			p.averageResult();
			if (p.hasResult()) 				
				colors[i] = vs.getScaleColor((float)((p.result-minResult)/(maxResult - minResult)));
			else 
				colors[i] = vs.getJ3DColor(vs.getFaceColor());			
		}	
		
		return colors;
	}
}
