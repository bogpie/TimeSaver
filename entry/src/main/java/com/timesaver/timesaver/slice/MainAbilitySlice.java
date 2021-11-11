package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.MainAbility;
import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Button;
import ohos.agp.components.TimePicker;

public class MainAbilitySlice extends AbilitySlice {
    int hour = 0;
    int minute = 0;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        Button buttonToBreaksSetup = (Button) findComponentById(ResourceTable.Id_buttonToBreaksSetup);

        TimePicker timePicker = (TimePicker) findComponentById(ResourceTable.Id_time_picker);
        timePicker.showSecond(false);

        timePicker.setHour(0);
        timePicker.setMinute(0);
        timePicker.setRange(new int[]{0, 0, 0, 12, 59, 59});


        timePicker.setTimeChangedListener((timepicker, hour, minute, second) -> {
                    this.hour = hour;
                    this.minute = minute;
                }
        );


        buttonToBreaksSetup.setClickedListener(listener -> {
                    Intent passedIntent = new Intent();

                    Operation operation =
                            new Intent.OperationBuilder()
                                    .withBundleName(getBundleName())
                                    .withAbilityName(MainAbility.class.getName())
                                    .withAction("action.setTime")
                                    .build();

                    passedIntent.setOperation(operation);

                    passedIntent.setParam("HOUR", hour);
                    passedIntent.setParam("MINUTE", hour);

                    //startAbility(passedIntent);

                    present(new BreaksSetupSlice(), passedIntent);
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