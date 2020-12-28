package game;

import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import collection.Animation;
import collection.SpriteCollection;
import collection.GameEnvironment;
import collection.Collidable;
import collection.Sprite;
import collection.KeyPressStoppableAnimation;
import geometry.Point;
import geometry.Rectangle;
import levels.LevelInformation;
import objects.Block;
import objects.AliensCommander;
import objects.Counter;
import objects.Paddle;
import objects.Background;
import objects.BackgroundColor;
import options.PauseScreen;

import java.util.HashMap;
import java.util.List;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class creates and holds the sprites and collidables,
 * and initializes the GameLevel, and runs the animation.
 * @version 1.0 17 April 2018
 * @author Avi miletzky
 */
public class GameLevel implements Animation {

    private AnimationRunner runner;
    private boolean running;
    private SpriteCollection sprites;
    private List<Sprite> spritesToRemove;
    private GameEnvironment environment;
    private GameEnvironment aliensEnvironment;
    private KeyboardSensor keyboard;
    private LevelInformation level;
    private Counter score;
    private Counter numOfLives;
    private Counter blocksCounter;
    private BlockRemover blockRemover;
    private BallRemover ballRemover;
    private ScoreTrackingListener scoreTL;
    private Paddle paddle;
    private AliensCommander aliensCommander;

    public static final int HEADER_HEIGHT = 20;
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int BORDER_BLOCK_WIDTH = 5;
    public static final int HEIGHT_OF_PADDLE = 12;

    /**
     * This method uses as a constructor.
     * @param level - the current level, in order to runs it.
     * @param kb - required in order to support the pause of the game, by pressed "p" key.
     * @param ar - the AnimationRunner that actually running the game loop.
     * @param score - the current score - which has so far accumulated in the game.
     * @param nol - the number of lives left for the game.
     */
    public GameLevel(LevelInformation level, KeyboardSensor kb, AnimationRunner ar, Counter score,
                     Counter nol) {
        this.level = level;
        this.keyboard = kb;
        this.runner = ar;
        this.score = score;
        this.numOfLives = nol;
        this.environment = new GameEnvironment();
        this.aliensEnvironment = new GameEnvironment();
        this.sprites = new SpriteCollection();
        this.spritesToRemove = new ArrayList<>();
        this.scoreTL = new ScoreTrackingListener(score);
        this.blocksCounter = new Counter(0);
        this.blockRemover = new BlockRemover(this, this.blocksCounter);
        this.ballRemover = new BallRemover(this);
    }

    /**
     * This method adds Collidable(c) to the ENVIRONMENT list.
     * @param c - the given Collidable.
     */
    public void addCollidable(Collidable c) {
        this.environment.addCollidable(c);
    }

    /**
     * This method adds Collidable(c) to the ENVIRONMENT list.
     * @param c - the given Collidable.
     */
    public void addCollidableToAliensEnvironment(Collidable c) {
        this.aliensEnvironment.addCollidable(c);
    }

    /**
     * This method adds Sprite(s) to the SPRITES list.
     * @param s - the given Sprite.
     */
    public void addSprite(Sprite s) {
        this.sprites.addSprite(s);
    }

    /**
     * This method adds Sprite(s) to the spritesToRemove list.
     * @param s - the given Sprite.
     */
    public void addSpriteToRemove(Sprite s) {
        this.spritesToRemove.add(s);
    }

    /**
     * This method returns the blocksCounter of the GameLevel.
     * @return this.blocksCounter;
     */
    public Counter getBlocksCounter() {
        return this.blocksCounter;
    }

    /**
     * This method returns the KeyboardSensor of the GameLevel.
     * @return this.keyboard;
     */
    public KeyboardSensor getKeyboard() {
        return this.keyboard;
    }

    /**
     * This method returns the GameEnvironment of the GameLevel.
     * @return this.environment;
     */
    public GameEnvironment getEnvironment() {
        return this.environment;
    }

    /**
     * This method returns the aliensEnvironment of the GameLevel.
     * @return this.aliensEnvironment;
     */
    public GameEnvironment getAliensEnvironment() {
        return this.aliensEnvironment;
    }

    /**
     * This method removes the given Collidable from the ENVIRONMENT list of the game.
     * @param c - the given collidable.
     */
    public void removeCollidable(Collidable c) {
        this.environment.removeCollidable(c);
    }

    /**
     * This method removes the given Collidable from the ENVIRONMENT list of the game.
     * @param c - the given collidable.
     */
    public void removeCollidableFromAliensEnvironment(Collidable c) {
        this.aliensEnvironment.removeCollidable(c);
    }

    /**
     * This method removes the given Sprite from the SPRITES list of the game.
     * @param s - the given Sprite.
     */
    public void removeSprite(Sprite s) {
        this.sprites.removeSprite(s);
    }

    /**
     * This method initializes and creates all objects in the game: aliens, blocks and paddle.
     * @param speed - speed of movement of aliens.
     * @param numOfBattle - number the level.
     */
    public void initialize(double speed, int numOfBattle) {
        // adds the background of level to Sprite Collection.
        this.sprites.addSprite(this.level.getBackground());
        //creates the paddle.
        this.paddle = new Paddle(new Rectangle(new Point((WINDOW_WIDTH - this.level.paddleWidth()) / 2 + 1,
                WINDOW_HEIGHT - HEIGHT_OF_PADDLE), this.level.paddleWidth(), HEIGHT_OF_PADDLE),
                this, Color.YELLOW, this.level.paddleSpeed());
        this.paddle.addToGame(this);
        addCollidableToAliensEnvironment(this.paddle);
        // creates aliens.
        this.blocksCounter.increase(level.numberOfBlocksToRemove());
        createsAliens(speed);
        // creates shield blocks.
        createsShieldBlocks(this.ballRemover);
        // Creates blocks that serve as boundaries.
        Block left = new Block(new Rectangle(new Point(0, HEADER_HEIGHT),
                BORDER_BLOCK_WIDTH, WINDOW_HEIGHT - BORDER_BLOCK_WIDTH));
        left.setBackground(new BackgroundColor(Color.GRAY));
        left.addToGame(this);
        Block bottom = new Block(new Rectangle(new Point(0, WINDOW_HEIGHT),
                WINDOW_WIDTH, BORDER_BLOCK_WIDTH));
        bottom.setBackground(new BackgroundColor(Color.GRAY));
        bottom.addToGame(this);
        bottom.addHitListener(ballRemover);
        Block right = new Block(new Rectangle(new Point(WINDOW_WIDTH - BORDER_BLOCK_WIDTH,
                HEADER_HEIGHT), BORDER_BLOCK_WIDTH,
                WINDOW_HEIGHT - BORDER_BLOCK_WIDTH));
        right.setBackground(new BackgroundColor(Color.GRAY));
        right.addToGame(this);
        Block header = new Block(new Rectangle(new Point(0, 0),
                WINDOW_WIDTH, HEADER_HEIGHT));
        header.setBackground(new BackgroundColor(new Color(0xBEBEBE)));
        header.addToGame(this);
        header.addHitListener(scoreTL);
        header.addHitListener(ballRemover);
        // Creates the indicators which at the header.
        ScoreIndicator sI = new ScoreIndicator(score);
        sI.addToGame(this);
        LivesIndicator lI = new LivesIndicator(numOfLives);
        lI.addToGame(this);
        NameLevelIndicator nLi = new NameLevelIndicator(this.level.levelName() + " " + numOfBattle);
        nLi.addToGame(this);
    }

    /**
     * This method creates aliens which can to move and shot.
     * @param speed - speed of movement.
     */
    private void createsAliens(double speed) {
        List<Block> blocksList = new ArrayList<>();
        // Put the blocks into the game.
        for (Block block : this.level.blocks()) {
            Block b = new Block(block);
            b.addToGame(this);
            b.addHitListener(blockRemover);
            b.addHitListener(ballRemover);
            b.addHitListener(scoreTL);
            blocksList.add(b);
        }
        this.aliensCommander = new AliensCommander(blocksList, this, speed);
        this.sprites.addSprite(aliensCommander);
    }

    /**
     * This method creates blocks which used as shields of the paddle.
     * @param br - BallRemover to remove damaged shields blocks.
     */
    private void createsShieldBlocks(BallRemover br) {
        // shields properties
        Color shieldColor = new Color(0x00cca3);
        int shieldWidth = 5, shieldHeight = 5;
        int shieldsNumber = 3, layersNumber = 3, blocksNumberInRow = 32;
        Point[] startPoints = new Point[] {
                new Point(80, 450), new Point(320, 450), new Point(560, 450)};
        BlockRemover bRemover = new BlockRemover(this, null);

        for (int i = 0; i < shieldsNumber; i++) {
            Point shieldStartPoint = startPoints[i];
            double currY = shieldStartPoint.getY();
            for (int j = 0; j < layersNumber; j++) {
                double currX = shieldStartPoint.getX();
                for (int k = 0; k < blocksNumberInRow; k++) {
                    Block shieldBlock = new Block(new Rectangle(
                            new Point(currX, currY), shieldWidth, shieldHeight));
                    Map<Integer, Background> background = new HashMap<>();
                    background.put(1, new BackgroundColor(shieldColor));
                    shieldBlock.setBackgroundOfEachHit(background);
                    shieldBlock.setNumOfHits(1);
                    shieldBlock.addToGame(this);
                    addCollidableToAliensEnvironment(shieldBlock);
                    shieldBlock.addHitListener(br);
                    shieldBlock.addHitListener(bRemover);
                    currX += shieldWidth;
                }
                currY += shieldHeight;
            }
        }
    }

    /**
     * This method initializes the position of the paddle to the center of the screen.
     */
    private void centerThePaddle() {
        this.paddle.getCollisionRectangle().setUpperLeft(new Point(
                (WINDOW_WIDTH - this.level.paddleWidth()) / 2 + 1,
                WINDOW_HEIGHT - HEIGHT_OF_PADDLE));
    }

    /**
     * This method is used as a stop condition to finish drawing.
     * @return the current condition.
     */
    public boolean shouldStop() {
        return !this.running;
    }

    /**
     * This method draws GameLevel on a given DrawSurface at each time it is called.
     * @param  d - the given DrawSurface.
     * @param  dt -
     */
    public void doOneFrame(DrawSurface d, double dt) {
        this.sprites.drawAllOn(d);
        this.sprites.notifyAllTimePassed(dt);
        if (this.keyboard.isPressed("p")) {
            this.runner.run(new KeyPressStoppableAnimation(keyboard, KeyboardSensor.SPACE_KEY,
                    new PauseScreen()));
        }
        if (this.blocksCounter.getValue() == 0) {
            this.running = false;
        }
        if (this.paddle.isWasHit() || this.aliensCommander.isLoss()) {
            reset();
        }
    }

    /**
     * This method resetting the game after disqualification.
     */
    private void reset() {
        this.running = false;
        this.numOfLives.decrease(1);
        this.paddle.resetWasHit();
        this.aliensCommander.reset();
        for (Sprite sprite : this.spritesToRemove) {
            removeSprite(sprite);
        }
        this.spritesToRemove = new ArrayList<>();
    }

    /**
     * This method initializes the balls and paddle at the center of the screen,
     * then runs the countdown and the current level of the game.
     */
    public void playOneTurn() {
        this.centerThePaddle();
        this.runner.run(new CountdownAnimation(2, 3, this.sprites));
        this.running = true;
        this.runner.run(this);
    }
}