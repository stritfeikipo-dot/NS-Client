package com.nsclient.module;

import com.nsclient.config.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for every toggleable feature (HUD elements, combat visuals,
 * particle effects, ...). A Module is purely client-side and purely visual:
 * it never sends extra packets or alters combat outcomes.
 */
public abstract class Module {

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final ModuleCategory category;
    private boolean enabled;
    private int keyCode = InputUtil.UNKNOWN_KEY.getCode(); // -1 = unbound

    private final List<Setting<?>> settings = new ArrayList<>();

    protected Module(String name, String description, ModuleCategory category, boolean enabledByDefault) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = enabledByDefault;
    }

    /** Called once after the module is registered, before config is loaded. */
    public void init() {
    }

    /** Called when the module is turned on. */
    public void onEnable() {
    }

    /** Called when the module is turned off. */
    public void onDisable() {
    }

    public final void toggle() {
        setEnabled(!enabled);
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    protected final <T> Setting<T> register(Setting<T> setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public Setting<?> getSetting(String name) {
        for (Setting<?> s : settings) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }
}
