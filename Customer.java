import java.util.concurrent.Semaphore;
import java.util.Random;

public class Customer  implements Runnable
{
	  private int TotalCustomers;
	  private int TotalPostalWorkers;
	  private int index,worker_index,customer_action,i;
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
	  
	 public Customer(int index,
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
		 this.index= index;
		 this.TotalCustomers = TotalCustomers;
		 this.TotalPostalWorkers=TotalPostalWorkers;
		 this.finished=finished;
		 this.finished=finished;
		 this.max_capacity=max_capacity;
		 this.free_counter=free_counter;
		 this.counter_available= counter_available;
		 this.worker=worker;
		 this.cust_ready=cust_ready;
		 this.mutex1= mutex1;
		 this.mutex2=mutex2;
		 this.mutex3=mutex3;
		 this.customer_entered= customer_entered;
		 this.serve_signal=serve_signal;
		 this.order_finish=order_finish;
		 this.order=order;
		 System.out.println("Customer "+ index +" created");
		 //to generate a random number between 1 and 3 for the customer service
		 Random rn = new Random();
		 this.customer_action = rn.nextInt(3) + 1;
		 
	  }
	public void run() 
	{	
		//for letting only 10 customers at a time inside the office
		//wait(max_capacity) 
		try{
			max_capacity.acquire(); 
			}catch (InterruptedException e){}
		
		System.out.println("Customer "+index+ " enters post office");
		
		//wait(serve_signal)
		try{
			serve_signal.acquire(); 
			}catch (InterruptedException e){}
	
		//it allows only 3 customers to go to counters, as there are only 3 workers available in the post office
		//wait(mutex1)
		try{
			mutex1.acquire();
			}catch (InterruptedException e){}		
		
		//to signal the worker that the customer is ready to be served
		//signal(cust_ready)
		cust_ready.release();
		
		PostOffice.cust_index = this.index;
		PostOffice.customer_action=this.customer_action;
		
		//to signal the worker once customer enters the post office
		//signal(customer_entered) 
		customer_entered.release();
		
		//to wait for the worker to be available
		//wait(counter_available)
		try{
			counter_available.acquire(); 
			}catch (InterruptedException e){}
		
		this.worker_index= PostOffice.worker_index;
		
		switch(customer_action)
		{
		case 1:
			System.out.println("Customer "+ index+ " asks postal worker "+this.worker_index+" to buy stamps");
			break;
		case 2:
			System.out.println("Customer "+ index+ " asks postal worker "+this.worker_index+" to mail a letter");
			 break;
		case 3:
			System.out.println("Customer "+ index+ " asks postal worker "+this.worker_index+" to mail a package");
		    break;
		} //switch statement ends here
		
		//to signal the worker to work on the service ordered by the customer
		//signal(order)
		order.release();
		
		//waiting for customer to receive signal that the worker is done.
		//wait(finished[cust_index])
		try{
            finished[index].acquire();
           }
		    catch (InterruptedException e){}
		
		//waiting for worker to finish the service ordered by the customer
		//wait(order_finish) 
		try{
			order_finish.acquire(); 
			}catch (InterruptedException e){}		
		 
		//to signal that 1 counter has become free
		//signal(mutex1)
		mutex1.release();
		
	    System.out.println("Customer "+ index+ " leaves post office");
	    
	    //to signal to the worker that 1 customer has left the office
		//signal(free_counter)
		free_counter.release();
		
		//to signal that 1 more customer can occupy the post office since 1 left the office
		//signal(max_capacity)
		 max_capacity.release();
		 
		}
}
