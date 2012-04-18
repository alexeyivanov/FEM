package editor;

import java.awt.Color;

/**
 * DrawModel
 *  - operates with data for visualisation
 *  - keep data for visualisation. 
 *  
 * Input data
 *  - primitives
 *  - VisualShape objects
 */
public interface DrawModel {
	
	GeometryShapeManager getGeometryShapeManager();

	void setGeometryShapeManager(GeometryShapeManager geometryShapeManager);

	VisualShapeManager getDrawShapeModelManager();

	void setDrawShapeModelManager(VisualShapeManager drawShapeModelManager);
	
	VisualShape add(final VisualShape shape);
	
	void setLineWidth(final float w);
	
	void setCheckIntersection(boolean v);
	
	VisualShape circle(double r);
	
	void setFaceColor(Color c);
	
	VisualShape cylinder(double r, double h, double angle);
	
	void save(String fileName);
	
	VisualShape[] getPicture();
	
	void deleteSelected();
	
	void meshAll();

	void setMeshSize(double d);
	
	VisualShape fuse(VisualShape s1, VisualShape s2);
	
	VisualShape cone(double baseRadius, double topRadius, double h, double angle);
	
	void move(double x, double y, double z);
	
	VisualShape cut(VisualShape s1, VisualShape s2);
	
	VisualShape copy(VisualShape s, double dx, double dy, double dz);
	
	void delete(VisualShape s);
	
	void moveTo(double x, double y, double z);
	
	void setTransparency(double t);
	
	void setDirection(double x, double y, double z);
	
	VisualShape box(double dx, double dy, double dz);
	
	VisualShape array(VisualShape s, int n, double dx,double dy, double dz);

}
