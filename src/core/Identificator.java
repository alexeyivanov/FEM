package core;

/**
 * This class is used for identification of types. 
 * @author Constantin Shashkin
 */
public class Identificator {
	
	private String name;
	
	/**
	 * Creates identificator with specified name 
	 * @param name
	 */
	public Identificator(String name) {
		this.name = name;
	}
	
	/**
	 * Returns name (for use in editor)
	 * @return
	 */
	public String getName() {
		return name;
	}
	
}

