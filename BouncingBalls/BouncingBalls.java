import acm.graphics.GObject;
import acm.graphics.GOval;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class BouncingBalls extends WindowProgram {
    /* The default width and height of the window. */
    public static final int APPLICATION_WIDTH = 500;
    public static final int APPLICATION_HEIGHT = 400;

    public static int minBallDiameter = 15;
    public static int maxBallDiameter = 150;

    public static int minBallColor = 230;
    public static int maxBallColor = 0;

    /* new GOval object */
    public static GOval ball;

    /* new radius for the ball */
    int newBallDiameter;

    /* value to manipulate with new size of the ball */
    public static int valueToCalculateNewDiameter = 350;

    /* value to manipulate with new color of the ball */
    public static int valueToCalculateNewColor = 350;

    /* new color for the ball */
    int newColor;

    /* the time when mouse was pressed */
    double startTime;

    /* Amount of time to pause between frames. */
    private static final double PAUSE_TIME = 1000.0 / 48;

    private static final double GRAVITY = 0.425;

    private static final double ELASTICITY = 0.75;

    private static final double BASIC_SPEED = 0.0;

    private boolean needToCreateBall = true;

    /* ball collection program has */
    ArrayList<GOval> ballCollection = new ArrayList<>();

    /* speed collection the ball collection has */
    ArrayList<Double> ballsSpeedCollection = new ArrayList<>();

    /* gravity collection the ball collection has */
    ArrayList<Boolean> gravityDirection = new ArrayList<>();

    public void run() {
        addMouseListeners();
        animateBalls();
    }

    public final void mousePressed(final MouseEvent e) {
        GObject object = getElementAt(e.getX(), e.getY());

        if (object == null) {
            /* program will create new ball */
            needToCreateBall = true;

            startTime = System.currentTimeMillis();
            return;
        }

        /* program won't create new ball */
        needToCreateBall = false;

        /* find the index of selected ball */
        int index = ballCollection.indexOf(object);
        gravityDirection.set(index, !gravityDirection.get(index));
    }

    public final void mouseReleased(final MouseEvent e) {
        /* need program create new ball? */
        if (!needToCreateBall)
            return;

        double endTime = System.currentTimeMillis();

        double holdTime = (endTime - startTime) / 1000;

        // every second that we held will be plus 10 pixels to ball radius
        defineSizeAndColorForNewBall(holdTime);

        /* x and y for the ball*/
        int ballX = e.getX() - newBallDiameter / 2;
        int ballY = e.getY() - newBallDiameter / 2;

        /* add new ball to canvas */
        add(ball = createNewBall(ballX, ballY));

        /* add ball to array */
        ballCollection.add(ball);

        /* basic speed of current ball */
        ballsSpeedCollection.add(BASIC_SPEED);

        /* add gravity value (false = up - down direction) */
        gravityDirection.add(false);
    }

    /**
     * Program makes the balls move up and down on the canvas using a ball collection
     * arraylist, speed of those balls and gravity params of them.
     */
    private void animateBalls() {
        while (true) {
            for (int i = 0; i < ballCollection.size(); i++) {
                double changedGravity = GRAVITY;

                /* check if the ball has inverted gravity */
                if (gravityDirection.get(i))
                    changedGravity = GRAVITY * -1;

                /* ball to work with */
                GOval currentBall = ballCollection.get(i);

                /* current speed of the ball */
                double currentSpeedOfTheBAll = ballsSpeedCollection.get(i);

                /* move the ball */
                currentBall.move(0, currentSpeedOfTheBAll);

                /* increase the speed of the ball */
                currentSpeedOfTheBAll += changedGravity;

                /* new speed for current ball */
                ballsSpeedCollection.set(i, currentSpeedOfTheBAll);

                /* make the ball bounce from the floor and ceiling */
                if ((isBallBelowTheFloor(currentBall) && currentSpeedOfTheBAll > 0) ||
                        (isBallAboveTheCeiling(currentBall) && currentSpeedOfTheBAll < 0))
                    ballsSpeedCollection.set(i, currentSpeedOfTheBAll * -ELASTICITY);
            }

            pause(PAUSE_TIME);
        }
    }

    /**
     * Check if the ball above the ceiling
     *
     * @param currentBall - current ball
     */
    private boolean isBallAboveTheCeiling(GOval currentBall) {
        return currentBall.getY() <= 0;
    }

    /**
     * Check if the ball below the floor
     *
     * @param currentBall - current ball
     */
    private boolean isBallBelowTheFloor(GOval currentBall) {
        return currentBall.getY() + currentBall.getHeight() >= getHeight();
    }

    /**
     * Defines new diameter and new color for the ball
     *
     * @param holdTime - time mouse was pressed
     */
    private void defineSizeAndColorForNewBall(double holdTime) {
        newBallDiameter = (int) (holdTime / 10 * valueToCalculateNewDiameter + minBallDiameter);

        if (newBallDiameter > maxBallDiameter)
            newBallDiameter = maxBallDiameter;

        newColor = minBallColor - (int) (holdTime / 10 * valueToCalculateNewColor + minBallDiameter);
        if (newColor < maxBallColor)
            newColor = maxBallColor;
    }

    /**
     * Creates new ball using x and y coordinates
     *
     * @param x - value of X-axis
     * @param y - value of Y-axis
     */
    public GOval createNewBall(int x, int y) {
        Color color = new Color(newColor, newColor, newColor);
        GOval newBall = new GOval(x, y, newBallDiameter, newBallDiameter);
        newBall.setFilled(true);
        newBall.setColor(color);
        return newBall;
    }
}
