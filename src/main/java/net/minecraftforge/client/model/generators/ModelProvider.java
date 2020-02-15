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

package net.minecraftforge.client.model.generators;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

public abstract class ModelProvider<T extends ModelBuilder<T>> implements IDataProvider {

    private class ExistingFileHelperIncludingGenerated extends ExistingFileHelper {

        private final ExistingFileHelper delegate;

        public ExistingFileHelperIncludingGenerated(ExistingFileHelper delegate) {
            super(Collections.emptyList(), true);
            this.delegate = delegate;
        }

        @Override
        public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) {
            if (generatedModels.containsKey(loc)) {
                return true;
            }
            return delegate.exists(loc, type, pathSuffix, pathPrefix);
        }
    }

    public static final String BLOCK_FOLDER = "block";
    public static final String ITEM_FOLDER = "item";

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final String modid;
    protected final String folder;
    protected final Function<ResourceLocation, T> factory;
    @VisibleForTesting
    protected final Map<ResourceLocation, T> generatedModels = new HashMap<>();
    protected final ExistingFileHelper existingFileHelper;

    protected DirectoryCache cache;

    protected abstract void registerModels();

    public ModelProvider(DataGenerator generator, String modid, String folder, Function<ResourceLocation, T> factory, ExistingFileHelper existingFileHelper) {
        Preconditions.checkNotNull(generator);
        this.generator = generator;
        Preconditions.checkNotNull(modid);
        this.modid = modid;
        Preconditions.checkNotNull(folder);
        this.folder = folder;
        Preconditions.checkNotNull(factory);
        this.factory = factory;
        Preconditions.checkNotNull(existingFileHelper);
        this.existingFileHelper = new ExistingFileHelperIncludingGenerated(existingFileHelper);
    }

    public ModelProvider(DataGenerator generator, String modid, String folder, BiFunction<ResourceLocation, ExistingFileHelper, T> builderFromModId, ExistingFileHelper existingFileHelper) {
        this(generator, modid, folder, loc->builderFromModId.apply(loc, existingFileHelper), existingFileHelper);
    }

    protected T getBuilder(String path) {
        Preconditions.checkNotNull(path, "Path must not be null");
        ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
        return generatedModels.computeIfAbsent(outputLoc, factory);
    }

    private ResourceLocation extendWithFolder(ResourceLocation rl) {
        if (rl.getPath().contains("/")) {
            return rl;
        }
        return new ResourceLocation(rl.getNamespace(), folder + "/" + rl.getPath());
    }

    protected ResourceLocation modLoc(String name) {
        return new ResourceLocation(modid, name);
    }

    protected ResourceLocation mcLoc(String name) {
        return new ResourceLocation(name);
    }

    protected T withExistingParent(String name, String parent) {
        return withExistingParent(name, mcLoc(parent));
    }

    protected T withExistingParent(String name, ResourceLocation parent) {
        return getBuilder(name).parent(getExistingFile(parent));
    }

    protected T cube(String name, ResourceLocation down, ResourceLocation up, ResourceLocation north, ResourceLocation south, ResourceLocation east, ResourceLocation west) {
        return withExistingParent(name, "cube")
                .texture("down", down)
                .texture("up", up)
                .texture("north", north)
                .texture("south", south)
                .texture("east", east)
                .texture("west", west);
    }

    private T singleTexture(String name, String parent, ResourceLocation texture) {
        return singleTexture(name, mcLoc(parent), texture);
    }

    protected T singleTexture(String name, ResourceLocation parent, ResourceLocation texture) {
        return singleTexture(name, parent, "texture", texture);
    }

    private T singleTexture(String name, String parent, String textureKey, ResourceLocation texture) {
        return singleTexture(name, mcLoc(parent), textureKey, texture);
    }

    protected T singleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
        return withExistingParent(name, parent)
                .texture(textureKey, texture);
    }

    protected T cubeAll(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/cube_all", "all", texture);
    }

    protected T cubeTop(String name, ResourceLocation side, ResourceLocation top) {
        return withExistingParent(name, BLOCK_FOLDER + "/cube_top")
                .texture("side", side)
                .texture("top", top);
    }

    private T sideBottomTop(String name, String parent, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, parent)
                .texture("side", side)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    protected T cubeBottomTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/cube_bottom_top", side, bottom, top);
    }

    protected T cubeColumn(String name, ResourceLocation side, ResourceLocation end) {
        return withExistingParent(name, BLOCK_FOLDER + "/cube_column")
                .texture("side", side)
                .texture("end", end);
    }

    protected T orientableVertical(String name, ResourceLocation side, ResourceLocation front) {
        return withExistingParent(name, BLOCK_FOLDER + "/orientable_vertical")
                .texture("side", side)
                .texture("front", front);
    }

    protected T orientableWithBottom(String name, ResourceLocation side, ResourceLocation front, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, BLOCK_FOLDER + "/orientable_with_bottom")
                .texture("side", side)
                .texture("front", front)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    protected T orientable(String name, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
        return withExistingParent(name, BLOCK_FOLDER + "/orientable")
                .texture("side", side)
                .texture("front", front)
                .texture("top", top);
    }

    protected T crop(String name, ResourceLocation crop) {
        return singleTexture(name, BLOCK_FOLDER + "/crop", "crop", crop);
    }

    protected T cross(String name, ResourceLocation cross) {
        return singleTexture(name, BLOCK_FOLDER + "/cross", "cross", cross);
    }

    protected T stairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/stairs", side, bottom, top);
    }

    protected T stairsOuter(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/outer_stairs", side, bottom, top);
    }

    protected T stairsInner(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/inner_stairs", side, bottom, top);
    }

    protected T slab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/slab", side, bottom, top);
    }

    protected T slabTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/slab_top", side, bottom, top);
    }

    protected T fencePost(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/fence_post", texture);
    }

    protected T fenceSide(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/fence_side", texture);
    }

    protected T fenceInventory(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/fence_inventory", texture);
    }

    protected T fenceGate(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate", texture);
    }

    protected T fenceGateOpen(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_open", texture);
    }

    protected T fenceGateWall(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_wall", texture);
    }

    protected T fenceGateWallOpen(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_wall_open", texture);
    }

    protected T wallPost(String name, ResourceLocation wall) {
        return singleTexture(name, BLOCK_FOLDER + "/template_wall_post", "wall", wall);
    }

    protected T wallSide(String name, ResourceLocation wall) {
        return singleTexture(name, BLOCK_FOLDER + "/template_wall_side", "wall", wall);
    }

    protected T wallInventory(String name, ResourceLocation wall) {
        return singleTexture(name, BLOCK_FOLDER + "/wall_inventory", "wall", wall);
    }

    private T pane(String name, String parent, ResourceLocation pane, ResourceLocation edge) {
        return withExistingParent(name, BLOCK_FOLDER + "/" + parent)
                .texture("pane", pane)
                .texture("edge", edge);
    }

    protected T panePost(String name, ResourceLocation pane, ResourceLocation edge) {
        return pane(name, "template_glass_pane_post", pane, edge);
    }

    protected T paneSide(String name, ResourceLocation pane, ResourceLocation edge) {
        return pane(name, "template_glass_pane_side", pane, edge);
    }

    protected T paneSideAlt(String name, ResourceLocation pane, ResourceLocation edge) {
        return pane(name, "template_glass_pane_side_alt", pane, edge);
    }

    protected T paneNoSide(String name, ResourceLocation pane) {
        return singleTexture(name, BLOCK_FOLDER + "/template_glass_pane_noside", "pane", pane);
    }

    protected T paneNoSideAlt(String name, ResourceLocation pane) {
        return singleTexture(name, BLOCK_FOLDER + "/template_glass_pane_noside_alt", "pane", pane);
    }

    private T door(String name, String model, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, BLOCK_FOLDER + "/" + model)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    protected T doorBottomLeft(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "door_bottom", bottom, top);
    }

    protected T doorBottomRight(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "door_bottom_rh", bottom, top);
    }

    protected T doorTopLeft(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "door_top", bottom, top);
    }

    protected T doorTopRight(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "door_top_rh", bottom, top);
    }

    protected T trapdoorBottom(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_bottom", texture);
    }

    protected T trapdoorTop(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_top", texture);
    }

    protected T trapdoorOpen(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_open", texture);
    }

    protected T trapdoorOrientableBottom(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_bottom", texture);
    }

    protected T trapdoorOrientableTop(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_top", texture);
    }

    protected T trapdoorOrientableOpen(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_open", texture);
    }

    protected T torch(String name, ResourceLocation torch) {
        return singleTexture(name, BLOCK_FOLDER + "/template_torch", "torch", torch);
    }

    protected T torchWall(String name, ResourceLocation torch) {
        return singleTexture(name, BLOCK_FOLDER + "/torch_wall", "torch", torch);
    }

    protected T carpet(String name, ResourceLocation wool) {
        return singleTexture(name, BLOCK_FOLDER + "/carpet", "wool", wool);
    }

    protected ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
        ModelFile.ExistingModelFile ret = new ModelFile.ExistingModelFile(extendWithFolder(path), existingFileHelper);
        ret.assertExistence();
        return ret;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        this.cache = cache;
        generatedModels.clear();
        registerModels();
        generateAll();
        this.cache = null;
    }

    private void generateAll() {
        for (T model : generatedModels.values()) {
            Path target = getPath(model);
            try {
                IDataProvider.save(GSON, cache, model.toJson(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path getPath(T model) {
        ResourceLocation loc = model.getLocation();
        return generator.getOutputFolder().resolve("assets/" + loc.getNamespace() + "/models/" + loc.getPath() + ".json");
    }
}
