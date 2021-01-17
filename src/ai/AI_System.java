package ai;

import system.Game;
import system.SoundEffect;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;

public class AI_System {
    private static LinkedList<Game> game_instances = new LinkedList<>();

    public static void main(String[] args) {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("Tetris.ttf")));
            // Tetris
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        SoundEffect.init();

        while(true) {

            game_instances.add(new Game(0, 100, 0));
            game_instances.add(new Game(600, 100, 1));
            game_instances.add(new Game(1200, 100, 2));
            game_instances.add(new Game(1800, 100, 2));

            while (gameIsRunning()) {

            }
            game_instances.clear();
        }
    }

    private static boolean gameIsRunning() {
        for(Game game : game_instances) {
            if(game.playing()) {
                return true;
            }
        }
        return false;
    }
}
