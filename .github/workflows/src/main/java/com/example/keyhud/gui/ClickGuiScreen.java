package com.example.keyhud.gui;

import com.example.keyhud.module.Module;
import com.example.keyhud.module.ModuleManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {
    public ClickGuiScreen() { super(Text.literal(com.example.keyhud.Branding.NAME)); }

    @Override
    protected void init() {
        int rowW = 150, gear = 20, gap = 4;
        int left = (width - (rowW + gap + gear)) / 2;
        int top = height / 2 - ModuleManager.MODULES.size() * 12 - 10;
        int i = 0;
        for (Module m : ModuleManager.MODULES) {
            int yy = top + i++ * 24;
            ButtonWidget toggle = ButtonWidget.builder(label(m), b -> {
                m.toggle();
                b.setMessage(label(m));
            }).dimensions(left, yy, rowW, 20).build();
            addDrawableChild(toggle);
            addDrawableChild(ButtonWidget.builder(Text.literal("⚙"),
                    b -> client.setScreen(new ModuleSettingsScreen(this, m)))
                    .dimensions(left + rowW + gap, yy, gear, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(width / 2 - 50, top + i * 24 + 8, 100, 20).build());
    }

    private static Text label(Module m) {
        return Text.literal(m.name + ": " + (m.enabled ? "ON" : "OFF"));
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFAA00);
    }

    @Override
    public void removed() { ModuleManager.save(); }
}
