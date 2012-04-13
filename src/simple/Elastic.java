package simple;

import java.util.ArrayList;


import core.Identificator;
import core.Material;
import core.Parameter;
import core.SolutionData;
import core.Time;
import Jama.Matrix;

public class Elastic implements Material {

	Double E, mu, ro;
	
	public Elastic(double E, double mu, double ro) {
		this.E = E;
		this.mu = mu;
		this.ro = ro;		
	}	


	@Override
	public ArrayList<Parameter> parameters() {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter("Elasticity modulus", E));
		parameters.add(new Parameter("Poisson ratio", mu));
		parameters.add(new Parameter("Density", ro));
		
		return null;
	}


	@Override
	public Matrix linearMatrix(Time t) {
		double[][] d = {{1-mu, mu, 0}, 
		        {mu, 1-mu, 0},
		        {0,  0, 0.5-mu}};
		
		Matrix D = new Matrix(d);		
		return D.times(E/(1-2*mu)/(1+mu));		
	}

	@Override
	public boolean available(Identificator[] idents) {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public SolutionData getMaterialData() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Matrix model(Matrix vector, Time t, SolutionData materialData) {
		// TODO Auto-generated method stub
		return linearMatrix(t).times(vector);
	}


//	@Override
	public double volumeLoad() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Matrix volumeF(Time t) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ArrayList<Identificator> resultsId() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public double getResult(Identificator resultId, SolutionData materialData) {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
