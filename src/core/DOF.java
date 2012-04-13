package core;

/**
 * Degree of freedom. This class is used for identification of DOFs in FEM model.
 * Every two element which will connect to each other must use identical DOFs as static objects. 
 * @author Constantin Shashkin
 */
public class DOF extends Identificator{
	
	private String nameF;
	
	/**
	 * Creates DOF with specified name 
	 * @param name
	 * @param nameF - name of value for external F vector (force, e.t.c.)
	 */
	public DOF(String name, String nameF) {		
		super(name);
		this.nameF = nameF;
	}
	
	/**
	 * Returns name of external F member (for use in editor)
	 * @return
	 */
	public String getNameF() {
		return nameF;
	}
	
}
