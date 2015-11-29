package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.gui.TileEntityGuiRegistryHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "guiregistrytest")
public class GuiRegistryTest {
    @Instance(value = "guiregistrytest")
    public static GuiRegistryTest instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        FMLCommonHandler.instance().getGuiRegistry(instance).registerElement("test", TileEntityGuiRegistryHandler.instance,ContainerTestBlock.class,GuiTestBlock.class);
        GameRegistry.registerBlock(new TestBlock(Material.circuits),"testBlock");
    }

    public static class ContainerTestBlock extends TileEntityGuiRegistryHandler.TileEntityContainer {

        public ContainerTestBlock(TileEntity tile, EntityPlayer player) {
            super(tile, player);
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    }
    public static class GuiTestBlock extends TileEntityGuiRegistryHandler.TileEntityGui{


        public GuiTestBlock(Container container) {
            super(container);
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
            drawString(Minecraft.getMinecraft().fontRendererObj,"It works!",mouseX+10,mouseY+10,mouseX*mouseY);
        }
    }

    public class TestBlock extends Block{

        public TestBlock(Material materialIn) {
            super(materialIn);
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
            playerIn.openGui(instance,"test",pos);
            return true;
        }
    }

}
