package editor;

import java.awt.Color;

public interface DrawModel {
	
	CommonShapeModelManager getCommonShapeModelManager();

	void setCommonShapeModelManager(CommonShapeModelManager shapeModelManager);

	DrawShapeModelManager getDrawShapeModelManager();

	void setDrawShapeModelManager(DrawShapeModelManager drawShapeModelManager);
	
	DrawShapeModel add(final DrawShapeModel shape);
	
	void setLineWidth(final float w);
	
	void setCheckIntersection(boolean v);
	
	DrawShapeModel circle(double r);
	
	void setFaceColor(Color c);
	
	DrawShapeModel cylinder(double r, double h, double angle);
	
	void save(String fileName);
	
	DrawShapeModel[] getPicture2();
	
	void deleteSelected();
	
	void meshAll();

	void setMeshSize(double d);
	
	DrawShapeModel fuse(DrawShapeModel s1, DrawShapeModel s2);
	
	DrawShapeModel cone(double baseRadius, double topRadius, double h, double angle);
	
	void move(double x, double y, double z);
	
	DrawShapeModel cut(DrawShapeModel s1, DrawShapeModel s2);
	
	DrawShapeModel copy(DrawShapeModel s, double dx, double dy, double dz);
	
	void delete(DrawShapeModel s);
	
	void moveTo(double x, double y, double z);
	
	void setTransparency(double t);
	
	void setDirection(double x, double y, double z);
	
	DrawShapeModel box(double dx, double dy, double dz);
	
	DrawShapeModel array(DrawShapeModel s, int n, double dx,double dy, double dz);

}
