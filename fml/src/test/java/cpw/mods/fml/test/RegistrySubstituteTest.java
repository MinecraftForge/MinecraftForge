package cpw.mods.fml.test;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.ExistingSubstitutionException;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.Type;

@Mod(modid = "RegistrySubstituteTest", name = "RegistrySubstituteTest", version = "0.0.0")
public class RegistrySubstituteTest
{
	public static boolean ENABLE = false;
	public static boolean ENABLE_BAD_REPLACEMENT_TEST = false; // substitutes a block without substituting its item, crashes on startup
	
	private static ItemStack blockTest, itemTest;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e)
	{
		if (!ENABLE)return;
		
		blockTest = new ItemStack(Blocks.planks);
		itemTest = new ItemStack(Items.ender_eye);
		
        try
        {
            Block planks = new BlockReplacedPlanks();
            GameRegistry.addSubstitutionAlias("minecraft:planks", Type.BLOCK, planks);
            GameRegistry.addSubstitutionAlias("minecraft:planks", Type.ITEM, new ItemReplacedPlanks(planks));

            GameRegistry.addSubstitutionAlias("minecraft:ender_eye", Type.ITEM, new ItemReplacedEnderEye());

            if (ENABLE_BAD_REPLACEMENT_TEST) GameRegistry.addSubstitutionAlias("minecraft:brick_block", Type.BLOCK, new BlockBadlyReplacedBrick());
        }
        catch(ExistingSubstitutionException ex)
        {
            FMLCommonHandler.instance().raiseException(ex, "Error testing registry substitute.", true);
        }
	}
	
    // substitute classes
    private static class BlockReplacedPlanks extends BlockWood
    {
        public BlockReplacedPlanks()
        {
            setHardness(2F);
            setResistance(5F);
            setStepSound(soundTypeWood);
            setBlockName("wood");
            setBlockTextureName("planks");
            setLightLevel(1F); // make the change visible in-game
        }
    }

    private static class ItemReplacedPlanks extends ItemMultiTexture
    {
        public ItemReplacedPlanks(Block block)
        {
            super(block, block, BlockWood.field_150096_a);
            setUnlocalizedName("wood");
        }

        @Override
        public String getItemStackDisplayName(ItemStack is) // make the change visible in-game
        {
            return super.getItemStackDisplayName(is) + " (Custom)";
        }
    }

    private static class ItemReplacedEnderEye extends ItemEnderEye
    {
        public ItemReplacedEnderEye()
        {
            setUnlocalizedName("eyeOfEnder");
            setTextureName("ender_eye");
        }

        @Override
        public String getItemStackDisplayName(ItemStack is) // make the change visible in-game
        {
            return super.getItemStackDisplayName(is) + " (Custom)";
        }
    }

    private static class BlockBadlyReplacedBrick extends Block
    {
        public BlockBadlyReplacedBrick()
        {
            super(Material.rock);
            setBlockName("brick");
            setBlockTextureName("brick");
        }
    }
	
	// test command - need to test after injectWorldIDMap
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent e)
    {
        if (ENABLE) e.registerServerCommand(new TestCommand());
    }

    private static class TestCommand extends CommandBase
    {
        @Override
        public String getCommandName()
        {
            return "registry-substitute-test";
        }

        @Override
        public String getCommandUsage(ICommandSender sender)
        {
            return "/registry-substitute-test";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args)
        {
            try
            {
                runTest();
                sender.addChatMessage(new ChatComponentText("Test succeeded!"));
            }
            catch(Exception e)
            {
                sender.addChatMessage(new ChatComponentText("Test failed: " + e.getMessage()));
            }
        }
    }

    // test
    private static void runTest() throws Exception
    {
        // check stone brick block
        if (Blocks.planks.delegate.name() == null)
            throw new Exception("Block delegate name is null.");
        
        if (!Blocks.planks.delegate.name().equals("minecraft:planks"))
            throw new Exception("Block delegate name is incorrect: " + Blocks.planks.delegate.name());
        
        if (!BlockReplacedPlanks.class.isInstance(Blocks.planks.delegate.get()))
            throw new Exception("Block delegate type is incorrect: " + Blocks.planks.delegate.get().getClass().getName());

        if (!BlockReplacedPlanks.class.isInstance(Blocks.planks))
            throw new Exception("Block class type is incorrect: " + Blocks.planks.getClass().getName());
        
        if (Block.blockRegistry.getObject("minecraft:planks") != Blocks.planks)
            throw new Exception("Block registry has invalid block: " + Block.blockRegistry.getObject("minecraft:planks"));
        
        if (Block.getIdFromBlock(Blocks.planks) != 5)
            throw new Exception("Block registry returns invalid ID: " + Block.getIdFromBlock(Blocks.planks));
        
        if (Block.getBlockById(5) != Blocks.planks)
            throw new Exception("Block registry returns invalid block by ID: " + Block.getBlockById(5));

        // check stone brick itemblock
        ItemBlock ib = (ItemBlock) Item.getItemFromBlock(Blocks.planks);

        if (ib.delegate.name() == null)
            throw new Exception("ItemBlock delegate name is null.");
        
        if (!ib.delegate.name().equals("minecraft:planks"))
            throw new Exception("ItemBlock delegate name is incorrect: " + ib.delegate.name());
        
        if (!ItemReplacedPlanks.class.isInstance(ib.delegate.get()))
            throw new Exception("ItemBlock delegate type is incorrect: " + ib.delegate.get().getClass().getName());

        if (!BlockReplacedPlanks.class.isInstance(ib.field_150939_a))
            throw new Exception("ItemBlock block type is incorrect: " + ib.field_150939_a.getClass().getName());
        
        if (!ItemReplacedPlanks.class.isInstance(ib))
            throw new Exception("ItemBlock class type is incorrect: " + ib.getClass().getName());
        

        // check ender eye
        if (Items.ender_eye.delegate.name() == null)
            throw new Exception("Item delegate name is null.");
        
        if (!Items.ender_eye.delegate.name().equals("minecraft:ender_eye"))
            throw new Exception("Item delegate name is incorrect: " + Items.ender_eye.delegate.name());
        
        if (!ItemReplacedEnderEye.class.isInstance(Items.ender_eye.delegate.get()))
            throw new Exception("Item delegate type is incorrect: " + Items.ender_eye.delegate.get().getClass().getName());

        if (!ItemReplacedEnderEye.class.isInstance(Items.ender_eye))
            throw new Exception("Item class type is incorrect: " + Items.ender_eye.getClass().getName());
        
        if (Item.itemRegistry.getObject("minecraft:ender_eye") != Items.ender_eye)
            throw new Exception("Item registry has invalid item: " + Item.itemRegistry.getObject("minecraft:ender_eye"));
        
        if (Item.getIdFromItem(Items.ender_eye) != Item.getIdFromItem(itemTest.getItem()))
            throw new Exception("ItemStack item cannot be found in registry: " + Item.getIdFromItem(itemTest.getItem()));
        
        if (Item.getIdFromItem(Items.ender_eye) != 381)
            throw new Exception("Item registry returns invalid ID: " + Item.getIdFromItem(Items.ender_eye));
        
        if (Item.getItemById(381) != Items.ender_eye)
            throw new Exception("Item registry returns invalid item by ID: " + Item.getItemById(381));
    }
}
