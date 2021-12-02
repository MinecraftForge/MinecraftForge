package net.minecraftforge.datafix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        super(new ForgeSchema(wrapped.getVersionKey(), null, null), false);
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
        try
        {
            final Object candidate = makeRuleMethod.invoke(this.wrapped);
            if (candidate instanceof TypeRewriteRule typeRewriteRule) {
                return typeRewriteRule;
            }
            throw new IllegalStateException("Failed to get a rewrite rule. The returned object was not a TypeRewriteRule!"); //Should never be reached.
        }
        catch (IllegalAccessException | InvocationTargetException e)
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
