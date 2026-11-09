package com.example.keyhud.module;

import com.example.keyhud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;

public class CpsModule extends Module {
    private final ArrayDeque<Long> left = new ArrayDeque<>(), right = new ArrayDeque<>();
    private boolean leftDown, rightDown;

    public CpsModule() { super("CPS", true, 6, 6); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.getWindow() == null) return;
        long w = mc.getWindow().getHandle();
        long now = System.currentTimeMillis();
        boolean l = GLFW.glfwGetMouseButton(w, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(w, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (l && !leftDown) left.add(now);
        if (r && !rightDown) right.add(now);
        leftDown = l; rightDown = r;
        while (!left.isEmpty() && now - left.peek() > 1000) left.poll();
        while (!right.isEmpty() && now - right.peek() > 1000) right.poll();
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        Hud.draw(ctx, mc, x.asInt(), y.asInt(),
                new Hud.Row("CPS", left.size() + " | " + right.size()));
    }
}
