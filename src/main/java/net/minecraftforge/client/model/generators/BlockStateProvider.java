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

package net.minecraftforge.client.model.generators;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * Data provider for blockstate files. Extends {@link BlockModelProvider} so that
 * blockstates and their referenced models can be provided in tandem.
 */
public abstract class BlockStateProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    @VisibleForTesting
    protected final Map<Block, IGeneratedBlockstate> registeredBlocks = new LinkedHashMap<>();

    private final DataGenerator generator;
    private final String modid;
    private final BlockModelProvider blockModels;
    private final ItemModelProvider itemModels;

    public BlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        this.generator = gen;
        this.modid = modid;
        this.blockModels = new BlockModelProvider(gen, modid, exFileHelper) {
            @Override protected void registerModels() {}
        };
        this.itemModels = new ItemModelProvider(gen, modid, this.blockModels.existingFileHelper) {
            @Override protected void registerModels() {}
        };
    }

    @Override
    public void run(HashCache cache) throws IOException {
        models().clear();
        itemModels().clear();
        registeredBlocks.clear();
        registerStatesAndModels();
        models().generateAll(cache);
        itemModels().generateAll(cache);
        for (Map.Entry<Block, IGeneratedBlockstate> entry : registeredBlocks.entrySet()) {
            saveBlockState(cache, entry.getValue().toJson(), entry.getKey());
        }
    }

    protected abstract void registerStatesAndModels();

    public VariantBlockStateBuilder getVariantBuilder(Block b) {
        if (registeredBlocks.containsKey(b)) {
            IGeneratedBlockstate old = registeredBlocks.get(b);
            Preconditions.checkState(old instanceof VariantBlockStateBuilder);
            return (VariantBlockStateBuilder) old;
        } else {
            VariantBlockStateBuilder ret = new VariantBlockStateBuilder(b);
            registeredBlocks.put(b, ret);
            return ret;
        }
    }

    public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
        if (registeredBlocks.containsKey(b)) {
            IGeneratedBlockstate old = registeredBlocks.get(b);
            Preconditions.checkState(old instanceof MultiPartBlockStateBuilder);
            return (MultiPartBlockStateBuilder) old;
        } else {
            MultiPartBlockStateBuilder ret = new MultiPartBlockStateBuilder(b);
            registeredBlocks.put(b, ret);
            return ret;
        }
    }

    public BlockModelProvider models() {
        return blockModels;
    }

    public ItemModelProvider itemModels() {
        return itemModels;
    }

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(modid, name);
    }

    public ResourceLocation mcLoc(String name) {
        return new ResourceLocation(name);
    }

    private String name(Block block) {
        return block.getRegistryName().getPath();
    }

    public ResourceLocation blockTexture(Block block) {
        ResourceLocation name = block.getRegistryName();
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }

    public ModelFile cubeAll(Block block) {
        return models().cubeAll(name(block), blockTexture(block));
    }

    public void simpleBlock(Block block) {
        simpleBlock(block, cubeAll(block));
    }

    public void simpleBlock(Block block, Function<ModelFile, ConfiguredModel[]> expander) {
        simpleBlock(block, expander.apply(cubeAll(block)));
    }

    public void simpleBlock(Block block, ModelFile model) {
        simpleBlock(block, new ConfiguredModel(model));
    }

    public void simpleBlockItem(Block block, ModelFile model) {
        itemModels().getBuilder(block.getRegistryName().getPath()).parent(model);
    }

    public void simpleBlock(Block block, ConfiguredModel... models) {
        getVariantBuilder(block)
            .partialState().setModels(models);
    }

    public void axisBlock(RotatedPillarBlock block) {
        axisBlock(block, blockTexture(block));
    }

    public void logBlock(RotatedPillarBlock block) {
        axisBlock(block, blockTexture(block), extend(blockTexture(block), "_top"));
    }

    public void axisBlock(RotatedPillarBlock block, ResourceLocation baseName) {
        axisBlock(block, extend(baseName, "_side"), extend(baseName, "_end"));
    }

    public void axisBlock(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        axisBlock(block, models().cubeColumn(name(block), side, end), models().cubeColumnHorizontal(name(block) + "_horizontal", side, end));
    }

    public void axisBlock(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
        getVariantBuilder(block)
            .partialState().with(RotatedPillarBlock.AXIS, Axis.Y)
                .modelForState().modelFile(vertical).addModel()
            .partialState().with(RotatedPillarBlock.AXIS, Axis.Z)
                .modelForState().modelFile(horizontal).rotationX(90).addModel()
            .partialState().with(RotatedPillarBlock.AXIS, Axis.X)
                .modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
    }

    private static final int DEFAULT_ANGLE_OFFSET = 180;

    public void horizontalBlock(Block block, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
        horizontalBlock(block, models().orientable(name(block), side, front, top));
    }

    public void horizontalBlock(Block block, ModelFile model) {
        horizontalBlock(block, model, DEFAULT_ANGLE_OFFSET);
    }

    public void horizontalBlock(Block block, ModelFile model, int angleOffset) {
        horizontalBlock(block, $ -> model, angleOffset);
    }

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        horizontalBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
    }

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
            .forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) % 360)
                    .build()
            );
    }

    public void horizontalFaceBlock(Block block, ModelFile model) {
        horizontalFaceBlock(block, model, DEFAULT_ANGLE_OFFSET);
    }

    public void horizontalFaceBlock(Block block, ModelFile model, int angleOffset) {
        horizontalFaceBlock(block, $ -> model, angleOffset);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        horizontalFaceBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
            .forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .rotationX(state.getValue(BlockStateProperties.ATTACH_FACE).ordinal() * 90)
                    .rotationY((((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) + (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? 180 : 0)) % 360)
                    .build()
            );
    }

    public void directionalBlock(Block block, ModelFile model) {
        directionalBlock(block, model, DEFAULT_ANGLE_OFFSET);
    }

    public void directionalBlock(Block block, ModelFile model, int angleOffset) {
        directionalBlock(block, $ -> model, angleOffset);
    }

    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        directionalBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
    }

    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
            .forAllStates(state -> {
                Direction dir = state.getValue(BlockStateProperties.FACING);
                return ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                    .build();
            });
    }

    public void stairsBlock(StairBlock block, ResourceLocation texture) {
        stairsBlock(block, texture, texture, texture);
    }

    public void stairsBlock(StairBlock block, String name, ResourceLocation texture) {
        stairsBlock(block, name, texture, texture, texture);
    }

    public void stairsBlock(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        stairsBlockInternal(block, block.getRegistryName().toString(), side, bottom, top);
    }

    public void stairsBlock(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        stairsBlockInternal(block, name + "_stairs", side, bottom, top);
    }

    private void stairsBlockInternal(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile stairs = models().stairs(baseName, side, bottom, top);
        ModelFile stairsInner = models().stairsInner(baseName + "_inner", side, bottom, top);
        ModelFile stairsOuter = models().stairsOuter(baseName + "_outer", side, bottom, top);
        stairsBlock(block, stairs, stairsInner, stairsOuter);
    }

    public void stairsBlock(StairBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
        getVariantBuilder(block)
            .forAllStatesExcept(state -> {
               Direction facing = state.getValue(StairBlock.FACING);
               Half half = state.getValue(StairBlock.HALF);
               StairsShape shape = state.getValue(StairBlock.SHAPE);
               int yRot = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
               if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                   yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
               }
               if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                   yRot += 90; // Top stairs are rotated 90 degrees clockwise
               }
               yRot %= 360;
               boolean uvlock = yRot != 0 || half == Half.TOP; // Don't set uvlock for states that have no rotation
               return ConfiguredModel.builder()
                       .modelFile(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
                       .rotationX(half == Half.BOTTOM ? 0 : 180)
                       .rotationY(yRot)
                       .uvLock(uvlock)
                       .build();
            }, StairBlock.WATERLOGGED);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
        slabBlock(block, doubleslab, texture, texture, texture);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        slabBlock(block, models().slab(name(block), side, bottom, top), models().slabTop(name(block) + "_top", side, bottom, top), models().getExistingFile(doubleslab));
    }

    public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
        getVariantBuilder(block)
            .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(bottom))
            .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(top))
            .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleslab));
    }

    public void fourWayBlock(CrossCollisionBlock block, ModelFile post, ModelFile side) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(post).addModel().end();
        fourWayMultipart(builder, side);
    }

    public void fourWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
        PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
            Direction dir = e.getKey();
            if (dir.getAxis().isHorizontal()) {
                builder.part().modelFile(side).rotationY((((int) dir.toYRot()) + 180) % 360).uvLock(true).addModel()
                    .condition(e.getValue(), true);
            }
        });
    }

    public void fenceBlock(FenceBlock block, ResourceLocation texture) {
        String baseName = block.getRegistryName().toString();
        fourWayBlock(block, models().fencePost(baseName + "_post", texture), models().fenceSide(baseName + "_side", texture));
    }

    public void fenceBlock(FenceBlock block, String name, ResourceLocation texture) {
        fourWayBlock(block, models().fencePost(name + "_fence_post", texture), models().fenceSide(name + "_fence_side", texture));
    }

    public void fenceGateBlock(FenceGateBlock block, ResourceLocation texture) {
        fenceGateBlockInternal(block, block.getRegistryName().toString(), texture);
    }

    public void fenceGateBlock(FenceGateBlock block, String name, ResourceLocation texture) {
        fenceGateBlockInternal(block, name + "_fence_gate", texture);
    }

    private void fenceGateBlockInternal(FenceGateBlock block, String baseName, ResourceLocation texture) {
        ModelFile gate = models().fenceGate(baseName, texture);
        ModelFile gateOpen = models().fenceGateOpen(baseName + "_open", texture);
        ModelFile gateWall = models().fenceGateWall(baseName + "_wall", texture);
        ModelFile gateWallOpen = models().fenceGateWallOpen(baseName + "_wall_open", texture);
        fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
    }

    public void fenceGateBlock(FenceGateBlock block, ModelFile gate, ModelFile gateOpen, ModelFile gateWall, ModelFile gateWallOpen) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            ModelFile model = gate;
            if (state.getValue(FenceGateBlock.IN_WALL)) {
                model = gateWall;
            }
            if (state.getValue(FenceGateBlock.OPEN)) {
                model = model == gateWall ? gateWallOpen : gateOpen;
            }
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) state.getValue(FenceGateBlock.FACING).toYRot())
                    .uvLock(true)
                    .build();
        }, FenceGateBlock.POWERED);
    }

    public void wallBlock(WallBlock block, ResourceLocation texture) {
        wallBlockInternal(block, block.getRegistryName().toString(), texture);
    }

    public void wallBlock(WallBlock block, String name, ResourceLocation texture) {
        wallBlockInternal(block, name + "_wall", texture);
    }

    private void wallBlockInternal(WallBlock block, String baseName, ResourceLocation texture) {
        wallBlock(block, models().wallPost(baseName + "_post", texture), models().wallSide(baseName + "_side", texture), models().wallSideTall(baseName + "_side_tall", texture));
    }
    
    public static final ImmutableMap<Direction, Property<WallSide>> WALL_PROPS = ImmutableMap.<Direction, Property<WallSide>>builder()
    		.put(Direction.EAST,  BlockStateProperties.EAST_WALL)
    		.put(Direction.NORTH, BlockStateProperties.NORTH_WALL)
    		.put(Direction.SOUTH, BlockStateProperties.SOUTH_WALL)
    		.put(Direction.WEST,  BlockStateProperties.WEST_WALL)
    		.build();

    public void wallBlock(WallBlock block, ModelFile post, ModelFile side, ModelFile sideTall) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(post).addModel()
                    .condition(WallBlock.UP, true).end();
        WALL_PROPS.entrySet().stream()
        	.filter(e -> e.getKey().getAxis().isHorizontal())
        	.forEach(e -> {
        		wallSidePart(builder, side, e, WallSide.LOW);
        		wallSidePart(builder, sideTall, e, WallSide.TALL);
        	});
    }
    
    private void wallSidePart(MultiPartBlockStateBuilder builder, ModelFile model, Map.Entry<Direction, Property<WallSide>> entry, WallSide height) {
        builder.part()
        	.modelFile(model)
        		.rotationY((((int) entry.getKey().toYRot()) + 180) % 360)
        		.uvLock(true)
        		.addModel()
    		.condition(entry.getValue(), height);
    }

    public void paneBlock(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge) {
        paneBlockInternal(block, block.getRegistryName().toString(), pane, edge);
    }

    public void paneBlock(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge) {
        paneBlockInternal(block, name + "_pane", pane, edge);
    }

    private void paneBlockInternal(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge) {
        ModelFile post = models().panePost(baseName + "_post", pane, edge);
        ModelFile side = models().paneSide(baseName + "_side", pane, edge);
        ModelFile sideAlt = models().paneSideAlt(baseName + "_side_alt", pane, edge);
        ModelFile noSide = models().paneNoSide(baseName + "_noside", pane);
        ModelFile noSideAlt = models().paneNoSideAlt(baseName + "_noside_alt", pane);
        paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
    }

    public void paneBlock(IronBarsBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(post).addModel().end();
        PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
            Direction dir = e.getKey();
            if (dir.getAxis().isHorizontal()) {
                boolean alt = dir == Direction.SOUTH;
                builder.part().modelFile(alt || dir == Direction.WEST ? sideAlt : side).rotationY(dir.getAxis() == Axis.X ? 90 : 0).addModel()
                    .condition(e.getValue(), true).end()
                .part().modelFile(alt || dir == Direction.EAST ? noSideAlt : noSide).rotationY(dir == Direction.WEST ? 270 : dir == Direction.SOUTH ? 90 : 0).addModel()
                    .condition(e.getValue(), false);
            }
        });
    }

    public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        doorBlockInternal(block, block.getRegistryName().toString(), bottom, top);
    }

    public void doorBlock(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top) {
        doorBlockInternal(block, name + "_door", bottom, top);
    }

    private void doorBlockInternal(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top) {
        ModelFile bottomLeft = models().doorBottomLeft(baseName + "_bottom", bottom, top);
        ModelFile bottomRight = models().doorBottomRight(baseName + "_bottom_hinge", bottom, top);
        ModelFile topLeft = models().doorTopLeft(baseName + "_top", bottom, top);
        ModelFile topRight = models().doorTopRight(baseName + "_top_hinge", bottom, top);
        doorBlock(block, bottomLeft, bottomRight, topLeft, topRight);
    }

    public void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomRight, ModelFile topLeft, ModelFile topRight) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            int yRot = ((int) state.getValue(DoorBlock.FACING).toYRot()) + 90;
            boolean rh = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
            boolean open = state.getValue(DoorBlock.OPEN);
            boolean right = rh ^ open;
            if (open) {
                yRot += 90;
            }
            if (rh && open) {
                yRot += 180;
            }
            yRot %= 360;
            return ConfiguredModel.builder().modelFile(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? (right ? bottomRight : bottomLeft) : (right ? topRight : topLeft))
                    .rotationY(yRot)
                    .build();
        }, DoorBlock.POWERED);
    }

    public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
        trapdoorBlockInternal(block, block.getRegistryName().toString(), texture, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable) {
        trapdoorBlockInternal(block, name + "_trapdoor", texture, orientable);
    }

    private void trapdoorBlockInternal(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable) {
        ModelFile bottom = orientable ? models().trapdoorOrientableBottom(baseName + "_bottom", texture) : models().trapdoorBottom(baseName + "_bottom", texture);
        ModelFile top = orientable ? models().trapdoorOrientableTop(baseName + "_top", texture) : models().trapdoorTop(baseName + "_top", texture);
        ModelFile open = orientable ? models().trapdoorOrientableOpen(baseName + "_open", texture) : models().trapdoorOpen(baseName + "_open", texture);
        trapdoorBlock(block, bottom, top, open, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, ModelFile bottom, ModelFile top, ModelFile open, boolean orientable) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            int xRot = 0;
            int yRot = ((int) state.getValue(TrapDoorBlock.FACING).toYRot()) + 180;
            boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
            if (orientable && isOpen && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
                xRot += 180;
                yRot += 180;
            }
            if (!orientable && !isOpen) {
                yRot = 0;
            }
            yRot %= 360;
            return ConfiguredModel.builder().modelFile(isOpen ? open : state.getValue(TrapDoorBlock.HALF) == Half.TOP ? top : bottom)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
    }

    private void saveBlockState(HashCache cache, JsonObject stateJson, Block owner) {
        ResourceLocation blockName = Preconditions.checkNotNull(owner.getRegistryName());
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "assets/" + blockName.getNamespace() + "/blockstates/" + blockName.getPath() + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            DataProvider.save(GSON, cache, stateJson, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save blockstate to {}", outputPath, e);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "Block States: " + modid;
    }

    public static class ConfiguredModelList {
        private final List<ConfiguredModel> models;

        private ConfiguredModelList(List<ConfiguredModel> models) {
            Preconditions.checkArgument(!models.isEmpty());
            this.models = models;
        }

        public ConfiguredModelList(ConfiguredModel model) {
            this(ImmutableList.of(model));
        }

        public ConfiguredModelList(ConfiguredModel... models) {
            this(Arrays.asList(models));
        }

        public JsonElement toJSON() {
            if (models.size()==1) {
                return models.get(0).toJSON(false);
            } else {
                JsonArray ret = new JsonArray();
                for (ConfiguredModel m:models) {
                    ret.add(m.toJSON(true));
                }
                return ret;
            }
        }

        public ConfiguredModelList append(ConfiguredModel... models) {
            return new ConfiguredModelList(ImmutableList.<ConfiguredModel>builder().addAll(this.models).add(models).build());
        }
    }
}
