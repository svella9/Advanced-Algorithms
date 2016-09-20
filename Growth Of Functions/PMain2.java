/*Parallelize Strassen's recursive dividenconquer strategybased algorithm.
Author: S.Dhamodhran*/
import java.util.*;
import java.io.*;

class Matrix2{
   static Random rand = new Random();
   int n;
   float M[][];
   float m11[][], m12[][], m21[][], m22[][];
   
   Matrix2(int n){
      this.n = n;
      M = new float[n][n];
   }
   
   Matrix2(float [][]a){
      this.n = a.length;
      M = new float[this.n][this.n];
      for(int i=0; i<a.length; i++)
         for(int j=0; j<a.length; j++)
               M[i][j] = a[i][j];
   }
   
   void readMatrix2(){
      Scanner in = new Scanner(System.in);
      for(int i=0; i<n; i++)
         for(int j=0; j<n; j++)
            M[i][j] = rand.nextFloat(); //in.nextFloat();//
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

public class PMain2{
	public static void main(String args[]){
			int n;
			double startTime, endTime;
			Scanner in = new Scanner(System.in);
			n = in.nextInt();
			Matrix2 a = new Matrix2(n);
			Matrix2 b = new Matrix2(n);
     
			a.readMatrix2();
			b.readMatrix2(); 
     
			startTime = System.nanoTime();
			Matrix2 result = ThreadCreate.threadCreate(a,b);
			endTime = System.nanoTime();
     
			result.display();
			System.out.println("Execution time: "+(endTime-startTime)/1000000+" milliseconds");
	}
	
	public static Matrix2 StrassenMultiply(Matrix2 a, Matrix2 b){
	int n = a.n;
	Matrix2 c = new Matrix2(a.n);
	
	if(n == 1){
		c.M[0][0] = a.M[0][0] * b.M[0][0];
		return c;
	}
	
	a.Partition();
	b.Partition();

	Matrix2 p1 = StrassenMultiply(new Matrix2(add(a.m11,a.m22)) , new Matrix2(add(b.m11,b.m22)));
	Matrix2 p2 = StrassenMultiply(new Matrix2(add(a.m21,a.m22)) , new Matrix2(b.m11));
	Matrix2	p3 = StrassenMultiply(new Matrix2(a.m11) , new Matrix2(sub(b.m12,b.m22)));
	Matrix2 p4 = StrassenMultiply(new Matrix2(a.m22) , new Matrix2(sub(b.m21,b.m11)));
	Matrix2 p5 = StrassenMultiply(new Matrix2(add(a.m11,a.m12)) , new Matrix2(b.m22));
	Matrix2 p6 = StrassenMultiply(new Matrix2(sub(a.m21,a.m11)), new Matrix2(add(b.m11,b.m12)));
	Matrix2 p7 = StrassenMultiply(new Matrix2(sub(a.m12,a.m22)), new Matrix2(add(b.m21,b.m22)));

	c.m11 = add(sub(add(p1.M,p4.M),p5.M),p7.M);
	c.m12 = add(p3.M,p5.M);
	c.m21 = add(p2.M,p4.M);
	c.m22 = add(sub(add(p1.M,p3.M),p2.M),p6.M);

	c = getMatrix2(c.m11, c.m12, c.m21, c.m22);
      return c;
  }

public static float[][] add(float [][]a, float[][]b){
      float c[][] = new float[a.length][a.length];
      for(int i=0;i<a.length;i++)
                for(int j=0;j<a.length;j++)
                     c[i][j]=a[i][j]+b[i][j];
           return c;
  }

public static float[][] sub(float [][]a, float[][]b){
      float c[][] = new float[a.length][a.length];
      for(int i=0;i<a.length;i++)
                for(int j=0;j<a.length;j++)
                     c[i][j]=a[i][j]-b[i][j];
           return c;
  }

public static Matrix2 getMatrix2(float [][]a, float [][]b, float [][]c, float [][]d){
      Matrix2 m = new Matrix2(a.length*2);
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
		public static Matrix2 threadCreate(Matrix2 a, Matrix2 b){
			int n = a.n;
			Matrix2 c = new Matrix2(n);
			if(n == 1){
				c.M[0][0] = a.M[0][0] * b.M[0][0];
			}
			
			else{
					a.Partition();
					b.Partition();
					
					MyThread []t = new MyThread[7];
					t[0] = new MyThread(new Matrix2(PMain2.add(a.m11,a.m22)) , new Matrix2(PMain2.add(b.m11,b.m22)));
					t[1] = new MyThread(new Matrix2(PMain2.add(a.m21,a.m22)) , new Matrix2(b.m11));
					t[2] = new MyThread(new Matrix2(a.m11) , new Matrix2(PMain2.sub(b.m12,b.m22)));
					t[3] = new MyThread(new Matrix2(a.m22) , new Matrix2(PMain2.sub(b.m21,b.m11)));
					t[4] = new MyThread(new Matrix2(PMain2.add(a.m11,a.m12)) , new Matrix2(b.m22));
					t[5] = new MyThread(new Matrix2(PMain2.sub(a.m21,a.m11)), new Matrix2(PMain2.add(b.m11,b.m12)));
					t[6] = new MyThread(new Matrix2(PMain2.sub(a.m12,a.m22)), new Matrix2(PMain2.add(b.m21,b.m22)));
					
					t[0].start();
					t[1].start();
					t[2].start();
					t[3].start();
					t[4].start();
					t[5].start();
					t[6].start();
					
					try
					{
						t[0].join();
						t[1].join();
						t[2].join();
						t[3].join();
						t[4].join();
						t[5].join();
						t[6].join();
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					c.m11 = PMain2.add(PMain2.sub(PMain2.add(t[0].P.M,t[3].P.M),t[4].P.M),t[6].P.M);
					c.m12 = PMain2.add(t[2].P.M,t[4].P.M);
					c.m21 = PMain2.add(t[1].P.M,t[3].P.M);
					c.m22 = PMain2.add(PMain2.sub(PMain2.add(t[0].P.M,t[2].P.M),t[1].P.M),t[5].P.M);
					
					c = PMain2.getMatrix2(c.m11, c.m12, c.m21, c.m22);
		}
		return c;
	}
	
	public static Matrix2 strassen(Matrix2 a, Matrix2 b){
		return PMain2.StrassenMultiply(a,b);
	}
}

class MyThread extends Thread{
	Matrix2 A;
	Matrix2 B;
	Matrix2 P;
	
	MyThread(Matrix2 A, Matrix2 B){
		this.A = A;
		this.B = B;
	}
	
	public void run(){
		P = ThreadCreate.strassen(A,B);
	}
}


