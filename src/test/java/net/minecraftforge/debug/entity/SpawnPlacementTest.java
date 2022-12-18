/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod("spawn_placement_test")
public class SpawnPlacementTest
{
    public static final boolean ENABLED = false;
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpawnPlacementTest()
    {
        if (ENABLED)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSpawnPlacementRegister);
        }
    }

    private void onSpawnPlacementRegister(SpawnPlacementRegisterEvent event)
    {
        LOGGER.info("Modifying spawn placements!");
        // AND: require zombies to spawn below y 40
        event.register(EntityType.ZOMBIE, ((entityType, level, spawnType, pos, random) -> pos.getY() < 40 && validMonsterSpawn(level, pos, entityType)), SpawnPlacementRegisterEvent.Operation.AND);
        // REPLACE: don't require darkness to spawn creepers
        event.register(EntityType.CREEPER, ((entityType, level, spawnType, pos, random) -> validMonsterSpawn(level, pos, entityType)), SpawnPlacementRegisterEvent.Operation.REPLACE);
        // OR: allow slimes to spawn in plains
        event.register(EntityType.SLIME, (entityType, level, spawnType, pos, random) -> validMonsterSpawn(level, pos, entityType) && level.getBiome(pos).is(Biomes.PLAINS));
    }

    private static boolean validMonsterSpawn(ServerLevelAccessor level, BlockPos pos, EntityType<?> type)
    {
        return level.getDifficulty() != Difficulty.PEACEFUL && level.getBlockState(pos.below()).isValidSpawn(level, pos.below(), type);
    }
}
