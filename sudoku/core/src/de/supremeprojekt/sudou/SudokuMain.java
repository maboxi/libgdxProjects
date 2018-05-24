package de.supremeprojekt.sudou;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class SudokuMain extends ApplicationAdapter implements InputProcessor{
	public static final Random RANDOM = new Random();
	
	SpriteBatch batch;
	ShapeRenderer sr;
	BitmapFont font;
	GlyphLayout layout;
	SetCheck set;
	
	public static final int CLEAR = -1;
	public static final int NO_HIGHLIGHT = -2;
	public static final int NO_SELECTION = -1;
	
	public int width, height;

	int[][] matrix;
	
	int highlight;
	
	int selX, selY;

	public SudokuMain(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	@Override
	public void create() {
		highlight = NO_HIGHLIGHT;
		
		selX = NO_SELECTION;
		selY = NO_SELECTION;
		
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		layout = new GlyphLayout();
		font = new BitmapFont();

		set = new SetCheck();

		font.getData().scale(0.5f);

		matrix = new int[9][9];

		setup();
		scramble();
		
		prepareField();
		
		Gdx.input.setInputProcessor(this);
		
		System.out.println(check());
	}
	
	public void prepareField() {
		int toDelete = 30;
		int deleted = 0;
		
		int x;
		int y;
		
		while(deleted < toDelete) {
			x = RANDOM.nextInt(matrix.length);
			y = RANDOM.nextInt(matrix.length);
		
			if(matrix[x][y] != CLEAR) {
				matrix[x][y] = CLEAR;
				deleted++;
			}
		}
	}

	public void setup() {
		String filled1 = 	"825 471 396" + 
							"194 326 578" + 
							"376 985 241" + 
							"519 743 862" + 
							"632 598 417" + 
							"487 612 935" + 
							"263 159 784" + 
							"948 267 153" + 
							"751 834 629";
		
		filled1 = filled1.replaceAll(" ", "");
		char[] filled1Chars = filled1.toCharArray();

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = Integer.parseInt("" + filled1Chars[(matrix.length - j - 1) * matrix.length + i]);
			}
		}
	}

	public boolean check() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (!checkBox(i, j))
					return false;

		for(int i = 0; i < matrix.length; i++) {
			if(!checkRow(i))
				return false;
			if(!checkCol(i))
				return false;
		}
		
		return true;
	}

	public boolean checkBox(int x, int y) {
		set.reset();
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				set.add(matrix[x * 3 + i][y * 3 + j]);
			}
		}
		
		return set.check();
	}

	public boolean checkRow(int r) {
		set.reset();

		for (int i = 0; i < matrix.length; i++)
			set.add(matrix[i][r]);

		return set.check();
	}

	public boolean checkCol(int c) {
		set.reset();

		for (int i = 0; i < matrix.length; i++)
			set.add(matrix[c][i]);

		return set.check();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		sr.begin(ShapeType.Filled);

		int rects = 9;

		for (int i = 0; i < rects; i++) {
			for (int j = 0; j < rects; j++) {
				if (mod(i / 3 + j / 3, 2) == 0)
					sr.setColor(200f / 255f, 200f / 255f, 200f / 255f, 1);
				else
					sr.setColor(230f / 255f, 230f / 255f, 230f / 255f, 1);
				
				if(i == selX && j == selY)
					sr.setColor(0, 1, 0, 1);
				
				sr.rect(i * width / rects, j * height / rects, width / rects, height / rects);
			}
		}

		sr.end();
		sr.begin(ShapeType.Line);
		sr.setColor(0, 0, 0, 1);

		sr.rect(1, 0, width, height - 1);

		for (int i = 0; i < rects; i++)
			for (int j = 0; j < rects; j++)
				sr.rect(i * width / rects, j * height / rects, width / rects, height / rects);

		sr.end();

		batch.begin();
		font.setColor(0, 0, 1, 1);

		String text;
		int value;
		
		for (int i = 0; i < rects; i++) {
			for (int j = 0; j < rects; j++) {
				value = matrix[i][j];
				
				if(value == CLEAR)
					continue;
				
				if(highlight != NO_HIGHLIGHT && value == highlight) 
					font.setColor(0, 1, 0, 1);
				
				text = "" + value;

				layout.setText(font, text);

				font.draw(batch, text, i * width / rects + width / (rects * 2) - layout.width / 2,
						j * height / rects + height / (rects * 2) + layout.height / 2);
				
				if(highlight != NO_HIGHLIGHT && value == highlight) 
					font.setColor(0, 0, 1, 1);
			}
		}

		batch.end();
	}
	
	public void scramble() {
		int scrInsg = RANDOM.nextInt(10);
		int scrEach = RANDOM.nextInt(10);
		
		int[] copy = new int[matrix.length];
		
		int scrIndex1;
		int scrIndex2;
		
		for(int n = 0; n < scrInsg; n++) {
			for(int k = 0; k < 3; k++) {
				for(int i = 0; i < scrEach; i++) {
					scrIndex1 = RANDOM.nextInt(matrix.length / 3) + k * 3;
					scrIndex2 = RANDOM.nextInt(matrix.length / 3) + k * 3;
					
					for(int j = 0; j < matrix.length; j++)
						copy[j] = matrix[j][scrIndex1];
					
					for(int j = 0; j < matrix.length; j++)
						matrix[j][scrIndex1] = matrix[j][scrIndex2];
					
					for(int j = 0; j < matrix.length; j++)
						matrix[j][scrIndex2] = copy[j];
				}
				
				for(int i = 0; i < scrEach; i++) {
					scrIndex1 = RANDOM.nextInt(matrix.length / 3) + k * 3;
					scrIndex2 = RANDOM.nextInt(matrix.length / 3) + k * 3;
					
					for(int j = 0; j < matrix.length; j++)
						copy[j] = matrix[scrIndex1][j];
					
					for(int j = 0; j < matrix.length; j++)
						matrix[scrIndex1][j] = matrix[scrIndex2][j];
					
					for(int j = 0; j < matrix.length; j++)
						matrix[scrIndex2][j] = copy[j];
				}
			}
		}
		
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Buttons.LEFT) {
			
			int gridX = matrix.length * screenX / width;
			int gridY = matrix.length - 1 - (matrix.length * screenY / height);
			
			if(gridY < 0 || gridY >= matrix.length || gridX < 0 || gridY >= matrix.length)
				return false;
			
			if(gridY == selY && gridX == selX) {
				selY = NO_SELECTION;
				selX = selY;
			} else {
				selX = gridX;
				selY = gridY;
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	private int mod(int i, int j) {
		while (i >= j)
			i = i - j;
		return i;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.SPACE) {
			System.out.println(check());
			return true;
		} else if(keycode == Keys.ENTER) {
			setup();
			scramble();
			prepareField();
			return true;
		}
		
		return false;
	}
	@Override
	public boolean keyTyped(char c) {
		if(selX == NO_SELECTION) {
			switch(c)
			{
			case '1':
				if(highlight == 1)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 1;
				break;
			case '2':
				if(highlight == 2)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 2;
				break;
			case '3':
				if(highlight == 3)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 3;
				break;
			case '4':
				if(highlight == 4)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 4;
				break;
			case '5':
				if(highlight == 5)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 5;
				break;
			case '6':
				if(highlight == 6)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 6;
				break;
			case '7':
				if(highlight == 7)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 7;
				break;
			case '8':
				if(highlight == 8)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 8;
				break;
			case '9':
				if(highlight == 9)
					highlight = NO_HIGHLIGHT;
				else
					highlight = 9;
				break;
			
			default:
				break;
			}
		} else {
			if(in(c, "123456789")) {
				int val = Integer.parseInt("" + c);
				
				if(matrix[selX][selY] == val)
					matrix[selX][selY] = CLEAR;
				else
					matrix[selX][selY] = val;
			}
		}
		
		return false;
	}
	
	public boolean in(char c, String chars) {
		char[] cs = chars.toCharArray();
		
		for(char c1 : cs)
			if(c == c1)
				return true;
		
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
