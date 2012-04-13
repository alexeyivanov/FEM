package models;

import core.Material;
import core.SolutionData;

/**
 * Container for internal data of finite element
 * @author Constantin Shashkin
 */
public class FESolutionData extends SolutionData {
	private static final long serialVersionUID = 1L;
	double[] x = null;
	SolutionData[] intPointsData;		
	
	/**
	 * Constructor
	 * @param intPointsNumber - number of integration points
	 * @param material - physical properties of finite element
	 */
	public FESolutionData(int intPointsNumber, Material material) {			
		intPointsData = new SolutionData[intPointsNumber];
		for (int i = 0; i < intPointsNumber; i++) intPointsData[i] = material.getMaterialData();
	}

	@Override
	public void nextTimeStep() {					
		for (int i = 0; i < intPointsData.length; i++) intPointsData[i].nextTimeStep();
	}
	
	/**
	 * @return result vector of node DOFs in element
	 */
	public double[] getX() {
		return x;
	}
	
	/**
	 * Sets result of solution. Usually should be used only by solver.
	 * @param x - internal vector of node DOFs in element
	 */
	public void setX(double[] x) {
		this.x = x;
	}
	
	/**
	 * Returns internal data for integration point i 
	 * @param i - number of integration point
	 * @return
	 */
	public SolutionData getIntPointsData(int i) {
		return intPointsData[i];
	}
	
}
