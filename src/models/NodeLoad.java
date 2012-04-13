package models;

import java.util.ArrayList;

import core.Coordinate;
import core.DOF;
import core.DoubleParameter;
import core.Element;
import core.ElementNode;
import core.Identificator;
import core.Parameter;
import core.SolutionData;
import core.Time;
import core.Time.Period;
import editor.Mesh;

public class NodeLoad implements Element {

	protected Time.Period period;		
	protected ElementNode[] nodes = null;	
	protected Double value;	
	protected String description = "";
	
	public NodeLoad(Coordinate c, DOF dof, double value, Time t) {
		nodes = new ElementNode[] {new ElementNode(c, dof)};
		period = t.periodFrom();
		this.value = value;		
	}
	
	@Override
	public Time.Period period() {		
		return period;
	}

	@Override
	public ArrayList<Parameter> parameters() {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();		
		parameters.add(new DoubleParameter(nodes[0].getDOFs()[0].getNameF(), value));		
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
		return new double[] {value*(t.get()*1+1)};
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
