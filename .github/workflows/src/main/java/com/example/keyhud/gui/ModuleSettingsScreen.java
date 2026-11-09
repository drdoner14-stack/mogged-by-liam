package com.example.keyhud.gui;

import com.example.keyhud.module.Module;
import com.example.keyhud.module.ModuleManager;
import com.example.keyhud.module.NumberSetting;
import com.example.keyhud.module.StringSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModuleSettingsScreen extends Screen {
    private final Screen parent;
    private final Module module;
    private final List<Label> labels = new ArrayList<>();

    private record Label(String text, int x, int y) {}

    public ModuleSettingsScreen(Screen parent, Module module) {
        super(Text.literal(module.name + " Settings"));
        this.parent = parent;
        this.module = module;
    }

    @Override
    protected void init() {
        labels.clear();
        int w = 180, left = (width - w) / 2;
        int rows = module.settings.size() + module.stringSettings.size() * 2;
        int top = height / 2 - rows * 12 - 10;
        int yy = top;

        for (NumberSetting s : module.settings) {
            addDrawableChild(new NumberSlider(left, yy, w, s));
            yy += 24;
        }
        for (StringSetting s : module.stringSettings) {
            labels.add(new Label(s.name, left, yy));
            yy += 12;
            TextFieldWidget field = new TextFieldWidget(textRenderer, left, yy, w, 20, Text.literal(s.name));
            field.setMaxLength(128);
            field.setText(s.value);
            field.setChangedListener(v -> s.value = v);
            addDrawableChild(field);
            yy += 28;
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
                .dimensions(width / 2 - 50, yy + 8, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        for (Label l : labels)
            ctx.drawTextWithShadow(textRenderer, l.text(), l.x(), l.y(), 0xFFFFFFFF);
    }

    @Override
    public void close() { client.setScreen(parent); }

    @Override
    public void removed() { ModuleManager.save(); }

    private static class NumberSlider extends SliderWidget {
        private final NumberSetting s;

        NumberSlider(int x, int y, int w, NumberSetting s) {
            super(x, y, w, 20, Text.empty(), (s.value - s.min) / (s.max - s.min));
            this.s = s;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(s.name + ": " + (int) s.value));
        }

        @Override
        protected void applyValue() {
            s.set(s.min + value * (s.max - s.min));
        }
    }
}
