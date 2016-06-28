package net.minecraftforge.test;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod(modid="autojumpdisancetest", name="Auto-Jump Distance Test", version="0.0.0", clientSideOnly = true)
public class AutoJumpDistanceTest {

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void updateAutoJumpDistance(PlayerTickEvent e) {
        if(e.player instanceof EntityPlayerSP){
        	ItemStack held=e.player.getHeldItemMainhand();
        	if( held!=null && held.getItem()==Items.STICK ){
    			((EntityPlayerSP)e.player).setAutoJumpDistance(3F);
        	}else
        		((EntityPlayerSP)e.player).setAutoJumpDistance(7F);
        }
    }
}
