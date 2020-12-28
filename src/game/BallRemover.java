package game;

import collection.HitListener;
import objects.Ball;
import objects.Block;

/**
 * The class BallRemover is in charge of removing balls from the game, as well as keeping count
 * of the number of balls that remain.
 * @version 1.0 17 Mars 2018
 * @author Avi miletzky
 */
public class BallRemover implements HitListener {

    private GameLevel game;

    /**
     * This method uses as a constructor.
     * @param game - required in order to remove balls from specific game.
     */
    public BallRemover(GameLevel game) {
        this.game = game;
    }

    /**
     * This method removes blocks from the game that are hit and reach 0 hit-points.
     * @param beingHit - the given block to remove
     * @param hitter - the given hitter ball
     */
    public void hitEvent(Block beingHit, Ball hitter) {
        hitter.removeFromGame(this.game);
    }
}
