package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class TargetHudModule extends HudModule {

    public TargetHudModule() {
        super("Target HUD", "Shows the name, health and distance of the entity you're looking at",
                ModuleCategory.HUD, true, 6, 180);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        Entity target = mc.targetedEntity;
        if (!(target instanceof LivingEntity living) || mc.player == null) {
            setLastSize(0, 0);
            return;
        }

        String name = living.getName().getString();
        float health = living.getHealth();
        float maxHealth = living.getMaxHealth();
        double distance = mc.player.distanceTo(living);

        String line1 = name;
        String line2 = String.format("HP: %.1f / %.1f", health, maxHealth);
        String line3 = String.format("Dist: %.1fm", distance);

        int width = Math.max(RenderUtil.textWidth(line1), Math.max(RenderUtil.textWidth(line2), RenderUtil.textWidth(line3))) + 10;
        int lineH = RenderUtil.lineHeight() + 2;
        int height = lineH * 3 + 6;

        int x = (int) getX();
        int y = (int) getY();

        RenderUtil.drawRoundedRect(context, x, y, width, height, NSColors.BG_PANEL);
        RenderUtil.drawRoundedOutline(context, x, y, width, height, NSColors.ACCENT_DIM);

        context.drawText(mc.textRenderer, line1, x + 5, y + 4, NSColors.TEXT_PRIMARY, true);
        context.drawText(mc.textRenderer, line2, x + 5, y + 4 + lineH, NSColors.ACCENT, true);
        context.drawText(mc.textRenderer, line3, x + 5, y + 4 + lineH * 2, NSColors.TEXT_SECONDARY, true);

        // Health bar
        int barY = y + height - 5;
        float pct = maxHealth > 0 ? Math.max(0, health / maxHealth) : 0;
        context.fill(x + 5, barY, x + width - 5, barY + 3, NSColors.TOGGLE_OFF);
        context.fill(x + 5, barY, x + 5 + (int) ((width - 10) * pct), barY + 3, NSColors.ACCENT);

        setLastSize(width, height);
    }
}
