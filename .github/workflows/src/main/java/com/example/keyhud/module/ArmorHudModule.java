package com.example.keyhud.module;

import com.example.keyhud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Armor icons with durability percentage; red below the warning threshold. */
public class ArmorHudModule extends Module {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private final NumberSetting warnBelow = new NumberSetting("Warn below %", 20, 0, 100, 5);

    public ArmorHudModule() {
        super("Armor HUD", true, 6, 126);
        settings.add(warnBelow);
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null) return;

        int count = 0;
        for (EquipmentSlot s : SLOTS) if (!mc.player.getEquippedStack(s).isEmpty()) count++;
        if (count == 0) return;

        int w = Hud.PAD_X + 16 + 5 + mc.textRenderer.getWidth("100%") + 5;
        int h = count * 18 + 2;
        int bx = x.asInt(), yy = y.asInt();
        Hud.box(ctx, bx, yy, w, h);

        int ry = yy + 2;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = mc.player.getEquippedStack(slot);
            if (stack.isEmpty()) continue;
            ctx.drawItem(stack, bx + Hud.PAD_X - 2, ry);
            if (stack.isDamageable()) {
                int max = stack.getMaxDamage();
                int pct = (int) (100.0 * (max - stack.getDamage()) / max);
                int color = pct <= warnBelow.asInt() ? Hud.BAD
                        : pct <= 50 ? 0xFFFFAA00 : 0xFF55FF88;
                String t = pct + "%";
                ctx.drawText(mc.textRenderer, t, bx + w - 5 - mc.textRenderer.getWidth(t),
                        ry + 4, color, !Hud.panels());
            }
            ry += 18;
        }
    }
}
