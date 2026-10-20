package com.nsclient.module.modules.combat;

import com.nsclient.config.Setting;
import com.nsclient.module.Module;
import com.nsclient.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

/**
 * Draws an additive custom crosshair over the vanilla one. NOTE: this module
 * draws on top of (not instead of) the vanilla crosshair icon; "Hide vanilla"
 * is a documented no-op for now (see README) - fully removing the vanilla
 * crosshair needs a HUD mixin targeting a private, version-specific method.
 */
public class CustomCrosshairModule extends Module {

    private final Setting<String> shape = register(Setting.mode("Shape", "Cross",
            List.of("Cross", "Dot", "Circle")));
    private final Setting<Float> crossSize = register(Setting.slider("Size", 6f, 2f, 20f, 1f));
    private final Setting<Float> thickness = register(Setting.slider("Thickness", 2f, 1f, 6f, 1f));
    private final Setting<Integer> color = register(Setting.color("Color", 0xFF9B4DFF));
    private final Setting<Float> opacity = register(Setting.slider("Opacity", 1.0f, 0.1f, 1.0f, 0.05f));
    private final Setting<Boolean> hideVanilla = register(Setting.bool("Hide vanilla", true));
    private final Setting<Boolean> dynamicColor = register(Setting.bool("Dynamic color on hit", false));

    public CustomCrosshairModule() {
        super("Custom Crosshair", "Replaces the crosshair's look: shape, size, thickness, color and opacity",
                ModuleCategory.COMBAT, false);
    }

    public void render(DrawContext context) {
        if (!isEnabled()) return;

        int cx = context.getScaledWindowWidth() / 2;
        int cy = context.getScaledWindowHeight() / 2;
        int argb = color.getValue();
        int alpha = (int) (((argb >>> 24) / 255f) * opacity.getValue() * 255f);
        int drawColor = (argb & 0x00FFFFFF) | (alpha << 24);

        // NOTE: "Hide vanilla" is intentionally not wired up to a real effect
        // yet. Vanilla's crosshair is drawn with a logic-op blend so it stays
        // visible against any background; a translucent fill can't cover it.
        // Actually removing it needs a mixin cancelling InGameHud's crosshair
        // render call, which is left out of this build (see README).

        int s = Math.round(crossSize.getValue());
        int t = Math.round(thickness.getValue());

        switch (shape.getValue()) {
            case "Dot" -> context.fill(cx - t / 2, cy - t / 2, cx + t / 2 + 1, cy + t / 2 + 1, drawColor);
            case "Circle" -> drawCircleOutline(context, cx, cy, s, t, drawColor);
            default -> {
                context.fill(cx - s, cy - t / 2, cx + s, cy + t / 2 + 1, drawColor);
                context.fill(cx - t / 2, cy - s, cx + t / 2 + 1, cy + s, drawColor);
            }
        }
    }

    private void drawCircleOutline(DrawContext context, int cx, int cy, int radius, int thickness, int color) {
        int steps = 24;
        for (int i = 0; i < steps; i++) {
            double angle = (2 * Math.PI / steps) * i;
            int px = cx + (int) (Math.cos(angle) * radius);
            int py = cy + (int) (Math.sin(angle) * radius);
            context.fill(px - thickness / 2, py - thickness / 2, px + thickness / 2 + 1, py + thickness / 2 + 1, color);
        }
    }
}
