package editor;

import java.util.List;

public interface CommonShapeModelManager {
	
	void save(List<CommonShape> shapes, String fileName);
	
	CommonShape getEdges(DrawShapeModel shape);
	
	CommonShape lineTo(Coordinate position, Coordinate to);
	 
	CommonShape makeFace(CommonShape lastEdge);
	
	CommonShape triangle(Coordinate a, Coordinate b, Coordinate c);
	
	CommonShape quadrangle(Coordinate a, Coordinate b, Coordinate c, Coordinate d);
	
	CommonShape copy(CommonShape shape, Coordinate translation);
	
	CommonShape[] copy(CommonShape[] shapes, Coordinate translation);
	
	CommonShape rotate(CommonShape s, double angle);
	
	CommonShape scale(CommonShape s, double a);
	
	CommonShape circle(Coordinate position, Coordinate direction, double r);
	
	CommonShape box(Coordinate position, double dx, double dy, double dz);
	
	CommonShape cylinder(Coordinate position, Coordinate direction, double r, double h, double angle);
	
	CommonShape torus(double r1, double r2);
	
	CommonShape cone(Coordinate position, Coordinate direction, double baseRadius, double topRadius, double h, double angle);
}
