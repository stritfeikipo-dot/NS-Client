package com.nsclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

import java.util.function.BiConsumer;

/**
 * Rendering helpers used throughout NS Client. Everything here uses plain
 * DrawContext calls (fills + vanilla text), so it stays compatible with
 * Sodium and needs no custom shaders.
 */
public final class RenderUtil {
    private RenderUtil() {
    }

    private static TextRenderer tr() {
        return MinecraftClient.getInstance().textRenderer;
    }

    /**
     * Draws a filled rectangle with 1px corner notches removed, giving a
     * cheap "soft rounded" look without needing a custom shader or texture.
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x + 1, y, x + width - 1, y + height, color);
        context.fill(x, y + 1, x + width, y + height - 1, color);
    }

    public static void drawRoundedOutline(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x + 1, y, x + width - 1, y + 1, color);
        context.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    /**
     * Draws a small HUD "chip": translucent rounded background behind a
     * line of text, at the given scale and anchored top-left at (x, y).
     * Reports the final pixel size back via sizeCallback so HudModule can
     * keep an accurate drag/selection box.
     */
    public static void drawScaledLabel(DrawContext context, String text, float x, float y, float scale,
                                        BiConsumer<Integer, Integer> sizeCallback) {
        int textWidth = tr().getWidth(text);
        int padding = 4;
        int width = (int) ((textWidth + padding * 2) * scale);
        int height = (int) ((tr().fontHeight + padding) * scale);

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);

        drawRoundedRect(context, 0, 0, textWidth + padding * 2, tr().fontHeight + padding, NSColors.HUD_CHIP_BG);
        context.fill(0, 0, 2, tr().fontHeight + padding, NSColors.ACCENT);
        context.drawText(tr(), text, padding, padding / 2, NSColors.TEXT_PRIMARY, true);

        matrices.popMatrix();

        if (sizeCallback != null) sizeCallback.accept(width, height);
    }

    public static int textWidth(String text) {
        return tr().getWidth(text);
    }

    public static int lineHeight() {
        return tr().fontHeight;
    }
}
