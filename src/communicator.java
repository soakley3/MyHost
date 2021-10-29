/*

I'd really like for this class to be the socket handler for the restaurant server.
This *might* allow for each class to issue communication requests to various clients.

Infographic - 

Option: 
- Allow every class/object to communicate directly with the communicator class?

                        ,-------------------.
.-- group3.1   group3.2-|  group3.3 --.     |
|-- group2.1   group2.2-|  group2.3 --|     |
|-- group1.1   group1.2-|  group1.3 --|     |
'.      |         |     ,    |        |     |
  \--- table1   table2-'  table3 -----|     |
   \         \    |      /            |     |
    \         restaurant <--(thread1) |     |
     \            |                   |     |
      '----- communicator ------------'     |
                  |  ^----(thread2)         |
                  '-------------------------'

OR
- Or have each request from each class get filtered via the restaurant class? 

                                             
    group3.1   group3.2   group3.3            
    group2.1   group2.2   group2.3          
    group1.1   group1.2   group1.3          
        |         |         |               
       table1   table2   table3              
             \    |      /                   
              restaurant  (thread 1)                   
                  |                          
             communicator (thread 2)                   
                                             
                                               
The communicator class will need to spawn a NEW thread per communication channel with the front end.
This will prevent blocking the main thread (causing a stall of the application) when waiting to hear 
back from the front end: whether or not a customer accepts the new table offering.  

I think the second option is the best.

* Second, it's worth noting that with multiple tables being access concurrently, and multiple customers
* in communication, the communication server may have to spawn several threads so that the communication 
* server itself does not become blocked.



*/