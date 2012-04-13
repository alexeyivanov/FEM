package simple;

import java.util.ArrayList;

import core.*;
import core.Time.Period;
import editor.Mesh;
import Jama.*;

public class LinearElement implements Element{
	
	private static final DOF[] DOFS = {DOFs.displacementX,DOFs.displacementY};
	private ElementNode[] nodes;
	
	protected Material material;
	
	Matrix delta, sigma, sigmaPrev;
	Matrix B;
	double s;
	
	
	public LinearElement(Coordinate a,Coordinate b,Coordinate c, Material material) {
		nodes = new ElementNode[3];
		nodes[0] = new ElementNode(a,DOFS);
		nodes[1] = new ElementNode(b,DOFS);
		nodes[2] = new ElementNode(c,DOFS);
		this.material = material;
		sigma = new Matrix(3,1,0);
		sigmaPrev = new Matrix(3,1,0);
		s = getVolume();
		B = getB();		
		
	}
	
	@Override
	public ElementNode[] getNodes() {
		return nodes;
	}
	
	
	@Override
	public double[][] getMatrix(Time t) {	
		return B.transpose().times(material.linearMatrix(t).times(B)).times(s).getArray();
	}
	
	@Override
	public double[] getFVector(Time t) {						 				        	
		return null;	
	}
	
	@Override
	public double[] getInternalFVector(double[] x, Time t) {
		Matrix vector = new Matrix(x,x.length);
//		B = getB(vector);
		Matrix depsilon = B.times(vector);
		sigma = material.model(depsilon,t,null);					
		return B.transpose().times(sigma).times(s).getColumnPackedCopy();			
	}
	
	public int getNodeResultsNumber(int node) {		
		return 6;	
	}
	
		
	protected double getS() {
		Coordinate i = nodes[0].getCoordinate();
		Coordinate j = nodes[1].getCoordinate();
		Coordinate k = nodes[2].getCoordinate();
		double xi = i.getX();
		double yi = i.getY();
		double xj = j.getX();
		double yj = j.getY();
		double xk = k.getX();
		double yk = k.getY();
		double[][] a = {{xi, yi, 1},
						{xj, yj, 1},
						{xk, yk, 1}};		
		Matrix A = new Matrix(a);
		return A.det()/2;				
	}
	
	protected double getVolume() {	
		return Math.abs(getS());				
	}
	
	protected Matrix getB() {
		
		Coordinate i = nodes[0].getCoordinate();
		Coordinate j = nodes[1].getCoordinate();
		Coordinate k = nodes[2].getCoordinate();
		double xi = i.getX();
		double yi = i.getY();
		double xj = j.getX();
		double yj = j.getY();
		double xk = k.getX();
		double yk = k.getY();
		
//		double xi = coordinates[0].getX();
//		double yi = coordinates[0].getY();
//		double xj = coordinates[1].getX();
//		double yj = coordinates[1].getY();
//		double xk = coordinates[2].getX();
//		double yk = coordinates[2].getY();			
//		double S = getS();
		
		
		double[][] b = {{ (yj-yk)/(2*s), 0,            -(yi-yk)/(2*s), 0,             (yi-yj)/(2*s), 0             },
						{  0,            -(xj-xk)/(2*s), 0,            (xi-xk)/(2*s), 0,             -(xi-xj)/(2*s) },
						{ -(xj-xk)/(2*s), (yj-yk)/(2*s), (xi-xk)/(2*s), -(yi-yk)/(2*s), -(xi-xj)/(2*s), (yi-yj)/(2*s) }};   
		
			
		return new Matrix(b);		
	}
	
	protected Matrix getB(Matrix vector) {
		double [][] displ = vector.getArray();
		Coordinate i = nodes[0].getCoordinate();
		Coordinate j = nodes[1].getCoordinate();
		Coordinate k = nodes[2].getCoordinate();
		double xi = i.getX()+displ[0][0];
		double yi = i.getY()+displ[1][0];
		double xj = j.getX()+displ[2][0];
		double yj = j.getY()+displ[3][0];
		double xk = k.getX()+displ[4][0];
		double yk = k.getY()+displ[5][0];
		
		double[][] b = {{ (yj-yk)/(2*s), 0,            -(yi-yk)/(2*s), 0,             (yi-yj)/(2*s), 0             },
				{  0,            -(xj-xk)/(2*s), 0,            (xi-xk)/(2*s), 0,             -(xi-xj)/(2*s) },
				{ -(xj-xk)/(2*s), (yj-yk)/(2*s), (xi-xk)/(2*s), -(yi-yk)/(2*s), -(xi-xj)/(2*s), (yi-yj)/(2*s) }};   

	
		return new Matrix(b);		
	}

	@Override
	public Period period() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Parameter> parameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SolutionData getSolutionData() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public Drawing draw() {
//		// TODO Auto-generated method stub
//		return null;
//	}

@Override
public double[] getLastResult() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void setSolutionData(SolutionData solutionData) {
	// TODO Auto-generated method stub
	
}

@Override
public Mesh draw(Identificator resultId) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public ArrayList<Identificator> resultsId() {
	// TODO Auto-generated method stub
	return null;
}
		
	
	
}
