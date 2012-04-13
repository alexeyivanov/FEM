package models.mechanics.planeXY;

import java.util.ArrayList;

import Jama.Matrix;
import core.*;
import editor.Mesh;
import models.AbstractFE;
import models.ElementCoordinates;
import models.LCoordinate;
import models.LCoordinates;
import models.mechanics.DOFs;
import models.mechanics.InternalIdents;
import models.mechanics.ResultIdents;

public class Triangle1 extends PlaneFE {
	
	private static Identificator[] BMatrixMembers = {InternalIdents.epsilonX, InternalIdents.epsilonY, InternalIdents.gammaXY};
	
	private LCoordinates L;
	private Matrix B;	
	
	public Triangle1(Coordinate[] coordinates, Material material, Time t) {		
		super(1, 1, material, t);		
		nodes = new ElementNode[] {
				new ElementNode(coordinates[0], dofs),
				new ElementNode(coordinates[1], dofs),
				new ElementNode(coordinates[2], dofs)
		};
		L = new LCoordinates(coordinates);
		calculateB();
	}
		
	private void calculateB() {
		double[][] b = {{ L.dLdx(0), 0,         L.dLdx(1), 0,         L.dLdx(2), 0         },
		                { 0,         L.dLdy(0), 0,         L.dLdy(1), 0,         L.dLdy(2) },
		                { L.dLdy(0), L.dLdx(0), L.dLdy(1), L.dLdx(1), L.dLdy(2), L.dLdx(2) }};
		
		
		B = new Matrix(b);
	}
	
	@Override
	public ElementCoordinates getLocalCoordinates() {
		return L;
	}
	
	@Override
	public DOF[] getNMatrixMembers() {
		return dofs;
	}
	
	@Override
	public Matrix getN(Coordinate c) {
		LCoordinate cL;
		if (!(c instanceof LCoordinate)) cL = (LCoordinate) c;
		else cL = L.getL(c);
		double[][] n = { {cL.getL(0), 0, cL.getL(1), 0, cL.getL(2), 0},
				         {0, cL.getL(0), 0, cL.getL(1), 0, cL.getL(2)}};
		return new Matrix(n);
	}

	@Override
	public Identificator[] getBMatrixMembers() {
		return BMatrixMembers; 
	}
	
	@Override
	public Matrix getB(Coordinate c) {
		return B;
	}
	

	@Override	
	public ArrayList<Identificator> resultsId() {
		ArrayList<Identificator>  resultsId = super.resultsId(); 		
		resultsId.add(ResultIdents.maxDisplacement);
		return resultsId;
	}
	
	
	@Override
	public Mesh draw(Identificator resultId) {
		Mesh m = new Mesh();
		Coordinate[] coords = getDeformedCoordinates();		
		int[] points = { m.point(coords[0]), m.point(coords[1]), m.point(coords[2])};
		setResults(m,resultId,points);
		m.triangle(points[0], points[1], points[2]);
		return m;
	}

	
}
