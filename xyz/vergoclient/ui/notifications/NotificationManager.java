package xyz.vergoclient.ui.notifications;

import xyz.vergoclient.util.ChatUtils;

import java.util.concurrent.LinkedBlockingQueue;

public class NotificationManager {
    public static LinkedBlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue<>();
    private static Notification currentNotification = null;

    public static void show(Notification notification) {
        pendingNotifications.add(notification);
    }

    public static void update() {
        if (currentNotification != null && !currentNotification.isShown()) {
            currentNotification = null;
        }

        if (currentNotification == null && !pendingNotifications.isEmpty() && pendingNotifications.size() < 5) {
            currentNotification = pendingNotifications.poll();
            currentNotification.show();
        } else if(pendingNotifications.size() > 1) {
            ChatUtils.addChatMessage("Notifications have been cleared to reduce spam.");
            pendingNotifications.clear();
        }

    }

    public static void render() {
        update();

        if (currentNotification != null)
            currentNotification.render();
    }
}