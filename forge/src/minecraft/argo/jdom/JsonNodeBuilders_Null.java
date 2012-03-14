package argo.jdom;

final class JsonNodeBuilders_Null implements JsonNodeBuilder
{
    public JsonNode buildNode()
    {
        return JsonNodeFactories.aJsonNull();
    }
}
