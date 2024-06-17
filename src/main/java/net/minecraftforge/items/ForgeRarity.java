package net.minecraftforge.items;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.registries.ForgeRegistries;

public record ForgeRarity(ChatFormatting color) implements IForgeRarity {
    public static final Codec<ForgeRarity> RARITY_CODEC = ForgeRegistries.FORGE_RARITY.get().getCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeRarity> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(RARITY_CODEC);
}
