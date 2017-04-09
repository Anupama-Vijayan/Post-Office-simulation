import java.util.concurrent.Semaphore;

public class PostalWorker implements Runnable
{
	  private int TotalCustomers;
	  private int TotalPostalWorkers;
	  private int index,cust_index,worker_index,customer_action,i;
	  private static Semaphore max_capacity ; 
	  private static Semaphore free_counter; 
	  private static Semaphore counter_available; 
	  private static Semaphore worker; 
	  private static Semaphore cust_ready; 
	  private static Semaphore mutex1;
	  private static Semaphore mutex2;
	  private static Semaphore mutex3;
	  private static Semaphore customer_entered;
	  private static Semaphore serve_signal;
	  private static Semaphore order_finish;
	  private static Semaphore order;
	  private static Semaphore finished[] = new Semaphore[50];
	  
	  public PostalWorker(int index,
			      int TotalCustomers,
			      int TotalPostalWorkers,
			      Semaphore []finished,
			      int customer_action,			      
			      Semaphore max_capacity, 
			      Semaphore free_counter,
			      Semaphore counter_available,
			      Semaphore worker,
			      Semaphore cust_ready,
			      Semaphore mutex1,
			      Semaphore mutex2,
			      Semaphore mutex3,
			      Semaphore customer_entered,
			      Semaphore serve_signal,
			      Semaphore order_finish,
			      Semaphore order)
	  {
		 this.TotalCustomers = TotalCustomers;
		 this.TotalPostalWorkers=TotalPostalWorkers;
		 this.index= index;
		 this.finished=finished;
		 this.max_capacity=max_capacity;
		 this.free_counter=free_counter;
		 this.counter_available= counter_available;
		 this.worker=worker;
		 this.cust_ready=cust_ready;
		 this.mutex1=mutex1;
		 this.mutex2=mutex2;
		 this.mutex3=mutex3;
		 this.customer_entered= customer_entered;
		 this.serve_signal=serve_signal;
		 this.order_finish=order_finish;
		 this.order=order;
		 
	  }
	public void run() 
	{
		System.out.println("Postal Worker "+ index +" created");
		while(true)
		{
		//waiting for customer to be ready to be served
		//wait(cust_ready) 
		try{
			cust_ready.acquire(); 
			}catch (InterruptedException e){}
		
		//wait(worker)
		try{
			worker.acquire(); 
		   }catch (InterruptedException e){}				
			
		//only after the customer comes to the counter the worker will work
		//wait(customer_entered) 
		try{			
			customer_entered.acquire();
		   }catch (InterruptedException e){}
		
		//to ensure mutual exclusion
		//wait(mutex2)
		try{
			mutex2.acquire();
		}catch (InterruptedException e){}
	
		this.cust_index= PostOffice.cust_index;
		
		System.out.println("Postal Worker "+index+" serving Customer "+ this.cust_index);
		
		PostOffice.worker_index=this.index;
		
		//to let the customer know that he can ask for the service he wants
		//signal(serve_signal)
		serve_signal.release();
		
		//to signal that the counter is available for the next customer
		//signal(counter_available)
		counter_available.release();
				
		this.customer_action= PostOffice.customer_action;
		
		//waiting for customer to order what service he wants
		//wait(order)
		try{
			order.acquire(); //
		}catch (InterruptedException e){}
		
		//mutual exclusion ends here
		//signal(mutex2)
				mutex2.release();
				
		switch(this.customer_action)
		{
		case 1:
			 try
		      {
		         Thread.sleep(1000);
		      }
		      catch (InterruptedException e)
		      {
		      }
			 
			  break;
		case 2:
			try
		      {
		         Thread.sleep(1500);
		      }
		      catch (InterruptedException e)
		      {
		      }
			 
			  break;
		case 3:
			//to ensure mutual exclusion to the scales
			//wait(mutex3)
			try{
				mutex3.acquire(); 
				}catch (InterruptedException e){}
			
			System.out.println("Scales in use by postal worker "+ index);
			try
		      {
		         Thread.sleep(2000);
		      }
		      catch (InterruptedException e)
		      {
		      }
			System.out.println("Scales released by postal worker "+index);
			
			//mutual exclusion of the scale ends here
			//signal(mutex3)
			mutex3.release();
			break;
	      }//switch case ends here
		
		System.out.println("Postal worker " +index+ " finished serving customer "+this.cust_index);
		
		//signal(finished[cust_index])
		finished[this.cust_index].release();
		
		switch(this.customer_action)
		{
		case 1:			
			 System.out.println("Customer "+this.cust_index+" finished buying stamps");
			  break;
		case 2:			
			 System.out.println("Customer "+this.cust_index+" finished mailing a letter");
			  break;
		case 3:			
			 System.out.println("Customer "+this.cust_index+" finished mailing a package");
			 break;			 
		}//switch ends here		
		
		//to signal that the customer has confirmed that he is done with his work
		//signal(order_finish)
		order_finish.release();		
			
		//waiting for customer to leave and the counter to be free
		//wait(free_counter)
		try{
			free_counter.acquire(); 
			}catch (InterruptedException e){}
		
		//signal(worker)
		worker.release();	
		}
	}
}
