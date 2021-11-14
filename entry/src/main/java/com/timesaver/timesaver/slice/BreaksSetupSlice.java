package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Picker;

public class BreaksSetupSlice extends AbilitySlice {
    static int hour = 1; // default value for debugging
    static int minute = 0;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_breaks_setup);

        int sessionTime = hour * 60 + minute;

        Picker breaksPicker = (Picker) findComponentById(ResourceTable.Id_breaks);
        Picker breakTimePicker = (Picker) findComponentById(ResourceTable.Id_breakTime);

        Button buttonToStartTime = (Button) findComponentById(ResourceTable.Id_buttonToStartTime);
        buttonToStartTime.setClickedListener(listener -> present(new TimerSlice(), new Intent()));



        breaksPicker.setValueChangedListener(new Picker.ValueChangedListener() {
            @Override
            public void onValueChanged(Picker newBreaksPicker, int breaksLastValue, int breaksNextValue) {
                if(breaksNextValue == 0)
                {
                    breakTimePicker.setValue(0);
                    breakTimePicker.setMaxValue(0);
                }
                else
                {
                    int breakTime = sessionTime / breaksNextValue / 2;
                    breakTimePicker.setMaxValue(breakTime);
                }
            }
        });

        breakTimePicker.setValueChangedListener(new Picker.ValueChangedListener() {
            @Override
            public void onValueChanged(Picker newBreakTimePicker, int breakTimeLastValue, int breakTimeNextValue) {
                if(breakTimeNextValue == 0) {
                    breaksPicker.setValue(0);
                    breakTimePicker.setMaxValue(0);
                }
            }
        });

        if (getAbility() != null) {
            if (getAbility().getIntent() != null) {
                if (getAbility().getIntent().hasParameter("HOUR")) {
                    hour = getAbility().getIntent().getIntParam("HOUR", 0);
                    minute = getAbility().getIntent().getIntParam("MINUTE", 0);
                }
            }
        }

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
