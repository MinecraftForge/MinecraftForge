package net.minecraftforge.common;

import java.util.function.Supplier;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;

public class ForgeSpawnEggItem extends SpawnEggItem
{    
    private final Supplier<? extends EntityType<?>> typeSupplier;

    /**
     * Registry-friendly spawn egg constructor.
     * @param typeSupplier A RegistryObject or other entity type supplier to retrieve the spawn egg's associated entity type later
     * @param foregroundColor The foreground color for the egg texture
     * @param backgroundColor The background (spots) color for the egg texture
     * @param properties The item properties for the egg item
     * **/ 
    public ForgeSpawnEggItem(final Supplier<? extends EntityType<?>> typeSupplier, final int backgroundColor, final int foregroundColor, final Item.Properties properties)
    {
       super(null, backgroundColor, foregroundColor, properties);
       this.typeSupplier = java.util.Objects.requireNonNull(typeSupplier);
    }

    @Override
    public EntityType<?> getType(CompoundNBT nbt)
    {
        // run the vanilla logic to allow type overrides via itemstack NBT
        EntityType<?> type = super.getType(nbt);
        // otherwise return our supplied type
        return type != null ? type : this.typeSupplier.get();
    }
    
    public Supplier<? extends EntityType<?>> getTypeSupplier()
    {
        return this.typeSupplier;
    }
}