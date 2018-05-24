package de.supremeprojekt.snake;

import com.badlogic.gdx.Input.Keys;

public enum Direction {
	N, E, S, W;
	
	public Direction getNewDir(int keyIn) {
		Direction newdir = getDir(keyIn);
		
		if(newdir == null)
			return null;
		
		if(this.getOppositeDir() == newdir)
			return this;
		
		return newdir;
	}
	
	private Direction getDir(int keyIn) {
		switch(keyIn)
		{
		case Keys.UP:
			return N;
		case Keys.LEFT:
			return W;
		case Keys.DOWN:
			return S;
		case Keys.RIGHT:
			return E;
		default:
			return null;
		}
	}
	
	private Direction getOppositeDir(Direction dir) {
		switch(dir)
		{
		case N:
			return S;
		case E:
			return W;
		case S:
			return N;
		case W:
			return E;
		default:
			return null;
		}
	}
	
	private Direction getOppositeDir() {
		return getOppositeDir(this);
	}
}
