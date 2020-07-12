package net.minecraftforge.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class JsonMerger
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Gson SERIALIZER = new GsonBuilder().create();

    public static JsonElement combineAllJsonResources(IResourceManager resourceManager, ResourceLocation location) throws IOException
    {
        List<IResource> allResources = resourceManager.getAllResources(location);
        List<IResource> resourceList = allResources.size() == 1 ? allResources : Lists.reverse(allResources);
        List<JsonElement> results = Lists.newArrayList();
        boolean stopping = false;
        for(IResource resource : resourceList)
        {
            if (stopping)
            {
                // needed to avoid leaking file handles.
                resource.close();
                continue;
            }
            try (   IResource t = resource;
                    InputStream is = t.getInputStream();
                    InputStreamReader rd = new InputStreamReader(is))
            {
                JsonElement jsonElement = SERIALIZER.fromJson(rd, JsonElement.class);
                if (jsonElement == null) {
                    LOGGER.error("Couldn't load data from {} as it's null or empty", location);
                    return null;
                }

                results.add(jsonElement);

                if (isOverwriteMode(jsonElement))
                    stopping = true;
            }
            catch(IOException e)
            {
                LOGGER.error("Error reading JSON from {}", location, e);
            }
        }

        return combineAllJsonResources(results.size() == 1 ? results : Lists.reverse(results));
    }

    public static JsonElement combineAllJsonResources(List<JsonElement> elements)
    {
        JsonElement merged = null;
        for(JsonElement next : elements)
        {
            if (merged == null)
            {
                merged = next;
            }
            else
            {
                merged = combineJsonElements(merged, next, MergeMode.OVERWRITE);
            }
        }
        return merged;
    }

    private static JsonElement combineJsonElements(JsonElement first, JsonElement second, MergeMode parentMode)
    {
        MergeMode mode = parentMode;
        JsonObject settings;
        if (second.isJsonObject())
        {
            JsonObject secondObj = second.getAsJsonObject();
            if (secondObj.has("_forge_combine"))
            {
                settings = secondObj.getAsJsonObject("_forge_combine");

                if (settings.has("mode"))
                {
                    String modeName = settings.get("mode").getAsString();
                    mode = MergeMode.byName(modeName);
                    if (mode == null)
                        throw new JsonSyntaxException(String.format("Unknown combine mode: %s",  modeName));
                }

                if (settings.has("value"))
                {
                    second = settings.get("value");

                    for(Map.Entry<String, JsonElement> child : secondObj.entrySet())
                    {
                        if (!child.getKey().equals("_forge_combine"))
                            throw new JsonSyntaxException("Objects must not have content other than '_forge_combine' if they use the 'value' feature.");
                    }
                }
            }
        }

        return getCombinedInternal(first, second, mode);
    }

    private static JsonElement getCombinedInternal(JsonElement first, JsonElement second, MergeMode mode)
    {
        if (mode == MergeMode.OVERWRITE)
            return second;

        if (first.isJsonObject())
        {
            if (!second.isJsonObject())
            {
                throw new JsonSyntaxException("The combining counterpart for a json object must be another json object");
            }

            if (mode == MergeMode.COMBINE)
            {
                return combineObjects(first.getAsJsonObject(), second.getAsJsonObject());
            }

            throw new JsonSyntaxException(String.format("Invalid combine mode for a json object: %s. Allowed: overwrite, combine", mode));
        }
        else if (first.isJsonArray())
        {
            switch(mode)
            {
                case COMBINE:
                    return concatenateArrays(first.getAsJsonArray(), second.getAsJsonArray());
                case ZIP:
                    return zipArrays(first.getAsJsonArray(), second.getAsJsonArray());
                default:
                    throw new JsonSyntaxException(String.format("Invalid combine mode for a json array: %s. Allowed: overwrite, combine, zip", mode));
            }
        }
        else if (first.isJsonNull())
        {
            if (mode != MergeMode.COMBINE)
                throw new JsonSyntaxException(String.format("Invalid combine mode for a json null: %s", mode));
        }
        else
        {
            if (mode != MergeMode.COMBINE)
                throw new JsonSyntaxException(String.format("Invalid combine mode for a json primitive: %s", mode));
        }

        return second;
    }

    public static JsonElement combineObjects(JsonObject first, JsonObject second)
    {
        JsonObject result = new JsonObject();
        for(Map.Entry<String, JsonElement> entry : first.entrySet())
        {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (second.has(key))
            {
                result.add(key, combineJsonElements(value, second.get(key), MergeMode.COMBINE));
            }
            else
            {
                result.add(key, value);
            }
        }
        for(Map.Entry<String, JsonElement> entry : first.entrySet())
        {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (!result.has(key))
            {
                result.add(key, value);
            }
        }
        return result;
    }

    private static JsonElement zipArrays(JsonArray first, JsonArray second)
    {
        JsonArray a = new JsonArray();
        int end = Math.min(first.size(),second.size());
        for(int i=0;i<end;i++)
        {
            a.add(combineJsonElements(first.get(i), second.get(i), MergeMode.COMBINE));
        }
        return null;
    }

    private static JsonElement concatenateArrays(JsonArray first, JsonArray second)
    {
        List<JsonElement> elements = Lists.newArrayList();
        for(int i=0;i<first.size();i++)
            elements.add(first.get(i));
        for(int i=0;i<second.size();i++)
            processJsonArrayElement(elements, second.get(i));

        JsonArray result = new JsonArray();
        for (JsonElement element : elements)
            result.add(element);
        return result;
    }

    private static void processJsonArrayElement(List<JsonElement> parent, JsonElement second)
    {
        MergeMode mode = MergeMode.APPEND;
        JsonObject settings;
        Integer index = null;
        JsonElement find = null;
        boolean silent = false;
        boolean exact = false;
        boolean after = false;
        if (second.isJsonObject())
        {
            JsonObject secondObj = second.getAsJsonObject();
            if (secondObj.has("_forge_combine"))
            {
                settings = secondObj.getAsJsonObject("_forge_combine");

                if (settings.has("mode"))
                {
                    String modeName = settings.get("mode").getAsString();
                    mode = MergeMode.byName(modeName);
                    if (mode == null)
                        throw new JsonSyntaxException(String.format("Unknown combine mode: %s",  modeName));
                }

                if (settings.has("value"))
                {
                    second = settings.get("value");
                }

                if (settings.has("index"))
                {
                    index = settings.get("index").getAsInt();
                }

                if (settings.has("find"))
                {
                    if (index != null)
                        throw new JsonSyntaxException("Array replace option 'find' can not be used at the same time as 'index'.");
                    find = settings.get("find");
                }

                if (settings.has("silent"))
                    silent = settings.get("silent").getAsBoolean();

                if (settings.has("exact"))
                    exact = settings.get("exact").getAsBoolean();

                if (settings.has("after"))
                    after = settings.get("after").getAsBoolean();
            }
        }

        switch(mode)
        {
            case APPEND:
            {
                parent.add(second);
                break;
            }
            case INSERT:
            case OVERWRITE:
            case COMBINE:
            {
                if (index == null && find == null)
                    throw new JsonSyntaxException("Insert/replace mode must specify an 'index' or a 'find' search pattern.");
                if (index != null)
                {
                    switch (mode)
                    {
                        case COMBINE:
                            parent.set(index, combineJsonElements(parent.get(index), second, MergeMode.COMBINE));
                            break;
                        case INSERT:
                            if (after)
                                throw new JsonSyntaxException("After is not available in indexed insert mode.");
                            parent.add(index, second);
                            break;
                        default:
                            parent.set(index, second);
                            break;
                    }
                }
                else
                {
                    boolean found = false;
                    for(int i=0;i<parent.size();i++)
                    {
                        if (compareWithPattern(parent.get(i), find, exact))
                        {
                            switch (mode)
                            {
                                case COMBINE:
                                    parent.set(i, combineJsonElements(parent.get(i), second, MergeMode.COMBINE));
                                    break;
                                case INSERT:
                                    if (after)
                                        parent.add(i+1, second);
                                    else
                                        parent.add(i, second);
                                    break;
                                default:
                                    parent.set(i, second);
                                    break;
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found && !silent)
                    {
                        LOGGER.warn("Replace pattern did not find a target while looking for: {}", find.toString());
                    }
                }
                break;
            }
            default:
                throw new JsonSyntaxException(String.format("Invalid combine mode for children of json arrays: %s. Allowed: append, combine, overwrite, insert", mode));
        }
    }

    private static boolean compareWithPattern(JsonElement base, JsonElement pattern, boolean exact)
    {
        if (pattern.isJsonObject())
        {
            if (!base.isJsonObject())
                return false;
            return compareObjectWithPattern(base.getAsJsonObject(), pattern.getAsJsonObject(), exact);
        }
        else if(pattern.isJsonArray())
        {
            if (!base.isJsonArray())
                return false;
            return compareArrayWithPattern(base.getAsJsonArray(), pattern.getAsJsonArray(), exact);
        }
        else
        {
            return base.equals(pattern);
        }
    }

    private static boolean compareArrayWithPattern(JsonArray array, JsonArray pattern, boolean exact)
    {
        if (exact)
        {
            if (array.size() != pattern.size())
                return false;

            for(int i=0;i<pattern.size();i++)
            {
                if (!compareWithPattern(array.get(i), pattern.get(i), true))
                    return false;
            }
        }

        if (pattern.size() > array.size())
            return false;

        BitSet processedIndices = new BitSet();
        for(int i=0;i<pattern.size();i++)
        {
            boolean found = false;
            for(int j=0;j<array.size();j++)
            {
                if (processedIndices.get(j))
                    continue;

                if (compareWithPattern(array.get(j), pattern.get(i), true))
                {
                    processedIndices.set(j);
                    found = true;
                    break;
                }
            }

            if(!found)
                return false;
        }

        return true;
    }

    private static boolean compareObjectWithPattern(JsonObject obj, JsonObject pattern, boolean exact)
    {
        for(Map.Entry<String, JsonElement> entry : pattern.entrySet())
        {
            String key = entry.getKey();
            if (!obj.has(key))
                return false;
            if (!compareWithPattern(obj.get(key), entry.getValue(), exact))
                return false;
        }

        if (exact)
        {
            for(Map.Entry<String, JsonElement> entry : pattern.entrySet())
            {
                String key = entry.getKey();
                if (!pattern.has(key))
                    return false;
            }
        }

        return true;
    }

    private static boolean isOverwriteMode(JsonElement jsonElement)
    {
        if (!jsonElement.isJsonObject())
            return true;
        JsonObject obj = jsonElement.getAsJsonObject();
        if (!obj.has("_forge_combine"))
            return true;
        JsonObject settings = obj.getAsJsonObject("_forge_combine");
        if (!settings.has("mode"))
            return true;
        return settings.get("mode").getAsString().equals("overwrite");
    }

    public enum MergeMode
    {
        OVERWRITE("overwrite"), /* default vanilla behaviour. CHILDREN OF ARRAYS: replaces */

        COMBINE("combine"), /* OBJECTS: combines values with same key; ARRAYS: inserts, replaces or appends the elements of the second array into the first */

        ZIP("zip"), /* ARRAYS ONLY: zips the arrays, combining each pair of elements */

        APPEND("append"), /* CHILDREN OF ARRAYS ONLY: inserts the element at the end of the array (DEFAULT) */
        INSERT("insert"); /* CHILDREN OF ARRAYS ONLY: inserts the element at the specified index */

        private final String name;

        MergeMode(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public static final ImmutableList<MergeMode> VALUES = ImmutableList.copyOf(values());

        @Nullable
        public static MergeMode byName(String name)
        {
            for(MergeMode m : VALUES)
            {
                if (m.getName().equals(name))
                    return m;
            }
            return null;
        }
    }
}
