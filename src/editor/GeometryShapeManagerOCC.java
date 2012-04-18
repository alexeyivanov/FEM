package editor;

import java.util.ArrayList;
import java.util.List;

import org.jcae.opencascade.jni.BRepAlgoAPI_Common;
import org.jcae.opencascade.jni.BRepAlgoAPI_Cut;
import org.jcae.opencascade.jni.BRepAlgoAPI_Fuse;
import org.jcae.opencascade.jni.BRepBuilderAPI_MakeEdge;
import org.jcae.opencascade.jni.BRepBuilderAPI_MakeFace;
import org.jcae.opencascade.jni.BRepBuilderAPI_MakeWire;
import org.jcae.opencascade.jni.BRepBuilderAPI_Transform;
import org.jcae.opencascade.jni.BRepFilletAPI_MakeFillet;
import org.jcae.opencascade.jni.BRepPrimAPI_MakeBox;
import org.jcae.opencascade.jni.BRepPrimAPI_MakeCone;
import org.jcae.opencascade.jni.BRepPrimAPI_MakeCylinder;
import org.jcae.opencascade.jni.BRepPrimAPI_MakePrism;
import org.jcae.opencascade.jni.BRepPrimAPI_MakeRevol;
import org.jcae.opencascade.jni.BRepPrimAPI_MakeTorus;
import org.jcae.opencascade.jni.BRepTools;
import org.jcae.opencascade.jni.BRep_Builder;
import org.jcae.opencascade.jni.GP_Circ;
import org.jcae.opencascade.jni.GP_Trsf;
import org.jcae.opencascade.jni.TopAbs_ShapeEnum;
import org.jcae.opencascade.jni.TopExp_Explorer;
import org.jcae.opencascade.jni.TopoDS_Compound;
import org.jcae.opencascade.jni.TopoDS_Edge;
import org.jcae.opencascade.jni.TopoDS_Face;
import org.jcae.opencascade.jni.TopoDS_Shape;
import org.jcae.opencascade.jni.TopoDS_Solid;
import org.jcae.opencascade.jni.TopoDS_Wire;

import core.Coordinate;
import editor.Shape.FaceMesh;

public class GeometryShapeManagerOCC implements GeometryShapeManager {

	@Override
	public GeometryShape load(String fileName) {

		final BRep_Builder bb = new BRep_Builder();
		final TopoDS_Shape shape = BRepTools.read(fileName, bb);

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(shape);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(shape);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, shape,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape circle(Coordinate position, Coordinate direction,
			double r) {

		final GP_Circ gp_circle = new GP_Circ(new double[] { position.getX(),
				position.getY(), position.getZ(), direction.getX(),
				direction.getY(), direction.getZ() }, r);

		final TopoDS_Edge circle = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				gp_circle).shape();

		final TopoDS_Wire wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
				circle).shape();
		final TopoDS_Face face = (TopoDS_Face) new BRepBuilderAPI_MakeFace(
				wire, true).shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(face);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(face);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, face,
				TopoDS_Shape.class);
	}

	@Override
	public void save(List<GeometryShape> shapes, String fileName) {

		final BRep_Builder bb = new BRep_Builder();
		final TopoDS_Compound compound = new TopoDS_Compound();
		bb.makeCompound(compound);

		for (final GeometryShape s : shapes) {
			
			if(s == null) {
				continue;
			}
			
			bb.add(compound, s.getIntertalShape(TopoDS_Shape.class));
		}

		BRepTools.write(compound, fileName);
	}

	@Override
	public GeometryShape fuse(GeometryShape s1, GeometryShape s2) {

		final TopoDS_Shape shape = new BRepAlgoAPI_Fuse(
				s1.getIntertalShape(TopoDS_Shape.class),
				s2.getIntertalShape(TopoDS_Shape.class)).shape();

		if (OCCUtils.isEmpty(shape)) {
			return null;
		}

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(shape);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(shape);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, shape,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape common(GeometryShape s1, GeometryShape s2) {

		final TopoDS_Shape shape = new BRepAlgoAPI_Common(
				s1.getIntertalShape(TopoDS_Shape.class),
				s2.getIntertalShape(TopoDS_Shape.class)).shape();

		if (OCCUtils.isEmpty(shape)) {
			return null;
		}

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(shape);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(shape);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, shape,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape cut(GeometryShape s1, GeometryShape s2) {

		final TopoDS_Shape shape = new BRepAlgoAPI_Cut(
				s1.getIntertalShape(TopoDS_Shape.class),
				s2.getIntertalShape(TopoDS_Shape.class)).shape();

		if (OCCUtils.isEmpty(shape)) {
			return null;
		}

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(shape);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(shape);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, shape,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape getEdges(GeometryShape shape) {

		TopExp_Explorer explorer = new TopExp_Explorer();		
		TopoDS_Wire wire = null;
		for (explorer.init(shape.getIntertalShape(TopoDS_Shape.class), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
		{						
			TopoDS_Shape line = explorer.current();
			if (!(line instanceof TopoDS_Edge)) continue; // should not happen!
			if (wire == null) wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire((TopoDS_Edge)line).shape();
			else wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(wire, (TopoDS_Edge)line).shape();														
		}	
		
		return buildGeometryShape(wire);
	}

	@Override
	public GeometryShape lineTo(Coordinate position, double x, double y, double z) {
		
		final TopoDS_Edge line =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{position.getX(),position.getY(),position.getZ()},
				new double[]{x,y,z}
				).shape();
		
		
		return buildGeometryShape(line);
	}
	
	@Override
	public GeometryShape wire(GeometryShape line) {
		
		TopoDS_Wire wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(line.getIntertalShape(TopoDS_Edge.class))
				.shape();
		
		return buildGeometryShape(wire);
	}
	
		@Override
	public GeometryShape wire(GeometryShape edge, GeometryShape line) {

		TopoDS_Wire wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
				(TopoDS_Wire) edge.getIntertalShape(TopoDS_Shape.class), line.getIntertalShape(TopoDS_Edge.class))
				.shape();
			
		return buildGeometryShape(wire);
	}

	@Override
	public GeometryShape makeFace(GeometryShape lastEdge) {

		final TopoDS_Face face = (TopoDS_Face) new BRepBuilderAPI_MakeFace(
				(TopoDS_Wire) lastEdge.getIntertalShape(TopoDS_Shape.class)).shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(face);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(face);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, face,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape triangle(Coordinate a, Coordinate b, Coordinate c) {

		final TopoDS_Edge line1 = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[] { a.getX(), a.getY(), a.getZ() }, new double[] {
						b.getX(), b.getY(), b.getZ() }).shape();

		final TopoDS_Edge line2 = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[] { b.getX(), b.getY(), b.getZ() }, new double[] {
						c.getX(), c.getY(), c.getZ() }).shape();

		final TopoDS_Edge line3 = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[] { c.getX(), c.getY(), c.getZ() }, new double[] {
						a.getX(), a.getY(), a.getZ() }).shape();

		final TopoDS_Wire wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
				line1, line2, line3).shape();

		final TopoDS_Face face = (TopoDS_Face) new BRepBuilderAPI_MakeFace(
				wire, true).shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(face);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(face);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, face,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape quadrangle(Coordinate a, Coordinate b, Coordinate c,
			Coordinate d) {

		final TopoDS_Edge line1 = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[] { a.getX(), a.getY(), a.getZ() }, new double[] {
						b.getX(), b.getY(), b.getZ() }).shape();

		final TopoDS_Edge line2 = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[] { b.getX(), b.getY(), b.getZ() }, new double[] {
						c.getX(), c.getY(), c.getZ() }).shape();

		final TopoDS_Edge line3 = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[] { c.getX(), c.getY(), c.getZ() }, new double[] {
						d.getX(), d.getY(), d.getZ() }).shape();

		final TopoDS_Edge line4 = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[] { d.getX(), d.getY(), d.getZ() }, new double[] {
						a.getX(), a.getY(), a.getZ() }).shape();

		final TopoDS_Wire wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
				line1, line2, line3, line4).shape();

		final TopoDS_Face face = (TopoDS_Face) new BRepBuilderAPI_MakeFace(wire)
				.shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(face);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(face);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, face,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape copy(GeometryShape shape, Coordinate translation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeometryShape copy(VisualShape s, double dx, double dy, double dz) {

		GP_Trsf trsf = new GP_Trsf();
		trsf.setTranslation(new double[] { dx, dy, dz });
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(
				s.getShape2(TopoDS_Shape.class), trsf, true);
		TopoDS_Shape newShape = transform.shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(newShape);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(newShape);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, newShape,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape[] copy(GeometryShape[] shapes, double dx, double dy,
			double dz) {

		final GP_Trsf trsf = new GP_Trsf();
		trsf.setTranslation(new double[] { dx, dy, dz });

		final BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(
				trsf);

		final GeometryShape[] shapes1 = new GeometryShape[shapes.length];

		for (int i = 0; i < shapes.length; i++) {
			GeometryShape s = shapes[i];
			transform.perform(s.getIntertalShape(TopoDS_Shape.class), true);
			final TopoDS_Shape newShape = transform.shape();

			final List<FaceMesh> faceMeshes = OCCUtils
					.createFaceMeshes(newShape);
			final List<float[]> edgeArrays = OCCUtils
					.createEdgeArrays(newShape);

			shapes1[i] = new GeometryShapeImpl(faceMeshes, edgeArrays,
					newShape, TopoDS_Shape.class);
		}

		return shapes1;
	}

	@Override
	public GeometryShape rotate(Coordinate position, Coordinate direction,
			GeometryShape s, double angle) {

		final GP_Trsf trsf = new GP_Trsf();
		trsf.setRotation(
				new double[] { position.getX(), position.getY(),
						position.getZ(), direction.getX(), direction.getY(),
						direction.getZ() }, angle);

		final BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(
				s.getIntertalShape(TopoDS_Shape.class), trsf, true);
		TopoDS_Shape newShape = transform.shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(newShape);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(newShape);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, newShape,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape scale(GeometryShape s, double a) {

		final GP_Trsf trsf = new GP_Trsf();
		double[] matrix = new double[] { a, 0, 0, 0, 0, a, 0, 0, 0, 0, a, 0 };

		trsf.setValues(matrix, 0.0, 0.0);

		final BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(
				s.getIntertalShape(TopoDS_Shape.class), trsf, true);
		final TopoDS_Shape newShape = transform.shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(newShape);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(newShape);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, newShape,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape box(Coordinate position, double dx, double dy,
			double dz) {

		final TopoDS_Shape box = new BRepPrimAPI_MakeBox(new double[] {
				position.getX(), position.getY(), position.getZ() },
				new double[] { position.getX() + dx, position.getY() + dy,
						position.getZ() + dz }).shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(box);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(box);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, box,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape cylinder(Coordinate position, Coordinate direction,
			double r, double h, double angle) {

		TopoDS_Shape cylinder = new BRepPrimAPI_MakeCylinder(new double[] {
				position.getX(), position.getY(), position.getZ(),
				direction.getX(), direction.getY(), direction.getZ() }, r, h,
				angle).shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(cylinder);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(cylinder);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, cylinder,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape torus(Coordinate position, Coordinate direction,
			double r1, double r2) {

		final TopoDS_Shape cylinder = new BRepPrimAPI_MakeTorus(new double[] {
				position.getX(), position.getY(), position.getZ(),
				direction.getX(), direction.getY(), direction.getZ() }, r1, r2)
				.shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(cylinder);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(cylinder);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, cylinder,
				TopoDS_Shape.class);
	}

	@Override
	public GeometryShape cone(Coordinate position, Coordinate direction,
			double baseRadius, double topRadius, double h, double angle) {

		final TopoDS_Shape cylinder = new BRepPrimAPI_MakeCone(new double[] {
				position.getX(), position.getY(), position.getZ(),
				direction.getX(), direction.getY(), direction.getZ() },
				baseRadius, topRadius, h, angle).shape();

		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(cylinder);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(cylinder);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, cylinder,
				TopoDS_Shape.class);
	}

	@Override
	public List<GeometryShape> fillet(GeometryShape s, double radius) {

		final List<GeometryShape> geometryShapes = new ArrayList<GeometryShape>();

		final TopExp_Explorer explorer = new TopExp_Explorer();

		for (explorer.init(s.getIntertalShape(TopoDS_Shape.class),
				TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next()) {
			BRepFilletAPI_MakeFillet fillet = new BRepFilletAPI_MakeFillet(
					s.getIntertalShape(TopoDS_Shape.class));
			TopExp_Explorer eexplorer = new TopExp_Explorer();
			for (eexplorer.init((TopoDS_Solid) explorer.current(),
					TopAbs_ShapeEnum.EDGE); eexplorer.more(); eexplorer.next()) {
				TopoDS_Edge edge = (TopoDS_Edge) eexplorer.current();
				// TopExp_Explorer vexplorer = new TopExp_Explorer();
				// for (vexplorer.init(edge, TopAbs_ShapeEnum.VERTEX);
				// vexplorer.more(); vexplorer.next()) {
				// TopoDS_Vertex v = (TopoDS_Vertex)vexplorer.current();
				//
				// }
				fillet.add(radius, edge);
			}

			final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(fillet
					.shape());
			final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(fillet
					.shape());
			geometryShapes.add(new GeometryShapeImpl(faceMeshes, edgeArrays,
					fillet.shape(), TopoDS_Shape.class));
		}

		return geometryShapes;
	}

	@Override
	public GeometryShape revolveFace(Coordinate position, Coordinate direction,
			GeometryShape s, double angle) {

		GeometryShape result = null;

		final TopExp_Explorer explorer = new TopExp_Explorer();

		for (explorer.init(s.getIntertalShape(TopoDS_Shape.class),
				TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next()) {
			TopoDS_Shape sh = explorer.current();
			if (!(sh instanceof TopoDS_Face))
				continue; // should not happen!
			TopoDS_Face face = (TopoDS_Face) sh;
			TopoDS_Solid solid = (TopoDS_Solid) new BRepPrimAPI_MakeRevol(face,
					new double[] { position.getX(), position.getY(),
							position.getZ(), direction.getX(),
							direction.getY(), direction.getZ() }, angle)
					.shape();

			final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(solid);
			final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(solid);

			result = new GeometryShapeImpl(faceMeshes, edgeArrays, solid,
					TopoDS_Shape.class);
			break;
		}

		return result;
	}

	@Override
	public GeometryShape revolveEdge(Coordinate position, Coordinate direction,
			GeometryShape s, double angle) {

		GeometryShape result = null;

		final TopExp_Explorer explorer = new TopExp_Explorer();

		for (explorer.init(s.getIntertalShape(TopoDS_Shape.class),
				TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next()) {
			TopoDS_Shape sh = explorer.current();
			if (!(sh instanceof TopoDS_Edge))
				continue; // should not happen!
			TopoDS_Edge edge = (TopoDS_Edge) sh;
			TopoDS_Face face = (TopoDS_Face) new BRepPrimAPI_MakeRevol(edge,
					new double[] { position.getX(), position.getY(),
							position.getZ(), direction.getX(),
							direction.getY(), direction.getZ() }, angle)
					.shape();

			final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(face);
			final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(face);

			result = new GeometryShapeImpl(faceMeshes, edgeArrays, face,
					TopoDS_Shape.class);
			break;
		}

		return result;
	}

	@Override
	public GeometryShape extrudeFace(Coordinate direction, GeometryShape s,
			double h) {

		GeometryShape result = null;

		final TopExp_Explorer explorer = new TopExp_Explorer();

		for (explorer.init(s.getIntertalShape(TopoDS_Shape.class),
				TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next()) {
			TopoDS_Shape sh = explorer.current();
			if (!(sh instanceof TopoDS_Face))
				continue; // should not happen!
			TopoDS_Face face = (TopoDS_Face) sh;
			TopoDS_Solid solid = (TopoDS_Solid) new BRepPrimAPI_MakePrism(face,
					new double[] { direction.getX() * h, direction.getY() * h,
							direction.getZ() * h }).shape();

			final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(solid);
			final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(solid);

			result = new GeometryShapeImpl(faceMeshes, edgeArrays, solid,
					TopoDS_Shape.class);
			break;
		}

		return result;
	}

	@Override
	public GeometryShape extrudeEdge(Coordinate direction, GeometryShape s,
			double h) {

		GeometryShape result = null;

		final TopExp_Explorer explorer = new TopExp_Explorer();

		for (explorer.init(s.getIntertalShape(TopoDS_Shape.class),
				TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next()) {
			
			TopoDS_Shape sh = explorer.current();
			if (!(sh instanceof TopoDS_Edge))
				continue; // should not happen!
			TopoDS_Edge edge = (TopoDS_Edge) sh;
			TopoDS_Face face = (TopoDS_Face) new BRepPrimAPI_MakePrism(edge,
					new double[] { direction.getX() * h, direction.getY() * h,
							direction.getZ() * h }).shape();
			
			final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(face);
			final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(face);

			result = new GeometryShapeImpl(faceMeshes, edgeArrays, face,
					TopoDS_Shape.class);
			break;
		}
		
		return result;
	}

	@Override
	public List<GeometryShape> explodeSolid(GeometryShape s) {
		
		final TopExp_Explorer explorer = new TopExp_Explorer();
		
		final List<GeometryShape> geometryShapes = new ArrayList<GeometryShape>();
		
		for (explorer.init(s.getIntertalShape(TopoDS_Shape.class), TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next())
		{						
			TopoDS_Shape sh = explorer.current();
			if (!(sh instanceof TopoDS_Solid)) continue; // should not happen!
			TopoDS_Solid solid = (TopoDS_Solid)sh;
			
			final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(solid);
			final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(solid);

			geometryShapes.add(new GeometryShapeImpl(faceMeshes, edgeArrays, solid,
					TopoDS_Shape.class));
		}
		
		return geometryShapes;
	}

	@Override
	public List<GeometryShape> explodeFace(GeometryShape s) {
	
		final TopExp_Explorer explorer = new TopExp_Explorer();
		
		final List<GeometryShape> geometryShapes = new ArrayList<GeometryShape>();
		
		for (explorer.init(s.getIntertalShape(TopoDS_Shape.class), TopAbs_ShapeEnum.FACE, TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next())
		{						
			TopoDS_Shape sh = explorer.current();
			if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
			TopoDS_Face face = (TopoDS_Face)sh;
			
			geometryShapes.add(buildGeometryShape(face));
		}
		
		return geometryShapes;
	}

	@Override
	public List<GeometryShape> explodeEdge(GeometryShape s) {

		final TopExp_Explorer explorer = new TopExp_Explorer();
		
		final List<GeometryShape> geometryShapes = new ArrayList<GeometryShape>();
		
		for (explorer.init(s.getIntertalShape(TopoDS_Shape.class), TopAbs_ShapeEnum.EDGE, TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
		{						
			TopoDS_Shape sh = explorer.current();
			if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
			TopoDS_Edge edge = (TopoDS_Edge)sh;
			
			geometryShapes.add(buildGeometryShape(edge));
		}
		
		return geometryShapes;
	}

	@Override
	public GeometryShape compound(GeometryShape s, List<GeometryShape> shapeList) {
		
		final BRep_Builder bb = new BRep_Builder();
		final TopoDS_Compound compound = new TopoDS_Compound();
		bb.makeCompound(compound);
		
		bb.add(compound, s.getIntertalShape(TopoDS_Shape.class));
		
		for (GeometryShape sh: shapeList) {
			bb.add(compound, sh.getIntertalShape(TopoDS_Shape.class));
		}
		
		return buildGeometryShape(compound);
	}
	
	public GeometryShape buildGeometryShape(final TopoDS_Shape shape) {
		
		if(shape == null) {
			return null;
		}
		
		final List<FaceMesh> faceMeshes = OCCUtils.createFaceMeshes(shape);
		final List<float[]> edgeArrays = OCCUtils.createEdgeArrays(shape);

		return new GeometryShapeImpl(faceMeshes, edgeArrays, shape, TopoDS_Shape.class);
	}
	
}
