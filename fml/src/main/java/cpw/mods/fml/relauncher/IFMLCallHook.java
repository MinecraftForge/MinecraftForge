/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.relauncher;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * This call hook allows for code to execute at the very early stages of
 * minecraft initialization. FML uses it to validate that there is a
 * safe environment for further loading of FML.
 *
 * @author cpw
 *
 */
public interface IFMLCallHook extends Callable<Void>
{
    /**
     * Injected with data from the FML environment:
     * "classLoader" : The FML Class Loader
     * @param data
     */
    void injectData(Map<String,Object> data);
}
