package editor;

public class VisualShapeManagerImpl implements VisualShapeManager {

	@Override
	public VisualShape create(int type, GeometryShape shape, VisualSettings vs) {
		
		return new VisualShapeImpl(type, shape, vs);
	}

}
