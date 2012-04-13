package models.mechanics.planeXY;

import models.AbstractFE;
import models.mechanics.DOFs;
import models.mechanics.InternalIdents;
import models.mechanics.ResultIdents;
import core.Coordinate;
import core.DOF;
import core.Identificator;
import core.Material;
import core.Time;
import editor.Mesh;

public abstract class PlaneFE extends AbstractFE {

	protected static DOF[] dofs = {DOFs.displacementX,DOFs.displacementY};
	
	public PlaneFE(int intPointsNumber, int order, Material material, Time t) {
		super(intPointsNumber, order, material, t);	
	}
	
	protected Coordinate[] getDeformedCoordinates() {
		double[] x = solutionData.getX();		
		Coordinate[] coords = new Coordinate[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			coords[i] = nodes[i].getCoordinate();
			if (ResultIdents.deformedMeshScale != 0) {
				Coordinate deformed = new Coordinate(x[i*2+0], x[i*2+1], 0);
				deformed.multEquals(ResultIdents.deformedMeshScale);
				coords[i].plusEquals(deformed);
			}
		}		
		return coords;
	}
	
	protected void setResults(Mesh m, Identificator resultId, int[] points) {
		double[] x = solutionData.getX();		
		if (resultId == dofs[0]) {
			for (int i =0; i < points.length; i++) {
				m.setResult(points[i], x[i*2+0]);
			}			
			return;
		}
		if (resultId == dofs[1]) {
			for (int i =0; i < points.length; i++) {
				m.setResult(points[i], x[i*2+1]);
			}
			return;
		}
		if (resultId == ResultIdents.maxDisplacement) {
			for (int i =0; i < points.length; i++) {
				double d = Math.sqrt(x[i*2+0]*x[i*2+0]+x[i*2+1]*x[i*2+1]);
				m.setResult(points[i], d);
			}			
			return;			
		}		
		double d = material.getResult(resultId, solutionData.getIntPointsData(0));
		for (int p : points) m.setResult(p, d);		
		return;		
	}
	
}
