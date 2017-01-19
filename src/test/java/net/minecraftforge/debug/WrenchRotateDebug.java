package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = WrenchRotateDebug.modID, name = "Wrench Rotate Debug", version = "0.0.0")
public class WrenchRotateDebug
{
    public static final String modID = "wrenchrotatedebug";
    private static final ResourceLocation testWrenchName = new ResourceLocation(modID, "test_wrench");

    @SidedProxy
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        TestWrench wrench = new TestWrench();
        wrench.setRegistryName(testWrenchName);

        GameRegistry.register(wrench);
        proxy.setupModel(wrench);
    }

    public static class CommonProxy
    {
        void setupModel(TestWrench wrench)
        {
        }
    }

    public static class ServerProxy extends CommonProxy
    {
    }

    public static class ClientProxy extends CommonProxy
    {
        @SuppressWarnings("unused")
        @Override
        void setupModel(TestWrench wrench)
        {
            final ModelResourceLocation wrenchName = new ModelResourceLocation(testWrenchName, "inventory");
            ModelBakery.registerItemVariants(wrench, wrenchName);
            ModelLoader.setCustomMeshDefinition(wrench, new ItemMeshDefinition()
            {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack)
                {
                    return wrenchName;
                }
            });
        }
    }

    public static class TestWrench extends Item
    {
        public TestWrench()
        {
            setCreativeTab(CreativeTabs.TOOLS);
            setMaxStackSize(1);
            setHarvestLevel("wrench", 0);
        }

        @Override
        public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
        {
            ItemStack wrench = player.getHeldItem(hand);
            if (player.canPlayerEdit(pos, facing, wrench) && worldIn.isBlockModifiable(player, pos))
            {
                IBlockState blockState = worldIn.getBlockState(pos);
                Block block = blockState.getBlock();
                if (block.rotateBlock(worldIn, pos, facing))
                {
                    player.swingArm(hand);
                    worldIn.notifyNeighborsOfStateChange(pos, block, false);
                    return EnumActionResult.SUCCESS;
                }
            }
            return EnumActionResult.FAIL;
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Forge Test Wrench";
        }
    }
}
