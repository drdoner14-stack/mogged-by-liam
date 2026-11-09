package com.example.keyhud.module;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> MODULES = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("moggedbyliam.json");

    public static void init() {
        MODULES.add(new HudStyleModule());
        MODULES.add(new WatermarkModule());
        MODULES.add(new CpsModule());
        MODULES.add(new KeysModule());
        MODULES.add(new OreFinderModule());
        MODULES.add(new SpawnerFinderModule());
        MODULES.add(new AutoToolModule());
        MODULES.add(new BlockFinderModule());
        MODULES.add(new BaseFinderModule());
        MODULES.add(new ItemCountModule());
        MODULES.add(new ArmorHudModule());
        load();
    }

    public static void tick(MinecraftClient mc) {
        for (Module m : MODULES) if (m.enabled) m.onTick(mc);
    }

    public static void render(DrawContext ctx, MinecraftClient mc) {
        if (mc.options.hudHidden) return;
        for (Module m : MODULES) if (m.enabled) m.onRender(ctx, mc);
    }

    public static void save() {
        JsonObject root = new JsonObject();
        for (Module m : MODULES) {
            JsonObject o = new JsonObject();
            o.addProperty("enabled", m.enabled);
            for (NumberSetting s : m.settings) o.addProperty(s.name, s.value);
            for (StringSetting s : m.stringSettings) o.addProperty(s.name, s.value);
            root.add(m.name, o);
        }
        try {
            Files.writeString(FILE, GSON.toJson(root));
        } catch (Exception e) {
            System.err.println("[Mogged by Liam] failed to save config: " + e);
        }
    }

    private static void load() {
        if (!Files.exists(FILE)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            for (Module m : MODULES) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                if (o.has("enabled")) m.enabled = o.get("enabled").getAsBoolean();
                for (NumberSetting s : m.settings)
                    if (o.has(s.name)) s.set(o.get(s.name).getAsDouble());
                for (StringSetting s : m.stringSettings)
                    if (o.has(s.name)) s.value = o.get(s.name).getAsString();
            }
        } catch (Exception e) {
            System.err.println("[Mogged by Liam] failed to load config: " + e);
        }
    }
}
