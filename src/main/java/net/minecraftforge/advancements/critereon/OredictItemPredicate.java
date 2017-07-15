package net.minecraftforge.advancements.critereon;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.oredict.OreDictionary;

/**
 * 
 * @author Landmaster
 *
 */
public class OredictItemPredicate extends ItemPredicate
{
    // cannot be named "deserialize" because that would conflict with ItemPredicate#deserialize
    public static OredictItemPredicate deserializeOredictPredicate(@Nullable JsonElement element)
    {
        final JsonObject jsonObject = JsonUtils.getJsonObject(element, "ore");
        return new OredictItemPredicate(JsonUtils.getString(jsonObject, "ore"));
    }

    public static OredictItemPredicate[] deserializeOredictPredicateArray(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            final JsonArray jsonArray = JsonUtils.getJsonArray(element, "ores");
            final OredictItemPredicate[] predicateArray = new OredictItemPredicate[jsonArray.size()];

            for (int i = 0; i < predicateArray.length; ++i)
            {
                predicateArray[i] = deserializeOredictPredicate(jsonArray.get(i));
            }

            return predicateArray;
        }
        return new OredictItemPredicate[0];
    }

    private final String ore;

    public OredictItemPredicate(String ore)
    {
        this.ore = ore;
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return !stack.isEmpty() && ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID(ore));
    }
}
