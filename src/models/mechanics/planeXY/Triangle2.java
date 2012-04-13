package models.mechanics.planeXY;

import Jama.Matrix;
import core.Coordinate;
import core.DOF;
import core.ElementNode;
import core.Identificator;
import core.Material;
import core.Time;
import editor.Mesh;
import models.AbstractFE;
import models.ElementCoordinates;
import models.LCoordinate;
import models.LCoordinates;
import models.mechanics.DOFs;
import models.mechanics.InternalIdents;
import models.mechanics.ResultIdents;

public class Triangle2 extends PlaneFE {

	private static DOF[] dofs = {DOFs.displacementX,DOFs.displacementY};
	private static Identificator[] BMatrixMembers = {InternalIdents.epsilonX, InternalIdents.epsilonY, InternalIdents.gammaXY};
	private LCoordinates L;
	
	public Triangle2(Coordinate[] coordinates, Material material, Time t) {
		super(LCoordinates.getIntPointsNumber2D(2), 2, material, t);
		Coordinate c01 = new Coordinate(coordinates[0]);
		c01.plusEquals(coordinates[1]);
		c01.multEquals(0.5);
		Coordinate c12 = new Coordinate(coordinates[1]);
		c12.plusEquals(coordinates[2]);
		c12.multEquals(0.5);
		Coordinate c20 = new Coordinate(coordinates[2]);
		c20.plusEquals(coordinates[0]);
		c20.multEquals(0.5);
		
		nodes = new ElementNode[] {
				new ElementNode(coordinates[0], dofs),
				new ElementNode(coordinates[1], dofs),
				new ElementNode(coordinates[2], dofs),
				new ElementNode(c01, dofs),
				new ElementNode(c12, dofs),
				new ElementNode(c20, dofs),				
		};
		L = new LCoordinates(coordinates);
		
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
		if (c instanceof LCoordinate) cL = (LCoordinate) c;
		else cL = L.getL(c);
		double[] N = new double[6];
		N[0] = cL.getL(0)*(2*cL.getL(0)-1);
		N[1] = cL.getL(1)*(2*cL.getL(1)-1);
		N[2] = cL.getL(2)*(2*cL.getL(2)-1);
		N[3] = 4*cL.getL(0)*cL.getL(1);
		N[4] = 4*cL.getL(0)*cL.getL(2);
		N[5] = 4*cL.getL(1)*cL.getL(2);
		double[][] n = {{ N[0], 0, N[1], 0, N[2], 0, N[3], 0, N[4], 0, N[5], 0},
				        { 0, N[0], 0, N[1], 0, N[2], 0, N[3], 0, N[4], 0, N[5]}};
		return new Matrix(n);
	}

	@Override
	public Identificator[] getBMatrixMembers() {
		return BMatrixMembers; 
	}
	
	@Override
	public Matrix getB(Coordinate c) {
		LCoordinate cL;
		if (c instanceof LCoordinate) cL = (LCoordinate) c;
		else cL = L.getL(c);
		double[] dNdx = new double[6];
		dNdx[0] = (4*cL.getL(0)-1)*L.dLdx(0);
		dNdx[1] = (4*cL.getL(1)-1)*L.dLdx(1);
		dNdx[2] = (4*cL.getL(2)-1)*L.dLdx(2);
		dNdx[3] = (4*cL.getL(1))*L.dLdx(0) + (4*cL.getL(0))*L.dLdx(1);
		dNdx[4] = (4*cL.getL(2))*L.dLdx(1) + (4*cL.getL(1))*L.dLdx(2);		
		dNdx[5] = (4*cL.getL(0))*L.dLdx(2) + (4*cL.getL(2))*L.dLdx(0);
		
		double[] dNdy = new double[6];
		dNdy[0] = (4*cL.getL(0)-1)*L.dLdy(0);
		dNdy[1] = (4*cL.getL(1)-1)*L.dLdy(1);
		dNdy[2] = (4*cL.getL(2)-1)*L.dLdy(2);
		dNdy[3] = (4*cL.getL(1))*L.dLdy(0) + (4*cL.getL(0))*L.dLdy(1);
		dNdy[4] = (4*cL.getL(2))*L.dLdy(1) + (4*cL.getL(1))*L.dLdy(2);		
		dNdy[5] = (4*cL.getL(0))*L.dLdy(2) + (4*cL.getL(2))*L.dLdy(0);
		
		double[][] b = {{ dNdx[0], 0, dNdx[1], 0, dNdx[2], 0, dNdx[3], 0, dNdx[4], 0,  dNdx[5], 0},
				        { 0, dNdy[0], 0, dNdy[1], 0, dNdy[2], 0, dNdy[3], 0, dNdy[4], 0,  dNdy[5]},
				        { dNdy[0], dNdx[0], dNdy[1], dNdx[1], dNdy[2], dNdx[2], dNdy[3], dNdx[3], dNdy[4], dNdx[4], dNdy[5], dNdx[5]}};
		
//		Matrix B = new Matrix(b);
//		B.print(10, 3);
		
		return new Matrix(b);
	}	
	
	@Override
	public Mesh draw(Identificator resultId) {		
		Mesh m = new Mesh();		
		Coordinate[] coords = getDeformedCoordinates();				
		int i1 = m.point(coords[0]);
		int i2 = m.point(coords[1]);
		int i3 = m.point(coords[2]);
		int i12 = m.point(coords[3]);
		int i23 = m.point(coords[4]);
		int i13 = m.point(coords[5]);
		setResults(m,resultId,new int[] {i1,i2,i3,i12,i23,i13});				
		m.face(i1, i12, i13);
		m.face(i2, i12, i23);
		m.face(i3, i23, i13);
		m.face(i12, i23, i13);
		m.line(i1, i12);
		m.line(i12, i2);
		m.line(i2, i23);
		m.line(i23, i3);
		m.line(i3, i13);
		m.line(i13, i1);		
		return m;
	}
	
	
}
