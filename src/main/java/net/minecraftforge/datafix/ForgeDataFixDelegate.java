/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.datafix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This delegate allows for rebuilding of the cached rule, which is needed since it is
 * cached in {@link DataFix} and the recompute of schema will need to invalidate this cached
 * type rewrite rule.
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
            //Check if we have a rule, this is just a security barrier to just crash hard if in the future the schema changes,
            //so that this is noted during the initial porting phase.
            if (candidate instanceof TypeRewriteRule typeRewriteRule)
            {
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
