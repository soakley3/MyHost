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
    public Client(String address, int port, String[] toSend) 
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
    	
		BufferedReader br;
		String[] lns = {};
		
		try {
			br = new BufferedReader(new FileReader(args[0]));
			lns = br.lines().toArray(String[]::new);
		} catch (Exception e) {
			System.out.println("error file " + e.toString());
		}

    	System.out.println(args[0]);
    	for (String t: lns) { 
    		System.out.println(t);
    	}
        Client client = new Client("127.0.0.1", 5000, lns);
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
