package com.example.keyhud;

import com.example.keyhud.gui.ClickGuiScreen;
import com.example.keyhud.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyHudClient implements ClientModInitializer {
    private static KeyBinding openGui;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        // 1.21.9+ requires a KeyBinding.Category instead of a plain string.
        openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.keyhud.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of("keyhud", "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openGui.wasPressed()) mc.setScreen(new ClickGuiScreen());
            ModuleManager.tick(mc);
        });

        HudElementRegistry.addLast(Identifier.of("keyhud", "hud"),
                (ctx, tick) -> ModuleManager.render(ctx, MinecraftClient.getInstance()));
    }
}
