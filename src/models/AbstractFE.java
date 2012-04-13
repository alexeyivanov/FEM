package models;

import java.util.ArrayList;

import core.*;

import Jama.Matrix;

/**
 * Abstract class for any finite element.
 * @author Constantin Shashkin
 *
 */

public abstract class AbstractFE implements Element {
	
	protected FESolutionData solutionData;	
	protected int order;
	protected Time.Period period;		
	protected ElementNode[] nodes = null;	
	protected Material material;
	protected String description ="";
	
	
	public AbstractFE(int intPointsNumber, int order, Material material, Time t) {
		setMaterial(material);
		period = t.periodFrom();
		this.order = order;
		solutionData = new FESolutionData(intPointsNumber, material);		
	}	
	
	public int getOrder() {
		return order;
	}
	
	public void setMaterial(Material material) {
		if (!material.available(getBMatrixMembers())) 
			throw new ElementException("Wrong material " + material.getClass().getName() +  "for element type " + this.getClass().getName());
		this.material = material;
	}

	@Override
	public Time.Period period() {
		return period; 
	}
		
	@Override
	public ArrayList<Parameter> parameters() {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();				
		parameters.add(new Parameter("Element material model", material));
		parameters.add(new Parameter("Description", description));
		return parameters;
	}

	@Override
	public ElementNode[] getNodes() {	
		if (nodes == null) throw new ElementException("Nodes of element were not defined in element " + this.getClass().getName() + 
		". Put nodes definition in constructor of element");
		if (getV() < 1E-10) throw new ElementException("Volume of element  " + this.getClass().getName() + 
		" is about zero");
		return nodes;
	}

	@Override
	public double[][] getMatrix(Time t) {
		ElementCoordinates L = getLocalCoordinates();
		Matrix K = null;		
		Coordinate[] intPoints = L.getIntegrationPoints(order);
		double[] factors = L.getWeightFactors(order);		
		for (int i = 0; i < intPoints.length; i++) {						
			Matrix B = getB(intPoints[i]);
			Matrix Ki =  B.transpose().times(material.linearMatrix(t)).times(B).times(factors[i]);						
			if (K == null)
				K = Ki;
			else
				K.plusEquals(Ki);			
		}
		return K.times(getV()).getArray();		
	}

	@Override
	public double[] getFVector(Time t) {
		ElementCoordinates L = getLocalCoordinates();
		Matrix F = null;		
		Coordinate[] intPoints = L.getIntegrationPoints(order);
		double[] factors = L.getWeightFactors(order);		
		for (int i = 0; i < intPoints.length; i++) {			
			Matrix N = getN(intPoints[i]);
			Matrix Fi =  N.transpose().times(material.volumeF(t)).times(factors[i]);						
			if (F == null)
				F = Fi;
			else
				F.plusEquals(Fi);			
		}
		return F.times(getV()).getColumnPackedCopy();		
	}

	@Override
	public double[] getInternalFVector(double[] x, Time t) {
		ElementCoordinates L = getLocalCoordinates();
		solutionData.setX(x);		
		Matrix vx = new Matrix(x,x.length);		
		Matrix F = null;		
		Coordinate[] intPoints = L.getIntegrationPoints(order);
		double[] factors = L.getWeightFactors(order);
		for (int i = 0; i < intPoints.length; i++) {			
			Matrix B = getB(intPoints[i]);			
			Matrix modelInput = B.times(vx);
			Matrix modelOutput = material.model(modelInput, t, solutionData.getIntPointsData(i));
			Matrix Fi = B.transpose().times(modelOutput).times(factors[i]);  
			if (F == null)
				F = Fi;
			else
				F.plusEquals(Fi);			
		}
		return F.times(getV()).getColumnPackedCopy();
	}	
	
	@Override
	public double[] getLastResult() {
		return solutionData.getX();
	}
	
	@Override
	public SolutionData getSolutionData() {		
		return solutionData;		
	}	
	
	
	@Override
	public void setSolutionData(SolutionData solutionData) {
		this.solutionData = (FESolutionData)solutionData;
	}

	
	public double getV() {
		ElementCoordinates L = getLocalCoordinates();
		return L.getV();
	}
	
	
	@Override
	public ArrayList<Identificator> resultsId() {
		ArrayList<Identificator>  resultsId = new ArrayList<Identificator>(); 
		DOF[] dofs = getNMatrixMembers();
		for (DOF dof : dofs) {
			resultsId.add(dof);
		}
		resultsId.addAll(material.resultsId());
		return resultsId;
	}
	
	public abstract ElementCoordinates getLocalCoordinates();
	
	public abstract DOF[] getNMatrixMembers();
	
	public abstract Matrix getN(Coordinate c);
	
	public abstract Identificator[] getBMatrixMembers();
	
	public abstract Matrix getB(Coordinate c);
	
}
