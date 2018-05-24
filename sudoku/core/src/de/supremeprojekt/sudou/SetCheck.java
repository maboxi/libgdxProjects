package de.supremeprojekt.sudou;

public class SetCheck {
	private boolean[] ints;
	
	public SetCheck() {
		ints = new boolean[9];
	}
	
	public void reset() {
		for(int i = 0; i < ints.length; i++)
			ints[i] = false;
	}
	
	public void add(int i) {
		if(i > ints.length || i <= 0)
			return;
		
		ints[i - 1] = true;
	}
	
	public boolean check() {
		for(int i = 0; i < ints.length; i++)
			if(!ints[i])
				return false;
		
		return true;
	}
}
