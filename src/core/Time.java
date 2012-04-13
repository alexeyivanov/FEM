package core;

public class Time {
	
	public class Period  {
		
		private Time first, last;
		
		public Period(Time first, Time last) {			
			this.first = first;
			this.last = last;
		}
		
		public Period(Time first) {			
			this.first = first;
			this.last = null;
		}		
		
		public void setLastTime(Time t) {
			last = t;
		}
		
	}
	
	public Period periodFrom() {
		return new Period(this);
	}
	
	private static double MINIMUM_TIME_DIFFERENCE = 1E-10;
	private double time;		
	private String description;
	private Time next = null, previous = null;	
	
	public Time(double time) {
		this.time = time;	
		this.description = "";
	}
	
	public Time(double time, String description) {
		this.time = time;	
		this.description = description;			
	}
	
	public Time next() {
		return next;
	}
	
	public Time previous() {
		return previous;
	}
	
	public double get() {
		return time;
	}
	
	public String getDescription() {
		return description;
	}
	
	public double delta() {
		if (previous != null) return time - previous.time; else return 0;			
	}
					
	public boolean inside(Period period) {
		
		if (time >= period.first.time && period.last == null) return true;
		if (time >= period.first.time && time <= period.last.time) return true;			
		return false; 
	}
	
	public boolean after(Period period) {			
		if (period.last == null) return false;
		if (time > period.last.time) return true;			
		return false; 
	}
	
// not necessary?	
	
//	public boolean atBegin(Period period) {
//		if (period.first == this) return true;
//		return false;			
//	}
//	
//	public boolean immediatlyAfter(Period period) {
//		if (period.last == null) return false;
//		if (period.last.next == this) return true;
//		return false;
//	}
	
	public Time toBegin() {
		Time t = this;
		while (t.previous != null) t = t.previous;
		return t;
	}
	
	public Time[] getArray() {
		int count = 0;
		Time t = toBegin();
		while (t != null) { count++; t = t.next; };
		t = toBegin();
		Time[] array = new Time[count];
		for (int i = 0; i < count; i++) { array[i] = t; t = t.next; } 
		return array;
	}
	
	public Time add(double time) {
		Time t = toBegin();
		Time last = t;
		while (t != null) {
			if (Math.abs(t.time - time) < MINIMUM_TIME_DIFFERENCE) return t;
			if (t.time > time) {
				Time newTime = new Time(time);
				if (t.previous != null) t.previous.next = newTime;				
				newTime.previous = t.previous;
				newTime.next = t;
				t.previous = newTime;
				return newTime;
			}
			last = t;
			t = t.next;			
		}
		Time newTime = new Time(time);
		last.next = newTime;
		newTime.previous = last;
		newTime.next = null;
		return newTime;
	}
	
}
