package models.mechanics;

import java.util.ArrayList;
import java.util.Arrays;

import Jama.Matrix;
import core.*;

public class Elastic implements Material {

	private static Identificator[] mat1D = {InternalIdents.epsilonX};
	private static Identificator[] mat2D = {InternalIdents.epsilonX, InternalIdents.epsilonY, InternalIdents.gammaXY};
	private static Identificator[] mat3D = { 
		 InternalIdents.epsilonX,
  	 	 InternalIdents.epsilonY,
		 InternalIdents.epsilonZ,
		 InternalIdents.gammaXY,
		 InternalIdents.gammaXZ,
		 InternalIdents.gammaYZ};
	private static Identificator[] mat2D_PlainStrain = {InternalIdents.epsilonX, InternalIdents.epsilonY, InternalIdents.gammaXY};
	
	private static Identificator[] results3D = {
		ResultIdents.sigmaX,
		ResultIdents.sigmaY,
		ResultIdents.sigmaZ,
		ResultIdents.tauXY,
		ResultIdents.tauYZ,
		ResultIdents.tauXZ,
		ResultIdents.sigma1,
		ResultIdents.sigma2,
		ResultIdents.sigma3
	};
	
	enum ModelType {mat1D, mat2D, mat3D, mat2D_PlainStrain, mat2D_AxialSymmetry};
	ModelType modelType;
	double E,mu,ro;
	String description;
	
	public Elastic(double E, double mu, double ro) {
		this.E = E;
		this.mu = mu;
		this.ro = ro;		
	}
	
	public void setPlaneStrainMode() {
		if (modelType != ModelType.mat2D) throw new ElementException("Plane strain mode can used only for 2D models");
		modelType = ModelType.mat2D_PlainStrain; 
	}
	
	protected class Data extends SolutionData {
		private static final long serialVersionUID = 1L;		
		
		Matrix sigma = null, sigma_prev = null;
		Matrix epsilon = null, epsilon_prev = null;

		@Override
		public void nextTimeStep() {			
			sigma_prev = sigma;
			epsilon_prev = epsilon;
		}				
	}
	
	@Override
	public ArrayList<Parameter> parameters() {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new DoubleParameter("Elasticity modulus", E,0,Double.MAX_VALUE));
		parameters.add(new DoubleParameter("Poisson ratio", mu, 0, 0.4999));
		parameters.add(new DoubleParameter("Density", ro, 0, Double.MAX_VALUE));
		parameters.add(new Parameter("Description", description));
		return parameters;
	}	
	
	@Override
	public SolutionData getMaterialData() {		
		return new Data();
	}

	@Override
	public Matrix linearMatrix(Time t) {
		if (modelType == ModelType.mat2D) {
			double[][] d = {{1-mu, mu, 0}, 
							{mu, 1-mu, 0},
							{0,  0, 0.5-mu}};
			
			Matrix D = new Matrix(d);		
			return D.times(E/(1-2*mu)/(1+mu));	
		}
		throw new ElementException("Unknown material mode");	
	}

	@Override
	public Matrix model(Matrix vector, Time t, SolutionData materialData) {
		Data data = (Data)materialData; 		
		data.epsilon = vector;
		if (data.sigma_prev != null)
			data.sigma = data.sigma_prev.plus(linearMatrix(t).times(data.epsilon.minus(data.epsilon_prev)));
		else
			data.sigma = linearMatrix(t).times(vector);
		return data.sigma; 
	}

	@Override
	public boolean available(Identificator[] idents) {
		if (mat1D.equals(idents)) {
			modelType = ModelType.mat1D;			
			return true;
		}
		if (Arrays.equals(mat2D, idents)) {
			modelType = ModelType.mat2D;			
			return true;
		}
		if (mat3D.equals(idents)) {
			modelType = ModelType.mat3D;			
			return true;
		}
		return false;
	}

	@Override
	public Matrix volumeF(Time t) {
		double[] vF = null;
		if (modelType == ModelType.mat1D) {
			vF = new double[1];
			vF[0] = ro*Global.g_force*Global.gravitationDirection[0];
		}
		if (modelType == ModelType.mat2D) {
			vF = new double[2];
			for (int i = 0; i < 2; i++) vF[i] = ro*Global.g_force*Global.gravitationDirection[i];
		}
		if (modelType == ModelType.mat3D) {
			vF = new double[3];
			for (int i = 0; i < 3; i++) vF[i] = ro*Global.g_force*Global.gravitationDirection[i];
		}
		if (vF == null) throw new ElementException("Unknown material mode");
		return new Matrix(vF, vF.length);
	}

	@Override
	public ArrayList<Identificator> resultsId() {
		ArrayList<Identificator>  resultsId = new ArrayList<Identificator>(); 				
		resultsId.add(ResultIdents.sigmaX);
		resultsId.add(ResultIdents.sigmaY);
		resultsId.add(ResultIdents.sigmaZ);
		resultsId.add(ResultIdents.tauXY);
		resultsId.add(ResultIdents.tauYZ);
		resultsId.add(ResultIdents.tauXZ);
		resultsId.add(ResultIdents.sigma1);
		resultsId.add(ResultIdents.sigma2);
		resultsId.add(ResultIdents.sigma3);
		
		resultsId.add(ResultIdents.epsilonX);
		resultsId.add(ResultIdents.epsilonY);
		resultsId.add(ResultIdents.epsilonZ);
		resultsId.add(ResultIdents.gammaXY);
		resultsId.add(ResultIdents.gammaYZ);
		resultsId.add(ResultIdents.gammaXZ);
		resultsId.add(ResultIdents.epsilon1);
		resultsId.add(ResultIdents.epsilon2);
		resultsId.add(ResultIdents.epsilon3);
		
		resultsId.add(ResultIdents.maxGamma);				
		
		return resultsId;
	}

	@Override
	public double getResult(Identificator resultId, SolutionData materialData) {
		Data data = (Data)materialData;
		if (modelType == ModelType.mat1D) {
			if (resultId == ResultIdents.sigmaX) return data.sigma.get(0, 0);
			if (resultId == ResultIdents.epsilonX) return data.epsilon.get(0, 0);
			if (resultId == ResultIdents.sigma1) return data.sigma.get(0, 0);
			return 0;
		}
		if (modelType == ModelType.mat2D) {
			if (resultId == ResultIdents.sigmaX) return data.sigma.get(0, 0);
			if (resultId == ResultIdents.sigmaY) return data.sigma.get(1, 0);
			if (resultId == ResultIdents.tauXY) return data.sigma.get(2, 0);
			if (resultId == ResultIdents.epsilonX) return data.epsilon.get(0, 0);
			if (resultId == ResultIdents.epsilonY) return data.epsilon.get(1, 0);
			if (resultId == ResultIdents.gammaXY) return data.epsilon.get(2, 0);
//			if (resultId == ResultIdents.sigma1) return data.sigma.get(0, 0);
			return 0;
		}
		
		if (modelType == ModelType.mat3D) {
			if (resultId == ResultIdents.sigmaX) return data.sigma.get(0, 0);
			if (resultId == ResultIdents.sigmaY) return data.sigma.get(1, 0);
			if (resultId == ResultIdents.sigmaZ) return data.sigma.get(2, 0);
			if (resultId == ResultIdents.tauXY) return data.sigma.get(3, 0);
			if (resultId == ResultIdents.tauYZ) return data.sigma.get(4, 0);
			if (resultId == ResultIdents.tauXZ) return data.sigma.get(5, 0);
			if (resultId == ResultIdents.epsilonX) return data.epsilon.get(0, 0);
			if (resultId == ResultIdents.epsilonY) return data.epsilon.get(1, 0);
			if (resultId == ResultIdents.epsilonZ) return data.epsilon.get(2, 0);
			if (resultId == ResultIdents.gammaXY) return data.epsilon.get(3, 0);
			if (resultId == ResultIdents.gammaYZ) return data.epsilon.get(4, 0);
			if (resultId == ResultIdents.gammaXZ) return data.epsilon.get(5, 0);
//			if (resultId == ResultIdents.sigma1) return data.sigma.get(0, 0);
			return 0;
		}
		
		throw new ElementException("Unknown material mode");
				
	}


}
