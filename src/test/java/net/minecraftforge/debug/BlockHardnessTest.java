package net.minecraftforge.debug;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "blockhardnesstest", name = "Block Hardness Event Test", version = "1.0", acceptableRemoteVersions = "*")
public class BlockHardnessTest
{
    static final boolean ENABLED = true;

    @Mod.EventHandler
    public void onPreIn(FMLPreInitializationEvent event) 
    {      
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onBlockBreakHardness(PlayerEvent.BlockHardness event)
    {
        if (ENABLED && event.getStats().getBlock() == Blocks.OBSIDIAN)
        {
            event.setNewHardness(Blocks.STONE.getDefaultState().getBlockHardness(event.getEntityPlayer().world, event.getPos()));
        }
    }
}
