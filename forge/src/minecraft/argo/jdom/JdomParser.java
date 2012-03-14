package argo.jdom;

import argo.saj.InvalidSyntaxException;
import argo.saj.SajParser;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JdomParser
{
    /**
     * Parse the character stream from the specified Reader into a JsonRootNode object.
     */
    public JsonRootNode parse(Reader par1Reader) throws InvalidSyntaxException, IOException
    {
        JsonListenerToJdomAdapter var2 = new JsonListenerToJdomAdapter();
        (new SajParser()).parse(par1Reader, var2);
        return var2.getDocument();
    }

    /**
     * Parse the specified JSON String
     */
    public JsonRootNode parse(String par1Str) throws InvalidSyntaxException
    {
        try
        {
            JsonRootNode var2 = this.parse(new StringReader(par1Str));
            return var2;
        }
        catch (IOException var4)
        {
            throw new RuntimeException("Coding failure in Argo:  StringWriter gave an IOException", var4);
        }
    }
}
