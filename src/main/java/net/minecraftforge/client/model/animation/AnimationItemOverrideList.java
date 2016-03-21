package net.minecraftforge.client.model.animation;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

import com.google.common.base.Function;

public final class AnimationItemOverrideList extends ItemOverrideList
{
    private final IModel model;
    private final IModelState state;
    private final VertexFormat format;
    private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

    public AnimationItemOverrideList(IModel model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, ItemOverrideList overrides)
    {
        this(model, state, format, bakedTextureGetter, overrides.getOverrides());
    }

    public AnimationItemOverrideList(IModel model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, List<ItemOverride> overrides)
    {
        super(overrides);
        this.model = model;
        this.state = state;
        this.format = format;
        this.bakedTextureGetter = bakedTextureGetter;
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
    {
        if(stack.hasCapability(net.minecraftforge.client.model.animation.CapabilityAnimation.ANIMATION_CAPABILITY, null))
        {
            // TODO: caching?
            IAnimationStateMachine asm = stack.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null);
            if(world == null)
            {
                world = entity.worldObj;
            }
            if(world == null)
            {
                world = Minecraft.getMinecraft().theWorld;
            }
            IModelState state = asm.apply(Animation.getWorldTime(world, Animation.getPartialTickTime())).getLeft();
            return model.bake(new ModelStateComposition(state, this.state), format, bakedTextureGetter);
        }
        return originalModel;
    }
}
