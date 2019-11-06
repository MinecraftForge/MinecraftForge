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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * If this mod is enabled it adds a modified town_center to the plains village pool with high weight.
 * This new town_center has lava instead of water
 *
 *
 * test 0: nothing
 * test 1: only custom town_center with weight 1
 * test 2: empty street JigsawPattern pool
 * test 3: custom JigsawPattern with custom town_center and directly attaching a house from the new JigsawPattern to the town_center
 */
@Mod.EventBusSubscriber(modid = VillageJigsawPiecesInitTest.MODID)
@Mod(value = VillageJigsawPiecesInitTest.MODID)
public class VillageJigsawPiecesInitTest
{
    public static final String MODID = "villagepiecesinittest";
    public static int test = 0;

    @SubscribeEvent
    public static void onStructuresFinished(JigsawPatternInitEvent event)
    {
        switch (test){
            case 1:
                if(event.isPool("minecraft:village/plains/town_centers")){
                    event.removeBuildings(Lists.newArrayList(new ResourceLocation("village/plains/town_centers/plains_fountain_01"),new ResourceLocation("village/plains/town_centers/plains_meeting_point_1"),new ResourceLocation("village/plains/town_centers/plains_meeting_point_2"),new ResourceLocation("village/plains/town_centers/plains_meeting_point_3"),new ResourceLocation("village/plains/zombie/town_centers/plains_fountain_01"),new ResourceLocation("village/plains/zombie/town_centers/plains_meeting_point_1"),new ResourceLocation("village/plains/zombie/town_centers/plains_meeting_point_2"),new ResourceLocation("village/plains/zombie/town_centers/plains_meeting_point_3")));
                    event.addBuilding(new SingleJigsawPiece("villagepiecesinittest:village/plains/town_centers/plains_lava_01", ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))))),1);
                }
                break;
            case 2:
                if(event.jigsawPoolName.equals(new ResourceLocation("village/plains/streets")))
                event.removeBuildings(Lists.newArrayList(new ResourceLocation("village/plains/streets/corner_01"),new ResourceLocation("village/plains/streets/corner_02"),new ResourceLocation("village/plains/streets/corner_03"),new ResourceLocation("village/plains/streets/straight_01"),new ResourceLocation("village/plains/streets/straight_02"),new ResourceLocation("village/plains/streets/straight_03"),new ResourceLocation("village/plains/streets/straight_04"),new ResourceLocation("village/plains/streets/straight_05"), new ResourceLocation("village/plains/streets/straight_06"),new ResourceLocation("village/plains/streets/crossroad_01"),new ResourceLocation("village/plains/streets/crossroad_02"), new ResourceLocation("village/plains/streets/crossroad_03"),new ResourceLocation("village/plains/streets/crossroad_04"),new ResourceLocation("village/plains/streets/crossroad_05"),new ResourceLocation("village/plains/streets/crossroad_06"),new ResourceLocation("village/plains/streets/turn_01")));
                break;
            case 3:
                if(event.jigsawPoolName.equals(new ResourceLocation("village/plains/streets")))
                event.removeBuildings(Lists.newArrayList(new ResourceLocation("village/plains/streets/corner_01"),new ResourceLocation("village/plains/streets/corner_02"),new ResourceLocation("village/plains/streets/corner_03"),new ResourceLocation("village/plains/streets/straight_01"),new ResourceLocation("village/plains/streets/straight_02"),new ResourceLocation("village/plains/streets/straight_03"),new ResourceLocation("village/plains/streets/straight_04"),new ResourceLocation("village/plains/streets/straight_05"), new ResourceLocation("village/plains/streets/straight_06"),new ResourceLocation("village/plains/streets/crossroad_01"),new ResourceLocation("village/plains/streets/crossroad_02"), new ResourceLocation("village/plains/streets/crossroad_03"),new ResourceLocation("village/plains/streets/crossroad_04"),new ResourceLocation("village/plains/streets/crossroad_05"),new ResourceLocation("village/plains/streets/crossroad_06"),new ResourceLocation("village/plains/streets/turn_01")));
                if(event.isPool("minecraft:village/plains/town_centers")) {
                    event.removeBuildings( Lists.newArrayList(new ResourceLocation("village/plains/town_centers/plains_fountain_01"), new ResourceLocation("village/plains/town_centers/plains_meeting_point_1"), new ResourceLocation("village/plains/town_centers/plains_meeting_point_2"), new ResourceLocation("village/plains/town_centers/plains_meeting_point_3"), new ResourceLocation("village/plains/zombie/town_centers/plains_fountain_01"), new ResourceLocation("village/plains/zombie/town_centers/plains_meeting_point_1"), new ResourceLocation("village/plains/zombie/town_centers/plains_meeting_point_2"), new ResourceLocation("village/plains/zombie/town_centers/plains_meeting_point_3")));
                    event.addBuilding(new SingleJigsawPiece("villagepiecesinittest:village/plains/town_centers/plains_lava_01", ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))))), 10);
                }
                break;
        }
    }

    @SubscribeEvent
    public static void onJigsawPool(JigsawPatternInitEvent.StructureJigsawPoolInitEvent.Village event){
        if (test == 3){
            event.register(new JigsawPattern(new ResourceLocation(MODID,"village/plains/test"),new ResourceLocation("empty"),ImmutableList.of(new Pair<>(new SingleJigsawPiece("villagepiecesinittest:village/plains/houses/plains_small_house", ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))))), 2)),JigsawPattern.PlacementBehaviour.RIGID));
        }
    }
}
