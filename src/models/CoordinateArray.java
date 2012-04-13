package models;

import java.util.ArrayList;
import java.util.LinkedList;

import core.*;


public class CoordinateArray {

	private ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
	
	private double maxX, minX, maxY, minY, maxZ, minZ;
	private double assembleDist = 0.001;
	private int tableSize;
	private class CoordArray extends LinkedList<Coordinate> {
		private static final long serialVersionUID = 1L;
		
	}
	
	private  CoordArray [][][] table3D;
		
	public void add(Coordinate c) {
		coords.add(c);	
	}	
	
	private void calculateMinMaxCoords() {
		maxX = Double.MIN_VALUE;
		minX = Double.MAX_VALUE;
		maxY = Double.MIN_VALUE;
		minY = Double.MAX_VALUE;
		maxZ = Double.MIN_VALUE;
		minZ = Double.MAX_VALUE;		
		for (Coordinate c : coords) {
			if (c.getX() < minX) minX = c.getX();
			if (c.getX() > maxX) maxX = c.getX();
			if (c.getY() < minY) minX = c.getY();
			if (c.getY() > maxY) maxX = c.getY();
			if (c.getZ() < minZ) minX = c.getZ();
			if (c.getZ() > maxZ) maxX = c.getZ();						
		}
	}
	
	private boolean check(int i, int j, int k, Coordinate coord) {
		CoordArray ca = table3D[i][j][k];
		for (Coordinate c : ca) {
			if (c.distance(coord) < assembleDist) return true; 
		}
		return false;
	}
	
	private void addCoord(Coordinate c) {
		int indexX = (int)(tableSize * (c.getX() - minX)/(maxX - minX));
		int indexY = (int)(tableSize * (c.getY() - minY)/(maxY - minY));
		int indexZ = (int)(tableSize * (c.getZ() - minZ)/(maxZ - minZ));
		if (indexX < 0) indexX = 0;
		if (indexY < 0) indexY = 0;
		if (indexZ < 0) indexZ = 0;
		if (indexX >= tableSize) indexX = tableSize - 1;
		if (indexY >= tableSize) indexY = tableSize - 1;
		if (indexZ >= tableSize) indexZ = tableSize - 1;
		if (check(indexX,indexY,indexZ,c)) return;
		if (check(indexX-1,indexY,indexZ,c)) return;
		if (check(indexX+1,indexY,indexZ,c)) return;
		if (check(indexX,indexY-1,indexZ,c)) return;
		if (check(indexX,indexY+1,indexZ,c)) return;
		if (check(indexX,indexY,indexZ-1,c)) return;
		if (check(indexX,indexY,indexZ+1,c)) return;
		if (check(indexX-1,indexY-1,indexZ,c)) return;
		if (check(indexX+1,indexY+1,indexZ,c)) return;
		if (check(indexX,indexY-1,indexZ-1,c)) return;
		if (check(indexX,indexY+1,indexZ+1,c)) return;
		if (check(indexX-1,indexY,indexZ-1,c)) return;
		if (check(indexX+1,indexY,indexZ+1,c)) return;
		table3D[indexX][indexY][indexZ].add(c);
	}	
	
	public void assemble() {
		if (coords == null) return;
		calculateMinMaxCoords();
		table3D = new  CoordArray [tableSize][tableSize][tableSize];		
		for (int i = 0; i < tableSize; i++) {
			for (int j = 0; j < tableSize; j++) {
				for (int k = 0; k < tableSize; k++) {
					table3D[i][j][k] = new CoordArray();					
				}				
			}			
		}
		
		
		
	}
	
	
}
