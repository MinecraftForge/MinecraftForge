package cpw.mods.fml.common;

import org.objectweb.asm.tree.ClassNode;

public interface IASMHook {
    /**
     * Inject the {@link Mod} class node into this instance. This allows retrieval from custom
     * attributes or other artifacts in your mod class
     *
     * @param modClassNode The mod class
     * @return optionally a code generated class that will be injected into the classloader
     */
    ClassNode inject(ClassNode modClassNode);
    /**
     * Allow mods to manipulate classes loaded from this {@link Mod}'s jar file. The {@link Mod}
     * class is always guaranteed to be called first
     *
     * @param node The class being loaded
     */
    void modifyClass(String className, ClassNode node);
}
