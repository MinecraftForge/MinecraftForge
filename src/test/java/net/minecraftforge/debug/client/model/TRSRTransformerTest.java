/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.client.model;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod(TRSRTransformerTest.MODID)
public class TRSRTransformerTest {
    public static final String MODID = "trsr_transformer_test";

    public TRSRTransformerTest() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if ("trsr_transformer_test".equals(id.getNamespace()) && "test".equals(id.getPath())) {
                e.getModelRegistry().put(id, new MyBakedModel(e.getModelRegistry().get(id)));
            }
        }
    }

    public class MyBakedModel implements IDynamicBakedModel
    {
        private final IBakedModel base;

        public MyBakedModel(IBakedModel base) {
            this.base = base;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData data) {
            ImmutableList.Builder<BakedQuad> quads = new ImmutableList.Builder<>();

            Quaternion rot = TransformationHelper.quatFromXYZ(new Vector3f(0, 45, 0), true);
            Vector3f translation = new Vector3f(0, 0.33f, 0);

            TransformationMatrix trans = new TransformationMatrix(translation, rot, null, null).blockCenterToCorner();

            for (BakedQuad quad : base.getQuads(state, side, rand, data)) {

                if(true)
                {
                    BakedQuadBuilder builder = new BakedQuadBuilder();

                    TRSRTransformer transformer = new TRSRTransformer(builder, trans);

                    quad.pipe(transformer);

                    quads.add(builder.build());
                }
                else
                {
                    QuadTransformer qt = new QuadTransformer(trans);
                    quads.add(qt.processOne(quad));
                }
            }

            return quads.build();
        }

        @Override
        public boolean isAmbientOcclusion() {
            return base.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return base.isGui3d();
        }

        @Override
        public boolean func_230044_c_() {
            return base.func_230044_c_();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return base.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return base.getParticleTexture();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return base.getOverrides();
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @ObjectHolder("trsr_transformer_test:test")
        public static Block testblock;

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().register(new Block(Block.Properties.create(Material.ROCK)).setRegistryName("trsr_transformer_test", "test"));
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            itemRegistryEvent.getRegistry().register(new BlockItem(testblock, new Item.Properties().group(ItemGroup.MISC)).setRegistryName("trsr_transformer_test", "test"));
        }
    }
}
