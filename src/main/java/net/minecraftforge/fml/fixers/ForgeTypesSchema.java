package net.minecraftforge.fml.fixers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class ForgeTypesSchema extends NamespacedSchema
{

    protected static final Map<String, Function<Schema, Supplier<TypeTemplate>>> ENTITIES_MAP = new HashMap<String, Function<Schema, Supplier<TypeTemplate>>>();
    protected static final Map<String, Function<Schema, Supplier<TypeTemplate>>> TILEENTITIES_MAP = new HashMap<String, Function<Schema, Supplier<TypeTemplate>>>();

    public static void registerSimpleEntity(String name)
    {
        ENTITIES_MAP.put(name, s -> DSL::remainder);
        DataFixesManager.markDirty();
    }

    public static void registerSimpleTileEntity(String name)
    {
        TILEENTITIES_MAP.put(name, s -> DSL::remainder);
        DataFixesManager.markDirty();
    }

    public static void registerEntity(String name, Supplier<TypeTemplate> type)
    {
        ENTITIES_MAP.put(name, s -> type);
        DataFixesManager.markDirty();
    }

    public static void registerTileEntity(String name, Supplier<TypeTemplate> type)
    {
        TILEENTITIES_MAP.put(name, s -> type);
        DataFixesManager.markDirty();
    }
    
    public static void registerEntity(String name, Function<Schema, Supplier<TypeTemplate>> typeGetter)
    {
        ENTITIES_MAP.put(name, typeGetter);
        DataFixesManager.markDirty();
    }

    public static void registerTileEntity(String name, Function<Schema, Supplier<TypeTemplate>> typeGetter)
    {
        TILEENTITIES_MAP.put(name, typeGetter);
        DataFixesManager.markDirty();
    }

    public static void registerEquipmentEntity(String name)
    {
        ENTITIES_MAP.put(name, schema -> () -> {
            return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
        });
        DataFixesManager.markDirty();
    }

    public ForgeTypesSchema(int versionKey, Schema schema)
    {
        super(versionKey, schema);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema)
    {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        TILEENTITIES_MAP.forEach((s, func) -> map.put(s, func.apply(schema)));
        return map;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema)
    {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
        ENTITIES_MAP.forEach((s, func) -> map.put(s, func.apply(schema)));
        return map;
    }

}
