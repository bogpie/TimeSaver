package com.timesaver.timesaver;

import com.timesaver.timesaver.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        setSwipeToDismiss(true);
        setAbilitySliceAnimator(null);
    }
}
