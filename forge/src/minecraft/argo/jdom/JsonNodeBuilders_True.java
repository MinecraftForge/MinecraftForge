package argo.jdom;

final class JsonNodeBuilders_True implements JsonNodeBuilder
{
    public JsonNode buildNode()
    {
        return JsonNodeFactories.aJsonTrue();
    }
}
