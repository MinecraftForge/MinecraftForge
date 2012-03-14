package argo.jdom;

final class JsonNodeBuilders_False implements JsonNodeBuilder
{
    public JsonNode buildNode()
    {
        return JsonNodeFactories.aJsonFalse();
    }
}
