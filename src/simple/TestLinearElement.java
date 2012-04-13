package simple;

import core.*;
import Jama.*;

public class TestLinearElement {
	
	
	public static void main(String[] args) {
		Elastic material = new Elastic(10000,0.3,0);
		LinearElement le = new LinearElement(new Coordinate(0,0,0),
											 new Coordinate(1,0,0),
											 new Coordinate(1,1,0), 
											 material);
		
		
		Matrix K = new Matrix(le.getMatrix(null));
		double[] f = le.getFVector(null);
		Matrix F = new Matrix(f,f.length);
		System.out.print("K matrix");
		K.print(10, 3);
		double det = K.det();
		System.out.print("det=");
		System.out.println(det);
		System.out.print("F vector");
		F.print(10, 3);
		
		Elements elements = new Elements();
		
		elements.add(le);
		elements.add(new Load(new Coordinate(1,1,0), DOFs.displacementY, -1));
		elements.add(new FixationS(new Coordinate(0,0,0),DOFs.displacementX,0));
		elements.add(new FixationS(new Coordinate(0,0,0),DOFs.displacementY,0));
		elements.add(new FixationS(new Coordinate(0,1,0),DOFs.displacementY,0));		
	}
}
