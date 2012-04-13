package models.mechanics;

import core.DOF;

/**
 * Node degrees of freedom and forces for mechanical problems
 * @author Constantin Shashkin
 */

public class DOFs {
	public static DOF displacementX = new DOF("Displacement along X axis", "Load along X axis");
	public static DOF displacementY = new DOF("Displacement along Y axis", "Load along Y axis");
	public static DOF displacementZ = new DOF("Displacement along Z axis", "Load along Z axis");
	public static DOF rotationAroundX = new DOF("Rotation around X axis", "Moment around X axis");
	public static DOF rotationAroundY = new DOF("Rotation around Y axis", "Moment around Y axis");
	public static DOF rotationAroundZ = new DOF("Rotation around Z axis", "Moment around Z axis");
}
