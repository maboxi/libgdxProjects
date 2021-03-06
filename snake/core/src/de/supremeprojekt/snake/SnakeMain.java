package de.supremeprojekt.snake;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class SnakeMain extends ApplicationAdapter implements InputProcessor {
	public static final int CELLSIZE = 10;
	public static final int WALL = -1;
	public static final Random RANDOM = new Random();

	SpriteBatch batch;
	ShapeRenderer sr;
	BitmapFont font;
	
	public int screenW, screenH;

	public int sizeX, sizeY;
	public int[][] grid;

	public int posX, posY;
	public int length;
	public int foodX, foodY;

	public long lastUpdate;
	public int intervall;

	public Direction dir;

	public boolean aimHelp;
	public boolean running;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		font = new BitmapFont();
		
		screenW = Gdx.graphics.getWidth();
		screenH = Gdx.graphics.getHeight();

		sizeX = screenW / CELLSIZE;
		sizeY = screenH / CELLSIZE;

		grid = new int[sizeX][sizeY];

		posX = sizeX / 2;
		posY = sizeY / 2;

		for (int x = 0; x < sizeX; x++)
			for (int y = 0; y < sizeY; y++)
				grid[x][y] = 0;

		grid[posX][posY] = 1;
		length = 15;
		
		dir = Direction.N;

		lastUpdate = System.currentTimeMillis();
		intervall = 250;

		foodX = 0;
		foodY = 0;

		aimHelp = false;

		
		for(int i = 0; i < 8; i++)
			System.out.println(i + ": " + createBarrier());

		resetFood();
		
		Gdx.input.setInputProcessor(this);
		
		running = true;
	}

	@Override
	public void render () {
		if(running)
			update();
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sr.begin(ShapeType.Filled);

		sr.setColor(230f / 255f, 230f / 255f, 230f / 255f, 1.0f);

		int val;
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {

				if (x == foodX && y == foodY) {
					sr.setColor(0, 1.0f, 0, 1.0f);

					sr.rect(x * CELLSIZE, y * CELLSIZE, CELLSIZE, CELLSIZE);

					sr.setColor(230f / 255f, 230f / 255f, 230f / 255f, 1.0f);
					continue;
				}

				val = grid[x][y];

				if (val > 0)
					sr.setColor(0, 0, 0, 1.0f);
				
				else if(val == WALL)
					sr.setColor(1, 0, 0, 1);
				else if (aimHelp && (x == posX || y == posY))
					sr.setColor(200f / 255f, 200f / 255f, 200f / 255f, 1.0f);

				sr.rect(x * CELLSIZE, y * CELLSIZE, CELLSIZE, CELLSIZE);

				if (val > 0 || aimHelp && (x == posX || y == posY) || val == WALL)
					sr.setColor(230f / 255f, 230f / 255f, 230f / 255f, 1.0f);
			}
		}

		sr.end();
		

		batch.begin();
		font.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		font.draw(batch, "Running: " + running, 5, screenH - 5);
		font.draw(batch, "Length: " + length, 5, screenH - 20);
		batch.end();
	}

	public void update() {
		long current = System.currentTimeMillis();

		if (Math.abs(current - lastUpdate) >= intervall) {
			lastUpdate = current;

			for (int x = 0; x < sizeX; x++) {
				for (int y = 0; y < sizeY; y++) {
					if (grid[x][y] > 0) {
						grid[x][y]++;
					}
				}
			}

			for (int x = 0; x < sizeX; x++) {
				for (int y = 0; y < sizeY; y++) {
					if (grid[x][y] > length) {
						grid[x][y] = 0;
					}
				}
			}

			// Bewegung

			switch (dir) {
			case N:
				posY++;
				break;
			case E:
				posX++;
				break;
			case S:
				posY--;
				break;
			case W:
				posX--;
				break;
			default:
				break;
			}

			if (posX < 0)
				posX = sizeX - 1;
			else if (posX >= sizeX)
				posX = 0;

			if (posY < 0)
				posY = sizeY - 1;
			else if (posY >= sizeY)
				posY = 0;

			if (posX == foodX && posY == foodY) {
				length++;
				resetFood();
			}
			
			if(grid[posX][posY] == 0)
				grid[posX][posY] = 1;
			else
				running = false;
		}
	}

	public boolean createBarrier() {
		int x = RANDOM.nextInt(sizeX);
		int y = RANDOM.nextInt(sizeY);
		int length = RANDOM.nextInt(10) + 10;
		Direction dir = Direction.getDir(RANDOM.nextInt(4));
		
		int tries = 0;
		
		while(checkBarrier(x, y, length, dir)) {
			
			if(tries > 20)
				return false;
			
			x = RANDOM.nextInt(sizeX);
			y = RANDOM.nextInt(sizeY);
			length = RANDOM.nextInt(10) + 10;
			dir = Direction.getDir(RANDOM.nextInt(4));
			tries++;
		}
		
		for(int i = 0; i < length; i++) {
			if(dir == Direction.N) {
				grid[x][y + i] = WALL;
			}
			if(dir == Direction.E) {
				grid[x + i][y] = WALL;
			}
			if(dir == Direction.S) {
				grid[x][y - i] = WALL;
			}
			if(dir == Direction.N) {
				grid[x - i][y] = WALL;
			}
		}
		
		return true;
	}

	public boolean checkBarrier(int x, int y, int len, Direction dir) {

		if (dir == Direction.N && (y + len - 1 > sizeY || y + len - 1 < 0)) {
			return true;
		}
		if (dir == Direction.E && (x + len - 1 > sizeX || x + len - 1 < 0)) {
			return true;
		}
		if (dir == Direction.S && (y - len + 1 > sizeY || y - len + 1 < 0)) {
			return true;
		}
		if (dir == Direction.N && (x - len + 1 > sizeX || x - len + 1 < 0)) {
			return true;
		}
		
		for (int i = 0; i < length; i++) {
			if (dir == Direction.N && grid[x][y + i] != 0) {
				return true;
			}
			if (dir == Direction.E && grid[x + i][y] != 0) {
				return true;
			}
			if (dir == Direction.S && grid[x][y - i] != 0) {
				return true;
			}
			if (dir == Direction.N && grid[x - i][y] != 0) {
				return true;
			}
		}

		return false;
	}

	public void resetFood() {
		do {
			foodX = RANDOM.nextInt(sizeX);
			foodY = RANDOM.nextInt(sizeY);
		} while (grid[foodX][foodY] != 0);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public boolean keyUp(int key) {

		Direction newDir = this.dir.getNewDir(key);

		if (newDir != null) {
			this.dir = newDir;

			return true;
		} else {
			if (key == Keys.SPACE) {
				aimHelp = !aimHelp;
			}
		}

		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
