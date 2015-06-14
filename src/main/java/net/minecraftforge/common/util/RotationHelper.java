package net.minecraftforge.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHay;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class RotationHelper
{

    static public EnumAxis fromQuartz( BlockQuartz.EnumType type )
    {
        switch(type)
        {
            case LINES_X:
                return EnumAxis.X;
            case LINES_Y:
                return EnumAxis.Y;
            case LINES_Z:
                return EnumAxis.Z;
            default:
                return EnumAxis.NONE;
        }
    }
    
    static public boolean canRotateBlock( Block block )
    {
        return block instanceof BlockQuartz ||
                block instanceof BlockLog ||
                block instanceof BlockHay ||
                block instanceof BlockPumpkin ||
                block instanceof BlockFenceGate ||
                block instanceof BlockTripWireHook ||
                block instanceof BlockCocoa ||
                block instanceof BlockRailPowered ||
                block instanceof BlockRailDetector ||
                block instanceof BlockStairs ||
                block instanceof BlockChest ||
                block instanceof BlockEnderChest ||
                block instanceof BlockFurnace ||
                block instanceof BlockLadder ||
                block instanceof BlockWallSign ||
                block instanceof BlockDoor ||
                block instanceof BlockRail ||
                block instanceof BlockButton ||
                block instanceof BlockRedstoneRepeater ||
                block instanceof BlockRedstoneComparator ||
                block instanceof BlockTrapDoor ||
                block instanceof BlockSkull ||
                block instanceof BlockAnvil ||
                block instanceof BlockLever ||
                block instanceof BlockTorch ||
                block instanceof BlockDispenser ||
                block instanceof BlockPistonBase ||
                block instanceof BlockPistonExtension ||
                block instanceof BlockHopper;
    }
    
    @SuppressWarnings( "unchecked" )
    static public boolean rotateVanillaBlock(World world, BlockPos pos, EnumFacing axis)
    {
        IBlockState state = world.getBlockState(pos);
        
        if ( canRotateBlock( state.getBlock() ) )
        {
            for (IProperty prop : (java.util.Set<IProperty>)state.getProperties().keySet())
            {
                // handles chests/pistons/dispensers/furnaces/levers/torches/anvils/hoppers/doors/gates/rails etc...
                if (prop.getName().equals("facing") || prop.getName().equals("shape"))
                {
                    world.setBlockState(pos, state.cycleProperty(prop));
                    return true;
                }
                
                // handles quartz pillar
                if (prop.getName().equals("variant") && prop.getValueClass() == BlockQuartz.EnumType.class )
                {
                    EnumAxis side = fromQuartz( (BlockQuartz.EnumType)state.getValue( prop ) );
                    if ( side == EnumAxis.fromFacingAxis( axis.getAxis() ) || side == EnumAxis.NONE ) return false;
                    
                    do
                    {
                        state = state.cycleProperty(prop);
                        side = fromQuartz( (BlockQuartz.EnumType)state.getValue( prop ) );
                    }
                    while ( side == EnumAxis.fromFacingAxis( axis.getAxis() ) || side == EnumAxis.NONE );
                    
                    world.setBlockState(pos, state );
                    return true;
                }
                
                // handles hay
                if (prop.getName().equals("axis") && prop.getValueClass() == EnumFacing.Axis.class)
                {
                    EnumFacing.Axis side = (EnumFacing.Axis)state.getValue( prop );
                    if ( side == axis.getAxis() ) return false;
                    
                    do
                    {
                        state = state.cycleProperty(prop);
                        side = (EnumFacing.Axis)state.getValue( prop );
                    }
                    while ( side == axis.getAxis() );
                    
                    world.setBlockState(pos, state );
                    return true;
                }
                
                // handles logs
                if (prop.getName().equals("axis") && prop.getValueClass() == EnumAxis.class)
                {
                    EnumAxis side = (EnumAxis)state.getValue( prop );
                    if ( side == EnumAxis.fromFacingAxis( axis.getAxis() ) || side == EnumAxis.NONE ) return false;
                    
                    do
                    {
                        state = state.cycleProperty(prop);
                        side = (EnumAxis)state.getValue( prop );
                    }
                    while ( side == EnumAxis.fromFacingAxis( axis.getAxis() ) || side == EnumAxis.NONE );
                    
                    world.setBlockState(pos, state );
                    return true;
                }
            }
        }
        
        return false;
    }
    
}
