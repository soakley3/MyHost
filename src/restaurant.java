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
	
	
	
	private int id;
	private String name;
	private String phone;
	private String StartOfDay;
	private String EndOfDay;
	
	// list of tables 
	private LinkedList<table> tables;
	
	public void addTable(table x) {
		tables.add(x);  
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
	
	
	// this can be used to contact the groups waiting longest.
	// This may need to be spun off into it's own thread to prevent blocking of restaurant 
	// oeprations in the main loop
	private void sortGroupsByLongestWaiting() {
		LinkedList<group> sortable = new LinkedList<>();
		for (table singleTable : tables) {
			for (group waiting : singleTable.getQueued()) {
				sortable.add(waiting);
			}
		}
		Collections.sort(sortable, new Comparator<group>() {
		@Override
			public int compare(group g1, group g2) {
				return g1.getSecondsWaiting() - g2.getSecondsWaiting();
			}
		});
		// now the sortable linked list is sorted for the longest waiting, we can
		// pop through the list and see who is compatible with the number of free
		// seats from the free table. 
	}
	
	public restaurant() {
		// load all of the tables and config for this restaurant.
		// Options:
		// 1. Database?                             <-- good for future dev.
		// 2. Json config file for locally run app? <-- likely best for this project.
		// 3. xml config?
		// 4. Custom config? 
		
		tables = new LinkedList<table>();
		parseConfig();
	}
	
	private void parseConfig() {
		try (BufferedReader br = new BufferedReader(new FileReader("ServerConfig.json"))) {
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
		       
		       System.out.println(line + " - "+ configLevel);
		       System.out.println(splitLine[0] + ":" + splitLine[1] + ":"+ configLevel);
		       
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
			    		   addTable(new table(mostRecentTableID));
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
			FileWriter myWriter = new FileWriter("ServerConfig2.json");
			
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