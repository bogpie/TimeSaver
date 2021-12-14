package com.timesaver.timesaver.notification;


import com.timesaver.timesaver.MainAbility;
import ohos.agp.utils.Color;
import ohos.event.notification.*;
import ohos.rpc.RemoteException;
import ohos.vibrator.agent.VibratorAgent;
import ohos.vibrator.bean.VibrationPattern;

import java.util.List;

public class NotificationManager {

    public enum NotificationType {
        WORK, BREAK, FINISHED
    }

    private final VibratorAgent vibratorAgent = new VibratorAgent();

    public void createVibration(boolean isLong) {
        // Code from official guidelines
        String vibration = isLong ?
                VibrationPattern.VIBRATOR_TYPE_WATCH_SYSTEMTYPE_STRENGTH7 :
                VibrationPattern.VIBRATOR_TYPE_WATCH_SYSTEMTYPE_STRENGTH2;

        List<Integer> vibratorList = vibratorAgent.getVibratorIdList();
        if (vibratorList.isEmpty()) {
            return;
        }
        int vibratorId = vibratorList.get(0);

        // Check whether a specified vibrator supports a preset vibration effect.
        boolean isSupport = vibratorAgent.isEffectSupport(vibratorId,
                vibration);

        // Create a one-shot vibration with a specified effect.
        if (isSupport) {
            vibratorAgent.startOnce(vibratorId, vibration);
        }
    }

    public void createNotification(NotificationType type, int seconds) {
        createVibration(true);

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
        // slot.setLockscreenVisibleness(NotificationRequest.VISIBLENESS_TYPE_PUBLIC);
        slot.setEnableLight(true);
        slot.setLedLightColor(Color.BLUE.getValue());

        try {
            // NotificationHelper.addNotificationSlot(slot);
            ReminderHelper.addNotificationSlot(slot);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        ReminderRequest request = new ReminderRequestTimer(seconds);
        request.setSlotId(slot.getId());

//        NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();

        request.setTitle(title)
                .setContent(text);

        request.setRingDuration(10);

        // NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(content);
        // request.setContent(notificationContent);

        request.setIntentAgent("com.timesaver.timesaver", MainAbility.class.getName());

        request.setActionButton("close", ReminderRequest.ACTION_BUTTON_TYPE_CLOSE);

        try {
            ReminderHelper.publishReminder(request);
        } catch (ReminderManager.AppLimitExceedsException | ReminderManager.SysLimitExceedsException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
