/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import static net.minecraftforge.debug.block.ParticleEffectsTest.MOD_ID;
import static net.minecraftforge.debug.block.ParticleEffectsTest.MOD_NAME;

@EventBusSubscriber
@Mod (modid = MOD_ID, name = MOD_NAME, version = "1.0", acceptableRemoteVersions = "*")
public class ParticleEffectsTest
{

    public static final String MOD_ID = "blockparticleeffectstest";
    public static final String MOD_NAME = "Block particle effects test";

    @ObjectHolder ("particle_block")
    public static final Block PARTICLE_BLOCK = null;

    @ObjectHolder ("particle_block")
    public static final Item PARTICLE_ITEM = null;

    private static final ResourceLocation particleBlockLocation = new ResourceLocation(MOD_ID, "particle_block");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        Block block = new Block(Material.ROCK)
        {
            @Override
            public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
            {
                world.spawnParticle(EnumParticleTypes.REDSTONE, entity.posX, entity.posY, entity.posZ, numberOfParticles, 0, 0, 0, 0.1D);
                return true;
            }

            @Override
            public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
            {
                world.spawnParticle(EnumParticleTypes.REDSTONE, entity.posX, entity.posY, entity.posZ, 1, 0, 0);
                return true;
            }

            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager)
            {
                world.spawnParticle(EnumParticleTypes.REDSTONE, target.hitVec.x, target.hitVec.y, target.hitVec.z, 0, 1, 0);
                return true;
            }

            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
            {
                world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 1);
                return true;
            }
        };
        event.getRegistry().register(block.setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setHardness(20).setRegistryName(particleBlockLocation));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        Item item = new ItemBlock(PARTICLE_BLOCK) {
            @Override
            public String getItemStackDisplayName(ItemStack stack)
            {
                return "Particle Tests Block";
            }
        };
        event.getRegistry().register(item.setRegistryName(particleBlockLocation));
    }

    @EventBusSubscriber (value = Side.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelResourceLocation normalLoc = new ModelResourceLocation("minecraft:stone", "normal");
            ModelResourceLocation invLoc = new ModelResourceLocation("minecraft:stone", "normal");
            ModelLoader.setCustomStateMapper(PARTICLE_BLOCK, new StateMapperBase()
            {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return normalLoc;
                }
            });
            ModelLoader.setCustomModelResourceLocation(PARTICLE_ITEM, 0, invLoc);
        }
    }
}
