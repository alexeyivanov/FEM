package editor;

import java.awt.Color;

public class Java3DViewerTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		DrawModel d = drawWall2();
		DrawModel d = drawFN2();
		
		Java3DViewer.view(d);
	}
	
	static DrawModel drawFN2() {
		
		DrawModel d = new DrawModelImpl();
		d.setCommonShapeModelManager(new CommonShapeModelManagerOCC());
		d.setDrawShapeModelManager(new DrawShapeModelManagerImpl());
	
		d.setLineWidth(0.1f);
		
		d.circle(0.6);
		
		d.circle(0.4);
		
		d.moveTo(0, -0.2, 0);
		d.setFaceColor(Color.CYAN);
		DrawShapeModel b1 = d.box(0.8, 0.3, 0.3);
		
		d.setFaceColor(Color.GREEN);
		DrawShapeModel b2 = d.box(0.8, 0.15, 0.6);
				
		d.setFaceColor(Color.MAGENTA);
		DrawShapeModel box = d.box(-0.25, 0.25, 0.25);
		
		d.setFaceColor(Color.ORANGE);
		d.box(-0.1, 0.1, 0.35);
		
//		d.setTexture("brick1.gif");
		
		d.setFaceColor(Color.YELLOW);
		DrawShapeModel cyl = d.cylinder(0.5, 0.7, Math.PI*3/2);
				
//		d.noTexture();
		d.moveTo(0, 0, 0);
		d.setFaceColor(Color.BLUE);
		d.cylinder(0.2, 0.3, Math.PI*2);
		d.save("FN.brep");
		return d;
	}
	
	static DrawModel drawWall2() {
		DrawModel d = new DrawModelImpl();
		d.setCommonShapeModelManager(new CommonShapeModelManagerOCC());
		d.setDrawShapeModelManager(new DrawShapeModelManagerImpl());
		
		d.setCheckIntersection(false);
		d.setFaceColor(Color.WHITE);
		d.setTransparency(0);
		d.moveTo(-0.4,0,-0.4);		
		DrawShapeModel base = d.box(0.8, 0.2, 0.8);
		d.moveTo(0, 0.2, 0);
		DrawShapeModel col = d.cone(0.3, 0.2, 3.8, Math.PI*2);
		d.moveTo(0, 4, 0);
		DrawShapeModel capitel1 = d.cone(0.2, 0.3, 0.2, Math.PI*2);
		d.move(0, 0.2, 0);
		DrawShapeModel capitel2 = d.cylinder(0.35, 0.15, Math.PI*2);
		DrawShapeModel column = d.fuse(base, col);
		column = d.fuse(column, capitel1);
		column = d.fuse(column, capitel2);
		d.moveTo(-1, 0, 0);
		DrawShapeModel cutBox = d.box(5, 6, 1);
		column = d.cut(column, cutBox);
		d.delete(cutBox);
		DrawShapeModel columns = d.array(column, 6, 2, 0, 0);				
		d.moveTo(-0.4, 4.35, -0.4);		
		DrawShapeModel beam = d.box(5*2+0.8, 0.3, 0.4);
		
		d.moveTo(-0.4, 0, 0);
		d.setFaceColor(Color.GREEN);		
		DrawShapeModel wall = d.box(5*2+0.8, 4.65, 0.5);
		
		wall = d.fuse(wall, beam);
		d.fuse(columns, wall);
	
		d.setDirection(0, 0, 1);
		d.moveTo(1, 3, 0);
		DrawShapeModel arch = d.cylinder(0.5, 0.5, Math.PI*2);			
		d.move(-0.5,0,0);
		DrawShapeModel door = d.box(1, -3, 0.5);
		door = d.fuse(door, arch);
		wall = d.cut(wall,door);		
		door = d.copy(door, 2, 0, 0);
		wall = d.cut(wall,door);
		door = d.copy(door, 2, 0, 0);
		wall = d.cut(wall,door);
		door = d.copy(door, 2, 0, 0);
		wall = d.cut(wall,door);
		door = d.copy(door, 2, 0, 0);
		wall = d.cut(wall,door);
		
		d.save("columns.brep");	
		return d;
	}

}

