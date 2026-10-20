package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

public class CoordsModule extends HudModule {

    public CoordsModule() {
        super("Coordinates", "Displays your current block position", ModuleCategory.HUD, true, 6, 48);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        String text = "XYZ: --";
        if (mc.player != null) {
            BlockPos pos = mc.player.getBlockPos();
            text = String.format("XYZ: %d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
        }
        RenderUtil.drawScaledLabel(context, text, getX(), getY(), getScale(), this::setLastSize);
    }
}
