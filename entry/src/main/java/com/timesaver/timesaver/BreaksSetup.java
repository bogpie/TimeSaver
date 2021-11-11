package com.timesaver.timesaver;

import com.timesaver.timesaver.slice.BreaksSetupSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class BreaksSetup extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(BreaksSetupSlice.class.getName());
    }
}
