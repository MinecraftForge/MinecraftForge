/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.world.biome.Biome;

@SuppressWarnings("deprecation")
public abstract class LanguageProvider implements IDataProvider {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, String> data = new TreeMap<>();
    private final DataGenerator gen;
    private final String modid;
    private final String locale;

    public LanguageProvider(DataGenerator gen, String modid, String locale) {
        this.gen = gen;
        this.modid = modid;
        this.locale = locale;
    }

    protected abstract void addTranslations();

    @Override
    public void act(DirectoryCache cache) throws IOException {
        addTranslations();
        if (!data.isEmpty())
            save(cache, data, this.gen.getOutputFolder().resolve("assets/" + modid + "/lang/" + locale + ".json"));
    }

    @Override
    public String getName() {
        return "Languages: " + locale;
    }

    private void save(DirectoryCache cache, Object object, Path target) throws IOException {
        String data = GSON.toJson(object);
        data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data); // Escape unicode after the fact so that it's not double escaped by GSON
        String hash = IDataProvider.HASH_FUNCTION.hashUnencodedChars(data).toString();
        if (!Objects.equals(cache.getPreviousHash(target), hash) || !Files.exists(target)) {
           Files.createDirectories(target.getParent());

           try (BufferedWriter bufferedwriter = Files.newBufferedWriter(target)) {
              bufferedwriter.write(data);
           }
        }

        cache.func_208316_a(target, hash);
    }

    protected void addBlock(Supplier<? extends Block> key, String name) {
        add(key.get(), name);
    }

    protected void add(Block key, String name) {
        add(key.getTranslationKey(), name);
    }

    protected void addItem(Supplier<? extends Item> key, String name) {
        add(key.get(), name);
    }

    protected void add(Item key, String name) {
        add(key.getTranslationKey(), name);
    }

    protected void addItemStack(Supplier<ItemStack> key, String name) {
        add(key.get(), name);
    }

    protected void add(ItemStack key, String name) {
        add(key.getTranslationKey(), name);
    }

    protected void addEnchantment(Supplier<? extends Enchantment> key, String name) {
        add(key.get(), name);
    }

    protected void add(Enchantment key, String name) {
        add(key.getName(), name);
    }

    protected void addBiome(Supplier<? extends Biome> key, String name) {
        add(key.get(), name);
    }

    protected void add(Biome key, String name) {
        add(key.getTranslationKey(), name);
    }

    protected void addEffect(Supplier<? extends Effect> key, String name) {
        add(key.get(), name);
    }

    protected void add(Effect key, String name) {
        add(key.getName(), name);
    }

    protected void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
        add(key.get(), name);
    }

    protected void add(EntityType<?> key, String name) {
        add(key.getTranslationKey(), name);
    }

    protected void add(String key, String value) {
        if (data.put(key, value) != null)
            throw new IllegalStateException("Duplicate translation key " + key);
    }
}
