// A Java program for a Client
// I did not write this and found it on the internet for testing reasons.
// It will be modified to poll the restaurant server. 
import java.net.*;
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
            input  = new DataInputStream(System.in);
  
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
        
        
        String a = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        //a += "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        
        send(a);
        
        //out.flush();
        
        int z = 0;
  
        // string to read message from input
        String line = "";
  
        // keep reading until "Over" is input
        while (true)
        {
            try
            {
                //line = input.readLine();
                if (line.equals("Over")) break;
                for (int j = 0; 10 > j; j++) {
                	send(a + Integer.toString(j)+"."+Integer.toString(z));
                	z++;
                	if (z > 10) { 
                		send("DIE");
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
