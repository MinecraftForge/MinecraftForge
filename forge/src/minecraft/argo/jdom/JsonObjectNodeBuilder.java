package argo.jdom;

import java.util.LinkedList;
import java.util.List;

public final class JsonObjectNodeBuilder implements JsonNodeBuilder
{
    private final List fieldBuilders = new LinkedList();

    public JsonObjectNodeBuilder withFieldBuilder(JsonFieldBuilder par1JsonFieldBuilder)
    {
        this.fieldBuilders.add(par1JsonFieldBuilder);
        return this;
    }

    public JsonRootNode func_27235_a()
    {
        return JsonNodeFactories.aJsonObject(new JsonObjectNodeBuilder_List(this));
    }

    public JsonNode buildNode()
    {
        return this.func_27235_a();
    }

    static List func_27236_a(JsonObjectNodeBuilder par0JsonObjectNodeBuilder)
    {
        return par0JsonObjectNodeBuilder.fieldBuilders;
    }
}
