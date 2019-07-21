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
 *//*


package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(ParticleEffectsTest.MOD_ID)
@Mod.EventBusSubscriber
public class ParticleEffectsTest
{

    public static final String MOD_ID = "block_particle_effects_test";
    public static final String BLOCK_ID = "particle_block";

    @ObjectHolder(BLOCK_ID)
    public static final Block PARTICLE_BLOCK = null;

    @ObjectHolder(BLOCK_ID)
    public static final Item PARTICLE_ITEM = null;

    private static final ResourceLocation particleBlockLocation = new ResourceLocation(MOD_ID, "particle_block");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        Block block = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(20, 0))
        {
            @Override
            public boolean addLandingEffects(BlockState state, ServerWorld world, BlockPos blockPosition, BlockState BlockState, LivingEntity entity, int numberOfParticles)
            {
                world.spawnParticle(ParticleTypes.EXPLOSION, entity.posX, entity.posY, entity.posZ, numberOfParticles, 0, 0, 0, 0.1D);
                return true;
            }

            @Override
            public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity)
            {
                world.addParticle(ParticleTypes.EXPLOSION, entity.posX, entity.posY, entity.posZ, 1, 0, 0);
                return true;
            }

            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager)
            {
                world.addParticle(ParticleTypes.EXPLOSION, target.getHitVec().x, target.getHitVec().y, target.getHitVec().z, 0, 1, 0);
                return true;
            }

            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager)
            {
                world.addParticle(ParticleTypes.EXPLOSION, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 1);
                return true;
            }
        };
        event.getRegistry().register(block.setRegistryName(particleBlockLocation));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        Item item = new BlockItem(PARTICLE_BLOCK, new Item.Properties()) {
            @Override
            public ITextComponent getDisplayName(ItemStack stack)
            {
                return new StringTextComponent("Particle Tests Block");
            }
        };
        event.getRegistry().register(item.setRegistryName(particleBlockLocation));
    }

    */
/*
    @EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
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
                protected ModelResourceLocation getModelResourceLocation(BlockState state)
                {
                    return normalLoc;
                }
            });
            ModelLoader.setCustomModelResourceLocation(PARTICLE_ITEM, 0, invLoc);
        }
    }
    *//*

}
*/
