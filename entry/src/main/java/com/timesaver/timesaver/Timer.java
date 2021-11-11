package com.timesaver.timesaver;

import com.timesaver.timesaver.slice.TimerSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class Timer extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(TimerSlice.class.getName());
    }
}
