package com.nsclient.keybind;

import com.nsclient.gui.ClickGUIScreen;
import com.nsclient.module.ModuleManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class KeybindHandler {

    private static KeyBinding openGuiKey;
    private static KeyBinding openHudEditorKey;

    private KeybindHandler() {
    }

    public static void register() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nsclient.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.nsclient.general"
        ));

        openHudEditorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nsclient.open_hud_editor",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                "category.nsclient.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen != null) return;

            while (openGuiKey.wasPressed()) {
                client.setScreen(new ClickGUIScreen());
            }
            while (openHudEditorKey.wasPressed()) {
                client.setScreen(new com.nsclient.hud.HudEditorScreen());
            }

            // Per-module keybinds (bound in the ClickGUI's keybind picker).
            // Only fires toggles for modules bound to this exact code;
            // ModuleManager ignores unbound (-1) modules.
            if (client.player != null) {
                for (int code = GLFW.GLFW_KEY_SPACE; code <= GLFW.GLFW_KEY_LAST; code++) {
                    if (wasJustPressed(code)) {
                        ModuleManager.get().handleKeyPress(code);
                    }
                }
            }
        });
    }

    // Simple edge-detection so holding a key doesn't spam-toggle every tick.
    private static final boolean[] keyWasDown = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private static boolean wasJustPressed(int code) {
        long handle = net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle();
        boolean down = InputUtil.isKeyPressed(handle, code);
        boolean justPressed = down && !keyWasDown[code];
        keyWasDown[code] = down;
        return justPressed;
    }
}
