/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug.village;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.RandomBlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleStructureProcessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.VillageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * If this mod is enabled it adds a modified town_center to the plains village pool with high weight.
 * This new town_center has lava instead of water
 */
@Mod(value = VillagePieceTest.MODID)
public class VillagePieceTest
{
    public static final String MODID = "villagepiecetest";

    public VillagePieceTest()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onStructuresFinished(VillageEvent event)
    {
        JigsawManager.field_214891_a.get(new ResourceLocation("village/plains/town_centers")).addBuilding(new Pair<>(new SingleJigsawPiece("villagepiecetest:village/plains/town_centers/plains_lava_01", ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))))),500));
    }
}
