package editor;

import java.awt.Color;

public class ViewerTest {

	static Drawing drawFN() {
		Drawing d = new Drawing();
	
		d.setLineWidth(0.1f);
		
		
		d.circle(0.6);
		
		d.circle(0.4);
		
		d.moveTo(0, -0.2, 0);
		d.setFaceColor(Color.CYAN);
		Shape b1 = d.box(0.8, 0.3, 0.3);
		
		d.setFaceColor(Color.GREEN);
		Shape b2 = d.box(0.8, 0.15, 0.6);
				
		d.setFaceColor(Color.MAGENTA);
		Shape box = d.box(-0.25, 0.25, 0.25);
		
		d.setFaceColor(Color.ORANGE);
		d.box(-0.1, 0.1, 0.35);
		
//		d.setTexture("brick1.gif");
		
		d.setFaceColor(Color.YELLOW);
		Shape cyl = d.cylinder(0.5, 0.7, Math.PI*3/2);
				
//		d.noTexture();
		d.moveTo(0, 0, 0);
		d.setFaceColor(Color.BLUE);
		d.cylinder(0.2, 0.3, Math.PI*2);
		d.save("FN.brep");
		return d;
	}
	
	static Drawing drawFigure() {
		Drawing d = new Drawing();
		
		d.setCheckIntersection(true);
		d.setLineWidth(2);
		d.setFaceColor(Color.CYAN);
		d.moveTo(0, 0, 0);
		d.line(1,0,0);
		d.line(0,0,1);
		d.line(-1,0,0);
		Shape rect = d.close();
		
		d.setFaceColor(Color.YELLOW);
		d.move(0.2, 0, 0.2);
		d.line(0.6,0,0);
		d.line(0,0,0.6);
		d.line(-0.6,0,0);
		Shape rect1 = d.close();
		

		
		d.moveTo(0, 0, 2);
		d.setDirection(0, 1, 0);
		Shape s = d.extrude(rect, 0.5);
		
//		d.fillet(s);
		
//		d.setDirection(1, 0, 0);				
//		Shape shape = d.revolve(rect, Math.PI/4);
		
////		d.setCheckIntersection(false);
//		d.setVirtualDrawingMode(true);
//		d.moveTo(0, 1, 0.6);
//		Shape circle = d.circle(0.15);
//		Shape cylinder = d.extrude(circle, 1);	
//		d.setVirtualDrawingMode(false);
//		
//		
////		d.add(cylinder);
//		shape = d.cut(shape, cylinder);
//		
//		d.moveTo(0,0,0);
//		d.setDirection(-1, 0, 0);
//		Shape rev = d.revolve(rect1, Math.PI);
//		d.delete(rev);
//		d.delete(d.getCommon());
//		
//		
////		d.setCheckIntersection(false);		
////		int n = 10;
////		double dist = 2;
////		Shape line = d.array(shape, n, dist, 0, 0);
////		Shape area = d.array(line, n, 0, dist, 0);
////		Shape cube = d.array(area, n, 0, 0, dist);
////		
////		d.setCheckIntersection(true);
//		
////		d.copy(shape, 2,0,0);
//		
////		Shape shape1 = d.copy(shape, 2, 0, 0);
////		
////		shape1 = d.scale(shape1, 0.5);
////		
////		d.moveTo(2, 0, 0);
////		d.setDirection(0, 0, 1);		
////		shape1 = d.rotate(shape1, Math.PI/4);
////		
//		
////		rect1.setSelected(true);
//////		d.save("figure.brep");
//////		d.explode(shape);
		
		return d;
	}
	
	static Drawing drawBox() {
		Drawing d = new Drawing();
		d.setLineWidth(2);
		
		d.setCheckIntersection(false);
		Shape box = d.box(1, 1, 1);
		int n = 15;
		double dist = 1.1;
		Shape line = d.array(box, n, dist, 0, 0);
		Shape area = d.array(line, n, 0, dist, 0);
		Shape cube = d.array(area, n, 0, 0, dist);
		
		d.setCheckIntersection(true);
		
		return d;
	}
	
	static Drawing drawBuilding() {
		Drawing d = new Drawing();
		d.setLineWidth(2);
	    d.moveTo(0, 0, 0);
	    d.setCheckIntersection(true);
	    d.setFaceColor(Color.GRAY);
	    Shape plate = d.rectangleXZ(30, 15);
	    Shape wall = d.getEdges(plate);
	    d.setFaceColor(Color.DARK_GRAY);
	    Shape base = d.extrude(wall, -1.5);	    
	    d.copy(plate, 0, -1.5, 0);
	    d.setFaceColor(Color.YELLOW);
	    wall = d.getEdges(plate);
	    wall = d.extrude(wall, 3);
	    d.setFaceColor(Color.CYAN);
	    for (int i = 0; i < 10; i++) {
	    	d.moveTo(0.5+i*3, 0.8, 0);
		    Shape window = d.rectangleXY(1, 1.7);
		    d.delete(window);
		    d.moveTo(0.5+i*3, 0.8, 15);
		    window = d.rectangleXY(1, 1.7);
		    d.delete(window);
	    }
	    d.setCheckIntersection(false);
	    d.array(wall, 10, 0, 3, 0);
	    d.array(plate, 11, 0, 3, 0);
	    d.setCheckIntersection(true);	    	    
	    
	    d.moveTo(-20, 0, -20);   
	    d.setFaceColor(Color.GREEN);
	    d.box(70, -30, 60);
	    Shape excavation = d.extrude(plate, -1.5);
	    d.delete(excavation);
	    
//	    d.delete(plate);
	    
		d.save("building.brep");
		return d;
	}
	
	static Drawing drawBuilding3D() {
		Drawing d = new Drawing();
		d.setLineWidth(2);
	    d.moveTo(0, 0, 0);
	    d.setCheckIntersection(true);
	    d.setFaceColor(Color.GRAY);
	    Shape wall = d.rectangleXZ(30, 15);
	    d.moveTo(0.3,0,0.3);
	    Shape plate = d.rectangleXZ(30-0.4, 15-0.4);	    
	    d.setFaceColor(Color.YELLOW);
	    wall = d.extrude(wall, 3);
	    d.setFaceColor(Color.CYAN);
	    for (int i = 0; i < 10; i++) {
	    	d.moveTo(0.5+i*3, 0.8, 0);
		    Shape window = d.box(1, 1.5, 0.2);
		    d.delete(window);
		    d.moveTo(0.5+i*3, 0.8, 15);
		    window = d.box(1, 1.5, 0.2);
		    d.delete(window);
	    }
	    d.array(wall, 10, 0, 3, 0);
	    d.array(plate, 11, 0, 3, 0);
	    d.moveTo(-20, 0, -20);
	    d.setFaceColor(Color.GREEN);
	    d.box(70, -30, 60);
	    
		d.save("building.brep");
		return d;
	}
	
	static Drawing drawMesh() {
		Drawing d = new Drawing();
		
		Mesh m = new Mesh();
		
		int n = 100;
		double s = 1;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
//					Mesh m1 = new Mesh(m);
					int p1 = m.point(i*s, j*s, k*s);
					int p2 = m.point(i*s+s, j*s, k*s);
					int p3 = m.point(i*s+s, j*s+s, k*s);
					int p4 = m.point(i*s, j*s+s, k*s);
					
					int p5 = m.point(i*s, j*s, k*s+s);
					int p6 = m.point(i*s+s, j*s, k*s+s);
					int p7 = m.point(i*s+s, j*s+s, k*s+s);
					int p8 = m.point(i*s, j*s+s, k*s+s);
					
					m.setResult(p1,(i+j+k)*0.1);
					m.setResult(p2,(i+j+k)*0.1);
					m.setResult(p3,(i+j+k)*0.1);
					m.setResult(p4,(i+j+k)*0.1);
					
					m.setResult(p5,(i+j+k)*0.1);
					m.setResult(p6,(i+j+k)*0.1);
					m.setResult(p7,(i+j+k)*0.1);
					m.setResult(p8,(i+j+k)*0.1);
									
					
					m.hexahedron(p1, p2, p3, p4, p5, p6, p7, p8);					
				}
			}			
		}	
		System.out.println("Created");
		
		System.out.print("Assembling...");

		m.assemble();
		System.out.println("done.");
		d.addMesh(m);

		return d;

	}
	
	static Drawing draw() {
		Drawing d = new Drawing();
		d.setCheckIntersection(false);
		Shape box = d.box(1, 1, 1);
		box = d.fillet(box,0.2);
		d.moveTo(0.5, 0, 0.5);
		Shape cone = d.cone(0.2,0.3,1,Math.PI*2);
		Shape fig = d.cut(box, cone);
		d.delete(cone);					
		
		return d;
	}
	
	
	static Drawing drawWall() {
		Drawing d = new Drawing();
		
		d.setCheckIntersection(false);
		d.setFaceColor(Color.WHITE);
		d.setTransparency(0);
		d.moveTo(-0.4,0,-0.4);		
		Shape base = d.box(0.8, 0.2, 0.8);
		d.moveTo(0, 0.2, 0);
		Shape col = d.cone(0.3, 0.2, 3.8, Math.PI*2);
		d.moveTo(0, 4, 0);
		Shape capitel1 = d.cone(0.2, 0.3, 0.2, Math.PI*2);
		d.move(0, 0.2, 0);
		Shape capitel2 = d.cylinder(0.35, 0.15, Math.PI*2);
		Shape column = d.fuse(base, col);
		column = d.fuse(column, capitel1);
		column = d.fuse(column, capitel2);
		d.moveTo(-1, 0, 0);
		Shape cutBox = d.box(5, 6, 1);
		column = d.cut(column, cutBox);
		d.delete(cutBox);
		Shape columns = d.array(column, 6, 2, 0, 0);				
		d.moveTo(-0.4, 4.35, -0.4);		
		Shape beam = d.box(5*2+0.8, 0.3, 0.4);
		
		d.moveTo(-0.4, 0, 0);
		d.setFaceColor(Color.GREEN);		
		Shape wall = d.box(5*2+0.8, 4.65, 0.5);
		
		wall = d.fuse(wall, beam);
		d.fuse(columns, wall);
	
		d.setDirection(0, 0, 1);
		d.moveTo(1, 3, 0);
		Shape arch = d.cylinder(0.5, 0.5, Math.PI*2);			
		d.move(-0.5,0,0);
		Shape door = d.box(1, -3, 0.5);
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
	
	
	static Drawing drawTest() {
		Drawing d = new Drawing();
		d.setFaceColor(Color.YELLOW);
//		d.setTransparency(0.9);
		d.moveTo(0, 0, 0);
		d.line(2, 0, 0);
		d.line(0, 0, 2);
		d.line(-2, 0, 0);
		Shape rect = d.close();
		
		d.moveTo(0.2, 0, 0.2);
		d.line(1.6, 0, 0);
		d.line(0, 0, 1.6);
		d.line(-1.6, 0, 0);
		Shape intern = d.close();
		d.delete(intern);
		
		Shape fig = d.extrude(rect, 2);
		d.save("test.brep");
		
		d.setMeshSize(fig, 0.1);
		d.setMeshSize(fig, 2, 0, 0, 0.01);
		fig.setMeshSize(0, 0.5, 0, 50);
		
		return d;
	}
	
	static Drawing testMesh() {
		Drawing d = new Drawing();
		d.setTransparency(0.9);
		Shape s = d.box(2, 2, 2);
		Shape s1 = d.box(1, 1, 1);
		
		s = d.cut(s, s1);
		d.moveTo(0, 2, 0);
		Shape s2 = d.box(0.2, 2, 0.2);
		
		s.setMeshSize(0.3);
		s2.setMeshSize(0.1);
		//d.fuse(s, s2);
		d.save("test.brep");

		return d;
	}
	
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
		d.setGeometryShapeManager(new GeometryShapeManagerOCC());
		d.setDrawShapeModelManager(new VisualShapeManagerImpl());
	
		d.setLineWidth(0.1f);
		
		
		d.circle(0.6);
		
		d.circle(0.4);
		
		d.moveTo(0, -0.2, 0);
		d.setFaceColor(Color.CYAN);
		VisualShape b1 = d.box(0.8, 0.3, 0.3);
		
		d.setFaceColor(Color.GREEN);
		VisualShape b2 = d.box(0.8, 0.15, 0.6);
				
		d.setFaceColor(Color.MAGENTA);
		VisualShape box = d.box(-0.25, 0.25, 0.25);
		
		d.setFaceColor(Color.ORANGE);
		d.box(-0.1, 0.1, 0.35);
		
//		d.setTexture("brick1.gif");
		
		d.setFaceColor(Color.YELLOW);
		VisualShape cyl = d.cylinder(0.5, 0.7, Math.PI*3/2);
				
//		d.noTexture();
		d.moveTo(0, 0, 0);
		d.setFaceColor(Color.BLUE);
		d.cylinder(0.2, 0.3, Math.PI*2);
		d.save("FN.brep");
		return d;
	}
	
	static DrawModel drawWall2() {
		DrawModel d = new DrawModelImpl();
		d.setGeometryShapeManager(new GeometryShapeManagerOCC());
		d.setDrawShapeModelManager(new VisualShapeManagerImpl());
		
		d.setCheckIntersection(false);
		d.setFaceColor(Color.WHITE);
		d.setTransparency(0);
		d.moveTo(-0.4,0,-0.4);		
		VisualShape base = d.box(0.8, 0.2, 0.8);
		d.moveTo(0, 0.2, 0);
		VisualShape col = d.cone(0.3, 0.2, 3.8, Math.PI*2);
		d.moveTo(0, 4, 0);
		VisualShape capitel1 = d.cone(0.2, 0.3, 0.2, Math.PI*2);
		d.move(0, 0.2, 0);
		VisualShape capitel2 = d.cylinder(0.35, 0.15, Math.PI*2);
		VisualShape column = d.fuse(base, col);
		column = d.fuse(column, capitel1);
		column = d.fuse(column, capitel2);
		d.moveTo(-1, 0, 0);
		VisualShape cutBox = d.box(5, 6, 1);
		column = d.cut(column, cutBox);
		d.delete(cutBox);
		VisualShape columns = d.array(column, 6, 2, 0, 0);				
		d.moveTo(-0.4, 4.35, -0.4);		
		VisualShape beam = d.box(5*2+0.8, 0.3, 0.4);
		
		d.moveTo(-0.4, 0, 0);
		d.setFaceColor(Color.GREEN);		
		VisualShape wall = d.box(5*2+0.8, 4.65, 0.5);
		
		wall = d.fuse(wall, beam);
		d.fuse(columns, wall);
	
		d.setDirection(0, 0, 1);
		d.moveTo(1, 3, 0);
		VisualShape arch = d.cylinder(0.5, 0.5, Math.PI*2);			
		d.move(-0.5,0,0);
		VisualShape door = d.box(1, -3, 0.5);
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
