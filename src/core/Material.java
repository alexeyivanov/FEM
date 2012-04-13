package core;

import java.util.ArrayList;




import Jama.*;

public interface Material {
	
	public ArrayList<Parameter> parameters();
	
	public boolean available(Identificator[] idents);
	
	public SolutionData getMaterialData();
	
	public Matrix linearMatrix(Time t);
	
	public Matrix model(Matrix vector, Time t, SolutionData materialData);
	
	public Matrix volumeF(Time t);
	
	public ArrayList<Identificator> resultsId();
	
	public double getResult(Identificator resultId, SolutionData materialData);
	
}
