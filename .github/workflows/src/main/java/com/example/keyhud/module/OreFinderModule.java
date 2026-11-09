package com.example.keyhud.module;

import com.example.keyhud.Hud;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

/** Singleplayer only: does nothing on multiplayer servers. */
public class OreFinderModule extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 16, 4, 32, 1);
    private BlockPos emerald, coal;
    private int cooldown;

    public OreFinderModule() {
        super("Ore Finder (SP)", false, 110, 6);
        settings.add(radius);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!mc.isInSingleplayer() || mc.player == null || mc.world == null) {
            emerald = null; coal = null;
            return;
        }
        if (--cooldown > 0) return;
        cooldown = 10;
        scan(mc);
    }

    private void scan(MinecraftClient mc) {
        BlockPos o = mc.player.getBlockPos();
        BlockPos.Mutable p = new BlockPos.Mutable();
        int r = radius.asInt();
        double bestE = Double.MAX_VALUE, bestC = Double.MAX_VALUE;
        emerald = null; coal = null;
        for (int dx = -r; dx <= r; dx++)
            for (int dy = -r; dy <= r; dy++)
                for (int dz = -r; dz <= r; dz++) {
                    p.set(o.getX() + dx, o.getY() + dy, o.getZ() + dz);
                    Block b = mc.world.getBlockState(p).getBlock();
                    if (b == Blocks.DEEPSLATE_EMERALD_ORE) {
                        double d = o.getSquaredDistance(p);
                        if (d < bestE) { bestE = d; emerald = p.toImmutable(); }
                    } else if (b == Blocks.DEEPSLATE_COAL_ORE) {
                        double d = o.getSquaredDistance(p);
                        if (d < bestC) { bestC = d; coal = p.toImmutable(); }
                    }
                }
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        if (!mc.isInSingleplayer() || mc.player == null) return;
        BlockPos o = mc.player.getBlockPos();
        java.util.List<Hud.Row> rows = new java.util.ArrayList<>();
        if (emerald != null)
            rows.add(new Hud.Row("Emerald", emerald.toShortString() + "  "
                    + (int) Math.sqrt(o.getSquaredDistance(emerald)) + "m", 0xFF55FF88));
        if (coal != null)
            rows.add(new Hud.Row("Coal", coal.toShortString() + "  "
                    + (int) Math.sqrt(o.getSquaredDistance(coal)) + "m"));
        Hud.draw(ctx, mc, x.asInt(), y.asInt(), rows.toArray(new Hud.Row[0]));
    }
}
