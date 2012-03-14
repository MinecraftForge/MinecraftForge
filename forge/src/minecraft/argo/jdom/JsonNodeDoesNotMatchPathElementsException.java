package argo.jdom;

import argo.format.CompactJsonFormatter;
import argo.format.JsonFormatter;

public final class JsonNodeDoesNotMatchPathElementsException extends JsonNodeDoesNotMatchJsonNodeSelectorException
{
    private static final JsonFormatter JSON_FORMATTER = new CompactJsonFormatter();

    static JsonNodeDoesNotMatchPathElementsException jsonNodeDoesNotMatchPathElementsException(JsonNodeDoesNotMatchChainedJsonNodeSelectorException par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException, Object[] par1ArrayOfObj, JsonRootNode par2JsonRootNode)
    {
        return new JsonNodeDoesNotMatchPathElementsException(par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException, par1ArrayOfObj, par2JsonRootNode);
    }

    private JsonNodeDoesNotMatchPathElementsException(JsonNodeDoesNotMatchChainedJsonNodeSelectorException par1JsonNodeDoesNotMatchChainedJsonNodeSelectorException, Object[] par2ArrayOfObj, JsonRootNode par3JsonRootNode)
    {
        super(formatMessage(par1JsonNodeDoesNotMatchChainedJsonNodeSelectorException, par2ArrayOfObj, par3JsonRootNode));
    }

    private static String formatMessage(JsonNodeDoesNotMatchChainedJsonNodeSelectorException par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException, Object[] par1ArrayOfObj, JsonRootNode par2JsonRootNode)
    {
        return "Failed to find " + par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException.failedNode.toString() + " at [" + JsonNodeDoesNotMatchChainedJsonNodeSelectorException.getShortFormFailPath(par0JsonNodeDoesNotMatchChainedJsonNodeSelectorException.failPath) + "] while resolving [" + commaSeparate(par1ArrayOfObj) + "] in " + JSON_FORMATTER.format(par2JsonRootNode) + ".";
    }

    private static String commaSeparate(Object[] par0ArrayOfObj)
    {
        StringBuilder var1 = new StringBuilder();
        boolean var2 = true;
        Object[] var3 = par0ArrayOfObj;
        int var4 = par0ArrayOfObj.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            Object var6 = var3[var5];

            if (!var2)
            {
                var1.append(".");
            }

            var2 = false;

            if (var6 instanceof String)
            {
                var1.append("\"").append(var6).append("\"");
            }
            else
            {
                var1.append(var6);
            }
        }

        return var1.toString();
    }
}
