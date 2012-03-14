package argo.jdom;

import java.util.List;
import java.util.Map;

public abstract class JsonNode
{
    public abstract JsonNodeType getType();

    public abstract String getText();

    /**
     * return the fields associated with this node
     */
    public abstract Map getFields();

    public abstract List getElements();

    /**
     * Gets a String by navigating the hierarchy below this node.
     */
    public final String getStringValue(Object ... par1ArrayOfObj)
    {
        return (String)this.wrapExceptionsFor(JsonNodeSelectors.func_27349_a(par1ArrayOfObj), this, par1ArrayOfObj);
    }

    /**
     * Gets a List of JsonNodes, representing a JSON array, by navigating the hierarchy below this node.
     */
    public final List getArrayNode(Object ... par1ArrayOfObj)
    {
        return (List)this.wrapExceptionsFor(JsonNodeSelectors.func_27346_b(par1ArrayOfObj), this, par1ArrayOfObj);
    }

    private Object wrapExceptionsFor(JsonNodeSelector par1JsonNodeSelector, JsonNode par2JsonNode, Object[] par3ArrayOfObj)
    {
        try
        {
            return par1JsonNodeSelector.getValue(par2JsonNode);
        }
        catch (JsonNodeDoesNotMatchChainedJsonNodeSelectorException var5)
        {
            throw JsonNodeDoesNotMatchPathElementsException.jsonNodeDoesNotMatchPathElementsException(var5, par3ArrayOfObj, JsonNodeFactories.aJsonArray(new JsonNode[] {par2JsonNode}));
        }
    }
}
