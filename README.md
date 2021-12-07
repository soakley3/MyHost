# MyHost


Recent work:
============

1. Table handling complete. If someone adds themself as open to a new table, they'll eventually get scheduled to an open table
	if they've added to an occupied table. 

2. Socket communication started.

3. Test client created. This clearly over communicates to stress the server, but the webclient wont do that much communication.


Next Steps:
===========

1. Need to create login details for the backend. 

2. Need to add 1 hour timers to tables to kick out a patron and invite new ones. 

3. Add more socket commands for the table front end.

4. Add more socket commands for the admin view. We NEED this made in the webclient first! 

5. There's always more....

Completed:
==========

1. General Table / Group / Restaurant classes.
2. Reading the config on Server boot.
3. Saving the current table configuration from memory to disk, when closing down the back end server.


Notes:
======

1. Are we going to just skip using an actual database? That would be most effective if this application served MULTIPLE restaurants simulatenously. 
   This is going to remove a huge chunk of work, removing the requirement of a 24x7 production level database. We can just stick with this java app
   loading and parsing, then saving, the ServerConfig.json. 


Server Communication Guidelines:
================================

1. When writing to the client socket, the String must have "\n\r" appended so that the String makes it to the client.

	Example:
	```
	public void send(String t) {
		try {
			out.writeUTF(t+"\r\n");
			out.flush();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	```

2. When sending the string through the socket, use a colon as the delimeter.

	Example:

		"isFree:table:1"


3. When asking the server for any data, be prepared to receive a response, even if issuing "forceful" responses.
	An example is when setting a table free manually, ignoring the usual processes, expect to receive an response from the server 
	acknowledging that the table is actually now free. 


4. This is the current list (C)lient calls, and (S)erver responses. Obviously some of the details will be different. For example when asking
	if table is free, you'll need to adjust the ID value to the appropriate table. See sections (I) for insight. 

	```
	C: "isFree:table:1",//                        -> [isFree, table, 1]                           -> get the info from restaurant - is table of ID 1 free? 
	S: "table:1:true"
	I: The server response could be true, false, or doesntExist.
	```

	```
	C: "setFree:table:1",//                       -> [setFree, table, 1]                          -> manually/forcefully set the table of ID 1 free
	S: "table:1:true"
	I: The server response could be true, false, or doesntExist.
	```

	```
	C: "addTable:table:20:hexagon",//             -> [addTable, table, 20]                        -> manually add a table with 20 seats
	S: "addTable:4:true"
	I: The server response provides the ID of the new table, and whether or not it was successfully created.
	```

	```
	 //queue,table,tablenum,name,phone,partysize,opentonewtable
	C: "queue:table:1:Jonny Depp:919919919:3:true",// -> [queue, table, 1, Jonny Depp, phoneNum, group size, accepting other tables?] -> Add jonny depp to the queue for table 1
	S: "queue:table:1:Failed"
	I: The server response could be SatImmediately, queued, or doesntExist. Note that this failed because the group size exceeded the table size.
	```

	```
	C: "removeTable:table:1",//                   -> [removeTable, table, 1]                      -> manually/forcefully delete table of ID 1
	S: "removeTable:true"
	I: The server response could be true, false, or doesntExist.
	```

	```
	C: "addTable:table:20:hexagon",//             -> [addTable, table, 20, hexagon]
	S: "addTable:5:true"
	I: The server response provides the ID of the new table, and whether or not it was successfully created.
	```

	```
	C: "getAllTables",
	S: "getAllTables:Table: 2; Shape: Circle; Seats: 4;
	Currently Sat: No one; Queued: ;Table: 3; Shape: Square; Seats: 4;
	Currently Sat: No one; Queued: ;Table: 4; Shape: hexagon; Seats: 20;
	Currently Sat: No one; Queued: ;Table: 5; Shape: hexagon; Seats: 20;
	Currently Sat: No one; Queued: ;"
	I: This will send back all of the tables: ID, Shape, Seat number, Whos currently sat, and who's currently in the queue. Note that this could be a very large string!!!!
	```

	```
	C:"queue:table:1:Jonny Depp:919919919:2:true",
	S:"table:1:doesntExist"
	I: Definition above. This failed because we previously deleted the table with an ID of 1.
	```

	```
	C:"queue:table:5:Jonny Depp:919919919:2:true"
	S:"queue:table:5:satImmediately"
	I: Definition above. We sat jonny depp, and no one was sat so it was immediate. 
	```

        ```
        C:"getQueueSize:table:1"
        S:"queueSize:1:0"
        I: Definition above. The number of groups queued for table 1 is zero. 
        ```

        ```
        C:"testLogin:username:password"
        S:"testLogin:passed"
        S:"testLogin:failed:wrong account or password"
        I: Definion: sent via the login page to the system. Expect the server responses above, and parse to continue or not to the home page. 
        ```

        ```
        C:"changePassword:username:newPassword"
        S:"changePassword:passed"
        S:"hangePassword:failed:failed to change password"
        I: Definion: UNTESTED send this from the account change page to the server to change the logged in user's password. Expect the server responses above, and parse to continue or not to the home page.
        ```



Run time instructions:
======================

1. Download the repo.

2. cd into the src directory and run - 

	```
	javac ITSC4155.java
	```

3. Then to run the server:

	```
	java ITSC4155
	```

4. You'll need to do the same thing if you want to try out the tester client. It's located in src/Client/. Run as the following

	```
	java Client testcase1.txt
	```





