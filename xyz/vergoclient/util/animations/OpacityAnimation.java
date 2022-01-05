package xyz.vergoclient.util.animations;

public class OpacityAnimation {

    private float opacity;
    private long lastMS;

    public OpacityAnimation(int opacity) {
        this.opacity = opacity;
        lastMS = System.currentTimeMillis();
    }

    public void interpolate(int targetOpacity) {
        opacity = (int) AnimationUtil.calculateCompensation(targetOpacity, opacity, 16, 5);
    }

    public void interp(float targetOpacity, float speed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - lastMS;
        lastMS = currentMS;
        opacity = (AnimationUtil.calculateCompensation(targetOpacity, opacity, delta, speed));
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

}
