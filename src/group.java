import java.util.Date;



class group {
	
	
	private String name;
	private String phoneNumber;
	
	// number of members of the group.
	private int size; 
	
	// record the time spent waiting the queue for a table.
	private long timeWaiting;
	
	// is this group open to sitting at a different table if one is available earlier than the selected table? 
	private boolean tableIsVariable;
	
	
	// unique id / hash for this group? 
	private int id; // 
	
	public int getGroupSize() {
		return size;
	}
	
	// Parameters:     Contact name, contact phone number, # chairs required,  willing to accept a different table? 
	public group(String contactName, String contactNumber, int partSize, boolean tableDifferent) {
		name = contactName;
		phoneNumber = contactNumber;
		size = partSize;
		tableIsVariable = tableDifferent;
		timeWaiting = System.currentTimeMillis();	
	}
	
	public String getName() { 
		return name;
	}
	
	public String getNumber() {
		return phoneNumber;
	}
	
	public int getPartySize() {
		return size;	
	}
	
	
	
		
	public void adjustTableVariability(boolean isVariable) {
		tableIsVariable = isVariable;
	}
	
	// This returns the length of time the group has been waiting in seconds.
	// This should be granular enough for the logic of the system to make appropriate time 
	// decisions for the group(s) waiting the longest. 
	public int getSecondsWaiting() { 
		return (int)(System.currentTimeMillis() - timeWaiting)/1000; 
	}

	
}