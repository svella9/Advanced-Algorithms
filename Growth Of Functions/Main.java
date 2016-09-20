/*Standard inner product based row-col multiplication algorithm.
Author: S.Dhamodhran*/
import java.util.*;

class Matrix{
	static Random rand = new Random();
	int n;
	float M[][];
	
	Matrix(int n){
		this.n = n;
		M = new float[n][n];
	}
	
	public void readMatrix(){
		Scanner in = new Scanner(System.in);
		for(int i=0; i<n; i++)
		         for(int j=0; j<n; j++)
		            M[i][j] = rand.nextFloat(); //in.nextFloat();
	}

	public void display(){
		for(int i=0; i<n; i++){
		         for(int j=0; j<n; j++)
				System.out.print(M[i][j]+" ");
			System.out.println();
		}
	}
}
class Main{
	
	public static void main(String args[]){
		int n;
		Scanner in = new Scanner(System.in);
		double startTime, endTime;
		n = in.nextInt();
		Matrix a = new Matrix(n);
		Matrix b = new Matrix(n);
		
		a.readMatrix();
		b.readMatrix();
		
		startTime = System.nanoTime();
		Matrix c = NaiveMultiply(a,b);
		endTime = System.nanoTime();

		c.display();
		System.out.println("Execution time: "+(endTime-startTime)/1000000+" milliseconds");
	}
	
	public static Matrix NaiveMultiply(Matrix a, Matrix b){
		Matrix c = new Matrix(a.n);
		for(int i=0; i<a.n; i++)
			for(int j=0; j<a.n; j++){
				c.M[i][j] = 0;
				for(int k=0; k<a.n; k++)
					c.M[i][j] = c.M[i][j] + a.M[i][k] * b.M[k][j]; 		
			}
		return c;	
	}
}
