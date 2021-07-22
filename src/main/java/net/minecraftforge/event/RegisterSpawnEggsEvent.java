package net.minecraftforge.event;

import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

/**
 * Mod bus event that allows mods to mark their spawn eggs as behaving like vanilla spawn eggs.
 * Marking an egg as a "standard mod egg" by adding it to this event's map will cause these things to happen:<br>
 * 
 * -- The egg item will be added to vanilla's entitytype->eggitem lookup map.<br>
 * -- Creative players will be able to obtain the egg item by middle-click-picking associated entities<br>
 * -- On client distributions, a standard color tinter will be assigned to the egg item. This will occur before the
 *  {@link ColorHandlerEvent.Item} for item colors, allowing mods to override the tinting function for their egg if they desire.<br>
 *  
 * Using this event will not automatically register dispenser-spawning behavior to the spawn egg.
 * 
 * A mutable map of IDs-to-egg-suppliers is provided, allowing addon mods to remove egg suppliers if they intend to
 * associate nonstandard behavior with the egg item.<br>
 * 
 * Because this event must fire before color handlers are registered, it also must necessarily fire before
 * common configs load and before common setup.
 */
public class RegisterSpawnEggsEvent extends Event implements IModBusEvent
{
    private final Map<ResourceLocation, Supplier<? extends ForgeSpawnEggItem>> eggMap;
    
    public RegisterSpawnEggsEvent(Map<ResourceLocation, Supplier<? extends ForgeSpawnEggItem>> eggMap)
    {
        this.eggMap = eggMap;
    }
    
    /**
     * Gets the (mutable) map of standard mod spawn egg suppliers.
     * Egg suppliers added to this map will be considered "standard mod spawn eggs" -- they will be added to the vanilla
     * EntityType->SpawnEggItem lookup map, will be obtainable in creative mode by middle-click-picking their associated entities,
     * and will automatically have a color tinter assigned.
     * 
     * This does *not* automatically register the egg item to the dispenser behavior registry.
     * @return map
     */
    public Map<ResourceLocation, Supplier<? extends ForgeSpawnEggItem>> getStandardModEggs()
    {
        return this.eggMap;
    }
}
