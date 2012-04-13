package editor;

import java.util.List;

import editor.Shape.FaceMesh;

public interface CommonShape {
	
	List<FaceMesh> getFaceMeshes();
	List<float[]> getEdgeArrays();

	<T> T getIntertalShape(Class<T> clazz);
}
