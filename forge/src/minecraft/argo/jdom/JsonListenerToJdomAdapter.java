package argo.jdom;

import argo.saj.JsonListener;
import java.util.Stack;

final class JsonListenerToJdomAdapter implements JsonListener
{
    private final Stack stack = new Stack();
    private JsonNodeBuilder root;

    JsonRootNode getDocument()
    {
        return (JsonRootNode)this.root.buildNode();
    }

    public void startDocument() {}

    public void endDocument() {}

    public void startArray()
    {
        JsonArrayNodeBuilder var1 = JsonNodeBuilders.anArrayBuilder();
        this.addRootNode(var1);
        this.stack.push(new JsonListenerToJdomAdapter_Array(this, var1));
    }

    public void endArray()
    {
        this.stack.pop();
    }

    public void startObject()
    {
        JsonObjectNodeBuilder var1 = JsonNodeBuilders.anObjectBuilder();
        this.addRootNode(var1);
        this.stack.push(new JsonListenerToJdomAdapter_Object(this, var1));
    }

    public void endObject()
    {
        this.stack.pop();
    }

    public void startField(String par1Str)
    {
        JsonFieldBuilder var2 = JsonFieldBuilder.aJsonFieldBuilder().withKey(JsonNodeBuilders.func_27254_b(par1Str));
        ((JsonListenerToJdomAdapter_NodeContainer)this.stack.peek()).addField(var2);
        this.stack.push(new JsonListenerToJdomAdapter_Field(this, var2));
    }

    public void endField()
    {
        this.stack.pop();
    }

    public void numberValue(String par1Str)
    {
        this.addValue(JsonNodeBuilders.func_27250_a(par1Str));
    }

    public void trueValue()
    {
        this.addValue(JsonNodeBuilders.func_27251_b());
    }

    public void stringValue(String par1Str)
    {
        this.addValue(JsonNodeBuilders.func_27254_b(par1Str));
    }

    public void falseValue()
    {
        this.addValue(JsonNodeBuilders.func_27252_c());
    }

    public void nullValue()
    {
        this.addValue(JsonNodeBuilders.func_27248_a());
    }

    private void addRootNode(JsonNodeBuilder par1JsonNodeBuilder)
    {
        if (this.root == null)
        {
            this.root = par1JsonNodeBuilder;
        }
        else
        {
            this.addValue(par1JsonNodeBuilder);
        }
    }

    private void addValue(JsonNodeBuilder par1JsonNodeBuilder)
    {
        ((JsonListenerToJdomAdapter_NodeContainer)this.stack.peek()).addNode(par1JsonNodeBuilder);
    }
}
