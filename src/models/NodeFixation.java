package models;

import java.util.ArrayList;
import core.*;
import editor.Mesh;

public class NodeFixation implements Element {

	
	protected Time.Period period;		
	protected ElementNode[] nodes = null;
	protected String description = "";
	
	public NodeFixation(Coordinate c, DOF dof, double value, Time t) {
		nodes = new ElementNode[] {new ElementNode(c, dof,value)};
		period = t.periodFrom();		
	}
	
	@Override
	public Time.Period period() {		
		return period;
	}

	@Override
	public ArrayList<Parameter> parameters() {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();		
		parameters.add(new DoubleParameter(nodes[0].getDOFs()[0].getName(), nodes[0].getFixedValues()[0]));
		parameters.add(new Parameter("Description", description));
		return parameters;
	}

	@Override
	public ElementNode[] getNodes() {
		return nodes;
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
	public double[] getLastResult() {
		return null;
	}

	@Override
	public SolutionData getSolutionData() {
		return null;
	}
	
	@Override
	public void setSolutionData(SolutionData solutionData) {		
	}

	@Override
	public Mesh draw(Identificator resultId) {
		return null;
	}

	@Override
	public ArrayList<Identificator> resultsId() {
		return null;
	}

}
