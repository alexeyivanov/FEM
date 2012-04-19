package editor;

/**
 * VisualShape
 *  - keep geometry data + visual settings for this object.
 */
public interface VisualShape {

	void select();
	void setSelected(boolean v);
	boolean isSelected();
	
	int getType();
	VisualSettings getVisualSettings();
	void mesh();
	
	void setMeshSize(double size);
	void setMeshSize(double x, double y, double z, double size);
	void setMeshSize(double x, double y, double z, int n);
	void setCutted(boolean cutted);
	
	boolean haveCommon(VisualShape s);
	
	<T> T haveCommon(final VisualShape s, final Class<T> clazz);
	<T> T getBounds(final Class<T> clazz);
	<T> T getFaces2(Class<T> clazz);
	<T> T getEdges2(Class<T> clazz);
	<T> T getText2(Class<T> clazz);
	
	void setShape(GeometryShape geometryShape);
	
	<K> void setShape(K shape, Class<K> clazz);
	<K> K getShape2(Class<K> clazz);
	
	Mesh getMesh();
	
	GeometryShape getGeometryShape();
}
