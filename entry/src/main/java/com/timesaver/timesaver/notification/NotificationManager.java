package com.timesaver.timesaver.notification;


import ohos.agp.utils.Color;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.NotificationRequest;
import ohos.event.notification.NotificationSlot;
import ohos.rpc.RemoteException;

public class NotificationManager {
    public static void createNotification() {
        NotificationSlot slot = new NotificationSlot("slot_001", "slot_default", NotificationSlot.LEVEL_DEFAULT);
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

        String title = "Notification Example";
        String text = "This is a test notification";
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
