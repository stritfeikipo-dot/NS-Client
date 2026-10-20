package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Collection;

public class PotionHudModule extends HudModule {

    public PotionHudModule() {
        super("Potion HUD", "Lists your active status effects with remaining time", ModuleCategory.HUD, true, 6, 134);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        if (effects.isEmpty()) {
            setLastSize(0, 0);
            return;
        }

        int x = (int) getX();
        int y = (int) getY();
        int rowHeight = RenderUtil.lineHeight() + 4;
        int i = 0;
        int widest = 0;

        for (StatusEffectInstance effect : effects) {
            String name = effect.getEffectType().value().getName().getString();
            int amplifier = effect.getAmplifier() + 1;
            int seconds = effect.getDuration() / 20;
            String text = name + " " + (amplifier > 1 ? toRoman(amplifier) + " " : "") + formatTime(seconds);

            int rowY = y + i * rowHeight;
            RenderUtil.drawRoundedRect(context, x, rowY, RenderUtil.textWidth(text) + 8, rowHeight, NSColors.HUD_CHIP_BG);
            context.fill(x, rowY, x + 2, rowY + rowHeight, NSColors.ACCENT);
            context.drawText(mc.textRenderer, text, x + 5, rowY + 2, NSColors.TEXT_PRIMARY, true);

            widest = Math.max(widest, RenderUtil.textWidth(text) + 8);
            i++;
        }

        setLastSize(widest, i * rowHeight);
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%d:%02d", m, s);
    }

    private String toRoman(int number) {
        return switch (number) {
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            default -> String.valueOf(number);
        };
    }
}
