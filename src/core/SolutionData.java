package core;

import java.io.Serializable;

/**
 * Container for all internal solution data in elements & in integration points
 * @author Constantin Shashkin
 *
 */
public abstract class SolutionData implements Serializable, Cloneable {	
	private static final long serialVersionUID = 1L;
	
	/**
	 * This method can be used for copying internal data if we need information from previous steps.   
	 */
	public abstract void nextTimeStep();
	
}
