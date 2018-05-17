package memtest;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

	Map<MemDef,Object> memory = new ConcurrentHashMap<MemDef,Object>();
		
	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		
		ScheduledExecutorService svc = Executors.newScheduledThreadPool(1);
		Cleanup cleanup = new Cleanup();
		svc.scheduleAtFixedRate(cleanup, 0, 1, TimeUnit.SECONDS);
		
		while (true) {
			MemDef md = getMemDef();
			say(md.toString());
			allocate(md);
		}
	}
	
	private void allocate(MemDef md) {
		for (int i = 0; i < md.count; i++) {
			char[] arr = new char[md.size];
			memory.put(md.copyOf(), arr);
		}
	}

	private MemDef getMemDef() {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		System.out.print("size (mb): ");
		int size = sc.nextInt() * 1024 * 1024;
		System.out.print("count: ");
		int count = sc.nextInt();
		System.out.print("ttl (sec): ");
		int ttlMs = sc.nextInt() * 1000;
		
		return new MemDef(size, count, ttlMs);
		
	}

	private void say(String format, Object... objects) {
		if (objects != null) {
			format = String.format(format, objects);
		}
		System.out.println(format);
	}
	
	private class MemDef {
		int size;
		int count;
		int ttlMs;
		long createdAt;

		public MemDef(int size, int count, int ttlMs) {
			super();
			this.size = size;
			this.count = count;
			this.ttlMs = ttlMs;
			this.createdAt = System.currentTimeMillis();
		}

		public MemDef copyOf() {
			MemDef newMd = new MemDef(this.size, this.count, this.ttlMs);
			newMd.createdAt = this.createdAt; 
			return newMd;
		}

		@Override
		public String toString() {
			return "MemDef [size=" + size + ", count=" + count + ", ttlMs=" + ttlMs + "]";
		}
	}
	
	private class Cleanup implements Runnable{
		@Override
		public void run() {
			long now = System.currentTimeMillis();
			Set<MemDef> set = memory.keySet();
			for (MemDef k : set) {
				if (k.ttlMs > 0 && now - k.createdAt >= k.ttlMs) {
					memory.remove(k);
					say("\ndone with %d bytes", k.size * k.count);
				}
			}
		}
	}
}
