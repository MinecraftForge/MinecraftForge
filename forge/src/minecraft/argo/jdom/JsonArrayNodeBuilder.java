package argo.jdom;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class JsonArrayNodeBuilder implements JsonNodeBuilder
{
    private final List elementBuilders = new LinkedList();

    /**
     * Adds the given element to the array that will be built.
     */
    public JsonArrayNodeBuilder withElement(JsonNodeBuilder par1JsonNodeBuilder)
    {
        this.elementBuilders.add(par1JsonNodeBuilder);
        return this;
    }

    public JsonRootNode build()
    {
        LinkedList var1 = new LinkedList();
        Iterator var2 = this.elementBuilders.iterator();

        while (var2.hasNext())
        {
            JsonNodeBuilder var3 = (JsonNodeBuilder)var2.next();
            var1.add(var3.buildNode());
        }

        return JsonNodeFactories.aJsonArray(var1);
    }

    public JsonNode buildNode()
    {
        return this.build();
    }
}
