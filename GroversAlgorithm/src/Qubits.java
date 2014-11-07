import java.util.Random;


public class Qubits {
    public double[] qBitValues;
    public double[] probabilityPerValue;
    public int numQBits;
    public int numOps;
    
   
    public Qubits(int numQBits)
    {
    	//Initialize qubits to all zeros except for first bit.
    	this.numOps = 0;
    	this.numQBits = numQBits;
    	this.qBitValues = new double[numQBits];
    	for(int i = 0; i<numQBits; i++)
    	{
    		this.qBitValues[i]=0;
    	}
    	
    	//The probability stored at index i is the norm of the component of the 
    	//qubit state which corresponds to that index.
    	this.probabilityPerValue = new double[(int) Math.pow(2,numQBits)];
    	for(int i = 0; i<this.probabilityPerValue.length; i++)
    	{
    		this.probabilityPerValue[i]=0;
    	}
    	this.probabilityPerValue[0]=1;
    	
    }
    public void hadamard(int qBitIndex)
    {
    	//Simulation of a hadamard gate on a single wire.
    	//This is where the magic happens. 
    	
    	//This counts as one quantum operation. Increase the numOps by 1. 
    	this.numOps=this.numOps+1;
    	
    	for(int i = 0; i<this.probabilityPerValue.length; i++)
    	{
    		
    		if ((i & (1 << qBitIndex)) == 0)
    		{
    			//The bit at qBitIndex was not set
    			int i0 = i;
    			//Flip bit at qBitIndex
    			int i1 = i^(1<<qBitIndex);
    			double p0 = this.probabilityPerValue[i0];
    			double p1 = this.probabilityPerValue[i1];
    			
    			//This corresponds to 1/sqrt(2) * the unitary operator
    			//[1 1
    			// 1 -1]
    			this.probabilityPerValue[i0]=(p0+p1)/Math.sqrt(2);
    			this.probabilityPerValue[i1]=(p0-p1)/Math.sqrt(2);
    			
    		}
    	}
    	
    	
    }
    
    public void And(int qBitIndex1, int qBitIndex2, int qBitIndexResult)
    {
    	//Although not a quantum op, this still counts as an operation. Increase the numOps by 1. 
    	this.numOps=this.numOps+1;
    	
    	for(int i = 0; i<this.probabilityPerValue.length; i++)
    	{
    		if(((i & (1 << qBitIndex1)) == 1) &&((i & (1 << qBitIndex2)) == 1))
    		{
    			//both bits are set	
    			int iFlipped = i^(1<<qBitIndexResult);
    			double iFlippedProbabilityTemp = this.probabilityPerValue[iFlipped];
    			this.probabilityPerValue[iFlipped]=this.probabilityPerValue[i];
    			this.probabilityPerValue[i]=iFlippedProbabilityTemp;
    		}
    	}
    }
    
    public void cNot(int qBitIndex1, int qBitIndex2)
    {
    	//Although not a quantum op, this still counts as an operation. Increase the numOps by 1. 
    	this.numOps=this.numOps+1;
    	
    	for(int i = 0; i<this.probabilityPerValue.length; i++)
    	{
    		if((i & (1 << qBitIndex1)) == 1)
    		{
    			int iFlipped = i^(1<<qBitIndex2);
    			double iFlippedProbabilityTemp = this.probabilityPerValue[iFlipped];
    			this.probabilityPerValue[iFlipped]=this.probabilityPerValue[i];
    			this.probabilityPerValue[i]=iFlippedProbabilityTemp;
    		}
    	}
    }
    
    public void exchange(int qBitIndex1, int qBitIndex2)
    {
    	//Although not a quantum op, this still counts as an operation. Increase the numOps by 1. 
    	this.numOps=this.numOps+1;
    	
    	for(int i = 0; i<this.probabilityPerValue.length; i++)
    	{
    		if (!((i & (1 << qBitIndex1)) == 1)&&((i & (1 << qBitIndex2)) == 1))
    		{
    			//TODO: maybe wrong..
    			int iFlipped = (i^(1<<qBitIndex1))^(1<<qBitIndex2);
    		}
    	}
    }
    
    public void applyOracleOp(Oracle oracle)
    {
    	
    	//This quantum op negates the vector if oracle.test(n)==true
    	
    	//This counts as one quantum operation. Increase the numOps by 1. 
    	this.numOps=this.numOps+1;
    	
    	//Really the inner workings of this operator are implemented differently.
    	//I am still looking into the precise gate logic that would
    	//be needed for this op. 
    	for(int i = 0; i<this.probabilityPerValue.length; i++)
    	{
    		if(oracle.test(i)==true)
    		{
    			this.probabilityPerValue[i]=-this.probabilityPerValue[i];
    		}
    	}
    	
    }
    
    
    public void applyDiffusion()
    {
    	
    	int n = this.probabilityPerValue.length;
    	//This counts as one quantum operation. Increase the numOps by 1. 
    	this.numOps=this.numOps+1;
    	
    	//constructing the diffusion operator
    	//Really this should be prebuilt into
    	//circuit logic. I think it maybe should be implemented
    	//with the hadamard gate. I am still looking into
    	//specific details of the hardware implementation
    	
    	//construct |s>
    	double[] s = new double[n];
    	for(int i =0; i<n; i++)
    	{
    		//s is an even superposition over all states
    		s[i]=1.0/Math.sqrt(n);
    	}
    	//construct outer product of |s><s|*2-I
    	//TODO: double check outer product is correct
    	double[][] diffusionOp = new double[n][n];
    	for(int i = 0; i<n; i++)
    	{
    		for(int j = 0; j<n; j++)
    		{
    			diffusionOp[i][j]=2*s[i]*s[j];
    			if(i==j)
    			{
    				//subtract Identity
    				diffusionOp[i][j]=diffusionOp[i][j]-1;
    			}
    		}
    	}
    	
    	//apply the diffusion op to the probability vector
    	double[] newProbabilityPerValue = new double[n];
    	for(int i = 0; i<n; i++)
    	{
    		
    		for(int j = 0; j<n; j++)
    		{
    			newProbabilityPerValue[i]+=diffusionOp[i][j]*this.probabilityPerValue[j];
    		}
    		
    	}
    	this.probabilityPerValue=newProbabilityPerValue;
    	
    }
    public int measure()
    {
    	//simulates the result of taking a measurement of the qubits
    	double high = 0;
    	for (int i = 0; i<this.probabilityPerValue.length; i++)
    	{
    		high+=this.probabilityPerValue[i]*this.probabilityPerValue[i];
    		
    	}
    	
    	Random r = new Random();
    	double randomValue = high * r.nextDouble();
    	
    	for (int i = 0; i<this.probabilityPerValue.length; i++)
    	{
    		if(randomValue<probabilityPerValue[i]*probabilityPerValue[i])
    		{
    			return i;
    		}
    		else
    		{
    			randomValue-=probabilityPerValue[i]*probabilityPerValue[i];
    		}
    	}
    	return -1;
    }
    
    
}
