package net.minecraftforge.datafix;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerUpper;

import java.util.concurrent.Executor;

/**
 * A handler which can update the data fixer, if the data fixer has support for this enabled.
 */
public class ForgeDataFixUpdateHandler
{

    private ForgeDataFixUpdateHandler()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ForgeDataFixUpdateHandler. This is a utility class");
    }

    /**
     * Update the data fixer on the given executor, if support for this is enabled.
     *
     * @param dataFixerUpper The data fixer to update.
     * @param executor The executor to update on.
     */
    public static void rebuildDataFixer(final DataFixer dataFixerUpper, final Executor executor) {
        if (dataFixerUpper instanceof ForgeDataFixerDelegateHandler forgeDataFixerDelegateHandler)
        {
            forgeDataFixerDelegateHandler.rebuildFixer(executor);
        }
    }
}
