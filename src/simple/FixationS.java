package simple;

import java.util.ArrayList;

import core.Coordinate;
import core.DOF;
import editor.Drawing;
import editor.Mesh;
import core.Element;
import core.ElementNode;
import core.Identificator;
import core.Parameter;
import core.SolutionData;
import core.Time;
import core.Time.Period;

public class FixationS implements Element {

	ElementNode node;
	
	public FixationS(Coordinate c, DOF dof, double value) {
		node = new ElementNode(c, dof, value);				
	}

	@Override
	public Period period() {
		
		return null;
	}

	@Override
	public ArrayList<Parameter> parameters() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override 
	public ElementNode[] getNodes() {
		return new ElementNode[] {node};
	}

	@Override
	public double[][] getMatrix(Time t) {
		return null;
	}

	@Override
	public double[] getFVector(Time t) {
		return null;
	}

	@Override
	public double[] getInternalFVector(double[] x, Time t) {
		return null;
	}

	
	@Override
	public SolutionData getSolutionData() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public Drawing draw() {
		// TODO Auto-generated method stub
		return null;
	}

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
