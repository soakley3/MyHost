import java.util.concurrent.TimeUnit;

public class ITSC4155 {
	public static void main(String[] args) {
		System.out.println("hello ITSC4155");
		
		restaurant resty = new restaurant();
		
		table t0 = new table(1);
		table t1 = new table(3);
		table t2 = new table(4);
		table t3 = new table(5);
		table t4 = new table(6);
		
		
		resty.addTable(t0);
		resty.addTable(t1);
		resty.addTable(t2);
		resty.addTable(t3);
		resty.addTable(t4);
		
		
		
		group a = new group("aaa", "9199868283", 1, true);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		group b = new group("bbb", "9199868283", 3, true);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		group c = new group("ccc", "9199868283", 4, true);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		group d = new group("ddd", "9199868283", 5, true);
		group e = new group("eee", "9199868283", 1, true);
		
		
		t0.queueGroup(a);
		t1.queueGroup(b);
		t2.queueGroup(c);
		t3.queueGroup(d);
		t0.queueGroup(e); // add to the queue.
		
		
		for (table tTable: resty.getTables()) {
			if (tTable.isReady()) {
				tTable.seatNext();
			}
			if (tTable.getCurrentlySat() != null) {
				System.out.println(tTable.getCurrentlySat().getName() + ", "+tTable.getCurrentlySat().getSecondsWaiting());
				System.out.println(tTable.isReady());
			} 
			for (group g: tTable.getQueued()) {
				System.out.println("Queued, "+tTable.getSeats()+", " + g.getName());
			}
		}
		
		

		
	}
}