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

package cpw.mods.fml.common;

import org.objectweb.asm.tree.ClassNode;

public interface IASMHook {
    /**
     * Inject the {@link Mod} class node into this instance. This allows retrieval from custom
     * attributes or other artifacts in your mod class
     *
     * @param modClassNode The mod class
     * @return optionally some code generated classes that will be injected into the classloader
     */
    ClassNode[] inject(ClassNode modClassNode);
    /**
     * Allow mods to manipulate classes loaded from this {@link Mod}'s jar file. The {@link Mod}
     * class is always guaranteed to be called first.
     * The node state should be changed in place.
     *
     * @param node The class being loaded
     */
    void modifyClass(String className, ClassNode node);
}
