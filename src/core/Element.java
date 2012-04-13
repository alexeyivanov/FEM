/**
 * 
 */
package core;

import java.util.ArrayList;

import editor.Mesh;

/**
 * Base interface for all finite elements, used in calculations
 * @author Constantin Shashkin 
 */
public interface Element {
	
	/**
	 * Returns period of element live. Within this period element take part in calculations.
	 * After the last time in period internal forces in element are applied to the system.
	 * @return Time.Period
	 */
	public Time.Period period();	
		
	/**
	 * Method returns list of parameters of element and parameters of material
	 * @return array list of parameters of element 
	 */
	public ArrayList<Parameter> parameters();
	
	/**
	 * Element nodes specification
	 * @return array of nodes with specification of node DOFs and fixed values
	 */
	public ElementNode[] getNodes();
	
	/**
	 * Returns element matrix for linear models and tangent matrix for nonlinear models
	 * @return square element matrix in array[][]
	 */
	public double[][] getMatrix(Time t);
	
	/**
	 * Returns external F vector (forces e.t.c.)
	 * @return
	 */
	public double[] getFVector(Time t);
	
	/**
	 * Returns internal F vector
	 * @param x - part of global solution vector for element
	 * @return
	 */
	public double[] getInternalFVector(double[] x, Time t);

	/**
	 * Returns internal vector x(e) for next step of solution
	 * @return
	 */
	public double[] getLastResult();
	
	/**
	 * Returns internal results in element. All important data must be collected in
	 * SolutionData object. 
	 * @return SolutionData
	 */
	public SolutionData getSolutionData();
	
	/**
	 * Sets internal solution data (for example during loading from file)
	 */
	public void setSolutionData(SolutionData solutionData);
	
	/**
	 * Returns view of element in Mesh object.
	 * @return Mesh
	 */
	public Mesh draw(Identificator resultId); 
	
	/**
	 * Returns identificators of element results.
	 * @return Mesh
	 */
	public ArrayList<Identificator> resultsId();
	
}
