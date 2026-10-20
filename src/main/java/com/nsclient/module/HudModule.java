package com.nsclient.module;

import net.minecraft.client.gui.DrawContext;

/**
 * A Module that draws something on screen and can be repositioned in the
 * HUD editor (drag with mouse or touch, scale with a slider).
 */
public abstract class HudModule extends Module {

    private float x;
    private float y;
    private float scale = 1.0f;

    // Cached size of the last render, used by the HUD editor for hit-testing
    // and for drawing the selection outline.
    private int lastWidth = 40;
    private int lastHeight = 12;

    protected HudModule(String name, String description, ModuleCategory category,
                         boolean enabledByDefault, float defaultX, float defaultY) {
        super(name, description, category, enabledByDefault);
        this.x = defaultX;
        this.y = defaultY;
    }

    /**
     * Render the element at its stored position. Implementations should use
     * {@link #getX()}/{@link #getY()}/{@link #getScale()} and update
     * {@link #setLastSize(int, int)} with the drawn size so the HUD editor
     * can show an accurate selection box.
     */
    public abstract void render(DrawContext context, float tickDelta);

    /** A short label shown in the HUD editor when this element is selected. */
    public String getHudLabel() {
        return getName();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = Math.max(0.5f, Math.min(3.0f, scale));
    }

    public int getLastWidth() {
        return lastWidth;
    }

    public int getLastHeight() {
        return lastHeight;
    }

    protected void setLastSize(int width, int height) {
        this.lastWidth = width;
        this.lastHeight = height;
    }
}
