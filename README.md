# MyHost

Next Steps:
===========

1. Need to add Table handling / timers.
2. Should there be a table timer, thus starting the cleaning process and effectively kicking out the current customer, and notifying the next customers? 
3. Need to add Java server communication ability between this backend code and the website front end. 
4. There's always more....

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







