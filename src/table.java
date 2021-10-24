
import java.util.*;

class table { 

	// Status options - 0: Available, 1: In use, 2: cleaning.
	// Consider: 0, a group can sit down.
	//           1, the table is unavailable.
	//           2, the table is being cleaned, so notify the group to return
	final int STATUS_AVAILABLE = 0; 
	final int STATUS_OCCUPIED  = 1; 
	final int STATUS_CLEANING  = 2;
	private int status; 
	
	// set this as true when the table as totally available: when there is no one occupying it and the queue is empty.
	boolean totallyFree;
	
	// non null for current group, null for no group currently occupying the table
	private group currentlySat;
	
	
	// Unique id or hash for this table object.
	private int id; 
	private String shape;
	
	
	// Available seating so groups can be privy
	private int seats;
	
	
	// note we need a FIFO (first in first out) queue for groups, so linkedlist has fixed order.
	// <1>..<2>..<3>..<4>, etc.
	//  ^ pop this when the table is set to available status and the customer accepts the table.
	private LinkedList<group> queue;
	
	public group getCurrentlySat() { 
		return currentlySat;
	}
	
	public boolean queueGroup(group addition) {
		if (meetsRequirements(addition.getGroupSize())) {
			return queue.add(addition); //true for success, false for failure to add to the queue
		} else {
			System.out.println("Group too big for table.");
			return false;
		}
	}
	
	public boolean seatNext() {
		if (queue.size() == 0) {
			totallyFree = true;
			return false;
		}
		currentlySat = queue.pop(); // assign the next group to sit at the table. 
		return (currentlySat != null);
	}
	
	public LinkedList<group> getQueued() {
		return queue;
	}
	
	public boolean isReady() {
		return ((status == STATUS_AVAILABLE) && (currentlySat == null));
	}
	
	// This removes the current group from the seating.
	public void setCleaning() {
		currentlySat = null;
		status = STATUS_CLEANING;
	}
	
	// The current group should have already been removed from seating by the time this is called.
	public void setReady() {
		status = STATUS_AVAILABLE;
	}
	
	public boolean meetsRequirements(int partySize) {
		return (seats >= partySize);
	}
	
	public int getSeats() { 
		return seats;
	}
	
	public String getShape() {
		return shape;
	}
	
	public int getID() {
		return id;
	}
	
	public void setSeats(int chairs) { 
		seats = chairs;
	}
	
	public void setShape(String s) {
		shape = s;
	}
	
	//public table(int _id, int chairs, String tableShape) {
	public table(int _id) {
		id = _id;
		currentlySat = null;
		queue = new LinkedList<group>();
		status = STATUS_AVAILABLE;
	}
	
	public String toString() {
		return "Table " + Integer.toString(id) + "; Shape " + shape +"; Seats " + Integer.toString(seats) + ";";
	}
}