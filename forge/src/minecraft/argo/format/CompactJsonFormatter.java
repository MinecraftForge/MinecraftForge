package argo.format;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.TreeSet;

public final class CompactJsonFormatter implements JsonFormatter
{
    public String format(JsonRootNode par1JsonRootNode)
    {
        StringWriter var2 = new StringWriter();

        try
        {
            this.format(par1JsonRootNode, var2);
        }
        catch (IOException var4)
        {
            throw new RuntimeException("Coding failure in Argo:  StringWriter gave an IOException", var4);
        }

        return var2.toString();
    }

    public void format(JsonRootNode par1JsonRootNode, Writer par2Writer) throws IOException
    {
        this.formatJsonNode(par1JsonRootNode, par2Writer);
    }

    private void formatJsonNode(JsonNode par1JsonNode, Writer par2Writer) throws IOException
    {
        boolean var3 = true;
        Iterator var4;

        switch (CompactJsonFormatter_JsonNodeType.enumJsonNodeTypeMappingArray[par1JsonNode.getType().ordinal()])
        {
            case 1:
                par2Writer.append('[');
                var4 = par1JsonNode.getElements().iterator();

                while (var4.hasNext())
                {
                    JsonNode var6 = (JsonNode)var4.next();

                    if (!var3)
                    {
                        par2Writer.append(',');
                    }

                    var3 = false;
                    this.formatJsonNode(var6, par2Writer);
                }

                par2Writer.append(']');
                break;

            case 2:
                par2Writer.append('{');
                var4 = (new TreeSet(par1JsonNode.getFields().keySet())).iterator();

                while (var4.hasNext())
                {
                    JsonStringNode var5 = (JsonStringNode)var4.next();

                    if (!var3)
                    {
                        par2Writer.append(',');
                    }

                    var3 = false;
                    this.formatJsonNode(var5, par2Writer);
                    par2Writer.append(':');
                    this.formatJsonNode((JsonNode)par1JsonNode.getFields().get(var5), par2Writer);
                }

                par2Writer.append('}');
                break;

            case 3:
                par2Writer.append('\"').append((new JsonEscapedString(par1JsonNode.getText())).toString()).append('\"');
                break;

            case 4:
                par2Writer.append(par1JsonNode.getText());
                break;

            case 5:
                par2Writer.append("false");
                break;

            case 6:
                par2Writer.append("true");
                break;

            case 7:
                par2Writer.append("null");
                break;

            default:
                throw new RuntimeException("Coding failure in Argo:  Attempt to format a JsonNode of unknown type [" + par1JsonNode.getType() + "];");
        }
    }
}
