package com.example.keyhud.module;

import com.example.keyhud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;

/** WASD cluster; pressed keys light up in the accent color. */
public class KeysModule extends Module {
    private static final int SIZE = 15, GAP = 2;

    public KeysModule() { super("Keys", true, 6, 28); }

    private void key(DrawContext ctx, MinecraftClient mc, String label, KeyBinding kb, int kx, int ky) {
        boolean down = kb.isPressed();
        int bg = down ? (Hud.accent() & 0x00FFFFFF) | 0xCC000000 : Hud.background();
        ctx.fill(kx + 1, ky, kx + SIZE - 1, ky + SIZE, bg);
        ctx.fill(kx, ky + 1, kx + SIZE, ky + SIZE - 1, bg);
        int tw = mc.textRenderer.getWidth(label);
        ctx.drawText(mc.textRenderer, label, kx + (SIZE - tw) / 2, ky + (SIZE - 8) / 2,
                down ? 0xFF101010 : Hud.LABEL, false);
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        var o = mc.options;
        int bx = x.asInt(), by = y.asInt();
        key(ctx, mc, "W", o.forwardKey, bx + SIZE + GAP, by);
        key(ctx, mc, "A", o.leftKey, bx, by + SIZE + GAP);
        key(ctx, mc, "S", o.backKey, bx + SIZE + GAP, by + SIZE + GAP);
        key(ctx, mc, "D", o.rightKey, bx + (SIZE + GAP) * 2, by + SIZE + GAP);
    }
}
