package com.nsclient.gui;

import com.nsclient.config.ConfigManager;
import com.nsclient.config.Setting;
import com.nsclient.module.Module;
import com.nsclient.module.ModuleCategory;
import com.nsclient.module.ModuleManager;
import com.nsclient.util.NSColors;
import com.nsclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The main "NS Client" ClickGUI. Pure vanilla DrawContext rendering (fills +
 * text), so it stays Sodium-compatible and needs no custom shaders. Rounded
 * corners are approximated with 1px corner notches (see RenderUtil).
 */
public class ClickGUIScreen extends Screen {

    private static final int SIDEBAR_X = 20;
    private static final int SIDEBAR_Y = 30;
    private static final int SIDEBAR_WIDTH = 150;
    private static final int ROW_HEIGHT = 24;

    private static final int LIST_X = 180;
    private static final int LIST_Y = 30;
    private static final int LIST_WIDTH = 260;

    private static final int SETTINGS_X = 450;
    private static final int SETTINGS_Y = 30;
    private static final int SETTINGS_WIDTH = 280;

    private ModuleCategory selectedCategory = ModuleCategory.COMBAT;
    private TextFieldWidget searchBox;
    private Module selectedModule;
    private Module awaitingKeybind;

    private final Set<Module> expanded = new HashSet<>();
    private int listScroll = 0;

    // Slider drag state
    private Setting<Float> draggingFloatSlider;
    private Setting<Integer> draggingIntSlider;
    private int sliderTrackX;
    private int sliderTrackWidth;

    private static final int[] COLOR_PRESETS = {
            0xFF9B4DFF, 0xFFB37BFF, 0xFF6E4AA8, 0xFFE8D4FF,
            0xFFFF4D6D, 0xFF4DFFEF, 0xFFFFD24D, 0xFFFFFFFF
    };

    public ClickGUIScreen() {
        super(Text.literal("NS Client"));
    }

    @Override
    protected void init() {
        searchBox = new TextFieldWidget(textRenderer, LIST_X, 8, LIST_WIDTH, 16, Text.literal("Search"));
        searchBox.setPlaceholder(Text.literal("Search modules..."));
        addSelectableChild(searchBox);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xA0000000);

        renderSidebar(context, mouseX, mouseY);
        renderModuleList(context, mouseX, mouseY);
        renderSettingsPanel(context, mouseX, mouseY);

        searchBox.render(context, mouseX, mouseY, delta);

        context.drawText(textRenderer, "NS CLIENT", SIDEBAR_X, 10, NSColors.ACCENT_HOVER, true);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderSidebar(DrawContext context, int mouseX, int mouseY) {
        int height = ModuleCategory.values().length * ROW_HEIGHT + 8;
        RenderUtil.drawRoundedRect(context, SIDEBAR_X, SIDEBAR_Y, SIDEBAR_WIDTH, height, NSColors.BG_SIDEBAR);

        int y = SIDEBAR_Y + 4;
        for (ModuleCategory cat : ModuleCategory.values()) {
            boolean selected = cat == selectedCategory;
            boolean hovered = isHovered(mouseX, mouseY, SIDEBAR_X + 4, y, SIDEBAR_WIDTH - 8, ROW_HEIGHT - 2);

            int bg = selected ? NSColors.ACCENT_DIM : (hovered ? NSColors.BG_PANEL_LIGHT : 0x00000000);
            if (bg != 0) RenderUtil.drawRoundedRect(context, SIDEBAR_X + 4, y, SIDEBAR_WIDTH - 8, ROW_HEIGHT - 2, bg);
            if (selected) context.fill(SIDEBAR_X + 4, y, SIDEBAR_X + 6, y + ROW_HEIGHT - 2, NSColors.ACCENT);

            int textColor = selected ? NSColors.TEXT_PRIMARY : NSColors.TEXT_SECONDARY;
            context.drawText(textRenderer, cat.getDisplayName(), SIDEBAR_X + 12, y + 6, textColor, false);
            y += ROW_HEIGHT;
        }
    }

    private List<Module> currentModules() {
        String query = searchBox != null ? searchBox.getText() : "";
        if (!query.isBlank()) {
            return ModuleManager.get().search(query);
        }
        return ModuleManager.get().byCategory().get(selectedCategory);
    }

    private void renderModuleList(DrawContext context, int mouseX, int mouseY) {
        List<Module> modules = currentModules();
        int panelHeight = height - LIST_Y - 20;
        RenderUtil.drawRoundedRect(context, LIST_X, LIST_Y, LIST_WIDTH, panelHeight, NSColors.BG_PANEL);

        int y = LIST_Y + 22 - listScroll;
        for (Module module : modules) {
            if (y + ROW_HEIGHT < LIST_Y + 20) {
                y += rowTotalHeight(module);
                continue;
            }
            if (y > LIST_Y + panelHeight) break;

            boolean hovered = isHovered(mouseX, mouseY, LIST_X + 6, y, LIST_WIDTH - 12, ROW_HEIGHT - 2);
            int bg = module == selectedModule ? NSColors.BG_PANEL_LIGHT : (hovered ? 0x30FFFFFF : 0x00000000);
            if (bg != 0) RenderUtil.drawRoundedRect(context, LIST_X + 6, y, LIST_WIDTH - 12, ROW_HEIGHT - 2, bg);

            context.drawText(textRenderer, module.getName(), LIST_X + 14, y + 7,
                    module.isEnabled() ? NSColors.TEXT_PRIMARY : NSColors.TEXT_SECONDARY, false);

            drawToggle(context, LIST_X + LIST_WIDTH - 36, y + 4, module.isEnabled());

            y += ROW_HEIGHT;
        }
    }

    private int rowTotalHeight(Module module) {
        return ROW_HEIGHT;
    }

    private void renderSettingsPanel(DrawContext context, int mouseX, int mouseY) {
        int panelHeight = height - SETTINGS_Y - 20;
        RenderUtil.drawRoundedRect(context, SETTINGS_X, SETTINGS_Y, SETTINGS_WIDTH, panelHeight, NSColors.BG_PANEL);

        if (selectedModule == null) {
            context.drawText(textRenderer, "Select a module to edit its settings", SETTINGS_X + 10, SETTINGS_Y + 10,
                    NSColors.TEXT_SECONDARY, false);
            return;
        }

        context.drawText(textRenderer, selectedModule.getName(), SETTINGS_X + 10, SETTINGS_Y + 8, NSColors.ACCENT_HOVER, true);
        context.drawText(textRenderer, selectedModule.getDescription(), SETTINGS_X + 10, SETTINGS_Y + 20, NSColors.TEXT_SECONDARY, false);

        int y = SETTINGS_Y + 36;

        // Keybind row
        String keyLabel = awaitingKeybind == selectedModule ? "Press a key..." : describeKey(selectedModule.getKeyCode());
        context.drawText(textRenderer, "Keybind: " + keyLabel, SETTINGS_X + 10, y, NSColors.TEXT_PRIMARY, false);
        y += 16;

        for (Setting<?> setting : selectedModule.getSettings()) {
            y = renderSetting(context, setting, SETTINGS_X + 10, y, SETTINGS_WIDTH - 20);
            y += 6;
        }
    }

    private int renderSetting(DrawContext context, Setting<?> setting, int x, int y, int width) {
        context.drawText(textRenderer, setting.getName(), x, y, NSColors.TEXT_SECONDARY, false);
        y += 11;

        switch (setting.getType()) {
            case BOOLEAN -> {
                boolean value = (Boolean) setting.getValue();
                drawToggle(context, x, y, value);
                return y + 18;
            }
            case SLIDER_FLOAT, SLIDER_INT -> {
                drawSlider(context, setting, x, y, width);
                return y + 18;
            }
            case MODE -> {
                String value = (String) setting.getValue();
                RenderUtil.drawRoundedRect(context, x, y, width, 16, NSColors.TOGGLE_OFF);
                context.drawText(textRenderer, value, x + 6, y + 4, NSColors.TEXT_PRIMARY, false);
                return y + 18;
            }
            case COLOR -> {
                int current = (Integer) setting.getValue();
                RenderUtil.drawRoundedRect(context, x, y, 16, 16, current);
                int sx = x + 22;
                for (int preset : COLOR_PRESETS) {
                    RenderUtil.drawRoundedRect(context, sx, y, 14, 16, preset);
                    sx += 17;
                }
                return y + 18;
            }
            default -> {
                return y + 4;
            }
        }
    }

    private void drawToggle(DrawContext context, int x, int y, boolean on) {
        int w = 28, h = 14;
        RenderUtil.drawRoundedRect(context, x, y, w, h, on ? NSColors.ACCENT : NSColors.TOGGLE_OFF);
        int knobX = on ? x + w - h : x;
        RenderUtil.drawRoundedRect(context, knobX, y, h, h, NSColors.TEXT_PRIMARY);
    }

    private void drawSlider(DrawContext context, Setting<?> setting, int x, int y, int width) {
        float min = setting.getMin();
        float max = setting.getMax();
        float value = setting.getType() == Setting.Type.SLIDER_INT
                ? ((Integer) setting.getValue()).floatValue()
                : ((Float) setting.getValue());
        float pct = max > min ? (value - min) / (max - min) : 0;

        RenderUtil.drawRoundedRect(context, x, y, width, 4, NSColors.TOGGLE_OFF);
        RenderUtil.drawRoundedRect(context, x, y, (int) (width * pct), 4, NSColors.ACCENT);

        int knobX = x + (int) (width * pct) - 3;
        RenderUtil.drawRoundedRect(context, knobX, y - 3, 6, 10, NSColors.TEXT_PRIMARY);

        String valueText = setting.getType() == Setting.Type.SLIDER_INT
                ? String.valueOf((int) value)
                : String.format("%.2f", value);
        context.drawText(textRenderer, valueText, x + width - RenderUtil.textWidth(valueText), y - 10, NSColors.TEXT_SECONDARY, false);
    }

    private String describeKey(int code) {
        if (code < 0) return "None";
        return InputUtil.Type.KEYSYM.createFromCode(code).getLocalizedText().getString();
    }

    private boolean isHovered(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchBox.mouseClicked(mouseX, mouseY, button)) return true;

        // Sidebar
        int y = SIDEBAR_Y + 4;
        for (ModuleCategory cat : ModuleCategory.values()) {
            if (isHovered((int) mouseX, (int) mouseY, SIDEBAR_X + 4, y, SIDEBAR_WIDTH - 8, ROW_HEIGHT - 2)) {
                selectedCategory = cat;
                listScroll = 0;
                return true;
            }
            y += ROW_HEIGHT;
        }

        // Module list
        List<Module> modules = currentModules();
        int panelHeight = height - LIST_Y - 20;
        int ly = LIST_Y + 22 - listScroll;
        for (Module module : modules) {
            if (ly >= LIST_Y + 20 && ly <= LIST_Y + panelHeight) {
                if (isHovered((int) mouseX, (int) mouseY, LIST_X + LIST_WIDTH - 36, ly + 4, 28, 14)) {
                    module.toggle();
                    return true;
                }
                if (isHovered((int) mouseX, (int) mouseY, LIST_X + 6, ly, LIST_WIDTH - 12, ROW_HEIGHT - 2)) {
                    selectedModule = module;
                    return true;
                }
            }
            ly += ROW_HEIGHT;
        }

        // Settings panel interaction
        if (selectedModule != null) {
            handleSettingsClick(mouseX, mouseY);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleSettingsClick(double mouseX, double mouseY) {
        int x = SETTINGS_X + 10;
        int y = SETTINGS_Y + 36;
        int width = SETTINGS_WIDTH - 20;

        // Keybind row
        if (isHovered((int) mouseX, (int) mouseY, x, y, width, 12)) {
            awaitingKeybind = selectedModule;
            return;
        }
        y += 16;

        for (Setting<?> setting : selectedModule.getSettings()) {
            int rowTop = y + 11;
            switch (setting.getType()) {
                case BOOLEAN -> {
                    if (isHovered((int) mouseX, (int) mouseY, x, rowTop, 28, 14)) {
                        @SuppressWarnings("unchecked")
                        Setting<Boolean> b = (Setting<Boolean>) setting;
                        b.setValue(!b.getValue());
                    }
                }
                case SLIDER_FLOAT -> {
                    if (isHovered((int) mouseX, (int) mouseY, x, rowTop - 4, width, 12)) {
                        draggingFloatSlider = (Setting<Float>) setting;
                        sliderTrackX = x;
                        sliderTrackWidth = width;
                        updateFloatSlider(mouseX);
                    }
                }
                case SLIDER_INT -> {
                    if (isHovered((int) mouseX, (int) mouseY, x, rowTop - 4, width, 12)) {
                        draggingIntSlider = (Setting<Integer>) setting;
                        sliderTrackX = x;
                        sliderTrackWidth = width;
                        updateIntSlider(mouseX);
                    }
                }
                case MODE -> {
                    if (isHovered((int) mouseX, (int) mouseY, x, rowTop, width, 16)) {
                        @SuppressWarnings("unchecked")
                        Setting<String> modeSetting = (Setting<String>) setting;
                        List<String> options = modeSetting.getModes();
                        int idx = options.indexOf(modeSetting.getValue());
                        modeSetting.setValue(options.get((idx + 1) % options.size()));
                    }
                }
                case COLOR -> {
                    int sx = x + 22;
                    for (int preset : COLOR_PRESETS) {
                        if (isHovered((int) mouseX, (int) mouseY, sx, rowTop, 14, 16)) {
                            @SuppressWarnings("unchecked")
                            Setting<Integer> colorSetting = (Setting<Integer>) setting;
                            colorSetting.setValue(preset);
                            return;
                        }
                        sx += 17;
                    }
                }
            }
            y += 18 + 6;
        }
    }

    private void updateFloatSlider(double mouseX) {
        if (draggingFloatSlider == null) return;
        float pct = (float) Math.max(0, Math.min(1, (mouseX - sliderTrackX) / sliderTrackWidth));
        float raw = draggingFloatSlider.getMin() + pct * (draggingFloatSlider.getMax() - draggingFloatSlider.getMin());
        float step = draggingFloatSlider.getStep();
        if (step > 0) raw = Math.round(raw / step) * step;
        draggingFloatSlider.setValue(raw);
    }

    private void updateIntSlider(double mouseX) {
        if (draggingIntSlider == null) return;
        float pct = (float) Math.max(0, Math.min(1, (mouseX - sliderTrackX) / sliderTrackWidth));
        int raw = Math.round(draggingIntSlider.getMin() + pct * (draggingIntSlider.getMax() - draggingIntSlider.getMin()));
        draggingIntSlider.setValue(raw);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingFloatSlider != null) {
            updateFloatSlider(mouseX);
            return true;
        }
        if (draggingIntSlider != null) {
            updateIntSlider(mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingFloatSlider = null;
        draggingIntSlider = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isHovered((int) mouseX, (int) mouseY, LIST_X, LIST_Y, LIST_WIDTH, height - LIST_Y - 20)) {
            listScroll = Math.max(0, listScroll - (int) (verticalAmount * ROW_HEIGHT));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (awaitingKeybind != null) {
            awaitingKeybind.setKeyCode(keyCode);
            awaitingKeybind = null;
            return true;
        }
        if (searchBox.isFocused() && searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchBox.isFocused() && searchBox.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
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
