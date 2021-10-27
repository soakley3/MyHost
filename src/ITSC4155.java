import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.*;


public class ITSC4155 {
	
	
	
	
	public static void main(String[] args) {
		System.out.println("hello ITSC4155");
		// Setup restaurant 
		restaurant resty = new restaurant();

		for(table t: resty.getTables()) {
			t.setSeats(20);
		}
		
		// Add a few extra tables
		resty.addTable(new table(4));
		resty.getTableByID(4).setSeats(20);
		resty.getTableByID(4).setShape("RAINBOW");		
		
		resty.addTable(new table(5));
		resty.getTableByID(5).setSeats(20);
		resty.getTableByID(5).setShape("RAINBOW");	
		
		resty.addTable(new table(6));
		resty.getTableByID(6).setSeats(20);
		resty.getTableByID(6).setShape("RAINBOW");	
		
		resty.addTable(new table(7));
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
				group t = new group(name, "9199868283", r.nextInt(10), (variableTable >= 1 ? true:false) );
				
				resty.getTableByID(r.nextInt(7)+1).queueGroup(t);
				
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
		
		for (table t: resty.getTables()) {
			System.out.println("------------------");
			System.out.println(t.toString());
			System.out.println("------------------");
		}
		
	
		// Now add a new table to test if any one wants it.
		// This will test the sorting algo to determine if we do choose the longest waiting queued group 
		// and offer them the table, only if they want it. 
		
		
		resty.sortGroupsByLongestWaiting();
		
		/*
		 * Okay the sorting works - example output, note that node 0 of the linked list is the longest queued.
		 * Next: implement the ability to send out coms regarding if a table is open or not.
		 * 
		 * *-----------*
			Name: dagwjzrllv; phone: 9199868283; Size: 5; Variable Table: true; : 35  <---- seconds waiting!!!!
			*-----------*
			Name: wezbqitcml; phone: 9199868283; Size: 7; Variable Table: true; : 33
			*-----------*
			Name: jhospftrvi; phone: 9199868283; Size: 8; Variable Table: true; : 32
		 * 
		 */
		
		//resty.saveConfig();
		
		

		
	}
}