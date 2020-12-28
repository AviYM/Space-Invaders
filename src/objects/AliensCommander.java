package objects;

import biuoop.DrawSurface;
import collection.Sprite;
import game.GameLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class defines the AliensCommander -
 * The commander is responsible for the direction of the aliens movement,
 * the rules of engagement: who shoots (one of the aliens on the front of battlefield which chosen randomly),
 * and when he fires (half a second after the previous shot).
 * @version 1.0 28 June 2018
 * @author Avi miletzky
 */
public class AliensCommander implements Sprite {

    private List<List<Alien>> aliens;
    private double dx;
    private double currDx;
    private double dy;
    private double currDy;
    private boolean loss;
    private long lastTimeIsShot;
    private GameLevel game;

    /**
     * This method used as a constructor,
     * and creates an AlienCommander who commands all the aliens.
     * @param blocks - List of blocks to create them from aliens.
     * @param gl - The game that aliens are added into.
     * @param speed - Speed of movement of aliens.
     */
    public AliensCommander(List<Block> blocks, GameLevel gl, double speed) {
        this.aliens = new ArrayList<>();
        this.game = gl;
        setAliens(blocks);
        this.dx = speed;
        this.currDx = this.dx;
        this.dy = 600;
        this.currDy = 0;
        this.loss = false;
        this.lastTimeIsShot = 0;
    }

    /**
     * This method goes through the entire list,
     * and each time takes one block and creates an alien from it.
     * @param blocks - the given list of blocks.
     */
    private void setAliens(List<Block> blocks) {
        for (int j = 0; j < 10; j++) {
            List<Alien> column = new ArrayList<>();
            for (int i = 40 + j; i >= 0; i -= 10) {
                column.add(new Alien(blocks.get(i), this.game));
            }
            this.aliens.add(column);
        }
    }

    /**
     * This method removes the aliens who died.
     */
    private void removeTheDead() {
        Alien alien = null;
        List<Alien> column = null;
        for (int j = 0; j < this.aliens.size(); j++) {
            column = this.aliens.get(j);
            for (int i = 0; i < column.size(); i++) {
                alien = column.get(i);
                if (!alien.isLive()) {
                    column.remove(alien);
                }
                if (column.size() == 0) {
                    this.aliens.remove(column);
                }
            }
        }
    }

    /**
     * This method moves the aliens one step.
     * @param dt -
     */
    private void moveOneStep(double dt) {
        for (List<Alien> column : this.aliens) {
            for (Alien alien : column) {
                alien.moveHorizontally(this.currDx * dt);
                alien.moveVertically(this.currDy * dt);
            }
        }
        this.currDy = 0;
    }

    /**
     * This method moves the aliens right and left,
     * every time the aliens collide with the borders they change direction.
     */
    private void horizontalMovement() {
        Alien alien;
        int rightBorder = 795;
        int leftBorder = 5;
        alien = getRightestAlien();
        if (alien.getBlock().getCollisionRectangle().getUpperLeft().getX()
                +  alien.getBlock().getCollisionRectangle().getWidth() >= rightBorder) {
            this.currDx *= -1.1;
            verticalMovement();
        }
        alien = getLeftestAlien();
        if (alien.getBlock().getCollisionRectangle().getUpperLeft().getX() <= leftBorder) {
            this.currDx *= -1.1;
            verticalMovement();
        }
    }

    /**
     * This method moves the aliens down as long as there is no conflict with the shields.
     */
    private void verticalMovement() {
        int bottomBorder = 450;
        Alien alien = getBottomestAlien();
        if ((alien.getBlock().getCollisionRectangle().getUpperLeft().getY()
                + alien.getBlock().getCollisionRectangle().getHeight()) >= bottomBorder) {
            this.loss = true;
        } else {
            this.currDy = this.dy;
        }
    }

    /**
     * This method initial the position of all remaining aliens.
     */
    public void initialAliensPosition() {
        Alien alien = null;
        List<Alien> column = null;
        for (int j = 0; j < this.aliens.size(); j++) {
            column = this.aliens.get(j);
            for (int i = 0; i < column.size(); i++) {
                alien = column.get(i);
                alien.resetPosition();
            }
        }
    }

    /**
     * This method informs if the aliens collided with the shields.
     * @return this.loss;
     */
    public boolean isLoss() {
        return this.loss;
    }

    /**
     * This method resets the position of all remaining aliens, after disqualification.
     */
    public void reset() {
        initialAliensPosition();
        this.currDx = this.dx;
        this.loss = false;
    }

    /**
     * This method returns the Rightest alien.
     * @return the Rightest alien.
     */
    private Alien getRightestAlien() {
        Alien alien = null;
        if (this.aliens.get(this.aliens.size() - 1).get(0) != null) {
            alien = this.aliens.get(this.aliens.size() - 1).get(0);
        }
        return alien;
    }

    /**
     * This method returns the Leftest alien.
     * @return the Leftest alien.
     */
    private Alien getLeftestAlien() {
        Alien alien = null;
        if (this.aliens.get(0).get(0) != null) {
            alien =  this.aliens.get(0).get(0);
        }
        return alien;
    }

    /**
     * This method returns the bottommost alien (the first in front).
     * @return the bottommost alien.
     */
    private Alien getBottomestAlien() {
        double maxY = 0;
        Alien alien = null;
        List<Alien> column = null;
        for (int i = 0; i < this.aliens.size(); i++) {
            column = this.aliens.get(i);
            if (column.get(0).getBlock().getCollisionRectangle().getUpperLeft().getY() > maxY) {
                maxY = column.get(0).getBlock().getCollisionRectangle().getUpperLeft().getY();
                alien = column.get(0);
            }
        }
        return alien;
    }

    /**
     * This method returns list with all aliens on the front of battlefield.
     * @return List of aliens.
     */
    private List<Alien> getTheLowerAliens() {
        List<Alien> lowerAliens = new ArrayList<>();
        List<Alien> column = null;
        for (int j = 0; j < this.aliens.size(); j++) {
            column = this.aliens.get(j);
            lowerAliens.add(column.get(0));
        }
        return lowerAliens;
    }

    /**
     * This method shoot bullet from one of the aliens on the front of battlefield which chosen randomly.
     */
    private void shootRandomBullet() {
        List<Alien> lowerAliens = getTheLowerAliens();
        Random random = new Random();
        Alien alien = lowerAliens.get(random.nextInt(lowerAliens.size()));
        alien.shootBullet();
    }

    @Override
    public void drawOn(DrawSurface d) {
    }

    @Override
    public void timePassed(double dt) {
        removeTheDead();
        horizontalMovement();
        moveOneStep(dt);
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeIsShot >= 500) {
            shootRandomBullet();
            this.lastTimeIsShot = System.currentTimeMillis();
        }
    }
}
