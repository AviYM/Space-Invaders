package objects;

import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import collection.Collidable;
import collection.Sprite;
import game.GameLevel;
import geometry.Point;
import geometry.Rectangle;
import geometry.Velocity;
import java.awt.Color;

/**
 * This class defines the paddle as a rectangle with color that is controlled by the arrow keys,
 * and moves according to the player key presses.
 * @version 1.0 18 April 2018
 * @author Avi miletzky
 */
public class Paddle implements Sprite, Collidable {

    private Rectangle rect;
    private Color color;
    private Velocity velocity;
    private GameLevel game;
    private double speed;
    private double dt;
    private long lastTimeIsShot;
    private boolean wasHit;

    /**
     * This method uses as a constructor of paddle.
     * @param r - The given Rectangle-shape of paddle.
     * @param game - The given game - needed in order to control on paddle movement, by keyboard' arrows.
     * @param c - The given Color of paddle.
     * @param speed - the given speed of the paddle
     */
    public Paddle(Rectangle r, GameLevel game, Color c, double speed) {
        this.rect = r;
        this.color = c;
        this.game = game;
        this.speed = speed;
        this.lastTimeIsShot = 0;
        this.wasHit = false;
    }

    /**
     * This method defines the movement velocity of the paddle, by received velocity.
     * In the absence of this setting the paddle will not move in the window.
     * @param v - The given velocity.
     */
    public void setVelocity(Velocity v) {
        this.velocity = v;
    }

    /**
     * This method moves the paddle one step to the left side.
     */
    public void moveLeft() {
        if (this.getCollisionRectangle().getUpperLeft().getX() > GameLevel.BORDER_BLOCK_WIDTH) {
            this.setVelocity(new Velocity(-(this.speed * this.dt), 0));
            Point newLocation = this.velocity.applyToPoint(this.getCollisionRectangle().getUpperLeft());
            this.getCollisionRectangle().setUpperLeft(newLocation);
        } else {
            this.getCollisionRectangle().setUpperLeft(new Point(
                    0 + GameLevel.BORDER_BLOCK_WIDTH,
                    this.getCollisionRectangle().getUpperLeft().getY()));
        }
    }

    /**
     * This method moves the paddle one step to the right side.
     */
    public void moveRight() {
        if ((this.getCollisionRectangle().getUpperLeft().getX()
                + this.getCollisionRectangle().getWidth())
                < GameLevel.WINDOW_WIDTH - GameLevel.BORDER_BLOCK_WIDTH) {
            this.setVelocity(new Velocity(this.speed * this.dt, 0));
            Point newLocation = this.velocity.applyToPoint(this.getCollisionRectangle().getUpperLeft());
            this.getCollisionRectangle().setUpperLeft(newLocation);
        } else {
            this.getCollisionRectangle().setUpperLeft(new Point(
                    GameLevel.WINDOW_WIDTH - GameLevel.BORDER_BLOCK_WIDTH
                            - this.getCollisionRectangle().getWidth(),
                    this.getCollisionRectangle().getUpperLeft().getY()));
        }
    }

    /**
     * This method moves the paddle each time to the right or left according to the pressed key.
     * @param dt1 -
     */
    public void timePassed(double dt1) {
        this.dt = dt1;
        if (this.game.getKeyboard().isPressed(KeyboardSensor.LEFT_KEY)) {
            moveLeft();
        } else if (this.game.getKeyboard().isPressed(KeyboardSensor.RIGHT_KEY)) {
            moveRight();
        }
        if (this.game.getKeyboard().isPressed(KeyboardSensor.SPACE_KEY)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastTimeIsShot >= 350) {
                shootBullet();
                this.lastTimeIsShot = System.currentTimeMillis();
            }
        }
    }

    /**
     * This method creates a bullet and shoots it from the center of the paddle.
     */
    public void shootBullet() {
        double x = this.rect.getUpperLeft().getX() + this.rect.getWidth() / 2;
        double y = this.rect.getUpperLeft().getY() - 1;
        Ball ball = new Ball(x, y, 4, Color.WHITE);
        ball.setVelocity(Velocity.fromAngleAndSpeed(0, 500));
        ball.setGameEnvironment(game.getEnvironment());
        ball.addToGame(this.game);
    }

    /**
     * This method draws the paddle on the window by DrawSurface.
     * @param d - the given DrawSurface.
     */
    public void drawOn(DrawSurface d) {
        d.setColor(this.color);
        d.fillRectangle((int) this.rect.getUpperLeft().getX(), (int) this.rect.getUpperLeft().getY(),
                (int) this.rect.getWidth(), (int) this.rect.getHeight() - 4);
    }

    /**
     * This method returns the RECT of paddle.
     * @return the "collision shape" of the object.
     */
    public Rectangle getCollisionRectangle() {
        return this.rect;
    }

    /**
     * This method notify the object that we collided with it.
     * @param hitter - a given hitter
     */
    public void hit(Ball hitter) {
        this.wasHit = true;
    }

    /**
     * This method informs if the paddle is hit by an alien bullet.
     * @return this.wasHit;
     */
    public boolean isWasHit() {
        return this.wasHit;
    }

    /**
     * This method resets the boolean variable which informs if the paddle is hit by an alien bullet.
     */
    public void resetWasHit() {
        this.wasHit = false;
    }

    /**
     * This method adds THIS paddle to the game.
     * @param g - the given Game.
     */
    public void addToGame(GameLevel g) {
        g.addSprite(this);
        g.addCollidable(this);
    }
}
