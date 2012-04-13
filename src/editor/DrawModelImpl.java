package editor;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.vecmath.*;

import org.jcae.opencascade.jni.*;

import com.sun.j3d.utils.image.TextureLoader;


public class DrawModelImpl implements DrawModel {
	
	private Coordinate position = new Coordinate(0, 0, 0);
	private Coordinate direction = new Coordinate(0, 1, 0);
	VisualSettings vs;
	private boolean checkIntersection = true, virtualDrawingMode = false;
//	private ShapeModel lastEdge = null;
	private DrawShapeModel lastEdge = null;
	private Coordinate firstPoint = new Coordinate(0,0,0);
	
//	private ArrayList<ShapeModel> shapes = new ArrayList<ShapeModel>(); 
//	private ArrayList<ShapeModel> common = new ArrayList<ShapeModel>(); 
	
	private ArrayList<DrawShapeModel> shapeList = new ArrayList<DrawShapeModel>(); 
	private ArrayList<DrawShapeModel> commonList = new ArrayList<DrawShapeModel>();
	
	private CommonShapeModelManager commonShapeModelManager;
	private DrawShapeModelManager drawShapeModelManager;
	
	public CommonShapeModelManager getCommonShapeModelManager() {
		return commonShapeModelManager;
	}

	public void setCommonShapeModelManager(CommonShapeModelManager shapeModelManager) {
		this.commonShapeModelManager = shapeModelManager;
	}

	public DrawShapeModelManager getDrawShapeModelManager() {
		return drawShapeModelManager;
	}

	public void setDrawShapeModelManager(DrawShapeModelManager drawShapeModelManager) {
		this.drawShapeModelManager = drawShapeModelManager;
	}

	public DrawModelImpl() {
		vs = new VisualSettings(Color.GREEN, Color.BLACK, 1, null, 0);
	}
	
	public void add(ArrayList<DrawShapeModel> newShapes) {
		for (DrawShapeModel s: newShapes) {
			add(s);
		}
	}
	
	public void add(DrawShapeModel[] newShapes) {
		for (DrawShapeModel s: newShapes) {
			add(s);
		}
	}
	
	public void add(DrawModelImpl d) {
		for (DrawShapeModel s : d.shapeList) {
			add(s);
		}
	}
		
	private DrawShapeModel checkIntersection(DrawShapeModel shape) {
		int i = 0;
		while (i < shapeList.size()) {
			DrawShapeModel s = shapeList.get(i);
			TopoDS_Shape common = shape.haveCommon(s, TopoDS_Shape.class); 
			if (common != null) {
				TopoDS_Shape s1 = null;
				if (shape.getType() >= s.getType())
					s1 = new BRepAlgoAPI_Cut(s.getShape2(TopoDS_Shape.class), shape.getShape2(TopoDS_Shape.class)).shape();
				else s1 = s.getShape2(TopoDS_Shape.class);
				TopoDS_Shape s2 = common;
				TopoDS_Shape s3 = null;
				if (s.getType() >= shape.getType())
					s3 = new BRepAlgoAPI_Cut(shape.getShape2(TopoDS_Shape.class), s.getShape2(TopoDS_Shape.class)).shape();
				else s3 = shape.getShape2(TopoDS_Shape.class);
				boolean s1empty = OCCUtils.isEmpty(s1), 
				        s2empty = OCCUtils.isEmpty(s2),
				        s3empty = OCCUtils.isEmpty(s3);
				if (s1empty) { // s inside shape
					if (!s3empty) {
						DrawShapeModel toReturn = checkIntersection(new DrawShapeModelImpl(shape.getType(), s3, shape.getVisualSettings()));
						toReturn.setCutted(true);
						return toReturn;
					}
					return null;
				}
				if (s3empty) { // shape inside s
					if (!s1empty) shapeList.get(i).setShape(s1, TopoDS_Shape.class);
					shapeList.add(shape);
					return shape;
				}
				//intersection
				if (!s1empty) shapeList.get(i).setShape(s1, TopoDS_Shape.class);
				if (!s2empty) {
					DrawShapeModel commonShape = new DrawShapeModelImpl(s.getType(), s2, s.getVisualSettings());
					this.commonList.add(commonShape);
					shapeList.add(commonShape);
				}
				if (!s3empty) {
					DrawShapeModel toReturn = checkIntersection(new DrawShapeModelImpl(shape.getType(), s3, shape.getVisualSettings()));
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
	
	public ArrayList<DrawShapeModel> getCommon() {
		return commonList;
	}
	
	public void delete(DrawShapeModel s) {
		shapeList.remove(s);
	}
	
	public void delete(ArrayList<DrawShapeModel> delShapes) {
		for (DrawShapeModel s: delShapes) {
			delete(s);
		}
	}
	
	public void delete(DrawShapeModel[] delShapes) {
		for (DrawShapeModel s: delShapes) {
			delete(s);
		}
	}
	
	public void load(String fileName) {
		BRep_Builder bb=new BRep_Builder();
		TopoDS_Shape s = BRepTools.read(fileName, bb);
		DrawShapeModel shape = new DrawShapeModelImpl(Shape.COMPOUND, s, vs);
		explode(shape);		
	}
	
	public void save(String fileName) {
		BRep_Builder bb=new BRep_Builder();
		TopoDS_Compound compound=new TopoDS_Compound();
		bb.makeCompound(compound);
		for (DrawShapeModel s : shapeList)
			bb.add(compound, s.getShape2(TopoDS_Shape.class));
		BRepTools.write(compound, fileName);
		
//		shapeModelFactory.save(shapes, fileName);

	}
	
	public void explode(DrawShapeModel s) {
		TopExp_Explorer explorer = new TopExp_Explorer();
		if (s.getType() == Shape.COMPOUND) {
			shapeList.remove(s);
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Solid)) continue; // should not happen!
				TopoDS_Solid solid = (TopoDS_Solid)sh;
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.SOLID, solid, s.getVisualSettings());
				shapeList.add(shape);
			}
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.FACE, TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
				TopoDS_Face face = (TopoDS_Face)sh;
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.FACE, face, s.getVisualSettings());
				shapeList.add(shape);
			}
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.EDGE, TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.EDGE, edge, s.getVisualSettings());
				shapeList.add(shape);
			}
		}
		
		if (s.getType() == Shape.SOLID) {
			shapeList.remove(s);
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
				TopoDS_Face face = (TopoDS_Face)sh;
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.FACE, face, s.getVisualSettings());
				shapeList.add(shape);
			}
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.EDGE, edge, s.getVisualSettings());
				shapeList.add(shape);
			}
		}
		
		if (s.getType() == Shape.FACE) {
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.EDGE, edge, s.getVisualSettings());
				shapeList.add(shape);
			}
		}
		
	}
	
	public DrawShapeModel getEdges(DrawShapeModel s) {
		TopExp_Explorer explorer = new TopExp_Explorer();		
		TopoDS_Wire wire = null;
		for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
		{						
			TopoDS_Shape line = explorer.current();
			if (!(line instanceof TopoDS_Edge)) continue; // should not happen!
			if (wire == null) wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire((TopoDS_Edge)line).shape();
			else wire = (TopoDS_Wire) new BRepBuilderAPI_MakeWire(wire, (TopoDS_Edge)line).shape();														
		}	
		DrawShapeModel shape = new DrawShapeModelImpl(Shape.EDGE, wire, s.getVisualSettings());
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
	
	public DrawShapeModel addMesh(Mesh m) {		
		DrawShapeModel shape = new DrawShapeModelImpl(Shape.MESH, m, vs);
		shapeList.add(shape);
		return shape;
	}
	
	public DrawShapeModel lineTo(double x, double y, double z) {		
		TopoDS_Edge line =  (TopoDS_Edge) new BRepBuilderAPI_MakeEdge(
				new double[]{position.getX(),position.getY(),position.getZ()},
				new double[]{x,y,z}
				).shape();
		
		DrawShapeModel shape = null;
		if (lastEdge == null) {
			TopoDS_Wire wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
					line
					).shape();
			shape = new DrawShapeModelImpl(Shape.EDGE, wire, vs);
			lastEdge = add(shape);
			firstPoint.set(position);
		}
		else {
			shapeList.remove(lastEdge);
			TopoDS_Wire wire =  (TopoDS_Wire) new BRepBuilderAPI_MakeWire(
					(TopoDS_Wire)lastEdge.getShape2(TopoDS_Shape.class),
					line
					).shape();
			shape = new DrawShapeModelImpl(Shape.EDGE, wire, vs);
			lastEdge = add(shape);
			
		}
						
		setPos(x,y,z);
		if (Math.sqrt((firstPoint.getX()-x)*(firstPoint.getX()-x)+(firstPoint.getY()-y)*(firstPoint.getY()-y)+(firstPoint.getZ()-z)*(firstPoint.getZ()-z)) < 1E-6) {
			return makeFace();
		}
		return shape;		
	}
	
	public DrawShapeModel lineTo(Point3d p) {
		return lineTo(p.x,p.y,p.z);
	}
	
	public DrawShapeModel line(double l) {
		return lineTo(position.getX() + direction.getX()*l,
				position.getY() + direction.getY()*l,
				position.getZ() + direction.getZ()*l);
	}
	
	public DrawShapeModel line(double x, double y, double z) {
		return lineTo(position.getX() + x,
				position.getY() + y,
				position.getZ() + z);
	}
	
	public void newLine() {
		lastEdge = null;
	}
	
	private DrawShapeModel makeFace() {
		shapeList.remove(lastEdge);
		TopoDS_Face face=(TopoDS_Face) new BRepBuilderAPI_MakeFace((TopoDS_Wire)lastEdge.getShape2(TopoDS_Shape.class)).shape();
		DrawShapeModel shape = new DrawShapeModelImpl(Shape.FACE, face, vs);
		newLine();
		return add(shape);
	}
	
	public DrawShapeModel close() {
		return lineTo(firstPoint.getX(),firstPoint.getY(),firstPoint.getZ());
		
//		if (lastEdge.cutted) return null;
//		boolean save = checkIntersection;
//		checkIntersection = false;
//		lineTo(firstPoint.getX(),firstPoint.getY(),firstPoint.getZ());
//		checkIntersection = save;
//		return makeFace();
	}
	
	public DrawShapeModel triangle(Point3d a, Point3d b, Point3d c) {
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
		DrawShapeModel shape = new DrawShapeModelImpl(Shape.FACE, face, vs);
		return add(shape);
	}
	
	public DrawShapeModel quadrangle(Point3d a, Point3d b, Point3d c, Point3d d) {
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
		DrawShapeModel shape = new DrawShapeModelImpl(Shape.FACE, face, vs);
		return add(shape);
	}
	
	public DrawShapeModel rectangleXY(double dx, double dy) {		
		line(dx,0,0);
		line(0,dy,0);
		line(-dx,0,0);
		return close();		
	}
	
	public DrawShapeModel rectangleXZ(double dx, double dz) {		
		line(dx,0,0);
		line(0,0,dz);
		line(-dx,0,0);
		return close();		
	}
	
	public DrawShapeModel rectangleYZ(double dy, double dz) {		
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
	
	public DrawShapeModel extrude(DrawShapeModel s, double h) {
		TopExp_Explorer explorer = new TopExp_Explorer();
		if (s.getType() == Shape.FACE) {
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
				TopoDS_Face face = (TopoDS_Face)sh;
				TopoDS_Solid solid = (TopoDS_Solid) new BRepPrimAPI_MakePrism(
						face, 
						new double[]{direction.getX()*h, direction.getY()*h, direction.getZ()*h}).shape();
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.SOLID, solid, s.getVisualSettings());
				shapeList.remove(s);
				return add(shape);
			}
		}
		
		if (s.getType() == Shape.EDGE) {
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				TopoDS_Face face = (TopoDS_Face) new BRepPrimAPI_MakePrism(
						edge, 
						new double[]{direction.getX()*h, direction.getY()*h, direction.getZ()*h}).shape();
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.FACE, face, s.getVisualSettings());
				shapeList.remove(s);
				return add(shape);
			}
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
	
	public DrawShapeModel revolve(DrawShapeModel s, double angle) {
		TopExp_Explorer explorer = new TopExp_Explorer();
		if (s.getType() == Shape.FACE) {
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Face)) continue; // should not happen!
				TopoDS_Face face = (TopoDS_Face)sh;
				TopoDS_Solid solid = (TopoDS_Solid) new BRepPrimAPI_MakeRevol(
						face, 
						new double[]{position.getX(),position.getY(),position.getZ(),
								     direction.getX(), direction.getY(), direction.getZ()},
								     angle).shape();
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.SOLID, solid, s.getVisualSettings());
				shapeList.remove(s);
				return add(shape);
			}
		}
				
		if (s.getType() == Shape.EDGE) {
			for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.EDGE); explorer.more(); explorer.next())
			{						
				TopoDS_Shape sh = explorer.current();
				if (!(sh instanceof TopoDS_Edge)) continue; // should not happen!
				TopoDS_Edge edge = (TopoDS_Edge)sh;
				TopoDS_Face face = (TopoDS_Face) new BRepPrimAPI_MakeRevol(
						edge, 
						new double[]{position.getX(),position.getY(),position.getZ(),
								     direction.getX(), direction.getY(), direction.getZ()},
								     angle).shape();
				DrawShapeModel shape = new DrawShapeModelImpl(Shape.FACE, face, s.getVisualSettings());
				shapeList.remove(s);
				return add(shape);
			}
		}
		return null;
	}
	
	public DrawShapeModel fillet(DrawShapeModel s, double radius) {
		shapeList.remove(s);
		DrawShapeModel last = null;
		TopExp_Explorer explorer = new TopExp_Explorer();
		for (explorer.init(s.getShape2(TopoDS_Shape.class), TopAbs_ShapeEnum.SOLID); explorer.more(); explorer.next()) {
			BRepFilletAPI_MakeFillet fillet = new BRepFilletAPI_MakeFillet(s.getShape2(TopoDS_Shape.class));
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
			DrawShapeModel shape = new DrawShapeModelImpl(Shape.SOLID, fillet.shape(), s.getVisualSettings());
			last = add(shape);
		}		
		return last;		
	}
	
	public DrawShapeModel copy(DrawShapeModel s, double dx, double dy, double dz) {		
		GP_Trsf trsf = new GP_Trsf();
		trsf.setTranslation(new double[] {dx,dy,dz});
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(s.getShape2(TopoDS_Shape.class),trsf, true);
		TopoDS_Shape newShape=transform.shape();
		DrawShapeModel shape = new DrawShapeModelImpl(s.getType(), newShape, s.getVisualSettings());		
		return add(shape);		
	}
	
	public DrawShapeModel[] copy(DrawShapeModel[] shapes, double dx, double dy, double dz) {		
		GP_Trsf trsf = new GP_Trsf();
		trsf.setTranslation(new double[] {dx,dy,dz});
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(trsf);
		DrawShapeModel[] shapes1 = new DrawShapeModel[shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			DrawShapeModel s = shapes[i];
			transform.perform(s.getShape2(TopoDS_Shape.class), true);
			TopoDS_Shape newShape=transform.shape();
			DrawShapeModel shape = new DrawShapeModelImpl(s.getType(), newShape, s.getVisualSettings());		
			add(shape);
			shapes1[i] = shape;
		}		
		return shapes;
	}
	
	public DrawShapeModel rotate(DrawShapeModel s, double angle) {		
		GP_Trsf trsf = new GP_Trsf();
		trsf.setRotation(
				new double[]{position.getX(),position.getY(),position.getZ(),
				         direction.getX(),direction.getY(),direction.getZ()}, angle);
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(s.getShape2(TopoDS_Shape.class),trsf, true);		
		TopoDS_Shape newShape=transform.shape();
		DrawShapeModel shape = new DrawShapeModelImpl(s.getType(), newShape, s.getVisualSettings());
		delete(s);
		return add(shape);		
	}
	
	
	public DrawShapeModel scale(DrawShapeModel s, double a) {		
		GP_Trsf trsf = new GP_Trsf();		
		double[] matrix=new double[]{
			a, 0, 0, 0,
			0, a, 0, 0,
			0, 0, a, 0	
		};
		trsf.setValues(matrix, 0.0, 0.0);		
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(s.getShape2(TopoDS_Shape.class),trsf, true);		
		TopoDS_Shape newShape=transform.shape();
		DrawShapeModel shape = new DrawShapeModelImpl(s.getType(), newShape, s.getVisualSettings());
		delete(s);
		return add(shape);		
	}
	
		
	public DrawShapeModel array(DrawShapeModel s, int n, double dx,double dy, double dz) {		
		DrawShapeModel[] shapes = new DrawShapeModel[n-1];		
		for (int i = 1; i < n; i++) {
			shapes[i-1] = copy(s, dx*i, dy*i, dz*i);
			if (shapes[i-1].getType() == Shape.COMPOUND) explode(shapes[i-1]); 
		}
		BRep_Builder bb=new BRep_Builder();
		TopoDS_Compound compound=new TopoDS_Compound();
		bb.makeCompound(compound);
		bb.add(compound, s.getShape2(TopoDS_Shape.class));
		for (DrawShapeModel sh: shapeList) {
			bb.add(compound, sh.getShape2(TopoDS_Shape.class));
		}
		DrawShapeModel shape = new DrawShapeModelImpl(Shape.COMPOUND, compound, s.getVisualSettings());
		return shape;			
	}	
	
	
	public DrawShapeModel circle(double r) {
		
		final CommonShape commonShape = commonShapeModelManager.circle(position, direction, r);
		
		final DrawShapeModel shape = drawShapeModelManager.create(Shape.FACE, commonShape, vs);
		
		return add(shape); 
	}
	
	public DrawShapeModel box(double dx, double dy, double dz) {
		
		final CommonShape commonShape = commonShapeModelManager.box(position, dx, dy, dz);
		
		final DrawShapeModel shape = drawShapeModelManager.create(Shape.SOLID, commonShape, vs);
		
		return add(shape); 
	}
	
	public DrawShapeModel cylinder(double r, double h, double angle) {
		
		final CommonShape commonShape = commonShapeModelManager.cylinder(position, direction, r, h, angle);
		
		final DrawShapeModel shape = drawShapeModelManager.create(Shape.SOLID, commonShape, vs);
		
		return add(shape); 
	}
	
	public DrawShapeModel torus(double r1, double r2) {
		TopoDS_Shape cylinder=new BRepPrimAPI_MakeTorus(
				new double[]{position.getX(),position.getY(),position.getZ(),
						     direction.getX(),direction.getY(),direction.getZ()},
				r1, r2
				).shape();
		DrawShapeModel shape = new DrawShapeModelImpl(Shape.SOLID, cylinder, vs);
		return add(shape); 
	}

	
	public DrawShapeModel cone(double baseRadius, double topRadius, double h, double angle) {
		
		final CommonShape commonShape = commonShapeModelManager.cone(position, direction, baseRadius, topRadius, h, angle);
		
		final DrawShapeModel shape = drawShapeModelManager.create(Shape.SOLID, commonShape, vs);
		
		return add(shape); 
	}
	
	public DrawShapeModel common(DrawShapeModel s1, DrawShapeModel s2) {
		shapeList.remove(s1);
		shapeList.remove(s2);
		TopoDS_Shape s = new BRepAlgoAPI_Common(s1.getShape2(TopoDS_Shape.class), s2.getShape2(TopoDS_Shape.class)).shape();
		if (OCCUtils.isEmpty(s)) return null;
		int newType = s1.getType();
		if (s2.getType() < newType) newType = s2.getType();
		DrawShapeModel shape = new DrawShapeModelImpl(newType, s, vs);
		shapeList.add(shape);
		return shape;
	}
	
	public DrawShapeModel cut(DrawShapeModel s1, DrawShapeModel s2) {
		if (s1.getType() > s2.getType()) return null;
		shapeList.remove(s1);
		shapeList.remove(s2);
		TopoDS_Shape s = new BRepAlgoAPI_Cut(s1.getShape2(TopoDS_Shape.class), s2.getShape2(TopoDS_Shape.class)).shape();
		if (OCCUtils.isEmpty(s)) return null;
		DrawShapeModel shape = new DrawShapeModelImpl(s1.getType(), s, s1.getVisualSettings());
		shapeList.add(shape);
		return shape;
	}
	
	public DrawShapeModel fuse(DrawShapeModel s1, DrawShapeModel s2) {
		if (s1.getType() != s2.getType()) return null;
		shapeList.remove(s1);
		shapeList.remove(s2);
		TopoDS_Shape s = new BRepAlgoAPI_Fuse(s1.getShape2(TopoDS_Shape.class), s2.getShape2(TopoDS_Shape.class)).shape();
		if (OCCUtils.isEmpty(s)) return null;
		DrawShapeModel shape = new DrawShapeModelImpl(s1.getType(), s, s1.getVisualSettings());		
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
		for (DrawShapeModel s : shapeList) s.setMeshSize(size);		
	}
	
	public void setMeshSize(DrawShapeModel s, double size) {
		s.setMeshSize(size);		
	}
	
	public void setMeshSize(DrawShapeModel s, int n) {
		s.setMeshSize(n);		
	}
	
	public void setMeshSize(DrawShapeModel s, double x, double y, double z, int n) {
		s.setMeshSize(x,y,z,n);		
	}
	
	public void setMeshSize(DrawShapeModel s, double x, double y, double z, double size) {
		s.setMeshSize(x,y,z,size);		
	}
	
//	public void setMeshSize(ShapeModel s, Point3d p, double size) {
//		s.setMeshSize(p,size);		
//	}
	
	public void meshAll() {			
		for (DrawShapeModel s : shapeList) s.mesh();
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
	
	public void meshShape(DrawShapeModel s) {			
		addMesh(Mesher.createMesh(s));
		shapeList.remove(s);
	}

	
	
	public DrawShapeModel add(DrawShapeModel shape) {
		if (virtualDrawingMode) return shape;
		if (checkIntersection) commonList.clear();
		if (checkIntersection) return checkIntersection(shape); else shapeList.add(shape);
		
		
		shapeList.add(shape);
		
		return shape;
	}

	
	public DrawShapeModel copy2(DrawShapeModel s, double dx, double dy, double dz) {		
		GP_Trsf trsf = new GP_Trsf();
		trsf.setTranslation(new double[] {dx,dy,dz});
		BRepBuilderAPI_Transform transform = new BRepBuilderAPI_Transform(s.getShape2(TopoDS_Shape.class),trsf, true);
		TopoDS_Shape newShape=transform.shape();
		DrawShapeModel shape = new DrawShapeModelImpl(s.getType(), newShape, s.getVisualSettings());		
		return add(shape);		
	}

	public DrawShapeModel[] getPicture2() {
		DrawShapeModel[] arr = new DrawShapeModel[shapeList.size()];
		for (int i = 0; i < shapeList.size(); i++) arr[i] = shapeList.get(i);
		return arr;
	}
	
}
