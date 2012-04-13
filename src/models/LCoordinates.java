package models;

import Jama.Matrix;
import core.Coordinate;
import core.ElementException;

public class LCoordinates implements ElementCoordinates {
	
	
	public static LCoordinate[][] intPoints1D = null;
	public static double[][] weightFactors1D = null;	
	
	public static LCoordinate[][] intPoints2D = {
			{ new LCoordinate(1.0/3, 1.0/3)},
			
			{ new LCoordinate(2.0/3, 1.0/6), 
			  new LCoordinate(1.0/6, 2.0/3),
			  new LCoordinate(1.0/6, 1.0/6)},
			  
			{ new LCoordinate(1.0/3, 1.0/3),
			  new LCoordinate(0.6, 0.2),
			  new LCoordinate(0.2, 0.6),
			  new LCoordinate(0.2, 0.2)},
			  
			{ new LCoordinate(0.816847572980459, 0.091576213509771),
		      new LCoordinate(0.091576213509771, 0.816847572980459),
		      new LCoordinate(0.091576213509771, 0.091576213509771),
		      new LCoordinate(0.108103018168070, 0.445948490915965),
		      new LCoordinate(0.445948490915965, 0.108103018168070),
			  new LCoordinate(0.445948490915965, 0.445948490915965)},
			  
			{ new LCoordinate(1.0/3, 1.0/3),
			  new LCoordinate(0.797426985353087, 0.101286507323456),
			  new LCoordinate(0.101286507323456, 0.797426985353087),
			  new LCoordinate(0.101286507323456, 0.101286507323456),
			  new LCoordinate(0.059715871789770, 0.470142064105115),
			  new LCoordinate(0.470142064105115, 0.059715871789770),
			  new LCoordinate(0.470142064105115, 0.470142064105115)}
	};
	
	public  static double[][] weightFactors2D = {
		{1.0},
		{1.0/3, 1.0/3, 1.0/3},
		{-0.5625, 0.528333333333333, 0.528333333333333, 0.528333333333333},
		{0.109951743655322, 0.109951743655322, 0.109951743655322, 0.223381589678011, 0.223381589678011, 0.223381589678011},
		{0.225, 0.125939180544827, 0.125939180544827, 0.125939180544827, 0.132394152788506}
	};
	
	static double a4 = 0.399403576166799, b4 = 0.100596423833201;
	static double a5 = 0.066550153573664, b5 = 0.433449846426336;
	
	public  static LCoordinate[][] intPoints3D = {
			{ new LCoordinate(1.0/4, 1.0/4, 1.0/4)},
			
			{ new LCoordinate(0.585410196624969, 0.138196601125011, 0.138196601125011),
			  new LCoordinate(0.138196601125011, 0.585410196624969, 0.138196601125011),
			  new LCoordinate(0.138196601125011, 0.585410196624969, 0.138196601125011),
			  new LCoordinate(0.138196601125011, 0.138196601125011, 0.138196601125011)},
			
		    { new LCoordinate(1.0/4, 1.0/4, 1.0/4),
		      new LCoordinate(1.0/2, 1.0/6, 1.0/6),
		      new LCoordinate(1.0/6, 1.0/2, 1.0/6),
		      new LCoordinate(1.0/6, 1.0/6, 1.0/2),
		      new LCoordinate(1.0/6, 1.0/6, 1.0/6)},
		      
			
			{ new LCoordinate(1.0/4, 1.0/4, 1.0/4),
			  new LCoordinate(0.785714285714286, 0.071428571428571, 0.071428571428571),
			  new LCoordinate(0.071428571428571, 0.785714285714286, 0.071428571428571),
			  new LCoordinate(0.071428571428571, 0.071428571428571, 0.785714285714286),
			  new LCoordinate(0.071428571428571, 0.071428571428571, 0.071428571428571),			  
			  new LCoordinate(a4, a4, b4),
			  new LCoordinate(a4, b4, a4),
			  new LCoordinate(b4, a4, b4),
			  new LCoordinate(b4, b4, a4),
			  new LCoordinate(a4, b4, b4),
			  new LCoordinate(b4, a4, a4)},
			  
		    { new LCoordinate(1.0/4, 1.0/4, 1.0/4),
		      new LCoordinate(0, 1.0/3, 1.0/3),
		      new LCoordinate(1.0/3, 0, 1.0/3),
		      new LCoordinate(1.0/3, 1.0/3, 0),
		      new LCoordinate(1.0/3, 1.0/3, 1.0/3),
		      new LCoordinate(0.727272727272727, 0.090909090909091, 0.090909090909091),
		      new LCoordinate(0.090909090909091, 0.727272727272727, 0.090909090909091),
		      new LCoordinate(0.090909090909091, 0.090909090909091, 0.727272727272727),
		      new LCoordinate(0.090909090909091, 0.090909090909091, 0.090909090909091),
		      new LCoordinate(a5, a5, b5),
			  new LCoordinate(a5, b5, a5),
			  new LCoordinate(b5, a5, b5),
			  new LCoordinate(b5, b5, a5),
			  new LCoordinate(a5, b5, b5),
			  new LCoordinate(b5, a5, a5)}                		      			  			  
	};
	
	public static double[][] weightFactors3D = {
		{1.0},
		{1.0/4, 1.0/4, 1.0/4, 1.0/4},
		{0.8, 0.45, 0.45, 0.45, 0.45},
		{-0.013155555555556, 
		  0.007622222222222, 0.007622222222222, 0.007622222222222, 0.007622222222222,  
		  0.024888888888889, 0.024888888888889, 0.024888888888889, 0.024888888888889, 0.024888888888889, 0.024888888888889},
		{ 0.030283678097089, 
		  0.006026785714286, 0.006026785714286, 0.006026785714286, 0.006026785714286,
		  0.011645249086029, 0.011645249086029, 0.011645249086029, 0.011645249086029, 
		  0.010949141561386, 0.010949141561386, 0.010949141561386, 0.010949141561386, 0.010949141561386, 0.010949141561386}		  
	};
	
	Matrix A, Ainv;
	double v;
	
	/**
	 * Calculates coefficients of L coordinates of element
	 * Columns - number of node (base coordinate)
	 * Rows - {x, 1}, {x, y, 1} or {x, y, z, 1}
	 * 
	 *     0    1    2
	 * x  {L00  L01  L02}
	 * y  {L10  L11  L12}
	 * 1  {L20  L21  L22} 
	 * @param c - array of coordinates
	 */
	public LCoordinates(Coordinate[] c) {
		double[][] a = null;
		A = null;
		if (c.length == 2) {
			a =  new double[][] {{c[0].getX(), 1},
					             {c[1].getX(), 1}};
			A = new Matrix(a);
			v = A.det();
		}
		if (c.length == 3) {
			a =  new double[][] {{c[0].getX(), c[0].getY(), 1},
								 {c[1].getX(), c[1].getY(), 1},
								 {c[2].getX(), c[2].getY(), 1}};
			A = new Matrix(a);
			v = A.det()/2;
		}
		if (c.length == 4) {
			a =  new double[][] {{c[0].getX(), c[0].getY(), c[0].getZ(), 1},
								 {c[1].getX(), c[1].getY(), c[1].getZ(), 1},
								 {c[2].getX(), c[2].getY(), c[2].getZ(), 1},
								 {c[3].getX(), c[3].getY(), c[3].getZ(), 1}};
			A = new Matrix(a);
			v = A.det()/6;
		}				
		if (A == null) throw new ElementException("Wrong coordinates number for generation L-coordinates");
		A = A.transpose();
		Ainv = A.inverse();		
	}
	
	public LCoordinate getL(Coordinate c) {
		int size = A.getRowDimension();
		double[] xyz1 = null;
		if (size == 2) xyz1 = new double[] {c.getX(),1};
		if (size == 3) xyz1 = new double[] {c.getX(), c.getY() ,1};
		if (size == 4) xyz1 = new double[] {c.getX(), c.getY(), c.getZ(), 1};		
		Matrix L = Ainv.times(new Matrix(xyz1, size));
		if (size == 2) return  new LCoordinate(L.get(0,0));
		if (size == 3) return  new LCoordinate(L.get(0,0),L.get(1,0));
		if (size == 4) return  new LCoordinate(L.get(0,0),L.get(1,0), L.get(2,0));
		return null;
	}
	
	public Coordinate getXYZ(LCoordinate c) {
		int size = A.getRowDimension();
		double[] L = null;
		if (size == 2) L = new double[] {c.getL(0),1};
		if (size == 3) L = new double[] {c.getL(0), c.getL(1) ,1};
		if (size == 4) L = new double[] {c.getL(0), c.getL(1), c.getL(2), 1};		
		Matrix xyz1 = A.times(new Matrix(L, size));
		if (size == 2) return  new LCoordinate(xyz1.get(0,0));
		if (size == 3) return  new LCoordinate(xyz1.get(0,0),xyz1.get(1,0));
		if (size == 4) return  new LCoordinate(xyz1.get(0,0),xyz1.get(1,0), xyz1.get(2,0));
		return null;
	}
	
	
	public double dLdx(int i) {
		return Ainv.get(i,0);
	}
	
	public double dLdy(int i) {
		return Ainv.get(i,1);
	}
	
	public double dLdz(int i) {
		return Ainv.get(i,2);
	}
	
	public double getV() {
		return v;
	}	
	
	public static int getIntPointsNumber1D(int order) {
		try {
			return intPoints1D[order-1].length; 
		}
		catch (Exception e) {
			throw new ElementException("Order "+ order+ " is not supported in LCoordinates class");
		}
	}
	
	public static int getIntPointsNumber2D(int order) {
		try {
			return intPoints2D[order-1].length; 
		}
		catch (Exception e) {
			throw new ElementException("Order "+ order+ " is not supported in LCoordinates class");
		}
	}
	
	public static int getIntPointsNumber3D(int order) {
		try {
			return intPoints3D[order-1].length; 
		}
		catch (Exception e) {
			throw new ElementException("Order "+ order+ " is not supported in LCoordinates class");
		}
	}

	@Override
	public Coordinate[] getIntegrationPoints(int order) {		
		int size = A.getRowDimension();
		try {
			if (size == 2) return intPoints1D[order-1];
			if (size == 3) return intPoints2D[order-1];
			if (size == 4) return intPoints3D[order-1]; 
		}
		catch (Exception e) {
			throw new ElementException("Order "+ order+ " is not supported in LCoordinates class");
		}				
		return null;
	}

	@Override
	public double[] getWeightFactors(int order) {
		int size = A.getRowDimension();
		try {
			if (size == 2) return weightFactors1D[order-1];
			if (size == 3) return weightFactors2D[order-1];
			if (size == 4) return weightFactors3D[order-1]; 
		}
		catch (Exception e) {
			throw new ElementException("Order "+ order+ " is not supported in LCoordinates class");
		}				
		return null;
	}
}
