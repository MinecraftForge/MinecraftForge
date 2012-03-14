package argo.jdom;

import java.util.LinkedList;
import java.util.List;

public final class JsonNodeDoesNotMatchChainedJsonNodeSelectorException extends JsonNodeDoesNotMatchJsonNodeSelectorException
{
    final Functor failedNode;
    final List failPath;

    static JsonNodeDoesNotMatchJsonNodeSelectorException func_27322_a(Functor par0Functor)
    {
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(par0Functor, new LinkedList());
    }

    static JsonNodeDoesNotMatchJsonNodeSelectorException func_27323_a(JsonNodeDoesNotMatchChainedJsonNodeSelectorException par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException, JsonNodeSelector par1JsonNodeSelector)
    {
        LinkedList var2 = new LinkedList(par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException.failPath);
        var2.add(par1JsonNodeSelector);
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException.failedNode, var2);
    }

    static JsonNodeDoesNotMatchJsonNodeSelectorException func_27321_b(JsonNodeDoesNotMatchChainedJsonNodeSelectorException par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException, JsonNodeSelector par1JsonNodeSelector)
    {
        LinkedList var2 = new LinkedList();
        var2.add(par1JsonNodeSelector);
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException.failedNode, var2);
    }

    private JsonNodeDoesNotMatchChainedJsonNodeSelectorException(Functor par1Functor, List par2List)
    {
        super("Failed to match any JSON node at [" + getShortFormFailPath(par2List) + "]");
        this.failedNode = par1Functor;
        this.failPath = par2List;
    }

    static String getShortFormFailPath(List par0List)
    {
        StringBuilder var1 = new StringBuilder();

        for (int var2 = par0List.size() - 1; var2 >= 0; --var2)
        {
            var1.append(((JsonNodeSelector)par0List.get(var2)).shortForm());

            if (var2 != 0)
            {
                var1.append(".");
            }
        }

        return var1.toString();
    }

    public String toString()
    {
        return "JsonNodeDoesNotMatchJsonNodeSelectorException{failedNode=" + this.failedNode + ", failPath=" + this.failPath + '}';
    }
}
