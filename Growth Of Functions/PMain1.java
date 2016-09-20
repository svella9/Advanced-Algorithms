/*Parallelize O(n^3) DividenConquer strategybased multiplication algorithm.
Author:S.Dhamodhran*/
import java.util.*;
import java.io.*;

class Matrix1{
   static Random rand = new Random();
   int n;
   float M[][];
   float m11[][], m12[][], m21[][], m22[][];
   
   Matrix1(int n){
      this.n = n;
      M = new float[n][n];
   }
   
   Matrix1(float [][]a){
      this.n = a.length;
      M = new float[this.n][this.n];
      for(int i=0; i<a.length; i++)
         for(int j=0; j<a.length; j++)
               M[i][j] = a[i][j];
   }
   
   void readMatrix1(){
      Scanner in = new Scanner(System.in);
      for(int i=0; i<n; i++)
         for(int j=0; j<n; j++)
            M[i][j] = rand.nextFloat();//in.nextFloat();// 
    }
    
    void Partition(){
      m11 = new float[this.n/2][this.n/2];
      m12 = new float[this.n/2][this.n/2];
      m21 = new float[this.n/2][this.n/2];
      m22 = new float[this.n/2][this.n/2];
      
      for(int i=0; i<m11.length; i++)
         for(int j=0; j<m11.length; j++)
            m11[i][j] = M[i][j];
      
      for(int i=0; i<m11.length; i++)
         for(int j=m11.length; j<2*m11.length; j++)
            m12[i][j-m11.length] = M[i][j];
      
      for(int i=m11.length; i<2*m11.length; i++)
         for(int j=0; j<m11.length; j++)
            m21[i-m11.length][j] = M[i][j];
        
      for(int i=m11.length; i< 2*m11.length; i++)
         for(int j=m11.length; j< 2*m11.length; j++)
            m22[i-m11.length][j-m11.length] = M[i][j];
    }
    
    void display(){
      for(int i=0; i<n; i++){
         for(int j=0; j<n; j++)
              System.out.print(M[i][j]+" ");
         System.out.println();
       }
    }
  
}

public class PMain1{
		public static void main(String args[]){
			int n;
			double startTime, endTime;
			Scanner in = new Scanner(System.in);
			n = in.nextInt();
			Matrix1 a = new Matrix1(n);
			Matrix1 b = new Matrix1(n);
     
			a.readMatrix1();
			b.readMatrix1(); 
     
			startTime = System.nanoTime();
			Matrix1 result = ThreadCreate.threadCreate(a,b);
			endTime = System.nanoTime();
     
			result.display();
			System.out.println("Execution time: "+(endTime-startTime)/1000000+" milliseconds");
		}
		
		public static Matrix1 DividenConquer(Matrix1 a, Matrix1 b){
			int n = a.n;
			Matrix1 c = new Matrix1(n);
      
			if(n == 1){
				c.M[0][0] = a.M[0][0] * b.M[0][0];
				return c; 
			}
      
			a.Partition();
			b.Partition();
      
			c.m11 = add(DividenConquer(new Matrix1(a.m11), new Matrix1(b.m11)), DividenConquer(new Matrix1(a.m12), new Matrix1(b.m21)));
			c.m12 = add(DividenConquer(new Matrix1(a.m11), new Matrix1(b.m12)), DividenConquer(new Matrix1(a.m12), new Matrix1(b.m22)));
			c.m21 = add(DividenConquer(new Matrix1(a.m21), new Matrix1(b.m11)), DividenConquer(new Matrix1(a.m22), new Matrix1(b.m21)));
			c.m22 = add(DividenConquer(new Matrix1(a.m21), new Matrix1(b.m12)), DividenConquer(new Matrix1(a.m22), new Matrix1(b.m22)));
      
			c = getMatrix1(c.m11, c.m12, c.m21, c.m22);
			return c;
		}
  
		public static float[][] add(Matrix1 a, Matrix1 b){
			float c[][] = new float[a.n][a.n];
			for(int i=0;i<a.n;i++)
                for(int j=0;j<a.n;j++)
                     c[i][j]=a.M[i][j]+b.M[i][j];
			return c;
		}
  
		public static Matrix1 getMatrix1(float [][]a, float [][]b, float [][]c, float [][]d){
			Matrix1 m = new Matrix1(a.length*2);
			for(int i=0;i<a.length;i++)
				for(int j=0;j<a.length;j++)
						m.M[i][j]=a[i][j];
           
			for(int i=0;i<a.length;i++)
				for(int j=a.length;j<2*a.length;j++)
						m.M[i][j]=b[i][j-a.length];
           
			for(int i=a.length;i<2*a.length;i++)
				for(int j=0;j<a.length;j++)
							m.M[i][j]=c[i-a.length][j];
           
			for(int i=a.length;i<2*a.length;i++)
				for(int j=a.length;j<2*a.length;j++)
						m.M[i][j]=d[i-a.length][j-a.length];
     
			return m;
		}
}

class ThreadCreate{
		public static Matrix1 threadCreate(Matrix1 a, Matrix1 b){
			int n = a.n;
			Matrix1 c = new Matrix1(n);
			if(n == 1){
				c.M[0][0] = a.M[0][0] * b.M[0][0];
			}
			
			else{
					a.Partition();
					b.Partition();
					
					MyThread []t = new MyThread[4];
					t[0] = new MyThread(a.m11,a.m12,b.m11,b.m21);
					t[1] = new MyThread(a.m11,a.m12,b.m12,b.m22);
					t[2] = new MyThread(a.m21,a.m22,b.m11,b.m21);
					t[3] = new MyThread(a.m21,a.m22,b.m12,b.m22);
					
					t[0].start();
					t[1].start();
					t[2].start();
					t[3].start();
					try
					{
						t[0].join();
						t[1].join();
						t[2].join();
						t[3].join();
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					c = PMain1.getMatrix1(t[0].C, t[1].C, t[2].C,t[3].C);
		}
		return c;
	}
	
	public static float[][] Add(float[][]a, float[][] b, float[][] c, float[][] d){
			return PMain1.add(PMain1.DividenConquer(new Matrix1(a), new Matrix1(c)), PMain1.DividenConquer(new Matrix1(b), new Matrix1(d)));
	}
}

class MyThread extends Thread{
	float[][] C;
	
	float[][] a;
	float[][] b;
	float[][] c;
	float[][] d;
	
	MyThread(float[][] a, float[][] b,float[][]c, float[][] d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;

	}
	
	public void run(){
		C = ThreadCreate.Add(a,b,c,d);
	}
}