
package com.nsclient.module.modules.hud;

import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorHudModule extends HudModule {

    private static final int SLOT = 18;
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorHudModule() {
        super("Armor HUD", "Shows your equipped armor pieces and durability", ModuleCategory.HUD, true, 6, 110);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.player == null) return;

        List<ItemStack> armor = new ArrayList<>();
        for (EquipmentSlot slot : SLOTS) {
            armor.add(mc.player.getEquippedStack(slot));
        }

        int x = (int) getX();
        int y = (int) getY();
        int drawn = 0;

        for (ItemStack stack : armor) {
            if (stack.isEmpty()) continue;
            int slotX = x + drawn * (SLOT + 2);
            RenderUtil.drawRoundedRect(context, slotX, y, SLOT, SLOT, NSColors.BG_PANEL_LIGHT);
            context.drawItem(stack, slotX + 1, y + 1);
            context.drawStackOverlay(mc.textRenderer, stack, slotX + 1, y + 1);
            drawn++;
        }

        if (drawn == 0) {
            setLastSize(0, 0);
        } else {
            setLastSize(drawn * (SLOT + 2), SLOT);
        }
    }
}
