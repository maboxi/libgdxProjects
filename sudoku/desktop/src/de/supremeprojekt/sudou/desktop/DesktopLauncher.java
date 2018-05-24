package de.supremeprojekt.sudou.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.supremeprojekt.sudou.SudokuMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 3 * 150;
		config.height = config.width;
		config.title = "Sudoku";
		config.resizable = false;
		
		new LwjglApplication(new SudokuMain(config.width, config.height), config);
	}
}
