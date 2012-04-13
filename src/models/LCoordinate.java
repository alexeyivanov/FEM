package models;

import core.Coordinate;
import core.ElementException;

public class LCoordinate extends Coordinate {

	double L3;
	
	public LCoordinate(double L0) {
		super(L0, 1-L0, 0);
		L3 = 0;
	}
	
	public LCoordinate(double L0, double L1) {
		super(L0, L1, 1-L0-L1);
		L3 = 0;
	}
	
	public LCoordinate(double L0, double L1, double L2) {
		super(L0, L1, L2);
		L3 = 1-L0-L1-L2;
	}
	
	public double getL(int i) {
		switch (i) {
		case 0:	return x;
		case 1: return y;
		case 2: return z;
		case 3: return L3;	
		}
		throw new ElementException("Wronq L-coordinate number"); 	
	}	
	
	public double getVector(int i) {
		switch (i) {
		case 0:	return x;
		case 1: return y;
		case 2: return z;
		case 3: return L3;	
		}
		throw new ElementException("Wronq L-coordinate number"); 
	
	}

}
