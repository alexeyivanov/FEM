package models;

import core.Coordinate;
import Jama.Matrix;

public interface ElementCoordinates {
	
	public Coordinate[] getIntegrationPoints(int order);
	
	public double[] getWeightFactors(int order);
	
	public double getV();

}
