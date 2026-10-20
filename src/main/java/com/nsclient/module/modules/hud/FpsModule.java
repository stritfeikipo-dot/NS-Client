package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FpsModule extends HudModule {

    public FpsModule() {
        super("FPS", "Displays current frames per second", ModuleCategory.HUD, true, 6, 6);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int fps = MinecraftClient.getInstance().getCurrentFps();
        String text = "FPS: " + fps;
        RenderUtil.drawScaledLabel(context, text, getX(), getY(), getScale(), this::setLastSize);
    }
}
