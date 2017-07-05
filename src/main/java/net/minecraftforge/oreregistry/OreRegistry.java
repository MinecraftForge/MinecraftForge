/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.oreregistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;

/**
 * The Ore Registry allows mods to register *basic* material items so they can be unified.
 * <p>
 * Using the Ore Registry, everything can return a single unified type,
 * so for example players will only ever get one kind of Copper Ingot ItemStack.
 * <p>
 * Basic materials follow vanilla conventions, like iron ore being smeltable into iron ingots.
 * If other mods cannot make basic assumptions about your materials, do not register them here.
 * <p>
 * Recipes created with the Ore Registry will accept any item registered for a type, and return just the one unified type.
 * Blocks are not unified, but ores generated in the world should be written to drop the ore ItemStack registered here.
 * <p>
 * <b>Usage:</b>
 * <p>
 * First, create your {@link OreMaterial}s and {@link OreShape}s or use the ones listed in {@link OreRegistryConstants}.
 * <p>
 * Then, add your ores with {@link #addOre(OreMaterial, OreShape, ItemStack)}.
 * You will get back instances of {@link BasicOre}, which is used to unify all ores of that type.
 * <p>
 * <b>Recipes:</b>
 * <p>
 * {@link BasicOre} can be used in recipes like {@link ShapedForgeRecipe} and {@link ShapelessForgeRecipe},
 * or you can implement your own recipes.
 * <p>
 * You can also create JSON recipes that use {@link BasicOre} as an ingredient or result, by using
 * the recipe type "forge:shaped" or "forge:shapeless", and using the ingredient format
 * {"type":"forge:ore_registry", "material": "materialUid", "shape": "shapeUid"}
 * For examples see the json recipes in the forge test directory "src\test\resources\assets\ore_registry_test\recipes"
 */
public final class OreRegistry
{
    private static final Map<OreMaterial, Map<OreShape, BasicOre>> ORE_TABLE = new IdentityHashMap<>();
    private static final Map<String, OreMaterial> MATERIALS = new HashMap<>();
    private static final Map<String, OreShape> SHAPES = new HashMap<>();

    private OreRegistry()
    {
    }

    /**
     * Get an OreMaterial from a unique Id. Creates one if it does not exist.
     * Many materials are already created in {@link OreRegistryConstants}.
     */
    @Nonnull
    public static OreMaterial addMaterial(String materialUid)
    {
        Preconditions.checkNotNull(materialUid, "material uid must not be null.");
        return MATERIALS.computeIfAbsent(materialUid, OreMaterial::new);
    }

    /**
     * Get an OreMaterial from a unique Id. Returns null if it does not exist.
     * Many materials are already created in {@link OreRegistryConstants}.
     */
    @Nullable
    public static OreMaterial getMaterial(String materialUid)
    {
        Preconditions.checkNotNull(materialUid, "material uid must not be null.");
        return MATERIALS.get(materialUid);
    }

    /**
     * Get an OreShape from a unique Id. Creates one if it does not exist.
     * Many shapes are already created in {@link OreRegistryConstants}.
     */
    @Nonnull
    public static OreShape addShape(String shapeUid)
    {
        Preconditions.checkNotNull(shapeUid, "shape uid must not be null.");
        return SHAPES.computeIfAbsent(shapeUid, OreShape::new);
    }

    /**
     * Get an OreShape from a unique Id. Returns null if it does not exist.
     * Many shapes are already created in {@link OreRegistryConstants}.
     */
    @Nullable
    public static OreShape getShape(String shapeUid)
    {
        Preconditions.checkNotNull(shapeUid, "shape uid must not be null.");
        return SHAPES.get(shapeUid);
    }

    /**
     * Add an ore to the registry.
     * If no BasicOre exists yet, one is created.
     * If a BasicOre already exist with these properties, the ore is added to its list of variants.
     * For example usage, see {@link OreRegistryConstants#initVanillaEntries()}
     */
    @Nonnull
    public static BasicOre addOre(OreMaterial material, OreShape shape, ItemStack ore)
    {
        Preconditions.checkNotNull(material, "material must not be null");
        Preconditions.checkNotNull(shape, "shape must not be null");

        Map<OreShape, BasicOre> oreShapeBasicOreMap = ORE_TABLE.computeIfAbsent(material, k -> new IdentityHashMap<>());
        BasicOre basicOre = oreShapeBasicOreMap.computeIfAbsent(shape, k -> new BasicOre(material, shape));
        basicOre.addVariant(ore);
        return basicOre;
    }

    /**
     * @return a BasicOre with the given properties if it has been added before,
     * or null if the specified BasicOre has not been added.
     */
    @Nullable
    public static BasicOre getOre(OreMaterial material, OreShape shape)
    {
        Preconditions.checkNotNull(material, "material must not be null");
        Preconditions.checkNotNull(shape, "shape must not be null");
        Map<OreShape, BasicOre> oreShapeBasicOreMap = ORE_TABLE.get(material);
        if (oreShapeBasicOreMap != null)
        {
            return oreShapeBasicOreMap.get(shape);
        }
        return null;
    }

    /**
     * @return all the materials that have been added.
     */
    @Nonnull
    public static Collection<OreMaterial> getMaterials()
    {
        return Collections.unmodifiableCollection(MATERIALS.values());
    }

    /**
     * @return all the shapes that have been added.
     */
    @Nonnull
    public static Collection<OreShape> getShapes()
    {
        return Collections.unmodifiableCollection(SHAPES.values());
    }

    /**
     * @return a map of all the shapes registered for a given material.
     */
    @Nonnull
    public static Map<OreShape, BasicOre> getShapesMapForMaterial(OreMaterial material)
    {
        Preconditions.checkNotNull(material, "material must not be null");
        Map<OreShape, BasicOre> oreShapeBasicOreMap = ORE_TABLE.get(material);
        if (oreShapeBasicOreMap == null) return Collections.emptyMap();
        return Collections.unmodifiableMap(oreShapeBasicOreMap);
    }
}
