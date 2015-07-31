package net.minecraftforge.client;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ForgeTileEntityItemStackRenderer extends TileEntityItemStackRenderer {
    private List<Block> vanillaBlocks = Arrays.asList(Blocks.air, Blocks.flowing_water, Blocks.water, Blocks.flowing_lava, Blocks.lava, Blocks.piston_extension, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.standing_sign, Blocks.skull, Blocks.end_portal, Blocks.barrier, Blocks.wall_sign, Blocks.wall_banner, Blocks.standing_banner);

    public void renderByItem(ItemStack stack)
    {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block instanceof ITileEntityProvider && !isVanillaBlock(block)) {
            TileEntity entity = ((ITileEntityProvider)block).createNewTileEntity(Minecraft.getMinecraft().theWorld, stack.getMetadata());
            TileEntityRendererDispatcher.instance.renderTileEntityAt(entity, 0.0D, 0.0D, 0.0D, 0.0F);
        } else {
            super.renderByItem(stack);
        }
    }
    
    private boolean isVanillaBlock(Block block)
    {
        return vanillaBlocks.contains(block);
    }
}
