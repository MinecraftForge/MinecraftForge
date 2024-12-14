/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

@Mod(LateBoundIdMapperTest.MODID)
@GameTestHolder("forge." + LateBoundIdMapperTest.MODID)
public class LateBoundIdMapperTest extends BaseTestMod {
    public LateBoundIdMapperTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    public static final String MODID = "late_bound_id_mapper";
    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void compound(GameTestHelper helper) {
        var ID_MAPPER = ItemTintSources.ID_MAPPER;

        ID_MAPPER.put(rl("test"), Test.CODEC);

        var expected = new Test(10);

        var encoded = ItemTintSources.CODEC.encodeStart(JsonOps.INSTANCE, expected).result().orElse(null);
        if (encoded == null)
            helper.fail("Failed to encode test value");

        var found = ItemTintSources.CODEC.decode(JsonOps.INSTANCE, encoded).result().orElse(Pair.of(null, null)).getFirst();
        if (found == null)
            helper.fail("Failed to decode test value");

        if (!found.equals(expected))
            helper.fail("Decoded value was not the same as encoded");

        helper.succeed();
    }

    private record Test(int value) implements ItemTintSource {
        public static final MapCodec<Test> CODEC = RecordCodecBuilder.mapCodec(
            b -> b.group(Codec.INT.fieldOf("value").forGetter(Test::value)).apply(b, Test::new)
        );

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            return this.value;
        }

        @Override
        public MapCodec<Test> type() {
            return CODEC;
        }
    }
}
