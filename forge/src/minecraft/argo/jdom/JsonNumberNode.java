package argo.jdom;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

final class JsonNumberNode extends JsonNode
{
    private static final Pattern PATTERN = Pattern.compile("(-?)(0|([1-9]([0-9]*)))(\\.[0-9]+)?((e|E)(\\+|-)?[0-9]+)?");
    private final String value;

    JsonNumberNode(String par1Str)
    {
        if (par1Str == null)
        {
            throw new NullPointerException("Attempt to construct a JsonNumber with a null value.");
        }
        else if (!PATTERN.matcher(par1Str).matches())
        {
            throw new IllegalArgumentException("Attempt to construct a JsonNumber with a String [" + par1Str + "] that does not match the JSON number specification.");
        }
        else
        {
            this.value = par1Str;
        }
    }

    public JsonNodeType getType()
    {
        return JsonNodeType.NUMBER;
    }

    public String getText()
    {
        return this.value;
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

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            JsonNumberNode var2 = (JsonNumberNode)par1Obj;
            return this.value.equals(var2.value);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.value.hashCode();
    }

    public String toString()
    {
        return "JsonNumberNode value:[" + this.value + "]";
    }
}
