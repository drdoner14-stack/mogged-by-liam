package com.example.keyhud.module;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * While you hold attack on a block, swaps to the hotbar slot that mines it fastest.
 * Block breaking only; some servers disallow automatic hotbar switching, so check the rules.
 */
public class AutoToolModule extends Module {
    private final NumberSetting switchBack = new NumberSetting("Switch back (0/1)", 1, 0, 1, 1);
    private int previousSlot = -1;

    public AutoToolModule() {
        super("Auto Tool", false, 5, 41);
        settings.add(switchBack);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

        boolean mining = mc.options.attackKey.isPressed()
                && mc.crosshairTarget != null
                && mc.crosshairTarget.getType() == HitResult.Type.BLOCK;

        if (!mining || mc.player.isCreative()) {
            restore(mc);
            return;
        }

        BlockState state = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos());
        if (state.isAir()) return;

        var inv = mc.player.getInventory();
        int current = inv.getSelectedSlot();
        int best = current;
        float bestSpeed = inv.getStack(current).getMiningSpeedMultiplier(state);

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = inv.getStack(slot);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) { bestSpeed = speed; best = slot; }
        }

        if (best != current) {
            if (previousSlot == -1) previousSlot = current;
            inv.setSelectedSlot(best);
        }
    }

    private void restore(MinecraftClient mc) {
        if (previousSlot != -1 && mc.player != null && switchBack.asInt() == 1) {
            mc.player.getInventory().setSelectedSlot(previousSlot);
        }
        previousSlot = -1;
    }
}
