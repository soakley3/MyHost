import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AccountStorage {
	
	public ArrayList<account> accountList = new ArrayList<>();

	public void loadAccounts() {
        try {
            try (FileReader reader = new FileReader("userinfo.txt")) {
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                	
                	String[] splitRead = line.split(":");
                	String userRead = splitRead[0].toLowerCase(); //convert to lower case because we don't want 
                	String passRead = splitRead[1];               // user names to be case sensitive, only passwords
                	
                	accountList.add(new account(userRead, passRead));
                }
            }

        } catch (IOException e) {
        	System.out.println("UNABLE TO READ ACCOUNTS! "+e); // need to print the call stack if failure occurs
        }
	}
	
	
	public void saveAccounts() {
		try {
			FileWriter myWriter = new FileWriter("userinfo.txt");
			
			String toSave = "";
	    	for (account t: accountList) {
	    		toSave += t.getUsername()+":"+t.getPassword()+"\n";
	    	}
				
			myWriter.write(toSave);
	        myWriter.close();
	        System.out.println("Successfully saved the current restaurant configuration.");
	    } catch (IOException e) {
	        System.out.println("An error occurred whilst saving the restaurant configuration.");
	        e.printStackTrace();
	    }
	}
	
	
    // checks account arraylist for the username and a matching pass
    public boolean isAccount(String givenUserName, String givenPassword) {
        for (account tt: accountList) {
        	if (givenPassword.equals(tt.getPassword()) && givenUserName.toLowerCase().equals(tt.getUsername().toLowerCase())) return true;
        }
        return false; // if we have made it this far....
    }
    
    public void addNewAccount(String givenUserName, String givenPassword) {
    	accountList.add(new account(givenUserName.toLowerCase(), givenPassword));
    	this.saveAccounts();
    }
    
    public void changePassword(String givenUserName, String newPassword) {
    	for (account t: accountList) {
    		if (t.getUsername().toLowerCase().equals(givenUserName.toLowerCase())) {
    			t.setPassword(newPassword);
    		}
    	}
    	this.saveAccounts(); // we want to immediately save any password changes to disk.
    }
    
    public AccountStorage() {
    	this.loadAccounts();
    }
    
	
	
}