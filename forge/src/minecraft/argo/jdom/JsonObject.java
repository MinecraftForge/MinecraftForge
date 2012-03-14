package argo.jdom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class JsonObject extends JsonRootNode
{
    private final Map fields;

    JsonObject(Map par1Map)
    {
        this.fields = new HashMap(par1Map);
    }

    /**
     * return the fields associated with this node
     */
    public Map getFields()
    {
        return new HashMap(this.fields);
    }

    public JsonNodeType getType()
    {
        return JsonNodeType.OBJECT;
    }

    public String getText()
    {
        throw new IllegalStateException("Attempt to get text on a JsonNode without text.");
    }

    public List getElements()
    {
        throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            JsonObject var2 = (JsonObject)par1Obj;
            return this.fields.equals(var2.fields);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.fields.hashCode();
    }

    public String toString()
    {
        return "JsonObject fields:[" + this.fields + "]";
    }
}
