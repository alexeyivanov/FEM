package core;

import Jama.Matrix;

/**
 * Class for 3D coordinate
 * @author Constantin Shashkin
 */
public class Coordinate {
	
	protected double x, y, z;
	
	public Coordinate(double x, double y, double z) {		
		setXYZ(x, y, z);
	}
	
	public Coordinate(double x) {		
		setXYZ(x, 0, 0);
	}
	
	public Coordinate(double x, double y) {		
		setXYZ(x, y, 0);
	}
	
	public Coordinate(double[] c) {		
		if (c.length == 1) setXYZ(c[0],0, 0);
		if (c.length == 2) setXYZ(c[0],c[1],0);
		if (c.length >= 3) setXYZ(c[0],c[1],c[2]);
	}
	
	public Coordinate(Coordinate a) {		
		set(a);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setXYZ(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Coordinate a) {
		x = a.x;
		y = a.y;
		z = a.z;
	}
	
	public double distance(Coordinate b) {		
		return Math.sqrt((x-b.x)*(x-b.x)+(y-b.y)*(y-b.y)+(z-b.z)*(z-b.z));
	}
	
	public void plusEquals(Coordinate b) {
		x += b.x;
		y += b.y;
		z += b.z;
	}
	
	public void minusEquals(Coordinate b) {
		x -= b.x;
		y -= b.y;
		z -= b.z;
	}
	
	public void multEquals(double b) {
		x *= b;
		y *= b;
		z *= b;
	}
	
	

}
