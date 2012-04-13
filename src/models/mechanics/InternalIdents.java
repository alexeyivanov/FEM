package models.mechanics;

import core.Identificator;

/**
 * Internal identificator for mechanical problems (rows of B matrices)
 * @author Constantin Shashkin
 */
public class InternalIdents {
	public static Identificator u = new Identificator("Function u - displacement along X axis");
	public static Identificator v = new Identificator("Function v - displacement along Y axis");
	public static Identificator w = new Identificator("Function w - displacement along Z axis");	
	public static Identificator epsilonX = new Identificator("Deformation along X axis");
	public static Identificator epsilonY = new Identificator("Deformation along Y axis");
	public static Identificator epsilonZ = new Identificator("Deformation along Z axis");
	public static Identificator gammaXY = new Identificator("Shear deformation gammaXY");
	public static Identificator gammaYZ = new Identificator("Shear deformation gammaYZ");
	public static Identificator gammaXZ = new Identificator("Shear deformation gammaXZ");	
}
