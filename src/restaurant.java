import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

class restaurant {
	
	public AccountStorage accStores; 
	
	private int id;
	public String name;
	private String phone;
	private String StartOfDay;
	private String EndOfDay;
	
	
	// list of tables 
	public LinkedList<table> tables;
	
	public void addTable(table x) {
		tables.add(x);  
	}
	
	public int getHighestTableIndex() {
		int highest = 0;
		for (table t: tables) {
			int z = t.getID();
			if (z > highest)
				highest = z;
		}
		return highest;
	}
	
	
	public LinkedList<table> getTables() {
		return tables;
	}
	
	public table getTableByID(int _id) {
		for (table singleTable : tables) {
			if (singleTable.getID() == _id) 
				return singleTable;
		}
		return null;
	}
	
	public String getRestaurantName() {
		return name;
	}
	
	public boolean tableExists(int zid) {
		return getTableByID(zid) != null;
 	}
	
	
	// this can be used to contact the groups waiting longest.
	// This may need to be spun off into it's own thread to prevent blocking of restaurant 
	// oeprations in the main loop
	public LinkedList<group> sortGroupsByLongestWaiting() {
		LinkedList<group> sortable = new LinkedList<>();
		//System.out.println("--> " + tables);
		for (table singleTable : tables) {
			//System.out.println("---> " + singleTable.toString());
			for (group waiting : singleTable.getQueued()) {
				if (waiting.openToDifferentTable()) {          // only add the group to the list if they're open to a different table
					sortable.add(waiting);
				}
			}
		}
		Collections.sort(sortable, new Comparator<group>() {
		@Override
			public int compare(group g1, group g2) {
				return g2.getSecondsWaiting() - g1.getSecondsWaiting();
			}
		});
		
		for (group g: sortable) {
			System.out.println("*-----------*");
			System.out.println(g.toString()+": "+g.getSecondsWaiting());
		}
		// now the sortable linked list is sorted for the longest waiting, we can
		// pop through the list and see who is compatible with the number of free
		// seats from the free table. 
		
		return sortable;
	}
	
	public restaurant() {
		// load all of the tables and config for this restaurant.
		// Options:
		// 1. Database?                             <-- good for future dev.
		// 2. Json config file for locally run app? <-- likely best for this project.
		// 3. xml config?
		// 4. Custom config? 
		
		//tables = new LinkedList<table>();
		//parseConfig();
		accStores = new AccountStorage();
		for (account t: accStores.accountList) {
			System.out.println(">> " + t.getUsername() + " "+t.getPassword());
		}
		System.out.println(">>> "+accStores.isAccount("smiley.puma@gmail.com", "bigPass1"));   //true
		System.out.println(">>> "+accStores.isAccount("will.doug@gmail.com", "oldDumbpass2")); //true
		System.out.println(">>> "+accStores.isAccount("will.doug@gmail.com", "oldDumbpass3")); //false
		
		
		account t2 = new account("will.doug@gmail.com", "oldDumbpass2");
		account t3 = new account("will.doug@gmail.com", "oldDumbpass3");
		account t4 = new account("will.doug@gmail.com", "oldDumbpass2");
		System.out.println(">>>> "+t2.equals(t3));
		System.out.println(">>>> "+t2.equals(t4));
		System.out.println(">>>>> "+accStores.accountList.contains(t2));
		System.out.println(">>>>> "+accStores.accountList.contains(t3));
		
		accStores.changePassword("will.doug@gmail.com", "oldDumbpass23");

		for (account t: accStores.accountList) {
			System.out.println(">> " + t.getUsername() + " "+t.getPassword());
		}

	}
	
	public void parseConfig() {
		try (BufferedReader br = new BufferedReader(new FileReader("ServerConfig2.json"))) {
			int mostRecentTableID = -1;
		    String line;
		    int configLevel = 0;
		    while ((line = br.readLine()) != null) {
		       
		       if (line.contains("{") || line.contains("[")) {
		    	   configLevel++;
		    	   continue;
		       }
		       if (line.contains("}") || line.contains("]")) {
		    	   configLevel--;
		    	   continue;
		       }

		       line = line.replaceAll("\"", "");     // remove quotes
		       line = line.replaceAll(" ", "");      // remove spaces
		       line = line.replaceAll(",", "");      // remove comma
		       line = line.replaceAll("	", "");      // remove tab
		       
		       String[] splitLine = line.split(":"); // split the string into string[], key:value
		       
		       //System.out.println(line + " - "+ configLevel);
		       //System.out.println(splitLine[0] + ":" + splitLine[1] + ":"+ configLevel);
		       
		       // Here we set retaurant config
		       if (configLevel == 2) { 
		    	   switch (splitLine[0]) {
			    	   case "restaurant_name": {
			    		   name = splitLine[1];
			    		   break;
			    	   }
			    	   case "restaurant_phone": {
			    		   phone = splitLine[1];
			    		   break;
			    	   }
			    	   case "hours_open": {
			    		   StartOfDay = splitLine[1];
			    		   break;
			    	   }
			    	   case "hours_end": {
			    		   EndOfDay = splitLine[1];
			    		   break;
			    	   }
		    	   }
		       }    
		       // Here we load the table configuration 
		       if (configLevel == 3) { 
		    	   switch (splitLine[0]) {
			    	   case "table_id": {
			    		   mostRecentTableID = Integer.parseInt(splitLine[1]);
			    		   System.out.println("MOST RECENT TABLE ID: "+mostRecentTableID);
			    		   addTable(new table(this, mostRecentTableID));
			    	   }
			    	   case "table_shape": {
			    		   getTableByID(mostRecentTableID).setShape(splitLine[1]);
			    		   break;
			    	   }
			    	   case "seats": {
			    		   getTableByID(mostRecentTableID).setSeats(Integer.parseInt(splitLine[1]));
			    		   break;
			    	   }
		    	   }
		       }      
		    }
		} catch (FileNotFoundException e) {
			System.out.println("Failure reading the config file.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure reading the config file, indiscernible error.");
			e.printStackTrace();
		}
	}
	
	public void saveConfig() { 
		try {
			FileWriter myWriter = new FileWriter("ServerConfig3.json");
			
			String toSave = "{\n"
				+ "	\"restaurant\": {\n"                         // one tab
				+ "		\"restaurant_name\": \""+name+"\",\n"    // two tab
				+ "		\"restaurant_phone\": \""+phone+"\",\n"  // two tab
				+ "		\"hours_open\": \""+StartOfDay+"\",\n"   // two tab 
				+ "		\"hours_end\": \""+EndOfDay+"\",\n"               // two tab
				+ "	}\n"                                 // one tab
				+ "	\"tables\": [\n";                    // one tab
			for (table s: getTables()) {
				toSave += "		{\n"
						+ "			\"table_id\": \""+s.getID()+"\",\n"
						+ "			\"table_shape\": \""+s.getShape()+"\",\n"
						+ "			\"seats\": \""+s.getSeats()+"\"\n"
						+ "		},\n";
			}
			toSave += "	]\n}";
				
			myWriter.write(toSave);
	        myWriter.close();
	        System.out.println("Successfully saved the current restaurant configuration.");
	    } catch (IOException e) {
	        System.out.println("An error occurred whilst saving the restaurant configuration.");
	        e.printStackTrace();
	    }
	}
	
}