package com.nsclient.hud;

import com.nsclient.config.ConfigManager;
import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleManager;
import com.nsclient.util.NSColors;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Lets the player drag every enabled HUD element to a new position, scale it
 * with the mouse wheel, and reset it with a right-click. Large hit boxes
 * (min 28px) keep this usable on touch screens (Android launchers).
 */
public class HudEditorScreen extends Screen {

    private static final int TOUCH_PADDING = 10;

    private HudModule dragging;
    private float dragOffsetX;
    private float dragOffsetY;
    private HudModule selected;

    public HudEditorScreen() {
        super(Text.literal("NS Client - HUD Editor"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x60000000);

        for (HudModule module : ModuleManager.get().getHudModules()) {
            if (!module.isEnabled()) continue;

            module.render(context, delta);

            int x = (int) module.getX();
            int y = (int) module.getY();
            int w = Math.max(module.getLastWidth(), 20);
            int h = Math.max(module.getLastHeight(), 14);

            boolean isSelected = module == selected || module == dragging;
            int outline = isSelected ? NSColors.ACCENT_HOVER : NSColors.ACCENT_DIM;
            RenderUtil.drawRoundedOutline(context, x - 2, y - 2, w + 4, h + 4, outline);

            if (isSelected) {
                context.drawText(textRenderer, module.getHudLabel() + " (scroll = scale)", x, y + h + 3,
                        NSColors.TEXT_PRIMARY, true);
            }
        }

        context.drawText(textRenderer, "HUD editor: drag an element to move it. Esc - save and exit.",
                10, 10, NSColors.TEXT_PRIMARY, true);

        super.render(context, mouseX, mouseY, delta);
    }

    private HudModule findAt(double mouseX, double mouseY) {
        for (HudModule module : ModuleManager.get().getHudModules()) {
            if (!module.isEnabled()) continue;
            int x = (int) module.getX() - TOUCH_PADDING;
            int y = (int) module.getY() - TOUCH_PADDING;
            int w = Math.max(module.getLastWidth(), 20) + TOUCH_PADDING * 2;
            int h = Math.max(module.getLastHeight(), 14) + TOUCH_PADDING * 2;
            if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                return module;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        HudModule target = findAt(mouseX, mouseY);
        if (target != null) {
            selected = target;
            if (click.button() == 0) {
                dragging = target;
                dragOffsetX = (float) mouseX - target.getX();
                dragOffsetY = (float) mouseY - target.getY();
            } else if (click.button() == 1) {
                target.setPosition(6, 6);
                target.setScale(1.0f);
            }
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging != null) {
            float newX = Math.max(0, (float) click.x() - dragOffsetX);
            float newY = Math.max(0, (float) click.y() - dragOffsetY);
            dragging.setPosition(newX, newY);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        dragging = null;
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        HudModule target = selected != null ? selected : findAt(mouseX, mouseY);
        if (target != null) {
            target.setScale(target.getScale() + (float) verticalAmount * 0.05f);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        ConfigManager.save();
        super.close();
    }
}
