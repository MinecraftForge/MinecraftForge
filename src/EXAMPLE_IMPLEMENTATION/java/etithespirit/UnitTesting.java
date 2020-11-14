package etithespirit;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEmittedSoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.Entity;


import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("unittesting")
public class UnitTesting
{
    private static final SoundEvent SSSSS = new SoundEvent(new ResourceLocation("unittesting:entity.creeper.demoman_sssss"));
	
	static {
		SSSSS.setRegistryName("unittesting", "entity.creeper.demoman_sssss");
	}
    
    public UnitTesting() 
    {
        MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(Implementation.class);
		GameRegistry.findRegistry(SoundEvent.class).register(SSSSS);
    }
    
    public static class Implementation 
    {
	    @SubscribeEvent
        public static void onEntityPlayedSound(EntityEmittedSoundEvent evt) 
        {
            Entity source = evt.getEntity();
            if (source instanceof CreeperEntity)
            {
                CreeperEntity awwMan = (CreeperEntity)source;
                if (awwMan.isCharged() && evt.getSound().getName().getPath().equals("entity.creeper.primed")) 
                {
                    evt.setSound(SSSSS);
                    evt.setPitch(1f);
                }
            }
        }
    	
    }
}
