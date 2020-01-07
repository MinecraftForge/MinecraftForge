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
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.*;
import net.minecraftforge.event.world.JigsawPatternInitEvent;
import net.minecraftforge.event.world.StructureJigsawPoolInitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * If this mod is enabled it adds a modified town_center to the plains village pool with high weight.
 * This new town_center has lava instead of water
 *
 * note all changes only apply to plains village
 * test 0: nothing
 * test 1: only custom town_center with weight 1
 * test 2: empty street JigsawPattern pool
 * test 3: custom JigsawPattern with custom town_center and directly attaching a house from the new JigsawPattern to the town_center
 * test 4: added to custom farms to categories
 * test 5: added new category with house
 */
@Mod.EventBusSubscriber(modid = VillageJigsawPiecesInitTest.MODID)
@Mod(value = VillageJigsawPiecesInitTest.MODID)
public class VillageJigsawPiecesInitTest
{
    public static final String MODID = "villagepiecesinittest";
    public static int test = 4;

    @SubscribeEvent
    public static void onStructuresFinished(JigsawPatternInitEvent event)
    {
        switch (test){
            case 1:
                if(event.isPool("minecraft:village/plains/town_centers")){
                    event.removeCategory(new ResourceLocation("meeting_point"));
                    event.removeBuildings(new ResourceLocation("fountain"),new ResourceLocation("village/plains/town_centers/plains_fountain_01"),new ResourceLocation("village/plains/zombie/town_centers/plains_fountain_01"));
                    event.addBuildings(new ResourceLocation("fountain"),Pair.of(new SingleJigsawPiece("villagepiecesinittest:village/plains/town_centers/plains_lava_01", ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))))),2));
                }
                break;
            case 3:
                if(event.isPool("minecraft:village/plains/town_centers")) {
                    event.removeCategory(new ResourceLocation("meeting_point"));
                    event.removeBuildings(new ResourceLocation("fountain"),new ResourceLocation("village/plains/town_centers/plains_fountain_01"),new ResourceLocation("village/plains/zombie/town_centers/plains_fountain_01"));
                    event.addBuildings(new ResourceLocation("fountain"),Pair.of(new SingleJigsawPiece("villagepiecesinittest:village/plains/town_centers/plains_lava_01", ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))))),2));
                }
            case 2:
                if(event.jigsawPatternRegistryName.equals(new ResourceLocation("village/plains/streets"))){
                    event.removeCategory(new ResourceLocation("straight"));
                    event.removeCategory(new ResourceLocation("corner"));
                    event.removeCategory(new ResourceLocation("crossroad"));
                    event.removeCategory(new ResourceLocation("turn"));
                }
                break;
            case 4:
                if(event.jigsawPatternRegistryName.equals(new ResourceLocation("village/plains/houses"))){
                    event.addBuildings(new ResourceLocation("large_farm"), Pair.of(new SingleJigsawPiece(MODID+"village/plains/houses/plains_large_farm_1"),10));
                    event.addBuildings(new ResourceLocation("small_farm"), Pair.of(new SingleJigsawPiece(MODID+"village/plains/houses/plains_small_farm_1"),10));
                }
                break;
            case 5:
                if(event.jigsawPatternRegistryName.equals(new ResourceLocation("village/plains/houses"))){
                    event.addCategory(new ResourceLocation(MODID,"test"),100, new Pair<>(new SingleJigsawPiece(MODID+"village/plains/houses/plains_small_farm_1"),10));
                }
                break;
            default:
                break;
        }
    }

    @SubscribeEvent
    public static void onJigsawPool(StructureJigsawPoolInitEvent.Village event){
        if (test == 3){
            event.register(new JigsawPattern(new ResourceLocation(MODID,"village/plains/test"),new ResourceLocation("empty"),ImmutableList.of(new Pair<>(new SingleJigsawPiece(new ResourceLocation(MODID,"village/plains/houses/plains_small_house"), ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())))), JigsawPattern.PlacementBehaviour.RIGID), 2)),JigsawPattern.PlacementBehaviour.RIGID));
        }
    }
}
