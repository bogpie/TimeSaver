package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerSlice extends AbilitySlice implements Component.ClickedListener {
    Button start;
    Button end;
    TickTimer tickTimer;
    int milliSecondsFromMidnight;
    int milliSecondsForTimer = 90000;
    String secondsString;
    Text.TextObserver observer;
    int value = 10;
    private ListContainer container;
    Text secondsText;
    RoundProgressBar roundProgressBar;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_timer);

        roundProgressBar = (RoundProgressBar) findComponentById(ResourceTable.Id_roundProgressBar);
        roundProgressBar.setProgressValue(value);

        tickTimer = (TickTimer) findComponentById(ResourceTable.Id_ticktimer);
        tickTimer.setCountDown(true);
        milliSecondsFromMidnight = LocalTime.now().toSecondOfDay() * 1000;
        tickTimer.setFormat("HH:mm:ss");


        tickTimer.setBaseTime(milliSecondsFromMidnight - milliSecondsForTimer);
        tickTimer.start();

        TickTimer.TickListener listener = new TickTimer.TickListener() {
            @Override
            public void onTickTimerUpdate(TickTimer tickTimer) {
                value = LocalTime.now().getSecond() * 10;
                roundProgressBar.setProgressValue(value % 100);
            }
        };
        tickTimer.setTickListener(listener);

        start = (Button) findComponentById(ResourceTable.Id_start);
        end = (Button) findComponentById(ResourceTable.Id_end);
        start.setClickedListener(this);
        end.setClickedListener(this);

        secondsText = (Text) findComponentById(ResourceTable.Id_test);


        observer = new Text.TextObserver() {
            @Override
            public void onTextUpdated(String s, int i, int i1, int i2) {
                secondsString = s;
            }
        };
        secondsText.addTextObserver(observer);
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
        if (component == start) {
            tickTimer.start();
            tickTimer.setBaseTime(70379000);
            secondsText.setText(LocalTime.now().toString());
            value+=10;
            roundProgressBar.setProgressValue(value);
        } else if (component == end) {
            tickTimer.stop();
        }
    }
}
