/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.entity;

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
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EntityEmittedSoundEventTest.MODID)
public class EntityEmittedSoundEventTest
{
    public static final String MODID = "entity_emitted_sound_event_test";
    private static final boolean ENABLE = true;
    
    private static final DeferredRegister<SoundEvent> SOUND_REGISTRY;
    private static final RegistryObject<SoundEvent> SAWTOOTH_WAVE;
    
    static 
    {
        // Do not create registries if the mod is not enabled.
        if (!ENABLE) 
        {
            SOUND_REGISTRY = null;
            SAWTOOTH_WAVE = null;
        } 
        else 
        {
            SOUND_REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
            SAWTOOTH_WAVE = SOUND_REGISTRY.register("sawtooth", () -> new SoundEvent(new ResourceLocation(MODID, "sawtooth")));
        }
    }
    
    public EntityEmittedSoundEventTest() 
    {
        // Do not register events if the mod is not enabled.
        if (!ENABLE) return;
        MinecraftForge.EVENT_BUS.register(this);
        SOUND_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    @SubscribeEvent
    public void onEntityPlayedSound(EntityEmittedSoundEvent evt) 
    {
        if (!ENABLE) return;
        
        Entity source = evt.getEntity();
        if (source instanceof CreeperEntity)
        {
            CreeperEntity creeper = (CreeperEntity)source;
            if (creeper.isCharged() && evt.getSound().getName().getPath().equals("entity.creeper.primed")) 
            {
                // docs say to use a method to test if it exists, but in this case I will not do this
                // If it doesn't exist, it needs to error out because that means there's a problem.
                evt.setSound(SAWTOOTH_WAVE.get());
                evt.setPitch(1f);
            }
        }
    }
}
