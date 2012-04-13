package editor;

import java.awt.Color;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

/**
 * Visual settings for graphic Java3D objects in viewer and editor
 * @author Constantin Shashkin
 *
 */
public class VisualSettings {
	private Color faceColor = Color.GREEN;
	private float transparency = 0;
	private Color lineColor = Color.BLACK;
	private float lineWidth = 1;
	protected Texture2D texture;
	private LineAttributes lineAttributes=new LineAttributes();
	private static final float zFactorAbs=Float.parseFloat(System.getProperty(
			"javax.media.j3d.zFactorAbs", "20.0f"));
	private static final float zFactorRel=Float.parseFloat(System.getProperty(
			"javax.media.j3d.zFactorRel", "2.0f"));
	private final static PolygonAttributes polygonAttrFront =
		new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_FRONT,
				20.0f * zFactorAbs, false, zFactorRel);
	private final static PolygonAttributes polygonAttrBack =
		new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_BACK,
				20.0f * zFactorAbs, false, zFactorRel);
	private final static PolygonAttributes polygonAttrNone =
		new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE,
				20.0f * zFactorAbs, false, zFactorRel);
	final private static PolygonAttributes poligonAttrLine=new PolygonAttributes(
			PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_NONE, zFactorAbs);
	
	private TransparencyAttributes normalTA, selectedTA;
	
	
	/**
	 * Constructor
	 * @param faceColor
	 * @param lineColor
	 * @param lineWidth
	 * @param texture
	 * @param t - transparency value 0...1
	 */
	public VisualSettings(Color faceColor,	Color lineColor, float lineWidth, Texture2D texture, double t) {
		
		this.faceColor = faceColor;
		this.lineColor = lineColor;
		this.lineWidth = lineWidth;
		lineAttributes.setCapability(LineAttributes.ALLOW_WIDTH_WRITE);
		lineAttributes.setLineWidth(lineWidth);
		
		selectedTA = new TransparencyAttributes(TransparencyAttributes.BLENDED,0.5f);
		selectedTA.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
		selectedTA.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
		setTransparency(t);		
		this.texture = texture;
	}
	
	/**
	 * Creates new object with same visual settings as in vs
	 * @param vs
	 */
	public VisualSettings(VisualSettings vs) {
		this(vs.getFaceColor(), vs.getLineColor(), vs.getLineWidth(), vs.getTexture(), vs.getTransparncy());	
	}
	
	
	
	public Appearance getLineAppearance() {
		Appearance a=new Appearance();
		a.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		a.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);
		ColoringAttributes ca=new ColoringAttributes();
		ca.setColor((float)lineColor.getRed()/255, (float)lineColor.getGreen()/255, (float)lineColor.getBlue()/255);
		ca.setShadeModel(ColoringAttributes.FASTEST);
		lineAttributes.setLineWidth(lineWidth);
		a.setLineAttributes(lineAttributes);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		a.setColoringAttributes(ca);
		return a;
	}
	
	public Appearance getSelectedLineAppearance() {
		Appearance a=new Appearance();
		a.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		a.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);		
		ColoringAttributes ca=new ColoringAttributes();
		ca.setColor(1f, 0f, 0f);
		ca.setShadeModel(ColoringAttributes.FASTEST);
		lineAttributes.setLineWidth(lineWidth+2);
		a.setLineAttributes(lineAttributes);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		a.setColoringAttributes(ca);
		return a;
	}
	
	public Appearance getMeshAppearance() {
		Appearance a=new Appearance();
		Material m1=new Material();
		Color3f color=new Color3f(
				((float)faceColor.getRed())/255
				,((float)faceColor.getGreen())/255
				,((float)faceColor.getBlue())/255);				
		Color3f gray=new Color3f(0.4f,0.4f,0.4f);		
		m1.setAmbientColor(gray);
		m1.setDiffuseColor(color);
		m1.setSpecularColor(gray);
		m1.setShininess(1000f);
		
		a.setMaterial(m1);

		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		if (transparency != 0) a.setTransparencyAttributes(normalTA);		
		a.setPolygonAttributes(polygonAttrNone);
		return a;
	}
	
	public Appearance getSelectedMeshAppearance() {
		Appearance a=new Appearance();
		Material m1=new Material();
		Color faceColor = Color.RED;
		Color3f color=new Color3f(
				((float)faceColor.getRed())/255
				,((float)faceColor.getGreen())/255
				,((float)faceColor.getBlue())/255);				
		Color3f gray=new Color3f(0.4f,0.4f,0.4f);		
		m1.setAmbientColor(gray);
		m1.setDiffuseColor(color);
		m1.setSpecularColor(gray);
		m1.setShininess(1000f);
		a.setMaterial(m1);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		a.setTransparencyAttributes(selectedTA);
		a.setPolygonAttributes(polygonAttrNone);
		return a;
	}
	
	public Appearance getFaceAppearance() {
		Appearance a=new Appearance();
		Material m1=new Material();
		Color3f color=new Color3f(
				((float)faceColor.getRed())/255
				,((float)faceColor.getGreen())/255
				,((float)faceColor.getBlue())/255);
		
		Color3f gray=new Color3f(0.4f,0.4f,0.4f);
		
		m1.setAmbientColor(gray);
		m1.setDiffuseColor(color);
		m1.setSpecularColor(gray);
		m1.setShininess(1000f);
		m1.setCapability(Material.ALLOW_COMPONENT_WRITE);			
		a.setMaterial(m1);

		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		if (transparency != 0) a.setTransparencyAttributes(normalTA);
		a.setPolygonAttributes(polygonAttrBack);
		
		if (texture != null) {
			//OBJECT_LINEAR
			TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR,
	                TexCoordGeneration.TEXTURE_COORDINATE_2);
			a.setTexCoordGeneration(tcg);
			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.COMBINE);
			ta.setCombineAlphaMode(TextureAttributes.COMBINE_INTERPOLATE);
			a.setTextureAttributes(ta);
			a.setTexture(texture);
		}
		return a;
	}
	
	public Appearance getSelectedFaceAppearance() {
		Appearance a=new Appearance();
		Material m1=new Material();
		Color faceColor = Color.RED;
		Color3f color=new Color3f(
				((float)faceColor.getRed())/255
				,((float)faceColor.getGreen())/255
				,((float)faceColor.getBlue())/255);
		
		Color3f gray=new Color3f(0.4f,0.4f,0.4f);
		
		m1.setAmbientColor(gray);
		m1.setDiffuseColor(color);
		m1.setSpecularColor(gray);
		m1.setShininess(1000f);
		m1.setCapability(Material.ALLOW_COMPONENT_WRITE);			
		a.setMaterial(m1);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		a.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		a.setTransparencyAttributes(selectedTA);
		a.setPolygonAttributes(polygonAttrBack);
		
		if (texture != null) {
			//OBJECT_LINEAR
			TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR,
	                TexCoordGeneration.TEXTURE_COORDINATE_2);
			a.setTexCoordGeneration(tcg);
			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.COMBINE);
			ta.setCombineAlphaMode(TextureAttributes.COMBINE_INTERPOLATE);
			a.setTextureAttributes(ta);
			a.setTexture(texture);
		}
		
		return a;
	}
	
//	public TransparencyAttributes getSelectedTransparencyAttributes() {
//		return selectedTA;
//	}
	
	public Color3f getScaleColor(float v) {		
		float R, G, B;
		if (v < 0.2) { // magenta - blue
			R = 1 - 5*v;
			G = 0;
			B = 1;
		}
		else if (v < 0.4) { // blue - cyan
			R = 0;
			G = 5*(v - 0.2f);
			B = 1;
		}
		else if (v < 0.6) { // cyan - green
			R = 0;
			G = 1;
			B = 1 - 5*(v - 0.4f);
		}
		else if (v < 0.8) { // green - yellow
			R = 5*(v - 0.6f);
			G = 1;
			B = 0;
		}
			
		else { // yellow - red
			R = 1;
			G = 1 - 5*(v - 0.8f);
			B = 0;
		}
		
		return new Color3f(R,G,B);
		
//		int iR = (int) (R*
//				255 + 0.5);
//		int iG = (int) (G*
//				255 + 0.5);
//		int iB = (int) (B*
//				255 + 0.5);
//		return (255 << 24) | (iR << 16) | (iG << 8) | iB;
	}
	
	public Color3f getJ3DColor(Color c) {
		return new Color3f(((float)c.getRed())/255,((float)c.getGreen())/255,((float)c.getBlue())/255);
	}


	public Color getFaceColor() {
		return faceColor;
	}


	public void setFaceColor(Color faceColor) {
		this.faceColor = faceColor;
	}
	
	public void setTransparency(double t) {
		transparency = (float)t;		
		normalTA = new TransparencyAttributes(TransparencyAttributes.BLENDED,transparency);
		normalTA.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
		normalTA.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);	
	}
	
	public double getTransparncy() {
		return transparency; 
	}

	public Color getLineColor() {
		return lineColor;
	}


	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}


	public float getLineWidth() {
		return lineWidth;
	}


	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
		lineAttributes.setLineWidth(lineWidth);
	}


	public Texture2D getTexture() {
		return texture;
	}


	public void setTexture(Texture2D texture) {
		this.texture = texture;
	}


}