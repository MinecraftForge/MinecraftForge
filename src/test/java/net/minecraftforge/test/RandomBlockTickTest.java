package net.minecraftforge.test;

import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = RandomBlockTickTest.MODID, version = RandomBlockTickTest.VERSION)
public class RandomBlockTickTest {

	public static final String MODID = "RandomBlockTickEventTest";
    public static final String VERSION = "1.0";
    
    public static final boolean ENABLE = false;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	if(!ENABLE)return;
    	FMLLog.info("[RandomBlockTickEventTest] Preinit test mod");
    	BlockEvent.RandomTickEvent.RandomBlockTickEventRegistry.registerBlockForEvent(BlockFire.class);
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onBlockRandomTick(BlockEvent.RandomTickEvent event)
    {
    	System.out.println("Block "+event.getState().getBlock().getUnlocalizedName()+ " at "+event.getPos().toString()+" tries to do a random tick!");
    	event.setState(Blocks.tnt.getDefaultState());
    	event.setCanceled(true);
    }
}
