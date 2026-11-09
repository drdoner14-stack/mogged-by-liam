package com.example.keyhud.module;

public class NumberSetting {
    public final String name;
    public final double min, max, step;
    public double value;

    public NumberSetting(String name, double value, double min, double max, double step) {
        this.name = name; this.min = min; this.max = max; this.step = step;
        this.value = value;
    }

    public void set(double v) {
        v = Math.round(v / step) * step;
        value = Math.max(min, Math.min(max, v));
    }

    public int asInt() { return (int) value; }
}
