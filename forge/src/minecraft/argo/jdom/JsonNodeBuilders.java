package argo.jdom;

public final class JsonNodeBuilders
{
    public static JsonNodeBuilder func_27248_a()
    {
        return new JsonNodeBuilders_Null();
    }

    public static JsonNodeBuilder func_27251_b()
    {
        return new JsonNodeBuilders_True();
    }

    public static JsonNodeBuilder func_27252_c()
    {
        return new JsonNodeBuilders_False();
    }

    public static JsonNodeBuilder func_27250_a(String par0Str)
    {
        return new JsonNumberNodeBuilder(par0Str);
    }

    public static JsonStringNodeBuilder func_27254_b(String par0Str)
    {
        return new JsonStringNodeBuilder(par0Str);
    }

    public static JsonObjectNodeBuilder anObjectBuilder()
    {
        return new JsonObjectNodeBuilder();
    }

    public static JsonArrayNodeBuilder anArrayBuilder()
    {
        return new JsonArrayNodeBuilder();
    }
}
