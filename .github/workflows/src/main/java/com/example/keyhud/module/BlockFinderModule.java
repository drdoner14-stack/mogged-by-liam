package com.example.keyhud.module;

import com.example.keyhud.Hud;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/** Singleplayer only: finds the nearest block of a type you choose (e.g. "diamond_ore"). */
public class BlockFinderModule extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 16, 4, 32, 1);
    private final StringSetting blockId = new StringSetting("Block ID", "minecraft:diamond_ore");

    private String parsedFrom = null;
    private Block target = Blocks.AIR;
    private BlockPos nearest;
    private int cooldown;

    public BlockFinderModule() {
        super("Block Finder (SP)", false, 110, 60);
        settings.add(radius);
        stringSettings.add(blockId);
    }

    private void resolveTarget() {
        if (blockId.value.equals(parsedFrom)) return;
        parsedFrom = blockId.value;
        Identifier id = Identifier.tryParse(blockId.value.trim().toLowerCase());
        target = id == null ? Blocks.AIR : Registries.BLOCK.get(id); // unknown IDs fall back to AIR
        nearest = null;
        cooldown = 0;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!mc.isInSingleplayer() || mc.player == null || mc.world == null) {
            nearest = null;
            return;
        }
        resolveTarget();
        if (target == Blocks.AIR) { nearest = null; return; }
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
                    if (mc.world.getBlockState(p).isOf(target)) {
                        double d = o.getSquaredDistance(p);
                        if (d < best) { best = d; nearest = p.toImmutable(); }
                    }
                }
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        if (!mc.isInSingleplayer() || mc.player == null) return;
        if (target == Blocks.AIR) {
            Hud.draw(ctx, mc, x.asInt(), y.asInt(), new Hud.Row("Block Finder", "unknown ID", Hud.BAD));
            return;
        }
        if (nearest == null) return;
        int dist = (int) Math.sqrt(mc.player.getBlockPos().getSquaredDistance(nearest));
        Hud.draw(ctx, mc, x.asInt(), y.asInt(),
                new Hud.Row(target.getName().getString(), nearest.toShortString() + "  " + dist + "m"));
    }
}
