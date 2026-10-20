package com.nsclient;

import com.nsclient.config.ConfigManager;
import com.nsclient.event.CombatEventHandler;
import com.nsclient.keybind.KeybindHandler;
import com.nsclient.module.HudModule;
import com.nsclient.module.ModuleManager;
import com.nsclient.module.modules.combat.CustomCrosshairModule;
import com.nsclient.module.modules.combat.HitmarkerModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

import java.util.Optional;

public class NSClient implements ClientModInitializer {

    public static final String MOD_ID = "nsclient";
    public static final String MOD_NAME = "NS Client";

    @Override
    public void onInitializeClient() {
        ConfigManager.init();
        ModuleManager.get().registerAll();
        ConfigManager.load();

        KeybindHandler.register();
        CombatEventHandler.register();

        HudRenderCallback.EVENT.register(this::onHudRender);

        // Autosave periodically and on disconnect/shutdown so a crash doesn't
        // lose HUD positions or settings tweaked mid-session.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ConfigManager.save());
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::save));

        int[] ticks = {0};
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (++ticks[0] % (20 * 60) == 0) { // every ~60s
                ConfigManager.save();
            }
        });
    }

    private void onHudRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null && mc.options.hudHidden) return;

        float tickDelta = tickCounter.getTickDelta(true);

        for (HudModule module : ModuleManager.get().getHudModules()) {
            if (module.isEnabled()) {
                module.render(context, tickDelta);
            }
        }

        getModule(HitmarkerModule.class).ifPresent(m -> m.render(context));
        getModule(CustomCrosshairModule.class).ifPresent(m -> m.render(context));
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> getModule(Class<T> type) {
        for (var module : ModuleManager.get().getModules()) {
            if (type.isInstance(module)) return Optional.of((T) module);
        }
        return Optional.empty();
    }
}
