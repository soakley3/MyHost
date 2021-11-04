
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
	
	private restaurant parent;
	
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
	
	private void immediatelySeatGroup(group addition) {
		
	}
	
	public void removeGroupFromTable(group g) {
		queue.remove(g); // delete this exact group.
	}
	
	public boolean queueGroup(group addition) {
		if (meetsRequirements(addition.getGroupSize())) {
			if (currentlySat == null) {
				System.out.println("Table " + id + ": Immediately seating this group: " + addition.toString());
				currentlySat = addition;
				return true;
			} else {
				System.out.println("Table " + id + ": queuing new group: "+ addition.toString());
				return queue.add(addition); //true for success, false for failure to add to the queue
			}
		} else {
			System.out.println("Group too big for table.");
			return false;
		}
	}
	
	
	public boolean queueExternalCandidates() {
		// Concerns: we need to avoid a race condition.
		// If we ping a customer (waiting for a different table) offering them to take this table, and someone else books 
		// through the app/website before this person accepts, the first contender might be overwritten by the second contender....
		System.out.println("* " + parent.getRestaurantName());
		LinkedList<group> allQueuedInOrder = parent.sortGroupsByLongestWaiting();
		if (allQueuedInOrder.size() == 0) {
			totallyFree = true;
			return false; // break if theres no one waiting for other tables.
		} else {
			// Here we the next allQueuedInOrder offer the table to the alternate table. All groups in the linkedList have chosen to be offered a 
			// different table, but we need to check if their group size will fit. 
			for (group possibleNext: allQueuedInOrder) {
				if (possibleNext.getPartySize() <= getSeats()) {
					// send offer out to the group via the webapp
					currentlySat = possibleNext; // <--- delete this. tis for testing. 
					parent.getTableByID(possibleNext.getAssignedTable()).getQueued().remove(possibleNext); // hopefully this should remove this exact group from the respective table

					setOccupied();               // <--- delete this
					break;                       // <--- delete this
				} 
				// else we skip it and go to the next party.
			}
		}		
		
		return false;
	}
	
	public boolean seatNext() {
		// check to see if the wait queue is empty. If so, then offer the table to people waiting for a DIFFERENT table. 
		if (queue.size() == 0) {
			totallyFree = true;
			// Reach out to the queued groups waiting for other tables. 
			queueExternalCandidates();
			return false;
		}
		setOccupied();
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
		currentlySat = null;
		seatNext(); // this will try and sit the next customer.
	}
	
	public void setOccupied() {
		status = STATUS_OCCUPIED;
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
	
	public table(restaurant par, int _id) {
		parent = par; // This is essentially a pointer to the resty/restaurant class and gives us access to all the tables!
		id = _id;
		currentlySat = null;
		queue = new LinkedList<group>();
		status = STATUS_AVAILABLE;
	}
	
	public String toString() {
		String toRet = "Table: " + Integer.toString(id) + "; Shape: " + shape +"; Seats: " + Integer.toString(seats) + ";\n";
		String queueue = "";
		for (group g: queue) {
			queueue += g.toString() + " - ";
		}
		String current = "";
		if (currentlySat == null) current = "No one";
		else current = currentlySat.toString();
		
		toRet += "Currently Sat: "+ current + "; Queued: "+ queueue +";";
		return toRet;
		
	}
}