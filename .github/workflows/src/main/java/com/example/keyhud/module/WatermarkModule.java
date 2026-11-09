package com.example.keyhud.module;

import com.example.keyhud.Branding;
import com.example.keyhud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** "Mogged by Liam" pinned to the left or right edge of the screen. */
public class WatermarkModule extends Module {
    private final NumberSetting side = new NumberSetting("Side (0 left, 1 right)", 1, 0, 1, 1);
    private final NumberSetting margin = new NumberSetting("Margin", 6, 0, 60, 1);
    private final NumberSetting height = new NumberSetting("Height %", 50, 0, 100, 1);

    public WatermarkModule() {
        super("Watermark", true, 0, 0);
        settings.clear();
        settings.add(side);
        settings.add(margin);
        settings.add(height);
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        int textW = mc.textRenderer.getWidth(Branding.NAME);
        int boxW = textW + Hud.PAD_X + 5;
        int boxH = Hud.LINE + Hud.PAD_Y * 2 - 2;
        int sw = mc.getWindow().getScaledWidth(), sh = mc.getWindow().getScaledHeight();

        int px = side.asInt() == 1 ? sw - boxW - margin.asInt() : margin.asInt();
        int py = (int) ((sh - boxH) * (height.value / 100.0));

        Hud.box(ctx, px, py, boxW, boxH);
        ctx.drawText(mc.textRenderer, Branding.NAME, px + Hud.PAD_X, py + Hud.PAD_Y,
                Hud.accent(), !Hud.panels());
    }
}
