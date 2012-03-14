package argo.jdom;

import java.util.Arrays;

public final class JsonNodeSelectors
{
    public static JsonNodeSelector func_27349_a(Object ... par0ArrayOfObj)
    {
        return chainOn(par0ArrayOfObj, new JsonNodeSelector(new JsonNodeSelectors_String()));
    }

    public static JsonNodeSelector func_27346_b(Object ... par0ArrayOfObj)
    {
        return chainOn(par0ArrayOfObj, new JsonNodeSelector(new JsonNodeSelectors_Array()));
    }

    public static JsonNodeSelector func_27353_c(Object ... par0ArrayOfObj)
    {
        return chainOn(par0ArrayOfObj, new JsonNodeSelector(new JsonNodeSelectors_Object()));
    }

    public static JsonNodeSelector func_27348_a(String par0Str)
    {
        return func_27350_a(JsonNodeFactories.aJsonString(par0Str));
    }

    public static JsonNodeSelector func_27350_a(JsonStringNode par0JsonStringNode)
    {
        return new JsonNodeSelector(new JsonNodeSelectors_Field(par0JsonStringNode));
    }

    public static JsonNodeSelector func_27351_b(String par0Str)
    {
        return func_27353_c(new Object[0]).with(func_27348_a(par0Str));
    }

    public static JsonNodeSelector func_27347_a(int par0)
    {
        return new JsonNodeSelector(new JsonNodeSelectors_Element(par0));
    }

    public static JsonNodeSelector func_27354_b(int par0)
    {
        return func_27346_b(new Object[0]).with(func_27347_a(par0));
    }

    private static JsonNodeSelector chainOn(Object[] par0ArrayOfObj, JsonNodeSelector par1JsonNodeSelector)
    {
        JsonNodeSelector var2 = par1JsonNodeSelector;

        for (int var3 = par0ArrayOfObj.length - 1; var3 >= 0; --var3)
        {
            if (par0ArrayOfObj[var3] instanceof Integer)
            {
                var2 = chainedJsonNodeSelector(func_27354_b(((Integer)par0ArrayOfObj[var3]).intValue()), var2);
            }
            else
            {
                if (!(par0ArrayOfObj[var3] instanceof String))
                {
                    throw new IllegalArgumentException("Element [" + par0ArrayOfObj[var3] + "] of path elements" + " [" + Arrays.toString(par0ArrayOfObj) + "] was of illegal type [" + par0ArrayOfObj[var3].getClass().getCanonicalName() + "]; only Integer and String are valid.");
                }

                var2 = chainedJsonNodeSelector(func_27351_b((String)par0ArrayOfObj[var3]), var2);
            }
        }

        return var2;
    }

    private static JsonNodeSelector chainedJsonNodeSelector(JsonNodeSelector par0JsonNodeSelector, JsonNodeSelector par1JsonNodeSelector)
    {
        return new JsonNodeSelector(new ChainedFunctor(par0JsonNodeSelector, par1JsonNodeSelector));
    }
}
