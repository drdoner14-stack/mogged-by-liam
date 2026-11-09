package com.example.keyhud.module;

import com.example.keyhud.Hud;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleplayer only. Looks through the chunks you have loaded for clusters of storage and
 * utility blocks (chests, barrels, furnaces, beds, shulkers...) and reports the biggest cluster,
 * which is usually a base. Villages and other structures can show up too.
 */
public class BaseFinderModule extends Module {
    private final NumberSetting minBlocks = new NumberSetting("Min blocks", 8, 3, 40, 1);

    private int bestX, bestZ, bestScore;
    private boolean found;
    private int cooldown;

    public BaseFinderModule() {
        super("Base Finder (SP)", false, 110, 82);
        settings.add(minBlocks);
    }

    private static boolean isIndicator(BlockState s) {
        return s.isOf(Blocks.CHEST) || s.isOf(Blocks.TRAPPED_CHEST) || s.isOf(Blocks.BARREL)
                || s.isOf(Blocks.FURNACE) || s.isOf(Blocks.BLAST_FURNACE) || s.isOf(Blocks.SMOKER)
                || s.isOf(Blocks.ENDER_CHEST) || s.isOf(Blocks.ENCHANTING_TABLE)
                || s.isOf(Blocks.BREWING_STAND) || s.isIn(BlockTags.BEDS)
                || s.isIn(BlockTags.SHULKER_BOXES);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!mc.isInSingleplayer() || mc.player == null || mc.world == null) {
            found = false;
            return;
        }
        if (--cooldown > 0) return;
        cooldown = 40;

        int view = mc.options.getViewDistance().getValue();
        ChunkPos center = mc.player.getChunkPos();
        Map<Long, Integer> scores = new HashMap<>();

        for (int cx = center.x - view; cx <= center.x + view; cx++) {
            for (int cz = center.z - view; cz <= center.z + view; cz++) {
                Chunk chunk = mc.world.getChunk(cx, cz, ChunkStatus.FULL, false);
                if (chunk == null) continue;
                int n = 0;
                for (BlockPos pos : chunk.getBlockEntityPositions()) {
                    if (isIndicator(mc.world.getBlockState(pos))) n++;
                }
                if (n > 0) scores.put(ChunkPos.toLong(cx, cz), n);
            }
        }

        // A base often spans a few chunks, so score each chunk with its 8 neighbours.
        found = false;
        bestScore = 0;
        for (long key : scores.keySet()) {
            int cx = ChunkPos.getPackedX(key), cz = ChunkPos.getPackedZ(key);
            int sum = 0;
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++)
                    sum += scores.getOrDefault(ChunkPos.toLong(cx + dx, cz + dz), 0);
            if (sum >= minBlocks.asInt() && sum > bestScore) {
                bestScore = sum;
                bestX = cx * 16 + 8;
                bestZ = cz * 16 + 8;
                found = true;
            }
        }
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        if (!mc.isInSingleplayer() || mc.player == null || !found) return;
        double dx = bestX - mc.player.getX(), dz = bestZ - mc.player.getZ();
        int dist = (int) Math.sqrt(dx * dx + dz * dz);
        Hud.draw(ctx, mc, x.asInt(), y.asInt(),
                new Hud.Row("Base", bestX + ", " + bestZ + "  " + dist + "m", 0xFF55AAFF),
                new Hud.Row("Storage blocks", String.valueOf(bestScore)));
    }
}
