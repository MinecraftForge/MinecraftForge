package net.minecraftforge.datafix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This delegate allows for rebuilding of the cached rule, which is needed since it is
 */
class ForgeDataFixDelegate extends DataFix
{

    private static final Method makeRuleMethod = ObfuscationReflectionHelper.findMethod(
      DataFix.class,
      "makeRule"
    );

    private final DataFix wrapped;

    @Nullable
    private TypeRewriteRule wrappedRule;

    ForgeDataFixDelegate(final DataFix wrapped)
    {
        //We pass in dummy data to the super type.
        //However we can not pass in null to the super type for the output schema, since this would cause an NPE.
        //Luckily ForgeSchema has this option while preserving the version key.
        //So we pass it that, for good measure.
        super(new ForgeSchema(wrapped.getVersionKey(), null, null), false);
        this.wrapped = wrapped;
    }

    @Override
    public TypeRewriteRule getRule()
    {
        //Check if OUR cached rule is available.
        if (this.wrappedRule == null)
        {
            //Not available get a new one.
            this.wrappedRule = rebuildRuleReflectively();
        }

        //Cached version available, return it.
        return wrappedRule;
    }

    /**
     * Rebuilds the rule using reflection.
     * This is needed since "makeRule" is protected in DataFix.
     * And "getRule" returns a potentially cached version, which is explicitly not what we want,
     * since that cached version might contain an older invalid schema from before a rebake.
     *
     * @return The type rewrite rule as defined by "makeRule" in the wrapped DataFix.
     */
    private TypeRewriteRule rebuildRuleReflectively()
    {
        try
        {
            //Just invoke it via reflection.
            final Object candidate = makeRuleMethod.invoke(this.wrapped);
            //Check if we have a rule.
            if (candidate instanceof TypeRewriteRule typeRewriteRule)
            {
                //Yep, returning.
                return typeRewriteRule;
            }

            //WTF Who put this here? Who is returning a none TypeRewriteRule from a method that should return it.!
            throw new IllegalStateException("Failed to get a rewrite rule. The returned object was not a TypeRewriteRule!"); //Should never be reached.
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalStateException("Could not get or invoke makeRule() Method on data fixer!", e);
        }
    }

    /**
     * Allows for resetting of the cached rule.
     */
    public void resetRule()
    {
        this.wrappedRule = null;
    }

    /**
     * Returns the wrapped datafix originally cached rule.
     * Does not rebuild it!.
     *
     * Is also irrelevant since we are not using this value anyway and is only needed to prevent NPEs in the super class.
     *
     * @return The value.
     */
    @Override
    protected TypeRewriteRule makeRule()
    {
        return wrapped.getRule();
    }
}
