package com.nsclient.util;

/** Central palette for the "Modern Purple" theme, ARGB ints. */
public final class NSColors {
    private NSColors() {
    }

    public static final int BG_PANEL = 0xE0141018;        // near-black, slightly transparent
    public static final int BG_PANEL_LIGHT = 0xE01C1624;
    public static final int BG_SIDEBAR = 0xF00E0B14;
    public static final int ACCENT = 0xFF9B4DFF;           // neon purple
    public static final int ACCENT_DIM = 0x809B4DFF;
    public static final int ACCENT_HOVER = 0xFFB37BFF;
    public static final int TEXT_PRIMARY = 0xFFEDE8F5;
    public static final int TEXT_SECONDARY = 0xFF9A90AE;
    public static final int TOGGLE_OFF = 0xFF2A2433;
    public static final int HUD_CHIP_BG = 0xA0120C1A;
    public static final int HITMARKER = 0xFFD9B8FF;
    public static final int CRIT_PARTICLE = 0xFFB37BFF;
    public static final int HIT_PARTICLE = 0xFF6E4AA8;
    public static final int KILL_PARTICLE = 0xFFE8D4FF;

    public static int withAlpha(int argb, int alpha) {
        return (argb & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }
}
