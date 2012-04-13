package simple;

import core.Identificator;

public class Results {
	static Identificator displacementX = new Identificator(DOFs.displacementX.getName());
	static Identificator displacementY = new Identificator(DOFs.displacementY.getName());
	
	static Identificator sigmaMatrix = new Identificator("Array of stresses in element");	
}
