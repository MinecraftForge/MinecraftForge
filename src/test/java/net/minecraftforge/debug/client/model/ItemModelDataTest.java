/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod(ItemModelDataTest.MODID)
public class ItemModelDataTest {

    public static final String MODID = "item_model_data";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final ModelProperty<ItemStack> ITEM_PROPERTY = new ModelProperty<>();

    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test", () -> new Item(new Item.Properties().group(ItemGroup.MISC)){
        @Nonnull
        @Override
        public IModelData getModelData(ItemStack stack) {
            return new ModelDataMap.Builder().withInitial(ITEM_PROPERTY, new ItemStack(Items.BAMBOO)).build();
        }
    });

    public ItemModelDataTest() {
        final IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> mod.addListener(this::onModelBake));
        ITEMS.register(mod);
    }

    public void onModelBake(ModelBakeEvent e) {
        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            if (MODID.equals(id.getNamespace()) && "test".equals(id.getPath())) {
                e.getModelRegistry().put(id, new ItemModelDataTest.MyBakedModel(e.getModelRegistry().get(id)));
            }
        }
    }

    public class MyBakedModel implements IDynamicBakedModel {

        private final IBakedModel base;

        public MyBakedModel(IBakedModel base) {
            this.base = base;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
            if (extraData.hasProperty(ITEM_PROPERTY)) {
                ItemStack stack = extraData.getData(ITEM_PROPERTY);
                return Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(stack).getQuads(state, side, rand, extraData);
            }
            return this.base.getQuads(state, side, rand, extraData);
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
}
