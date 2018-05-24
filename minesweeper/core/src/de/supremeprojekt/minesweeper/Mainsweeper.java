package de.supremeprojekt.minesweeper;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Mainsweeper extends ApplicationAdapter implements InputProcessor{
	public static final Random RANDOM = new Random();
	
	public static final int TILESIZE = 30;
	
	public static final int CLEAR = 0;
	public static final int MINE = CLEAR - 1;
	public static final int STATUS_HIDDEN = 0;
	public static final int STATUS_SHOWN = 1;
	public static final int STATUS_FLAGGED = 2;
	
	public static final float MINES = 0.15f;

	public static int width, height;
	
	SpriteBatch batch;
	ShapeRenderer sr;
	BitmapFont font;
	GlyphLayout layout;
	
	public int[][] field_value;
	public int[][] field_status;
	public int sizex, sizey;
	
	public long starttime;
	public boolean started;
	
	public Mainsweeper(int width, int height) {
		super();
		Mainsweeper.width = width;
		Mainsweeper.height = height;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		font = new BitmapFont();
		layout = new GlyphLayout();
		
		this.sizex = width / TILESIZE;
		this.sizey = height / TILESIZE;
		
		field_value = new int[sizex][sizey];
		field_status = new int[sizex][sizey];
		
		Gdx.input.setInputProcessor(this);
		
		setup();
	}
	
	public void setup() {
		for(int i = 0; i < sizex; i++) {
			for(int j = 0; j < sizey; j++) {
				field_value[i][j] = CLEAR;
				field_status[i][j] = STATUS_HIDDEN;
			}
		}
		
		int mines = 0;
		int maxmines = (int) (sizex * sizey * MINES);
		
		int minex, miney;
		while(mines < maxmines) {
			minex = RANDOM.nextInt(sizex);
			miney = RANDOM.nextInt(sizey);
			
			if(field_value[minex][miney] != MINE) {
				field_value[minex][miney] = MINE;
				mines++;
			}
		}
		for(int x = 0; x < sizex; x++) {
			for(int y = 0; y < sizey; y++) {
				int code = field_value[x][y];
				if(code == CLEAR) {
					// Unten links
					if(x > 0 && y > 0 && field_value[x - 1][y - 1] == MINE)
						code++;
					// Unten rechts
					if(x < sizex - 1 && y > 0 && field_value[x + 1][y - 1] == MINE)
						code++;
					// Oben rechts
					if(x < sizex - 1 && y < sizey - 1 && field_value[x + 1][y + 1] == MINE)
						code++;
					// Oben links
					if(x > 0 && y < sizey - 1 && field_value[x - 1][y + 1] == MINE)
						code++;
					// Oben
					if(y < sizey - 1 && field_value[x][y + 1] == MINE)
						code++;
					// Rechts
					if(x < sizex - 1 && field_value[x + 1][y] == MINE)
						code++;
					// Unten
					if(y > 0 && field_value[x][y - 1] == MINE)
						code++;
					// Links
					if(x > 0 && field_value[x - 1][y] == MINE)
						code++;
					
					field_value[x][y] = code;
				}
			}
		}
		
		started = false;
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int code;
		int status;
		
		sr.begin(ShapeType.Filled);

		int border = 2;
		
		while(4 * border >= TILESIZE)
			border -= 2;
		
		for(int x = 0; x < sizex; x++) {
			for(int y = 0; y < sizey; y++) {
				code = field_value[x][y];
				status = field_status[x][y];
				if(status == STATUS_SHOWN) {
					sr.setColor(206f/ 255f,206f/ 255f, 206f/ 255f, 1.0f);
					sr.rect(x * TILESIZE, y * TILESIZE, TILESIZE, TILESIZE);
					
					if(code == MINE)
						sr.setColor(1.0f, 0.0f, 0.0f, 1.0f);
					else
						sr.setColor(215f / 255f, 215f / 255f, 215f / 255f, 1.0f);
					
					sr.rect(x * TILESIZE + border, y * TILESIZE + border, TILESIZE - 2 * border, TILESIZE - 2 * border);
				} else if(status == STATUS_FLAGGED) {
					sr.setColor(206f/ 255f, 206f/ 255f, 206f/ 255f, 1.0f);
					sr.rect(x * TILESIZE, y * TILESIZE, TILESIZE, TILESIZE);
					sr.setColor(242f/ 255f, 242f/ 255f, 242f/ 255f, 1.0f);
					sr.rect(x * TILESIZE + border, y * TILESIZE + border, TILESIZE - 2 * border, TILESIZE - 2 * border);
					sr.setColor(0.0f, 1.0f, 0.0f, 0.8f);
					sr.rect(x * TILESIZE + 4 * border, y * TILESIZE + 4 * border, TILESIZE - 8 * border, TILESIZE - 8 * border);
					//sr.circle(x * TILESIZE + TILESIZE / 2, y * TILESIZE + TILESIZE / 2, TILESIZE / 2 - border * 2);
				} else {
					sr.setColor(206f/ 255f, 206f/ 255f, 206f/ 255f, 1.0f);
					sr.rect(x * TILESIZE, y * TILESIZE, TILESIZE, TILESIZE);
					sr.setColor(242f/ 255f, 242f/ 255f, 242f/ 255f, 1.0f);
					sr.rect(x * TILESIZE + border, y * TILESIZE + border, TILESIZE - 2 * border, TILESIZE - 2 * border);
				}
			}
		}
		
		sr.end();
		
		batch.begin();
		
		String text;
		
		font.setColor(0.0f, 0.0f, 1.0f, 1.0f);
		for(int x = 0; x < sizex; x++) {
			for(int y = 0; y < sizey; y++) {
				code = field_value[x][y];
				status = field_status[x][y];
				
				if(status != STATUS_SHOWN)
					continue;
				
				if(code <= CLEAR)
					continue;
				
				text = "" + (code - CLEAR);
				layout.setText(font, text);
				font.draw(batch, text, x * TILESIZE + TILESIZE / 2 - layout.width / 2, y * TILESIZE + TILESIZE / 2 + layout.height / 2);
			}
		}
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		sr.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.SPACE) {
			for(int x = 0; x < sizex; x++)
				for(int y = 0; y < sizey; y++)
					if(field_value[x][y] != MINE)
						field_status[x][y] = STATUS_SHOWN;
			
			return true;
		}
		
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

	public void clearArea(int x, int y) {
		if(x > sizex || y > sizey || x < 0 || y < 0)
			return;
		int value = field_value[x][y];
		int status = field_status[x][y];
		
		if(status == STATUS_SHOWN)
			return;
		else if(status == STATUS_HIDDEN) {
			if(value >= CLEAR) {
				field_status[x][y] = STATUS_SHOWN;
				
				if(value == CLEAR) {
					// Unten links
					if(x > 0 && y > 0 && field_value[x - 1][y - 1] != MINE)
						clearArea(x - 1, y - 1);
					// Unten rechts
					if(x < sizex - 1 && y > 0 && field_value[x + 1][y - 1] != MINE)
						clearArea(x + 1, y - 1);
					// Oben rechts
					if(x < sizex - 1 && y < sizey - 1 && field_value[x + 1][y + 1] != MINE)
						clearArea(x + 1, y + 1);
					// Oben links
					if(x > 0 && y < sizey - 1 && field_value[x - 1][y + 1] != MINE)
						clearArea(x - 1, y + 1);
					// Oben
					if(y < sizey - 1 && field_value[x][y + 1] != MINE)
						clearArea(x, y + 1);
					// Rechts
					if(x < sizex - 1 && field_value[x + 1][y] != MINE)
						clearArea(x + 1, y);
					// Unten
					if(y > 0 && field_value[x][y - 1] != MINE)
						clearArea(x, y - 1);
					// Links
					if(x > 0 && field_value[x - 1][y] != MINE)
						clearArea(x - 1, y);
				}
			}
		}
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		int fieldx, fieldy;
		
		screenY = height - screenY;
		
		if(button == Buttons.LEFT) {
			// Aufdecken
			
			fieldx = screenX / TILESIZE;
			fieldy = screenY / TILESIZE;
			
			if(fieldx > sizex || fieldy > sizey || fieldx < 0 || fieldy < 0)
				return false;
			
			if(field_status[fieldx][fieldy] != STATUS_FLAGGED) {
				if(field_value[fieldx][fieldy] == MINE) {
					setup();
					return true;
				}
				
				if(field_value[fieldx][fieldy] == CLEAR)
					clearArea(fieldx, fieldy);
				else
					field_status[fieldx][fieldy] = STATUS_SHOWN;
				
				boolean win = true;
				
				for(int x = 0; x < sizex; x++) {
					for(int y = 0; y < sizey; y++) {
						if(field_value[x][y] != MINE && field_status[x][y] != STATUS_SHOWN) {
							win = false;
							break;
						}
					}
				}
				
				if(!started) {
					System.out.println("timereset");
					started = true;
					starttime = System.currentTimeMillis();
				}
				
				if(win && started) {
					long time = System.currentTimeMillis() - starttime;
					
					time /= 1000;
					
					System.out.println("Gewonnen! Benötigte Zeit: " + time + "s");
					setup();
				}
				
				return true;
			}
		} else if(button == Buttons.RIGHT) {
			// Markieren
			
			fieldx = screenX / TILESIZE;
			fieldy = screenY / TILESIZE;
			
			if(fieldx > sizex || fieldy > sizey || fieldx < 0 || fieldy < 0)
				return false;
			
			int status = field_status[fieldx][fieldy];
			
			field_status[fieldx][fieldy] = status == STATUS_FLAGGED ? STATUS_HIDDEN : status == STATUS_HIDDEN ? STATUS_FLAGGED : status;
			return true;
		}
		
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
