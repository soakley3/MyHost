/*

I'd really like for this class to be the socket handler for the restaurant server.
This *might* allow for each class to issue communication requests to various clients.

Infographic - 

Option: 
- Have each request from each class get filtered via the restaurant class.

                                             
    group3.1   group3.2   group3.3            
    group2.1   group2.2   group2.3          
    group1.1   group1.2   group1.3          
        |         |         |               
       table1   table2   table3              
             \    |      /                   
              restaurant  (thread 1)                         | Note: Orchestrate the table organization. 
                  |                          
             communicator (thread 2)                         | Note: Accept connections from the front end.
                  |
                  |`--- individual coms thread (thread 3)    | Note: these coms threads are spawned to reach out to the customer
                  |`--- individual coms thread (thread 4)    |       and wait to hear back if they want the new table or not.
                  |`--- individual coms thread (thread 5)    |       This will prevent thread 1 and 2 from blocking. After we 
                  |`--- individual coms thread (thread 6)    |       hear back, the com thread will be killed. 
                   `--- <..etc..>             
                                             
                                               
The communicator class will need to spawn a NEW thread per communication channel with the front end.
This will prevent blocking the main thread (preventing a stall of the application) when waiting to hear 
back from the front end: whether or not a customer accepts the new table offering.  

* Second, it's worth noting that with multiple tables being access concurrently, and multiple customers
* in communication, the communication server may have to spawn several threads so that the communication 
* server itself does not become blocked.

*/


class communicator {
	
	
	
	public communicator(int port) {
		
		// we're going to need to make sockets available on this port.
		
	}
	

	
	
	
}









































