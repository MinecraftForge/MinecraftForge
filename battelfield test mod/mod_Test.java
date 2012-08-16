package net.minecraft.src;

import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.INBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.NBTHelper;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.block.CanBlockStayCheckEvent;

public class mod_Test extends BaseMod{

	public static BiomeGenBase biome = new BiomeGenTest(100).setBiomeName("stoneDesert").setMinMaxHeight(0F, 0f);
	
	public String getVersion() {
		return "1.0";
	}
	
	private int timesLoaded = 0;

	public void load()
	{
		MinecraftForge.EVENT_BUS.register(new BlockPlaceEvent());
		NBTHelper.registerNbtHandler(new NBTHandlerTest());
		BiomeManager.addBiome(biome);
		BiomeManager.setPlayerCanSpawnInBiome(biome, true);
		BiomeManager.setStrongholdCanSpawnInBiome(biome, true);
		BiomeManager.setVillageCanSpawnInBiome(biome, true);
	}
	
	public class NBTHandlerTest implements INBT
	{
		public String nbtName() {
			return "TestNbt";
		}

		public boolean savePerPlayer() {
			return false;
		}

		public void writeToNBT(NBTTagCompound nbttagcompound, EntityPlayer player) {
			nbttagcompound.setInteger("TimesLoaded", timesLoaded);
		}

		public void readFromNBT(NBTTagCompound nbttagcompound, EntityPlayer player) {
			timesLoaded = nbttagcompound.getInteger("TimesLoaded");
			timesLoaded++;
		}
		
	}
	
	public class BlockPlaceEvent
	{
		@ForgeSubscribe
		public void intercept(CanBlockStayCheckEvent event)
		{
			if(event.block == Block.cactus)
			{
				if(event.blockBelow.itemID == Block.stone.blockID)
				{
					event.setCanPlace(true);
				}else if(event.blockBelow.itemID == Block.sand.blockID)
				{
					event.setCanPlace(false);
				}
			}else if(event.block == Block.plantRed && event.blockBelow.itemID == Block.stone.blockID)
			{
				event.setCanPlace(true);
			}else if(event.block == Block.reed && event.blockBelow.itemID == Block.stone.blockID)
			{
				event.setCanPlace(true);
			}
		}
	}
	
}
