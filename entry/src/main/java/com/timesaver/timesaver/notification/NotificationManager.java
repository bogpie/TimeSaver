package com.timesaver.timesaver.notification;


import ohos.agp.utils.Color;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.NotificationRequest;
import ohos.event.notification.NotificationSlot;
import ohos.rpc.RemoteException;
import ohos.vibrator.agent.VibratorAgent;
import ohos.vibrator.bean.VibrationPattern;

import java.util.List;

public class NotificationManager {

    public enum NotificationType {
        WORK, BREAK, FINISHED
    }

    private final VibratorAgent vibratorAgent = new VibratorAgent();

    // private int[] timing = {1000, 1000, 2000, 5000};
    // private int[] intensity = {50, 100, 200, 255};

    public void createVibration() {
        // Code from official guidelines

        List<Integer> vibratorList = vibratorAgent.getVibratorIdList();
        if (vibratorList.isEmpty()) {
            return;
        }
        int vibratorId = vibratorList.get(0);

        // Check whether a specified vibrator supports a preset vibration effect.
        boolean isSupport = vibratorAgent.isEffectSupport(vibratorId,
                VibrationPattern.VIBRATOR_TYPE_CAMERA_CLICK);

        // Create a one-shot vibration with a specified effect.
        boolean vibrateEffectResult = vibratorAgent.startOnce(vibratorId,
                VibrationPattern.VIBRATOR_TYPE_CAMERA_CLICK);

        // Stop the custom vibration effect of a vibrator.
        boolean stopResult = vibratorAgent.stop(vibratorId,
                VibratorAgent.VIBRATOR_STOP_MODE_CUSTOMIZED);
    }

    public void createNotification(NotificationType type) {

        createVibration();

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
                text = "You have finished your working session.";
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
