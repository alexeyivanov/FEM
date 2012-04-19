package editor;

import java.util.List;

import core.Coordinate;

/**
 * GeometryShapeManager
 *  - operates with geometry data
 *  	- create different shapes
 *  	- move shapes
 *      - ...
 */
public interface GeometryShapeManager {
	
	GeometryShape load(String fileName);
	
	void save(List<GeometryShape> shapes, String fileName);
	
	GeometryShape fuse(GeometryShape s1, GeometryShape s2);
	
	GeometryShape cut(GeometryShape s1, GeometryShape s2);
	
	GeometryShape common(GeometryShape s1, GeometryShape s2);
	
	GeometryShape haveCommon(GeometryShape s1, int s1Type, GeometryShape s2, int s2Type);
	
	GeometryShape getEdges(GeometryShape shape);
	
	GeometryShape lineTo(Coordinate position, double x, double y, double z);
	
	GeometryShape wire(GeometryShape line);
	
	GeometryShape wire(GeometryShape edge, GeometryShape line);
	 
	GeometryShape makeFace(GeometryShape lastEdge);
	
	GeometryShape triangle(Coordinate a, Coordinate b, Coordinate c);
	
	GeometryShape quadrangle(Coordinate a, Coordinate b, Coordinate c, Coordinate d);
	
	GeometryShape[] copy(GeometryShape[] shapes, double dx, double dy, double dz);
	
	GeometryShape copy(GeometryShape s, double dx, double dy, double dz);
	
	GeometryShape rotate(Coordinate position, Coordinate direction, GeometryShape s, double angle);
	
	GeometryShape scale(GeometryShape s, double a);
	
	GeometryShape circle(Coordinate position, Coordinate direction, double r);
	
	GeometryShape box(Coordinate position, double dx, double dy, double dz);
	
	GeometryShape cylinder(Coordinate position, Coordinate direction, double r, double h, double angle);
	
	GeometryShape torus(Coordinate position, Coordinate direction, double r1, double r2);
	
	GeometryShape cone(Coordinate position, Coordinate direction, double baseRadius, double topRadius, double h, double angle);
	
	List<GeometryShape> fillet(GeometryShape s, double radius);
	
	GeometryShape revolveFace(Coordinate position, Coordinate direction, GeometryShape s, double angle);
	
	GeometryShape revolveEdge(Coordinate position, Coordinate direction, GeometryShape s, double angle);
	
	GeometryShape extrudeFace(Coordinate direction, GeometryShape s, double h);
	
	GeometryShape extrudeEdge(Coordinate direction, GeometryShape s, double h);
	
	List<GeometryShape> explodeSolid(GeometryShape s);
	
	List<GeometryShape> explodeFace(GeometryShape s);
	
	List<GeometryShape> explodeEdge(GeometryShape s);
	
	GeometryShape compound(GeometryShape s, List<GeometryShape> shapeList);
	
	boolean isEmpty(GeometryShape s);
}
