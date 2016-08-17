package net.minecraftforge.client.event;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader.VanillaModelWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Events fired when certain methods are called on vanilla json model hierachy.
 * Use in conjunction with capabilities to add new functionality to vanila jsons. 
 */
public class VanillaModelWrapperEvent extends Event
{

    private final VanillaModelWrapper modelWrapper;

    public VanillaModelWrapperEvent(VanillaModelWrapper modelWrapper)
    {
        this.modelWrapper = modelWrapper;
    }

    public VanillaModelWrapper getModelWrapper()
    {
        return modelWrapper;
    }

    public static class GetDependencies extends VanillaModelWrapperEvent
    {

        private final Collection<ResourceLocation> dependecies;

        public GetDependencies(VanillaModelWrapper modelWrapper, Collection<ResourceLocation> dependecies)
        {
            super(modelWrapper);
            this.dependecies = dependecies;
        }

        public Collection<ResourceLocation> getDependecies()
        {
            return dependecies;
        }

    }

    public static class GetTextures extends VanillaModelWrapperEvent
    {

        private final ImmutableSet.Builder<ResourceLocation> textures;

        public GetTextures(VanillaModelWrapper modelWrapper, ImmutableSet.Builder<ResourceLocation> textures)
        {
            super(modelWrapper);
            this.textures = textures;
        }

        public ImmutableSet.Builder<ResourceLocation> getTextures()
        {
            return textures;
        }

    }

    public static class Bake extends VanillaModelWrapperEvent {

        private final ModelBlock model;
        private final IModelState perState;
        private final IModelState modelState;
        private final List<TRSRTransformation> newTransforms;
        private final VertexFormat format;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
        private final boolean uvLocked;

        public Bake(VanillaModelWrapper modelWrapper, ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked)
        {
            super(modelWrapper);
            this.model = model;
            this.perState = perState;
            this.modelState = modelState;
            this.newTransforms = newTransforms;
            this.format = format;
            this.bakedTextureGetter = bakedTextureGetter;
            this.uvLocked = uvLocked;
        }

        public ModelBlock getModel()
        {
            return model;
        }

        public IModelState getPerState()
        {
            return perState;
        }

        public IModelState getModelState()
        {
            return modelState;
        }

        public List<TRSRTransformation> getNewTransforms()
        {
            return newTransforms;
        }

        public VertexFormat getFormat()
        {
            return format;
        }

        public Function<ResourceLocation, TextureAtlasSprite> getBakedTextureGetter()
        {
            return bakedTextureGetter;
        }

        public boolean isUvLocked()
        {
            return uvLocked;
        }

        public static class Model extends VanillaModelWrapperEvent.Bake {

            private TRSRTransformation baseState;
            private TextureAtlasSprite particle;
            private SimpleBakedModel.Builder builder;

            public Model(VanillaModelWrapper modelWrapper, ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked, TRSRTransformation baseState, TextureAtlasSprite particle, SimpleBakedModel.Builder builder)
            {
                super(modelWrapper, model, modelState, modelState, newTransforms, format, bakedTextureGetter, uvLocked);
                this.baseState = baseState;
                this.particle = particle;
                this.builder = builder;
            }

            public TRSRTransformation getBaseState()
            {
                return baseState;
            }

            public TextureAtlasSprite getParticle()
            {
                return particle;
            }

            public SimpleBakedModel.Builder getBuilder()
            {
                return builder;
            }

            public void setBaseState(TRSRTransformation baseState)
            {
                this.baseState = baseState;
            }

            public void setParticle(TextureAtlasSprite particle)
            {
                this.particle = particle;
            }

            public void setBuilder(SimpleBakedModel.Builder builder)
            {
                this.builder = builder;
            }

            public static class Pre extends VanillaModelWrapperEvent.Bake.Model {

                public Pre(VanillaModelWrapper modelWrapper, ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked, TRSRTransformation baseState, TextureAtlasSprite particle, SimpleBakedModel.Builder builder)
                {
                    super(modelWrapper, model, perState, modelState, newTransforms, format, bakedTextureGetter, uvLocked, baseState, particle, builder);
                }

            }

            public static class Post extends VanillaModelWrapperEvent.Bake.Model {

                public Post(VanillaModelWrapper modelWrapper, ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked, TRSRTransformation baseState, TextureAtlasSprite particle, SimpleBakedModel.Builder builder)
                {
                    super(modelWrapper, model, perState, modelState, newTransforms, format, bakedTextureGetter, uvLocked, baseState, particle, builder);
                }

            }

        }

        public static class BlockPart extends VanillaModelWrapperEvent.Bake {

            protected net.minecraft.client.renderer.block.model.BlockPart part;
            protected TRSRTransformation transformation;

            public BlockPart(VanillaModelWrapper modelWrapper, ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked, net.minecraft.client.renderer.block.model.BlockPart part, TRSRTransformation transformation)
            {
                super(modelWrapper, model, perState, modelState, newTransforms, format, bakedTextureGetter, uvLocked);
                this.part = part;
                this.transformation = transformation;
            }

            public net.minecraft.client.renderer.block.model.BlockPart getPart()
            {
                return part;
            }

            public TRSRTransformation getTransformation()
            {
                return transformation;
            }

            public static class Pre extends VanillaModelWrapperEvent.Bake.BlockPart {

                public Pre(VanillaModelWrapper modelWrapper, ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked, net.minecraft.client.renderer.block.model.BlockPart part, TRSRTransformation transformation)
                {
                    super(modelWrapper, model, perState, modelState, newTransforms, format, bakedTextureGetter, uvLocked, part, transformation);
                }

                public void setPart(net.minecraft.client.renderer.block.model.BlockPart part)
                {
                    this.part = part;
                }

                public void setTransformation(TRSRTransformation transformation)
                {
                    this.transformation = transformation;
                }

            }

            public static class Post extends VanillaModelWrapperEvent.Bake.BlockPart {

                public Post(VanillaModelWrapper modelWrapper, ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked, net.minecraft.client.renderer.block.model.BlockPart part, TRSRTransformation transformation)
                {
                    super(modelWrapper, model, perState, modelState, newTransforms, format, bakedTextureGetter, uvLocked, part, transformation);
                }

            }

        }

    }

}
