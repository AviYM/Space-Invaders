//package game;

import levels.LevelSpecificationReader;
import table.HighScoresAnimation;
import table.HighScoresTable;
import biuoop.DialogManager;
import biuoop.GUI;
import biuoop.KeyboardSensor;
import collection.KeyPressStoppableAnimation;
import game.AnimationRunner;
import game.GameFlow;
import game.GameLevel;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.File;
import java.io.Reader;
import java.io.IOException;
import java.util.List;
import levels.LevelInformation;
import menu.Task;
import menu.Menu;
import menu.MenuAnimation;
import menu.RunGameTask;
import menu.ShowHiScoresTask;

/**
 * This class runs all the game and include the main method.
 * @version 1.0 23 Mars 2018
 * @author Avi miletzky
 */
public class Ass7Game {

    /**
     * This method runs all the game.
     * if there is no argument the program runs the game from default file.
     * if there is path-argument, the program will run the game from received file.
     * @param args - is empty.
     */
    public static void main(String[] args) {
        GUI gui = new GUI("Space Invaders", GameLevel.WINDOW_WIDTH, GameLevel.WINDOW_HEIGHT);
        DialogManager dialog = gui.getDialogManager();
        AnimationRunner ar = new AnimationRunner(gui);
        KeyboardSensor kb = gui.getKeyboardSensor();
        File highScores = new File("src/highscores.txt");
        HighScoresTable table = new HighScoresTable(5);
        try {
            if (!highScores.exists()) {
                highScores.createNewFile();
                table.save(highScores);
            } else {
                table = HighScoresTable.loadFromFile(highScores);
            }
        } catch (IOException e) {
            System.out.println("the create file is filed");
        }
        String path;
        if (args.length == 0) {
            path = "definitions/alien_level_definitions.txt";
        } else {
            path = args[0];
        }
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        Reader readerLevels = new InputStreamReader(is);
        LevelSpecificationReader lsr = new LevelSpecificationReader();
        List<LevelInformation> levels = lsr.fromReader(readerLevels);
        GameFlow gf = new GameFlow(ar, kb, highScores, table, dialog, 3);
        while (true) {
            Menu<Task<Void>> menu = new MenuAnimation<>("Space Invaders", kb);
            menu.addSelection("s", "Start Game", new RunGameTask(gf, levels));
            menu.addSelection("h", "High Scores", new ShowHiScoresTask(ar,
                    new KeyPressStoppableAnimation(kb, KeyboardSensor.SPACE_KEY,
                            new HighScoresAnimation(table))));
            menu.addSelection("e", "Exit", new Task<Void>() {
                @Override
                public Void run() {
                    System.exit(0);
                    return null;
                }
            });
            ar.run(menu);
            // wait for user selection
            Task<Void> task = menu.getStatus();
            task.run();
        }
    }
}