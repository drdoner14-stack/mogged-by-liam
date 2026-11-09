package com.example.keyhud.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public boolean enabled;
    public final NumberSetting x, y;
    public final List<NumberSetting> settings = new ArrayList<>();
    public final List<StringSetting> stringSettings = new ArrayList<>();

    protected Module(String name, boolean enabledByDefault, int defaultX, int defaultY) {
        this.name = name;
        this.enabled = enabledByDefault;
        this.x = new NumberSetting("HUD X", defaultX, 0, 800, 1);
        this.y = new NumberSetting("HUD Y", defaultY, 0, 500, 1);
        settings.add(x);
        settings.add(y);
    }

    public void toggle() { enabled = !enabled; }

    /** Called every client tick while enabled. */
    public void onTick(MinecraftClient mc) {}

    /** Called every HUD frame while enabled. */
    public void onRender(DrawContext ctx, MinecraftClient mc) {}
}
