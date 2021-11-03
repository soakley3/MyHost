import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
* 
* 
* 1 November 2021:
Alright, so the pretend client and this server now talk, and the DIE directive does kill the server
despite there being two nested while true loops. We'll need figure out a better way to do this. The 
next steps are communicating meaningful data about the restaurants and tables. 

I've just realized that I cannot create a new socket to "inform" the client about the free table... 
There will not be an available socket to connect to.. and the IP's will vary..... 

I need to make the coms class, and then make it thread compatible.

* 

*/


class communicator implements Runnable {
	
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;
    private DataOutputStream out     = null;
	private restaurant parent;
	private int _port;
    boolean die = false;

	
	private Thread t;

	

	public void start() {
		if (this.t == null) {
			this.t = new Thread(this);
			this.t.start();
		}
	}
	
    public void send(String t) {
    	try {
    		out.writeUTF(t+"\r\n");
    		out.flush();
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
    
    public String parseIncoming(String t) {
    	
    	// group parameter note public group(String contactName, String contactNumber, int partSize, int theAssignedTable, boolean tableDifferent) 
    	/* A few examples
    	"isFree:table:1"                        -> [isFree, table, 1]                           -> get the info from restaurant - is table of ID 1 free? 
    	"setFree:table:1"                       -> [setFree, table, 1]                          -> manually/forcefully set the table of ID 1 free
    	"removeTable:table:1                    -> [removeTable, table, 1]                      -> manually/forcefully delete table of ID 1
    	"addTable:table:20                      -> [addTable, table, 20]                        -> manually add a table with 20 seats
    	"queue:table:1:Jonny Depp:phoneNum:PartSize:table:alternateAcceptabe" -> [queue, table, 1, Jonny Depp, phoneNum, etc] -> Add jonny depp to the queue for table 1
    	
    	*/
    	t = t.replaceAll("\r", "");
    	t = t.replaceAll("\n", "");

    	String[] parsed = t.split(":");   

    	String builtLine = "";
    	if (parsed.length == 0) return builtLine;
    	
    	System.out.println("- "+t);
    	for(String x: parsed) {
    		System.out.println("-- '"+x+"'");
    	}
    	
    	switch (parsed[0]) {
    		//figure it out... 
    	
	    	case "isFree": {
	    		if (!parent.tableExists(Integer.parseInt(parsed[2]))) {
	    			System.out.println("isFree not exist");
	    			break;
	    		}
	    		
	    		boolean tIsFree = parent.getTableByID(Integer.parseInt(parsed[2])).isReady();
	    		System.out.println("|is the table "+ Integer.parseInt(parsed[2])+ " free? " + tIsFree);
	    		send("table:"+Integer.parseInt(parsed[2])+":"+Boolean.toString(tIsFree)); // send "table:1:true"
	    		break;
	    	}
	    	
	    	case "setFree": {
	    		if (!parent.tableExists(Integer.parseInt(parsed[2]))) {
	    			System.out.println("setFree not exist");
	    			break;
	    		}
	    		System.out.println("|Setting the table "+ Integer.parseInt(parsed[2])+ " free");
	    		parent.getTableByID(Integer.parseInt(parsed[2])).setReady();
	    		send("table:"+Integer.parseInt(parsed[2])+":"+Boolean.toString(parent.getTableByID(Integer.parseInt(parsed[2])).isReady())); // send "table:1:true"
	    		break;
	    	}
	
	    	case "queue": {
	    		if (!parent.tableExists(Integer.parseInt(parsed[2]))) {
	    			System.out.println("queue not exist");
	    			break;
	    		}
	    		group nt = new group(parsed[3], parsed[4], Integer.parseInt(parsed[5]), Integer.parseInt(parsed[2]),Boolean.parseBoolean(parsed[7]));
	    		System.out.println("|adding new group: "+nt.toString());
	    		parent.getTableByID(Integer.parseInt(parsed[2])).queueGroup(nt);
	    		break;
	    		// determine if the new group was added to the queue or immediately sat
	    	}
	    	
	    	case "removeTable": {
	    		if (!parent.tableExists(Integer.parseInt(parsed[2]))) {
	    			System.out.println("removeTable not exist");
	    			break;
	    		}
	    		System.out.println("|Setting the table "+ Integer.parseInt(parsed[2])+ " remove");
	    		parent.tables.remove(parent.getTableByID(Integer.parseInt(parsed[2])));
	    	}
	    	
	    	case "addTable": {
	    		int maxInd = parent.getHighestTableIndex();
	    		table t1 = new table(parent, maxInd+1);
	    		t1.setSeats(Integer.parseInt(parsed[2]));
	    		System.out.println("|Adding the table "+ t1.toString());
	    		parent.addTable(t1);
	    		break;
	    	}
	    	
	    	case "getAllTables": {
	    		String allTables = "";
	    		for (table ttable: parent.tables ) {
	    			allTables += ttable.toString() + "\n";// NOTE THE \n ADDS A NATURAL DELIMETER!!!!!!
	    		}
	    		break;
	    	}
	    	
	    	default: {
	    		System.out.println("|No parsing matches");
	    		break;
	    	}
	    	
    	
    	}
    	
    	return builtLine;
    	
    	
    }
    
	
	public void run()  {

        try {
            // continue communication until the DIE directive is provided.
        	// For now this involves a horrific double trouble while true loop.......!>?!>!.
            while (true) {
            	// For now the communication should be lightning fast, so we can create the socket, communicate, close the socket, loop back and recreate it.
            	// This is seriously not the right approach for a production system....................
                server = new ServerSocket(_port);
                System.out.println("Communication socket started");  
                socket = server.accept(); // wait for a connection.
                System.out.println("Socket Client Connection");
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                // sends output to the socket
                out    = new DataOutputStream(socket.getOutputStream());
                String line = "";
            	// Do the actual communication.
	            while (true) {
	                try {
	                    line = in.readUTF();	                    
	                    if (line.contains("DIE")) {
	                    	die = true;
	                    	break;
	                    }
	                    
	                    // write an actual parsing function for this, so we can poll the restaurant object for what's going on..
	                    parseIncoming(line);
	                    
	                    System.out.println(line);
	                }
	                catch(IOException i) {
	                	// catch all garbage socket connection issues..
	                    System.out.println(i);
	                    break;
	                }
	            }

	            System.out.println("Closing this socket");
                // close connection
	            socket.close();
	            in.close();
	            if (die) break;
            }
  

        }
        catch(IOException i) {
        	// just in case, catch any other exceptions....
            System.out.println(i);
        }
		
	}
	
	public communicator(restaurant p, int port) {
		parent = p;
		_port = port;	
	}
	

	
	
	
}









































