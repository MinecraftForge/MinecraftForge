/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(TRSRTransformerTest.MODID)
public class TRSRTransformerTest {
    public static final String MODID = "trsr_transformer_test";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test", () -> new Block(Block.Properties.of(Material.STONE)));
    @SuppressWarnings("unused")
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public TRSRTransformerTest() {
        final IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> mod.addListener(this::onModelBake));
        BLOCKS.register(mod);
        ITEMS.register(mod);
    }

    public void onModelBake(ModelBakeEvent e) {
        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if (MODID.equals(id.getNamespace()) && "test".equals(id.getPath())) {
                e.getModelRegistry().put(id, new MyBakedModel(e.getModelRegistry().get(id)));
            }
        }
    }

    public class MyBakedModel implements IDynamicBakedModel {
        private final BakedModel base;

        public MyBakedModel(BakedModel base) {
            this.base = base;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData data) {
            ImmutableList.Builder<BakedQuad> quads = new ImmutableList.Builder<>();

            Quaternion rot = TransformationHelper.quatFromXYZ(new Vector3f(0, 45, 0), true);
            Vector3f translation = new Vector3f(0, 0.33f, 0);

            Transformation trans = new Transformation(translation, rot, null, null).blockCenterToCorner();

            for (BakedQuad quad : base.getQuads(state, side, rand, data)) {

                if (true)
                {
                    BakedQuadBuilder builder = new BakedQuadBuilder();

                    TRSRTransformer transformer = new TRSRTransformer(builder, trans);

                    quad.pipe(transformer);

                    quads.add(builder.build());
                } /* else {
                    QuadTransformer qt = new QuadTransformer(trans);
                    quads.add(qt.processOne(quad));
                }*/
            }

            return quads.build();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return base.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return base.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return base.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return base.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return base.getParticleIcon();
        }

        @Override
        public ItemOverrides getOverrides() {
            return base.getOverrides();
        }
    }
}
