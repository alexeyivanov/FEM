package editor;

import java.util.List;

import org.jcae.opencascade.jni.BRepBuilderAPI_MakeEdge;
import org.jcae.opencascade.jni.BRepBuilderAPI_MakeFace;
import org.jcae.opencascade.jni.BRepBuilderAPI_MakeWire;
import org.jcae.opencascade.jni.BRepPrimAPI_MakeBox;
import org.jcae.opencascade.jni.BRepPrimAPI_MakeCone;
import org.jcae.opencascade.jni.BRepPrimAPI_MakeCylinder;
import org.jcae.opencascade.jni.BRepTools;
import org.jcae.opencascade.jni.BRep_Builder;
import org.jcae.opencascade.jni.GP_Circ;
import org.jcae.opencascade.jni.TopoDS_Compound;
import org.jcae.opencascade.jni.TopoDS_Edge;
import org.jcae.opencascade.jni.TopoDS_Face;
import org.jcae.opencascade.jni.TopoDS_Shape;
import org.jcae.opencascade.jni.TopoDS_Wire;

import editor.Shape.FaceMesh;

public class CommonShapeModelManagerOCC implements CommonShapeModelManager {

	@Override
	public CommonShape circle(Coordinate position, Coordinate direction, double r) {
		
		final GP_Circ gp_circle = new GP_Circ(
				new double[]{position.getX(),position.getY(),position.getZ(),
					         direction.getX(),direction.getY(),direction.getZ()},
					         r);
		
		final TopoDS_Edge circle = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(gp_circle).shape();
		
		final TopoDS_Wire wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(circle).shape();
		final TopoDS_Face face = (TopoDS_Face) new BRepBuilderAPI_MakeFace(wire, true).shape();
		
		
		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(face);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(face);
		
		return new CommonShapeImpl(faceMeshes, edgeArrays, face, TopoDS_Shape.class);
	}

	@Override
	public void save(List<CommonShape> shapes, String fileName) {
		
		final BRep_Builder bb = new BRep_Builder();
		final TopoDS_Compound compound=new TopoDS_Compound();
		bb.makeCompound(compound);
		
		for (final CommonShape s : shapes) {
			bb.add(compound, s.getIntertalShape(TopoDS_Shape.class));
		}
		
		BRepTools.write(compound, fileName);
	}

	@Override
	public CommonShape getEdges(DrawShapeModel shape) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape lineTo(Coordinate position, Coordinate to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape makeFace(CommonShape lastEdge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape triangle(Coordinate a, Coordinate b, Coordinate c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape quadrangle(Coordinate a, Coordinate b, Coordinate c,
			Coordinate d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape copy(CommonShape shape, Coordinate translation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape[] copy(CommonShape[] shapes, Coordinate translation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape rotate(CommonShape s, double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape scale(CommonShape s, double a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape box(Coordinate position, double dx, double dy, double dz) {
		
		final TopoDS_Shape box = new BRepPrimAPI_MakeBox(
				new double[]{position.getX(),position.getY(),position.getZ()},
				new double[]{position.getX()+dx,position.getY()+dy,position.getZ()+dz}
				).shape();
		
		
		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(box);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(box);
		
		return new CommonShapeImpl(faceMeshes, edgeArrays, box, TopoDS_Shape.class);
	}

	@Override
	public CommonShape cylinder(Coordinate position, Coordinate direction,
			double r, double h, double angle) {
		
		TopoDS_Shape cylinder = new BRepPrimAPI_MakeCylinder(
				new double[]{position.getX(),position.getY(),position.getZ(),
						     direction.getX(),direction.getY(),direction.getZ()},
				r, h, angle
				).shape();
		
		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(cylinder);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(cylinder);
		
		return new CommonShapeImpl(faceMeshes, edgeArrays, cylinder, TopoDS_Shape.class);
	}

	@Override
	public CommonShape torus(double r1, double r2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonShape cone(Coordinate position, Coordinate direction, double baseRadius, double topRadius, double h,
			double angle) {
		
		final TopoDS_Shape cylinder = new BRepPrimAPI_MakeCone(
				new double[]{position.getX(),position.getY(),position.getZ(),
						     direction.getX(),direction.getY(),direction.getZ()},
				baseRadius, topRadius, h, angle
				).shape();
		
		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(cylinder);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(cylinder);
		
		return new CommonShapeImpl(faceMeshes, edgeArrays, cylinder, TopoDS_Shape.class); 
	}

}
