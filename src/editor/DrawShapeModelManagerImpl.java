package editor;

public class DrawShapeModelManagerImpl implements DrawShapeModelManager {

	@Override
	public DrawShapeModel create(int type, CommonShape shape, VisualSettings vs) {
		
		return new DrawShapeModelImpl(type, shape, vs);
	}

}
