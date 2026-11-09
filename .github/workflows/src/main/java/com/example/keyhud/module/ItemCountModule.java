package com.example.keyhud.module;

import com.example.keyhud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Totems, end crystals, golden apples and obsidian you are carrying. */
public class ItemCountModule extends Module {
    private final NumberSetting lowWarn = new NumberSetting("Low warning at", 2, 0, 10, 1);

    public ItemCountModule() {
        super("Item Counter", true, 6, 66);
        settings.add(lowWarn);
    }

    private static int count(MinecraftClient mc, Item item) {
        var inv = mc.player.getInventory();
        int total = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (s.isOf(item)) total += s.getCount();
        }
        return total;
    }

    private Hud.Row row(MinecraftClient mc, String label, Item item) {
        int n = count(mc, item);
        return new Hud.Row(label, String.valueOf(n), n <= lowWarn.asInt() ? Hud.BAD : Hud.VALUE);
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null) return;
        Hud.draw(ctx, mc, x.asInt(), y.asInt(),
                row(mc, "Totems", Items.TOTEM_OF_UNDYING),
                row(mc, "Crystals", Items.END_CRYSTAL),
                row(mc, "Gapples", Items.ENCHANTED_GOLDEN_APPLE),
                row(mc, "Obsidian", Items.OBSIDIAN));
    }
}
