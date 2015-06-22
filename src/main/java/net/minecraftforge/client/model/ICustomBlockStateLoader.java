package net.minecraftforge.client.model;

import java.io.Reader;

import net.minecraft.client.renderer.block.model.ModelBlockDefinition;

import com.google.gson.Gson;

public interface ICustomBlockStateLoader
{
    public ModelBlockDefinition load(Reader reader, Gson vanillaGSON);
}
