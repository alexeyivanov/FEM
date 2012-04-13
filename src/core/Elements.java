package core;

import java.util.*;

public class Elements extends ArrayList<Element> {

	private static final long serialVersionUID = 1L;
	
	private Time time;
	
	public Elements() {
		time = new Time(0);		
	}
	
	public Time getTime() {
		return time;
	}
	
}
