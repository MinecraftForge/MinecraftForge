package awesomesyd.fishingexample;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BlockReelEnchantment extends Enchantment
{
    public static HashSet<Block> blackList = new HashSet<Block>();

    public BlockReelEnchantment(Rarity rarity, EnchantmentType applicable, EquipmentSlotType... slots)
    {
        super(rarity, applicable, slots);
        blackList.add(Blocks.PISTON);// example, and also they sometimes have a TE, and I don't want to mess with that
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public int getMinCost(int level)
    {
        return 15;
    }

    @Override
    public int getMaxCost(int level)
    {
        return getMinCost(level + 1);
    }

    protected static boolean canReel(IWorldReader level, BlockPos pos, int enchLevel)
    {
        BlockState state = level.getBlockState(pos);
        if (state.hasTileEntity())
            return false;
        if (state.getDestroySpeed(level, pos) > 2 * enchLevel)
            return false;
        if (blackList.contains(state.getBlock()))
            return false;
        return true;
    }
}