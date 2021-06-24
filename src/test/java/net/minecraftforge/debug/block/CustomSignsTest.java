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

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SignItem;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomSignsTest.MODID)
public class CustomSignsTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "custom_signs_test";

    public static final WoodType TEST_WOOD_TYPE = WoodType.create(new ResourceLocation(MODID, "test").toString());

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<CustomStandingSignBlock> TEST_STANDING_SIGN = BLOCKS.register("test_sign", () -> new CustomStandingSignBlock(Block.Properties.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundType.WOOD), CustomSignsTest.TEST_WOOD_TYPE));
    public static final RegistryObject<CustomWallSignBlock> TEST_WALL_SIGN = BLOCKS.register("test_wall_sign", () -> new CustomWallSignBlock(Block.Properties.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundType.WOOD), CustomSignsTest.TEST_WOOD_TYPE));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<SignItem> TEST_SIGN = ITEMS.register("test_sign", () -> new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_DECORATIONS), TEST_STANDING_SIGN.get(), TEST_WALL_SIGN.get()));

    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    public static final RegistryObject<TileEntityType<CustomSignTileEntity>> CUSTOM_SIGN = TILE_ENTITIES.register("custom_sign", () -> TileEntityType.Builder.of(CustomSignTileEntity::new, TEST_WALL_SIGN.get(), TEST_STANDING_SIGN.get()).build(null));

    public CustomSignsTest()
    {
        if (ENABLE)
        {
            final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(eventBus);
            ITEMS.register(eventBus);
            TILE_ENTITIES.register(eventBus);

            eventBus.addListener(this::clientSetup);
            eventBus.addListener(this::commonSetup);
        }
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        ClientRegistry.bindTileEntityRenderer(CUSTOM_SIGN.get(), SignTileEntityRenderer::new);
        event.enqueueWork(() -> {
           Atlases.addWoodType(TEST_WOOD_TYPE);
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> WoodType.register(TEST_WOOD_TYPE));
    }

    public static class CustomStandingSignBlock extends StandingSignBlock
    {

        public CustomStandingSignBlock(Properties propertiesIn, WoodType woodTypeIn)
        {
            super(propertiesIn, woodTypeIn);
        }

        @Override
        public boolean hasTileEntity(BlockState stateIn)
        {
            return true;
        }

        @Override
        public TileEntity newBlockEntity(IBlockReader worldIn)
        {
            return new CustomSignTileEntity();
        }
    }

    public static class CustomWallSignBlock extends WallSignBlock
    {

        public CustomWallSignBlock(Properties propertiesIn, WoodType woodTypeIn)
        {
            super(propertiesIn, woodTypeIn);
        }

        @Override
        public boolean hasTileEntity(BlockState stateIn)
        {
            return true;
        }

        @Override
        public TileEntity newBlockEntity(IBlockReader worldIn)
        {
            return new CustomSignTileEntity();
        }
    }

    public static class CustomSignTileEntity extends SignTileEntity
    {
        @Override
        public TileEntityType<CustomSignTileEntity> getType()
        {
            return CUSTOM_SIGN.get();
        }
    }
}
