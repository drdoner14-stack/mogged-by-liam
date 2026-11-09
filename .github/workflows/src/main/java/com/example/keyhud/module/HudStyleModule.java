package com.example.keyhud.module;

/** Global HUD look. Enabled = panels with backgrounds; disabled = plain shadowed text. */
public class HudStyleModule extends Module {
    public static HudStyleModule INSTANCE;

    private final NumberSetting accent = new NumberSetting("Accent (0-5)", 0, 0, 5, 1);
    private final NumberSetting opacity = new NumberSetting("Background %", 55, 0, 100, 5);

    public HudStyleModule() {
        super("HUD Style", true, 0, 0);
        settings.clear(); // no X/Y: this module draws nothing itself
        settings.add(accent);
        settings.add(opacity);
        INSTANCE = this;
    }

    public int accentIndex() { return accent.asInt(); }
    public int opacityPercent() { return opacity.asInt(); }
}
