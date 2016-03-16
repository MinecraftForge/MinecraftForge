package net.minecraftforge.common;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public class BlockStates
{
    public static final IBlockState air;
    public static final IBlockState stone;
    public static final IBlockState stone_granite;
    public static final IBlockState stone_smoothgranite;
    public static final IBlockState stone_diorite;
    public static final IBlockState stone_smoothdiorite;
    public static final IBlockState stone_andesite;
    public static final IBlockState stone_smoothandesite;
    public static final IBlockState grass;
    public static final IBlockState dirt;
    public static final IBlockState dirt_coarsedirt;
    public static final IBlockState dirt_podzol;
    public static final IBlockState cobblestone;
    public static final IBlockState planks_oak;
    public static final IBlockState planks_spruce;
    public static final IBlockState planks_birch;
    public static final IBlockState planks_jungle;
    public static final IBlockState planks_acacia;
    public static final IBlockState planks_darkoak;
    public static final IBlockState sapling_oak_stage_0;
    public static final IBlockState sapling_spruce_stage_0;
    public static final IBlockState sapling_birch_stage_0;
    public static final IBlockState sapling_jungle_stage_0;
    public static final IBlockState sapling_acacia_stage_0;
    public static final IBlockState sapling_darkoak_stage_0;
    public static final IBlockState sapling_oak_stage_1;
    public static final IBlockState sapling_spruce_stage_1;
    public static final IBlockState sapling_birch_stage_1;
    public static final IBlockState sapling_jungle_stage_1;
    public static final IBlockState sapling_acacia_stage_1;
    public static final IBlockState sapling_darkoak_stage_1;
    public static final IBlockState bedrock;
    public static final IBlockState flowing_water_level_0;
    public static final IBlockState flowing_water_level_1;
    public static final IBlockState flowing_water_level_2;
    public static final IBlockState flowing_water_level_3;
    public static final IBlockState flowing_water_level_4;
    public static final IBlockState flowing_water_level_5;
    public static final IBlockState flowing_water_level_6;
    public static final IBlockState flowing_water_level_7;
    public static final IBlockState flowing_water_level_8;
    public static final IBlockState flowing_water_level_9;
    public static final IBlockState flowing_water_level_10;
    public static final IBlockState flowing_water_level_11;
    public static final IBlockState flowing_water_level_12;
    public static final IBlockState flowing_water_level_13;
    public static final IBlockState flowing_water_level_14;
    public static final IBlockState flowing_water_level_15;
    public static final IBlockState water_level_0;
    public static final IBlockState water_level_1;
    public static final IBlockState water_level_2;
    public static final IBlockState water_level_3;
    public static final IBlockState water_level_4;
    public static final IBlockState water_level_5;
    public static final IBlockState water_level_6;
    public static final IBlockState water_level_7;
    public static final IBlockState water_level_8;
    public static final IBlockState water_level_9;
    public static final IBlockState water_level_10;
    public static final IBlockState water_level_11;
    public static final IBlockState water_level_12;
    public static final IBlockState water_level_13;
    public static final IBlockState water_level_14;
    public static final IBlockState water_level_15;
    public static final IBlockState flowing_lava_level_0;
    public static final IBlockState flowing_lava_level_1;
    public static final IBlockState flowing_lava_level_2;
    public static final IBlockState flowing_lava_level_3;
    public static final IBlockState flowing_lava_level_4;
    public static final IBlockState flowing_lava_level_5;
    public static final IBlockState flowing_lava_level_6;
    public static final IBlockState flowing_lava_level_7;
    public static final IBlockState flowing_lava_level_8;
    public static final IBlockState flowing_lava_level_9;
    public static final IBlockState flowing_lava_level_10;
    public static final IBlockState flowing_lava_level_11;
    public static final IBlockState flowing_lava_level_12;
    public static final IBlockState flowing_lava_level_13;
    public static final IBlockState flowing_lava_level_14;
    public static final IBlockState flowing_lava_level_15;
    public static final IBlockState lava_level_0;
    public static final IBlockState lava_level_1;
    public static final IBlockState lava_level_2;
    public static final IBlockState lava_level_3;
    public static final IBlockState lava_level_4;
    public static final IBlockState lava_level_5;
    public static final IBlockState lava_level_6;
    public static final IBlockState lava_level_7;
    public static final IBlockState lava_level_8;
    public static final IBlockState lava_level_9;
    public static final IBlockState lava_level_10;
    public static final IBlockState lava_level_11;
    public static final IBlockState lava_level_12;
    public static final IBlockState lava_level_13;
    public static final IBlockState lava_level_14;
    public static final IBlockState lava_level_15;
    public static final IBlockState sand;
    public static final IBlockState sand_redsand;
    public static final IBlockState gravel;
    public static final IBlockState gold_ore;
    public static final IBlockState iron_ore;
    public static final IBlockState coal_ore;
    public static final IBlockState log_y_oak;
    public static final IBlockState log_y_spruce;
    public static final IBlockState log_y_birch;
    public static final IBlockState log_y_jungle;
    public static final IBlockState log_x_oak;
    public static final IBlockState log_x_spruce;
    public static final IBlockState log_x_birch;
    public static final IBlockState log_x_jungle;
    public static final IBlockState log_z_oak;
    public static final IBlockState log_z_spruce;
    public static final IBlockState log_z_birch;
    public static final IBlockState log_z_jungle;
    public static final IBlockState log_none_oak;
    public static final IBlockState log_none_spruce;
    public static final IBlockState log_none_birch;
    public static final IBlockState log_none_jungle;
    public static final IBlockState log2_y_acacia;
    public static final IBlockState log2_y_darkoak;
    public static final IBlockState log2_x_acacia;
    public static final IBlockState log2_x_darkoak;
    public static final IBlockState log2_z_acacia;
    public static final IBlockState log2_z_darkoak;
    public static final IBlockState log2_none_acacia;
    public static final IBlockState log2_none_darkoak;
    public static final IBlockState leaves_oak_decayable;
    public static final IBlockState leaves_spruce_decayable;
    public static final IBlockState leaves_birch_decayable;
    public static final IBlockState leaves_jungle_decayable;
    public static final IBlockState leaves_oak;
    public static final IBlockState leaves_spruce;
    public static final IBlockState leaves_birch;
    public static final IBlockState leaves_jungle;
    public static final IBlockState leaves_oak_checkdecay_decayable;
    public static final IBlockState leaves_spruce_checkdecay_decayable;
    public static final IBlockState leaves_birch_checkdecay_decayable;
    public static final IBlockState leaves_jungle_checkdecay_decayable;
    public static final IBlockState leaves_oak_checkdecay;
    public static final IBlockState leaves_spruce_checkdecay;
    public static final IBlockState leaves_birch_checkdecay;
    public static final IBlockState leaves_jungle_checkdecay;
    public static final IBlockState leaves2_acacia_decayable;
    public static final IBlockState leaves2_darkoak_decayable;
    public static final IBlockState leaves2_acacia;
    public static final IBlockState leaves2_darkoak;
    public static final IBlockState leaves2_acacia_checkdecay_decayable;
    public static final IBlockState leaves2_darkoak_checkdecay_decayable;
    public static final IBlockState leaves2_acacia_checkdecay;
    public static final IBlockState leaves2_darkoak_checkdecay;
    public static final IBlockState sponge;
    public static final IBlockState sponge_wet;
    public static final IBlockState glass;
    public static final IBlockState lapis_ore;
    public static final IBlockState lapis_block;
    public static final IBlockState dispenser_down;
    public static final IBlockState dispenser_up;
    public static final IBlockState dispenser_north;
    public static final IBlockState dispenser_south;
    public static final IBlockState dispenser_west;
    public static final IBlockState dispenser_east;
    public static final IBlockState dispenser_down_triggered;
    public static final IBlockState dispenser_up_triggered;
    public static final IBlockState dispenser_north_triggered;
    public static final IBlockState dispenser_south_triggered;
    public static final IBlockState dispenser_west_triggered;
    public static final IBlockState dispenser_east_triggered;
    public static final IBlockState sandstone;
    public static final IBlockState sandstone_chiseledsandstone;
    public static final IBlockState sandstone_smoothsandstone;
    public static final IBlockState noteblock;
    public static final IBlockState bed_foot_south;
    public static final IBlockState bed_foot_west;
    public static final IBlockState bed_foot_north;
    public static final IBlockState bed_foot_east;
    public static final IBlockState bed_head_south;
    public static final IBlockState bed_head_west;
    public static final IBlockState bed_head_north;
    public static final IBlockState bed_head_east;
    public static final IBlockState bed_head_south_occupied;
    public static final IBlockState bed_head_west_occupied;
    public static final IBlockState bed_head_north_occupied;
    public static final IBlockState bed_head_east_occupied;
    public static final IBlockState golden_rail_northsouth;
    public static final IBlockState golden_rail_eastwest;
    public static final IBlockState golden_rail_ascendingeast;
    public static final IBlockState golden_rail_ascendingwest;
    public static final IBlockState golden_rail_ascendingnorth;
    public static final IBlockState golden_rail_ascendingsouth;
    public static final IBlockState golden_rail_northsouth_powered;
    public static final IBlockState golden_rail_eastwest_powered;
    public static final IBlockState golden_rail_ascendingeast_powered;
    public static final IBlockState golden_rail_ascendingwest_powered;
    public static final IBlockState golden_rail_ascendingnorth_powered;
    public static final IBlockState golden_rail_ascendingsouth_powered;
    public static final IBlockState detector_rail_northsouth;
    public static final IBlockState detector_rail_eastwest;
    public static final IBlockState detector_rail_ascendingeast;
    public static final IBlockState detector_rail_ascendingwest;
    public static final IBlockState detector_rail_ascendingnorth;
    public static final IBlockState detector_rail_ascendingsouth;
    public static final IBlockState detector_rail_northsouth_powered;
    public static final IBlockState detector_rail_eastwest_powered;
    public static final IBlockState detector_rail_ascendingeast_powered;
    public static final IBlockState detector_rail_ascendingwest_powered;
    public static final IBlockState detector_rail_ascendingnorth_powered;
    public static final IBlockState detector_rail_ascendingsouth_powered;
    public static final IBlockState sticky_piston_down;
    public static final IBlockState sticky_piston_up;
    public static final IBlockState sticky_piston_north;
    public static final IBlockState sticky_piston_south;
    public static final IBlockState sticky_piston_west;
    public static final IBlockState sticky_piston_east;
    public static final IBlockState sticky_piston_down_extended;
    public static final IBlockState sticky_piston_up_extended;
    public static final IBlockState sticky_piston_north_extended;
    public static final IBlockState sticky_piston_south_extended;
    public static final IBlockState sticky_piston_west_extended;
    public static final IBlockState sticky_piston_east_extended;
    public static final IBlockState web;
    public static final IBlockState tallgrass_deadbush;
    public static final IBlockState tallgrass_tallgrass;
    public static final IBlockState tallgrass_fern;
    public static final IBlockState deadbush;
    public static final IBlockState piston_down;
    public static final IBlockState piston_up;
    public static final IBlockState piston_north;
    public static final IBlockState piston_south;
    public static final IBlockState piston_west;
    public static final IBlockState piston_east;
    public static final IBlockState piston_down_extended;
    public static final IBlockState piston_up_extended;
    public static final IBlockState piston_north_extended;
    public static final IBlockState piston_south_extended;
    public static final IBlockState piston_west_extended;
    public static final IBlockState piston_east_extended;
    public static final IBlockState piston_head_normal_down;
    public static final IBlockState piston_head_normal_up;
    public static final IBlockState piston_head_normal_north;
    public static final IBlockState piston_head_normal_south;
    public static final IBlockState piston_head_normal_west;
    public static final IBlockState piston_head_normal_east;
    public static final IBlockState piston_head_sticky_down;
    public static final IBlockState piston_head_sticky_up;
    public static final IBlockState piston_head_sticky_north;
    public static final IBlockState piston_head_sticky_south;
    public static final IBlockState piston_head_sticky_west;
    public static final IBlockState piston_head_sticky_east;
    public static final IBlockState wool_white;
    public static final IBlockState wool_orange;
    public static final IBlockState wool_magenta;
    public static final IBlockState wool_lightblue;
    public static final IBlockState wool_yellow;
    public static final IBlockState wool_lime;
    public static final IBlockState wool_pink;
    public static final IBlockState wool_gray;
    public static final IBlockState wool_silver;
    public static final IBlockState wool_cyan;
    public static final IBlockState wool_purple;
    public static final IBlockState wool_blue;
    public static final IBlockState wool_brown;
    public static final IBlockState wool_green;
    public static final IBlockState wool_red;
    public static final IBlockState wool_black;
    public static final IBlockState piston_extension_normal_down;
    public static final IBlockState piston_extension_normal_up;
    public static final IBlockState piston_extension_normal_north;
    public static final IBlockState piston_extension_normal_south;
    public static final IBlockState piston_extension_normal_west;
    public static final IBlockState piston_extension_normal_east;
    public static final IBlockState piston_extension_sticky_down;
    public static final IBlockState piston_extension_sticky_up;
    public static final IBlockState piston_extension_sticky_north;
    public static final IBlockState piston_extension_sticky_south;
    public static final IBlockState piston_extension_sticky_west;
    public static final IBlockState piston_extension_sticky_east;
    public static final IBlockState yellow_flower;
    public static final IBlockState red_flower_poppy;
    public static final IBlockState red_flower_blueorchid;
    public static final IBlockState red_flower_allium;
    public static final IBlockState red_flower_houstonia;
    public static final IBlockState red_flower_redtulip;
    public static final IBlockState red_flower_orangetulip;
    public static final IBlockState red_flower_whitetulip;
    public static final IBlockState red_flower_pinktulip;
    public static final IBlockState red_flower_oxeyedaisy;
    public static final IBlockState brown_mushroom;
    public static final IBlockState red_mushroom;
    public static final IBlockState gold_block;
    public static final IBlockState iron_block;
    public static final IBlockState double_stone_slab_stone;
    public static final IBlockState double_stone_slab_sandstone;
    public static final IBlockState double_stone_slab_woodold;
    public static final IBlockState double_stone_slab_cobblestone;
    public static final IBlockState double_stone_slab_brick;
    public static final IBlockState double_stone_slab_stonebrick;
    public static final IBlockState double_stone_slab_netherbrick;
    public static final IBlockState double_stone_slab_quartz;
    public static final IBlockState double_stone_slab_stone_seamless;
    public static final IBlockState double_stone_slab_sandstone_seamless;
    public static final IBlockState double_stone_slab_woodold_seamless;
    public static final IBlockState double_stone_slab_cobblestone_seamless;
    public static final IBlockState double_stone_slab_brick_seamless;
    public static final IBlockState double_stone_slab_stonebrick_seamless;
    public static final IBlockState double_stone_slab_netherbrick_seamless;
    public static final IBlockState double_stone_slab_quartz_seamless;
    public static final IBlockState stone_slab_bottom_stone;
    public static final IBlockState stone_slab_bottom_sandstone;
    public static final IBlockState stone_slab_bottom_woodold;
    public static final IBlockState stone_slab_bottom_cobblestone;
    public static final IBlockState stone_slab_bottom_brick;
    public static final IBlockState stone_slab_bottom_stonebrick;
    public static final IBlockState stone_slab_bottom_netherbrick;
    public static final IBlockState stone_slab_bottom_quartz;
    public static final IBlockState stone_slab_top_stone;
    public static final IBlockState stone_slab_top_sandstone;
    public static final IBlockState stone_slab_top_woodold;
    public static final IBlockState stone_slab_top_cobblestone;
    public static final IBlockState stone_slab_top_brick;
    public static final IBlockState stone_slab_top_stonebrick;
    public static final IBlockState stone_slab_top_netherbrick;
    public static final IBlockState stone_slab_top_quartz;
    public static final IBlockState brick_block;
    public static final IBlockState tnt;
    public static final IBlockState tnt_explode;
    public static final IBlockState bookshelf;
    public static final IBlockState mossy_cobblestone;
    public static final IBlockState obsidian;
    public static final IBlockState torch_east;
    public static final IBlockState torch_west;
    public static final IBlockState torch_south;
    public static final IBlockState torch_north;
    public static final IBlockState torch_up;
    public static final IBlockState fire_age_0;
    public static final IBlockState fire_age_1;
    public static final IBlockState fire_age_2;
    public static final IBlockState fire_age_3;
    public static final IBlockState fire_age_4;
    public static final IBlockState fire_age_5;
    public static final IBlockState fire_age_6;
    public static final IBlockState fire_age_7;
    public static final IBlockState fire_age_8;
    public static final IBlockState fire_age_9;
    public static final IBlockState fire_age_10;
    public static final IBlockState fire_age_11;
    public static final IBlockState fire_age_12;
    public static final IBlockState fire_age_13;
    public static final IBlockState fire_age_14;
    public static final IBlockState fire_age_15;
    public static final IBlockState mob_spawner;
    public static final IBlockState oak_stairs_bottom_east;
    public static final IBlockState oak_stairs_bottom_west;
    public static final IBlockState oak_stairs_bottom_south;
    public static final IBlockState oak_stairs_bottom_north;
    public static final IBlockState oak_stairs_top_east;
    public static final IBlockState oak_stairs_top_west;
    public static final IBlockState oak_stairs_top_south;
    public static final IBlockState oak_stairs_top_north;
    public static final IBlockState chest_north;
    public static final IBlockState chest_south;
    public static final IBlockState chest_west;
    public static final IBlockState chest_east;
    public static final IBlockState redstone_wire_power_0;
    public static final IBlockState redstone_wire_power_1;
    public static final IBlockState redstone_wire_power_2;
    public static final IBlockState redstone_wire_power_3;
    public static final IBlockState redstone_wire_power_4;
    public static final IBlockState redstone_wire_power_5;
    public static final IBlockState redstone_wire_power_6;
    public static final IBlockState redstone_wire_power_7;
    public static final IBlockState redstone_wire_power_8;
    public static final IBlockState redstone_wire_power_9;
    public static final IBlockState redstone_wire_power_10;
    public static final IBlockState redstone_wire_power_11;
    public static final IBlockState redstone_wire_power_12;
    public static final IBlockState redstone_wire_power_13;
    public static final IBlockState redstone_wire_power_14;
    public static final IBlockState redstone_wire_power_15;
    public static final IBlockState diamond_ore;
    public static final IBlockState diamond_block;
    public static final IBlockState crafting_table;
    public static final IBlockState wheat_age_0;
    public static final IBlockState wheat_age_1;
    public static final IBlockState wheat_age_2;
    public static final IBlockState wheat_age_3;
    public static final IBlockState wheat_age_4;
    public static final IBlockState wheat_age_5;
    public static final IBlockState wheat_age_6;
    public static final IBlockState wheat_age_7;
    public static final IBlockState farmland_moisture_0;
    public static final IBlockState farmland_moisture_1;
    public static final IBlockState farmland_moisture_2;
    public static final IBlockState farmland_moisture_3;
    public static final IBlockState farmland_moisture_4;
    public static final IBlockState farmland_moisture_5;
    public static final IBlockState farmland_moisture_6;
    public static final IBlockState farmland_moisture_7;
    public static final IBlockState furnace_north;
    public static final IBlockState furnace_south;
    public static final IBlockState furnace_west;
    public static final IBlockState furnace_east;
    public static final IBlockState lit_furnace_north;
    public static final IBlockState lit_furnace_south;
    public static final IBlockState lit_furnace_west;
    public static final IBlockState lit_furnace_east;
    public static final IBlockState standing_sign_rotation_0;
    public static final IBlockState standing_sign_rotation_1;
    public static final IBlockState standing_sign_rotation_2;
    public static final IBlockState standing_sign_rotation_3;
    public static final IBlockState standing_sign_rotation_4;
    public static final IBlockState standing_sign_rotation_5;
    public static final IBlockState standing_sign_rotation_6;
    public static final IBlockState standing_sign_rotation_7;
    public static final IBlockState standing_sign_rotation_8;
    public static final IBlockState standing_sign_rotation_9;
    public static final IBlockState standing_sign_rotation_10;
    public static final IBlockState standing_sign_rotation_11;
    public static final IBlockState standing_sign_rotation_12;
    public static final IBlockState standing_sign_rotation_13;
    public static final IBlockState standing_sign_rotation_14;
    public static final IBlockState standing_sign_rotation_15;
    public static final IBlockState oak_door_lower_left_east;
    public static final IBlockState oak_door_lower_left_south;
    public static final IBlockState oak_door_lower_left_west;
    public static final IBlockState oak_door_lower_left_north;
    public static final IBlockState oak_door_lower_left_east_open;
    public static final IBlockState oak_door_lower_left_south_open;
    public static final IBlockState oak_door_lower_left_west_open;
    public static final IBlockState oak_door_lower_left_north_open;
    public static final IBlockState oak_door_upper_left_north;
    public static final IBlockState oak_door_upper_right_north;
    public static final IBlockState oak_door_upper_left_north_powered;
    public static final IBlockState oak_door_upper_right_north_powered;
    public static final IBlockState spruce_door_lower_left_east;
    public static final IBlockState spruce_door_lower_left_south;
    public static final IBlockState spruce_door_lower_left_west;
    public static final IBlockState spruce_door_lower_left_north;
    public static final IBlockState spruce_door_lower_left_east_open;
    public static final IBlockState spruce_door_lower_left_south_open;
    public static final IBlockState spruce_door_lower_left_west_open;
    public static final IBlockState spruce_door_lower_left_north_open;
    public static final IBlockState spruce_door_upper_left_north;
    public static final IBlockState spruce_door_upper_right_north;
    public static final IBlockState spruce_door_upper_left_north_powered;
    public static final IBlockState spruce_door_upper_right_north_powered;
    public static final IBlockState birch_door_lower_left_east;
    public static final IBlockState birch_door_lower_left_south;
    public static final IBlockState birch_door_lower_left_west;
    public static final IBlockState birch_door_lower_left_north;
    public static final IBlockState birch_door_lower_left_east_open;
    public static final IBlockState birch_door_lower_left_south_open;
    public static final IBlockState birch_door_lower_left_west_open;
    public static final IBlockState birch_door_lower_left_north_open;
    public static final IBlockState birch_door_upper_left_north;
    public static final IBlockState birch_door_upper_right_north;
    public static final IBlockState birch_door_upper_left_north_powered;
    public static final IBlockState birch_door_upper_right_north_powered;
    public static final IBlockState jungle_door_lower_left_east;
    public static final IBlockState jungle_door_lower_left_south;
    public static final IBlockState jungle_door_lower_left_west;
    public static final IBlockState jungle_door_lower_left_north;
    public static final IBlockState jungle_door_lower_left_east_open;
    public static final IBlockState jungle_door_lower_left_south_open;
    public static final IBlockState jungle_door_lower_left_west_open;
    public static final IBlockState jungle_door_lower_left_north_open;
    public static final IBlockState jungle_door_upper_left_north;
    public static final IBlockState jungle_door_upper_right_north;
    public static final IBlockState jungle_door_upper_left_north_powered;
    public static final IBlockState jungle_door_upper_right_north_powered;
    public static final IBlockState acacia_door_lower_left_east;
    public static final IBlockState acacia_door_lower_left_south;
    public static final IBlockState acacia_door_lower_left_west;
    public static final IBlockState acacia_door_lower_left_north;
    public static final IBlockState acacia_door_lower_left_east_open;
    public static final IBlockState acacia_door_lower_left_south_open;
    public static final IBlockState acacia_door_lower_left_west_open;
    public static final IBlockState acacia_door_lower_left_north_open;
    public static final IBlockState acacia_door_upper_left_north;
    public static final IBlockState acacia_door_upper_right_north;
    public static final IBlockState acacia_door_upper_left_north_powered;
    public static final IBlockState acacia_door_upper_right_north_powered;
    public static final IBlockState dark_oak_door_lower_left_east;
    public static final IBlockState dark_oak_door_lower_left_south;
    public static final IBlockState dark_oak_door_lower_left_west;
    public static final IBlockState dark_oak_door_lower_left_north;
    public static final IBlockState dark_oak_door_lower_left_east_open;
    public static final IBlockState dark_oak_door_lower_left_south_open;
    public static final IBlockState dark_oak_door_lower_left_west_open;
    public static final IBlockState dark_oak_door_lower_left_north_open;
    public static final IBlockState dark_oak_door_upper_left_north;
    public static final IBlockState dark_oak_door_upper_right_north;
    public static final IBlockState dark_oak_door_upper_left_north_powered;
    public static final IBlockState dark_oak_door_upper_right_north_powered;
    public static final IBlockState ladder_north;
    public static final IBlockState ladder_south;
    public static final IBlockState ladder_west;
    public static final IBlockState ladder_east;
    public static final IBlockState rail_northsouth;
    public static final IBlockState rail_eastwest;
    public static final IBlockState rail_ascendingeast;
    public static final IBlockState rail_ascendingwest;
    public static final IBlockState rail_ascendingnorth;
    public static final IBlockState rail_ascendingsouth;
    public static final IBlockState rail_southeast;
    public static final IBlockState rail_southwest;
    public static final IBlockState rail_northwest;
    public static final IBlockState rail_northeast;
    public static final IBlockState stone_stairs_bottom_east;
    public static final IBlockState stone_stairs_bottom_west;
    public static final IBlockState stone_stairs_bottom_south;
    public static final IBlockState stone_stairs_bottom_north;
    public static final IBlockState stone_stairs_top_east;
    public static final IBlockState stone_stairs_top_west;
    public static final IBlockState stone_stairs_top_south;
    public static final IBlockState stone_stairs_top_north;
    public static final IBlockState wall_sign_north;
    public static final IBlockState wall_sign_south;
    public static final IBlockState wall_sign_west;
    public static final IBlockState wall_sign_east;
    public static final IBlockState lever_downx;
    public static final IBlockState lever_east;
    public static final IBlockState lever_west;
    public static final IBlockState lever_south;
    public static final IBlockState lever_north;
    public static final IBlockState lever_upz;
    public static final IBlockState lever_upx;
    public static final IBlockState lever_downz;
    public static final IBlockState lever_downx_powered;
    public static final IBlockState lever_east_powered;
    public static final IBlockState lever_west_powered;
    public static final IBlockState lever_south_powered;
    public static final IBlockState lever_north_powered;
    public static final IBlockState lever_upz_powered;
    public static final IBlockState lever_upx_powered;
    public static final IBlockState lever_downz_powered;
    public static final IBlockState stone_pressure_plate;
    public static final IBlockState stone_pressure_plate_powered;
    public static final IBlockState iron_door_lower_left_east;
    public static final IBlockState iron_door_lower_left_south;
    public static final IBlockState iron_door_lower_left_west;
    public static final IBlockState iron_door_lower_left_north;
    public static final IBlockState iron_door_lower_left_east_open;
    public static final IBlockState iron_door_lower_left_south_open;
    public static final IBlockState iron_door_lower_left_west_open;
    public static final IBlockState iron_door_lower_left_north_open;
    public static final IBlockState iron_door_upper_left_north;
    public static final IBlockState iron_door_upper_right_north;
    public static final IBlockState iron_door_upper_left_north_powered;
    public static final IBlockState iron_door_upper_right_north_powered;
    public static final IBlockState wooden_pressure_plate;
    public static final IBlockState wooden_pressure_plate_powered;
    public static final IBlockState redstone_ore;
    public static final IBlockState lit_redstone_ore;
    public static final IBlockState unlit_redstone_torch_east;
    public static final IBlockState unlit_redstone_torch_west;
    public static final IBlockState unlit_redstone_torch_south;
    public static final IBlockState unlit_redstone_torch_north;
    public static final IBlockState unlit_redstone_torch_up;
    public static final IBlockState redstone_torch_east;
    public static final IBlockState redstone_torch_west;
    public static final IBlockState redstone_torch_south;
    public static final IBlockState redstone_torch_north;
    public static final IBlockState redstone_torch_up;
    public static final IBlockState stone_button_down;
    public static final IBlockState stone_button_east;
    public static final IBlockState stone_button_west;
    public static final IBlockState stone_button_south;
    public static final IBlockState stone_button_north;
    public static final IBlockState stone_button_up;
    public static final IBlockState stone_button_down_powered;
    public static final IBlockState stone_button_east_powered;
    public static final IBlockState stone_button_west_powered;
    public static final IBlockState stone_button_south_powered;
    public static final IBlockState stone_button_north_powered;
    public static final IBlockState stone_button_up_powered;
    public static final IBlockState snow_layer_layers_1;
    public static final IBlockState snow_layer_layers_2;
    public static final IBlockState snow_layer_layers_3;
    public static final IBlockState snow_layer_layers_4;
    public static final IBlockState snow_layer_layers_5;
    public static final IBlockState snow_layer_layers_6;
    public static final IBlockState snow_layer_layers_7;
    public static final IBlockState snow_layer_layers_8;
    public static final IBlockState ice;
    public static final IBlockState snow;
    public static final IBlockState cactus_age_0;
    public static final IBlockState cactus_age_1;
    public static final IBlockState cactus_age_2;
    public static final IBlockState cactus_age_3;
    public static final IBlockState cactus_age_4;
    public static final IBlockState cactus_age_5;
    public static final IBlockState cactus_age_6;
    public static final IBlockState cactus_age_7;
    public static final IBlockState cactus_age_8;
    public static final IBlockState cactus_age_9;
    public static final IBlockState cactus_age_10;
    public static final IBlockState cactus_age_11;
    public static final IBlockState cactus_age_12;
    public static final IBlockState cactus_age_13;
    public static final IBlockState cactus_age_14;
    public static final IBlockState cactus_age_15;
    public static final IBlockState clay;
    public static final IBlockState reeds_age_0;
    public static final IBlockState reeds_age_1;
    public static final IBlockState reeds_age_2;
    public static final IBlockState reeds_age_3;
    public static final IBlockState reeds_age_4;
    public static final IBlockState reeds_age_5;
    public static final IBlockState reeds_age_6;
    public static final IBlockState reeds_age_7;
    public static final IBlockState reeds_age_8;
    public static final IBlockState reeds_age_9;
    public static final IBlockState reeds_age_10;
    public static final IBlockState reeds_age_11;
    public static final IBlockState reeds_age_12;
    public static final IBlockState reeds_age_13;
    public static final IBlockState reeds_age_14;
    public static final IBlockState reeds_age_15;
    public static final IBlockState jukebox;
    public static final IBlockState jukebox_hasrecord;
    public static final IBlockState oak_fence;
    public static final IBlockState spruce_fence;
    public static final IBlockState birch_fence;
    public static final IBlockState jungle_fence;
    public static final IBlockState dark_oak_fence;
    public static final IBlockState acacia_fence;
    public static final IBlockState pumpkin_south;
    public static final IBlockState pumpkin_west;
    public static final IBlockState pumpkin_north;
    public static final IBlockState pumpkin_east;
    public static final IBlockState netherrack;
    public static final IBlockState soul_sand;
    public static final IBlockState glowstone;
    public static final IBlockState portal_x;
    public static final IBlockState portal_z;
    public static final IBlockState lit_pumpkin_south;
    public static final IBlockState lit_pumpkin_west;
    public static final IBlockState lit_pumpkin_north;
    public static final IBlockState lit_pumpkin_east;
    public static final IBlockState cake_bites_0;
    public static final IBlockState cake_bites_1;
    public static final IBlockState cake_bites_2;
    public static final IBlockState cake_bites_3;
    public static final IBlockState cake_bites_4;
    public static final IBlockState cake_bites_5;
    public static final IBlockState cake_bites_6;
    public static final IBlockState unpowered_repeater_south_delay_1;
    public static final IBlockState unpowered_repeater_west_delay_1;
    public static final IBlockState unpowered_repeater_north_delay_1;
    public static final IBlockState unpowered_repeater_east_delay_1;
    public static final IBlockState unpowered_repeater_south_delay_2;
    public static final IBlockState unpowered_repeater_west_delay_2;
    public static final IBlockState unpowered_repeater_north_delay_2;
    public static final IBlockState unpowered_repeater_east_delay_2;
    public static final IBlockState unpowered_repeater_south_delay_3;
    public static final IBlockState unpowered_repeater_west_delay_3;
    public static final IBlockState unpowered_repeater_north_delay_3;
    public static final IBlockState unpowered_repeater_east_delay_3;
    public static final IBlockState unpowered_repeater_south_delay_4;
    public static final IBlockState unpowered_repeater_west_delay_4;
    public static final IBlockState unpowered_repeater_north_delay_4;
    public static final IBlockState unpowered_repeater_east_delay_4;
    public static final IBlockState powered_repeater_south_delay_1;
    public static final IBlockState powered_repeater_west_delay_1;
    public static final IBlockState powered_repeater_north_delay_1;
    public static final IBlockState powered_repeater_east_delay_1;
    public static final IBlockState powered_repeater_south_delay_2;
    public static final IBlockState powered_repeater_west_delay_2;
    public static final IBlockState powered_repeater_north_delay_2;
    public static final IBlockState powered_repeater_east_delay_2;
    public static final IBlockState powered_repeater_south_delay_3;
    public static final IBlockState powered_repeater_west_delay_3;
    public static final IBlockState powered_repeater_north_delay_3;
    public static final IBlockState powered_repeater_east_delay_3;
    public static final IBlockState powered_repeater_south_delay_4;
    public static final IBlockState powered_repeater_west_delay_4;
    public static final IBlockState powered_repeater_north_delay_4;
    public static final IBlockState powered_repeater_east_delay_4;
    public static final IBlockState trapdoor_bottom_north;
    public static final IBlockState trapdoor_bottom_south;
    public static final IBlockState trapdoor_bottom_west;
    public static final IBlockState trapdoor_bottom_east;
    public static final IBlockState trapdoor_bottom_north_open;
    public static final IBlockState trapdoor_bottom_south_open;
    public static final IBlockState trapdoor_bottom_west_open;
    public static final IBlockState trapdoor_bottom_east_open;
    public static final IBlockState trapdoor_top_north;
    public static final IBlockState trapdoor_top_south;
    public static final IBlockState trapdoor_top_west;
    public static final IBlockState trapdoor_top_east;
    public static final IBlockState trapdoor_top_north_open;
    public static final IBlockState trapdoor_top_south_open;
    public static final IBlockState trapdoor_top_west_open;
    public static final IBlockState trapdoor_top_east_open;
    public static final IBlockState monster_egg_stone;
    public static final IBlockState monster_egg_cobblestone;
    public static final IBlockState monster_egg_stonebrick;
    public static final IBlockState monster_egg_mossybrick;
    public static final IBlockState monster_egg_crackedbrick;
    public static final IBlockState monster_egg_chiseledbrick;
    public static final IBlockState stonebrick;
    public static final IBlockState stonebrick_mossystonebrick;
    public static final IBlockState stonebrick_crackedstonebrick;
    public static final IBlockState stonebrick_chiseledstonebrick;
    public static final IBlockState brown_mushroom_block_allinside;
    public static final IBlockState brown_mushroom_block_northwest;
    public static final IBlockState brown_mushroom_block_north;
    public static final IBlockState brown_mushroom_block_northeast;
    public static final IBlockState brown_mushroom_block_west;
    public static final IBlockState brown_mushroom_block_center;
    public static final IBlockState brown_mushroom_block_east;
    public static final IBlockState brown_mushroom_block_southwest;
    public static final IBlockState brown_mushroom_block_south;
    public static final IBlockState brown_mushroom_block_southeast;
    public static final IBlockState brown_mushroom_block_stem;
    public static final IBlockState brown_mushroom_block_alloutside;
    public static final IBlockState brown_mushroom_block_allstem;
    public static final IBlockState red_mushroom_block_allinside;
    public static final IBlockState red_mushroom_block_northwest;
    public static final IBlockState red_mushroom_block_north;
    public static final IBlockState red_mushroom_block_northeast;
    public static final IBlockState red_mushroom_block_west;
    public static final IBlockState red_mushroom_block_center;
    public static final IBlockState red_mushroom_block_east;
    public static final IBlockState red_mushroom_block_southwest;
    public static final IBlockState red_mushroom_block_south;
    public static final IBlockState red_mushroom_block_southeast;
    public static final IBlockState red_mushroom_block_stem;
    public static final IBlockState red_mushroom_block_alloutside;
    public static final IBlockState red_mushroom_block_allstem;
    public static final IBlockState iron_bars;
    public static final IBlockState glass_pane;
    public static final IBlockState melon_block;
    public static final IBlockState pumpkin_stem_age_0;
    public static final IBlockState pumpkin_stem_age_1;
    public static final IBlockState pumpkin_stem_age_2;
    public static final IBlockState pumpkin_stem_age_3;
    public static final IBlockState pumpkin_stem_age_4;
    public static final IBlockState pumpkin_stem_age_5;
    public static final IBlockState pumpkin_stem_age_6;
    public static final IBlockState pumpkin_stem_age_7;
    public static final IBlockState melon_stem_age_0;
    public static final IBlockState melon_stem_age_1;
    public static final IBlockState melon_stem_age_2;
    public static final IBlockState melon_stem_age_3;
    public static final IBlockState melon_stem_age_4;
    public static final IBlockState melon_stem_age_5;
    public static final IBlockState melon_stem_age_6;
    public static final IBlockState melon_stem_age_7;
    public static final IBlockState vine;
    public static final IBlockState vine_south;
    public static final IBlockState vine_west;
    public static final IBlockState vine_south_west;
    public static final IBlockState vine_north;
    public static final IBlockState vine_north_south;
    public static final IBlockState vine_north_west;
    public static final IBlockState vine_north_south_west;
    public static final IBlockState vine_east;
    public static final IBlockState vine_east_south;
    public static final IBlockState vine_east_west;
    public static final IBlockState vine_east_south_west;
    public static final IBlockState vine_east_north;
    public static final IBlockState vine_east_north_south;
    public static final IBlockState vine_east_north_west;
    public static final IBlockState vine_east_north_south_west;
    public static final IBlockState oak_fence_gate_south;
    public static final IBlockState oak_fence_gate_west;
    public static final IBlockState oak_fence_gate_north;
    public static final IBlockState oak_fence_gate_east;
    public static final IBlockState oak_fence_gate_south_open;
    public static final IBlockState oak_fence_gate_west_open;
    public static final IBlockState oak_fence_gate_north_open;
    public static final IBlockState oak_fence_gate_east_open;
    public static final IBlockState oak_fence_gate_south_powered;
    public static final IBlockState oak_fence_gate_west_powered;
    public static final IBlockState oak_fence_gate_north_powered;
    public static final IBlockState oak_fence_gate_east_powered;
    public static final IBlockState oak_fence_gate_south_open_powered;
    public static final IBlockState oak_fence_gate_west_open_powered;
    public static final IBlockState oak_fence_gate_north_open_powered;
    public static final IBlockState oak_fence_gate_east_open_powered;
    public static final IBlockState spruce_fence_gate_south;
    public static final IBlockState spruce_fence_gate_west;
    public static final IBlockState spruce_fence_gate_north;
    public static final IBlockState spruce_fence_gate_east;
    public static final IBlockState spruce_fence_gate_south_open;
    public static final IBlockState spruce_fence_gate_west_open;
    public static final IBlockState spruce_fence_gate_north_open;
    public static final IBlockState spruce_fence_gate_east_open;
    public static final IBlockState spruce_fence_gate_south_powered;
    public static final IBlockState spruce_fence_gate_west_powered;
    public static final IBlockState spruce_fence_gate_north_powered;
    public static final IBlockState spruce_fence_gate_east_powered;
    public static final IBlockState spruce_fence_gate_south_open_powered;
    public static final IBlockState spruce_fence_gate_west_open_powered;
    public static final IBlockState spruce_fence_gate_north_open_powered;
    public static final IBlockState spruce_fence_gate_east_open_powered;
    public static final IBlockState birch_fence_gate_south;
    public static final IBlockState birch_fence_gate_west;
    public static final IBlockState birch_fence_gate_north;
    public static final IBlockState birch_fence_gate_east;
    public static final IBlockState birch_fence_gate_south_open;
    public static final IBlockState birch_fence_gate_west_open;
    public static final IBlockState birch_fence_gate_north_open;
    public static final IBlockState birch_fence_gate_east_open;
    public static final IBlockState birch_fence_gate_south_powered;
    public static final IBlockState birch_fence_gate_west_powered;
    public static final IBlockState birch_fence_gate_north_powered;
    public static final IBlockState birch_fence_gate_east_powered;
    public static final IBlockState birch_fence_gate_south_open_powered;
    public static final IBlockState birch_fence_gate_west_open_powered;
    public static final IBlockState birch_fence_gate_north_open_powered;
    public static final IBlockState birch_fence_gate_east_open_powered;
    public static final IBlockState jungle_fence_gate_south;
    public static final IBlockState jungle_fence_gate_west;
    public static final IBlockState jungle_fence_gate_north;
    public static final IBlockState jungle_fence_gate_east;
    public static final IBlockState jungle_fence_gate_south_open;
    public static final IBlockState jungle_fence_gate_west_open;
    public static final IBlockState jungle_fence_gate_north_open;
    public static final IBlockState jungle_fence_gate_east_open;
    public static final IBlockState jungle_fence_gate_south_powered;
    public static final IBlockState jungle_fence_gate_west_powered;
    public static final IBlockState jungle_fence_gate_north_powered;
    public static final IBlockState jungle_fence_gate_east_powered;
    public static final IBlockState jungle_fence_gate_south_open_powered;
    public static final IBlockState jungle_fence_gate_west_open_powered;
    public static final IBlockState jungle_fence_gate_north_open_powered;
    public static final IBlockState jungle_fence_gate_east_open_powered;
    public static final IBlockState dark_oak_fence_gate_south;
    public static final IBlockState dark_oak_fence_gate_west;
    public static final IBlockState dark_oak_fence_gate_north;
    public static final IBlockState dark_oak_fence_gate_east;
    public static final IBlockState dark_oak_fence_gate_south_open;
    public static final IBlockState dark_oak_fence_gate_west_open;
    public static final IBlockState dark_oak_fence_gate_north_open;
    public static final IBlockState dark_oak_fence_gate_east_open;
    public static final IBlockState dark_oak_fence_gate_south_powered;
    public static final IBlockState dark_oak_fence_gate_west_powered;
    public static final IBlockState dark_oak_fence_gate_north_powered;
    public static final IBlockState dark_oak_fence_gate_east_powered;
    public static final IBlockState dark_oak_fence_gate_south_open_powered;
    public static final IBlockState dark_oak_fence_gate_west_open_powered;
    public static final IBlockState dark_oak_fence_gate_north_open_powered;
    public static final IBlockState dark_oak_fence_gate_east_open_powered;
    public static final IBlockState acacia_fence_gate_south;
    public static final IBlockState acacia_fence_gate_west;
    public static final IBlockState acacia_fence_gate_north;
    public static final IBlockState acacia_fence_gate_east;
    public static final IBlockState acacia_fence_gate_south_open;
    public static final IBlockState acacia_fence_gate_west_open;
    public static final IBlockState acacia_fence_gate_north_open;
    public static final IBlockState acacia_fence_gate_east_open;
    public static final IBlockState acacia_fence_gate_south_powered;
    public static final IBlockState acacia_fence_gate_west_powered;
    public static final IBlockState acacia_fence_gate_north_powered;
    public static final IBlockState acacia_fence_gate_east_powered;
    public static final IBlockState acacia_fence_gate_south_open_powered;
    public static final IBlockState acacia_fence_gate_west_open_powered;
    public static final IBlockState acacia_fence_gate_north_open_powered;
    public static final IBlockState acacia_fence_gate_east_open_powered;
    public static final IBlockState brick_stairs_bottom_east;
    public static final IBlockState brick_stairs_bottom_west;
    public static final IBlockState brick_stairs_bottom_south;
    public static final IBlockState brick_stairs_bottom_north;
    public static final IBlockState brick_stairs_top_east;
    public static final IBlockState brick_stairs_top_west;
    public static final IBlockState brick_stairs_top_south;
    public static final IBlockState brick_stairs_top_north;
    public static final IBlockState stone_brick_stairs_bottom_east;
    public static final IBlockState stone_brick_stairs_bottom_west;
    public static final IBlockState stone_brick_stairs_bottom_south;
    public static final IBlockState stone_brick_stairs_bottom_north;
    public static final IBlockState stone_brick_stairs_top_east;
    public static final IBlockState stone_brick_stairs_top_west;
    public static final IBlockState stone_brick_stairs_top_south;
    public static final IBlockState stone_brick_stairs_top_north;
    public static final IBlockState mycelium;
    public static final IBlockState waterlily;
    public static final IBlockState nether_brick;
    public static final IBlockState nether_brick_fence;
    public static final IBlockState nether_brick_stairs_bottom_east;
    public static final IBlockState nether_brick_stairs_bottom_west;
    public static final IBlockState nether_brick_stairs_bottom_south;
    public static final IBlockState nether_brick_stairs_bottom_north;
    public static final IBlockState nether_brick_stairs_top_east;
    public static final IBlockState nether_brick_stairs_top_west;
    public static final IBlockState nether_brick_stairs_top_south;
    public static final IBlockState nether_brick_stairs_top_north;
    public static final IBlockState nether_wart_age_0;
    public static final IBlockState nether_wart_age_1;
    public static final IBlockState nether_wart_age_2;
    public static final IBlockState nether_wart_age_3;
    public static final IBlockState enchanting_table;
    public static final IBlockState brewing_stand;
    public static final IBlockState brewing_stand_hasbottle0;
    public static final IBlockState brewing_stand_hasbottle1;
    public static final IBlockState brewing_stand_hasbottle0_hasbottle1;
    public static final IBlockState brewing_stand_hasbottle2;
    public static final IBlockState brewing_stand_hasbottle0_hasbottle2;
    public static final IBlockState brewing_stand_hasbottle1_hasbottle2;
    public static final IBlockState brewing_stand_hasbottle0_hasbottle1_hasbottle2;
    public static final IBlockState cauldron_level_0;
    public static final IBlockState cauldron_level_1;
    public static final IBlockState cauldron_level_2;
    public static final IBlockState cauldron_level_3;
    public static final IBlockState end_portal;
    public static final IBlockState end_portal_frame_south;
    public static final IBlockState end_portal_frame_west;
    public static final IBlockState end_portal_frame_north;
    public static final IBlockState end_portal_frame_east;
    public static final IBlockState end_portal_frame_south_eye;
    public static final IBlockState end_portal_frame_west_eye;
    public static final IBlockState end_portal_frame_north_eye;
    public static final IBlockState end_portal_frame_east_eye;
    public static final IBlockState end_stone;
    public static final IBlockState dragon_egg;
    public static final IBlockState redstone_lamp;
    public static final IBlockState lit_redstone_lamp;
    public static final IBlockState double_wooden_slab_oak;
    public static final IBlockState double_wooden_slab_spruce;
    public static final IBlockState double_wooden_slab_birch;
    public static final IBlockState double_wooden_slab_jungle;
    public static final IBlockState double_wooden_slab_acacia;
    public static final IBlockState double_wooden_slab_darkoak;
    public static final IBlockState wooden_slab_bottom_oak;
    public static final IBlockState wooden_slab_bottom_spruce;
    public static final IBlockState wooden_slab_bottom_birch;
    public static final IBlockState wooden_slab_bottom_jungle;
    public static final IBlockState wooden_slab_bottom_acacia;
    public static final IBlockState wooden_slab_bottom_darkoak;
    public static final IBlockState wooden_slab_top_oak;
    public static final IBlockState wooden_slab_top_spruce;
    public static final IBlockState wooden_slab_top_birch;
    public static final IBlockState wooden_slab_top_jungle;
    public static final IBlockState wooden_slab_top_acacia;
    public static final IBlockState wooden_slab_top_darkoak;
    public static final IBlockState cocoa_south_age_0;
    public static final IBlockState cocoa_west_age_0;
    public static final IBlockState cocoa_north_age_0;
    public static final IBlockState cocoa_east_age_0;
    public static final IBlockState cocoa_south_age_1;
    public static final IBlockState cocoa_west_age_1;
    public static final IBlockState cocoa_north_age_1;
    public static final IBlockState cocoa_east_age_1;
    public static final IBlockState cocoa_south_age_2;
    public static final IBlockState cocoa_west_age_2;
    public static final IBlockState cocoa_north_age_2;
    public static final IBlockState cocoa_east_age_2;
    public static final IBlockState sandstone_stairs_bottom_east;
    public static final IBlockState sandstone_stairs_bottom_west;
    public static final IBlockState sandstone_stairs_bottom_south;
    public static final IBlockState sandstone_stairs_bottom_north;
    public static final IBlockState sandstone_stairs_top_east;
    public static final IBlockState sandstone_stairs_top_west;
    public static final IBlockState sandstone_stairs_top_south;
    public static final IBlockState sandstone_stairs_top_north;
    public static final IBlockState emerald_ore;
    public static final IBlockState ender_chest_north;
    public static final IBlockState ender_chest_south;
    public static final IBlockState ender_chest_west;
    public static final IBlockState ender_chest_east;
    public static final IBlockState tripwire_hook_south;
    public static final IBlockState tripwire_hook_west;
    public static final IBlockState tripwire_hook_north;
    public static final IBlockState tripwire_hook_east;
    public static final IBlockState tripwire_hook_south_attached;
    public static final IBlockState tripwire_hook_west_attached;
    public static final IBlockState tripwire_hook_north_attached;
    public static final IBlockState tripwire_hook_east_attached;
    public static final IBlockState tripwire_hook_south_powered;
    public static final IBlockState tripwire_hook_west_powered;
    public static final IBlockState tripwire_hook_north_powered;
    public static final IBlockState tripwire_hook_east_powered;
    public static final IBlockState tripwire_hook_south_attached_powered;
    public static final IBlockState tripwire_hook_west_attached_powered;
    public static final IBlockState tripwire_hook_north_attached_powered;
    public static final IBlockState tripwire_hook_east_attached_powered;
    public static final IBlockState tripwire;
    public static final IBlockState tripwire_powered;
    public static final IBlockState tripwire_suspended;
    public static final IBlockState tripwire_powered_suspended;
    public static final IBlockState tripwire_attached;
    public static final IBlockState tripwire_attached_powered;
    public static final IBlockState tripwire_attached_suspended;
    public static final IBlockState tripwire_attached_powered_suspended;
    public static final IBlockState tripwire_disarmed;
    public static final IBlockState tripwire_disarmed_powered;
    public static final IBlockState tripwire_disarmed_suspended;
    public static final IBlockState tripwire_disarmed_powered_suspended;
    public static final IBlockState tripwire_attached_disarmed;
    public static final IBlockState tripwire_attached_disarmed_powered;
    public static final IBlockState tripwire_attached_disarmed_suspended;
    public static final IBlockState tripwire_attached_disarmed_powered_suspended;
    public static final IBlockState emerald_block;
    public static final IBlockState spruce_stairs_bottom_east;
    public static final IBlockState spruce_stairs_bottom_west;
    public static final IBlockState spruce_stairs_bottom_south;
    public static final IBlockState spruce_stairs_bottom_north;
    public static final IBlockState spruce_stairs_top_east;
    public static final IBlockState spruce_stairs_top_west;
    public static final IBlockState spruce_stairs_top_south;
    public static final IBlockState spruce_stairs_top_north;
    public static final IBlockState birch_stairs_bottom_east;
    public static final IBlockState birch_stairs_bottom_west;
    public static final IBlockState birch_stairs_bottom_south;
    public static final IBlockState birch_stairs_bottom_north;
    public static final IBlockState birch_stairs_top_east;
    public static final IBlockState birch_stairs_top_west;
    public static final IBlockState birch_stairs_top_south;
    public static final IBlockState birch_stairs_top_north;
    public static final IBlockState jungle_stairs_bottom_east;
    public static final IBlockState jungle_stairs_bottom_west;
    public static final IBlockState jungle_stairs_bottom_south;
    public static final IBlockState jungle_stairs_bottom_north;
    public static final IBlockState jungle_stairs_top_east;
    public static final IBlockState jungle_stairs_top_west;
    public static final IBlockState jungle_stairs_top_south;
    public static final IBlockState jungle_stairs_top_north;
    public static final IBlockState command_block;
    public static final IBlockState command_block_triggered;
    public static final IBlockState beacon;
    public static final IBlockState cobblestone_wall_cobblestone;
    public static final IBlockState cobblestone_wall_mossycobblestone;
    public static final IBlockState flower_pot;
    public static final IBlockState carrots_age_0;
    public static final IBlockState carrots_age_1;
    public static final IBlockState carrots_age_2;
    public static final IBlockState carrots_age_3;
    public static final IBlockState carrots_age_4;
    public static final IBlockState carrots_age_5;
    public static final IBlockState carrots_age_6;
    public static final IBlockState carrots_age_7;
    public static final IBlockState potatoes_age_0;
    public static final IBlockState potatoes_age_1;
    public static final IBlockState potatoes_age_2;
    public static final IBlockState potatoes_age_3;
    public static final IBlockState potatoes_age_4;
    public static final IBlockState potatoes_age_5;
    public static final IBlockState potatoes_age_6;
    public static final IBlockState potatoes_age_7;
    public static final IBlockState wooden_button_down;
    public static final IBlockState wooden_button_east;
    public static final IBlockState wooden_button_west;
    public static final IBlockState wooden_button_south;
    public static final IBlockState wooden_button_north;
    public static final IBlockState wooden_button_up;
    public static final IBlockState wooden_button_down_powered;
    public static final IBlockState wooden_button_east_powered;
    public static final IBlockState wooden_button_west_powered;
    public static final IBlockState wooden_button_south_powered;
    public static final IBlockState wooden_button_north_powered;
    public static final IBlockState wooden_button_up_powered;
    public static final IBlockState skull_down;
    public static final IBlockState skull_up;
    public static final IBlockState skull_north;
    public static final IBlockState skull_south;
    public static final IBlockState skull_west;
    public static final IBlockState skull_east;
    public static final IBlockState skull_down_nodrop;
    public static final IBlockState skull_up_nodrop;
    public static final IBlockState skull_north_nodrop;
    public static final IBlockState skull_south_nodrop;
    public static final IBlockState skull_west_nodrop;
    public static final IBlockState skull_east_nodrop;
    public static final IBlockState anvil_south_damage_0;
    public static final IBlockState anvil_west_damage_0;
    public static final IBlockState anvil_north_damage_0;
    public static final IBlockState anvil_east_damage_0;
    public static final IBlockState anvil_south_damage_1;
    public static final IBlockState anvil_west_damage_1;
    public static final IBlockState anvil_north_damage_1;
    public static final IBlockState anvil_east_damage_1;
    public static final IBlockState anvil_south_damage_2;
    public static final IBlockState anvil_west_damage_2;
    public static final IBlockState anvil_north_damage_2;
    public static final IBlockState anvil_east_damage_2;
    public static final IBlockState trapped_chest_north;
    public static final IBlockState trapped_chest_south;
    public static final IBlockState trapped_chest_west;
    public static final IBlockState trapped_chest_east;
    public static final IBlockState light_weighted_pressure_plate_power_0;
    public static final IBlockState light_weighted_pressure_plate_power_1;
    public static final IBlockState light_weighted_pressure_plate_power_2;
    public static final IBlockState light_weighted_pressure_plate_power_3;
    public static final IBlockState light_weighted_pressure_plate_power_4;
    public static final IBlockState light_weighted_pressure_plate_power_5;
    public static final IBlockState light_weighted_pressure_plate_power_6;
    public static final IBlockState light_weighted_pressure_plate_power_7;
    public static final IBlockState light_weighted_pressure_plate_power_8;
    public static final IBlockState light_weighted_pressure_plate_power_9;
    public static final IBlockState light_weighted_pressure_plate_power_10;
    public static final IBlockState light_weighted_pressure_plate_power_11;
    public static final IBlockState light_weighted_pressure_plate_power_12;
    public static final IBlockState light_weighted_pressure_plate_power_13;
    public static final IBlockState light_weighted_pressure_plate_power_14;
    public static final IBlockState light_weighted_pressure_plate_power_15;
    public static final IBlockState heavy_weighted_pressure_plate_power_0;
    public static final IBlockState heavy_weighted_pressure_plate_power_1;
    public static final IBlockState heavy_weighted_pressure_plate_power_2;
    public static final IBlockState heavy_weighted_pressure_plate_power_3;
    public static final IBlockState heavy_weighted_pressure_plate_power_4;
    public static final IBlockState heavy_weighted_pressure_plate_power_5;
    public static final IBlockState heavy_weighted_pressure_plate_power_6;
    public static final IBlockState heavy_weighted_pressure_plate_power_7;
    public static final IBlockState heavy_weighted_pressure_plate_power_8;
    public static final IBlockState heavy_weighted_pressure_plate_power_9;
    public static final IBlockState heavy_weighted_pressure_plate_power_10;
    public static final IBlockState heavy_weighted_pressure_plate_power_11;
    public static final IBlockState heavy_weighted_pressure_plate_power_12;
    public static final IBlockState heavy_weighted_pressure_plate_power_13;
    public static final IBlockState heavy_weighted_pressure_plate_power_14;
    public static final IBlockState heavy_weighted_pressure_plate_power_15;
    public static final IBlockState unpowered_comparator_compare_south;
    public static final IBlockState unpowered_comparator_compare_west;
    public static final IBlockState unpowered_comparator_compare_north;
    public static final IBlockState unpowered_comparator_compare_east;
    public static final IBlockState unpowered_comparator_subtract_south;
    public static final IBlockState unpowered_comparator_subtract_west;
    public static final IBlockState unpowered_comparator_subtract_north;
    public static final IBlockState unpowered_comparator_subtract_east;
    public static final IBlockState unpowered_comparator_compare_south_powered;
    public static final IBlockState unpowered_comparator_compare_west_powered;
    public static final IBlockState unpowered_comparator_compare_north_powered;
    public static final IBlockState unpowered_comparator_compare_east_powered;
    public static final IBlockState unpowered_comparator_subtract_south_powered;
    public static final IBlockState unpowered_comparator_subtract_west_powered;
    public static final IBlockState unpowered_comparator_subtract_north_powered;
    public static final IBlockState unpowered_comparator_subtract_east_powered;
    public static final IBlockState powered_comparator_compare_south;
    public static final IBlockState powered_comparator_compare_west;
    public static final IBlockState powered_comparator_compare_north;
    public static final IBlockState powered_comparator_compare_east;
    public static final IBlockState powered_comparator_subtract_south;
    public static final IBlockState powered_comparator_subtract_west;
    public static final IBlockState powered_comparator_subtract_north;
    public static final IBlockState powered_comparator_subtract_east;
    public static final IBlockState powered_comparator_compare_south_powered;
    public static final IBlockState powered_comparator_compare_west_powered;
    public static final IBlockState powered_comparator_compare_north_powered;
    public static final IBlockState powered_comparator_compare_east_powered;
    public static final IBlockState powered_comparator_subtract_south_powered;
    public static final IBlockState powered_comparator_subtract_west_powered;
    public static final IBlockState powered_comparator_subtract_north_powered;
    public static final IBlockState powered_comparator_subtract_east_powered;
    public static final IBlockState daylight_detector_power_0;
    public static final IBlockState daylight_detector_power_1;
    public static final IBlockState daylight_detector_power_2;
    public static final IBlockState daylight_detector_power_3;
    public static final IBlockState daylight_detector_power_4;
    public static final IBlockState daylight_detector_power_5;
    public static final IBlockState daylight_detector_power_6;
    public static final IBlockState daylight_detector_power_7;
    public static final IBlockState daylight_detector_power_8;
    public static final IBlockState daylight_detector_power_9;
    public static final IBlockState daylight_detector_power_10;
    public static final IBlockState daylight_detector_power_11;
    public static final IBlockState daylight_detector_power_12;
    public static final IBlockState daylight_detector_power_13;
    public static final IBlockState daylight_detector_power_14;
    public static final IBlockState daylight_detector_power_15;
    public static final IBlockState daylight_detector_inverted_power_0;
    public static final IBlockState daylight_detector_inverted_power_1;
    public static final IBlockState daylight_detector_inverted_power_2;
    public static final IBlockState daylight_detector_inverted_power_3;
    public static final IBlockState daylight_detector_inverted_power_4;
    public static final IBlockState daylight_detector_inverted_power_5;
    public static final IBlockState daylight_detector_inverted_power_6;
    public static final IBlockState daylight_detector_inverted_power_7;
    public static final IBlockState daylight_detector_inverted_power_8;
    public static final IBlockState daylight_detector_inverted_power_9;
    public static final IBlockState daylight_detector_inverted_power_10;
    public static final IBlockState daylight_detector_inverted_power_11;
    public static final IBlockState daylight_detector_inverted_power_12;
    public static final IBlockState daylight_detector_inverted_power_13;
    public static final IBlockState daylight_detector_inverted_power_14;
    public static final IBlockState daylight_detector_inverted_power_15;
    public static final IBlockState redstone_block;
    public static final IBlockState quartz_ore;
    public static final IBlockState hopper_down_enabled;
    public static final IBlockState hopper_north_enabled;
    public static final IBlockState hopper_south_enabled;
    public static final IBlockState hopper_west_enabled;
    public static final IBlockState hopper_east_enabled;
    public static final IBlockState hopper_down;
    public static final IBlockState hopper_north;
    public static final IBlockState hopper_south;
    public static final IBlockState hopper_west;
    public static final IBlockState hopper_east;
    public static final IBlockState quartz_block_default;
    public static final IBlockState quartz_block_chiseled;
    public static final IBlockState quartz_block_linesy;
    public static final IBlockState quartz_block_linesx;
    public static final IBlockState quartz_block_linesz;
    public static final IBlockState quartz_stairs_bottom_east;
    public static final IBlockState quartz_stairs_bottom_west;
    public static final IBlockState quartz_stairs_bottom_south;
    public static final IBlockState quartz_stairs_bottom_north;
    public static final IBlockState quartz_stairs_top_east;
    public static final IBlockState quartz_stairs_top_west;
    public static final IBlockState quartz_stairs_top_south;
    public static final IBlockState quartz_stairs_top_north;
    public static final IBlockState activator_rail_northsouth;
    public static final IBlockState activator_rail_eastwest;
    public static final IBlockState activator_rail_ascendingeast;
    public static final IBlockState activator_rail_ascendingwest;
    public static final IBlockState activator_rail_ascendingnorth;
    public static final IBlockState activator_rail_ascendingsouth;
    public static final IBlockState activator_rail_northsouth_powered;
    public static final IBlockState activator_rail_eastwest_powered;
    public static final IBlockState activator_rail_ascendingeast_powered;
    public static final IBlockState activator_rail_ascendingwest_powered;
    public static final IBlockState activator_rail_ascendingnorth_powered;
    public static final IBlockState activator_rail_ascendingsouth_powered;
    public static final IBlockState dropper_down;
    public static final IBlockState dropper_up;
    public static final IBlockState dropper_north;
    public static final IBlockState dropper_south;
    public static final IBlockState dropper_west;
    public static final IBlockState dropper_east;
    public static final IBlockState dropper_down_triggered;
    public static final IBlockState dropper_up_triggered;
    public static final IBlockState dropper_north_triggered;
    public static final IBlockState dropper_south_triggered;
    public static final IBlockState dropper_west_triggered;
    public static final IBlockState dropper_east_triggered;
    public static final IBlockState stained_hardened_clay_white;
    public static final IBlockState stained_hardened_clay_orange;
    public static final IBlockState stained_hardened_clay_magenta;
    public static final IBlockState stained_hardened_clay_lightblue;
    public static final IBlockState stained_hardened_clay_yellow;
    public static final IBlockState stained_hardened_clay_lime;
    public static final IBlockState stained_hardened_clay_pink;
    public static final IBlockState stained_hardened_clay_gray;
    public static final IBlockState stained_hardened_clay_silver;
    public static final IBlockState stained_hardened_clay_cyan;
    public static final IBlockState stained_hardened_clay_purple;
    public static final IBlockState stained_hardened_clay_blue;
    public static final IBlockState stained_hardened_clay_brown;
    public static final IBlockState stained_hardened_clay_green;
    public static final IBlockState stained_hardened_clay_red;
    public static final IBlockState stained_hardened_clay_black;
    public static final IBlockState barrier;
    public static final IBlockState iron_trapdoor_bottom_north;
    public static final IBlockState iron_trapdoor_bottom_south;
    public static final IBlockState iron_trapdoor_bottom_west;
    public static final IBlockState iron_trapdoor_bottom_east;
    public static final IBlockState iron_trapdoor_bottom_north_open;
    public static final IBlockState iron_trapdoor_bottom_south_open;
    public static final IBlockState iron_trapdoor_bottom_west_open;
    public static final IBlockState iron_trapdoor_bottom_east_open;
    public static final IBlockState iron_trapdoor_top_north;
    public static final IBlockState iron_trapdoor_top_south;
    public static final IBlockState iron_trapdoor_top_west;
    public static final IBlockState iron_trapdoor_top_east;
    public static final IBlockState iron_trapdoor_top_north_open;
    public static final IBlockState iron_trapdoor_top_south_open;
    public static final IBlockState iron_trapdoor_top_west_open;
    public static final IBlockState iron_trapdoor_top_east_open;
    public static final IBlockState hay_block_y;
    public static final IBlockState hay_block_x;
    public static final IBlockState hay_block_z;
    public static final IBlockState carpet_white;
    public static final IBlockState carpet_orange;
    public static final IBlockState carpet_magenta;
    public static final IBlockState carpet_lightblue;
    public static final IBlockState carpet_yellow;
    public static final IBlockState carpet_lime;
    public static final IBlockState carpet_pink;
    public static final IBlockState carpet_gray;
    public static final IBlockState carpet_silver;
    public static final IBlockState carpet_cyan;
    public static final IBlockState carpet_purple;
    public static final IBlockState carpet_blue;
    public static final IBlockState carpet_brown;
    public static final IBlockState carpet_green;
    public static final IBlockState carpet_red;
    public static final IBlockState carpet_black;
    public static final IBlockState hardened_clay;
    public static final IBlockState coal_block;
    public static final IBlockState packed_ice;
    public static final IBlockState acacia_stairs_bottom_east;
    public static final IBlockState acacia_stairs_bottom_west;
    public static final IBlockState acacia_stairs_bottom_south;
    public static final IBlockState acacia_stairs_bottom_north;
    public static final IBlockState acacia_stairs_top_east;
    public static final IBlockState acacia_stairs_top_west;
    public static final IBlockState acacia_stairs_top_south;
    public static final IBlockState acacia_stairs_top_north;
    public static final IBlockState dark_oak_stairs_bottom_east;
    public static final IBlockState dark_oak_stairs_bottom_west;
    public static final IBlockState dark_oak_stairs_bottom_south;
    public static final IBlockState dark_oak_stairs_bottom_north;
    public static final IBlockState dark_oak_stairs_top_east;
    public static final IBlockState dark_oak_stairs_top_west;
    public static final IBlockState dark_oak_stairs_top_south;
    public static final IBlockState dark_oak_stairs_top_north;
    public static final IBlockState slime_block;
    public static final IBlockState double_plant_lower_sunflower;
    public static final IBlockState double_plant_lower_syringa;
    public static final IBlockState double_plant_lower_doublegrass;
    public static final IBlockState double_plant_lower_doublefern;
    public static final IBlockState double_plant_lower_doublerose;
    public static final IBlockState double_plant_lower_paeonia;
    public static final IBlockState double_plant_upper_sunflower;
    public static final IBlockState stained_glass_white;
    public static final IBlockState stained_glass_orange;
    public static final IBlockState stained_glass_magenta;
    public static final IBlockState stained_glass_lightblue;
    public static final IBlockState stained_glass_yellow;
    public static final IBlockState stained_glass_lime;
    public static final IBlockState stained_glass_pink;
    public static final IBlockState stained_glass_gray;
    public static final IBlockState stained_glass_silver;
    public static final IBlockState stained_glass_cyan;
    public static final IBlockState stained_glass_purple;
    public static final IBlockState stained_glass_blue;
    public static final IBlockState stained_glass_brown;
    public static final IBlockState stained_glass_green;
    public static final IBlockState stained_glass_red;
    public static final IBlockState stained_glass_black;
    public static final IBlockState stained_glass_pane_white;
    public static final IBlockState stained_glass_pane_orange;
    public static final IBlockState stained_glass_pane_magenta;
    public static final IBlockState stained_glass_pane_lightblue;
    public static final IBlockState stained_glass_pane_yellow;
    public static final IBlockState stained_glass_pane_lime;
    public static final IBlockState stained_glass_pane_pink;
    public static final IBlockState stained_glass_pane_gray;
    public static final IBlockState stained_glass_pane_silver;
    public static final IBlockState stained_glass_pane_cyan;
    public static final IBlockState stained_glass_pane_purple;
    public static final IBlockState stained_glass_pane_blue;
    public static final IBlockState stained_glass_pane_brown;
    public static final IBlockState stained_glass_pane_green;
    public static final IBlockState stained_glass_pane_red;
    public static final IBlockState stained_glass_pane_black;
    public static final IBlockState prismarine;
    public static final IBlockState prismarine_prismarinebricks;
    public static final IBlockState prismarine_darkprismarine;
    public static final IBlockState sea_lantern;
    public static final IBlockState standing_banner_rotation_0;
    public static final IBlockState standing_banner_rotation_1;
    public static final IBlockState standing_banner_rotation_2;
    public static final IBlockState standing_banner_rotation_3;
    public static final IBlockState standing_banner_rotation_4;
    public static final IBlockState standing_banner_rotation_5;
    public static final IBlockState standing_banner_rotation_6;
    public static final IBlockState standing_banner_rotation_7;
    public static final IBlockState standing_banner_rotation_8;
    public static final IBlockState standing_banner_rotation_9;
    public static final IBlockState standing_banner_rotation_10;
    public static final IBlockState standing_banner_rotation_11;
    public static final IBlockState standing_banner_rotation_12;
    public static final IBlockState standing_banner_rotation_13;
    public static final IBlockState standing_banner_rotation_14;
    public static final IBlockState standing_banner_rotation_15;
    public static final IBlockState wall_banner_north;
    public static final IBlockState wall_banner_south;
    public static final IBlockState wall_banner_west;
    public static final IBlockState wall_banner_east;
    public static final IBlockState red_sandstone_redsandstone;
    public static final IBlockState red_sandstone_chiseledredsandstone;
    public static final IBlockState red_sandstone_smoothredsandstone;
    public static final IBlockState red_sandstone_stairs_bottom_east;
    public static final IBlockState red_sandstone_stairs_bottom_west;
    public static final IBlockState red_sandstone_stairs_bottom_south;
    public static final IBlockState red_sandstone_stairs_bottom_north;
    public static final IBlockState red_sandstone_stairs_top_east;
    public static final IBlockState red_sandstone_stairs_top_west;
    public static final IBlockState red_sandstone_stairs_top_south;
    public static final IBlockState red_sandstone_stairs_top_north;
    public static final IBlockState double_stone_slab2;
    public static final IBlockState double_stone_slab2_seamless;
    public static final IBlockState stone_slab2_bottom;
    public static final IBlockState stone_slab2_top;

    static
    {
        air = Blocks.air.getDefaultState();
        stone = Blocks.stone.getDefaultState();
        stone_granite = Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE);
        stone_smoothgranite = Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
        stone_diorite = Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
        stone_smoothdiorite = Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
        stone_andesite = Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);
        stone_smoothandesite = Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH);
        grass = Blocks.grass.getDefaultState();
        dirt = Blocks.dirt.getDefaultState();
        dirt_coarsedirt = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
        dirt_podzol = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
        cobblestone = Blocks.cobblestone.getDefaultState();
        planks_oak = Blocks.planks.getDefaultState();
        planks_spruce = Blocks.planks.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE);
        planks_birch = Blocks.planks.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.BIRCH);
        planks_jungle = Blocks.planks.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE);
        planks_acacia = Blocks.planks.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.ACACIA);
        planks_darkoak = Blocks.planks.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        sapling_oak_stage_0 = Blocks.sapling.getDefaultState();
        sapling_spruce_stage_0 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.SPRUCE);
        sapling_birch_stage_0 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.BIRCH);
        sapling_jungle_stage_0 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.JUNGLE);
        sapling_acacia_stage_0 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.ACACIA);
        sapling_darkoak_stage_0 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.DARK_OAK);
        sapling_oak_stage_1 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.STAGE, 1);
        sapling_spruce_stage_1 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.SPRUCE)
                .withProperty(BlockSapling.STAGE, 1);
        sapling_birch_stage_1 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.BIRCH)
                .withProperty(BlockSapling.STAGE, 1);
        sapling_jungle_stage_1 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.JUNGLE)
                .withProperty(BlockSapling.STAGE, 1);
        sapling_acacia_stage_1 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.ACACIA)
                .withProperty(BlockSapling.STAGE, 1);
        sapling_darkoak_stage_1 = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.DARK_OAK)
                .withProperty(BlockSapling.STAGE, 1);
        bedrock = Blocks.bedrock.getDefaultState();
        flowing_water_level_0 = Blocks.flowing_water.getDefaultState();
        flowing_water_level_1 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 1);
        flowing_water_level_2 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 2);
        flowing_water_level_3 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 3);
        flowing_water_level_4 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 4);
        flowing_water_level_5 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 5);
        flowing_water_level_6 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 6);
        flowing_water_level_7 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 7);
        flowing_water_level_8 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 8);
        flowing_water_level_9 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 9);
        flowing_water_level_10 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 10);
        flowing_water_level_11 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 11);
        flowing_water_level_12 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 12);
        flowing_water_level_13 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 13);
        flowing_water_level_14 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 14);
        flowing_water_level_15 = Blocks.flowing_water.getDefaultState().withProperty(BlockLiquid.LEVEL, 15);
        water_level_0 = Blocks.water.getDefaultState();
        water_level_1 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 1);
        water_level_2 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 2);
        water_level_3 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 3);
        water_level_4 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 4);
        water_level_5 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 5);
        water_level_6 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 6);
        water_level_7 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 7);
        water_level_8 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 8);
        water_level_9 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 9);
        water_level_10 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 10);
        water_level_11 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 11);
        water_level_12 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 12);
        water_level_13 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 13);
        water_level_14 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 14);
        water_level_15 = Blocks.water.getDefaultState().withProperty(BlockLiquid.LEVEL, 15);
        flowing_lava_level_0 = Blocks.flowing_lava.getDefaultState();
        flowing_lava_level_1 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 1);
        flowing_lava_level_2 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 2);
        flowing_lava_level_3 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 3);
        flowing_lava_level_4 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 4);
        flowing_lava_level_5 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 5);
        flowing_lava_level_6 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 6);
        flowing_lava_level_7 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 7);
        flowing_lava_level_8 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 8);
        flowing_lava_level_9 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 9);
        flowing_lava_level_10 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 10);
        flowing_lava_level_11 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 11);
        flowing_lava_level_12 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 12);
        flowing_lava_level_13 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 13);
        flowing_lava_level_14 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 14);
        flowing_lava_level_15 = Blocks.flowing_lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 15);
        lava_level_0 = Blocks.lava.getDefaultState();
        lava_level_1 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 1);
        lava_level_2 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 2);
        lava_level_3 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 3);
        lava_level_4 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 4);
        lava_level_5 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 5);
        lava_level_6 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 6);
        lava_level_7 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 7);
        lava_level_8 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 8);
        lava_level_9 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 9);
        lava_level_10 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 10);
        lava_level_11 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 11);
        lava_level_12 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 12);
        lava_level_13 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 13);
        lava_level_14 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 14);
        lava_level_15 = Blocks.lava.getDefaultState().withProperty(BlockLiquid.LEVEL, 15);
        sand = Blocks.sand.getDefaultState();
        sand_redsand = Blocks.sand.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND);
        gravel = Blocks.gravel.getDefaultState();
        gold_ore = Blocks.gold_ore.getDefaultState();
        iron_ore = Blocks.iron_ore.getDefaultState();
        coal_ore = Blocks.coal_ore.getDefaultState();
        log_y_oak = Blocks.log.getDefaultState();
        log_y_spruce = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
        log_y_birch = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
        log_y_jungle = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
        log_x_oak = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
        log_x_spruce = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
        log_x_birch = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
        log_x_jungle = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
        log_z_oak = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z);
        log_z_spruce = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
        log_z_birch = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
        log_z_jungle = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
        log_none_oak = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
        log_none_spruce = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
        log_none_birch = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
        log_none_jungle = Blocks.log.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE)
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
        log2_y_acacia = Blocks.log2.getDefaultState();
        log2_y_darkoak = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        log2_x_acacia = Blocks.log2.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
        log2_x_darkoak = Blocks.log2.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X)
                .withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        log2_z_acacia = Blocks.log2.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z);
        log2_z_darkoak = Blocks.log2.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z)
                .withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        log2_none_acacia = Blocks.log2.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
        log2_none_darkoak = Blocks.log2.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE)
                .withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        leaves_oak_decayable = Blocks.leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false);
        leaves_spruce_decayable = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)
                .withProperty(BlockLeaves.CHECK_DECAY, false);
        leaves_birch_decayable = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)
                .withProperty(BlockLeaves.CHECK_DECAY, false);
        leaves_jungle_decayable = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
                .withProperty(BlockLeaves.CHECK_DECAY, false);
        leaves_oak = Blocks.leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, false);
        leaves_spruce = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)
                .withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, false);
        leaves_birch = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)
                .withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, false);
        leaves_jungle = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
                .withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, false);
        leaves_oak_checkdecay_decayable = Blocks.leaves.getDefaultState();
        leaves_spruce_checkdecay_decayable = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE);
        leaves_birch_checkdecay_decayable = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH);
        leaves_jungle_checkdecay_decayable = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE);
        leaves_oak_checkdecay = Blocks.leaves.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false);
        leaves_spruce_checkdecay = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)
                .withProperty(BlockLeaves.DECAYABLE, false);
        leaves_birch_checkdecay = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)
                .withProperty(BlockLeaves.DECAYABLE, false);
        leaves_jungle_checkdecay = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
                .withProperty(BlockLeaves.DECAYABLE, false);
        leaves2_acacia_decayable = Blocks.leaves2.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false);
        leaves2_darkoak_decayable = Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
                .withProperty(BlockLeaves.CHECK_DECAY, false);
        leaves2_acacia = Blocks.leaves2.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, false);
        leaves2_darkoak = Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
                .withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, false);
        leaves2_acacia_checkdecay_decayable = Blocks.leaves2.getDefaultState();
        leaves2_darkoak_checkdecay_decayable = Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        leaves2_acacia_checkdecay = Blocks.leaves2.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false);
        leaves2_darkoak_checkdecay = Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
                .withProperty(BlockLeaves.DECAYABLE, false);
        sponge = Blocks.sponge.getDefaultState();
        sponge_wet = Blocks.sponge.getDefaultState().withProperty(BlockSponge.WET, true);
        glass = Blocks.glass.getDefaultState();
        lapis_ore = Blocks.lapis_ore.getDefaultState();
        lapis_block = Blocks.lapis_block.getDefaultState();
        dispenser_down = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.DOWN);
        dispenser_up = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.UP);
        dispenser_north = Blocks.dispenser.getDefaultState();
        dispenser_south = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.SOUTH);
        dispenser_west = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.WEST);
        dispenser_east = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.EAST);
        dispenser_down_triggered = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.DOWN)
                .withProperty(BlockDispenser.TRIGGERED, true);
        dispenser_up_triggered = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.UP)
                .withProperty(BlockDispenser.TRIGGERED, true);
        dispenser_north_triggered = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.TRIGGERED, true);
        dispenser_south_triggered = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.SOUTH)
                .withProperty(BlockDispenser.TRIGGERED, true);
        dispenser_west_triggered = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.WEST)
                .withProperty(BlockDispenser.TRIGGERED, true);
        dispenser_east_triggered = Blocks.dispenser.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.EAST)
                .withProperty(BlockDispenser.TRIGGERED, true);
        sandstone = Blocks.sandstone.getDefaultState();
        sandstone_chiseledsandstone = Blocks.sandstone.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED);
        sandstone_smoothsandstone = Blocks.sandstone.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH);
        noteblock = Blocks.noteblock.getDefaultState();
        bed_foot_south = Blocks.bed.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        bed_foot_west = Blocks.bed.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        bed_foot_north = Blocks.bed.getDefaultState();
        bed_foot_east = Blocks.bed.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        bed_head_south = Blocks.bed.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        bed_head_west = Blocks.bed.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        bed_head_north = Blocks.bed.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
        bed_head_east = Blocks.bed.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        bed_head_south_occupied = Blocks.bed.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH).withProperty(BlockBed.OCCUPIED, true);
        bed_head_west_occupied = Blocks.bed.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST).withProperty(BlockBed.OCCUPIED, true);
        bed_head_north_occupied = Blocks.bed.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD).withProperty(BlockBed.OCCUPIED, true);
        bed_head_east_occupied = Blocks.bed.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST).withProperty(BlockBed.OCCUPIED, true);
        golden_rail_northsouth = Blocks.golden_rail.getDefaultState();
        golden_rail_eastwest = Blocks.golden_rail.getDefaultState().withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST);
        golden_rail_ascendingeast = Blocks.golden_rail.getDefaultState().withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
        golden_rail_ascendingwest = Blocks.golden_rail.getDefaultState().withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
        golden_rail_ascendingnorth = Blocks.golden_rail.getDefaultState().withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
        golden_rail_ascendingsouth = Blocks.golden_rail.getDefaultState().withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
        golden_rail_northsouth_powered = Blocks.golden_rail.getDefaultState().withProperty(BlockRailPowered.POWERED, true);
        golden_rail_eastwest_powered = Blocks.golden_rail.getDefaultState().withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST)
                .withProperty(BlockRailPowered.POWERED, true);
        golden_rail_ascendingeast_powered = Blocks.golden_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST).withProperty(BlockRailPowered.POWERED, true);
        golden_rail_ascendingwest_powered = Blocks.golden_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST).withProperty(BlockRailPowered.POWERED, true);
        golden_rail_ascendingnorth_powered = Blocks.golden_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH).withProperty(BlockRailPowered.POWERED, true);
        golden_rail_ascendingsouth_powered = Blocks.golden_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH).withProperty(BlockRailPowered.POWERED, true);
        detector_rail_northsouth = Blocks.detector_rail.getDefaultState();
        detector_rail_eastwest = Blocks.detector_rail.getDefaultState().withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST);
        detector_rail_ascendingeast = Blocks.detector_rail.getDefaultState()
                .withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
        detector_rail_ascendingwest = Blocks.detector_rail.getDefaultState()
                .withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
        detector_rail_ascendingnorth = Blocks.detector_rail.getDefaultState()
                .withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
        detector_rail_ascendingsouth = Blocks.detector_rail.getDefaultState()
                .withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
        detector_rail_northsouth_powered = Blocks.detector_rail.getDefaultState().withProperty(BlockRailDetector.POWERED, true);
        detector_rail_eastwest_powered = Blocks.detector_rail.getDefaultState().withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST)
                .withProperty(BlockRailDetector.POWERED, true);
        detector_rail_ascendingeast_powered = Blocks.detector_rail.getDefaultState()
                .withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST).withProperty(BlockRailDetector.POWERED, true);
        detector_rail_ascendingwest_powered = Blocks.detector_rail.getDefaultState()
                .withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST).withProperty(BlockRailDetector.POWERED, true);
        detector_rail_ascendingnorth_powered = Blocks.detector_rail.getDefaultState()
                .withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH).withProperty(BlockRailDetector.POWERED, true);
        detector_rail_ascendingsouth_powered = Blocks.detector_rail.getDefaultState()
                .withProperty(BlockRailDetector.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH).withProperty(BlockRailDetector.POWERED, true);
        sticky_piston_down = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.DOWN);
        sticky_piston_up = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.UP);
        sticky_piston_north = Blocks.sticky_piston.getDefaultState();
        sticky_piston_south = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.SOUTH);
        sticky_piston_west = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.WEST);
        sticky_piston_east = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.EAST);
        sticky_piston_down_extended = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.DOWN)
                .withProperty(BlockPistonBase.EXTENDED, true);
        sticky_piston_up_extended = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.UP)
                .withProperty(BlockPistonBase.EXTENDED, true);
        sticky_piston_north_extended = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.EXTENDED, true);
        sticky_piston_south_extended = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.SOUTH)
                .withProperty(BlockPistonBase.EXTENDED, true);
        sticky_piston_west_extended = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.WEST)
                .withProperty(BlockPistonBase.EXTENDED, true);
        sticky_piston_east_extended = Blocks.sticky_piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.EAST)
                .withProperty(BlockPistonBase.EXTENDED, true);
        web = Blocks.web.getDefaultState();
        tallgrass_deadbush = Blocks.tallgrass.getDefaultState();
        tallgrass_tallgrass = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);
        tallgrass_fern = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.FERN);
        deadbush = Blocks.deadbush.getDefaultState();
        piston_down = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.DOWN);
        piston_up = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.UP);
        piston_north = Blocks.piston.getDefaultState();
        piston_south = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.SOUTH);
        piston_west = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.WEST);
        piston_east = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.EAST);
        piston_down_extended = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.DOWN)
                .withProperty(BlockPistonBase.EXTENDED, true);
        piston_up_extended = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.UP).withProperty(BlockPistonBase.EXTENDED, true);
        piston_north_extended = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.EXTENDED, true);
        piston_south_extended = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.SOUTH)
                .withProperty(BlockPistonBase.EXTENDED, true);
        piston_west_extended = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.WEST)
                .withProperty(BlockPistonBase.EXTENDED, true);
        piston_east_extended = Blocks.piston.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.EAST)
                .withProperty(BlockPistonBase.EXTENDED, true);
        piston_head_normal_down = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.DOWN);
        piston_head_normal_up = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.UP);
        piston_head_normal_north = Blocks.piston_head.getDefaultState();
        piston_head_normal_south = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.SOUTH);
        piston_head_normal_west = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.WEST);
        piston_head_normal_east = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.EAST);
        piston_head_sticky_down = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
                .withProperty(BlockPistonMoving.FACING, EnumFacing.DOWN);
        piston_head_sticky_up = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
                .withProperty(BlockPistonMoving.FACING, EnumFacing.UP);
        piston_head_sticky_north = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY);
        piston_head_sticky_south = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
                .withProperty(BlockPistonMoving.FACING, EnumFacing.SOUTH);
        piston_head_sticky_west = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
                .withProperty(BlockPistonMoving.FACING, EnumFacing.WEST);
        piston_head_sticky_east = Blocks.piston_head.getDefaultState().withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
                .withProperty(BlockPistonMoving.FACING, EnumFacing.EAST);
        wool_white = Blocks.wool.getDefaultState();
        wool_orange = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
        wool_magenta = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.MAGENTA);
        wool_lightblue = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE);
        wool_yellow = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
        wool_lime = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIME);
        wool_pink = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PINK);
        wool_gray = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
        wool_silver = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
        wool_cyan = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN);
        wool_purple = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PURPLE);
        wool_blue = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLUE);
        wool_brown = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
        wool_green = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN);
        wool_red = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
        wool_black = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK);
        piston_extension_normal_down = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.DOWN);
        piston_extension_normal_up = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.UP);
        piston_extension_normal_north = Blocks.piston_extension.getDefaultState();
        piston_extension_normal_south = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.SOUTH);
        piston_extension_normal_west = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.WEST);
        piston_extension_normal_east = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, EnumFacing.EAST);
        piston_extension_sticky_down = Blocks.piston_extension.getDefaultState()
                .withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY).withProperty(BlockPistonMoving.FACING, EnumFacing.DOWN);
        piston_extension_sticky_up = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
                .withProperty(BlockPistonMoving.FACING, EnumFacing.UP);
        piston_extension_sticky_north = Blocks.piston_extension.getDefaultState()
                .withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY);
        piston_extension_sticky_south = Blocks.piston_extension.getDefaultState()
                .withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY).withProperty(BlockPistonMoving.FACING, EnumFacing.SOUTH);
        piston_extension_sticky_west = Blocks.piston_extension.getDefaultState()
                .withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY).withProperty(BlockPistonMoving.FACING, EnumFacing.WEST);
        piston_extension_sticky_east = Blocks.piston_extension.getDefaultState()
                .withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.STICKY).withProperty(BlockPistonMoving.FACING, EnumFacing.EAST);
        yellow_flower = Blocks.yellow_flower.getDefaultState();
        red_flower_poppy = Blocks.red_flower.getDefaultState();
        red_flower_blueorchid = Blocks.red_flower.getDefaultState().withProperty(Blocks.red_flower.getTypeProperty(), BlockFlower.EnumFlowerType.BLUE_ORCHID);
        red_flower_allium = Blocks.red_flower.getDefaultState().withProperty(Blocks.red_flower.getTypeProperty(), BlockFlower.EnumFlowerType.ALLIUM);
        red_flower_houstonia = Blocks.red_flower.getDefaultState().withProperty(Blocks.red_flower.getTypeProperty(), BlockFlower.EnumFlowerType.HOUSTONIA);
        red_flower_redtulip = Blocks.red_flower.getDefaultState().withProperty(Blocks.red_flower.getTypeProperty(), BlockFlower.EnumFlowerType.RED_TULIP);
        red_flower_orangetulip = Blocks.red_flower.getDefaultState().withProperty(Blocks.red_flower.getTypeProperty(), BlockFlower.EnumFlowerType.ORANGE_TULIP);
        red_flower_whitetulip = Blocks.red_flower.getDefaultState().withProperty(Blocks.red_flower.getTypeProperty(), BlockFlower.EnumFlowerType.WHITE_TULIP);
        red_flower_pinktulip = Blocks.red_flower.getDefaultState().withProperty(Blocks.red_flower.getTypeProperty(), BlockFlower.EnumFlowerType.PINK_TULIP);
        red_flower_oxeyedaisy = Blocks.red_flower.getDefaultState().withProperty(Blocks.red_flower.getTypeProperty(), BlockFlower.EnumFlowerType.OXEYE_DAISY);
        brown_mushroom = Blocks.brown_mushroom.getDefaultState();
        red_mushroom = Blocks.red_mushroom.getDefaultState();
        gold_block = Blocks.gold_block.getDefaultState();
        iron_block = Blocks.iron_block.getDefaultState();
        double_stone_slab_stone = Blocks.double_stone_slab.getDefaultState();
        double_stone_slab_sandstone = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SAND);
        double_stone_slab_woodold = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.WOOD);
        double_stone_slab_cobblestone = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.COBBLESTONE);
        double_stone_slab_brick = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.BRICK);
        double_stone_slab_stonebrick = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SMOOTHBRICK);
        double_stone_slab_netherbrick = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK);
        double_stone_slab_quartz = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.QUARTZ);
        double_stone_slab_stone_seamless = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.SEAMLESS, true);
        double_stone_slab_sandstone_seamless = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SAND)
                .withProperty(BlockStoneSlab.SEAMLESS, true);
        double_stone_slab_woodold_seamless = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.WOOD)
                .withProperty(BlockStoneSlab.SEAMLESS, true);
        double_stone_slab_cobblestone_seamless = Blocks.double_stone_slab.getDefaultState()
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.COBBLESTONE).withProperty(BlockStoneSlab.SEAMLESS, true);
        double_stone_slab_brick_seamless = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.BRICK)
                .withProperty(BlockStoneSlab.SEAMLESS, true);
        double_stone_slab_stonebrick_seamless = Blocks.double_stone_slab.getDefaultState()
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SMOOTHBRICK).withProperty(BlockStoneSlab.SEAMLESS, true);
        double_stone_slab_netherbrick_seamless = Blocks.double_stone_slab.getDefaultState()
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK).withProperty(BlockStoneSlab.SEAMLESS, true);
        double_stone_slab_quartz_seamless = Blocks.double_stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.QUARTZ)
                .withProperty(BlockStoneSlab.SEAMLESS, true);
        stone_slab_bottom_stone = Blocks.stone_slab.getDefaultState();
        stone_slab_bottom_sandstone = Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SAND);
        stone_slab_bottom_woodold = Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.WOOD);
        stone_slab_bottom_cobblestone = Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.COBBLESTONE);
        stone_slab_bottom_brick = Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.BRICK);
        stone_slab_bottom_stonebrick = Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SMOOTHBRICK);
        stone_slab_bottom_netherbrick = Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK);
        stone_slab_bottom_quartz = Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.QUARTZ);
        stone_slab_top_stone = Blocks.stone_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
        stone_slab_top_sandstone = Blocks.stone_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SAND);
        stone_slab_top_woodold = Blocks.stone_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.WOOD);
        stone_slab_top_cobblestone = Blocks.stone_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.COBBLESTONE);
        stone_slab_top_brick = Blocks.stone_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.BRICK);
        stone_slab_top_stonebrick = Blocks.stone_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SMOOTHBRICK);
        stone_slab_top_netherbrick = Blocks.stone_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK);
        stone_slab_top_quartz = Blocks.stone_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.QUARTZ);
        brick_block = Blocks.brick_block.getDefaultState();
        tnt = Blocks.tnt.getDefaultState();
        tnt_explode = Blocks.tnt.getDefaultState().withProperty(BlockTNT.EXPLODE, true);
        bookshelf = Blocks.bookshelf.getDefaultState();
        mossy_cobblestone = Blocks.mossy_cobblestone.getDefaultState();
        obsidian = Blocks.obsidian.getDefaultState();
        torch_east = Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST);
        torch_west = Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST);
        torch_south = Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH);
        torch_north = Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH);
        torch_up = Blocks.torch.getDefaultState();
        fire_age_0 = Blocks.fire.getDefaultState();
        fire_age_1 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 1);
        fire_age_2 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 2);
        fire_age_3 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 3);
        fire_age_4 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 4);
        fire_age_5 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 5);
        fire_age_6 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 6);
        fire_age_7 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 7);
        fire_age_8 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 8);
        fire_age_9 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 9);
        fire_age_10 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 10);
        fire_age_11 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 11);
        fire_age_12 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 12);
        fire_age_13 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 13);
        fire_age_14 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 14);
        fire_age_15 = Blocks.fire.getDefaultState().withProperty(BlockFire.AGE, 15);
        mob_spawner = Blocks.mob_spawner.getDefaultState();
        oak_stairs_bottom_east = Blocks.oak_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        oak_stairs_bottom_west = Blocks.oak_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        oak_stairs_bottom_south = Blocks.oak_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        oak_stairs_bottom_north = Blocks.oak_stairs.getDefaultState();
        oak_stairs_top_east = Blocks.oak_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        oak_stairs_top_west = Blocks.oak_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        oak_stairs_top_south = Blocks.oak_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        oak_stairs_top_north = Blocks.oak_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        chest_north = Blocks.chest.getDefaultState();
        chest_south = Blocks.chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
        chest_west = Blocks.chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
        chest_east = Blocks.chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST);
        redstone_wire_power_0 = Blocks.redstone_wire.getDefaultState();
        redstone_wire_power_1 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 1);
        redstone_wire_power_2 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 2);
        redstone_wire_power_3 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 3);
        redstone_wire_power_4 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 4);
        redstone_wire_power_5 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 5);
        redstone_wire_power_6 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 6);
        redstone_wire_power_7 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 7);
        redstone_wire_power_8 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 8);
        redstone_wire_power_9 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 9);
        redstone_wire_power_10 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 10);
        redstone_wire_power_11 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 11);
        redstone_wire_power_12 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 12);
        redstone_wire_power_13 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 13);
        redstone_wire_power_14 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 14);
        redstone_wire_power_15 = Blocks.redstone_wire.getDefaultState().withProperty(BlockRedstoneWire.POWER, 15);
        diamond_ore = Blocks.diamond_ore.getDefaultState();
        diamond_block = Blocks.diamond_block.getDefaultState();
        crafting_table = Blocks.crafting_table.getDefaultState();
        wheat_age_0 = Blocks.wheat.getDefaultState();
        wheat_age_1 = Blocks.wheat.getDefaultState().withProperty(BlockCrops.AGE, 1);
        wheat_age_2 = Blocks.wheat.getDefaultState().withProperty(BlockCrops.AGE, 2);
        wheat_age_3 = Blocks.wheat.getDefaultState().withProperty(BlockCrops.AGE, 3);
        wheat_age_4 = Blocks.wheat.getDefaultState().withProperty(BlockCrops.AGE, 4);
        wheat_age_5 = Blocks.wheat.getDefaultState().withProperty(BlockCrops.AGE, 5);
        wheat_age_6 = Blocks.wheat.getDefaultState().withProperty(BlockCrops.AGE, 6);
        wheat_age_7 = Blocks.wheat.getDefaultState().withProperty(BlockCrops.AGE, 7);
        farmland_moisture_0 = Blocks.farmland.getDefaultState();
        farmland_moisture_1 = Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 1);
        farmland_moisture_2 = Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 2);
        farmland_moisture_3 = Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 3);
        farmland_moisture_4 = Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 4);
        farmland_moisture_5 = Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 5);
        farmland_moisture_6 = Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 6);
        farmland_moisture_7 = Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7);
        furnace_north = Blocks.furnace.getDefaultState();
        furnace_south = Blocks.furnace.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.SOUTH);
        furnace_west = Blocks.furnace.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.WEST);
        furnace_east = Blocks.furnace.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.EAST);
        lit_furnace_north = Blocks.lit_furnace.getDefaultState();
        lit_furnace_south = Blocks.lit_furnace.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.SOUTH);
        lit_furnace_west = Blocks.lit_furnace.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.WEST);
        lit_furnace_east = Blocks.lit_furnace.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.EAST);
        standing_sign_rotation_0 = Blocks.standing_sign.getDefaultState();
        standing_sign_rotation_1 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 1);
        standing_sign_rotation_2 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 2);
        standing_sign_rotation_3 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 3);
        standing_sign_rotation_4 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 4);
        standing_sign_rotation_5 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 5);
        standing_sign_rotation_6 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 6);
        standing_sign_rotation_7 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 7);
        standing_sign_rotation_8 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 8);
        standing_sign_rotation_9 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 9);
        standing_sign_rotation_10 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 10);
        standing_sign_rotation_11 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 11);
        standing_sign_rotation_12 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 12);
        standing_sign_rotation_13 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 13);
        standing_sign_rotation_14 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 14);
        standing_sign_rotation_15 = Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, 15);
        oak_door_lower_left_east = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST);
        oak_door_lower_left_south = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH);
        oak_door_lower_left_west = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST);
        oak_door_lower_left_north = Blocks.oak_door.getDefaultState();
        oak_door_lower_left_east_open = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST).withProperty(BlockDoor.OPEN, true);
        oak_door_lower_left_south_open = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH).withProperty(BlockDoor.OPEN, true);
        oak_door_lower_left_west_open = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST).withProperty(BlockDoor.OPEN, true);
        oak_door_lower_left_north_open = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.OPEN, true);
        oak_door_upper_left_north = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
        oak_door_upper_right_north = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
        oak_door_upper_left_north_powered = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.POWERED, true);
        oak_door_upper_right_north_powered = Blocks.oak_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.POWERED, true);
        spruce_door_lower_left_east = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST);
        spruce_door_lower_left_south = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH);
        spruce_door_lower_left_west = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST);
        spruce_door_lower_left_north = Blocks.spruce_door.getDefaultState();
        spruce_door_lower_left_east_open = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
                .withProperty(BlockDoor.OPEN, true);
        spruce_door_lower_left_south_open = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
                .withProperty(BlockDoor.OPEN, true);
        spruce_door_lower_left_west_open = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
                .withProperty(BlockDoor.OPEN, true);
        spruce_door_lower_left_north_open = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.OPEN, true);
        spruce_door_upper_left_north = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
        spruce_door_upper_right_north = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
        spruce_door_upper_left_north_powered = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.POWERED, true);
        spruce_door_upper_right_north_powered = Blocks.spruce_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.POWERED, true);
        birch_door_lower_left_east = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST);
        birch_door_lower_left_south = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH);
        birch_door_lower_left_west = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST);
        birch_door_lower_left_north = Blocks.birch_door.getDefaultState();
        birch_door_lower_left_east_open = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
                .withProperty(BlockDoor.OPEN, true);
        birch_door_lower_left_south_open = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
                .withProperty(BlockDoor.OPEN, true);
        birch_door_lower_left_west_open = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
                .withProperty(BlockDoor.OPEN, true);
        birch_door_lower_left_north_open = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.OPEN, true);
        birch_door_upper_left_north = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
        birch_door_upper_right_north = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
        birch_door_upper_left_north_powered = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.POWERED, true);
        birch_door_upper_right_north_powered = Blocks.birch_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.POWERED, true);
        jungle_door_lower_left_east = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST);
        jungle_door_lower_left_south = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH);
        jungle_door_lower_left_west = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST);
        jungle_door_lower_left_north = Blocks.jungle_door.getDefaultState();
        jungle_door_lower_left_east_open = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
                .withProperty(BlockDoor.OPEN, true);
        jungle_door_lower_left_south_open = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
                .withProperty(BlockDoor.OPEN, true);
        jungle_door_lower_left_west_open = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
                .withProperty(BlockDoor.OPEN, true);
        jungle_door_lower_left_north_open = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.OPEN, true);
        jungle_door_upper_left_north = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
        jungle_door_upper_right_north = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
        jungle_door_upper_left_north_powered = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.POWERED, true);
        jungle_door_upper_right_north_powered = Blocks.jungle_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.POWERED, true);
        acacia_door_lower_left_east = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST);
        acacia_door_lower_left_south = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH);
        acacia_door_lower_left_west = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST);
        acacia_door_lower_left_north = Blocks.acacia_door.getDefaultState();
        acacia_door_lower_left_east_open = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
                .withProperty(BlockDoor.OPEN, true);
        acacia_door_lower_left_south_open = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
                .withProperty(BlockDoor.OPEN, true);
        acacia_door_lower_left_west_open = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
                .withProperty(BlockDoor.OPEN, true);
        acacia_door_lower_left_north_open = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.OPEN, true);
        acacia_door_upper_left_north = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
        acacia_door_upper_right_north = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
        acacia_door_upper_left_north_powered = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.POWERED, true);
        acacia_door_upper_right_north_powered = Blocks.acacia_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.POWERED, true);
        dark_oak_door_lower_left_east = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST);
        dark_oak_door_lower_left_south = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH);
        dark_oak_door_lower_left_west = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST);
        dark_oak_door_lower_left_north = Blocks.dark_oak_door.getDefaultState();
        dark_oak_door_lower_left_east_open = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
                .withProperty(BlockDoor.OPEN, true);
        dark_oak_door_lower_left_south_open = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
                .withProperty(BlockDoor.OPEN, true);
        dark_oak_door_lower_left_west_open = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
                .withProperty(BlockDoor.OPEN, true);
        dark_oak_door_lower_left_north_open = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.OPEN, true);
        dark_oak_door_upper_left_north = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
        dark_oak_door_upper_right_north = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
        dark_oak_door_upper_left_north_powered = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.POWERED, true);
        dark_oak_door_upper_right_north_powered = Blocks.dark_oak_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.POWERED, true);
        ladder_north = Blocks.ladder.getDefaultState();
        ladder_south = Blocks.ladder.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
        ladder_west = Blocks.ladder.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.WEST);
        ladder_east = Blocks.ladder.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.EAST);
        rail_northsouth = Blocks.rail.getDefaultState();
        rail_eastwest = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST);
        rail_ascendingeast = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
        rail_ascendingwest = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
        rail_ascendingnorth = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
        rail_ascendingsouth = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
        rail_southeast = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.SOUTH_EAST);
        rail_southwest = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.SOUTH_WEST);
        rail_northwest = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.NORTH_WEST);
        rail_northeast = Blocks.rail.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.NORTH_EAST);
        stone_stairs_bottom_east = Blocks.stone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        stone_stairs_bottom_west = Blocks.stone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        stone_stairs_bottom_south = Blocks.stone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        stone_stairs_bottom_north = Blocks.stone_stairs.getDefaultState();
        stone_stairs_top_east = Blocks.stone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        stone_stairs_top_west = Blocks.stone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        stone_stairs_top_south = Blocks.stone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        stone_stairs_top_north = Blocks.stone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        wall_sign_north = Blocks.wall_sign.getDefaultState();
        wall_sign_south = Blocks.wall_sign.getDefaultState().withProperty(BlockWallSign.FACING, EnumFacing.SOUTH);
        wall_sign_west = Blocks.wall_sign.getDefaultState().withProperty(BlockWallSign.FACING, EnumFacing.WEST);
        wall_sign_east = Blocks.wall_sign.getDefaultState().withProperty(BlockWallSign.FACING, EnumFacing.EAST);
        lever_downx = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_X);
        lever_east = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.EAST);
        lever_west = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.WEST);
        lever_south = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.SOUTH);
        lever_north = Blocks.lever.getDefaultState();
        lever_upz = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_Z);
        lever_upx = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_X);
        lever_downz = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_Z);
        lever_downx_powered = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_X)
                .withProperty(BlockLever.POWERED, true);
        lever_east_powered = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.EAST)
                .withProperty(BlockLever.POWERED, true);
        lever_west_powered = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.WEST)
                .withProperty(BlockLever.POWERED, true);
        lever_south_powered = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.SOUTH)
                .withProperty(BlockLever.POWERED, true);
        lever_north_powered = Blocks.lever.getDefaultState().withProperty(BlockLever.POWERED, true);
        lever_upz_powered = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_Z)
                .withProperty(BlockLever.POWERED, true);
        lever_upx_powered = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_X)
                .withProperty(BlockLever.POWERED, true);
        lever_downz_powered = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_Z)
                .withProperty(BlockLever.POWERED, true);
        stone_pressure_plate = Blocks.stone_pressure_plate.getDefaultState();
        stone_pressure_plate_powered = Blocks.stone_pressure_plate.getDefaultState().withProperty(BlockPressurePlate.POWERED, true);
        iron_door_lower_left_east = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST);
        iron_door_lower_left_south = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH);
        iron_door_lower_left_west = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST);
        iron_door_lower_left_north = Blocks.iron_door.getDefaultState();
        iron_door_lower_left_east_open = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST).withProperty(BlockDoor.OPEN, true);
        iron_door_lower_left_south_open = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
                .withProperty(BlockDoor.OPEN, true);
        iron_door_lower_left_west_open = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST).withProperty(BlockDoor.OPEN, true);
        iron_door_lower_left_north_open = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.OPEN, true);
        iron_door_upper_left_north = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
        iron_door_upper_right_north = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
        iron_door_upper_left_north_powered = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.POWERED, true);
        iron_door_upper_right_north_powered = Blocks.iron_door.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
                .withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.POWERED, true);
        wooden_pressure_plate = Blocks.wooden_pressure_plate.getDefaultState();
        wooden_pressure_plate_powered = Blocks.wooden_pressure_plate.getDefaultState().withProperty(BlockPressurePlate.POWERED, true);
        redstone_ore = Blocks.redstone_ore.getDefaultState();
        lit_redstone_ore = Blocks.lit_redstone_ore.getDefaultState();
        unlit_redstone_torch_east = Blocks.unlit_redstone_torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST);
        unlit_redstone_torch_west = Blocks.unlit_redstone_torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST);
        unlit_redstone_torch_south = Blocks.unlit_redstone_torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH);
        unlit_redstone_torch_north = Blocks.unlit_redstone_torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH);
        unlit_redstone_torch_up = Blocks.unlit_redstone_torch.getDefaultState();
        redstone_torch_east = Blocks.redstone_torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST);
        redstone_torch_west = Blocks.redstone_torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST);
        redstone_torch_south = Blocks.redstone_torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH);
        redstone_torch_north = Blocks.redstone_torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH);
        redstone_torch_up = Blocks.redstone_torch.getDefaultState();
        stone_button_down = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.DOWN);
        stone_button_east = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.EAST);
        stone_button_west = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.WEST);
        stone_button_south = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.SOUTH);
        stone_button_north = Blocks.stone_button.getDefaultState();
        stone_button_up = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.UP);
        stone_button_down_powered = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.DOWN)
                .withProperty(BlockButton.POWERED, true);
        stone_button_east_powered = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.EAST)
                .withProperty(BlockButton.POWERED, true);
        stone_button_west_powered = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.WEST)
                .withProperty(BlockButton.POWERED, true);
        stone_button_south_powered = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.SOUTH)
                .withProperty(BlockButton.POWERED, true);
        stone_button_north_powered = Blocks.stone_button.getDefaultState().withProperty(BlockButton.POWERED, true);
        stone_button_up_powered = Blocks.stone_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.UP).withProperty(BlockButton.POWERED, true);
        snow_layer_layers_1 = Blocks.snow_layer.getDefaultState();
        snow_layer_layers_2 = Blocks.snow_layer.getDefaultState().withProperty(BlockSnow.LAYERS, 2);
        snow_layer_layers_3 = Blocks.snow_layer.getDefaultState().withProperty(BlockSnow.LAYERS, 3);
        snow_layer_layers_4 = Blocks.snow_layer.getDefaultState().withProperty(BlockSnow.LAYERS, 4);
        snow_layer_layers_5 = Blocks.snow_layer.getDefaultState().withProperty(BlockSnow.LAYERS, 5);
        snow_layer_layers_6 = Blocks.snow_layer.getDefaultState().withProperty(BlockSnow.LAYERS, 6);
        snow_layer_layers_7 = Blocks.snow_layer.getDefaultState().withProperty(BlockSnow.LAYERS, 7);
        snow_layer_layers_8 = Blocks.snow_layer.getDefaultState().withProperty(BlockSnow.LAYERS, 8);
        ice = Blocks.ice.getDefaultState();
        snow = Blocks.snow.getDefaultState();
        cactus_age_0 = Blocks.cactus.getDefaultState();
        cactus_age_1 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 1);
        cactus_age_2 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 2);
        cactus_age_3 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 3);
        cactus_age_4 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 4);
        cactus_age_5 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 5);
        cactus_age_6 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 6);
        cactus_age_7 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 7);
        cactus_age_8 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 8);
        cactus_age_9 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 9);
        cactus_age_10 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 10);
        cactus_age_11 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 11);
        cactus_age_12 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 12);
        cactus_age_13 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 13);
        cactus_age_14 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 14);
        cactus_age_15 = Blocks.cactus.getDefaultState().withProperty(BlockCactus.AGE, 15);
        clay = Blocks.clay.getDefaultState();
        reeds_age_0 = Blocks.reeds.getDefaultState();
        reeds_age_1 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 1);
        reeds_age_2 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 2);
        reeds_age_3 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 3);
        reeds_age_4 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 4);
        reeds_age_5 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 5);
        reeds_age_6 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 6);
        reeds_age_7 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 7);
        reeds_age_8 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 8);
        reeds_age_9 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 9);
        reeds_age_10 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 10);
        reeds_age_11 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 11);
        reeds_age_12 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 12);
        reeds_age_13 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 13);
        reeds_age_14 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 14);
        reeds_age_15 = Blocks.reeds.getDefaultState().withProperty(BlockReed.AGE, 15);
        jukebox = Blocks.jukebox.getDefaultState();
        jukebox_hasrecord = Blocks.jukebox.getDefaultState().withProperty(BlockJukebox.HAS_RECORD, true);
        oak_fence = Blocks.oak_fence.getDefaultState();
        spruce_fence = Blocks.spruce_fence.getDefaultState();
        birch_fence = Blocks.birch_fence.getDefaultState();
        jungle_fence = Blocks.jungle_fence.getDefaultState();
        dark_oak_fence = Blocks.dark_oak_fence.getDefaultState();
        acacia_fence = Blocks.acacia_fence.getDefaultState();
        pumpkin_south = Blocks.pumpkin.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        pumpkin_west = Blocks.pumpkin.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        pumpkin_north = Blocks.pumpkin.getDefaultState();
        pumpkin_east = Blocks.pumpkin.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        netherrack = Blocks.netherrack.getDefaultState();
        soul_sand = Blocks.soul_sand.getDefaultState();
        glowstone = Blocks.glowstone.getDefaultState();
        portal_x = Blocks.portal.getDefaultState();
        portal_z = Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, EnumFacing.Axis.Z);
        lit_pumpkin_south = Blocks.lit_pumpkin.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        lit_pumpkin_west = Blocks.lit_pumpkin.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        lit_pumpkin_north = Blocks.lit_pumpkin.getDefaultState();
        lit_pumpkin_east = Blocks.lit_pumpkin.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        cake_bites_0 = Blocks.cake.getDefaultState();
        cake_bites_1 = Blocks.cake.getDefaultState().withProperty(BlockCake.BITES, 1);
        cake_bites_2 = Blocks.cake.getDefaultState().withProperty(BlockCake.BITES, 2);
        cake_bites_3 = Blocks.cake.getDefaultState().withProperty(BlockCake.BITES, 3);
        cake_bites_4 = Blocks.cake.getDefaultState().withProperty(BlockCake.BITES, 4);
        cake_bites_5 = Blocks.cake.getDefaultState().withProperty(BlockCake.BITES, 5);
        cake_bites_6 = Blocks.cake.getDefaultState().withProperty(BlockCake.BITES, 6);
        unpowered_repeater_south_delay_1 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        unpowered_repeater_west_delay_1 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        unpowered_repeater_north_delay_1 = Blocks.unpowered_repeater.getDefaultState();
        unpowered_repeater_east_delay_1 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        unpowered_repeater_south_delay_2 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockRedstoneRepeater.DELAY, 2);
        unpowered_repeater_west_delay_2 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockRedstoneRepeater.DELAY, 2);
        unpowered_repeater_north_delay_2 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockRedstoneRepeater.DELAY, 2);
        unpowered_repeater_east_delay_2 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockRedstoneRepeater.DELAY, 2);
        unpowered_repeater_south_delay_3 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockRedstoneRepeater.DELAY, 3);
        unpowered_repeater_west_delay_3 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockRedstoneRepeater.DELAY, 3);
        unpowered_repeater_north_delay_3 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockRedstoneRepeater.DELAY, 3);
        unpowered_repeater_east_delay_3 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockRedstoneRepeater.DELAY, 3);
        unpowered_repeater_south_delay_4 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockRedstoneRepeater.DELAY, 4);
        unpowered_repeater_west_delay_4 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockRedstoneRepeater.DELAY, 4);
        unpowered_repeater_north_delay_4 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockRedstoneRepeater.DELAY, 4);
        unpowered_repeater_east_delay_4 = Blocks.unpowered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockRedstoneRepeater.DELAY, 4);
        powered_repeater_south_delay_1 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        powered_repeater_west_delay_1 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        powered_repeater_north_delay_1 = Blocks.powered_repeater.getDefaultState();
        powered_repeater_east_delay_1 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        powered_repeater_south_delay_2 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockRedstoneRepeater.DELAY, 2);
        powered_repeater_west_delay_2 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockRedstoneRepeater.DELAY, 2);
        powered_repeater_north_delay_2 = Blocks.powered_repeater.getDefaultState().withProperty(BlockRedstoneRepeater.DELAY, 2);
        powered_repeater_east_delay_2 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockRedstoneRepeater.DELAY, 2);
        powered_repeater_south_delay_3 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockRedstoneRepeater.DELAY, 3);
        powered_repeater_west_delay_3 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockRedstoneRepeater.DELAY, 3);
        powered_repeater_north_delay_3 = Blocks.powered_repeater.getDefaultState().withProperty(BlockRedstoneRepeater.DELAY, 3);
        powered_repeater_east_delay_3 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockRedstoneRepeater.DELAY, 3);
        powered_repeater_south_delay_4 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockRedstoneRepeater.DELAY, 4);
        powered_repeater_west_delay_4 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockRedstoneRepeater.DELAY, 4);
        powered_repeater_north_delay_4 = Blocks.powered_repeater.getDefaultState().withProperty(BlockRedstoneRepeater.DELAY, 4);
        powered_repeater_east_delay_4 = Blocks.powered_repeater.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockRedstoneRepeater.DELAY, 4);
        trapdoor_bottom_north = Blocks.trapdoor.getDefaultState();
        trapdoor_bottom_south = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH);
        trapdoor_bottom_west = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.WEST);
        trapdoor_bottom_east = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.EAST);
        trapdoor_bottom_north_open = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.OPEN, true);
        trapdoor_bottom_south_open = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH)
                .withProperty(BlockTrapDoor.OPEN, true);
        trapdoor_bottom_west_open = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.WEST)
                .withProperty(BlockTrapDoor.OPEN, true);
        trapdoor_bottom_east_open = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.EAST)
                .withProperty(BlockTrapDoor.OPEN, true);
        trapdoor_top_north = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP);
        trapdoor_top_south = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH);
        trapdoor_top_west = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.WEST);
        trapdoor_top_east = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.EAST);
        trapdoor_top_north_open = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.OPEN, true);
        trapdoor_top_south_open = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH).withProperty(BlockTrapDoor.OPEN, true);
        trapdoor_top_west_open = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.WEST).withProperty(BlockTrapDoor.OPEN, true);
        trapdoor_top_east_open = Blocks.trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.EAST).withProperty(BlockTrapDoor.OPEN, true);
        monster_egg_stone = Blocks.monster_egg.getDefaultState();
        monster_egg_cobblestone = Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.COBBLESTONE);
        monster_egg_stonebrick = Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONEBRICK);
        monster_egg_mossybrick = Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.MOSSY_STONEBRICK);
        monster_egg_crackedbrick = Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.CRACKED_STONEBRICK);
        monster_egg_chiseledbrick = Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.CHISELED_STONEBRICK);
        stonebrick = Blocks.stonebrick.getDefaultState();
        stonebrick_mossystonebrick = Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
        stonebrick_crackedstonebrick = Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
        stonebrick_chiseledstonebrick = Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
        brown_mushroom_block_allinside = Blocks.brown_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.ALL_INSIDE);
        brown_mushroom_block_northwest = Blocks.brown_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.NORTH_WEST);
        brown_mushroom_block_north = Blocks.brown_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.NORTH);
        brown_mushroom_block_northeast = Blocks.brown_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.NORTH_EAST);
        brown_mushroom_block_west = Blocks.brown_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.WEST);
        brown_mushroom_block_center = Blocks.brown_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.CENTER);
        brown_mushroom_block_east = Blocks.brown_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.EAST);
        brown_mushroom_block_southwest = Blocks.brown_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.SOUTH_WEST);
        brown_mushroom_block_south = Blocks.brown_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.SOUTH);
        brown_mushroom_block_southeast = Blocks.brown_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.SOUTH_EAST);
        brown_mushroom_block_stem = Blocks.brown_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.STEM);
        brown_mushroom_block_alloutside = Blocks.brown_mushroom_block.getDefaultState();
        brown_mushroom_block_allstem = Blocks.brown_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.ALL_STEM);
        red_mushroom_block_allinside = Blocks.red_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.ALL_INSIDE);
        red_mushroom_block_northwest = Blocks.red_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.NORTH_WEST);
        red_mushroom_block_north = Blocks.red_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.NORTH);
        red_mushroom_block_northeast = Blocks.red_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.NORTH_EAST);
        red_mushroom_block_west = Blocks.red_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.WEST);
        red_mushroom_block_center = Blocks.red_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.CENTER);
        red_mushroom_block_east = Blocks.red_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.EAST);
        red_mushroom_block_southwest = Blocks.red_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.SOUTH_WEST);
        red_mushroom_block_south = Blocks.red_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.SOUTH);
        red_mushroom_block_southeast = Blocks.red_mushroom_block.getDefaultState()
                .withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.SOUTH_EAST);
        red_mushroom_block_stem = Blocks.red_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.STEM);
        red_mushroom_block_alloutside = Blocks.red_mushroom_block.getDefaultState();
        red_mushroom_block_allstem = Blocks.red_mushroom_block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.ALL_STEM);
        iron_bars = Blocks.iron_bars.getDefaultState();
        glass_pane = Blocks.glass_pane.getDefaultState();
        melon_block = Blocks.melon_block.getDefaultState();
        pumpkin_stem_age_0 = Blocks.pumpkin_stem.getDefaultState();
        pumpkin_stem_age_1 = Blocks.pumpkin_stem.getDefaultState().withProperty(BlockStem.AGE, 1);
        pumpkin_stem_age_2 = Blocks.pumpkin_stem.getDefaultState().withProperty(BlockStem.AGE, 2);
        pumpkin_stem_age_3 = Blocks.pumpkin_stem.getDefaultState().withProperty(BlockStem.AGE, 3);
        pumpkin_stem_age_4 = Blocks.pumpkin_stem.getDefaultState().withProperty(BlockStem.AGE, 4);
        pumpkin_stem_age_5 = Blocks.pumpkin_stem.getDefaultState().withProperty(BlockStem.AGE, 5);
        pumpkin_stem_age_6 = Blocks.pumpkin_stem.getDefaultState().withProperty(BlockStem.AGE, 6);
        pumpkin_stem_age_7 = Blocks.pumpkin_stem.getDefaultState().withProperty(BlockStem.AGE, 7);
        melon_stem_age_0 = Blocks.melon_stem.getDefaultState();
        melon_stem_age_1 = Blocks.melon_stem.getDefaultState().withProperty(BlockStem.AGE, 1);
        melon_stem_age_2 = Blocks.melon_stem.getDefaultState().withProperty(BlockStem.AGE, 2);
        melon_stem_age_3 = Blocks.melon_stem.getDefaultState().withProperty(BlockStem.AGE, 3);
        melon_stem_age_4 = Blocks.melon_stem.getDefaultState().withProperty(BlockStem.AGE, 4);
        melon_stem_age_5 = Blocks.melon_stem.getDefaultState().withProperty(BlockStem.AGE, 5);
        melon_stem_age_6 = Blocks.melon_stem.getDefaultState().withProperty(BlockStem.AGE, 6);
        melon_stem_age_7 = Blocks.melon_stem.getDefaultState().withProperty(BlockStem.AGE, 7);
        vine = Blocks.vine.getDefaultState();
        vine_south = Blocks.vine.getDefaultState().withProperty(BlockVine.SOUTH, true);
        vine_west = Blocks.vine.getDefaultState().withProperty(BlockVine.WEST, true);
        vine_south_west = Blocks.vine.getDefaultState().withProperty(BlockVine.SOUTH, true).withProperty(BlockVine.WEST, true);
        vine_north = Blocks.vine.getDefaultState().withProperty(BlockVine.NORTH, true);
        vine_north_south = Blocks.vine.getDefaultState().withProperty(BlockVine.NORTH, true).withProperty(BlockVine.SOUTH, true);
        vine_north_west = Blocks.vine.getDefaultState().withProperty(BlockVine.NORTH, true).withProperty(BlockVine.WEST, true);
        vine_north_south_west = Blocks.vine.getDefaultState().withProperty(BlockVine.NORTH, true).withProperty(BlockVine.SOUTH, true)
                .withProperty(BlockVine.WEST, true);
        vine_east = Blocks.vine.getDefaultState().withProperty(BlockVine.EAST, true);
        vine_east_south = Blocks.vine.getDefaultState().withProperty(BlockVine.EAST, true).withProperty(BlockVine.SOUTH, true);
        vine_east_west = Blocks.vine.getDefaultState().withProperty(BlockVine.EAST, true).withProperty(BlockVine.WEST, true);
        vine_east_south_west = Blocks.vine.getDefaultState().withProperty(BlockVine.EAST, true).withProperty(BlockVine.SOUTH, true)
                .withProperty(BlockVine.WEST, true);
        vine_east_north = Blocks.vine.getDefaultState().withProperty(BlockVine.EAST, true).withProperty(BlockVine.NORTH, true);
        vine_east_north_south = Blocks.vine.getDefaultState().withProperty(BlockVine.EAST, true).withProperty(BlockVine.NORTH, true)
                .withProperty(BlockVine.SOUTH, true);
        vine_east_north_west = Blocks.vine.getDefaultState().withProperty(BlockVine.EAST, true).withProperty(BlockVine.NORTH, true)
                .withProperty(BlockVine.WEST, true);
        vine_east_north_south_west = Blocks.vine.getDefaultState().withProperty(BlockVine.EAST, true).withProperty(BlockVine.NORTH, true)
                .withProperty(BlockVine.SOUTH, true).withProperty(BlockVine.WEST, true);
        oak_fence_gate_south = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        oak_fence_gate_west = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        oak_fence_gate_north = Blocks.oak_fence_gate.getDefaultState();
        oak_fence_gate_east = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        oak_fence_gate_south_open = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true);
        oak_fence_gate_west_open = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true);
        oak_fence_gate_north_open = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true);
        oak_fence_gate_east_open = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true);
        oak_fence_gate_south_powered = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.POWERED, true);
        oak_fence_gate_west_powered = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.POWERED, true);
        oak_fence_gate_north_powered = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockFenceGate.POWERED, true);
        oak_fence_gate_east_powered = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.POWERED, true);
        oak_fence_gate_south_open_powered = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        oak_fence_gate_west_open_powered = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        oak_fence_gate_north_open_powered = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true)
                .withProperty(BlockFenceGate.POWERED, true);
        oak_fence_gate_east_open_powered = Blocks.oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        spruce_fence_gate_south = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        spruce_fence_gate_west = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        spruce_fence_gate_north = Blocks.spruce_fence_gate.getDefaultState();
        spruce_fence_gate_east = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        spruce_fence_gate_south_open = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true);
        spruce_fence_gate_west_open = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true);
        spruce_fence_gate_north_open = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true);
        spruce_fence_gate_east_open = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true);
        spruce_fence_gate_south_powered = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.POWERED, true);
        spruce_fence_gate_west_powered = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.POWERED, true);
        spruce_fence_gate_north_powered = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockFenceGate.POWERED, true);
        spruce_fence_gate_east_powered = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.POWERED, true);
        spruce_fence_gate_south_open_powered = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        spruce_fence_gate_west_open_powered = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        spruce_fence_gate_north_open_powered = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true)
                .withProperty(BlockFenceGate.POWERED, true);
        spruce_fence_gate_east_open_powered = Blocks.spruce_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        birch_fence_gate_south = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        birch_fence_gate_west = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        birch_fence_gate_north = Blocks.birch_fence_gate.getDefaultState();
        birch_fence_gate_east = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        birch_fence_gate_south_open = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true);
        birch_fence_gate_west_open = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true);
        birch_fence_gate_north_open = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true);
        birch_fence_gate_east_open = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true);
        birch_fence_gate_south_powered = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.POWERED, true);
        birch_fence_gate_west_powered = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.POWERED, true);
        birch_fence_gate_north_powered = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockFenceGate.POWERED, true);
        birch_fence_gate_east_powered = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.POWERED, true);
        birch_fence_gate_south_open_powered = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        birch_fence_gate_west_open_powered = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        birch_fence_gate_north_open_powered = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true)
                .withProperty(BlockFenceGate.POWERED, true);
        birch_fence_gate_east_open_powered = Blocks.birch_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        jungle_fence_gate_south = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        jungle_fence_gate_west = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        jungle_fence_gate_north = Blocks.jungle_fence_gate.getDefaultState();
        jungle_fence_gate_east = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        jungle_fence_gate_south_open = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true);
        jungle_fence_gate_west_open = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true);
        jungle_fence_gate_north_open = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true);
        jungle_fence_gate_east_open = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true);
        jungle_fence_gate_south_powered = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.POWERED, true);
        jungle_fence_gate_west_powered = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.POWERED, true);
        jungle_fence_gate_north_powered = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockFenceGate.POWERED, true);
        jungle_fence_gate_east_powered = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.POWERED, true);
        jungle_fence_gate_south_open_powered = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        jungle_fence_gate_west_open_powered = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        jungle_fence_gate_north_open_powered = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true)
                .withProperty(BlockFenceGate.POWERED, true);
        jungle_fence_gate_east_open_powered = Blocks.jungle_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        dark_oak_fence_gate_south = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        dark_oak_fence_gate_west = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        dark_oak_fence_gate_north = Blocks.dark_oak_fence_gate.getDefaultState();
        dark_oak_fence_gate_east = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        dark_oak_fence_gate_south_open = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true);
        dark_oak_fence_gate_west_open = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true);
        dark_oak_fence_gate_north_open = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true);
        dark_oak_fence_gate_east_open = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true);
        dark_oak_fence_gate_south_powered = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.POWERED, true);
        dark_oak_fence_gate_west_powered = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.POWERED, true);
        dark_oak_fence_gate_north_powered = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockFenceGate.POWERED, true);
        dark_oak_fence_gate_east_powered = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.POWERED, true);
        dark_oak_fence_gate_south_open_powered = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        dark_oak_fence_gate_west_open_powered = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        dark_oak_fence_gate_north_open_powered = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true)
                .withProperty(BlockFenceGate.POWERED, true);
        dark_oak_fence_gate_east_open_powered = Blocks.dark_oak_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        acacia_fence_gate_south = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        acacia_fence_gate_west = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        acacia_fence_gate_north = Blocks.acacia_fence_gate.getDefaultState();
        acacia_fence_gate_east = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        acacia_fence_gate_south_open = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true);
        acacia_fence_gate_west_open = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true);
        acacia_fence_gate_north_open = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true);
        acacia_fence_gate_east_open = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true);
        acacia_fence_gate_south_powered = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.POWERED, true);
        acacia_fence_gate_west_powered = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.POWERED, true);
        acacia_fence_gate_north_powered = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockFenceGate.POWERED, true);
        acacia_fence_gate_east_powered = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.POWERED, true);
        acacia_fence_gate_south_open_powered = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        acacia_fence_gate_west_open_powered = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        acacia_fence_gate_north_open_powered = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockFenceGate.OPEN, true)
                .withProperty(BlockFenceGate.POWERED, true);
        acacia_fence_gate_east_open_powered = Blocks.acacia_fence_gate.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockFenceGate.OPEN, true).withProperty(BlockFenceGate.POWERED, true);
        brick_stairs_bottom_east = Blocks.brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        brick_stairs_bottom_west = Blocks.brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        brick_stairs_bottom_south = Blocks.brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        brick_stairs_bottom_north = Blocks.brick_stairs.getDefaultState();
        brick_stairs_top_east = Blocks.brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        brick_stairs_top_west = Blocks.brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        brick_stairs_top_south = Blocks.brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        brick_stairs_top_north = Blocks.brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        stone_brick_stairs_bottom_east = Blocks.stone_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        stone_brick_stairs_bottom_west = Blocks.stone_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        stone_brick_stairs_bottom_south = Blocks.stone_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        stone_brick_stairs_bottom_north = Blocks.stone_brick_stairs.getDefaultState();
        stone_brick_stairs_top_east = Blocks.stone_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        stone_brick_stairs_top_west = Blocks.stone_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        stone_brick_stairs_top_south = Blocks.stone_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        stone_brick_stairs_top_north = Blocks.stone_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        mycelium = Blocks.mycelium.getDefaultState();
        waterlily = Blocks.waterlily.getDefaultState();
        nether_brick = Blocks.nether_brick.getDefaultState();
        nether_brick_fence = Blocks.nether_brick_fence.getDefaultState();
        nether_brick_stairs_bottom_east = Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        nether_brick_stairs_bottom_west = Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        nether_brick_stairs_bottom_south = Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        nether_brick_stairs_bottom_north = Blocks.nether_brick_stairs.getDefaultState();
        nether_brick_stairs_top_east = Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        nether_brick_stairs_top_west = Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        nether_brick_stairs_top_south = Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        nether_brick_stairs_top_north = Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        nether_wart_age_0 = Blocks.nether_wart.getDefaultState();
        nether_wart_age_1 = Blocks.nether_wart.getDefaultState().withProperty(BlockNetherWart.AGE, 1);
        nether_wart_age_2 = Blocks.nether_wart.getDefaultState().withProperty(BlockNetherWart.AGE, 2);
        nether_wart_age_3 = Blocks.nether_wart.getDefaultState().withProperty(BlockNetherWart.AGE, 3);
        enchanting_table = Blocks.enchanting_table.getDefaultState();
        brewing_stand = Blocks.brewing_stand.getDefaultState();
        brewing_stand_hasbottle0 = Blocks.brewing_stand.getDefaultState().withProperty(BlockBrewingStand.HAS_BOTTLE[0], true);
        brewing_stand_hasbottle1 = Blocks.brewing_stand.getDefaultState().withProperty(BlockBrewingStand.HAS_BOTTLE[1], true);
        brewing_stand_hasbottle0_hasbottle1 = Blocks.brewing_stand.getDefaultState().withProperty(BlockBrewingStand.HAS_BOTTLE[0], true)
                .withProperty(BlockBrewingStand.HAS_BOTTLE[1], true);
        brewing_stand_hasbottle2 = Blocks.brewing_stand.getDefaultState().withProperty(BlockBrewingStand.HAS_BOTTLE[2], true);
        brewing_stand_hasbottle0_hasbottle2 = Blocks.brewing_stand.getDefaultState().withProperty(BlockBrewingStand.HAS_BOTTLE[0], true)
                .withProperty(BlockBrewingStand.HAS_BOTTLE[2], true);
        brewing_stand_hasbottle1_hasbottle2 = Blocks.brewing_stand.getDefaultState().withProperty(BlockBrewingStand.HAS_BOTTLE[1], true)
                .withProperty(BlockBrewingStand.HAS_BOTTLE[2], true);
        brewing_stand_hasbottle0_hasbottle1_hasbottle2 = Blocks.brewing_stand.getDefaultState().withProperty(BlockBrewingStand.HAS_BOTTLE[0], true)
                .withProperty(BlockBrewingStand.HAS_BOTTLE[1], true).withProperty(BlockBrewingStand.HAS_BOTTLE[2], true);
        cauldron_level_0 = Blocks.cauldron.getDefaultState();
        cauldron_level_1 = Blocks.cauldron.getDefaultState().withProperty(BlockCauldron.LEVEL, 1);
        cauldron_level_2 = Blocks.cauldron.getDefaultState().withProperty(BlockCauldron.LEVEL, 2);
        cauldron_level_3 = Blocks.cauldron.getDefaultState().withProperty(BlockCauldron.LEVEL, 3);
        end_portal = Blocks.end_portal.getDefaultState();
        end_portal_frame_south = Blocks.end_portal_frame.getDefaultState().withProperty(BlockEndPortalFrame.FACING, EnumFacing.SOUTH);
        end_portal_frame_west = Blocks.end_portal_frame.getDefaultState().withProperty(BlockEndPortalFrame.FACING, EnumFacing.WEST);
        end_portal_frame_north = Blocks.end_portal_frame.getDefaultState();
        end_portal_frame_east = Blocks.end_portal_frame.getDefaultState().withProperty(BlockEndPortalFrame.FACING, EnumFacing.EAST);
        end_portal_frame_south_eye = Blocks.end_portal_frame.getDefaultState().withProperty(BlockEndPortalFrame.FACING, EnumFacing.SOUTH)
                .withProperty(BlockEndPortalFrame.EYE, true);
        end_portal_frame_west_eye = Blocks.end_portal_frame.getDefaultState().withProperty(BlockEndPortalFrame.FACING, EnumFacing.WEST)
                .withProperty(BlockEndPortalFrame.EYE, true);
        end_portal_frame_north_eye = Blocks.end_portal_frame.getDefaultState().withProperty(BlockEndPortalFrame.EYE, true);
        end_portal_frame_east_eye = Blocks.end_portal_frame.getDefaultState().withProperty(BlockEndPortalFrame.FACING, EnumFacing.EAST)
                .withProperty(BlockEndPortalFrame.EYE, true);
        end_stone = Blocks.end_stone.getDefaultState();
        dragon_egg = Blocks.dragon_egg.getDefaultState();
        redstone_lamp = Blocks.redstone_lamp.getDefaultState();
        lit_redstone_lamp = Blocks.lit_redstone_lamp.getDefaultState();
        double_wooden_slab_oak = Blocks.double_wooden_slab.getDefaultState();
        double_wooden_slab_spruce = Blocks.double_wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.SPRUCE);
        double_wooden_slab_birch = Blocks.double_wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.BIRCH);
        double_wooden_slab_jungle = Blocks.double_wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.JUNGLE);
        double_wooden_slab_acacia = Blocks.double_wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.ACACIA);
        double_wooden_slab_darkoak = Blocks.double_wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        wooden_slab_bottom_oak = Blocks.wooden_slab.getDefaultState();
        wooden_slab_bottom_spruce = Blocks.wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.SPRUCE);
        wooden_slab_bottom_birch = Blocks.wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.BIRCH);
        wooden_slab_bottom_jungle = Blocks.wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.JUNGLE);
        wooden_slab_bottom_acacia = Blocks.wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.ACACIA);
        wooden_slab_bottom_darkoak = Blocks.wooden_slab.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        wooden_slab_top_oak = Blocks.wooden_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
        wooden_slab_top_spruce = Blocks.wooden_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.SPRUCE);
        wooden_slab_top_birch = Blocks.wooden_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.BIRCH);
        wooden_slab_top_jungle = Blocks.wooden_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.JUNGLE);
        wooden_slab_top_acacia = Blocks.wooden_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.ACACIA);
        wooden_slab_top_darkoak = Blocks.wooden_slab.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
                .withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        cocoa_south_age_0 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        cocoa_west_age_0 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        cocoa_north_age_0 = Blocks.cocoa.getDefaultState();
        cocoa_east_age_0 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        cocoa_south_age_1 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH).withProperty(BlockCocoa.AGE, 1);
        cocoa_west_age_1 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST).withProperty(BlockCocoa.AGE, 1);
        cocoa_north_age_1 = Blocks.cocoa.getDefaultState().withProperty(BlockCocoa.AGE, 1);
        cocoa_east_age_1 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST).withProperty(BlockCocoa.AGE, 1);
        cocoa_south_age_2 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH).withProperty(BlockCocoa.AGE, 2);
        cocoa_west_age_2 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST).withProperty(BlockCocoa.AGE, 2);
        cocoa_north_age_2 = Blocks.cocoa.getDefaultState().withProperty(BlockCocoa.AGE, 2);
        cocoa_east_age_2 = Blocks.cocoa.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST).withProperty(BlockCocoa.AGE, 2);
        sandstone_stairs_bottom_east = Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        sandstone_stairs_bottom_west = Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        sandstone_stairs_bottom_south = Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        sandstone_stairs_bottom_north = Blocks.sandstone_stairs.getDefaultState();
        sandstone_stairs_top_east = Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        sandstone_stairs_top_west = Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        sandstone_stairs_top_south = Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        sandstone_stairs_top_north = Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        emerald_ore = Blocks.emerald_ore.getDefaultState();
        ender_chest_north = Blocks.ender_chest.getDefaultState();
        ender_chest_south = Blocks.ender_chest.getDefaultState().withProperty(BlockEnderChest.FACING, EnumFacing.SOUTH);
        ender_chest_west = Blocks.ender_chest.getDefaultState().withProperty(BlockEnderChest.FACING, EnumFacing.WEST);
        ender_chest_east = Blocks.ender_chest.getDefaultState().withProperty(BlockEnderChest.FACING, EnumFacing.EAST);
        tripwire_hook_south = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.SOUTH);
        tripwire_hook_west = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.WEST);
        tripwire_hook_north = Blocks.tripwire_hook.getDefaultState();
        tripwire_hook_east = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.EAST);
        tripwire_hook_south_attached = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.SOUTH)
                .withProperty(BlockTripWireHook.ATTACHED, true);
        tripwire_hook_west_attached = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.WEST)
                .withProperty(BlockTripWireHook.ATTACHED, true);
        tripwire_hook_north_attached = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.ATTACHED, true);
        tripwire_hook_east_attached = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.EAST)
                .withProperty(BlockTripWireHook.ATTACHED, true);
        tripwire_hook_south_powered = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.SOUTH)
                .withProperty(BlockTripWireHook.POWERED, true);
        tripwire_hook_west_powered = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.WEST)
                .withProperty(BlockTripWireHook.POWERED, true);
        tripwire_hook_north_powered = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.POWERED, true);
        tripwire_hook_east_powered = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.EAST)
                .withProperty(BlockTripWireHook.POWERED, true);
        tripwire_hook_south_attached_powered = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.SOUTH)
                .withProperty(BlockTripWireHook.ATTACHED, true).withProperty(BlockTripWireHook.POWERED, true);
        tripwire_hook_west_attached_powered = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.WEST)
                .withProperty(BlockTripWireHook.ATTACHED, true).withProperty(BlockTripWireHook.POWERED, true);
        tripwire_hook_north_attached_powered = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.ATTACHED, true)
                .withProperty(BlockTripWireHook.POWERED, true);
        tripwire_hook_east_attached_powered = Blocks.tripwire_hook.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.EAST)
                .withProperty(BlockTripWireHook.ATTACHED, true).withProperty(BlockTripWireHook.POWERED, true);
        tripwire = Blocks.tripwire.getDefaultState();
        tripwire_powered = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.POWERED, true);
        tripwire_suspended = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.SUSPENDED, true);
        tripwire_powered_suspended = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.POWERED, true).withProperty(BlockTripWire.SUSPENDED, true);
        tripwire_attached = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true);
        tripwire_attached_powered = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true).withProperty(BlockTripWire.POWERED, true);
        tripwire_attached_suspended = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true).withProperty(BlockTripWire.SUSPENDED, true);
        tripwire_attached_powered_suspended = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true)
                .withProperty(BlockTripWire.POWERED, true).withProperty(BlockTripWire.SUSPENDED, true);
        tripwire_disarmed = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.DISARMED, true);
        tripwire_disarmed_powered = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.DISARMED, true).withProperty(BlockTripWire.POWERED, true);
        tripwire_disarmed_suspended = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.DISARMED, true).withProperty(BlockTripWire.SUSPENDED, true);
        tripwire_disarmed_powered_suspended = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.DISARMED, true)
                .withProperty(BlockTripWire.POWERED, true).withProperty(BlockTripWire.SUSPENDED, true);
        tripwire_attached_disarmed = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true).withProperty(BlockTripWire.DISARMED, true);
        tripwire_attached_disarmed_powered = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true)
                .withProperty(BlockTripWire.DISARMED, true).withProperty(BlockTripWire.POWERED, true);
        tripwire_attached_disarmed_suspended = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true)
                .withProperty(BlockTripWire.DISARMED, true).withProperty(BlockTripWire.SUSPENDED, true);
        tripwire_attached_disarmed_powered_suspended = Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true)
                .withProperty(BlockTripWire.DISARMED, true).withProperty(BlockTripWire.POWERED, true).withProperty(BlockTripWire.SUSPENDED, true);
        emerald_block = Blocks.emerald_block.getDefaultState();
        spruce_stairs_bottom_east = Blocks.spruce_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        spruce_stairs_bottom_west = Blocks.spruce_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        spruce_stairs_bottom_south = Blocks.spruce_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        spruce_stairs_bottom_north = Blocks.spruce_stairs.getDefaultState();
        spruce_stairs_top_east = Blocks.spruce_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        spruce_stairs_top_west = Blocks.spruce_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        spruce_stairs_top_south = Blocks.spruce_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        spruce_stairs_top_north = Blocks.spruce_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        birch_stairs_bottom_east = Blocks.birch_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        birch_stairs_bottom_west = Blocks.birch_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        birch_stairs_bottom_south = Blocks.birch_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        birch_stairs_bottom_north = Blocks.birch_stairs.getDefaultState();
        birch_stairs_top_east = Blocks.birch_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        birch_stairs_top_west = Blocks.birch_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        birch_stairs_top_south = Blocks.birch_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        birch_stairs_top_north = Blocks.birch_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        jungle_stairs_bottom_east = Blocks.jungle_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        jungle_stairs_bottom_west = Blocks.jungle_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        jungle_stairs_bottom_south = Blocks.jungle_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        jungle_stairs_bottom_north = Blocks.jungle_stairs.getDefaultState();
        jungle_stairs_top_east = Blocks.jungle_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        jungle_stairs_top_west = Blocks.jungle_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        jungle_stairs_top_south = Blocks.jungle_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        jungle_stairs_top_north = Blocks.jungle_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        command_block = Blocks.command_block.getDefaultState();
        command_block_triggered = Blocks.command_block.getDefaultState().withProperty(BlockCommandBlock.TRIGGERED, true);
        beacon = Blocks.beacon.getDefaultState();
        cobblestone_wall_cobblestone = Blocks.cobblestone_wall.getDefaultState();
        cobblestone_wall_mossycobblestone = Blocks.cobblestone_wall.getDefaultState().withProperty(BlockWall.VARIANT, BlockWall.EnumType.MOSSY);
        flower_pot = Blocks.flower_pot.getDefaultState();
        carrots_age_0 = Blocks.carrots.getDefaultState();
        carrots_age_1 = Blocks.carrots.getDefaultState().withProperty(BlockCrops.AGE, 1);
        carrots_age_2 = Blocks.carrots.getDefaultState().withProperty(BlockCrops.AGE, 2);
        carrots_age_3 = Blocks.carrots.getDefaultState().withProperty(BlockCrops.AGE, 3);
        carrots_age_4 = Blocks.carrots.getDefaultState().withProperty(BlockCrops.AGE, 4);
        carrots_age_5 = Blocks.carrots.getDefaultState().withProperty(BlockCrops.AGE, 5);
        carrots_age_6 = Blocks.carrots.getDefaultState().withProperty(BlockCrops.AGE, 6);
        carrots_age_7 = Blocks.carrots.getDefaultState().withProperty(BlockCrops.AGE, 7);
        potatoes_age_0 = Blocks.potatoes.getDefaultState();
        potatoes_age_1 = Blocks.potatoes.getDefaultState().withProperty(BlockCrops.AGE, 1);
        potatoes_age_2 = Blocks.potatoes.getDefaultState().withProperty(BlockCrops.AGE, 2);
        potatoes_age_3 = Blocks.potatoes.getDefaultState().withProperty(BlockCrops.AGE, 3);
        potatoes_age_4 = Blocks.potatoes.getDefaultState().withProperty(BlockCrops.AGE, 4);
        potatoes_age_5 = Blocks.potatoes.getDefaultState().withProperty(BlockCrops.AGE, 5);
        potatoes_age_6 = Blocks.potatoes.getDefaultState().withProperty(BlockCrops.AGE, 6);
        potatoes_age_7 = Blocks.potatoes.getDefaultState().withProperty(BlockCrops.AGE, 7);
        wooden_button_down = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.DOWN);
        wooden_button_east = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.EAST);
        wooden_button_west = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.WEST);
        wooden_button_south = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.SOUTH);
        wooden_button_north = Blocks.wooden_button.getDefaultState();
        wooden_button_up = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.UP);
        wooden_button_down_powered = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.DOWN)
                .withProperty(BlockButton.POWERED, true);
        wooden_button_east_powered = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.EAST)
                .withProperty(BlockButton.POWERED, true);
        wooden_button_west_powered = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.WEST)
                .withProperty(BlockButton.POWERED, true);
        wooden_button_south_powered = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.SOUTH)
                .withProperty(BlockButton.POWERED, true);
        wooden_button_north_powered = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.POWERED, true);
        wooden_button_up_powered = Blocks.wooden_button.getDefaultState().withProperty(BlockButton.FACING, EnumFacing.UP)
                .withProperty(BlockButton.POWERED, true);
        skull_down = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.DOWN);
        skull_up = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP);
        skull_north = Blocks.skull.getDefaultState();
        skull_south = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.SOUTH);
        skull_west = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.WEST);
        skull_east = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.EAST);
        skull_down_nodrop = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.DOWN).withProperty(BlockSkull.NODROP, true);
        skull_up_nodrop = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP).withProperty(BlockSkull.NODROP, true);
        skull_north_nodrop = Blocks.skull.getDefaultState().withProperty(BlockSkull.NODROP, true);
        skull_south_nodrop = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.SOUTH).withProperty(BlockSkull.NODROP, true);
        skull_west_nodrop = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.WEST).withProperty(BlockSkull.NODROP, true);
        skull_east_nodrop = Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.EAST).withProperty(BlockSkull.NODROP, true);
        anvil_south_damage_0 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.SOUTH);
        anvil_west_damage_0 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.WEST);
        anvil_north_damage_0 = Blocks.anvil.getDefaultState();
        anvil_east_damage_0 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.EAST);
        anvil_south_damage_1 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.SOUTH).withProperty(BlockAnvil.DAMAGE, 1);
        anvil_west_damage_1 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.WEST).withProperty(BlockAnvil.DAMAGE, 1);
        anvil_north_damage_1 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.DAMAGE, 1);
        anvil_east_damage_1 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.EAST).withProperty(BlockAnvil.DAMAGE, 1);
        anvil_south_damage_2 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.SOUTH).withProperty(BlockAnvil.DAMAGE, 2);
        anvil_west_damage_2 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.WEST).withProperty(BlockAnvil.DAMAGE, 2);
        anvil_north_damage_2 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.DAMAGE, 2);
        anvil_east_damage_2 = Blocks.anvil.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.EAST).withProperty(BlockAnvil.DAMAGE, 2);
        trapped_chest_north = Blocks.trapped_chest.getDefaultState();
        trapped_chest_south = Blocks.trapped_chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
        trapped_chest_west = Blocks.trapped_chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
        trapped_chest_east = Blocks.trapped_chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST);
        light_weighted_pressure_plate_power_0 = Blocks.light_weighted_pressure_plate.getDefaultState();
        light_weighted_pressure_plate_power_1 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 1);
        light_weighted_pressure_plate_power_2 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 2);
        light_weighted_pressure_plate_power_3 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 3);
        light_weighted_pressure_plate_power_4 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 4);
        light_weighted_pressure_plate_power_5 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 5);
        light_weighted_pressure_plate_power_6 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 6);
        light_weighted_pressure_plate_power_7 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 7);
        light_weighted_pressure_plate_power_8 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 8);
        light_weighted_pressure_plate_power_9 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 9);
        light_weighted_pressure_plate_power_10 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 10);
        light_weighted_pressure_plate_power_11 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 11);
        light_weighted_pressure_plate_power_12 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 12);
        light_weighted_pressure_plate_power_13 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 13);
        light_weighted_pressure_plate_power_14 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 14);
        light_weighted_pressure_plate_power_15 = Blocks.light_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 15);
        heavy_weighted_pressure_plate_power_0 = Blocks.heavy_weighted_pressure_plate.getDefaultState();
        heavy_weighted_pressure_plate_power_1 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 1);
        heavy_weighted_pressure_plate_power_2 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 2);
        heavy_weighted_pressure_plate_power_3 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 3);
        heavy_weighted_pressure_plate_power_4 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 4);
        heavy_weighted_pressure_plate_power_5 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 5);
        heavy_weighted_pressure_plate_power_6 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 6);
        heavy_weighted_pressure_plate_power_7 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 7);
        heavy_weighted_pressure_plate_power_8 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 8);
        heavy_weighted_pressure_plate_power_9 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 9);
        heavy_weighted_pressure_plate_power_10 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 10);
        heavy_weighted_pressure_plate_power_11 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 11);
        heavy_weighted_pressure_plate_power_12 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 12);
        heavy_weighted_pressure_plate_power_13 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 13);
        heavy_weighted_pressure_plate_power_14 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 14);
        heavy_weighted_pressure_plate_power_15 = Blocks.heavy_weighted_pressure_plate.getDefaultState().withProperty(BlockPressurePlateWeighted.POWER, 15);
        unpowered_comparator_compare_south = Blocks.unpowered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        unpowered_comparator_compare_west = Blocks.unpowered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        unpowered_comparator_compare_north = Blocks.unpowered_comparator.getDefaultState();
        unpowered_comparator_compare_east = Blocks.unpowered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        unpowered_comparator_subtract_south = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        unpowered_comparator_subtract_west = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        unpowered_comparator_subtract_north = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT);
        unpowered_comparator_subtract_east = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        unpowered_comparator_compare_south_powered = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH).withProperty(BlockRedstoneComparator.POWERED, true);
        unpowered_comparator_compare_west_powered = Blocks.unpowered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockRedstoneComparator.POWERED, true);
        unpowered_comparator_compare_north_powered = Blocks.unpowered_comparator.getDefaultState().withProperty(BlockRedstoneComparator.POWERED, true);
        unpowered_comparator_compare_east_powered = Blocks.unpowered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockRedstoneComparator.POWERED, true);
        unpowered_comparator_subtract_south_powered = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH).withProperty(BlockRedstoneComparator.POWERED, true);
        unpowered_comparator_subtract_west_powered = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST).withProperty(BlockRedstoneComparator.POWERED, true);
        unpowered_comparator_subtract_north_powered = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT).withProperty(BlockRedstoneComparator.POWERED, true);
        unpowered_comparator_subtract_east_powered = Blocks.unpowered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST).withProperty(BlockRedstoneComparator.POWERED, true);
        powered_comparator_compare_south = Blocks.powered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        powered_comparator_compare_west = Blocks.powered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        powered_comparator_compare_north = Blocks.powered_comparator.getDefaultState();
        powered_comparator_compare_east = Blocks.powered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        powered_comparator_subtract_south = Blocks.powered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH);
        powered_comparator_subtract_west = Blocks.powered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST);
        powered_comparator_subtract_north = Blocks.powered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT);
        powered_comparator_subtract_east = Blocks.powered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST);
        powered_comparator_compare_south_powered = Blocks.powered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH)
                .withProperty(BlockRedstoneComparator.POWERED, true);
        powered_comparator_compare_west_powered = Blocks.powered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST)
                .withProperty(BlockRedstoneComparator.POWERED, true);
        powered_comparator_compare_north_powered = Blocks.powered_comparator.getDefaultState().withProperty(BlockRedstoneComparator.POWERED, true);
        powered_comparator_compare_east_powered = Blocks.powered_comparator.getDefaultState().withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST)
                .withProperty(BlockRedstoneComparator.POWERED, true);
        powered_comparator_subtract_south_powered = Blocks.powered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.SOUTH).withProperty(BlockRedstoneComparator.POWERED, true);
        powered_comparator_subtract_west_powered = Blocks.powered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.WEST).withProperty(BlockRedstoneComparator.POWERED, true);
        powered_comparator_subtract_north_powered = Blocks.powered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT).withProperty(BlockRedstoneComparator.POWERED, true);
        powered_comparator_subtract_east_powered = Blocks.powered_comparator.getDefaultState()
                .withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT)
                .withProperty(BlockDoublePlant.field_181084_N, EnumFacing.EAST).withProperty(BlockRedstoneComparator.POWERED, true);
        daylight_detector_power_0 = Blocks.daylight_detector.getDefaultState();
        daylight_detector_power_1 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 1);
        daylight_detector_power_2 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 2);
        daylight_detector_power_3 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 3);
        daylight_detector_power_4 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 4);
        daylight_detector_power_5 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 5);
        daylight_detector_power_6 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 6);
        daylight_detector_power_7 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 7);
        daylight_detector_power_8 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 8);
        daylight_detector_power_9 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 9);
        daylight_detector_power_10 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 10);
        daylight_detector_power_11 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 11);
        daylight_detector_power_12 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 12);
        daylight_detector_power_13 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 13);
        daylight_detector_power_14 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 14);
        daylight_detector_power_15 = Blocks.daylight_detector.getDefaultState().withProperty(BlockDaylightDetector.POWER, 15);
        daylight_detector_inverted_power_0 = Blocks.daylight_detector_inverted.getDefaultState();
        daylight_detector_inverted_power_1 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 1);
        daylight_detector_inverted_power_2 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 2);
        daylight_detector_inverted_power_3 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 3);
        daylight_detector_inverted_power_4 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 4);
        daylight_detector_inverted_power_5 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 5);
        daylight_detector_inverted_power_6 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 6);
        daylight_detector_inverted_power_7 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 7);
        daylight_detector_inverted_power_8 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 8);
        daylight_detector_inverted_power_9 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 9);
        daylight_detector_inverted_power_10 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 10);
        daylight_detector_inverted_power_11 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 11);
        daylight_detector_inverted_power_12 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 12);
        daylight_detector_inverted_power_13 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 13);
        daylight_detector_inverted_power_14 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 14);
        daylight_detector_inverted_power_15 = Blocks.daylight_detector_inverted.getDefaultState().withProperty(BlockDaylightDetector.POWER, 15);
        redstone_block = Blocks.redstone_block.getDefaultState();
        quartz_ore = Blocks.quartz_ore.getDefaultState();
        hopper_down_enabled = Blocks.hopper.getDefaultState();
        hopper_north_enabled = Blocks.hopper.getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.NORTH);
        hopper_south_enabled = Blocks.hopper.getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.SOUTH);
        hopper_west_enabled = Blocks.hopper.getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.WEST);
        hopper_east_enabled = Blocks.hopper.getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.EAST);
        hopper_down = Blocks.hopper.getDefaultState().withProperty(BlockHopper.ENABLED, false);
        hopper_north = Blocks.hopper.getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.NORTH).withProperty(BlockHopper.ENABLED, false);
        hopper_south = Blocks.hopper.getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.SOUTH).withProperty(BlockHopper.ENABLED, false);
        hopper_west = Blocks.hopper.getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.WEST).withProperty(BlockHopper.ENABLED, false);
        hopper_east = Blocks.hopper.getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.EAST).withProperty(BlockHopper.ENABLED, false);
        quartz_block_default = Blocks.quartz_block.getDefaultState();
        quartz_block_chiseled = Blocks.quartz_block.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
        quartz_block_linesy = Blocks.quartz_block.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y);
        quartz_block_linesx = Blocks.quartz_block.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_X);
        quartz_block_linesz = Blocks.quartz_block.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Z);
        quartz_stairs_bottom_east = Blocks.quartz_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        quartz_stairs_bottom_west = Blocks.quartz_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        quartz_stairs_bottom_south = Blocks.quartz_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        quartz_stairs_bottom_north = Blocks.quartz_stairs.getDefaultState();
        quartz_stairs_top_east = Blocks.quartz_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        quartz_stairs_top_west = Blocks.quartz_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        quartz_stairs_top_south = Blocks.quartz_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        quartz_stairs_top_north = Blocks.quartz_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        activator_rail_northsouth = Blocks.activator_rail.getDefaultState();
        activator_rail_eastwest = Blocks.activator_rail.getDefaultState().withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST);
        activator_rail_ascendingeast = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
        activator_rail_ascendingwest = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
        activator_rail_ascendingnorth = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
        activator_rail_ascendingsouth = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
        activator_rail_northsouth_powered = Blocks.activator_rail.getDefaultState().withProperty(BlockRailPowered.POWERED, true);
        activator_rail_eastwest_powered = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST).withProperty(BlockRailPowered.POWERED, true);
        activator_rail_ascendingeast_powered = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST).withProperty(BlockRailPowered.POWERED, true);
        activator_rail_ascendingwest_powered = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST).withProperty(BlockRailPowered.POWERED, true);
        activator_rail_ascendingnorth_powered = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH).withProperty(BlockRailPowered.POWERED, true);
        activator_rail_ascendingsouth_powered = Blocks.activator_rail.getDefaultState()
                .withProperty(BlockRailPowered.SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH).withProperty(BlockRailPowered.POWERED, true);
        dropper_down = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.DOWN);
        dropper_up = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.UP);
        dropper_north = Blocks.dropper.getDefaultState();
        dropper_south = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.SOUTH);
        dropper_west = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.WEST);
        dropper_east = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.EAST);
        dropper_down_triggered = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.DOWN)
                .withProperty(BlockDispenser.TRIGGERED, true);
        dropper_up_triggered = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.UP).withProperty(BlockDispenser.TRIGGERED, true);
        dropper_north_triggered = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.TRIGGERED, true);
        dropper_south_triggered = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.SOUTH)
                .withProperty(BlockDispenser.TRIGGERED, true);
        dropper_west_triggered = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.WEST)
                .withProperty(BlockDispenser.TRIGGERED, true);
        dropper_east_triggered = Blocks.dropper.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.EAST)
                .withProperty(BlockDispenser.TRIGGERED, true);
        stained_hardened_clay_white = Blocks.stained_hardened_clay.getDefaultState();
        stained_hardened_clay_orange = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
        stained_hardened_clay_magenta = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.MAGENTA);
        stained_hardened_clay_lightblue = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE);
        stained_hardened_clay_yellow = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
        stained_hardened_clay_lime = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIME);
        stained_hardened_clay_pink = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PINK);
        stained_hardened_clay_gray = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
        stained_hardened_clay_silver = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
        stained_hardened_clay_cyan = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN);
        stained_hardened_clay_purple = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PURPLE);
        stained_hardened_clay_blue = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLUE);
        stained_hardened_clay_brown = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
        stained_hardened_clay_green = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN);
        stained_hardened_clay_red = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
        stained_hardened_clay_black = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK);
        barrier = Blocks.barrier.getDefaultState();
        iron_trapdoor_bottom_north = Blocks.iron_trapdoor.getDefaultState();
        iron_trapdoor_bottom_south = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH);
        iron_trapdoor_bottom_west = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.WEST);
        iron_trapdoor_bottom_east = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.EAST);
        iron_trapdoor_bottom_north_open = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.OPEN, true);
        iron_trapdoor_bottom_south_open = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH)
                .withProperty(BlockTrapDoor.OPEN, true);
        iron_trapdoor_bottom_west_open = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.WEST)
                .withProperty(BlockTrapDoor.OPEN, true);
        iron_trapdoor_bottom_east_open = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.FACING, EnumFacing.EAST)
                .withProperty(BlockTrapDoor.OPEN, true);
        iron_trapdoor_top_north = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP);
        iron_trapdoor_top_south = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH);
        iron_trapdoor_top_west = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.WEST);
        iron_trapdoor_top_east = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.EAST);
        iron_trapdoor_top_north_open = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.OPEN, true);
        iron_trapdoor_top_south_open = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.SOUTH).withProperty(BlockTrapDoor.OPEN, true);
        iron_trapdoor_top_west_open = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.WEST).withProperty(BlockTrapDoor.OPEN, true);
        iron_trapdoor_top_east_open = Blocks.iron_trapdoor.getDefaultState().withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
                .withProperty(BlockTrapDoor.FACING, EnumFacing.EAST).withProperty(BlockTrapDoor.OPEN, true);
        hay_block_y = Blocks.hay_block.getDefaultState();
        hay_block_x = Blocks.hay_block.getDefaultState().withProperty(BlockRotatedPillar.AXIS, EnumFacing.Axis.X);
        hay_block_z = Blocks.hay_block.getDefaultState().withProperty(BlockRotatedPillar.AXIS, EnumFacing.Axis.Z);
        carpet_white = Blocks.carpet.getDefaultState();
        carpet_orange = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.ORANGE);
        carpet_magenta = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.MAGENTA);
        carpet_lightblue = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.LIGHT_BLUE);
        carpet_yellow = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.YELLOW);
        carpet_lime = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.LIME);
        carpet_pink = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.PINK);
        carpet_gray = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.GRAY);
        carpet_silver = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.SILVER);
        carpet_cyan = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.CYAN);
        carpet_purple = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.PURPLE);
        carpet_blue = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.BLUE);
        carpet_brown = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.BROWN);
        carpet_green = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.GREEN);
        carpet_red = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.RED);
        carpet_black = Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.BLACK);
        hardened_clay = Blocks.hardened_clay.getDefaultState();
        coal_block = Blocks.coal_block.getDefaultState();
        packed_ice = Blocks.packed_ice.getDefaultState();
        acacia_stairs_bottom_east = Blocks.acacia_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        acacia_stairs_bottom_west = Blocks.acacia_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        acacia_stairs_bottom_south = Blocks.acacia_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        acacia_stairs_bottom_north = Blocks.acacia_stairs.getDefaultState();
        acacia_stairs_top_east = Blocks.acacia_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        acacia_stairs_top_west = Blocks.acacia_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        acacia_stairs_top_south = Blocks.acacia_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        acacia_stairs_top_north = Blocks.acacia_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        dark_oak_stairs_bottom_east = Blocks.dark_oak_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        dark_oak_stairs_bottom_west = Blocks.dark_oak_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        dark_oak_stairs_bottom_south = Blocks.dark_oak_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        dark_oak_stairs_bottom_north = Blocks.dark_oak_stairs.getDefaultState();
        dark_oak_stairs_top_east = Blocks.dark_oak_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        dark_oak_stairs_top_west = Blocks.dark_oak_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        dark_oak_stairs_top_south = Blocks.dark_oak_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        dark_oak_stairs_top_north = Blocks.dark_oak_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        slime_block = Blocks.slime_block.getDefaultState();
        double_plant_lower_sunflower = Blocks.double_plant.getDefaultState();
        double_plant_lower_syringa = Blocks.double_plant.getDefaultState().withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.SYRINGA);
        double_plant_lower_doublegrass = Blocks.double_plant.getDefaultState().withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.GRASS);
        double_plant_lower_doublefern = Blocks.double_plant.getDefaultState().withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.FERN);
        double_plant_lower_doublerose = Blocks.double_plant.getDefaultState().withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.ROSE);
        double_plant_lower_paeonia = Blocks.double_plant.getDefaultState().withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.PAEONIA);
        double_plant_upper_sunflower = Blocks.double_plant.getDefaultState().withProperty(BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.UPPER);
        stained_glass_white = Blocks.stained_glass.getDefaultState();
        stained_glass_orange = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.ORANGE);
        stained_glass_magenta = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.MAGENTA);
        stained_glass_lightblue = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.LIGHT_BLUE);
        stained_glass_yellow = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.YELLOW);
        stained_glass_lime = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.LIME);
        stained_glass_pink = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.PINK);
        stained_glass_gray = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.GRAY);
        stained_glass_silver = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.SILVER);
        stained_glass_cyan = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.CYAN);
        stained_glass_purple = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.PURPLE);
        stained_glass_blue = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLUE);
        stained_glass_brown = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BROWN);
        stained_glass_green = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.GREEN);
        stained_glass_red = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.RED);
        stained_glass_black = Blocks.stained_glass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLACK);
        stained_glass_pane_white = Blocks.stained_glass_pane.getDefaultState();
        stained_glass_pane_orange = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.ORANGE);
        stained_glass_pane_magenta = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.MAGENTA);
        stained_glass_pane_lightblue = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.LIGHT_BLUE);
        stained_glass_pane_yellow = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.YELLOW);
        stained_glass_pane_lime = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.LIME);
        stained_glass_pane_pink = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.PINK);
        stained_glass_pane_gray = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.GRAY);
        stained_glass_pane_silver = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.SILVER);
        stained_glass_pane_cyan = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.CYAN);
        stained_glass_pane_purple = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.PURPLE);
        stained_glass_pane_blue = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.BLUE);
        stained_glass_pane_brown = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.BROWN);
        stained_glass_pane_green = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.GREEN);
        stained_glass_pane_red = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.RED);
        stained_glass_pane_black = Blocks.stained_glass_pane.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.BLACK);
        prismarine = Blocks.prismarine.getDefaultState();
        prismarine_prismarinebricks = Blocks.prismarine.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS);
        prismarine_darkprismarine = Blocks.prismarine.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK);
        sea_lantern = Blocks.sea_lantern.getDefaultState();
        standing_banner_rotation_0 = Blocks.standing_banner.getDefaultState();
        standing_banner_rotation_1 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 1);
        standing_banner_rotation_2 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 2);
        standing_banner_rotation_3 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 3);
        standing_banner_rotation_4 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 4);
        standing_banner_rotation_5 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 5);
        standing_banner_rotation_6 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 6);
        standing_banner_rotation_7 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 7);
        standing_banner_rotation_8 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 8);
        standing_banner_rotation_9 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 9);
        standing_banner_rotation_10 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 10);
        standing_banner_rotation_11 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 11);
        standing_banner_rotation_12 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 12);
        standing_banner_rotation_13 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 13);
        standing_banner_rotation_14 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 14);
        standing_banner_rotation_15 = Blocks.standing_banner.getDefaultState().withProperty(BlockBanner.ROTATION, 15);
        wall_banner_north = Blocks.wall_banner.getDefaultState();
        wall_banner_south = Blocks.wall_banner.getDefaultState().withProperty(BlockBanner.FACING, EnumFacing.SOUTH);
        wall_banner_west = Blocks.wall_banner.getDefaultState().withProperty(BlockBanner.FACING, EnumFacing.WEST);
        wall_banner_east = Blocks.wall_banner.getDefaultState().withProperty(BlockBanner.FACING, EnumFacing.EAST);
        red_sandstone_redsandstone = Blocks.red_sandstone.getDefaultState();
        red_sandstone_chiseledredsandstone = Blocks.red_sandstone.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.CHISELED);
        red_sandstone_smoothredsandstone = Blocks.red_sandstone.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.SMOOTH);
        red_sandstone_stairs_bottom_east = Blocks.red_sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        red_sandstone_stairs_bottom_west = Blocks.red_sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        red_sandstone_stairs_bottom_south = Blocks.red_sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        red_sandstone_stairs_bottom_north = Blocks.red_sandstone_stairs.getDefaultState();
        red_sandstone_stairs_top_east = Blocks.red_sandstone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.EAST);
        red_sandstone_stairs_top_west = Blocks.red_sandstone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.WEST);
        red_sandstone_stairs_top_south = Blocks.red_sandstone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP)
                .withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
        red_sandstone_stairs_top_north = Blocks.red_sandstone_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        double_stone_slab2 = Blocks.double_stone_slab2.getDefaultState();
        double_stone_slab2_seamless = Blocks.double_stone_slab2.getDefaultState().withProperty(BlockStoneSlabNew.SEAMLESS, true);
        stone_slab2_bottom = Blocks.stone_slab2.getDefaultState();
        stone_slab2_top = Blocks.stone_slab2.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
    }
}