package net.minecraftforge.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.tags.Tag;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

public class TagDeserializerExtensions
{
    public static <T> void deserializeAdditional(Tag.Builder<T> builder, Predicate<ResourceLocation> isValueKnown, Function<ResourceLocation, T> valueGetter, JsonObject json)
    {
        if (json.has("optional_values"))
        {
            for (JsonElement entry : JsonUtils.getJsonArray(json, "optional_values"))
            {
                String s = JsonUtils.getString(entry, "value");
                if (!s.startsWith("#"))
                {
                    ResourceLocation rl = new ResourceLocation(s);
                    if (isValueKnown.test(rl) && valueGetter.apply(rl) != null)
                    {
                        builder.add(valueGetter.apply(rl));
                    }
                } else
                {
                    builder.add(new OptionalTagEntry<>(new ResourceLocation(s.substring(1))));
                }
            }
        }

        if (json.has("remove_values"))
        {
            for (JsonElement entry : JsonUtils.getJsonArray(json, "remove_values"))
            {
                String s = JsonUtils.getString(entry, "value");
                if (!s.startsWith("#"))
                {
                    ResourceLocation rl = new ResourceLocation(s);
                    if (isValueKnown.test(rl) && valueGetter.apply(rl) != null)
                    {
                        Tag.ITagEntry<T> dummyEntry = new Tag.ListEntry<>(Collections.singletonList(valueGetter.apply(rl)));
                        builder.remove(dummyEntry);
                    }
                } else
                {
                    Tag.ITagEntry<T> dummyEntry = new Tag.TagEntry<>(new ResourceLocation(s.substring(1)));
                    builder.remove(dummyEntry);
                }
            }
        }
    }

    public static class OptionalTagEntry<T> extends Tag.TagEntry<T>
    {
        private Tag<T> resolvedTag = null;

        OptionalTagEntry(ResourceLocation referent)
        {
            super(referent);
        }

        @Override
        public boolean resolve(@Nonnull Function<ResourceLocation, Tag<T>> resolver)
        {
            if (this.resolvedTag == null)
            {
                this.resolvedTag = resolver.apply(this.getSerializedId());
            }
            return true; // never fail if resolver returns null
        }

        @Override
        public void populate(@Nonnull Collection<T> items)
        {
            if (this.resolvedTag != null)
            {
                items.addAll(this.resolvedTag.getAllElements());
            }
        }
    }
}
