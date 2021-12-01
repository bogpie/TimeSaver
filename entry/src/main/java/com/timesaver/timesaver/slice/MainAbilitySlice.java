package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import com.timesaver.timesaver.notification.NotificationManager;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Text;
import ohos.agp.components.TimePicker;

public class MainAbilitySlice extends AbilitySlice {
    int hour = 0;
    int minute = 0;
    Text setUpSession;
    NotificationManager notificationManager;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        notificationManager = new NotificationManager();

        Button buttonToBreaksSetup = (Button) findComponentById(ResourceTable.Id_buttonToBreaksSetup);

        TimePicker timePicker = (TimePicker) findComponentById(ResourceTable.Id_time_picker);
        timePicker.showSecond(false);

        timePicker.setHour(0);
        timePicker.setMinute(0);
        timePicker.setRange(new int[]{0, 0, 0, 12, 59, 59});


        timePicker.setTimeChangedListener((timepicker, hour, minute, second) -> {
                    notificationManager.createVibration(false);
                    this.hour = hour;
                    this.minute = minute;
                }
        );

        setUpSession = (Text) findComponentById(ResourceTable.Id_setUpSession);
        setUpSession.setText("Set up session");

        buttonToBreaksSetup.setClickedListener(listener -> {
                    notificationManager.createVibration(true);

                    BreaksSetupSlice.hour = hour;
                    BreaksSetupSlice.minute = minute;

                    if (hour == 0 && minute == 0) setUpSession.setText("Session too small!");
                    else
                        present(new BreaksSetupSlice(), new Intent());
                }
        );
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}