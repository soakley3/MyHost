import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

class restaurant {
	
	
	
	private int id;
	private String name;
	
	// list of tables 
	private LinkedList<table> tables;
	
	public void addTable(table x) {
		tables.add(x);  
	}
	
	public LinkedList<table> getTables() {
		return tables;
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
	}
	
	private void configLoad() {
		// load config
	}
	
}