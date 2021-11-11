package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;

public class BreaksSetupSlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_breaks_setup);
        Button buttonToStartTime = (Button) findComponentById(ResourceTable.Id_buttonToStartTime);
        buttonToStartTime.setClickedListener(listener -> present(new TimerSlice(), new Intent()));

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
