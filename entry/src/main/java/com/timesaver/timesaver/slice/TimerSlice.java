package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import com.timesaver.timesaver.notification.NotificationManager;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.ReminderHelper;
import ohos.rpc.RemoteException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class TimerSlice extends AbilitySlice implements Component.ClickedListener {
    Button startButton;
    Button cancelButton;
    TickTimer tickTimer;
    RoundProgressBar roundProgressBar;
    boolean isStarted;
    boolean isFinished;
    Text cycleType;
    int previousPassedMilliSeconds;
    int totalMilliSeconds;

    static int breaks;
    static int breakMinutes;

    static int works;
    static int workMilliseconds;

    static int currentCycleMilliSeconds;

    NotificationManager notificationManager;
    boolean isWork = true;
    Text actualTimer;

    private int getMilliSecondsFromMidnight() {
        return LocalTime.now().toSecondOfDay() * 1000;
    }

    private int getTickTimerMillis() {
        return LocalTime.parse(tickTimer.getText()).toSecondOfDay() * 1000;
    }

    private NotificationManager.NotificationType getType() {
        if (works == 0) {
            return NotificationManager.NotificationType.FINISHED;
        }
        if (isWork) {
            return NotificationManager.NotificationType.BREAK;
        }
        return NotificationManager.NotificationType.WORK;
    }

    private void startTimer() {
        tickTimer.setBaseTime(getMilliSecondsFromMidnight() - getTickTimerMillis());
        tickTimer.start();
        startButton.setText("Pause");
        isStarted = true;
        updateActualTimer();
        notificationManager.createNotification(getType(), (int) (getRemainingMillis() / 1000));
    }

    private void pauseTimer() {
        tickTimer.stop();
        startButton.setText("Start");
        isStarted = false;
        updateActualTimer();
        try {
            ReminderHelper.cancelAllReminders(); // remove all reminders
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void stopTimer() {
        tickTimer.stop();
        startButton.setText("Start");
        isStarted = false;
        tickTimer.setBaseTime(getMilliSecondsFromMidnight());
        tickTimer.start();
        tickTimer.stop();
        updateActualTimer();
    }

    private void switchToWork() {
        cycleType.setText("Work");
        previousPassedMilliSeconds += currentCycleMilliSeconds;
        currentCycleMilliSeconds = workMilliseconds;
        roundProgressBar.setProgressValue(100 - previousPassedMilliSeconds);

        int progress = (int) ((previousPassedMilliSeconds * 1.0 / totalMilliSeconds) * 100);
        roundProgressBar.setProgressValue(100 - progress);

        --breaks;
//        if (breaks != 0)
//            notificationManager.createNotification(NotificationManager.NotificationType.WORK);

        isWork = true;
        stopTimer();
    }

    private void switchToBreak() {
        cycleType.setText("Break");
        previousPassedMilliSeconds += currentCycleMilliSeconds;
        currentCycleMilliSeconds = breakMinutes * 60 * 1000;

        int progress = (int) ((previousPassedMilliSeconds * 1.0 / totalMilliSeconds) * 100);
        roundProgressBar.setProgressValue(100 - progress);

        --works;
//        if (works != 0)
//            notificationManager.createNotification(NotificationManager.NotificationType.BREAK);

        isWork = false;
        stopTimer();
    }

    private void resetTimer() {
        isStarted = false;
        isFinished = false;
        isWork = true;
        roundProgressBar.setProgressValue(100);
        startButton.setText("Start");
        cycleType.setText("Work");
        stopTimer();
    }

    private long getRemainingMillis() {
        LocalTime elapsedTime = LocalTime.parse(tickTimer.getText());
        LocalTime finalTime = LocalTime.ofSecondOfDay(currentCycleMilliSeconds / 1000);
        return Duration.between(elapsedTime, finalTime).toMillis();
    }

    private void updateActualTimer() {
        long remainingMillis = getRemainingMillis();

        String
                actualTimerString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(remainingMillis),
                TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % TimeUnit.MINUTES.toSeconds(1));

        actualTimer.setText(actualTimerString);
    }

    private void finishSession() {
        cycleType.setText("Finished!");
        actualTimer.setText("00:00:00");
        isFinished = true;
//        notificationManager.createNotification(NotificationManager.NotificationType.FINISHED);
    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_timer);

        roundProgressBar = (RoundProgressBar) findComponentById(ResourceTable.Id_roundProgressBar);
        cycleType = (Text) findComponentById(ResourceTable.Id_cycleType);
        tickTimer = (TickTimer) findComponentById(ResourceTable.Id_ticktimer);
        actualTimer = (Text) findComponentById(ResourceTable.Id_actualTimer);
        startButton = (Button) findComponentById(ResourceTable.Id_startButton);
        cancelButton = (Button) findComponentById(ResourceTable.Id_cancelButton);

        notificationManager = new NotificationManager();

        roundProgressBar.setProgressValue(100);

        currentCycleMilliSeconds = workMilliseconds;

        previousPassedMilliSeconds = 0;
        totalMilliSeconds = workMilliseconds * works + breakMinutes * breaks * 60 * 1000;

        tickTimer.setCountDown(false);
        tickTimer.setFormat("HH:mm:ss");

        updateActualTimer();

        TickTimer.TickListener listener = tickTimer -> {
            int totalPassedMilliSeconds = (previousPassedMilliSeconds + getTickTimerMillis());
            int progress = (int) ((totalPassedMilliSeconds * 1.0 / totalMilliSeconds) * 100);
            updateActualTimer();

            roundProgressBar.setProgressValue(100 - progress);
            if (getTickTimerMillis() >= currentCycleMilliSeconds) {
                if (isWork) {
                    switchToBreak();
                } else {
                    switchToWork();
                }
                if (works == 0) {
                    finishSession();
                }

            }
        };
        tickTimer.setTickListener(listener);

        startButton.setClickedListener(this);
        cancelButton.setClickedListener(this);

        resetTimer();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);

    }

    @Override
    public void onClick(Component component) {
        notificationManager.createVibration(true);
        if (component == startButton && !isFinished) {
            if (isStarted) {
                pauseTimer();
            } else {
                try {
                    NotificationHelper.cancelAllNotifications();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                startTimer();
            }
        }
        if (component == cancelButton) {
            try {
                ReminderHelper.cancelAllReminders(); // remove all reminders
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (isStarted) {
                stopTimer();
            }
            present(new MainAbilitySlice(), new Intent());
        }
    }
}