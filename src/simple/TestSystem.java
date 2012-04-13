package simple;


//import solver.Tests;
//import solver.solvers.DirectSolver;
//import solver.solvers.LinearCGSolver;
//import solver.solvers.NonlinearCGOptSolver;
//import solver.solvers.NonlinearCGSolver;
import core.*;

public class TestSystem {
	
//	static Elastic material = new Elastic (10000,0.3,0);
//	static Elastic material = new Elastic (10000,0.499,0);
	static MizesPlastic  material = new MizesPlastic(10000,0.3,0,30);
//	static MizesPlastic material = new MizesPlastic(10000,0.499,0,20);
//	static NonlinearElastic material = new NonlinearElastic(10000,0.3,0);
//	static SofteningTest material = new SofteningTest(10000,0.3,0);
	
	
	
	static Elastic  plate = new Elastic (200000000,0.3,0);

	public static void main(String[] args) {		
		
//		PS();
//		Scanner in = new Scanner(System.in);
//		System.out.println("Size of test model (x*y): ");
//		int size = in.nextInt();
//		Elements elements = generateSystem(size);
//		Elements elements = generateSystem1(1,100);
		
		Elements elements = generateSystemPlateLoading(100, 100); 
		System.out.println("Model created");
		
//		Tests solver = new Tests(elements,1);
//		DirectSolver solver = new DirectSolver(elements,4);
//		LinearCGSolver solver = new LinearCGSolver(elements,4);
//		NonlinearCGSolver solver = new NonlinearCGSolver(elements,4);
//		NonlinearCGOptSolver solver = new NonlinearCGOptSolver(elements,2);

//		solver.run();
		
	}
	
	public static void PS() {	
		for (double p = 0; p < 200; p +=20) {
			System.out.println("***************************************************");
			System.out.println(" P = "+Double.toString(p));
			Elements elements = generateSystem2(100,p);
//			NonlinearCGOptSolver solver = new NonlinearCGOptSolver(elements,2);
//			solver.run();
		}
		
	}
	
	public static Elements generateSystem1(int size, double p) {
		
		Coordinate a,b,c,d;
		a = new Coordinate(0,0,0);
		b = new Coordinate(0,0,0);
		c = new Coordinate(0,0,0);
		d = new Coordinate(0,0,0);
		
		Elements elements = new Elements();
		
		//Elements
		double elementSize = 1.0;//10.0/size;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				a.setXYZ(i*elementSize, j*elementSize, 0);
				b.setXYZ((i+1)*elementSize, j*elementSize, 0);
				c.setXYZ((i+1)*elementSize, (j+1)*elementSize, 0);
				d.setXYZ(i*elementSize, (j+1)*elementSize, 0);
				LinearElement e1 = new LinearElement(a,b,d,material);
				LinearElement e2 = new LinearElement(b,c,d,material);								
								
				elements.add(e1);
				elements.add(e2);										
			}		
		}
			
		// Fixations
		for (int i = 0; i <= size; i++) {
			a.setXYZ(i*elementSize, 0, 0);
			b.setXYZ(0, i*elementSize, 0);
			c.setXYZ(size*elementSize, i*elementSize, 0);
			FixationS f1 = new FixationS(a,DOFs.displacementY,0);
			FixationS f2 = new FixationS(b,DOFs.displacementX,0);
			FixationS f3 = new FixationS(c,DOFs.displacementX,0);
			elements.add(f1);
			elements.add(f2);
			elements.add(f3);						
		}
		// Loads
//		int nl = 10;
//		if (size < nl) nl = 0; 
//		for (int i = 0; i <= nl; i++) {
//			a.setXYZ(i*elementSize,size*elementSize, 0);
//			Load l;
//			if (i == 0 || i == 10)
//				l = new Load(a,DOFs.displacementY,-p*elementSize/2);
//			else
//				l = new Load(a,DOFs.displacementY,-p*elementSize);
//			elements.add(l);	
//		}
		
		a.setXYZ(0,size*elementSize, 0);
		Load l = new Load(a,DOFs.displacementY,-p);
		elements.add(l);
		
		return elements;									
	}
	
	public static Elements generateSystem2(int size, double p) {
		
		Coordinate a,b,c,d;
		a = new Coordinate(0,0,0);
		b = new Coordinate(0,0,0);
		c = new Coordinate(0,0,0);
		d = new Coordinate(0,0,0);
		
		Elements elements = new Elements();
		
		//Elements
		double elementSize = 1.0;//10.0/size;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				a.setXYZ(i*elementSize, j*elementSize, 0);
				b.setXYZ((i+1)*elementSize, j*elementSize, 0);
				c.setXYZ((i+1)*elementSize, (j+1)*elementSize, 0);
				d.setXYZ(i*elementSize, (j+1)*elementSize, 0);
				LinearElement e1 = new LinearElement(a,b,d,material);
				LinearElement e2 = new LinearElement(b,c,d,material);								
								
				elements.add(e1);
				elements.add(e2);										
			}		
		}
			
		
		for (int i = 0; i <= size; i++) {
			a.setXYZ(i*elementSize, 0, 0);
			b.setXYZ(0, i*elementSize, 0);
			c.setXYZ(size*elementSize, i*elementSize, 0);
			FixationS f1 = new FixationS(a,DOFs.displacementY,0);
			FixationS f2 = new FixationS(b,DOFs.displacementX,0);
			FixationS f3 = new FixationS(c,DOFs.displacementX,0);
			elements.add(f1);
			elements.add(f2);
//			elements.add(f3);						
		}
		// Loads
		int nl = size;		
		for (int i = 0; i <= nl; i++) {
			a.setXYZ(i*elementSize,size*elementSize, 0);
			Load l;
			if (i == 0 || i == nl)
				l = new Load(a,DOFs.displacementY,-p*elementSize/2);
			else
				l = new Load(a,DOFs.displacementY,-p*elementSize);
			elements.add(l);	
		}
		
//		a.setXYZ(0,size*elementSize, 0);
//		Load l = new Load(a,-p,2);
//		elements.add(l);
		
		return elements;									
	}

	public static Elements generateSystemPlateLoading(int size, double p) {
		
		Coordinate a,b,c,d;
		a = new Coordinate(0,0,0);
		b = new Coordinate(0,0,0);
		c = new Coordinate(0,0,0);
		d = new Coordinate(0,0,0);
		
		Elements elements = new Elements();
		double elementSize = 1.0;//10.0/size;

		// plate
		int nl = 10;
		for (int i = 0; i < nl; i++) {
			int j = size;
			a.setXYZ(i*elementSize, j*elementSize, 0);
			b.setXYZ((i+1)*elementSize, j*elementSize, 0);
			c.setXYZ((i+1)*elementSize, (j+1)*elementSize, 0);
			d.setXYZ(i*elementSize, (j+1)*elementSize, 0);
			LinearElement e1 = new LinearElement(a,b,d,plate);
			LinearElement e2 = new LinearElement(b,c,d,plate);								

			elements.add(e1);
			elements.add(e2);										
		}
		
		//Elements
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				a.setXYZ(i*elementSize, j*elementSize, 0);
				b.setXYZ((i+1)*elementSize, j*elementSize, 0);
				c.setXYZ((i+1)*elementSize, (j+1)*elementSize, 0);
				d.setXYZ(i*elementSize, (j+1)*elementSize, 0);
				LinearElement e1 = new LinearElement(a,b,d,material);
				LinearElement e2 = new LinearElement(b,c,d,material);								
								
				elements.add(e1);
				elements.add(e2);										
			}		
		}
				

			
		// Fixations
		for (int i = 0; i <= size; i++) {
			a.setXYZ(i*elementSize, 0, 0);
			b.setXYZ(0, i*elementSize, 0);
			c.setXYZ(size*elementSize, i*elementSize, 0);
			FixationS f1 = new FixationS(a,DOFs.displacementY,0);
			FixationS f2 = new FixationS(b,DOFs.displacementX,0);
			FixationS f3 = new FixationS(c,DOFs.displacementX,0);
			elements.add(f1);
			elements.add(f2);
			elements.add(f3);						
		}
		
		int ii = size+1;
		b.setXYZ(0, ii*elementSize, 0);
		FixationS f2 = new FixationS(b,DOFs.displacementX,0);
		elements.add(f2);
		
		
		// Loads				
		for (int i = 0; i <= nl; i++) {
			a.setXYZ(i*elementSize,(size+0)*elementSize, 0);
			Load l;
			if (i == 0 || i == nl)
				l = new Load(a,DOFs.displacementY,-p*elementSize/2);
			else
				l = new Load(a,DOFs.displacementY,-p*elementSize);
			elements.add(l);	
		}
		
		
		
		return elements;									
	}
	
}
