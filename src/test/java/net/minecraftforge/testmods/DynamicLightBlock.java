package net.minecraftforge.testmods;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;

public class DynamicLightBlock extends Block {

    private final BlockItem item;

    DynamicLightBlock() {
        super(Properties.create(Material.IRON).sound(SoundType.METAL));

        ResourceLocation regName = new ResourceLocation(LightTestMod.ID, "dynamic_light_block");
        setRegistryName(regName);

        item = new BlockItem(this, new Item.Properties().group(ItemGroup.MISC));
        item.setRegistryName(regName);
    }

    @Override
    public Item asItem() {
        return item;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        final Block block = world.getBlockState(pos.down()).getBlock();
        if (block == Blocks.GLASS || block == Blocks.STONE)
            return 15;
        return super.getLightValue(state, world, pos);
    }
}
