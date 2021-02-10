package net.minecraftforge.registries.injection;

import com.google.gson.JsonElement;
import net.minecraft.util.RegistryKey;

import java.io.IOException;

public interface Injector<E> {

    void inject(RegistryKey<E> entryKey, JsonElement entryData) throws IOException;
}
