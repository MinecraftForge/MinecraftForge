package argo.jdom;

import java.util.List;
import java.util.Map;

final class JsonConstants extends JsonNode
{
    static final JsonConstants NULL = new JsonConstants(JsonNodeType.NULL);
    static final JsonConstants TRUE = new JsonConstants(JsonNodeType.TRUE);
    static final JsonConstants FALSE = new JsonConstants(JsonNodeType.FALSE);
    private final JsonNodeType jsonNodeType;

    private JsonConstants(JsonNodeType par1JsonNodeType)
    {
        this.jsonNodeType = par1JsonNodeType;
    }

    public JsonNodeType getType()
    {
        return this.jsonNodeType;
    }

    public String getText()
    {
        throw new IllegalStateException("Attempt to get text on a JsonNode without text.");
    }

    /**
     * return the fields associated with this node
     */
    public Map getFields()
    {
        throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
    }

    public List getElements()
    {
        throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
    }
}
