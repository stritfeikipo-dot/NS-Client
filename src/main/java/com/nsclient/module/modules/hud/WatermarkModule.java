package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public class WatermarkModule extends HudModule {

    private static final String TEXT = "NS Client";

    public WatermarkModule() {
        super("Watermark", "Shows the \"NS Client\" tag in the corner of your screen", ModuleCategory.HUD, true, 6, 206);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int width = RenderUtil.textWidth(TEXT) + 10;
        int height = RenderUtil.lineHeight() + 6;
        int x = (int) getX();
        int y = (int) getY();

        RenderUtil.drawRoundedRect(context, x, y, width, height, NSColors.BG_PANEL);
        context.fill(x, y, x + 2, y + height, NSColors.ACCENT);
        context.drawText(mc.textRenderer, TEXT, x + 6, y + 3, NSColors.ACCENT_HOVER, true);

        setLastSize(width, height);
    }
}
