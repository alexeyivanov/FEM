package models.mechanics;


import core.DOF;
import core.Identificator;

/**
 * Internal identificator for mechanical problems (rows of B matrices)
 * @author Constantin Shashkin
 */
public class ResultIdents {
	public static Identificator displacementX = DOFs.displacementX;
	public static Identificator displacementY = DOFs.displacementY;
	public static Identificator displacementZ = DOFs.displacementZ;
	public static Identificator rotationAroundX = DOFs.rotationAroundX;
	public static Identificator rotationAroundY = DOFs.rotationAroundY;
	public static Identificator rotationAroundZ = DOFs.rotationAroundZ;
	public static Identificator maxDisplacement = new Identificator("Maximal displacement sqrt(x^2+y^2+z^2)");
	
	public static Identificator sigmaX = new Identificator("Normal stress Sigma_x");
	public static Identificator sigmaY = new Identificator("Normal stress Sigma_y");
	public static Identificator sigmaZ = new Identificator("Normal stress Sigma_z");
	public static Identificator tauXY = new Identificator("Shear stress Tau_xy");
	public static Identificator tauYZ = new Identificator("Shear stress Tau_yz");
	public static Identificator tauXZ = new Identificator("Shear stress Tau_xz");
	public static Identificator sigma1 = new Identificator("Principial stress Sigma_1");
	public static Identificator sigma2 = new Identificator("Principial stress Sigma_2");
	public static Identificator sigma3 = new Identificator("Principial stress Sigma_3");
	
	public static Identificator epsilonX = new Identificator("Normal strain Epsilon_x");
	public static Identificator epsilonY = new Identificator("Normal strain Epsilon_y");
	public static Identificator epsilonZ = new Identificator("Normal strain Epsilon_z");
	public static Identificator gammaXY = new Identificator("Shear strain Gamma_xy");
	public static Identificator gammaYZ = new Identificator("Shear strain Gamma_yz");
	public static Identificator gammaXZ = new Identificator("Shear strain Gamma_xz");
	public static Identificator epsilon1 = new Identificator("Principial strain Epsilon_1");
	public static Identificator epsilon2 = new Identificator("Principial strain Epsilon_2");
	public static Identificator epsilon3 = new Identificator("Principial strain Epsilon_3");
	public static Identificator maxGamma = new Identificator("Maximal shear strain");
	
	public static double deformedMeshScale = 0;
	
}

