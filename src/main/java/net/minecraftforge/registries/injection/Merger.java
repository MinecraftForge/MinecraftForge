package net.minecraftforge.registries.injection;

import com.google.gson.JsonElement;

import java.io.IOException;

public interface Merger {

    void merge(JsonElement dest, JsonElement src) throws IOException;
}
