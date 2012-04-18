package editor;

import java.util.List;

import editor.Shape.FaceMesh;

public class GeometryShapeImpl implements GeometryShape {
	
	private List<FaceMesh> faceMeshes;
	private List<float[]> edgeArrays;
	
	public GeometryShapeImpl(List<FaceMesh> faceMeshes, List<float[]> edgeArrays) {
		this.faceMeshes = faceMeshes;
		this.edgeArrays = edgeArrays;
	}
	
	public <T> GeometryShapeImpl(List<FaceMesh> faceMeshes, List<float[]> edgeArrays, T shape, Class<T> clazz) {
		this.faceMeshes = faceMeshes;
		this.edgeArrays = edgeArrays;
		intertalShape = shape;
	}
	
	public List<FaceMesh> getFaceMeshes() {
		return faceMeshes;
	}

	public List<float[]> getEdgeArrays() {
		return edgeArrays;
	}

	public Object getIntertalShape() {
		return intertalShape;
	}


	private Object intertalShape;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getIntertalShape(Class<T> clazz) {
		
		if(intertalShape == null) {
			return null;
		} 
//		else if (intertalShape.getClass() != clazz) {
//			throw new IllegalArgumentException("input param should be: " + intertalShape.getClass());
//		}
		
		return (T) intertalShape;	
	}
}
