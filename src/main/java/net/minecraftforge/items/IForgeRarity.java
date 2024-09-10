package net.minecraftforge.items;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public interface IForgeRarity {

    static IForgeRarity wrapper(Supplier<IForgeRarity> forgeRaritySupplier) {
        return new IForgeRarity() {
            @Override
            public ChatFormatting color() {
                return forgeRaritySupplier.get().color();
            }

            @Override
            public boolean equals(Object obj) {
                return forgeRaritySupplier.get().equals(obj);
            }
        };
    }

    ChatFormatting color();

    @ApiStatus.Internal
    default boolean isModded() {
        return getClass() != Rarity.class;
    }
}
