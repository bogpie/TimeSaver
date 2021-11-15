package com.timesaver.timesaver.notification;


import ohos.agp.utils.Color;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.NotificationRequest;
import ohos.event.notification.NotificationSlot;
import ohos.rpc.RemoteException;

public class NotificationManager {

    public enum NotificationType {
        WORK, BREAK, FINISHED;
    }

    public static void createNotification(NotificationType type) {
        String title;
        String text;

        switch (type) {
            case WORK:
                title = "Time to work!";
                text = "Your break has finished.";
                break;

            case BREAK:
                title = "Take a chill pill!";
                text = "Your current work cycle is over.";
                break;

            case FINISHED:
                title = "Session over!";
                text =  "You have finished your working session.";
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        NotificationSlot slot = new NotificationSlot("slot_001", "slot_default", NotificationSlot.LEVEL_HIGH);
        slot.setEnableVibration(true);
        slot.setLockscreenVisibleness(NotificationRequest.VISIBLENESS_TYPE_PUBLIC);
        slot.setEnableLight(true);
        slot.setLedLightColor(Color.BLUE.getValue());

        try {
            NotificationHelper.addNotificationSlot(slot);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        int notificationId = 1;
        NotificationRequest request = new NotificationRequest(notificationId);
        request.setSlotId(slot.getId());

        NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();
        content.setTitle(title)
                .setText(text);
        NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(content);
        request.setContent(notificationContent);

        try {
            NotificationHelper.publishNotification(request);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
