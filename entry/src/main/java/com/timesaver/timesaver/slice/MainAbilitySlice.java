package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.TimePicker;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        Button buttonToBreaksSetup = (Button) findComponentById(ResourceTable.Id_buttonToBreaksSetup);
        buttonToBreaksSetup.setClickedListener(listener -> present(new BreaksSetupSlice(), new Intent()));

        TimePicker timePicker = (TimePicker) findComponentById(ResourceTable.Id_time_picker);
        timePicker.showSecond(false);
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        timePicker.setHour(0);
        timePicker.setMinute(0);
        timePicker.setRange(new int[]{0, 0, 0, 12, 59, 59});

        timePicker.setTimeChangedListener((timePicker1, hour1, minute1, second1) -> {
                    //ToDo:
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