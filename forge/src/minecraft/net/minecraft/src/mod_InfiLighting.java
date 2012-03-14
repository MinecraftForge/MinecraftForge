
package net.minecraft.src;

import net.minecraft.src.*;
import net.minecraft.src.lighting.*;
import net.minecraft.src.blocks.*;
import net.minecraft.src.forge.*;
import java.io.File;
import java.lang.reflect.*;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class mod_InfiLighting extends BaseModMp
{
	public String getVersion()
	{
		return "0.1.2 Torches";
	}

	public mod_InfiLighting()
	{
		/*ModLoader.registerBlock(infiCraftingTable, net.minecraft.src.blocks.InfiWorkbenchItem.class);*/
		//ModLoader.registerBlock(torcha, net.minecraft.src.lighting.TorchaItem.class);
		MinecraftForgeClient.preloadTexture("/infiblocks/infiblocks.png");
		
		//Stick recipes
		for (int iter = 0; iter < stickArrayShort.length; iter++)
		{
			ModLoader.addRecipe(new ItemStack(Block.torchWood, 4), new Object[]
			{
				"c", "s", 'c', new ItemStack(Item.coal, 1, -1), 's', stickArrayShort[iter]
			});
			ModLoader.addRecipe(new ItemStack(Block.torchRedstoneActive, 1), new Object[]
			{
				"c", "s", 'c', new ItemStack(Item.redstone, 1, 0), 's', stickArrayShort[iter]
			});
		}
		
		for (int iter = 0; iter < stickArray.length; iter++)
		{
			ModLoader.addRecipe(new ItemStack(Block.lever, 1), new Object[]
			{
				"s", "c", 'c', new ItemStack(Block.cobblestone, 1, 0), 's', stickArray[iter]
			});
			ModLoader.addRecipe(new ItemStack(Block.rail, 16), new Object[]
			{
				"c c", "csc", "c c", 'c', new ItemStack(Item.ingotIron, 1, 0), 's', stickArray[iter]
			});
			ModLoader.addRecipe(new ItemStack(Block.railPowered, 6), new Object[]
			{
				"c c", "csc", "crc", 'c', new ItemStack(Item.ingotGold, 1, -1), 
				's', stickArray[iter], 'r', new ItemStack(Item.redstone, 1, 0)
			});
		}
		torchModel = ModLoader.getUniqueBlockModelID(this, true);
		addNames();
	}
	
	public void addNames() {
		//ModLoader.addName(torcha, "Torch");
	}
	
	public boolean RenderWorldBlock(RenderBlocks renderblocks, 
			IBlockAccess iblockaccess, int i, int j, int k, Block block, int modelID)
	{
		if (modelID == torchModel)
		{
			return LightRenders.RenderTorchInWorld(renderblocks, iblockaccess, i, j, k, block);
		}
		return false;
	}
	
	public void RenderInvBlock(RenderBlocks renderblocks, Block block, int i, int j)
	{
		if (j == torchModel)
		{
			LightRenders.RenderTorchInv(renderblocks, block, i);
		}
	}
	
	public static InfiProps getProps(InfiProps infiprops)
	{
		torchaID = infiprops.readInt("Torch ID 1");
		return infiprops;
	}

	public static InfiProps InitProps(InfiProps infiprops)
	{
		infiprops.accessInt("Torch ID 1", 3210);
		return infiprops;
	}
	
	public static int torchModel;
	public static int torchaID;
	
	public static Block torcha;
	
	public static InfiProps props;
	
	static Item[] stickArray = { 
		mod_InfiTools.stoneRod, mod_InfiTools.ironRod, mod_InfiTools.diamondRod,
		mod_InfiTools.goldRod, mod_InfiTools.redstoneRod, mod_InfiTools.obsidianRod,
		mod_InfiTools.sandstoneRod, Item.bone, mod_InfiTools.paperRod, mod_InfiTools.mossyRod,
		mod_InfiTools.netherrackRod, mod_InfiTools.glowstoneRod, mod_InfiTools.lavaRod, 
		mod_InfiTools.iceRod, mod_InfiTools.slimeRod, mod_InfiTools.cactusRod,
		mod_InfiTools.flintRod, mod_InfiTools.flintRod, mod_InfiTools.brickRod,
		Item.blazeRod
	};
	
	static Item[] stickArrayShort = { 
		mod_InfiTools.stoneRod, mod_InfiTools.ironRod, mod_InfiTools.diamondRod,
		mod_InfiTools.goldRod, mod_InfiTools.redstoneRod, mod_InfiTools.obsidianRod,
		mod_InfiTools.sandstoneRod, Item.bone, mod_InfiTools.paperRod, mod_InfiTools.mossyRod,
		mod_InfiTools.netherrackRod, mod_InfiTools.glowstoneRod, mod_InfiTools.lavaRod, 
		mod_InfiTools.slimeRod, mod_InfiTools.cactusRod, mod_InfiTools.flintRod,
		mod_InfiTools.flintRod, mod_InfiTools.brickRod, Item.blazeRod
	};
	
	static
	{
		File me = new File( (new StringBuilder().append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").toString() ) );
        me.mkdir();
		props = new InfiProps((new File((new StringBuilder()).append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").append('/').append("InfiLighting.cfg").toString())).getPath());
		props = InitProps(props);
		getProps(props);
		
		//torcha = new InfiBlockTorch(129, 1).setHardness(0.0F).setLightValue(0.9375F).setStepSound(Block.soundWoodFootstep).setBlockName("torcha").setRequiresSelfNotify();
		//infiCraftingTable = (new InfiWorkbenchBlock(blockCraftingID)).setHardness(0.5F).setBlockName("infiCraftingTable");
	}
	
	public void load()
	{
	}
}
