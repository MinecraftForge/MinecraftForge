package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.DestroyedByFireEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = DestroyedByFireTest.MODID, version = DestroyedByFireTest.VERSION)
public class DestroyedByFireTest
{
    public static final String MODID = "DestroyedByFireTest";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler
    {
        @SubscribeEvent
        public void destroyedByFireEvent(DestroyedByFireEvent event)
        {
            Block block = event.state.getBlock();
            if (block == Blocks.log2) // Acacia, Dark Oak
            {
                event.setCanceled(true);
                BlockPos fire = event.pos.offset(event.getFace());
                if (event.world.getBlockState(fire).getBlock() instanceof BlockFire)
                {
                    event.world.setBlockToAir(fire);
                }
            }
            else if (block == Blocks.log) // Oak, Spruce, Birch, Jungle
            {
                Block.spawnAsEntity(event.world, event.pos, new ItemStack(Items.coal, 1, 1));
            }
        }
    }
}