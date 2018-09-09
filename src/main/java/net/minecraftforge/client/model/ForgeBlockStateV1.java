/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.client.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BlockStateLoader.Marker;
import net.minecraftforge.client.model.BlockStateLoader.SubModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ForgeBlockStateV1 extends Marker
{
    private static final Logger LOGGER = LogManager.getLogger();
    ForgeBlockStateV1.Variant defaults;
    Multimap<String, ForgeBlockStateV1.Variant> variants = LinkedHashMultimap.create();

    public static class Deserializer implements JsonDeserializer<ForgeBlockStateV1>
    {
        static ForgeBlockStateV1.Deserializer INSTANCE = new Deserializer();
        @Override
        public ForgeBlockStateV1 deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject json = element.getAsJsonObject();
            ForgeBlockStateV1 ret = new ForgeBlockStateV1();
            ret.forge_marker = JsonUtils.getInt(json, "forge_marker");

            if (json.has("defaults"))   // Load defaults Variant.
            {
                ret.defaults = context.deserialize(json.get("defaults"), ForgeBlockStateV1.Variant.class);

                if (ret.defaults.simpleSubmodels.size() > 0)
                    throw new RuntimeException("\"defaults\" variant cannot contain a simple \"submodel\" definition.");
            }

            Map<String, Map<String, ForgeBlockStateV1.Variant>> condensed = Maps.newLinkedHashMap();    // map(property name -> map(property value -> variant))
            Multimap<String, ForgeBlockStateV1.Variant> specified = LinkedHashMultimap.create();    // Multimap containing all the states specified with "property=value".

            for (Entry<String, JsonElement> e : JsonUtils.getJsonObject(json, "variants").entrySet())
            {
                if (e.getValue().isJsonArray())
                {
                    // array of fully-defined variants
                    for (JsonElement a : e.getValue().getAsJsonArray())
                    {
                        Variant.Deserializer.INSTANCE.simpleSubmodelKey = e.getKey();
                        specified.put(e.getKey(), context.deserialize(a, Variant.class));
                    }
                }
                else
                {
                    JsonObject obj = e.getValue().getAsJsonObject();
                    if(obj.entrySet().iterator().next().getValue().isJsonObject())
                    {
                        // first value is a json object, so we know it's not a fully-defined variant
                        Map<String, ForgeBlockStateV1.Variant> subs = Maps.newLinkedHashMap();
                        condensed.put(e.getKey(), subs);
                        for (Entry<String, JsonElement> se : e.getValue().getAsJsonObject().entrySet())
                        {
                            Variant.Deserializer.INSTANCE.simpleSubmodelKey = e.getKey() + "=" + se.getKey();
                            subs.put(se.getKey(), context.deserialize(se.getValue(), Variant.class));
                        }
                    }
                    else
                    {
                        // fully-defined variant
                        Variant.Deserializer.INSTANCE.simpleSubmodelKey = e.getKey();
                        specified.put(e.getKey(), context.deserialize(e.getValue(), Variant.class));
                    }
                }
            }

            Multimap<String, ForgeBlockStateV1.Variant> permutations = getPermutations(condensed);    // Get permutations of Forge-style states.

            for (Entry<String, Collection<ForgeBlockStateV1.Variant>> e : specified.asMap().entrySet())
            {   // Make fully-specified variants override Forge variant permutations, inheriting the permutations' values.
                Collection<ForgeBlockStateV1.Variant> baseVars = permutations.get(e.getKey());
                List<ForgeBlockStateV1.Variant> addVars = Lists.newArrayList();

                for (ForgeBlockStateV1.Variant specVar : e.getValue())
                {
                    if (!baseVars.isEmpty())
                    {
                        for (ForgeBlockStateV1.Variant baseVar : baseVars)
                            addVars.add(new Variant(specVar).sync(baseVar));
                    }
                    else
                        addVars.add(specVar);
                }

                baseVars.clear();
                baseVars.addAll(addVars);
            }

            for (Entry<String, ForgeBlockStateV1.Variant> e : permutations.entries()) // Create the output map(state -> Variant).
            {
                ForgeBlockStateV1.Variant v = e.getValue();

                if (ret.defaults != null)
                {
                    v.sync(ret.defaults);   // Sync defaults into all permutation variants

                    for (Entry<String, Object> partKey : v.simpleSubmodels.entrySet())
                    {   // Sync variant values (including defaults) into simple submodel declarations.
                        if (partKey.getValue() == null)
                            continue;

                        if (!v.submodels.containsKey(partKey.getKey()))
                            throw new RuntimeException("This should never happen! Simple submodel is not contained in the submodel map!");
                        List<ForgeBlockStateV1.Variant> partList = v.submodels.get(partKey.getKey());
                        if (partList.size() > 1)
                            throw new RuntimeException("This should never happen! Simple submodel has multiple variants!");

                        ForgeBlockStateV1.Variant part = partList.get(0);
                        // Must keep old rotation for the part, because the base variant's rotation is applied to the parts already.
                        Optional<IModelState> state = part.state;
                        part.sync(v);
                        part.simpleSubmodels.clear();
                        part.state = state;
                    }
                }

                v.submodels.values().removeIf(Objects::isNull);

                if (v.textures != null)
                {
                    for (Entry<String, String> tex : v.textures.entrySet())
                    {
                        if (tex.getValue() != null && tex.getValue().charAt(0) == '#')
                        {
                            String value = v.textures.get(tex.getValue().substring(1));
                            if (value == null)
                            {
                                LOGGER.fatal("Could not resolve texture name \"{}\" for permutation \"{}\"", tex.getValue(), e.getKey());
                                for (Entry<String, String> t: v.textures.entrySet())
                                    LOGGER.fatal("{}={}", t.getKey(), t.getValue());
                                throw new JsonParseException("Could not resolve texture name \"" + tex.getValue() + "\" for permutation \"" + e.getKey() + "\"");
                            }
                            v.textures.put(tex.getKey(), value);
                        }
                    }

                    for (List<ForgeBlockStateV1.Variant> part : v.submodels.values())    // Sync base variant's textures (including defaults) into all submodels.
                    {
                        for (ForgeBlockStateV1.Variant partVar : part)
                        {
                            for (Entry<String, String> texEntry : v.textures.entrySet())
                            {
                                if (!partVar.textures.containsKey(texEntry.getKey()))
                                    partVar.textures.put(texEntry.getKey(), texEntry.getValue());
                            }
                        }
                    }
                }

                if (!v.submodels.isEmpty())
                    ret.variants.putAll(e.getKey(), getSubmodelPermutations(v, v.submodels));   // Do permutations of submodel variants.
                else
                    ret.variants.put(e.getKey(), v);
            }

            return ret;
        }

        private Multimap<String, ForgeBlockStateV1.Variant> getPermutations(List<String> sorted, Map<String, Map<String, ForgeBlockStateV1.Variant>> base, int depth, String prefix, Multimap<String, ForgeBlockStateV1.Variant> ret, @Nullable ForgeBlockStateV1.Variant parent)
        {
            if (depth == sorted.size())
            {
                if(parent != null)
                    ret.put(prefix, parent);
                return ret;
            }

            String name = sorted.get(depth);
            for (Entry<String, ForgeBlockStateV1.Variant> e : base.get(name).entrySet())
            {
                ForgeBlockStateV1.Variant newHead = parent == null ? new Variant(e.getValue()) : new Variant(parent).sync(e.getValue());

                getPermutations(sorted, base, depth + 1, prefix + (depth == 0 ? "" : ",") + name + "=" + e.getKey(), ret, newHead);
            }

            return ret;
        }

        private Multimap<String, ForgeBlockStateV1.Variant> getPermutations(Map<String, Map<String, ForgeBlockStateV1.Variant>> base)
        {
            List<String> sorted = Lists.newArrayList(base.keySet());
            Collections.sort(sorted);   // Sort to get consistent results.
            return getPermutations(sorted, base, 0, "", LinkedHashMultimap.create(), null);
        }

        private List<ForgeBlockStateV1.Variant> getSubmodelPermutations(ForgeBlockStateV1.Variant baseVar, List<String> sorted, Map<String, List<ForgeBlockStateV1.Variant>> map, int depth, Map<String, ForgeBlockStateV1.Variant> parts, List<ForgeBlockStateV1.Variant> ret)
        {
            if (depth >= sorted.size())
            {
                ForgeBlockStateV1.Variant add = new Variant(baseVar); // Create a duplicate variant object so modifying it doesn't modify baseVar.
                for (Entry<String, ForgeBlockStateV1.Variant> part : parts.entrySet())    // Put all the parts with single variants for this permutation.
                    add.submodels.put(part.getKey(), Collections.singletonList(part.getValue()));
                ret.add(add);
                return ret;
            }

            String name = sorted.get(depth);
            List<ForgeBlockStateV1.Variant> vars = map.get(sorted.get(depth));

            if (vars != null)
            {
                for (ForgeBlockStateV1.Variant v : vars)
                {
                    if (v != null)
                    {   // We put this part variant in the permutation's map to add further in recursion, and then remove it afterward just in case.
                        parts.put(name, v);
                        getSubmodelPermutations(baseVar, sorted, map, depth + 1, parts, ret);
                        parts.remove(name);
                    }
                }
            }
            else
            {
                getSubmodelPermutations(baseVar, sorted, map, depth + 1, parts, ret);
            }

            return ret;
        }

        private List<ForgeBlockStateV1.Variant> getSubmodelPermutations(ForgeBlockStateV1.Variant baseVar, Map<String, List<ForgeBlockStateV1.Variant>> variants)
        {
            List<String> sorted = Lists.newArrayList(variants.keySet());
            Collections.sort(sorted);   // Sort to get consistent results.
            return getSubmodelPermutations(baseVar, sorted, variants, 0, new LinkedHashMap<>(), new ArrayList<>());
        }
    }

    public static class Variant
    {
        public static final Object SET_VALUE = new Object();

        @Nullable
        private ResourceLocation model = null;
        private boolean modelSet = false;
        private Optional<IModelState> state = Optional.empty();
        private Optional<Boolean> uvLock = Optional.empty();
        private Optional<Boolean> smooth = Optional.empty();
        private Optional<Boolean> gui3d = Optional.empty();
        private Optional<Integer> weight = Optional.empty();
        private Map<String, String> textures = Maps.newHashMap();
        private Map<String, List<ForgeBlockStateV1.Variant>> submodels = Maps.newHashMap();
        private Map<String, Object> simpleSubmodels = Maps.newHashMap(); // Makeshift Set to allow us to "remove" (replace value with null) singleParts when needed.
        private Map<String, String> customData = Maps.newHashMap();

        private Variant(){}
        /**
         * Clone a variant.
         * @param other Variant to clone.
         */
        private Variant(ForgeBlockStateV1.Variant other)
        {
            this.model = other.model;
            this.modelSet = other.modelSet;
            this.state = other.state;
            this.uvLock = other.uvLock;
            this.smooth = other.smooth;
            this.gui3d = other.gui3d;
            this.weight = other.weight;
            this.textures.putAll(other.textures);
            this.mergeModelPartVariants(this.submodels, other.submodels);
            this.simpleSubmodels.putAll(other.simpleSubmodels);
            this.customData.putAll(other.customData);
        }

        /**
         * Sets values in this variant to the input's values only if they haven't been set already. Essentially inherits values from o.
         */
        ForgeBlockStateV1.Variant sync(ForgeBlockStateV1.Variant parent)
        {
            if (!this.modelSet)
            {
                this.model    = parent.model;
                this.modelSet = parent.modelSet;
            }
            if (!this.state.isPresent()) this.state = parent.state;
            if (!this.uvLock.isPresent())   this.uvLock   = parent.uvLock;
            if (!this.smooth.isPresent())   this.smooth   = parent.smooth;
            if (!this.gui3d.isPresent())    this.gui3d    = parent.gui3d;
            if (!this.weight.isPresent())   this.weight   = parent.weight;

            for (Entry<String, String> e : parent.textures.entrySet())
            {
                if (!this.textures.containsKey(e.getKey()))
                    this.textures.put(e.getKey(), e.getValue());
            }

            mergeModelPartVariants(this.submodels, parent.submodels);

            for (Entry<String, Object> e : parent.simpleSubmodels.entrySet())
            {
                if (!this.simpleSubmodels.containsKey(e.getKey()))
                    this.simpleSubmodels.put(e.getKey(), e.getValue());
            }

            for (Entry<String, String> e : parent.customData.entrySet())
            {
                if (!this.customData.containsKey(e.getKey()))
                    this.customData.put(e.getKey(), e.getValue());
            }

            return this;
        }

        /**
         * Inherits model parts from a parent, creating deep clones of all Variants.
         */
        Map<String, List<ForgeBlockStateV1.Variant>> mergeModelPartVariants(Map<String, List<ForgeBlockStateV1.Variant>> output, Map<String, List<ForgeBlockStateV1.Variant>> input)
        {
            for (Entry<String, List<ForgeBlockStateV1.Variant>> e : input.entrySet())
            {
                String key = e.getKey();
                if (!output.containsKey(key))
                {
                    List<ForgeBlockStateV1.Variant> variants = e.getValue();

                    if (variants != null)
                    {
                        List<ForgeBlockStateV1.Variant> newVariants = Lists.newArrayListWithCapacity(variants.size());

                        for (ForgeBlockStateV1.Variant v : variants)
                            newVariants.add(new Variant(v));

                        output.put(key, newVariants);
                    }
                    else
                        output.put(key, variants);
                }
            }
            return output;
        }

        protected SubModel asGenericSubModel()
        {
            return new SubModel(state.orElse(TRSRTransformation.identity()), uvLock.orElse(false), smooth.orElse(true), gui3d.orElse(true), getTextures(), model, getCustomData());
        }

        /**
         * Gets a list containing the single variant of each part.
         * Will throw an error if this Variant has multiple variants for a submodel.
         */
        public ImmutableMap<String, SubModel> getOnlyPartsVariant()
        {
            if (submodels.size() > 0)
            {
                ImmutableMap.Builder<String, SubModel> builder = ImmutableMap.builder();

                for (Entry<String, List<ForgeBlockStateV1.Variant>> entry : submodels.entrySet())
                {
                    List<ForgeBlockStateV1.Variant> part = entry.getValue();

                    if (part != null)
                    {
                        if (part.size() == 1)
                            builder.put(entry.getKey(), part.get(0).asGenericSubModel());
                        else
                            throw new RuntimeException("Something attempted to get the list of submodels "
                                    + "for a variant with model \"" + model + "\", when this variant "
                                    + "contains multiple variants for submodel " + entry.getKey());
                    }
                }
                return builder.build();
            }
            else {
                return ImmutableMap.of();
            }
        }

        public Optional<Boolean> getSmooth()
        {
            return smooth;
        }

        public Optional<Boolean> getGui3d()
        {
            return gui3d;
        }

        public static class Deserializer implements JsonDeserializer<ForgeBlockStateV1.Variant>
        {
            static Variant.Deserializer INSTANCE = new Deserializer();

            /** Used <i>once</i> (then set null) for the key to put a simple submodel declaration under in the submodel map. */
            public String simpleSubmodelKey = null;

            protected ResourceLocation getBlockLocation(String location)
            {
                ResourceLocation tmp = new ResourceLocation(location);
                return new ResourceLocation(tmp.getNamespace(), "block/" + tmp.getPath());
            }

            /** Throws an error if there are submodels in this submodel. */
            private void throwIfNestedSubmodels(ForgeBlockStateV1.Variant submodel)
            {
                if (submodel.submodels.size() > 0)
                    throw new UnsupportedOperationException("Forge BlockStateLoader V1 does not support nested submodels.");
            }

            private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s)
            {
                return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                    new Vector3f(tx / 16, ty / 16, tz / 16),
                    TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
                    new Vector3f(s, s, s),
                    null));
            }

            private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

            private static TRSRTransformation leftify(TRSRTransformation transform)
            {
                return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
            }

            // Note: these strings might change to a full-blown resource locations in the future, and move from here to some json somewhere
            // TODO: vanilla now includes from parent, deprecate?
            private static final ImmutableMap<String, IModelState> transforms;

            static
            {
                ImmutableMap.Builder<String, IModelState> builder = ImmutableMap.builder();

                builder.put("identity", TRSRTransformation.identity());

                // block/block
                {
                    EnumMap<TransformType, TRSRTransformation> map = new EnumMap<>(TransformType.class);
                    TRSRTransformation thirdperson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
                    map.put(TransformType.GUI,                     get(0, 0, 0, 30, 225, 0, 0.625f));
                    map.put(TransformType.GROUND,                  get(0, 3, 0, 0, 0, 0, 0.25f));
                    map.put(TransformType.FIXED,                   get(0, 0, 0, 0, 0, 0, 0.5f));
                    map.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
                    map.put(TransformType.THIRD_PERSON_LEFT_HAND,  leftify(thirdperson));
                    map.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
                    map.put(TransformType.FIRST_PERSON_LEFT_HAND,  get(0, 0, 0, 0, 225, 0, 0.4f));
                    builder.put("forge:default-block", new SimpleModelState(ImmutableMap.copyOf(map)));
                }

                // item/generated
                {
                    EnumMap<TransformType, TRSRTransformation> map = new EnumMap<>(TransformType.class);
                    TRSRTransformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
                    TRSRTransformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
                    map.put(TransformType.GROUND,                  get(0, 2, 0, 0, 0, 0, 0.5f));
                    map.put(TransformType.HEAD,                    get(0, 13, 7, 0, 180, 0, 1));
                    map.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
                    map.put(TransformType.THIRD_PERSON_LEFT_HAND,  leftify(thirdperson));
                    map.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
                    map.put(TransformType.FIRST_PERSON_LEFT_HAND,  leftify(firstperson));
                    map.put(TransformType.FIXED,                   get(0, 0, 0, 0, 180, 0, 1));
                    builder.put("forge:default-item", new SimpleModelState(ImmutableMap.copyOf(map)));
                }

                // item/handheld
                {
                    EnumMap<TransformType, TRSRTransformation> map = new EnumMap<>(TransformType.class);
                    map.put(TransformType.GROUND,                  get(0, 2, 0, 0, 0, 0, 0.5f));
                    map.put(TransformType.HEAD,                    get(0, 13, 7, 0, 180, 0, 1));
                    map.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0, 4, 0.5f,         0, -90, 55, 0.85f));
                    map.put(TransformType.THIRD_PERSON_LEFT_HAND,  get(0, 4, 0.5f,         0, 90, -55, 0.85f));
                    map.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f));
                    map.put(TransformType.FIRST_PERSON_LEFT_HAND,  get(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f));
                    map.put(TransformType.FIXED,                   get(0, 0, 0, 0, 180, 0, 1));
                    builder.put("forge:default-tool", new SimpleModelState(ImmutableMap.copyOf(map)));
                }

                transforms = builder.build();
            }

            @Override
            public ForgeBlockStateV1.Variant deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
            {
                ForgeBlockStateV1.Variant ret = new Variant();
                JsonObject json = element.getAsJsonObject();

                if (json.has("model"))
                {   // Load base model location.
                    if (json.get("model").isJsonNull())
                        ret.model = null;   // Allow overriding base model to remove it from a state.
                    else
                        ret.model = getBlockLocation(JsonUtils.getString(json, "model"));
                    ret.modelSet = true;
                }

                if (json.has("textures"))
                {   // Load textures map.
                    for (Entry<String, JsonElement> e : json.get("textures").getAsJsonObject().entrySet())
                    {
                        if (e.getValue().isJsonNull())
                            ret.textures.put(e.getKey(), ""); // We have to use "" because ImmutableMaps don't allow nulls -.-
                        else
                            ret.textures.put(e.getKey(), e.getValue().getAsString());
                    }
                }

                if (json.has("x") || json.has("y"))
                {   // Load rotation values.
                    int x = JsonUtils.getInt(json, "x", 0);
                    int y = JsonUtils.getInt(json, "y", 0);
                    ret.state = Optional.ofNullable(ModelRotation.getModelRotation(x, y));
                    if (!ret.state.isPresent())
                        throw new JsonParseException("Invalid BlockModelRotation x: " + x + " y: " + y);
                }

                if (json.has("transform"))
                {
                    if (json.get("transform").isJsonPrimitive() && json.get("transform").getAsJsonPrimitive().isString())
                    {
                        String transform = json.get("transform").getAsString();
                        ret.state = Optional.ofNullable(transforms.get(transform));
                        if (!ret.state.isPresent())
                        {
                            throw new JsonParseException("transform: unknown default string: " + transform);
                        }
                    }
                    else if (!json.get("transform").isJsonObject())
                    {
                        try
                        {
                            TRSRTransformation base = context.deserialize(json.get("transform"), TRSRTransformation.class);
                            ret.state = Optional.of(TRSRTransformation.blockCenterToCorner(base));
                        }
                        catch (JsonParseException e)
                        {
                            throw new JsonParseException("transform: expected a string, object or valid base transformation, got: " + json.get("transform"));
                        }
                    }
                    else
                    {
                        JsonObject transform = json.get("transform").getAsJsonObject();
                        EnumMap<TransformType, TRSRTransformation> transforms = Maps.newEnumMap(TransformType.class);
                        if(transform.has("thirdperson"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("thirdperson"), TRSRTransformation.class);
                            transform.remove("thirdperson");
                            transforms.put(TransformType.THIRD_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("thirdperson_righthand"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("thirdperson_righthand"), TRSRTransformation.class);
                            transform.remove("thirdperson_righthand");
                            transforms.put(TransformType.THIRD_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("thirdperson_lefthand"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("thirdperson_lefthand"), TRSRTransformation.class);
                            transform.remove("thirdperson_lefthand");
                            transforms.put(TransformType.THIRD_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("firstperson"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("firstperson"), TRSRTransformation.class);
                            transform.remove("firstperson");
                            transforms.put(TransformType.FIRST_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("firstperson_righthand"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("firstperson_righthand"), TRSRTransformation.class);
                            transform.remove("firstperson_righthand");
                            transforms.put(TransformType.FIRST_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("firstperson_lefthand"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("firstperson_lefthand"), TRSRTransformation.class);
                            transform.remove("firstperson_lefthand");
                            transforms.put(TransformType.FIRST_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("head"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("head"), TRSRTransformation.class);
                            transform.remove("head");
                            transforms.put(TransformType.HEAD, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("gui"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("gui"), TRSRTransformation.class);
                            transform.remove("gui");
                            transforms.put(TransformType.GUI, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("ground"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("ground"), TRSRTransformation.class);
                            transform.remove("ground");
                            transforms.put(TransformType.GROUND, TRSRTransformation.blockCenterToCorner(t));
                        }
                        if(transform.has("fixed"))
                        {
                            TRSRTransformation t = context.deserialize(transform.get("fixed"), TRSRTransformation.class);
                            transform.remove("fixed");
                            transforms.put(TransformType.FIXED, TRSRTransformation.blockCenterToCorner(t));
                        }
                        int k = transform.entrySet().size();
                        if(transform.has("matrix")) k--;
                        if(transform.has("translation")) k--;
                        if(transform.has("rotation")) k--;
                        if(transform.has("scale")) k--;
                        if(transform.has("post-rotation")) k--;
                        if(k > 0)
                        {
                            throw new JsonParseException("transform: allowed keys: 'thirdperson', 'firstperson', 'gui', 'head', 'matrix', 'translation', 'rotation', 'scale', 'post-rotation'");
                        }
                        TRSRTransformation base = TRSRTransformation.identity();
                        if(!transform.entrySet().isEmpty())
                        {
                            base = context.deserialize(transform, TRSRTransformation.class);
                            base = TRSRTransformation.blockCenterToCorner(base);
                        }
                        IModelState state;
                        if(transforms.isEmpty())
                        {
                            state = base;
                        }
                        else
                        {
                            state = new SimpleModelState(Maps.immutableEnumMap(transforms), Optional.of(base));
                        }
                        ret.state = Optional.of(state);
                    }
                }

                if (json.has("uvlock"))
                {   // Load uvlock.
                    ret.uvLock = Optional.of(JsonUtils.getBoolean(json, "uvlock"));
                }

                if (json.has("smooth_lighting"))
                {
                    ret.smooth = Optional.of(JsonUtils.getBoolean(json, "smooth_lighting"));
                }

                if (json.has("gui3d"))
                {
                    ret.gui3d = Optional.of(JsonUtils.getBoolean(json, "gui3d"));
                }

                if (json.has("weight"))
                {   // Load weight.
                    ret.weight = Optional.of(JsonUtils.getInt(json, "weight"));
                }

                if (json.has("submodel"))
                {   // Load submodels.
                    JsonElement submodels = json.get("submodel");

                    if (submodels.isJsonPrimitive())
                    {   // Load a simple submodel declaration.
                        if (simpleSubmodelKey == null)
                            throw new RuntimeException("Attempted to use a simple submodel declaration outside a valid state variant declaration.");
                        String key = simpleSubmodelKey;
                        simpleSubmodelKey = null;

                        ret.model = getBlockLocation(submodels.getAsString());
                        ret.modelSet = true;
                        ForgeBlockStateV1.Variant dummyVar = new Variant();   // Create a dummy Variant to use as the owner of the simple submodel.
                        dummyVar.submodels.put(key, Collections.singletonList(ret));
                        dummyVar.simpleSubmodels = Collections.singletonMap(key, Variant.SET_VALUE);
                        return dummyVar;
                    }
                    else
                    {   // Load full submodel declarations.
                        // Clear the simple submodel key so that when deserializing submodels, the deserializer doesn't use the key.
                        simpleSubmodelKey = null;

                        for (Entry<String, JsonElement> submodel : submodels.getAsJsonObject().entrySet())
                        {
                            JsonElement varEl = submodel.getValue();
                            List<ForgeBlockStateV1.Variant> submodelVariants;

                            if (varEl.isJsonArray())
                            {   // Multiple variants of the submodel.
                                submodelVariants = Lists.newArrayList();
                                for (JsonElement e : varEl.getAsJsonArray())
                                    submodelVariants.add(context.deserialize(e, Variant.class));
                            }
                            else if (varEl.isJsonNull())
                            {
                                submodelVariants = null;
                            }
                            else
                            {
                                submodelVariants = Collections.singletonList(context.deserialize(varEl, Variant.class));
                            }

                            if (submodelVariants != null)   // Throw an error if there are submodels inside a submodel.
                                for (ForgeBlockStateV1.Variant part : submodelVariants)
                                    throwIfNestedSubmodels(part);

                            ret.submodels.put(submodel.getKey(), submodelVariants);
                            // Put null to remove this submodel from the list of simple submodels when something inherits this submodel.
                            ret.simpleSubmodels.put(submodel.getKey(), null);
                        }
                    }
                }

                if (json.has("custom"))
                {
                    for (Entry<String, JsonElement> e : json.get("custom").getAsJsonObject().entrySet())
                    {
                        if (e.getValue().isJsonNull())
                            ret.customData.put(e.getKey(), null);
                        else
                            ret.customData.put(e.getKey(), e.getValue().toString());
                    }
                }

                simpleSubmodelKey = null;

                return ret;
            }
        }

        @Nullable
        public ResourceLocation getModel() { return model; }
        public boolean isModelSet() { return modelSet; }
        public Optional<IModelState> getState() { return state; }
        public Optional<Boolean> getUvLock() { return uvLock; }
        public Optional<Integer> getWeight() { return weight; }
        public ImmutableMap<String, String> getTextures() { return ImmutableMap.copyOf(textures); }
        public ImmutableMap<String, List<ForgeBlockStateV1.Variant>> getSubmodels() { return ImmutableMap.copyOf(submodels); }
        public ImmutableMap<String, String> getCustomData() { return ImmutableMap.copyOf(customData); }
    }

    public static class TRSRDeserializer implements JsonDeserializer<TRSRTransformation>
    {
        public static final TRSRDeserializer INSTANCE = new TRSRDeserializer();

        @Override
        public TRSRTransformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
            {
                String transform = json.getAsString();
                if(transform.equals("identity"))
                {
                    return TRSRTransformation.identity();
                }
                else
                {
                    throw new JsonParseException("TRSR: unknown default string: " + transform);
                }
            }
            if (json.isJsonArray())
            {
                // direct matrix array
                return new TRSRTransformation(parseMatrix(json));
            }
            if (!json.isJsonObject()) throw new JsonParseException("TRSR: expected array or object, got: " + json);
            JsonObject obj = json.getAsJsonObject();
            TRSRTransformation ret;
            if (obj.has("matrix"))
            {
                // matrix as a sole key
                ret = new TRSRTransformation(parseMatrix(obj.get("matrix")));
                obj.remove("matrix");
                if (obj.entrySet().size() != 0)
                {
                    throw new JsonParseException("TRSR: can't combine matrix and other keys");
                }
                return ret;
            }
            Vector3f translation = null;
            Quat4f leftRot = null;
            Vector3f scale = null;
            Quat4f rightRot = null;
            if (obj.has("translation"))
            {
                translation = new Vector3f(parseFloatArray(obj.get("translation"), 3, "Translation"));
                obj.remove("translation");
            }
            if (obj.has("rotation"))
            {
                leftRot = parseRotation(obj.get("rotation"));
                obj.remove("rotation");
            }
            if (obj.has("scale"))
            {
                if(!obj.get("scale").isJsonArray())
                {
                    try
                    {
                        float s = obj.get("scale").getAsNumber().floatValue();
                        scale = new Vector3f(s, s, s);
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("TRSR scale: expected number or array, got: " + obj.get("scale"));
                    }
                }
                else
                {
                    scale = new Vector3f(parseFloatArray(obj.get("scale"), 3, "Scale"));
                }
                obj.remove("scale");
            }
            if (obj.has("post-rotation"))
            {
                rightRot = parseRotation(obj.get("post-rotation"));
                obj.remove("post-rotation");
            }
            if (!obj.entrySet().isEmpty()) throw new JsonParseException("TRSR: can either have single 'matrix' key, or a combination of 'translation', 'rotation', 'scale', 'post-rotation'");
            return new TRSRTransformation(translation, leftRot, scale, rightRot);
        }

        public static Matrix4f parseMatrix(JsonElement e)
        {
            if (!e.isJsonArray()) throw new JsonParseException("Matrix: expected an array, got: " + e);
            JsonArray m = e.getAsJsonArray();
            if (m.size() != 3) throw new JsonParseException("Matrix: expected an array of length 3, got: " + m.size());
            Matrix4f ret = new Matrix4f();
            for (int i = 0; i < 3; i++)
            {
                if (!m.get(i).isJsonArray()) throw new JsonParseException("Matrix row: expected an array, got: " + m.get(i));
                JsonArray r = m.get(i).getAsJsonArray();
                if (r.size() != 4) throw new JsonParseException("Matrix row: expected an array of length 4, got: " + r.size());
                for (int j = 0; j < 4; j++)
                {
                    try
                    {
                        ret.setElement(i, j, r.get(j).getAsNumber().floatValue());
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("Matrix element: expected number, got: " + r.get(j));
                    }
                }
            }
            return ret;
        }

        public static float[] parseFloatArray(JsonElement e, int length, String prefix)
        {
            if (!e.isJsonArray()) throw new JsonParseException(prefix + ": expected an array, got: " + e);
            JsonArray t = e.getAsJsonArray();
            if (t.size() != length) throw new JsonParseException(prefix + ": expected an array of length " + length + ", got: " + t.size());
            float[] ret = new float[length];
            for (int i = 0; i < length; i++)
            {
                try
                {
                    ret[i] = t.get(i).getAsNumber().floatValue();
                }
                catch (ClassCastException ex)
                {
                    throw new JsonParseException(prefix + " element: expected number, got: " + t.get(i));
                }
            }
            return ret;
        }

        public static Quat4f parseAxisRotation(JsonElement e)
        {
            if (!e.isJsonObject()) throw new JsonParseException("Axis rotation: object expected, got: " + e);
            JsonObject obj  = e.getAsJsonObject();
            if (obj.entrySet().size() != 1) throw new JsonParseException("Axis rotation: expected single axis object, got: " + e);
            Map.Entry<String, JsonElement> entry = obj.entrySet().iterator().next();
            Quat4f ret = new Quat4f();
            try
            {
                if (entry.getKey().equals("x"))
                {
                    ret.set(new AxisAngle4d(1, 0, 0, Math.toRadians(entry.getValue().getAsNumber().floatValue())));
                }
                else if (entry.getKey().equals("y"))
                {
                    ret.set(new AxisAngle4d(0, 1, 0, Math.toRadians(entry.getValue().getAsNumber().floatValue())));
                }
                else if (entry.getKey().equals("z"))
                {
                    ret.set(new AxisAngle4d(0, 0, 1, Math.toRadians(entry.getValue().getAsNumber().floatValue())));
                }
                else throw new JsonParseException("Axis rotation: expected single axis key, got: " + entry.getKey());
            }
            catch(ClassCastException ex)
            {
                throw new JsonParseException("Axis rotation value: expected number, got: " + entry.getValue());
            }
            return ret;
        }

        public static Quat4f parseRotation(JsonElement e)
        {
            if (e.isJsonArray())
            {
                if (e.getAsJsonArray().get(0).isJsonObject())
                {
                    Quat4f ret = new Quat4f(0, 0, 0, 1);
                    for (JsonElement a : e.getAsJsonArray())
                    {
                        ret.mul(parseAxisRotation(a));
                    }
                    return ret;
                }
                else if (e.isJsonArray())
                {
                    JsonArray array = e.getAsJsonArray();
                    if (array.size() == 3) //Vanilla rotation
                        return TRSRTransformation.quatFromXYZDegrees(new Vector3f(parseFloatArray(e, 3, "Rotation")));
                    else // quaternion
                        return new Quat4f(parseFloatArray(e, 4, "Rotation"));
                }
                else throw new JsonParseException("Rotation: expected array or object, got: " + e);
            }
            else if (e.isJsonObject())
            {
                return parseAxisRotation(e);
            }
            else throw new JsonParseException("Rotation: expected array or object, got: " + e);
        }
    }
}
