package argo.jdom;

import java.util.Arrays;
import java.util.Map;

public final class JsonNodeFactories
{
    public static JsonNode aJsonNull()
    {
        return JsonConstants.NULL;
    }

    public static JsonNode aJsonTrue()
    {
        return JsonConstants.TRUE;
    }

    public static JsonNode aJsonFalse()
    {
        return JsonConstants.FALSE;
    }

    public static JsonStringNode aJsonString(String par0Str)
    {
        return new JsonStringNode(par0Str);
    }

    public static JsonNode aJsonNumber(String par0Str)
    {
        return new JsonNumberNode(par0Str);
    }

    public static JsonRootNode aJsonArray(Iterable par0Iterable)
    {
        return new JsonArray(par0Iterable);
    }

    public static JsonRootNode aJsonArray(JsonNode ... par0ArrayOfJsonNode)
    {
        return aJsonArray(Arrays.asList(par0ArrayOfJsonNode));
    }

    public static JsonRootNode aJsonObject(Map par0Map)
    {
        return new JsonObject(par0Map);
    }
}
