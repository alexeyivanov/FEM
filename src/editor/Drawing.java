package editor;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.vecmath.*;

import org.jcae.opencascade.jni.*;

import com.sun.j3d.utils.image.TextureLoader;

public class Drawing {
	
	private Point3d position = new Point3d(0, 0, 0);
	private Vector3d direction = new Vector3d(0, 1, 0);
	VisualSettings vs;
	private boolean checkIntersection = true, virtualDrawingMode = false;
	private Shape lastEdge = null;
	private Point3d firstPoint = new Point3d(0,0,0);
	
	private ArrayList<Shape> shapes = new ArrayList<Shape>(); 
	private ArrayList<Shape> common = new ArrayList<Shape>(); 
	
	public Drawing() {
		vs = new VisualSettings(Color.GREEN, Color.BLACK, 1, null, 0);
	}
	
	public Visible[] getPicture() {
		Visible[] arr = new Visible[shapes.size()];
		for (int i = 0; i < shapes.size(); i++) arr[i] = shapes.get(i);
		return arr;
	}
	
	public Shape add(Shape shape) {
		if (virtualDrawingMode) return shape;
		if (checkIntersection) common.clear();
		if (checkIntersection) return checkIntersection(shape); else shapes.add(shape);
		return shape;
	}
	
	public void add(ArrayList<Shape> newShapes) {
		for (Shape s: newShapes) {
			add(s);
		}
	}
	
	public void add(Shape[] newShapes) {
		for (Shape s: newShapes) {
			add(s);
		}
	}
	
	public void add(Drawing d) {
		for (Shape s : d.shapes) {
			add(s);
		}
	}
		
	private Shape checkIntersection(Shape shape) {
		int i = 0;
		while (i < shapes.size()) {
			Shape s = shapes.get(i);
			TopoDS_Shape common = shape.haveCommon(s); 
			if (common != null) {
				TopoDS_Shape s1 = null;
				if (shape.getType() >= s.getType())
					s1 = new BRepAlgoAPI_Cut(s.getShape(), shape.getShape()).shape();
				else s1 = s.getShape();
				TopoDS_Shape s2 = common;
				TopoDS_Shape s3 = null;
				if (s.getType() >= shape.getType())
					s3 = new BRepAlgoAPI_Cut(shape.getShape(), s.getShape()).shape();
				else s3 = shape.getShape();
				boolean s1empty = OCCUtils.isEmpty(s1), 
				        s2empty = OCCUtils.isEmpty(s2),
				        s3empty = OCCUtils.isEmpty(s3);
				if (s1empty) { // s inside shape
					if (!s3empty) {
						Shape toReturn = checkIntersection(new Shape(shape.getType(), s3, shape.getVisualSettings()));
						toReturn.cutted = true;
						return toReturn;
					}
					return null;
				}
				if (s3empty) { // shape inside s
					if (!s1empty) shapes.get(i).setShape(s1);
					shapes.add(shape);
					return shape;
				}
				//intersection
				if (!s1empty) shapes.get(i).setShape(s1);
				if (!s2empty) {
					Shape commonShape = new Shape(s.getType(), s2, s.getVisualSettings());
					this.common.add(commonShape);
					shapes.add(commonShape);
				}
				if (!s3empty) {
					Shape toReturn = checkIntersection(new Shape(shape.getType(), s3, shape.getVisualSettings()));
					toReturn.cutted = true;
					return toReturn;
				}
				return null;
 					
			}
			i++;
		}
		shapes.add(shape);
		return shape;
	}
	
	public ArrayList<Shape> getCommon() {
		return common;
	}
	
	public void delete(Shape s) {
		shapes.remove(s);
	}
	
	public void delete(ArrayList<Shape> delShapes) {
		for (Shape s: delShapes) {
			delete(s);
		}
	}
	
	public void delete(Shape[] delShapes) {
		for (Shape s: delShapes) {
			delete(s);
		}
	}
	
	public void load(String fileName) {
		BRep_Builder bb=new BRep_Builder();
		TopoDS_Shape s = BRepTools.read(fileName, bb);
		Shape shape = new Shape(Shape.COMPOUND, s, vs);
		explode(shape);		
	}
	
	public void save(String fileName) {
		BRep_Builder bb=new BRep_Builder();
		TopoDS_Compound compound=new TopoDS_Compound();
		bb.makeCompound(compound);
		for (Shape s : shapes)
			bb.add(compound, s.getShape());
		BRepTools.write(compound, fileName);

	}
	
	public void explode(Shape s) {
		TopExp_Explorer explorer = new TopExp_Explorer();
		if (s.type == Shape.COMPOUND) {
			shapes.remove(s);
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Solid)) continue; // should not happen!
				TopoDS_Solid solid = (TopoDS_Solid)sh;
				Shape shape = new Shape(Shape.SOLID, solid, s.getVisualSettings());
				shapes.add(shape);
			}
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.FACE, TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
				TopoDS_Face face = (TopoDS_Face)sh;
				Shape shape = new Shape(Shape.FACE, face, s.getVisualSettings());
				shapes.add(shape);
			}
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.EDGE, TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				Shape shape = new Shape(Shape.EDGE, edge, s.getVisualSettings());
				shapes.add(shape);
			}
		}
		
		if (s.type == Shape.SOLID) {
			shapes.remove(s);
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
				TopoDS_Face face = (TopoDS_Face)sh;
				Shape shape = new Shape(Shape.FACE, face, s.getVisualSettings());
				shapes.add(shape);
			}
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				Shape shape = new Shape(Shape.EDGE, edge, s.getVisualSettings());
				shapes.add(shape);
			}
		}
		
		if (s.type == Shape.FACE) {
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				Shape shape = new Shape(Shape.EDGE, edge, s.getVisualSettings());
				shapes.add(shape);
			}
		}
		
	}
	
	public Shape getEdges(Shape s) {
		TopExp_Explorer explorer = new TopExp_Explorer();		
		TopoDS_Wire wire = null;
		for (explorer.init(s.getShape(), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
		{						
			TopoDS_Shape line = explorer.current();
			if (!(line instanceof TopoDS_Edge)) continue; // should not happen!
			if (wire == null) wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire((TopoDS_Edge)line).shape();
			else wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(wire, (TopoDS_Edge)line).shape();														
		}	
		Shape shape = new Shape(Shape.EDGE, wire, s.getVisualSettings());
		shapes.add(shape);
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
		position.setX(x);
		position.setY(y);
		position.setZ(z);		
	}
	
	public void setDirection(double x, double y, double z) {
		double r = Math.sqrt(x*x+y*y+z*z);
		if (r < 1E-10) return;
		direction.setX(x/r);
		direction.setY(y/r);
		direction.setZ(z/r);
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
	
	public Shape addMesh(Mesh m) {		
		Shape shape = new Shape(Shape.MESH, m, vs);
		shapes.add(shape);
		return shape;
	}
	
	public Shape lineTo(double x, double y, double z) {		
		TopoDS_Edge line =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{position.getX(),position.getY(),position.getZ()},
				new double[]{x,y,z}
				).shape();
		
		Shape shape = null;
		if (lastEdge == null) {
			TopoDS_Wire wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
					line
					).shape();
			shape = new Shape(Shape.EDGE, wire, vs);
			lastEdge = add(shape);
			firstPoint.set(position);
		}
		else {
			shapes.remove(lastEdge);
			TopoDS_Wire wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
					(TopoDS_Wire)lastEdge.getShape(),
					line
					).shape();
			shape = new Shape(Shape.EDGE, wire, vs);
			lastEdge = add(shape);
			
		}
						
		setPos(x,y,z);
		if (Math.sqrt((firstPoint.x-x)*(firstPoint.x-x)+(firstPoint.y-y)*(firstPoint.y-y)+(firstPoint.z-z)*(firstPoint.z-z)) < 1E-6) {
			return makeFace();
		}
		return shape;		
	}
	
	public Shape lineTo(Point3d p) {
		return lineTo(p.x,p.y,p.z);
	}
	
	public Shape line(double l) {
		return lineTo(position.getX() + direction.getX()*l,
				position.getY() + direction.getY()*l,
				position.getZ() + direction.getZ()*l);
	}
	
	public Shape line(double x, double y, double z) {
		return lineTo(position.getX() + x,
				position.getY() + y,
				position.getZ() + z);
	}
	
	public void newLine() {
		lastEdge = null;
	}
	
	private Shape makeFace() {
		shapes.remove(lastEdge);
		TopoDS_Face face=(TopoDS_Face) new BRepBuilderAPI_MakeFace((TopoDS_Wire)lastEdge.getShape()).shape();
		Shape shape = new Shape(Shape.FACE, face, vs);
		newLine();
		return add(shape);
	}
	
	public Shape close() {
		return lineTo(firstPoint.getX(),firstPoint.getY(),firstPoint.getZ());
		
//		if (lastEdge.cutted) return null;
//		boolean save = checkIntersection;
//		checkIntersection = false;
//		lineTo(firstPoint.getX(),firstPoint.getY(),firstPoint.getZ());
//		checkIntersection = save;
//		return makeFace();
	}
	
	public Shape triangle(Point3d a, Point3d b, Point3d c) {
		TopoDS_Edge line1 =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{a.x,a.y,a.z},
				new double[]{b.x,b.y,b.z}
				).shape();
		TopoDS_Edge line2 =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{b.x,b.y,b.z},
				new double[]{c.x,c.y,c.z}
				).shape();
		TopoDS_Edge line3 =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{c.x,c.y,c.z},
				new double[]{a.x,a.y,a.z}
				).shape();
		TopoDS_Wire wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
				line1, line2, line3
				).shape();
		TopoDS_Face face=(TopoDS_Face) new BRepBuilderAPI_MakeFace(wire, true).shape();
		Shape shape = new Shape(Shape.FACE, face, vs);
		return add(shape);
	}
	
	public Shape quadrangle(Point3d a, Point3d b, Point3d c, Point3d d) {
		TopoDS_Edge line1 =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{a.x,a.y,a.z},
				new double[]{b.x,b.y,b.z}
				).shape();
		TopoDS_Edge line2 =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{b.x,b.y,b.z},
				new double[]{c.x,c.y,c.z}
				).shape();
		TopoDS_Edge line3 =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{c.x,c.y,c.z},
				new double[]{d.x,d.y,d.z}
				).shape();
		TopoDS_Edge line4 =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{d.x,d.y,d.z},
				new double[]{a.x,a.y,a.z}
				).shape();
		TopoDS_Wire wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
				line1, line2, line3, line4
				).shape();
		TopoDS_Face face=(TopoDS_Face) new BRepBuilderAPI_MakeFace(wire).shape();
		Shape shape = new Shape(Shape.FACE, face, vs);
		return add(shape);
	}
	
	public Shape rectangleXY(double dx, double dy) {		
		line(dx,0,0);
		line(0,dy,0);
		line(-dx,0,0);
		return close();		
	}
	
	public Shape rectangleXZ(double dx, double dz) {		
		line(dx,0,0);
		line(0,0,dz);
		line(-dx,0,0);
		return close();		
	}
	
	public Shape rectangleYZ(double dy, double dz) {		
		line(0,dy,0);
		line(0,0,dz);
		line(0,-dy,0);
		return close();		
	}
	
//	public Shape extrude(Shape s, double h) {
//		if (s.type == Shape.FACE || s.type == Shape.EDGE) {
//			TopoDS_Shape sh = new BRepPrimAPI_MakePrism(
//					s.shape, 
//					new double[]{direction.getX()*h, direction.getY()*h, direction.getZ()*h}).shape();
//			Shape shape = new Shape(s.type+1, sh, vs);
//			shapes.remove(s);
//			return add(shape);
//		}		
//		return null;
//	}
	
	public Shape extrude(Shape s, double h) {
		TopExp_Explorer explorer = new TopExp_Explorer();
		if (s.type == Shape.FACE) {
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
				TopoDS_Face face = (TopoDS_Face)sh;
				TopoDS_Solid solid = (TopoDS_Solid) new BRepPrimAPI_MakePrism(
						face, 
						new double[]{direction.getX()*h, direction.getY()*h, direction.getZ()*h}).shape();
				Shape shape = new Shape(Shape.SOLID, solid, s.vs);
				shapes.remove(s);
				return add(shape);
			}
		}
		
		if (s.type == Shape.EDGE) {
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				TopoDS_Face face = (TopoDS_Face) new BRepPrimAPI_MakePrism(
						edge, 
						new double[]{direction.getX()*h, direction.getY()*h, direction.getZ()*h}).shape();
				Shape shape = new Shape(Shape.FACE, face, s.vs);
				shapes.remove(s);
				return add(shape);
			}
		}
		return null;
	}
	
	
//	public Shape revolve(Shape s, double angle) {
//		if (s.type == Shape.FACE || s.type == Shape.EDGE) {
//			TopoDS_Shape sh = new BRepPrimAPI_MakeRevol(
//					s.shape, 
//					new double[]{position.getX(),position.getY(),position.getZ(),
//							     direction.getX(), direction.getY(), direction.getZ()},
//							     angle).shape();
//			Shape shape = new Shape(s.type+1, sh, vs);
//			shapes.remove(s);
//			return add(shape);
//		}		
//		return null;
//	}
	
	public Shape revolve(Shape s, double angle) {
		TopExp_Explorer explorer = new TopExp_Explorer();
		if (s.type == Shape.FACE) {
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
				TopoDS_Face face = (TopoDS_Face)sh;
				TopoDS_Solid solid = (TopoDS_Solid) new BRepPrimAPI_MakeRevol(
						face, 
						new double[]{position.getX(),position.getY(),position.getZ(),
								     direction.getX(), direction.getY(), direction.getZ()},
								     angle).shape();
				Shape shape = new Shape(Shape.SOLID, solid, s.vs);
				shapes.remove(s);
				return add(shape);
			}
		}
				
		if (s.type == Shape.EDGE) {
			for (explorer.init(s.getShape(), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				TopoDS_Face face = (TopoDS_Face) new BRepPrimAPI_MakeRevol(
						edge, 
						new double[]{position.getX(),position.getY(),position.getZ(),
								     direction.getX(), direction.getY(), direction.getZ()},
								     angle).shape();
				Shape shape = new Shape(Shape.FACE, face, s.vs);
				shapes.remove(s);
				return add(shape);
			}
		}
		return null;
	}
	
	public Shape fillet(Shape s, double radius) {
		shapes.remove(s);
		Shape last = null;
		TopExp_Explorer explorer = new TopExp_Explorer();
		for (explorer.init(s.getShape(), TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next()) {
			BRepFilletAPI_MakeFillet fillet = new BRepFilletAPI_MakeFillet(s.getShape());
			TopExp_Explorer eexplorer = new TopExp_Explorer();
			for (eexplorer.init((TopoDS_Solid)explorer.current(), TopAbs_ShapeEnum.EDGE); eexplorer.more(); eexplorer.next()) {									
				TopoDS_Edge edge = (TopoDS_Edge)eexplorer.current();
//				TopExp_Explorer vexplorer = new TopExp_Explorer();
//				for (vexplorer.init(edge, TopAbs_ShapeEnum.VERTEX); vexplorer.more(); vexplorer.next()) {
//					TopoDS_Vertex v = (TopoDS_Vertex)vexplorer.current();
//				
//				}				
				fillet.add(radius, edge);
			}
			Shape shape = new Shape(Shape.SOLID, fillet.shape(), s.vs);
			last = add(shape);
		}		
		return last;		
	}
	
	public Shape copy(Shape s, double dx, double dy, double dz) {		
		GP_Trsf trsf = new GP_Trsf();
		trsf.setTranslation(new double[] {dx,dy,dz});
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(s.getShape(),trsf, true);
		TopoDS_Shape newShape=transform.shape();
		Shape shape = new Shape(s.getType(), newShape, s.getVisualSettings());		
		return add(shape);		
	}
	
	public Shape[] copy(Shape[] shapes, double dx, double dy, double dz) {		
		GP_Trsf trsf = new GP_Trsf();
		trsf.setTranslation(new double[] {dx,dy,dz});
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(trsf);
		Shape[] shapes1 = new Shape[shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			Shape s = shapes[i];
			transform.perform(s.getShape(), true);
			TopoDS_Shape newShape=transform.shape();
			Shape shape = new Shape(s.getType(), newShape, s.getVisualSettings());		
			add(shape);
			shapes1[i] = shape;
		}		
		return shapes;
	}
	
	public Shape rotate(Shape s, double angle) {		
		GP_Trsf trsf = new GP_Trsf();
		trsf.setRotation(
				new double[]{position.getX(),position.getY(),position.getZ(),
				         direction.getX(),direction.getY(),direction.getZ()}, angle);
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(s.getShape(),trsf, true);		
		TopoDS_Shape newShape=transform.shape();
		Shape shape = new Shape(s.getType(), newShape, s.getVisualSettings());
		delete(s);
		return add(shape);		
	}
	
	
	public Shape scale(Shape s, double a) {		
		GP_Trsf trsf = new GP_Trsf();		
		double[] matrix=new double[]{
			a, 0, 0, 0,
			0, a, 0, 0,
			0, 0, a, 0	
		};
		trsf.setValues(matrix, 0.0, 0.0);		
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(s.getShape(),trsf, true);		
		TopoDS_Shape newShape=transform.shape();
		Shape shape = new Shape(s.getType(), newShape, s.getVisualSettings());
		delete(s);
		return add(shape);		
	}
	
		
	public Shape array(Shape s, int n, double dx,double dy, double dz) {		
		Shape[] shapes = new Shape[n-1];		
		for (int i = 1; i < n; i++) {
			shapes[i-1] = copy(s, dx*i, dy*i, dz*i);
			if (shapes[i-1].getType() == Shape.COMPOUND) explode(shapes[i-1]); 
		}
		BRep_Builder bb=new BRep_Builder();
		TopoDS_Compound compound=new TopoDS_Compound();
		bb.makeCompound(compound);
		bb.add(compound, s.getShape());
		for (Shape sh: shapes) {
			bb.add(compound, sh.getShape());
		}
		Shape shape = new Shape(Shape.COMPOUND, compound, s.getVisualSettings());
		return shape;			
	}	
	
	
	public Shape circle(double r) {
		GP_Circ gp_circle = new GP_Circ(
				new double[]{position.getX(),position.getY(),position.getZ(),
					         direction.getX(),direction.getY(),direction.getZ()},
					         r);
		TopoDS_Edge circle = (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(gp_circle).shape();
		
		TopoDS_Wire wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire(circle).shape();
		TopoDS_Face face=(TopoDS_Face) new BRepBuilderAPI_MakeFace(wire, true).shape();
		
		Shape shape = new Shape(Shape.FACE, face, vs);
		return add(shape); 
	}
	
	public Shape box(double dx, double dy, double dz) {
		TopoDS_Shape box=new BRepPrimAPI_MakeBox(
				new double[]{position.getX(),position.getY(),position.getZ()},
				new double[]{position.getX()+dx,position.getY()+dy,position.getZ()+dz}
				).shape();
		Shape shape = new Shape(Shape.SOLID, box, vs);
		return add(shape); 
	}
	
	public Shape cylinder(double r, double h, double angle) {
		TopoDS_Shape cylinder=new BRepPrimAPI_MakeCylinder(
				new double[]{position.getX(),position.getY(),position.getZ(),
						     direction.getX(),direction.getY(),direction.getZ()},
				r, h, angle
				).shape();
		Shape shape = new Shape(Shape.SOLID, cylinder, vs);
		return add(shape); 
	}
	
	public Shape torus(double r1, double r2) {
		TopoDS_Shape cylinder=new BRepPrimAPI_MakeTorus(
				new double[]{position.getX(),position.getY(),position.getZ(),
						     direction.getX(),direction.getY(),direction.getZ()},
				r1, r2
				).shape();
		Shape shape = new Shape(Shape.SOLID, cylinder, vs);
		return add(shape); 
	}
	
	
	public Shape cone(double baseRadius, double topRadius, double h, double angle) {
		TopoDS_Shape cylinder=new BRepPrimAPI_MakeCone(
				new double[]{position.getX(),position.getY(),position.getZ(),
						     direction.getX(),direction.getY(),direction.getZ()},
				baseRadius, topRadius, h, angle
				).shape();
		Shape shape = new Shape(Shape.SOLID, cylinder, vs);
		return add(shape); 
	}
	
	public Shape common(Shape s1, Shape s2) {
		shapes.remove(s1);
		shapes.remove(s2);
		TopoDS_Shape s = new BRepAlgoAPI_Common(s1.getShape(), s2.getShape()).shape();
		if (OCCUtils.isEmpty(s)) return null;
		int newType = s1.getType();
		if (s2.getType() < newType) newType = s2.getType();
		Shape shape = new Shape(newType, s, vs);
		shapes.add(shape);
		return shape;
	}
	
	public Shape cut(Shape s1, Shape s2) {
		if (s1.getType() > s2.getType()) return null;
		shapes.remove(s1);
		shapes.remove(s2);
		TopoDS_Shape s = new BRepAlgoAPI_Cut(s1.getShape(), s2.getShape()).shape();
		if (OCCUtils.isEmpty(s)) return null;
		Shape shape = new Shape(s1.getType(), s, s1.getVisualSettings());
		shapes.add(shape);
		return shape;
	}
	
	public Shape fuse(Shape s1, Shape s2) {
		if (s1.getType() != s2.getType()) return null;
		shapes.remove(s1);
		shapes.remove(s2);
		TopoDS_Shape s = new BRepAlgoAPI_Fuse(s1.getShape(), s2.getShape()).shape();
		if (OCCUtils.isEmpty(s)) return null;
		Shape shape = new Shape(s1.getType(), s, s1.getVisualSettings());		
		shapes.add(shape);				
		return shape;
	}
	
//	public Shape section(Shape s1, Shape s2) {
//		shapes.remove(s1);
//		shapes.remove(s2);
//		TopoDS_Shape s = new BRepAlgoAPI_Section(s1.shape, s2.shape).shape();
//		if (Shape.isEmpty(s)) return null;
//		Shape shape = new Shape(Shape.SOLID, s, s1.color, s1.lineColor, s1.lineWidth, s1.texture);
//		add(shape);
//		return shape;
//	}
	
	public void deleteSelected() {
		int i = 0;
		while (i < shapes.size()) {
			if (shapes.get(i).isSelected()) shapes.remove(i);
			else i++;
		}
	}
	
	public void setMeshSize(double size) {
		for (Shape s : shapes) s.setMeshSize(size);		
	}
	
	public void setMeshSize(Shape s, double size) {
		s.setMeshSize(size);		
	}
	
	public void setMeshSize(Shape s, int n) {
		s.setMeshSize(n);		
	}
	
	public void setMeshSize(Shape s, double x, double y, double z, int n) {
		s.setMeshSize(x,y,z,n);		
	}
	
	public void setMeshSize(Shape s, double x, double y, double z, double size) {
		s.setMeshSize(x,y,z,size);		
	}
	
	public void setMeshSize(Shape s, Point3d p, double size) {
		s.setMeshSize(p,size);		
	}
	
	public void meshAll() {			
		for (Shape s : shapes) s.mesh();
	}	
	
	public void meshSelected() {		
		int size = shapes.size();
		int i = 0;
		while (i < size) {
			if (!shapes.get(i).isSelected()) { i++; continue; }			
			addMesh(Mesher.createMesh(shapes.get(i)));
			shapes.remove(i);
			size--;
		}		
	}
	
	public void meshShape(Shape s) {			
		addMesh(Mesher.createMesh(s));
		shapes.remove(s);
	}
	
}
