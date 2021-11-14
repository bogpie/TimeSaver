package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;

import java.time.Duration;
import java.time.LocalDateTime;
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
    static int workMinutes;

    static int currentCycleMilliSeconds;

    boolean isWork = true;
    Text actualTimer;

    private int getMilliSecondsFromMidnight() {
        return LocalTime.now().toSecondOfDay() * 1000;
    }

    private int getElapsedMilliseconds() {
        return LocalTime.parse(tickTimer.getText()).toSecondOfDay() * 1000;
    }

    private void startTimer() {
        tickTimer.setBaseTime(getMilliSecondsFromMidnight() - getElapsedMilliseconds());
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
        cycleType.setText("Work");
        previousPassedMilliSeconds += currentCycleMilliSeconds;
        currentCycleMilliSeconds = workMinutes * 60 * 1000;
        --breaks;
        isWork = true;
        stopTimer();
    }

    private void switchToBreak() {
        cycleType.setText("Break");
        previousPassedMilliSeconds += currentCycleMilliSeconds;
        currentCycleMilliSeconds = breakMinutes * 60 * 1000;
        --works;
        isWork = false;
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

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_timer);
        currentCycleMilliSeconds = workMinutes * 60 * 1000;
        isStarted = false;
        isWork = true;
        isFinished = false;

        previousPassedMilliSeconds = 0;
        totalMilliSeconds = ((workMinutes * works + breakMinutes * breaks) * 60 * 1000);

        roundProgressBar = (RoundProgressBar) findComponentById(ResourceTable.Id_roundProgressBar);

        tickTimer = (TickTimer) findComponentById(ResourceTable.Id_ticktimer);
        tickTimer.setCountDown(false);
        tickTimer.setFormat("HH:mm:ss");

        tickTimer.setBaseTime(getMilliSecondsFromMidnight());
        tickTimer.start();
        tickTimer.stop();

        actualTimer = (Text) findComponentById(ResourceTable.Id_actualTimer);
        updateActualTimer();

        cycleType = (Text) findComponentById(ResourceTable.Id_cycleType);

        TickTimer.TickListener listener = tickTimer -> {

            int totalPassedMilliSeconds = (previousPassedMilliSeconds + getElapsedMilliseconds());
            int progress = (int) ((totalPassedMilliSeconds * 1.0 /  totalMilliSeconds) * 100);
            updateActualTimer();

            roundProgressBar.setProgressValue(progress);
            if (getElapsedMilliseconds() >= currentCycleMilliSeconds) {
                if (isWork) {
                    switchToBreak();
                } else {
                    switchToWork();
                }
                if (works == 0) {
                    cycleType.setText("Finished!");
                    actualTimer.setText("00:00:00");
                    isFinished = true;
                }

            }
        };
        tickTimer.setTickListener(listener);

        startButton = (Button) findComponentById(ResourceTable.Id_startButton);
        startButton.setClickedListener(this);
        startButton.setText("Start");
        cancelButton = (Button) findComponentById(ResourceTable.Id_cancelButton);
        cancelButton.setClickedListener(this);
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
        createNotification();

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