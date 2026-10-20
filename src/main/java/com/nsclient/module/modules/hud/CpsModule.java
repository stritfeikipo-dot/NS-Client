package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsModule extends HudModule {

    private final Deque<Long> leftClicks = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();

    public CpsModule() {
        super("CPS", "Clicks per second (left / right)", ModuleCategory.HUD, true, 6, 20);
    }

    public void registerLeftClick() {
        leftClicks.add(System.currentTimeMillis());
    }

    public void registerRightClick() {
        rightClicks.add(System.currentTimeMillis());
    }

    private int countRecent(Deque<Long> deque) {
        long now = System.currentTimeMillis();
        while (!deque.isEmpty() && now - deque.peekFirst() > 1000) {
            deque.pollFirst();
        }
        return deque.size();
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        String text = "CPS: " + countRecent(leftClicks) + " / " + countRecent(rightClicks);
        RenderUtil.drawScaledLabel(context, text, getX(), getY(), getScale(), this::setLastSize);
    }
}
