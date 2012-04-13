package simple;

import Jama.Matrix;
import java.lang.Math;

import core.*;





public class MizesPlastic extends Elastic implements Material  {

	double c = 10;
	
	public MizesPlastic(double E, double mu, double gamma, double c) {
		super(E,mu,gamma);
		this.c = c;			
	} 
	
	@Override
	public Matrix model(Matrix depsilon, Time t, SolutionData data) {
 		Matrix sigma_el = linearMatrix(t).times(depsilon);
		double Sx = sigma_el.get(0, 0);
		double Sy = sigma_el.get(1, 0);
		double Sz = mu*(Sx+Sy);
		double Txy = sigma_el.get(2, 0);		
		double S1 = (Sx+Sy)/2+Math.sqrt(Math.pow((Sx-Sy)/2,2) + Math.pow(Txy,2));
		double S3 = (Sx+Sy)/2-Math.sqrt(Math.pow((Sx-Sy)/2,2) + Math.pow(Txy,2));
		double SV = (Sx+Sy+Sz)/3;
//		double SV = (Sx+Sy)/2;
		double deviator = (S1-S3)/2;
		if (deviator < c) return sigma_el;
		double k = c/deviator;		
		double [] sv = {SV,SV,0};
		Matrix sigmaV =  new Matrix(sv,3);
		Matrix sigmaDev =  sigma_el.minus(sigmaV);
//		Matrix sig = sigmaV.plus(sigmaDev.times(k));
		return sigmaV.plus(sigmaDev.times(k));		
	}

	

}
