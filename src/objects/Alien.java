package objects;

import game.GameLevel;
import geometry.Point;
import geometry.Velocity;

import java.awt.Color;

/**
 * This class defines the Alien - Block which can move and shot.
 * @version 1.0 28 June 2018
 * @author Avi miletzky
 */
public class Alien {

    private Block block;
    private Point resetInitialPos;
    private GameLevel game;

    /**
     * This method used as a constructor. and creates new Alien by Block(b) in GameLevel(gl).
     * @param b - the given block.
     * @param gl - the game that is inside.
     */
    public Alien(Block b, GameLevel gl) {
        this.block = b;
        this.resetInitialPos = b.getCollisionRectangle().getUpperLeft();
        this.game = gl;
    }

    /**
     * This method returns the alien to its first position.
     */
    public void resetPosition() {
        this.block.getCollisionRectangle().setUpperLeft(this.resetInitialPos);
    }

    /**
     * This method moves the alien horizontally (on the X axis).
     * @param dx - size of movement;
     */
    public void moveHorizontally(double dx) {
        this.block.getCollisionRectangle().setUpperLeft(new Point(
                this.block.getCollisionRectangle().getUpperLeft().getX() + dx,
                        this.block.getCollisionRectangle().getUpperLeft().getY()));
    }

    /**
     * This method moves the alien vertically (on the Y axis)..
     * @param dy - size of movement;
     */
    public void moveVertically(double dy) {
        this.block.getCollisionRectangle().setUpperLeft(new Point(
                this.block.getCollisionRectangle().getUpperLeft().getX(),
                        this.block.getCollisionRectangle().getUpperLeft().getY() + dy));
    }

    /**
     * This method returns the Block of this alien.
     * @return this.block;
     */
    public Block getBlock() {
        return this.block;
    }

    /**
     * This method informs if life remains alien.
     * @return this.block;
     */
    public boolean isLive() {
        if (this.block.getNumOfHits() == 0) {
            return false;
        }
        return true;
    }

    /**
     * This method creates a bullet and shoots it from the center of the alien.
     */
    public void shootBullet() {
        Point p = this.block.getCollisionRectangle().getSidesArr()[1].middle();
        Point newPoint = new Point(p.getX(), p.getY() + 1);
        Ball ball = new Ball(newPoint, 5, Color.red);
        ball.setVelocity(Velocity.fromAngleAndSpeed(180, 500));
        ball.setGameEnvironment(game.getAliensEnvironment());
        ball.addToGame(this.game);
    }
}