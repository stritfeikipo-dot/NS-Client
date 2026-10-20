package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingModule extends HudModule {

    public PingModule() {
        super("Ping", "Displays your connection latency", ModuleCategory.HUD, true, 6, 34);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int ping = 0;
        if (mc.getNetworkHandler() != null && mc.player != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }
        String text = "Ping: " + ping + "ms";
        RenderUtil.drawScaledLabel(context, text, getX(), getY(), getScale(), this::setLastSize);
    }
}
