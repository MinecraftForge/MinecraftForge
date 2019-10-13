package net.minecraftforge.entity.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Abstract Boat Type to enable more types of boats.
 *
 * Register a new BoatType in BOAT_TYPES registry via RegistryEvent.Register<BoatType>.
 *
 * Hints:
 * - Don't forget to set the RegistryName of your BoatType! (example: "your_mod:boat_type_name")
 * - Don't forget to register the DispenseBehavior to {@link DispenserBlock} via static registerDispenseBehavior method
 */
public abstract class BoatType extends ForgeRegistryEntry<BoatType> {

    /**
     * The item representation of this boat.
     */
    public abstract Item getBoatItem();

    /**
     * When boat breaks it will drop these items.
     * Vanilla standard are 2 sticks and 3 planks of related wood.
     */
    public abstract IItemProvider[] getOnBreakItems();

}
