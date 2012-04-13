package simple;

import core.Material;
import Jama.Matrix;

public class SofteningTest extends Elastic implements Material {
	
	public SofteningTest(double E, double mu, double gamma) {
		super(E, 0, gamma);
	}
	
//	@Override
	public Matrix sigma(Matrix depsilon, Matrix sigma) {		
		double ex = depsilon.get(0, 0);
		double ey = depsilon.get(1, 0);
		double gxy = depsilon.get(2, 0);
		double ec = 0.1;
		double gc = 0.1;
		double slim = 100;
		double taulim = 100;
		double G = E/2;
		
		double sx = Math.signum(ex)*(Math.sqrt(1/(E*E)+4*ec*Math.abs(ex)/(slim*slim))-1/E)/(2*ec/(slim*slim));
		double sy = Math.signum(ey)*(Math.sqrt(1/(E*E)+4*ec*Math.abs(ey)/(slim*slim))-1/E)/(2*ec/(slim*slim));
		double txy = Math.signum(gxy)*(Math.sqrt(1/(G*G)+4*gc*Math.abs(gxy)/(taulim*taulim))-1/G)/(2*gc/(taulim*taulim));
		if (ex > ec) sx = 80;
		if (ey > ec) sy = 80;
		if (gxy > gc) txy = 80;
		
		double[][] sig = {{sx},{sy},{txy}};
				
 		return new Matrix(sig);		
	}
}
