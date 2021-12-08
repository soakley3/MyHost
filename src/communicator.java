import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/*

I'd really like for this class to be the socket handler for the restaurant server.
This *might* allow for each class to issue communication requests to various clients.

Infographic - 

Option: 
- Have each request from each class get filtered via the restaurant class.
- The communicator class has an open socket, and hopefully it will open->connect->close->open->connection->close...etc.
                                             
    group3.1   group3.2   group3.3            
    group2.1   group2.2   group2.3          
    group1.1   group1.2   group1.3          
        |         |         |               
       table1   table2   table3              
             \    |      /                   
              restaurant  (thread 1)    | Note: Orchestrate the table organization. 
                  |                          
             communicator (thread 2)    | Note: Accept connections from the front end.
                  |
                  |,-----port 5000 open for QUICK call/request/submit connections.
   ,--------------O------------,
   |      ,------`|`---,       |
webclient |     ,-'    |  webclient     .----- As of 4 November 2021 the group decided to forgo these extra threads to save time.
     webclient  |  webclient            X      It is understood that if a customer decides that they're open to a table, then they
            webclient                   |      they will automatically be accepted to the table, rather than actually be notified
                                        |      that the table is presently available, given the option, and wait for their acceptance.
                                        |  
                                        |
                                        |`--- individual coms thread (thread 3)    | Note: these coms threads are spawned to reach out to the customer
                                        |`--- individual coms thread (thread 4)    |       and wait to hear back if they want the new table or not.
                                        |`--- individual coms thread (thread 5)    |       This will prevent thread 1 and 2 from blocking. After we 
                                        |`--- individual coms thread (thread 6)    |       hear back, the com thread will be killed. 
                                         `--- <..etc..>             
                                             
4 November 2021:
================

[y] - The communicator class will have an open socket connection. This will accept a connection from the client,
      receive the request, push the response data, and then close the connection. The server socket will then reopen.
      I really need to test this immediately. 

[x] - the below is no longer the idea with the death of the coms class. Instead we'll continue with the above.                                               
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
	
	
	private ServerSocket serverz = null;
	private Socket clientz = null; 
	private InputStream inputz = null;
	private OutputStream outputz = null;
	private BufferedReader readerz = null;
	private PrintWriter writerz = null;
	
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private BufferedReader in       = null;
    private BufferedWriter out     = null;
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
    		out.write(t+"\r\n");
    		//out.writeUTF(t+"\r\n");
    		out.flush();
    		System.out.println("*"+t);
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
    	"getQueueSize:table:1    ->  send "queueSize:1:1"
    	*/
    	t = t.replaceAll(" ", "");
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
    	
	    	case "login": {
	    		
	    		System.out.print("LOGIN ATTEMPTED BY USER " + parsed[1]);
	    		
	    		
	    		
	    		break;
	    	}
    	
	    	case "isFree": {
	    		if (!parent.tableExists(Integer.parseInt(parsed[2]))) {
	    			System.out.println("isFree failure: Table "+ Integer.parseInt(parsed[2]) + " does not exist.");
	    			send("table:"+Integer.parseInt(parsed[2])+":doesntExist");
	    			break;
	    		}
	    		
	    		boolean tIsFree = parent.getTableByID(Integer.parseInt(parsed[2])).isReady();
	    		System.out.println("|is the table "+ Integer.parseInt(parsed[2])+ " free? " + tIsFree);
	    		send("table:"+Integer.parseInt(parsed[2])+":"+Boolean.toString(tIsFree)); // send "table:1:true"
	    		break;
	    	}
	    	
	    	
	    	case "tableAvailability": {
	    		String toRettables = "";
	    		for (table tt: parent.getTables() ) {
	    			toRettables += "table:"+Integer.toString(tt.getID())+":"+Boolean.toString(tt.isReady())+":";
	    		}
	    		toRettables = toRettables.substring(0, toRettables.length() - 1);
	    		System.out.println("|tableAvailability: " + toRettables);
	    		send(toRettables); // send "table:1:true"
	    		break;
	    	}
	    	
	    	
	    	case "setFree": {
	    		if (!parent.tableExists(Integer.parseInt(parsed[2]))) {
	    			System.out.println("setFree failure: Table "+ Integer.parseInt(parsed[2]) + " does not exist.");
	    			send("table:"+Integer.parseInt(parsed[2])+":doesntExist");
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
	    			send("queue:table:"+parsed[2]+":doesntExist"); 
	    			return "" ;
	    		}
	    		System.out.println("|Starting queue process");
	    		//                   name,      phone,        partysize,                 table ID                   true/false openToNewTable?
	    		group nt = new group(parsed[3], parsed[4], Integer.parseInt(parsed[5]), Integer.parseInt(parsed[2]),Boolean.parseBoolean(parsed[6]));
	    		System.out.println("|adding new group: "+nt.toString());
	    		if (!parent.getTableByID(Integer.parseInt(parsed[2])).queueGroup(nt)) {
	    			System.out.println("Could not queue group");
	    			send("queue:table:"+parsed[2]+":Failed");
	    			break;
	    		}
	    		// This will return to the client if the new group was sat immediately or if they were queued to the table.
	    		if (parent.getTableByID(Integer.parseInt(parsed[2])).getCurrentlySat() != null) {
	    			if (parent.getTableByID(Integer.parseInt(parsed[2])).getCurrentlySat().equals(nt))
	    				send("queue:table:"+parsed[2]+":satImmediately");  // queue:table:1:satImmediately
	    			else
	    				send("queue:table:"+parsed[2]+":queued");          // queue:table:1:queued
	    		} else
    				send("queue:table:"+parsed[2]+":queued");          // queue:table:1:queued
	    			
	    		break;
	    	}
	    	
	    	case "removeTable": {
	    		if (!parent.tableExists(Integer.parseInt(parsed[2]))) {
	    			System.out.println("removeTable not exist");
	    			send("table:"+Integer.parseInt(parsed[2])+":doesntExist");
	    			break;
	    		}
	    		System.out.println("|Setting the table "+ Integer.parseInt(parsed[2])+ " remove");
	    		parent.tables.remove(parent.getTableByID(Integer.parseInt(parsed[2])));
	    		send("removeTable:true"); // removeTable:true
	    		break;
	    	}
	    	
	    	case "addTable": {
	    		int maxInd = parent.getHighestTableIndex();
	    		table t1 = new table(parent, maxInd+1);
	    		t1.setSeats(Integer.parseInt(parsed[2]));
	    		t1.setShape(parsed[3]);
	    		System.out.println("|Adding the table "+ t1.toString());
	    		parent.addTable(t1);
	    		send("addTable:"+Integer.toString(maxInd+1)+":true"); // addTable:<newTableID>:true // maybe fix it for real feedback later
	    		break;
	    	}
	    	
	    	case "getAllTables": {
	    		String allTables = "";
	    		for (table ttable: parent.tables ) {
	    			allTables += ttable.toString();
	    		}
	    		System.out.println("|AllTables>"+allTables);
	    		send("getAllTables:"+allTables);
	    		break;
	    	}
	    	
	    	case "getQueueSize": {
	    		if (!parent.tableExists(Integer.parseInt(parsed[2]))) {
	    			System.out.println("getQueueSize failure: Table "+ Integer.parseInt(parsed[2]) + " does not exist.");
	    			send("table:"+Integer.parseInt(parsed[2])+":doesntExist");
	    			break;
	    		}
	    		
	    		int qSize = parent.getTableByID(Integer.parseInt(parsed[2])).getQueued().size();
	    		System.out.println("|queueSize "+ Integer.parseInt(parsed[2])+ ":" + qSize);
	    		send("queueSize:"+Integer.parseInt(parsed[2])+":"+Integer.toString(qSize)); // send "queueSize:1:1"
	    		break;
	    	}
	    	
	    	case "testLogin": {
	    		if (parsed.length != 3) {
	    			send("testLogin:failed:not enough data");
	    			break;
	    		}
	    		if (parent.accStores.isAccount(parsed[1], parsed[2])) {
	    			send("testLogin:passed");
	    		} else {
	    			send("testLogin:failed:wrong account or password");
	    		}
	    		break;
	    	}
	    	
	    	case "changePassword": {
	    		if (parsed.length != 3) {
	    			send("changePassword:failed:not enough data");
	    			break;
	    		}
	    		parent.accStores.changePassword(parsed[1], parsed[2]);
	    		
	    		if (parent.accStores.isAccount(parsed[1], parsed[2])) {
	    			send("changePassword:passed");
	    		} else {
	    			send("changePassword:failed:failed to change password");
	    		}
	    		break;
	    	}
	    	
	    	//changeUser:oldname:newName
	       	case "changeUser": {
	    		if (parsed.length != 3) {
	    			send("changeUser:failed:not enough data");
	    			break;
	    		}
	    		parent.accStores.changeUser(parsed[1], parsed[2]);
	    		
	    		if (parent.accStores.accountExists(parsed[2])) {
	    			send("changeUser:passed");
	    		} else {
	    			send("changeUser:failed:failed to change username");
	    		}
	    		break;
	    	}
	    	
	    	default: {
	    		System.out.println("|No parsing matches");
	    		send("no matching arguments");
	    		break;
	    	}
	    	
    	
    	}
    	
    	return builtLine;    	
    }
    

	
	public void run()  {
        // continue communication until the DIE directive is provided.
    	// For now this involves a horrific double trouble while true loop.......!>?!>!.
		
		
        try {
			server = new ServerSocket(_port);
    		System.out.println("&A");
            System.out.println("Communication socket started"); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        while (true) {
        	try {
            	// For now the communication should be lightning fast, so we can create the socket, above, communicate, close the socket at the end
        		// and then loop back without destroying the socket. This seems to work with my test client. If two client connect at the same time
        		// the second client just waits momentarily to connect until the first client ends the connection. 
                System.out.println("A client connected.");
        		System.out.println("&B");
                socket = server.accept(); // wait for a connection.

                System.out.println("Socket Client Connection");
        		System.out.println("&C");
                // in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        		in = new BufferedReader(
    					new InputStreamReader(socket.getInputStream()));
                // sends output to the socket
        		System.out.println("&D");
                //out = new DataOutputStream(socket.getOutputStream());
    			out = new BufferedWriter(
    					new OutputStreamWriter(socket.getOutputStream()));
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                String line = "";
                
            	// Do the actual communication.
	            while (true) {
	                try {
	                	line = in.readLine();
	                    //line = in.readUTF();	
	                	
	                	// CATCH THE WEB REQUEST!!!!!!
	                	
	                	if (line != null) {
		                    // write an actual parsing function for this, so we can poll the restaurant object for what's going on..
		                    // Just in case theres some sort of error, asplode into the try statement rather than bringing down the whole resty db.
		                    try {
			                    parseIncoming(line);
		                    } catch (Exception ex) {
		                    	send("ERROR: failed to poll restaurant: "+ex.toString());
		                    }
	                	}
	                	
	                	if (line != null) {
		                    if (line.contains("DIE")) {
		                    	die = true;
		                    	break;
		                    }
	                	} 
	                	

	                    if (line == null) break;
	                    System.out.println("'"+line+"'");
	                }
	                catch(IOException i) {
	                	i.printStackTrace();
	                	// catch all garbage socket connection issues..
	                    System.out.println("dead i: " + i);
	                    //die = true;
	                    break;
	                }
	            } 

	            System.out.println("Closing this socket");
                // close connection
	            // Just in case, send a DIE command to the other end as a safety measure.
		        send("DIE");
	            socket.close();
	            in.close();
	            out.close();
	            if (die) break;
  
            }
            catch(IOException i) {
            	// just in case, catch any other exceptions....
                System.out.println(i);
            }
        }
        parent.saveConfig(); // save config here !!!!!!!!!
	}
	
	
	
	public communicator(restaurant p, int port) {
		parent = p;
		_port = port;	
	}
	
	
	
}









































