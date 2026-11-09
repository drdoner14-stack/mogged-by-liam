package com.example.keyhud.module;

import com.example.keyhud.Hud;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

/** Singleplayer only: does nothing on multiplayer servers. */
public class SpawnerFinderModule extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 24, 8, 32, 1);
    private BlockPos nearest;
    private int cooldown;

    public SpawnerFinderModule() {
        super("Spawner Finder (SP)", false, 110, 38);
        settings.add(radius);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!mc.isInSingleplayer() || mc.player == null || mc.world == null) {
            nearest = null;
            return;
        }
        if (--cooldown > 0) return;
        cooldown = 20;

        BlockPos o = mc.player.getBlockPos();
        BlockPos.Mutable p = new BlockPos.Mutable();
        int r = radius.asInt();
        double best = Double.MAX_VALUE;
        nearest = null;
        for (int dx = -r; dx <= r; dx++)
            for (int dy = -r; dy <= r; dy++)
                for (int dz = -r; dz <= r; dz++) {
                    p.set(o.getX() + dx, o.getY() + dy, o.getZ() + dz);
                    if (mc.world.getBlockState(p).isOf(Blocks.SPAWNER)) {
                        double d = o.getSquaredDistance(p);
                        if (d < best) { best = d; nearest = p.toImmutable(); }
                    }
                }
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        if (!mc.isInSingleplayer() || mc.player == null || nearest == null) return;
        int dist = (int) Math.sqrt(mc.player.getBlockPos().getSquaredDistance(nearest));
        Hud.draw(ctx, mc, x.asInt(), y.asInt(),
                new Hud.Row("Spawner", nearest.toShortString() + "  " + dist + "m"));
    }
}
