
public class GroversAlgorithm {

	
	public static void grover(Oracle oracle, int numberQBits)
	{
		Qubits qubits = new Qubits(numberQBits);
		
		//initialize to even superposition
		for(int i = 0; i<numberQBits; i++)
		{
		    qubits.hadamard(i);
		}
		//Apply sqrt(N) iterations
		for(int i = 0; i<Math.pow(2,numberQBits/2); i++)
		{
			groverIteration(oracle, qubits);
		}
		//measure the qubits 
		int keyIndex = qubits.measure();
		
		System.out.println("Found index: " + keyIndex);
		System.out.println("Number of operations: " + qubits.numOps);
		
		
	}
	public static void groverIteration(Oracle oracle, Qubits qubits)
	{
		qubits.applyOracleOp(oracle);
		qubits.applyDiffusion();
	}
	
	public static void main(String[] args)
	{
		
		Oracle oracle = (x) -> x==7;
		grover(oracle,5);
	}
	
}
