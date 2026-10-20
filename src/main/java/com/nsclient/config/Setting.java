package com.nsclient.config;

import java.util.List;

/**
 * A single configurable value owned by a Module. One class handles every
 * setting kind the ClickGUI needs so modules can declare settings in one line.
 */
public class Setting<T> {

    public enum Type {
        BOOLEAN,
        SLIDER_FLOAT,
        SLIDER_INT,
        COLOR,
        MODE
    }

    private final String name;
    private final Type type;
    private T value;
    private final T defaultValue;

    // Bounds for sliders
    private float min;
    private float max;
    private float step;

    // Options for MODE settings
    private List<String> modes;

    private Setting(String name, Type type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public static Setting<Boolean> bool(String name, boolean def) {
        return new Setting<>(name, Type.BOOLEAN, def);
    }

    public static Setting<Float> slider(String name, float def, float min, float max, float step) {
        Setting<Float> s = new Setting<>(name, Type.SLIDER_FLOAT, def);
        s.min = min;
        s.max = max;
        s.step = step;
        return s;
    }

    public static Setting<Integer> sliderInt(String name, int def, int min, int max) {
        Setting<Integer> s = new Setting<>(name, Type.SLIDER_INT, def);
        s.min = min;
        s.max = max;
        s.step = 1;
        return s;
    }

    public static Setting<Integer> color(String name, int argbDefault) {
        return new Setting<>(name, Type.COLOR, argbDefault);
    }

    public static Setting<String> mode(String name, String def, List<String> options) {
        Setting<String> s = new Setting<>(name, Type.MODE, def);
        s.modes = options;
        return s;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void resetToDefault() {
        this.value = defaultValue;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStep() {
        return step;
    }

    public List<String> getModes() {
        return modes;
    }
}
