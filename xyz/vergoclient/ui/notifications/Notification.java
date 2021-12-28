package xyz.vergoclient.ui.notifications;


import javafx.animation.Animation;
import xyz.vergoclient.util.AnimationUtils;
import xyz.vergoclient.util.Timer;

public class Notification {

    private final NotificationType type;
    public float y;
    public boolean fading;
    Timer timer = new Timer();

    private float width, height, roundAmount, timeToLast;
    private String title;
    private String message;
    private boolean heightBudge;
    private Animation animation;
    private AnimationUtils.Direction direction;

    public Notification(NotificationType type, String title, String message) {
        // This is the MAIN notification handler. It sets everything that need's to be set.
        this.type = type;
        this.title = title;
        this.message = message;
        width = 120;
        height = 30;
        roundAmount = 3;
        timeToLast = (long) 3000;
        this.timer = new Timer();
    }

    public static void post(NotificationType type, String title, String message) {
        // This will eventually be the main thing to send a notification. This is not written and therefore will be ignored.
        // NotificationManager.whateverFunctionICallIt
    }

    public boolean isHeightBudged() {
        return heightBudge;
    }

    public void setHeightBudge(boolean value) {
        this.heightBudge = value;
    }

    public NotificationType getType() {
        return type;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float newWidth) {
        this.width = newWidth;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float newHeight) {
        this.height = newHeight;
    }

    public float getRoundAmount() {
        return getRoundAmount();
    }

    public void setRoundAmount(float newRoundAmount) {
        this.roundAmount = newRoundAmount;
    }

    public float getTime() {
        return timeToLast;
    }

    public void setTime(float newTime) {
        this.timeToLast = newTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        this.message = newMessage;
    }

    public void startAnimation(Animation animation) {
        this.animation = animation;
    }

    public void stopAnimation() {
        this.animation = null;
    }

    public Animation getAnimation() {
        return animation;
    }

    public AnimationUtils.Direction getDirection() {
        return direction;
    }

    public void setDirection(AnimationUtils.Direction direction) {
        this.direction = direction;
    }

    /*public boolean animationInProgress() {
        return animation != null && !animation.;
    }*/


    public enum NotificationType {
        SUCCESS,
        DISABLE,
        INFO,
        WARNING
    }

}