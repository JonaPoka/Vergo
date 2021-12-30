package xyz.vergoclient.util.animations;

import xyz.vergoclient.util.AnimationUtils;
import xyz.vergoclient.util.Animation;

public class SmoothAnimation extends Animation {

    public SmoothAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothAnimation(int ms, double endPoint, Enum<AnimationUtils.Direction> direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        double x1 = x / (double) duration;
        return -2 * Math.pow(x1, 3) + (3 * Math.pow(x1, 2));
    }

}
