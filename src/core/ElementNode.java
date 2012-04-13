package core;

/**
 * Class for identification of element nodes (degrees of freedom in node, fixed values)
 * @author Constantin Shashkin
 */
public class ElementNode {
	
	private Coordinate c;	
	private DOF[] DOFs;	
	private double[] fixedValues;	
	
	/**
	 * Creates node with set of DOFs and without fixed DOFs 
	 * @param c - coordinate of the node
	 * @param DOFs - array of DOFs in node 
	 */
	public ElementNode(Coordinate c, DOF[] DOFs) {
		this.c = new Coordinate(c);
		this.DOFs = DOFs;		
		this.fixedValues = null;
	}
	
	/**
	 * Creates node with one DOF (not fixed) 
	 * @param c
	 * @param dof
	 */
	public ElementNode(Coordinate c, DOF dof) {
		this.c = new Coordinate(c);
		this.DOFs = new DOF[] {dof};		
		this.fixedValues = null;
	}
	
	/**
	 * Creates node with one DOF with fixed value
	 * @param c - coordinate of the node 
	 * @param dof
	 * @param fixedValue
	 */
	public ElementNode(Coordinate c, DOF dof, double fixedValue) {
		this.c = new Coordinate(c);
		this.DOFs = new DOF[] {dof};		
		this.fixedValues = new double[] {fixedValue};
	}
		
	/**
	 * Creates node with set of DOFs with fixed values
	 * @param c - coordinate of the node
	 * @param DOFs - array of DOFs
	 * @param fixedValues - array of fixed values
	 */
	public ElementNode(Coordinate c, DOF[] DOFs, double[] fixedValues) {
		this.c = new Coordinate(c);
		this.DOFs = DOFs;	
		this.fixedValues = fixedValues;
	}
	
	/**
	 * Returns node coordinate
	 * @return
	 */
	public Coordinate getCoordinate() {
		return c;
	}
	
	/**
	 * Returns array of node DOFs
	 * @return
	 */
	public DOF[] getDOFs() {
		return DOFs;
	}
	
	/**
	 * Returns array of fixed values (null if there are not fixed values)
	 * @return
	 */
	public double[] getFixedValues() {
		return fixedValues;
	}
	

}
