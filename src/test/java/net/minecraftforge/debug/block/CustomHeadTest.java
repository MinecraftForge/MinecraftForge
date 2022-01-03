/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.block;

import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CustomHeadTest.MODID)
public class CustomHeadTest
{
    static final String MODID = "custom_head_test";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<Block> BLAZE_HEAD = BLOCKS.register("blaze_head", () -> new SkullBlock(SkullType.BLAZE, BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F)));
    private static final RegistryObject<Block> BLAZE_HEAD_WALL = BLOCKS.register("blaze_wall_head", () -> new WallSkullBlock(SkullType.BLAZE, BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).lootFrom(BLAZE_HEAD)));
    private static final RegistryObject<Item> BLAZE_HEAD_ITEM = ITEMS.register("blaze_head", () -> new StandingAndWallBlockItem(BLAZE_HEAD.get(), BLAZE_HEAD_WALL.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS).rarity(Rarity.UNCOMMON)));

    public CustomHeadTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }

    private enum SkullType implements SkullBlock.Type { BLAZE }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, bus = Bus.MOD, modid = MODID)
    private static class ClientEvents
    {
        private static final ModelLayerLocation BLAZE_HEAD_LAYER = new ModelLayerLocation(BLAZE_HEAD.getId(), "main");

        @SubscribeEvent
        static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            event.registerLayerDefinition(BLAZE_HEAD_LAYER, Lazy.of(SkullModel::createMobHeadLayer));
        }

        @SubscribeEvent
        static void clientSetupEvent(FMLClientSetupEvent event)
        {
            ForgeHooksClient.registerSkullModel(SkullType.BLAZE, models -> new SkullModel(models.bakeLayer(BLAZE_HEAD_LAYER)));
            SkullBlockRenderer.SKIN_BY_TYPE.put(SkullType.BLAZE, new ResourceLocation("textures/entity/blaze.png"));
        }
    }
}
