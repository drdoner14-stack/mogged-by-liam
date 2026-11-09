package com.example.keyhud;

import com.example.keyhud.module.HudStyleModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Shared look for every HUD element: translucent rounded panel, accent bar, aligned columns. */
public final class Hud {
    public static final int[] ACCENTS = {
            0xFFFFAA00, 0xFF55AAFF, 0xFFAA77FF, 0xFF55FF88, 0xFFFF5566, 0xFFFFFFFF
    };
    public static final int PAD_X = 7, PAD_Y = 3, LINE = 11, GAP = 10;
    public static final int LABEL = 0xFFAAAAAA, VALUE = 0xFFFFFFFF, BAD = 0xFFFF5555;

    public record Row(String label, String value, int color) {
        public Row(String label, String value) { this(label, value, VALUE); }
    }

    private Hud() {}

    public static int accent() {
        HudStyleModule t = HudStyleModule.INSTANCE;
        return t == null ? ACCENTS[0] : ACCENTS[t.accentIndex()];
    }

    public static boolean panels() {
        HudStyleModule t = HudStyleModule.INSTANCE;
        return t == null || t.enabled;
    }

    public static int background() {
        HudStyleModule t = HudStyleModule.INSTANCE;
        int pct = t == null ? 55 : t.opacityPercent();
        return ((int) (pct * 2.55) << 24) | 0x0E0E10;
    }

    /** Panel with softly rounded corners and a thin accent bar on its left edge. */
    public static void box(DrawContext ctx, int x, int y, int w, int h) {
        if (!panels()) return;
        int bg = background();
        ctx.fill(x + 1, y, x + w - 1, y + h, bg);
        ctx.fill(x, y + 1, x + w, y + h - 1, bg);
        ctx.fill(x, y + 1, x + 2, y + h - 1, accent());
    }

    /** Draws a panel of label/value rows with the values right-aligned. */
    public static void draw(DrawContext ctx, MinecraftClient mc, int x, int y, Row... rows) {
        if (rows.length == 0) return;
        int inner = 0;
        for (Row r : rows) {
            inner = Math.max(inner, mc.textRenderer.getWidth(r.label())
                    + GAP + mc.textRenderer.getWidth(r.value()));
        }
        int w = inner + PAD_X + 5;
        int h = rows.length * LINE + PAD_Y * 2 - 2;
        box(ctx, x, y, w, h);

        boolean shadow = !panels();
        int ty = y + PAD_Y;
        for (Row r : rows) {
            ctx.drawText(mc.textRenderer, r.label(), x + PAD_X, ty, LABEL, shadow);
            int vx = x + w - 5 - mc.textRenderer.getWidth(r.value());
            ctx.drawText(mc.textRenderer, r.value(), vx, ty, r.color(), shadow);
            ty += LINE;
        }
    }
}
