package net.minecraftforge.datafix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ForgeDataFixDelegate extends DataFix
{
    private final DataFix wrapped;

    @Nullable
    private TypeRewriteRule wrappedRule;

    ForgeDataFixDelegate(final DataFix wrapped)
    {
        super(null, false);
        this.wrapped = wrapped;
    }

    @Override
    public TypeRewriteRule getRule()
    {
        if (this.wrappedRule == null) {
            this.wrappedRule = rebuildRuleReflectively();
        }

        return wrappedRule;
    }

    private TypeRewriteRule rebuildRuleReflectively() {
        final Class<? extends DataFix> dataFixClass = this.wrapped.getClass();
        try
        {
            final Method makeRuleMethod = dataFixClass.getDeclaredMethod("makeRule");
            makeRuleMethod.setAccessible(true);
            final Object candidate = makeRuleMethod.invoke(this.wrapped);
            if (candidate instanceof TypeRewriteRule typeRewriteRule) {
                return typeRewriteRule;
            }

            throw new IllegalStateException("Failed to get a rewrite rule. The returned object was not a TypeRewriteRule!"); //Should never be reached.
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalStateException("Could not get or invoke makeRule() Method on data fixer!", e);
        }
    }

    public void resetRule() {
        this.wrappedRule = null;
    }

    @Override
    protected TypeRewriteRule makeRule()
    {
        return wrapped.getRule();
    }
}
