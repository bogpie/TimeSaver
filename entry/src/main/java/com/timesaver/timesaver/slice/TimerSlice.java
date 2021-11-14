package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimerSlice extends AbilitySlice implements Component.ClickedListener {
    Button startButton;
    Button cancelButton;
    TickTimer tickTimer;
    RoundProgressBar roundProgressBar;
    boolean isStarted;
    Text milliSecondsText;
    static int targetMilliSeconds;
    boolean isBreak;

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
    }

    private void pauseTimer() {
        tickTimer.stop();
        startButton.setText("Start");
        isStarted = false;
    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_timer);
        isStarted = false;
        isBreak = false;

        roundProgressBar = (RoundProgressBar) findComponentById(ResourceTable.Id_roundProgressBar);

        tickTimer = (TickTimer) findComponentById(ResourceTable.Id_ticktimer);
        tickTimer.setCountDown(false);
        tickTimer.setFormat("HH:mm:ss");

        tickTimer.setBaseTime(getMilliSecondsFromMidnight());
        tickTimer.start();
        tickTimer.stop();

        milliSecondsText = (Text) findComponentById(ResourceTable.Id_test);

        TickTimer.TickListener listener = new TickTimer.TickListener() {
            @Override
            public void onTickTimerUpdate(TickTimer tickTimer) {
                roundProgressBar.setProgressValue(LocalDateTime.now().getSecond());
                if (getElapsedMilliseconds() >= targetMilliSeconds) {
                    pauseTimer();
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
//        test.setText(String.valueOf(i));
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);

    }

    @Override
    public void onClick(Component component) {
        if (component == startButton) {
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