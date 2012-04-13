package core;

/**
 * Parameter. This class is used for identification of parameters of elements and materials.
 * @author Constantin Shashkin
 */
public class Parameter {
	
	private String name;
	private Object parameter;
	
	/**
	 * Creates parameter with specified name 
	 * @param name
	 */
	public Parameter(String name, Object parameter) {		
		this.name = name;
		this.parameter = parameter;
	}
	
	/**
	 * Returns parameter name (for use in editor)
	 * @return
	 */
	public String getName() {
		return name;
	}
		
	/**
	 * Returns parameter value (for use in editor)
	 * @return
	 */
	public Object getValue() {
		return parameter;
	}	
	
	/**
	 * Set parameter value
	 * @return empty string if parameter value is allowable, or error description
	 */
	public String setValue(Object value) {
		String res = check(value);
		if (res == "")	parameter = value;
		return res;
	}
	
	
	/**
	 * Method for checking parameter values
	 * @return empty string if parameter value is allowable, or error description
	 */
	public String check(Object value) {
		return "";
	}
	
}
