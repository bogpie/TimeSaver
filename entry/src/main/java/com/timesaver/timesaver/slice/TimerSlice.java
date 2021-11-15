package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import com.timesaver.timesaver.notification.NotificationManager;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static com.timesaver.timesaver.notification.NotificationManager.createNotification;

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

    boolean isWork = true;
    Text actualTimer;

    private int getMilliSecondsFromMidnight() {
        return LocalTime.now().toSecondOfDay() * 1000;
    }

    private int getTickTimerMillis() {
        return LocalTime.parse(tickTimer.getText()).toSecondOfDay() * 1000;
    }

    private void startTimer() {
        tickTimer.setBaseTime(getMilliSecondsFromMidnight() - getTickTimerMillis());
        tickTimer.start();
        startButton.setText("Pause");
        isStarted = true;
        updateActualTimer();
    }

    private void pauseTimer() {
        tickTimer.stop();
        startButton.setText("Start");
        isStarted = false;
        updateActualTimer();
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
        createNotification(NotificationManager.NotificationType.WORK);
        cycleType.setText("Work");
        previousPassedMilliSeconds += currentCycleMilliSeconds;
        currentCycleMilliSeconds = workMilliseconds;
        roundProgressBar.setProgressValue(previousPassedMilliSeconds);

        int progress = (int) ((previousPassedMilliSeconds * 1.0 /  totalMilliSeconds) * 100);
        roundProgressBar.setProgressValue(progress);

        --breaks;
        isWork = true;
        stopTimer();
    }

    private void switchToBreak() {
        createNotification(NotificationManager.NotificationType.BREAK);
        cycleType.setText("Break");
        previousPassedMilliSeconds += currentCycleMilliSeconds;
        currentCycleMilliSeconds = breakMinutes * 60 * 1000;

        int progress = (int) ((previousPassedMilliSeconds * 1.0 /  totalMilliSeconds) * 100);
        roundProgressBar.setProgressValue(progress);

        --works;
        isWork = false;
        stopTimer();
    }

    private void resetTimer() {
        isStarted = false;
        isFinished = false;
        isWork = true;
        roundProgressBar.setProgressValue(0);
        startButton.setText("Start");
//        cancelButton.setText("Cancel");
        cycleType.setText("Work");
        stopTimer();
    }


    private void updateActualTimer() {
        LocalTime elapsedTime = LocalTime.parse(tickTimer.getText());
        LocalTime finalTime = LocalTime.ofSecondOfDay(currentCycleMilliSeconds / 1000);
        long remainingMillis = Duration.between(elapsedTime, finalTime).toMillis();

        String actualTimerString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(remainingMillis),
                TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % TimeUnit.MINUTES.toSeconds(1));

        actualTimer.setText(actualTimerString);
    }

    private void finishSession() {
        cycleType.setText("Finished!");
        actualTimer.setText("00:00:00");
        isFinished = true;
        createNotification(NotificationManager.NotificationType.FINISHED);
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


        currentCycleMilliSeconds = workMilliseconds;

        previousPassedMilliSeconds = 0;
        totalMilliSeconds = workMilliseconds * works + breakMinutes * breaks * 60 * 1000;

        tickTimer.setCountDown(false);
        tickTimer.setFormat("HH:mm:ss");

        updateActualTimer();

        TickTimer.TickListener listener = tickTimer -> {

            int totalPassedMilliSeconds = (previousPassedMilliSeconds + getTickTimerMillis());
            int progress = (int) ((totalPassedMilliSeconds * 1.0 /  totalMilliSeconds) * 100);
            updateActualTimer();

            roundProgressBar.setProgressValue(progress);
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
        if (component == startButton && !isFinished) {
            if (isStarted) {
                pauseTimer();
            } else {
                startTimer();
            }
        }
        if (component == cancelButton) {
            present(new MainAbilitySlice(), new Intent());
        }
    }
}