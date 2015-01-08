package net.minecraftforge.client.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when the ModelManager is notified of the resource manager reloading.
 * Called before model registry is setup.
 */
public class ModelPreRegisterEvent extends Event
{
    public final ModelManager modelManager;
    public final ModelBakery modelBakery;
    public final TextureMap blockTextureMap;
    public final BlockModelShapes blockModelShapes;

    public ModelPreRegisterEvent(ModelManager modelManager, ModelBakery modelBakery, TextureMap blockTextureMap, BlockModelShapes blockModelShapes)
    {
        this.modelManager = modelManager;
        this.modelBakery = modelBakery;
        this.blockTextureMap = blockTextureMap;
        this.blockModelShapes = blockModelShapes;
    }
}
