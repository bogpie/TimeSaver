package com.timesaver.timesaver.slice;

import com.timesaver.timesaver.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.RoundProgressBar;
import ohos.agp.components.element.Element;

public class TimerSlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_timer);
        RoundProgressBar roundProgressBar = (RoundProgressBar) findComponentById(ResourceTable.Id_roundProgressBar);
        roundProgressBar.setProgressValue(75);

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
