import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
 * This class is for restaurant class instantiation and testing.
 * Theres a lot of garbage in here, but also some worthwhile commentary.
 */


public class ITSC4155 {
	
	
	public static void main(String[] args) {
		System.out.println("hello ITSC4155");
		// Setup restaurant 
		restaurant resty = new restaurant();
			
		resty.tables = new LinkedList<table>();
		resty.parseConfig();
	
		// Yee haw start the communication thread, including the resty class
		
		communicator server = null;
        try {		
			server = new communicator(resty, 5000);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        if (server == null) {
        	System.out.println("Failed to start server");
        	return;
        }
        
        // persistent checking for available tables.
        // We'll need to make these functions less verbose, because they're absolutely spamming the console 
        // to the point where its not readable..............................
        while(!server.die ) {
        	// loop doing table maintenance, like checking for free tables when others are queued.
        	for (table tz: resty.getTables()) {
        		tz.checkAvailability();
        	}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
        	
        }
        
		System.out.println("past the server instantiation.");
		
		
		// NOTE THE MODIFIED TABLE CONFIG **MUST** BE SAVED WHEN THE COMMUNICATOR THREAD DIES!!!!!!!
		// NOT HERE, because this executes and completes before the end of the communicator thread.
	}
	
	
	
	
	
	public void testExtraTablesAndPeople() { 
		/*x
		for(table t: resty.getTables()) {
			t.setSeats(20);
		}
		
		// Add a few extra tables
		resty.addTable(new table(resty, 4));
		resty.getTableByID(4).setSeats(20);
		resty.getTableByID(4).setShape("RAINBOW");		
		
		resty.addTable(new table(resty, 5));
		resty.getTableByID(5).setSeats(20);
		resty.getTableByID(5).setShape("RAINBOW");	
		
		resty.addTable(new table(resty, 6));
		resty.getTableByID(6).setSeats(20);
		resty.getTableByID(6).setShape("RAINBOW");	
		
		resty.addTable(new table(resty, 7));
		resty.getTableByID(7).setSeats(20);
		resty.getTableByID(7).setShape("RAINBOW");	
		
		// print some table garbage.
		System.out.println("Num tables: "+ resty.getTables().size());
		for(table t: resty.getTables()) {
			System.out.println("T: "+t.toString());
		}
		
		// Add many groups with different lengths of time being queued. 
		// This will allow the sorting algo to be tested.
		ArrayList<group> groups = new ArrayList<group>();
		for (int i = 0; 4 > i; i++) {
			for (int j = 0; 10 > j; j++ ) {
				String name = "";
				Random r = new Random();
				for (int x = 0; 10 > x; x++) {
					name += (char)(r.nextInt(26) + 'a');
				}
				int variableTable = r.nextInt(4);
				System.out.println("Variable table = "+variableTable);
				int assignt = r.nextInt(7)+1;
				//System.out.println("Assigned: " + assignt);
				
				group t = new group(name, "9199868283", r.nextInt(10), assignt, (variableTable >= 1 ? true:false) );
				
				resty.getTableByID(assignt).queueGroup(t);

				
				//groups.add(t);
				
				System.out.println(t);
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
x		*/ 
		/*
		 * SAMPLE TEST CASE OUTPUT 
		 * 
		 * 
		 * ------------------
			Table: 1; Shape: Square; Seats: 20;
			Currently Sat: Name: iblditdieo; phone: 9199868283; Size: 8; Variable Table: true; ; Queued: Name: rehjnzdwxw; phone: 9199868283; Size: 6; Variable Table: true;  - Name: pvcmgjfvkf; phone: 9199868283; Size: 8; Variable Table: true;  - ;
			------------------
			<..snip..>
			------------------
			Table: 7; Shape: RAINBOW; Seats: 20;
			Currently Sat: Name: ikrycxokjy; phone: 9199868283; Size: 7; Variable Table: true; ; Queued: Name: yfwuagjgsl; phone: 9199868283; Size: 7; Variable Table: true;  - Name: zxtbglinve; phone: 9199868283; Size: 9; Variable Table: false;  - Name: kqzpzwsxjz; phone: 9199868283; Size: 8; Variable Table: true;  - Name: tdbvcdwksh; phone: 9199868283; Size: 5; Variable Table: true;  - Name: datyslnfgc; phone: 9199868283; Size: 6; Variable Table: true;  - Name: ozwbhaequz; phone: 9199868283; Size: 5; Variable Table: true;  - ;
			------------------
		 * 
		 */
		/*y
		for (table t: resty.getTables()) {
			System.out.println("------------------");
			System.out.println(t.toString());
			System.out.println("------------------");
		}
		
	
		System.out.println("");
		System.out.println("");
		System.out.println("-- ADD NEW TABLE AND TEST ALTERNATE QUEUING");
		System.out.println("");
		System.out.println("");
		// Now add a new table to test if any one wants it.
		// This will test the sorting algo to determine if we do choose the longest waiting queued group 
		// and offer them the table, only if they want it. 
		
		resty.addTable(new table(resty, 8));
		resty.getTableByID(8).setSeats(2);
		resty.getTableByID(8).setShape("CUBE");
		
		
		// Since this table is new it is marked as totally free. No one is currently sat or queued.
		// Calling seatNext() will look at the other tables, see whos been waiting the longest, and then offer to seat them.
		// The test immediately accepts them. But from the debug output we can see that it's the longest waiting group.
		resty.getTableByID(8).seatNext(); 
		
		for (table t: resty.getTables()) {
			System.out.println("------------------");
			System.out.println(t.toString());
			System.out.println("------------------");
		}
		
		// UPDATE IT WORKS, now the longest waiting group can move to a different table.
		// This was achieved by essentially passing a "pointer" from the restaurant class to the table class on table instantiation!!!!
		
		System.out.println("");
		System.out.println("");
		System.out.println("-- Clean table 8 and then ask for another waiting customer");
		System.out.println("");
		System.out.println("");
		resty.getTableByID(8).setCleaning(); // this nulls out the current customer
		resty.getTableByID(8).seatNext();    // this should now bring the second longest waiting customer
		
		for (table t: resty.getTables()) {
			System.out.println("------------------");
			System.out.println(t.toString());
			System.out.println("------------------");
		}
		
		// It tests the length of time waiting, and then if the group size <= size of table. 
		
		// Next, add server communication.
		
		
y		*/
	}
	
	public void actualMain() { 
		// nadas for now.
	}
	
	
}