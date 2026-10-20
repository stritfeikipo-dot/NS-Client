package com.nsclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nsclient.module.HudModule;
import com.nsclient.module.Module;
import com.nsclient.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configDir;
    private static Path configFile;

    private ConfigManager() {
    }

    public static void init() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("nsclient");
        configFile = configDir.resolve("config.json");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            System.err.println("[NS Client] Failed to create config directory: " + e.getMessage());
        }
    }

    public static void load() {
        if (configFile == null || !Files.exists(configFile)) return;

        try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject modulesObj = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();

            for (Module module : ModuleManager.get().getModules()) {
                if (!modulesObj.has(module.getName())) continue;
                JsonObject mObj = modulesObj.getAsJsonObject(module.getName());

                if (mObj.has("enabled")) module.setEnabled(mObj.get("enabled").getAsBoolean());
                if (mObj.has("keyCode")) module.setKeyCode(mObj.get("keyCode").getAsInt());

                if (mObj.has("settings")) {
                    JsonObject settingsObj = mObj.getAsJsonObject("settings");
                    for (Setting<?> setting : module.getSettings()) {
                        if (!settingsObj.has(setting.getName())) continue;
                        applySettingValue(setting, settingsObj);
                    }
                }

                if (module instanceof HudModule hud) {
                    if (mObj.has("x") && mObj.has("y")) {
                        hud.setPosition(mObj.get("x").getAsFloat(), mObj.get("y").getAsFloat());
                    }
                    if (mObj.has("scale")) {
                        hud.setScale(mObj.get("scale").getAsFloat());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[NS Client] Failed to load config, using defaults: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void applySettingValue(Setting<?> setting, JsonObject settingsObj) {
        switch (setting.getType()) {
            case BOOLEAN -> ((Setting<Boolean>) setting).setValue(settingsObj.get(setting.getName()).getAsBoolean());
            case SLIDER_FLOAT -> ((Setting<Float>) setting).setValue(settingsObj.get(setting.getName()).getAsFloat());
            case SLIDER_INT -> ((Setting<Integer>) setting).setValue(settingsObj.get(setting.getName()).getAsInt());
            case COLOR -> ((Setting<Integer>) setting).setValue(settingsObj.get(setting.getName()).getAsInt());
            case MODE -> ((Setting<String>) setting).setValue(settingsObj.get(setting.getName()).getAsString());
        }
    }

    public static void save() {
        if (configFile == null) return;

        JsonObject root = new JsonObject();
        JsonObject modulesObj = new JsonObject();

        for (Module module : ModuleManager.get().getModules()) {
            JsonObject mObj = new JsonObject();
            mObj.addProperty("enabled", module.isEnabled());
            mObj.addProperty("keyCode", module.getKeyCode());

            JsonObject settingsObj = new JsonObject();
            for (Setting<?> setting : module.getSettings()) {
                switch (setting.getType()) {
                    case BOOLEAN -> settingsObj.addProperty(setting.getName(), (Boolean) setting.getValue());
                    case SLIDER_FLOAT -> settingsObj.addProperty(setting.getName(), (Float) setting.getValue());
                    case SLIDER_INT -> settingsObj.addProperty(setting.getName(), (Integer) setting.getValue());
                    case COLOR -> settingsObj.addProperty(setting.getName(), (Integer) setting.getValue());
                    case MODE -> settingsObj.addProperty(setting.getName(), (String) setting.getValue());
                }
            }
            mObj.add("settings", settingsObj);

            if (module instanceof HudModule hud) {
                mObj.addProperty("x", hud.getX());
                mObj.addProperty("y", hud.getY());
                mObj.addProperty("scale", hud.getScale());
            }

            modulesObj.add(module.getName(), mObj);
        }

        root.add("modules", modulesObj);

        try (Writer writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("[NS Client] Failed to save config: " + e.getMessage());
        }
    }
                                                         }
