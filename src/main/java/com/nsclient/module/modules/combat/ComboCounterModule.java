package com.nsclient.module.modules.combat;

import com.nsclient.config.Setting;
import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public class ComboCounterModule extends HudModule {

    private final Setting<Float> resetAfter = register(Setting.slider("Reset after (s)", 3.0f, 1.0f, 8.0f, 0.5f));

    private int combo = 0;
    private long lastHitAt = -1;

    public ComboCounterModule() {
        super("Combo Counter", "Counts consecutive hits on the same target before the timer resets",
                ModuleCategory.COMBAT, true, 6, 160);
    }

    public void onHit() {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        if (lastHitAt > 0 && (now - lastHitAt) <= resetAfter.getValue() * 1000) {
            combo++;
        } else {
            combo = 1;
        }
        lastHitAt = now;
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (combo <= 0 || lastHitAt < 0) {
            setLastSize(0, 0);
            return;
        }
        long elapsed = System.currentTimeMillis() - lastHitAt;
        if (elapsed > resetAfter.getValue() * 1000) {
            combo = 0;
            setLastSize(0, 0);
            return;
        }

        String text = combo + "x COMBO";
        RenderUtil.drawScaledLabel(context, text, getX(), getY(), getScale(), this::setLastSize);
    }
}
