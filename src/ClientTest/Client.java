// A Java program for a Client
// I did not write this and found it on the internet for testing reasons.
// It will be modified to poll the restaurant server. 
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
  
public class Client {
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
    
    boolean die = false;
  
    // constructor to put ip address and port
    public Client(String address, int port)
    {
        // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");
  
            // takes input from terminal
            input  = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            // sends output to the socket
            out    = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
        
        
        //String a = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        //a += "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        
        //send(a);
        
        String[] toSend = {
            	"isFree:table:1",//                        -> [isFree, table, 1]                           -> get the info from restaurant - is table of ID 1 free? 
            	"setFree:table:1",//                       -> [setFree, table, 1]                          -> manually/forcefully set the table of ID 1 free
            	"addTable:table:20:hexagon",//             -> [addTable, table, 20]                        -> manually add a table with 20 seats
            	//queue,table,tablenum,name,phone,partysize,opentonewtable
            	"queue:table:1:Jonny Depp:919919919:3:true",// -> [queue, table, 1, Jonny Depp, phoneNum, etc] -> Add jonny depp to the queue for table 1
            	"removeTable:table:1",//                   -> [removeTable, table, 1]                      -> manually/forcefully delete table of ID 1
            	"addTable:table:20:hexagon",//             -> [addTable, table, 20, hexagon]
            	"getAllTables",
            	"queue:table:1:Jonny Depp:919919919:2:true",
            	"queue:table:5:Jonny Depp:919919919:2:true"
        };
        
        
        
        
        //out.flush();
        
        
  
        // string to read message from input
        String line = "";
  
        // keep reading until "Over" is input
        while (true)
        {
            try
            {
                //line = input.readLine();
                if (line.equals("Over")) break;
                for (int z = 0; toSend.length > z; z++ ) {
                	send(toSend[z]);     
                	line = input.readUTF();
                	System.out.println("R: "+line);
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					if (z == toSend.length-1) {
						die = true;
						break;
					}
                }
            	
                if (die) break;
            }
            catch(Exception i)
            {
                System.out.println(i);
            }
        }
  
        // close the connection
        try
        {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
  
    public static void main(String args[])
    {
        Client client = new Client("127.0.0.1", 5000);
        // things to send

        
        //client.send();
    }
    
    public void send(String t) {
    	try {
    		out.writeUTF(t+"\r\n");
    		out.flush();
    	} catch (Exception e) {
    		System.out.println(e);
    	}

    }
}
