package editor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.image.TextureLoader;

import core.Coordinate;

public class DrawModelImpl implements DrawModel {
	
	private Coordinate position = new Coordinate(0, 0, 0);
	private Coordinate direction = new Coordinate(0, 1, 0);
	private VisualSettings vs;
	private boolean checkIntersection = true, virtualDrawingMode = false;
	private VisualShape lastEdge = null;
	private Coordinate firstPoint = new Coordinate(0,0,0);
	
	private ArrayList<VisualShape> shapeList = new ArrayList<VisualShape>(); 
	private ArrayList<VisualShape> commonList = new ArrayList<VisualShape>();
	
	private GeometryShapeManager geometryShapeManager;
	private VisualShapeManager visualShapeManager;
	

	@Override
	public GeometryShapeManager getGeometryShapeManager() {
		return geometryShapeManager;
	}

	@Override
	public void setGeometryShapeManager(GeometryShapeManager geometryShapeManager) {
		this.geometryShapeManager = geometryShapeManager;
		
	}

	public VisualShapeManager getDrawShapeModelManager() {
		return visualShapeManager;
	}

	public void setDrawShapeModelManager(VisualShapeManager drawShapeModelManager) {
		this.visualShapeManager = drawShapeModelManager;
	}

	public DrawModelImpl() {
		vs = new VisualSettings(Color.GREEN, Color.BLACK, 1, null, 0);
	}
	
	public void add(ArrayList<VisualShape> newShapes) {
		for (VisualShape s: newShapes) {
			add(s);
		}
	}
	
	public void add(VisualShape[] newShapes) {
		for (VisualShape s: newShapes) {
			add(s);
		}
	}
	
	public void add(DrawModelImpl d) {
		for (VisualShape s : d.shapeList) {
			add(s);
		}
	}
		
	private VisualShape checkIntersection(VisualShape shape) {
		int i = 0;
		
		while (i < shapeList.size()) {
			
			VisualShape s = shapeList.get(i);
			
			GeometryShape common = null;
			
			if(shape.haveCommon(s)) {
				common = geometryShapeManager
						.haveCommon(shape.getGeometryShape(), shape.getType(),
								s.getGeometryShape(), s.getType());
			}
			
//			TopoDS_Shape common = shape.haveCommon(s, TopoDS_Shape.class); 
			
			if (common != null) {
				
				GeometryShape s1 = null;
				
				if (shape.getType() >= s.getType()) {
//					s1 = new BRepAlgoAPI_Cut(s.getShape2(TopoDS_Shape.class), shape.getShape2(TopoDS_Shape.class)).shape();
					s1 = geometryShapeManager.cut(s.getGeometryShape(), shape.getGeometryShape());
				} else {
//					s1 = s.getShape2(TopoDS_Shape.class);
					s1 = s.getGeometryShape();
				}
				
				GeometryShape s2 = common;
				GeometryShape s3 = null;
				
				if (s.getType() >= shape.getType()) {
//					s3 = new BRepAlgoAPI_Cut(shape.getShape2(TopoDS_Shape.class), s.getShape2(TopoDS_Shape.class)).shape();
					s3 = geometryShapeManager.cut(shape.getGeometryShape(), s.getGeometryShape()); 
				} else {
					s3 = shape.getGeometryShape();
				}
				
				boolean s1empty = geometryShapeManager.isEmpty(s1), 
				        s2empty = geometryShapeManager.isEmpty(s2),
				        s3empty = geometryShapeManager.isEmpty(s3);
				
				if (s1empty) { // s inside shape
					if (!s3empty) {
						
						final VisualShape visualShape = visualShapeManager.create(shape.getType(), s3, shape.getVisualSettings());
						VisualShape toReturn = checkIntersection(visualShape);
						toReturn.setCutted(true);
						
						return toReturn;
					}
					return null;
				}
				
				if (s3empty) { // shape inside s
					if (!s1empty) {
						shapeList.get(i).setShape(s1);
					}
					
					shapeList.add(shape);
					
					return shape;
				}
				
				//intersection
				if (!s1empty) {
					shapeList.get(i).setShape(s1);
				}
				
				if (!s2empty) {
					
					final VisualShape geometryShape = visualShapeManager.create(s.getType(), s2, s.getVisualSettings());

					this.commonList.add(geometryShape);
					shapeList.add(geometryShape);
				}
				
				if (!s3empty) {
					
					final VisualShape geometryShape = visualShapeManager.create(shape.getType(), s3, shape.getVisualSettings());
					
					VisualShape toReturn = checkIntersection(geometryShape);
					toReturn.setCutted(true);
					return toReturn;
				}
				
				return null;
 					
			}
			i++;
		}
		
		shapeList.add(shape);
		return shape;
	}
	
	public ArrayList<VisualShape> getCommon() {
		return commonList;
	}
	
	public void delete(VisualShape s) {
		shapeList.remove(s);
	}
	
	public void delete(ArrayList<VisualShape> delShapes) {
		for (VisualShape s: delShapes) {
			delete(s);
		}
	}
	
	public void delete(VisualShape[] delShapes) {
		for (VisualShape s: delShapes) {
			delete(s);
		}
	}
	
	public void load(String fileName) {

		final GeometryShape geometryShape = geometryShapeManager.load(fileName);
		final VisualShape shape = visualShapeManager.create(Shape.COMPOUND, geometryShape, vs);

		explode(shape);		
	}
	
	public void save(String fileName) {
		
		final List<GeometryShape> geometryShapeList = new ArrayList<GeometryShape>();
		for (VisualShape s : shapeList) {
			geometryShapeList.add(s.getGeometryShape());
		}
		
		geometryShapeManager.save(geometryShapeList, fileName);
	}
	
	public void addToShapeList(final List<GeometryShape> geometryShapes, final int type, final VisualSettings vs) {
		
		if(geometryShapes == null) {
			return;
		}
		
		for (int i = 0; i < geometryShapes.size(); i++) {
			final VisualShape visualShape = visualShapeManager.create(type, geometryShapes.get(i), vs);
			add(visualShape);
		}
	}
	
	public void explode(VisualShape s) {
		
		if (s.getType() == Shape.COMPOUND) {
		
			shapeList.remove(s);
			
			final List<GeometryShape> solidGeometryShapes = geometryShapeManager.explodeSolid(s.getGeometryShape());
			final List<GeometryShape> faceGeometryShapes = geometryShapeManager.explodeFace(s.getGeometryShape());
			final List<GeometryShape> edgeGeometryShapes = geometryShapeManager.explodeEdge(s.getGeometryShape());
			
			addToShapeList(solidGeometryShapes, Shape.SOLID, s.getVisualSettings());
			addToShapeList(faceGeometryShapes, Shape.FACE, s.getVisualSettings());
			addToShapeList(edgeGeometryShapes, Shape.EDGE, s.getVisualSettings());
		}
		
		if (s.getType() == Shape.SOLID) {
			
			shapeList.remove(s);
			
			final List<GeometryShape> faceGeometryShapes = geometryShapeManager.explodeFace(s.getGeometryShape());
			final List<GeometryShape> edgeGeometryShapes = geometryShapeManager.explodeEdge(s.getGeometryShape());
			
			addToShapeList(faceGeometryShapes, Shape.FACE, s.getVisualSettings());
			addToShapeList(edgeGeometryShapes, Shape.EDGE, s.getVisualSettings());
		}
		
		if (s.getType() == Shape.FACE) {
			
			final List<GeometryShape> faceGeometryShapes = geometryShapeManager.explodeFace(s.getGeometryShape());
			
			addToShapeList(faceGeometryShapes, Shape.FACE, s.getVisualSettings());
		}
	}
	
	public VisualShape getEdges(VisualShape s) {
		
		final GeometryShape geometryShape = geometryShapeManager.getEdges(s.getGeometryShape());
		final VisualShape shape = visualShapeManager.create(Shape.EDGE, geometryShape, s.getVisualSettings());
		
		shapeList.add(shape);
		return shape;		
	}
	
	
	/*******************************
	 * Drawing API
	 ******************************/
	
	public void setCheckIntersection(boolean v) {
		checkIntersection = v;
	}
	
	public void setVirtualDrawingMode(boolean v) {
		virtualDrawingMode = v;
	}
		
	public void setFaceColor(Color c) {
		vs.setFaceColor(c);
	}
	
	public void setTransparency(double t) {
		vs.setTransparency(t);
	}
	
	public void setLineColor(Color c) {
		vs.setLineColor(c);
		newLine();
	}
	
	public void setLineWidth(float w) {
		vs.setLineWidth(w);
		newLine();
	}
	
	public boolean setTexture(String filename) {
		TextureLoader loader = new TextureLoader(filename, null);
		ImageComponent2D image = loader.getImage();
		if(image == null) {
			vs.texture = null;
			return false;
		}
		vs.texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
				image.getWidth(), image.getHeight());
		vs.texture.setImage(0, image);
		vs.texture.setEnable(true);
		return true;
	}
	
	public void noTexture() {
		vs.texture = null;
	}
	
	private void setPos(double x, double y, double z) {
		position.setXYZ(x, y, z);
	}
	
	public void setDirection(double x, double y, double z) {
		double r = Math.sqrt(x*x+y*y+z*z);
		if (r < 1E-10) return;
		direction.setXYZ(x/r, y/r, z/r);
	}
	
	public void moveTo(double x, double y, double z) {
		setPos(x,y,z);
		newLine();
	}
	
	public void move(double l) {
		moveTo(position.getX() + direction.getX()*l,
				position.getY() + direction.getY()*l,
				position.getZ() + direction.getZ()*l);
	}
	
	public void move(double x, double y, double z) {
		moveTo(position.getX() + x,
				position.getY() + y,
				position.getZ() + z);
	}
	
	public VisualShape addMesh(Mesh m) {		
		VisualShape shape = new VisualShapeImpl(Shape.MESH, m, vs);
		shapeList.add(shape);
		return shape;
	}
	
	public VisualShape lineTo(double x, double y, double z) {		
		
		final GeometryShape lineGeometryShape = geometryShapeManager.lineTo(position, x, y, z);
		
		VisualShape shape = null;
		
		if (lastEdge == null) {
			
			final GeometryShape geometryShape = geometryShapeManager.wire(lineGeometryShape);
			shape = visualShapeManager.create(Shape.EDGE, geometryShape, vs);
			
			lastEdge = add(shape);
			firstPoint.set(position);
		} else {
			shapeList.remove(lastEdge);
			
			final GeometryShape geometryShape = geometryShapeManager.wire(lastEdge.getGeometryShape(), lineGeometryShape);
			shape = visualShapeManager.create(Shape.EDGE, geometryShape, vs);

			lastEdge = add(shape);
		}
						
		setPos(x,y,z);
		
		if (Math.sqrt((firstPoint.getX()-x)*(firstPoint.getX()-x)+(firstPoint.getY()-y)*(firstPoint.getY()-y)+(firstPoint.getZ()-z)*(firstPoint.getZ()-z)) < 1E-6) {
			return makeFace();
		}
		
		return shape;		
	}
	
	public VisualShape lineTo(Point3d p) {
		return lineTo(p.x,p.y,p.z);
	}
	
	public VisualShape line(double l) {
		return lineTo(position.getX() + direction.getX()*l,
				position.getY() + direction.getY()*l,
				position.getZ() + direction.getZ()*l);
	}
	
	public VisualShape line(double x, double y, double z) {
		return lineTo(position.getX() + x,
				position.getY() + y,
				position.getZ() + z);
	}
	
	public void newLine() {
		lastEdge = null;
	}
	
	private VisualShape makeFace() {
		
		shapeList.remove(lastEdge);
		
		final GeometryShape geometryShape = geometryShapeManager.makeFace(lastEdge.getGeometryShape());
		
		final VisualShape shape = visualShapeManager.create(Shape.FACE, geometryShape, vs);
		
		newLine();
		
		return add(shape);
	}
	
	public VisualShape close() {
		return lineTo(firstPoint.getX(),firstPoint.getY(),firstPoint.getZ());
		
//		if (lastEdge.cutted) return null;
//		boolean save = checkIntersection;
//		checkIntersection = false;
//		lineTo(firstPoint.getX(),firstPoint.getY(),firstPoint.getZ());
//		checkIntersection = save;
//		return makeFace();
	}
	
	public VisualShape triangle(Coordinate a, Coordinate b, Coordinate c) {
		
		final GeometryShape face = geometryShapeManager.triangle(a, b, c);
		
		final VisualShape shape = visualShapeManager.create(Shape.FACE, face, vs);
		
		return add(shape);
	}
	
	public VisualShape quadrangle(Coordinate a, Coordinate b, Coordinate c, Coordinate d) {
		
		final GeometryShape face = geometryShapeManager.quadrangle(a, b, c, d);
		
		final VisualShape shape = visualShapeManager.create(Shape.FACE, face, vs);
		
		return add(shape);
	}
	
	public VisualShape rectangleXY(double dx, double dy) {		
		line(dx,0,0);
		line(0,dy,0);
		line(-dx,0,0);
		return close();		
	}
	
	public VisualShape rectangleXZ(double dx, double dz) {		
		line(dx,0,0);
		line(0,0,dz);
		line(-dx,0,0);
		return close();		
	}
	
	public VisualShape rectangleYZ(double dy, double dz) {		
		line(0,dy,0);
		line(0,0,dz);
		line(0,-dy,0);
		return close();		
	}
	
//	public ShapeModel extrude(ShapeModel s, double h) {
//		if (s.getType() == Shape.FACE || s.getType() == Shape.EDGE) {
//			TopoDS_Shape sh = new BRepPrimAPI_MakePrism(
//					s.shape, 
//					new double[]{direction.getX()*h, direction.getY()*h, direction.getZ()*h}).shape();
//			ShapeModel shape = new ShapeModelImpl(s.getType()+1, sh, vs);
//			shapes2.remove(s);
//			return add(shape);
//		}		
//		return null;
//	}
	
	public VisualShape extrude(VisualShape s, double h) {
		
		if (s.getType() == Shape.FACE) {
		
			final GeometryShape geometryShape = geometryShapeManager.extrudeFace(direction, s.getGeometryShape(), h);
			final VisualShape visualShape = visualShapeManager.create(Shape.SOLID, geometryShape, s.getVisualSettings());
			
			shapeList.remove(s);
			return add(visualShape);
		}
		
		if (s.getType() == Shape.EDGE) {
			
			final GeometryShape geometryShape = geometryShapeManager.extrudeEdge(direction, s.getGeometryShape(), h);
			final VisualShape visualShape = visualShapeManager.create(Shape.FACE, geometryShape, s.getVisualSettings());
			
			shapeList.remove(s);
			return add(visualShape);
		}

		return null;
	}
	
	
//	public ShapeModel revolve(ShapeModel s, double angle) {
//		if (s.getType() == Shape.FACE || s.getType() == Shape.EDGE) {
//			TopoDS_Shape sh = new BRepPrimAPI_MakeRevol(
//					s.shape, 
//					new double[]{position.getX(),position.getY(),position.getZ(),
//							     direction.getX(), direction.getY(), direction.getZ()},
//							     angle).shape();
//			ShapeModel shape = new ShapeModelImpl(s.getType()+1, sh, vs);
//			shapes2.remove(s);
//			return add(shape);
//		}		
//		return null;
//	}
	
	public VisualShape revolve(VisualShape s, double angle) {
		
		if (s.getType() == Shape.FACE) {
			
			final GeometryShape geometryShape = geometryShapeManager.revolveFace(position, direction, s.getGeometryShape(), angle);
			final VisualShape visualShape = visualShapeManager.create(Shape.SOLID, geometryShape, s.getVisualSettings());
			
			shapeList.remove(s);
			return add(visualShape);
		}
				
		if (s.getType() == Shape.EDGE) {

			final GeometryShape geometryShape = geometryShapeManager.revolveEdge(position, direction, s.getGeometryShape(), angle);
			final VisualShape visualShape = visualShapeManager.create(Shape.FACE, geometryShape, s.getVisualSettings());

			shapeList.remove(s);
			return add(visualShape);
		}
		
		return null;
	}
	
	public VisualShape fillet(VisualShape s, double radius) {
		shapeList.remove(s);
		
		VisualShape last = null;
		
		final List<GeometryShape> geometryShapes = geometryShapeManager.fillet(s.getGeometryShape(), radius);
		
		for (int i = 0; i < geometryShapes.size(); i++) {
			
			final VisualShape visualShape = visualShapeManager.create(Shape.SOLID, geometryShapes.get(i), s.getVisualSettings());
			add(visualShape);
			last = visualShape;
		}
		
		return last;		
	}
	
	public VisualShape copy(VisualShape s, double dx, double dy, double dz) {		
		
		final GeometryShape geometryShape = geometryShapeManager.copy(s.getGeometryShape(), dx, dy, dz);
		
		final VisualShape shape = visualShapeManager.create(s.getType(), geometryShape, s.getVisualSettings());		
	
		return add(shape);		
	}
	
	public VisualShape[] copy(VisualShape[] shapes, double dx, double dy, double dz) {		
		
		final GeometryShape[] geometryShapeOriginal  = new GeometryShape[shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			geometryShapeOriginal[i] = shapes[i].getGeometryShape();
		}
		
		final GeometryShape[] geometryShapesCopy = geometryShapeManager.copy(geometryShapeOriginal, dx, dy, dz);
		
		final VisualShape[] visualShapes = new VisualShape[geometryShapesCopy.length];
		
		for (int i = 0; i < geometryShapesCopy.length; i++) {
			visualShapes[i] = visualShapeManager.create(shapes[i].getType(), geometryShapesCopy[i], shapes[i].getVisualSettings());
		}
		
		return shapes;
	}
	
	public VisualShape rotate(VisualShape s, double angle) {		
		
		final GeometryShape geometryShape = geometryShapeManager.rotate(position, direction, s.getGeometryShape(), angle);
		final VisualShape shape = visualShapeManager.create(s.getType(), geometryShape, s.getVisualSettings());

		delete(s);
		return add(shape);		
	}
	
	
	public VisualShape scale(VisualShape s, double a) {		
		
		final GeometryShape geometryShape = geometryShapeManager.scale(s.getGeometryShape(), a);
		final VisualShape shape = visualShapeManager.create(s.getType(), geometryShape, vs);
		
		delete(s);
		return add(shape);		
	}
	
		
	public VisualShape array(VisualShape s, int n, double dx,double dy, double dz) {		
		
		VisualShape[] shapes = new VisualShape[n-1];		
		
		for (int i = 1; i < n; i++) {
			
			shapes[i-1] = copy(s, dx*i, dy*i, dz*i);
			
			if (shapes[i-1].getType() == Shape.COMPOUND) { 
				explode(shapes[i-1]); 
			}
		}
		
		final List<GeometryShape> geometryShapeList = new ArrayList<GeometryShape>();
		for (VisualShape vShape : shapeList) {
			geometryShapeList.add(vShape.getGeometryShape());
		}
		
		final GeometryShape compound = geometryShapeManager.compound(s.getGeometryShape(), geometryShapeList);
		
		final VisualShape shape = visualShapeManager.create(Shape.COMPOUND, compound, s.getVisualSettings());
		
		return shape;			
	}	
	
	
	public VisualShape circle(double r) {
		
		final GeometryShape geometryShape = geometryShapeManager.circle(position, direction, r);
		
		final VisualShape shape = visualShapeManager.create(Shape.FACE, geometryShape, vs);
		
		return add(shape); 
	}
	
	public VisualShape box(double dx, double dy, double dz) {
		
		final GeometryShape geometryShape = geometryShapeManager.box(position, dx, dy, dz);
		
		final VisualShape shape = visualShapeManager.create(Shape.SOLID, geometryShape, vs);
		
		return add(shape); 
	}
	
	public VisualShape cylinder(double r, double h, double angle) {
		
		final GeometryShape geometryShape = geometryShapeManager.cylinder(position, direction, r, h, angle);
		
		final VisualShape shape = visualShapeManager.create(Shape.SOLID, geometryShape, vs);
		
		return add(shape); 
	}
	
	public VisualShape torus(double r1, double r2) {
		
		final GeometryShape geometryShape = geometryShapeManager.torus(position, direction, r1, r2);
		
		final VisualShape shape = visualShapeManager.create(Shape.SOLID, geometryShape, vs);
		
		return add(shape); 
	}

	
	public VisualShape cone(double baseRadius, double topRadius, double h, double angle) {
		
		final GeometryShape geometryShape = geometryShapeManager.cone(position, direction, baseRadius, topRadius, h, angle);
		
		final VisualShape shape = visualShapeManager.create(Shape.SOLID, geometryShape, vs);
		
		return add(shape); 
	}
	
	public VisualShape common(VisualShape s1, VisualShape s2) {
		shapeList.remove(s1);
		shapeList.remove(s2);
		
		final GeometryShape geometryShape = geometryShapeManager.common(s1.getGeometryShape(), s2.getGeometryShape());
		
		if(geometryShape == null) {
			return null;
		}
		
		int newType = s1.getType();
		if (s2.getType() < newType) newType = s2.getType();
		
		final VisualShape shape = visualShapeManager.create(newType, geometryShape, vs);
		
		shapeList.add(shape);
		return shape;
	}
	
	public VisualShape cut(VisualShape s1, VisualShape s2) {
		if (s1.getType() > s2.getType()) return null;
		shapeList.remove(s1);
		shapeList.remove(s2);
		
		final GeometryShape geometryShape = geometryShapeManager.cut(s1.getGeometryShape(), s2.getGeometryShape());
		final VisualShape shape = visualShapeManager.create(s1.getType(), geometryShape, s1.getVisualSettings());
		
		shapeList.add(shape);
		return shape;
	}
	
	public VisualShape fuse(VisualShape s1, VisualShape s2) {
		if (s1.getType() != s2.getType()) return null;
		shapeList.remove(s1);
		shapeList.remove(s2);
		
		final GeometryShape geometryShape = geometryShapeManager.fuse(s1.getGeometryShape(), s2.getGeometryShape());
		if(geometryShape == null) {
			return null;
		}
		
		final VisualShape shape = visualShapeManager.create(s1.getType(), geometryShape, vs);
		
		shapeList.add(shape);				
		return shape;
	}
	
//	public ShapeModel section(Shape s1, Shape s2) {
//		shapes2.remove(s1);
//		shapes2.remove(s2);
//		TopoDS_Shape s = new BRepAlgoAPI_Section(s1.shape, s2.shape).shape();
//		if (Shape.isEmpty(s)) return null;
//		ShapeModel shape = new ShapeModelImpl(Shape.SOLID, s, s1.color, s1.lineColor, s1.lineWidth, s1.texture);
//		add(shape);
//		return shape;
//	}
	
	public void deleteSelected() {
		int i = 0;
		while (i < shapeList.size()) {
			if (shapeList.get(i).isSelected()) shapeList.remove(i);
			else i++;
		}
	}
	
	public void setMeshSize(double size) {
		for (VisualShape s : shapeList) s.setMeshSize(size);		
	}
	
	public void setMeshSize(VisualShape s, double size) {
		s.setMeshSize(size);		
	}
	
	public void setMeshSize(VisualShape s, int n) {
		s.setMeshSize(n);		
	}
	
	public void setMeshSize(VisualShape s, double x, double y, double z, int n) {
		s.setMeshSize(x,y,z,n);		
	}
	
	public void setMeshSize(VisualShape s, double x, double y, double z, double size) {
		s.setMeshSize(x,y,z,size);		
	}
	
//	public void setMeshSize(ShapeModel s, Point3d p, double size) {
//		s.setMeshSize(p,size);		
//	}
	
	public void meshAll() {			
		for (VisualShape s : shapeList) s.mesh();
	}	
	
	public void meshSelected() {		
		int size = shapeList.size();
		int i = 0;
		while (i < size) {
			if (!shapeList.get(i).isSelected()) { i++; continue; }			
			addMesh(Mesher.createMesh(shapeList.get(i)));
			shapeList.remove(i);
			size--;
		}		
	}
	
	public void meshShape(VisualShape s) {			
		addMesh(Mesher.createMesh(s));
		shapeList.remove(s);
	}
	
	
	public VisualShape add(VisualShape shape) {
		if (virtualDrawingMode) return shape;
		if (checkIntersection) commonList.clear();
		if (checkIntersection) return checkIntersection(shape); else shapeList.add(shape);
		
		
		shapeList.add(shape);
		
		return shape;
	}

	public VisualShape[] getPicture() {
		VisualShape[] arr = new VisualShape[shapeList.size()];
		for (int i = 0; i < shapeList.size(); i++) arr[i] = shapeList.get(i);
		return arr;
	}
}
