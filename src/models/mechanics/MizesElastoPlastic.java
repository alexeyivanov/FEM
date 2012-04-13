package models.mechanics;

import models.mechanics.Elastic.Data;
import Jama.Matrix;
import core.SolutionData;
import core.Time;

public class MizesElastoPlastic extends Elastic {

	double c;
	
//	protected class Data extends Elastic.Data {
//		private static final long serialVersionUID = 1L;				
//
//		@Override
//		public void copy(SolutionData d) {
//			super.copy(d);
//			
//		}
//
//		@Override
//		public void nextTimeStep() {			
//			sigmaPrev = sigma;
//		}				
//	}

	
	public MizesElastoPlastic(double E, double mu, double ro, double c) {
		super(E, mu, ro);
		this.c = c;
	}
	
	@Override
	public SolutionData getMaterialData() {		
		return new Data();
	}
	
	@Override
	public Matrix model(Matrix vector, Time t, SolutionData materialData) {
		super.model(vector, t, materialData);
		
		Data d = (Data)materialData;
//		d.sigma = linearMatrix(t).times(vector);
		double Sx = d.sigma.get(0, 0);
		double Sy = d.sigma.get(1, 0);
		double Sz = mu*(Sx+Sy);
		double Txy = d.sigma.get(2, 0);		
		double S1 = (Sx+Sy)/2+Math.sqrt(Math.pow((Sx-Sy)/2,2) + Math.pow(Txy,2));
		double S3 = (Sx+Sy)/2-Math.sqrt(Math.pow((Sx-Sy)/2,2) + Math.pow(Txy,2));
		double SV = (Sx+Sy+Sz)/3;
		double deviator = (S1-S3)/2;
		if (deviator < c) return d.sigma;
		double k = c/deviator;		
		double [] sv = {SV,SV,0};
		Matrix sigmaV =  new Matrix(sv,3);
		Matrix sigmaDev =  d.sigma.minus(sigmaV);
		d.sigma = sigmaV.plus(sigmaDev.times(k));				
		return d.sigma; 
	}

}
