package com.nsclient.module.modules.combat;

import com.nsclient.config.Setting;
import com.nsclient.module.Module;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Pure visual feedback module: flashes a small marker in the middle of the
 * screen for a few frames whenever {@link #trigger()} is called by the
 * attack-event hook. Does not affect combat in any way.
 */
public class HitmarkerModule extends Module {

    private final Setting<Boolean> critColor = register(Setting.bool("Color on crit", true));
    private final Setting<Float> duration = register(Setting.slider("Duration", 0.25f, 0.05f, 1.0f, 0.05f));
    private final Setting<Float> size = register(Setting.slider("Size", 6f, 2f, 16f, 1f));

    private long triggeredAt = -1;
    private boolean lastWasCrit = false;

    public HitmarkerModule() {
        super("Hitmarker", "Flashes a marker when you land a hit", ModuleCategory.COMBAT, true);
    }

    public void trigger(boolean crit) {
        if (!isEnabled()) return;
        this.triggeredAt = System.currentTimeMillis();
        this.lastWasCrit = crit;
    }

    public void render(DrawContext context) {
        if (!isEnabled() || triggeredAt < 0) return;
        long elapsed = System.currentTimeMillis() - triggeredAt;
        float durationMs = duration.getValue() * 1000f;
        if (elapsed > durationMs) return;

        float progress = elapsed / durationMs;
        float alpha = 1.0f - progress;
        float s = size.getValue() * (1.0f + progress * 0.5f);

        MinecraftClient mc = MinecraftClient.getInstance();
        int cx = context.getScaledWindowWidth() / 2;
        int cy = context.getScaledWindowHeight() / 2;

        int color = (lastWasCrit && critColor.getValue()) ? NSColors.ACCENT_HOVER : NSColors.HITMARKER;
        int withAlpha = NSColors.withAlpha(color, (int) (alpha * 255));

        int half = (int) (s / 2);
        context.fill(cx - half, cy - 1, cx + half, cy + 1, withAlpha);
        context.fill(cx - 1, cy - half, cx + 1, cy + half, withAlpha);
    }
}
