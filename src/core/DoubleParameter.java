package core;

public class DoubleParameter extends Parameter {	 
	
	private double min = Double.MIN_VALUE, max = Double.MAX_VALUE;
	
	public DoubleParameter(String name, Double parameter) {
		super(name, parameter);
	}
	
	public DoubleParameter(String name, Double parameter, double min, double max) {
		super(name, parameter);
	}	
	
	/**
	 * Method for checking parameter values
	 * @return empty string if parameter value is allowable, or error description
	 */
	@Override
	public String check(Object value) {
		if ((Double)value < min) 
			return "Value " + ((Double)value).toString()+" is lower than minimum allowed " + Double.toString(min);
		if ((Double)value > max) 
			return "Value " + ((Double)value).toString()+" is higher than maximum allowed " + Double.toString(max);
		return "";
	}
	
	
	
	

}
