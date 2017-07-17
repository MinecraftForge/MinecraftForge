package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = BlockExplodeEventTest.MOD_ID, name = "BlockExplodeEvent test mod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class BlockExplodeEventTest
{
    static final String MOD_ID = "block_explode_event_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onExplosionDestroyBlock(BlockEvent.ExplodeEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        Block block = event.getState().getBlock();
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        Explosion explosion = event.getExplosion();

        if (block instanceof BlockSand) {
            // If sand is exploded, have a chance to drop 2 glass instead of the usual 1 sand
            if (world.rand.nextFloat() <= 1.0F / explosion.getSize()) {
                Block.spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(Blocks.GLASS), 2));
            }
            block.onBlockExploded(world, pos, explosion);
            event.setCanceled(true);
        } else if (block instanceof BlockSandStone) {
            // If sandstone is exploded, change it to sand but don't actually remove or drop it
            world.setBlockState(pos, Blocks.SAND.getDefaultState());
            event.setCanceled(true);
        }
    }
}
