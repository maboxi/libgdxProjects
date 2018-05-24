package de.supremeprojekt.minesweeper.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.supremeprojekt.minesweeper.Mainsweeper;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 720;
		config.height = 720;
		config.resizable = false;
		config.title = "Minesweeper";
		new LwjglApplication(new Mainsweeper(config.width, config.height), config);
	}
}
