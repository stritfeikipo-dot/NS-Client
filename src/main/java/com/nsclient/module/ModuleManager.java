package com.nsclient.module;

import com.nsclient.module.modules.combat.ComboCounterModule;
import com.nsclient.module.modules.combat.CustomCrosshairModule;
import com.nsclient.module.modules.combat.HitmarkerModule;
import com.nsclient.module.modules.hud.ArmorHudModule;
import com.nsclient.module.modules.hud.CoordsModule;
import com.nsclient.module.modules.hud.CpsModule;
import com.nsclient.module.modules.hud.FpsModule;
import com.nsclient.module.modules.hud.KeystrokesModule;
import com.nsclient.module.modules.hud.PingModule;
import com.nsclient.module.modules.hud.PotionHudModule;
import com.nsclient.module.modules.hud.TargetHudModule;
import com.nsclient.module.modules.hud.WatermarkModule;
import com.nsclient.module.modules.visual.CritParticlesModule;
import com.nsclient.module.modules.visual.HitParticlesModule;
import com.nsclient.module.modules.visual.KillEffectsModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {

    private static final ModuleManager INSTANCE = new ModuleManager();

    public static ModuleManager get() {
        return INSTANCE;
    }

    private final List<Module> modules = new ArrayList<>();

    private ModuleManager() {
    }

    public void registerAll() {
        // HUD
        register(new FpsModule());
        register(new CpsModule());
        register(new PingModule());
        register(new CoordsModule());
        register(new KeystrokesModule());
        register(new ArmorHudModule());
        register(new PotionHudModule());
        register(new TargetHudModule());
        register(new WatermarkModule());

        // Combat
        register(new HitmarkerModule());
        register(new ComboCounterModule());
        register(new CustomCrosshairModule());

        // Particles
        register(new HitParticlesModule());
        register(new CritParticlesModule());
        register(new KillEffectsModule());

        modules.forEach(Module::init);
    }

    private void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public List<HudModule> getHudModules() {
        List<HudModule> result = new ArrayList<>();
        for (Module m : modules) {
            if (m instanceof HudModule hm) result.add(hm);
        }
        return result;
    }

    public Map<ModuleCategory, List<Module>> byCategory() {
        Map<ModuleCategory, List<Module>> map = new LinkedHashMap<>();
        for (ModuleCategory cat : ModuleCategory.values()) {
            map.put(cat, new ArrayList<>());
        }
        for (Module m : modules) {
            map.get(m.getCategory()).add(m);
        }
        return map;
    }

    public List<Module> search(String query) {
        if (query == null || query.isBlank()) return getModules();
        String q = query.toLowerCase();
        List<Module> result = new ArrayList<>();
        for (Module m : modules) {
            if (m.getName().toLowerCase().contains(q) || m.getCategory().getDisplayName().toLowerCase().contains(q)) {
                result.add(m);
            }
        }
        return result;
    }

    public Module getByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    public void handleKeyPress(int keyCode) {
        for (Module m : modules) {
            if (m.getKeyCode() == keyCode && keyCode != -1) {
                m.toggle();
            }
        }
    }
}
