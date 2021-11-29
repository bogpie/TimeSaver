package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Picker;

public class BreaksSetupSlice extends AbilitySlice {
    static int hour = 1; // default value for debugging
    static int minute;
    int sessionMinutes;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_breaks_setup);

        sessionMinutes = hour * 60 + minute;

        Picker breaksPicker = (Picker) findComponentById(ResourceTable.Id_breaks);
        Picker breakTimePicker = (Picker) findComponentById(ResourceTable.Id_breakTime);

        breaksPicker.setMaxValue(Math.min(9, sessionMinutes / 2));
        breaksPicker.setValue(0);
        breakTimePicker.setValue(0);

        breaksPicker.setValueChangedListener((newBreaksPicker, breaksLastValue, breaksNextValue) -> {
            if (breaksNextValue == 0) {
                breakTimePicker.setMaxValue(0);
                breakTimePicker.setValue(0);
            } else {
                int breakTime = sessionMinutes / breaksNextValue / 2;
                breakTimePicker.setMaxValue(breakTime);
                breakTimePicker.setValue(1);
            }
        });

        breakTimePicker.setValueChangedListener((newBreakTimePicker, breakTimeLastValue, breakTimeNextValue) -> {
            if (breakTimeNextValue == 0) {
                breaksPicker.setValue(0);
                breakTimePicker.setMaxValue(0);
            }
        });

        Button buttonToStartTime = (Button) findComponentById(ResourceTable.Id_buttonToStartTime);
        buttonToStartTime.setClickedListener(listener ->
                {
                    TimerSlice.breaks = breaksPicker.getValue();
                    TimerSlice.breakMinutes = breakTimePicker.getValue();
                    TimerSlice.works = breaksPicker.getValue() + 1;
                    TimerSlice.workMilliseconds =
                            (int) ((sessionMinutes - TimerSlice.breakMinutes * TimerSlice.breaks) * 1.0
                                    / TimerSlice.works
                                    * 60 * 1000);

                    present(new TimerSlice(), new Intent());
                }
        );

        Button buttonToGoBack = (Button) findComponentById(ResourceTable.Id_buttonToGoBack);

        buttonToGoBack.setClickedListener(listener -> {
            present(new MainAbilitySlice(), new Intent());
        });


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
