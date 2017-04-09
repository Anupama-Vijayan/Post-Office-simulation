import java.util.concurrent.Semaphore;

public class PostOffice
{
	
  public int TotalCustomers;
  public int TotalPostalWorkers;
  public static int cust_index;
  public static int worker_index;
  public static int customer_action;
  //semaphores
  public static Semaphore max_capacity = new Semaphore( 10, true );
  public static Semaphore free_counter = new Semaphore( 0, true ); 
  public static Semaphore counter_available = new Semaphore( 0, true ); 
  public static Semaphore worker= new Semaphore( 3, true ); 
  public static Semaphore cust_ready= new Semaphore( 0, true ); 
  public static Semaphore mutex1= new Semaphore( 3, true ); 
  public static Semaphore mutex2= new Semaphore( 1, true );
  public static Semaphore mutex3= new Semaphore( 1, true );
  public static Semaphore customer_entered= new Semaphore( 0, true ); 
  public static Semaphore serve_signal= new Semaphore( 1, true );
  public static Semaphore order_finish= new Semaphore( 0, true );
  public static Semaphore order= new Semaphore( 0, true );
  public static Semaphore finished[] = new Semaphore[50];
  
  
	public static void main(String args[])
    { 

    	int TotalCustomers = 50;
    	int TotalPostalWorkers = 3;
    	//Initiate array of semaphores
    	  for(int n =0; n <TotalCustomers ; n++)
    	  {
    	          finished[n]= new Semaphore(0, true); //Initialize each with 0
    	  }
    	
    	// object and thread for 50 customers
    	Customer CustomerObj[] = new Customer[TotalCustomers];
        Thread Customerthread[] = new Thread[TotalCustomers];
        
        // object and thread for the 3 postal workers
        PostalWorker WorkerObj[] = new PostalWorker[TotalPostalWorkers];
        Thread Workerthread[] = new Thread[TotalPostalWorkers];
        
        //thread initialised for customers
        for(int i = 0; i < TotalCustomers ; ++i ) 
        {
           CustomerObj[i] = new Customer(i,TotalCustomers,TotalPostalWorkers,finished,customer_action,max_capacity,free_counter,counter_available,worker,cust_ready,mutex1,mutex2,mutex3,customer_entered,serve_signal,order_finish,order);
           Customerthread[i] = new Thread( CustomerObj[i]);
           Customerthread[i].start();
        }
        
        //thread initialised for workers        
        for(int i = 0; i < TotalPostalWorkers; ++i ) 
        {
        	WorkerObj[i] = new PostalWorker(i,TotalCustomers,TotalPostalWorkers,finished,customer_action,max_capacity,free_counter,counter_available,worker,cust_ready,mutex1,mutex2,mutex3,customer_entered,serve_signal,order_finish,order);
        	Workerthread[i] = new Thread(WorkerObj[i]);
        	Workerthread[i].setDaemon(true);
        	Workerthread[i].start(); 
        	
        }
        for(int i = 0; i < TotalCustomers; ++i ) 
        {
	  	 try
	  	 {
	  		Customerthread[i].join();
	  	   System.out.println("Joined customer "+ i);
	  	 }
	  	 catch (InterruptedException e)
	  	 {
	  	 }
        }
     }	
}
