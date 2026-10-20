package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;

public class KeystrokesModule extends HudModule {

    private static final int KEY_SIZE = 18;
    private static final int GAP = 2;

    public KeystrokesModule() {
        super("Keystrokes", "Shows WASD and mouse buttons as you press them", ModuleCategory.HUD, false, 6, 64);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.options == null) return;

        int baseX = (int) getX();
        int baseY = (int) getY();

        drawKey(context, "W", mc.options.forwardKey, baseX + KEY_SIZE + GAP, baseY);
        drawKey(context, "A", mc.options.leftKey, baseX, baseY + KEY_SIZE + GAP);
        drawKey(context, "S", mc.options.backKey, baseX + KEY_SIZE + GAP, baseY + KEY_SIZE + GAP);
        drawKey(context, "D", mc.options.rightKey, baseX + (KEY_SIZE + GAP) * 2, baseY + KEY_SIZE + GAP);

        int rowY = baseY + (KEY_SIZE + GAP) * 2;
        drawKeyWide(context, "LMB", mc.options.attackKey, baseX, rowY, KEY_SIZE + 10);
        drawKeyWide(context, "RMB", mc.options.useKey, baseX + KEY_SIZE + GAP + KEY_SIZE + 12, rowY, KEY_SIZE + 10);

        setLastSize((KEY_SIZE + GAP) * 3 + 20, (KEY_SIZE + GAP) * 3 + 10);
    }

    private void drawKey(DrawContext context, String label, KeyBinding binding, int x, int y) {
        drawKeyWide(context, label, binding, x, y, KEY_SIZE);
    }

    private void drawKeyWide(DrawContext context, String label, KeyBinding binding, int x, int y, int width) {
        boolean pressed = binding != null && binding.isPressed();
        int bg = pressed ? NSColors.ACCENT : NSColors.TOGGLE_OFF;
        RenderUtil.drawRoundedRect(context, x, y, width, KEY_SIZE, bg);
        int textWidth = RenderUtil.textWidth(label);
        context.drawText(mc.textRenderer, label, x + (width - textWidth) / 2, y + (KEY_SIZE - RenderUtil.lineHeight()) / 2,
                NSColors.TEXT_PRIMARY, true);
    }
}
