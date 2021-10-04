/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeSpawnEggItem extends SpawnEggItem
{
    private static final List<ForgeSpawnEggItem> MOD_EGGS = new ArrayList<>();
    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public ForgeSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props)
    {
        super((EntityType<? extends Mob>) null, backgroundColor, highlightColor, props);
        this.typeSupplier = type;

        MOD_EGGS.add(this);
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag tag)
    {
        EntityType<?> type = super.getType(tag);
        return type != null ? type : typeSupplier.get();
    }

    @Nullable
    protected DispenseItemBehavior createDispenseBehavior()
    {
        return DEFAULT_DISPENSE_BEHAVIOR;
    }

    public static Iterable<ForgeSpawnEggItem> getModEggs()
    {
        return MOD_EGGS;
    }

    public static void finalizeModEggs()
    {
        MOD_EGGS.forEach(egg ->
        {
            DispenseItemBehavior dispenseBehavior = egg.createDispenseBehavior();
            if (dispenseBehavior != null)
            {
                DispenserBlock.registerBehavior(egg, dispenseBehavior);
            }

            BY_ID.put(egg.typeSupplier.get(), egg);
        });
    }



    private static final DispenseItemBehavior DEFAULT_DISPENSE_BEHAVIOR = (source, stack) ->
    {
        Direction face = source.getBlockState().getValue(DispenserBlock.FACING);
        EntityType<?> type = ((SpawnEggItem)stack.getItem()).getType(stack.getTag());

        try
        {
            type.spawn(source.getLevel(), stack, null, source.getPos().relative(face), MobSpawnType.DISPENSER, face != Direction.UP, false);
        }
        catch (Exception exception)
        {
            DispenseItemBehavior.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.getPos(), exception);
            return ItemStack.EMPTY;
        }

        stack.shrink(1);
        source.getLevel().gameEvent(GameEvent.ENTITY_PLACE, source.getPos());
        return stack;
    };
}