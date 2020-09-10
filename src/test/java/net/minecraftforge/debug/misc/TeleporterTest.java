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

package net.minecraftforge.debug.misc;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.TeleportationRepositioner.Result;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(TeleporterTest.MODID)
public class TeleporterTest
{
	protected static final String MODID = "teleporter_test";
	private static RegistryKey<World> RED_WORLD = RegistryKey.func_240903_a_(Registry.field_239699_ae_, new ResourceLocation(MODID, "red_world"));;
	private static RegistryKey<World> BLUE_WORLD = RegistryKey.func_240903_a_(Registry.field_239699_ae_, new ResourceLocation(MODID, "blue_world"));
	private static Block RED_TELEPORTER, BLUE_TELEPORTER;
	private static PointOfInterestType RED_POI, BLUE_POI;
	
	public TeleporterTest()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(PointOfInterestType.class, this::registerPOIs);
	}
	
	protected void registerBlocks(final RegistryEvent.Register<Block> event)
	{
		RED_TELEPORTER = registerBlock(event.getRegistry(), "red_teleporter", new TeleporterBlock(() -> RED_POI, RED_WORLD));
		BLUE_TELEPORTER = registerBlock(event.getRegistry(), "blue_teleporter", new TeleporterBlock(() -> BLUE_POI, BLUE_WORLD));
	}
	
	private Block registerBlock(IForgeRegistry<Block> registry, String name, Block block)
	{
		block.setRegistryName(new ResourceLocation(MODID, name));
		registry.register(block);
		return block;
	}
	
	protected void registerPOIs(final RegistryEvent.Register<PointOfInterestType> event)
	{
		RED_POI = registerPOI(event.getRegistry(), new PointOfInterestType(new ResourceLocation(MODID, "red_poi").toString(), PointOfInterestType.getAllStates(RED_TELEPORTER), 0, 1));
		BLUE_POI = registerPOI(event.getRegistry(), new PointOfInterestType(new ResourceLocation(MODID, "blue_poi").toString(), PointOfInterestType.getAllStates(BLUE_TELEPORTER), 0, 1));
	}
	
	private PointOfInterestType registerPOI(IForgeRegistry<PointOfInterestType> registry, PointOfInterestType poi)
	{
		poi.setRegistryName(new ResourceLocation(poi.toString()));
		registry.register(poi);
		try
		{
		    ObfuscationReflectionHelper.findMethod(PointOfInterestType.class, "func_221052_a", PointOfInterestType.class).invoke(null, poi);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return poi;
	}
	
	private static class TeleporterBlock extends Block
	{
		final Supplier<PointOfInterestType> poi;
		final RegistryKey<World> destWorldKey;
		
		private TeleporterBlock(Supplier<PointOfInterestType> poi, RegistryKey<World> destWorldKey)
		{
			super(Properties.from(Blocks.STONE));
			this.poi = poi;
			this.destWorldKey = destWorldKey;
		}
		
		@Override
		public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
		{
			if (!worldIn.isRemote)
				entityIn.changeDimension(((ServerWorld) worldIn).getServer().getWorld(worldIn.func_234923_W_() == this.destWorldKey ? World.field_234918_g_ : this.destWorldKey), new TestTeleporter(this.poi.get(), this));
		}
	}
	
	private static class TestTeleporter implements ITeleporter
	{
		final PointOfInterestType poi;
		final Block teleporterBlock;
		
		private TestTeleporter(PointOfInterestType poi, Block teleporterBlock)
		{
			this.poi = poi;
			this.teleporterBlock = teleporterBlock;
		}
		
		@Override
		public Optional<Result> createAndGetPortal(ServerWorld fromWorld, ServerWorld toWorld, Entity entity)
		{
			BlockPos scaledPos = this.getScaledPos(fromWorld, toWorld, new BlockPos(entity.getPosX(), 255, entity.getPosZ()));
			for (int i = 0; i < 256 && toWorld.getBlockState(scaledPos.down()).getMaterial() == Material.AIR; i++)
				scaledPos = scaledPos.down();
			toWorld.setBlockState(scaledPos, this.teleporterBlock.getDefaultState());
			return Optional.of(new TeleportationRepositioner.Result(scaledPos.north(), 0, 0));
		}
		
		@Override
		public PointOfInterestType getPortalPOI()
		{
			return this.poi;
		}
		
		@Override
		public PortalInfo getPortalInfo(Result tpResult)
		{
			BlockPos pos = tpResult.field_243679_a.north();
			return new PortalInfo(new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), Vector3d.ZERO, 0, 0);
		}
	}
}
