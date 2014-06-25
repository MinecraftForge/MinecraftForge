package cpw.mods.fml.common.asm.transformers;

import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ModAccessTransformer extends AccessTransformer {
    private static List<AccessTransformer> embedded = Lists.newArrayList();
    public ModAccessTransformer() throws IOException
    {
        super(ModAccessTransformer.class);
        for (AccessTransformer at : embedded)
        {
            mergeModifiers(at.getModifiers());
        }
    }

    private void mergeModifiers(Multimap<String, Modifier> modifiers)
    {
        getModifiers().putAll(modifiers);
    }

    public static void addJar(JarFile jar) throws IOException
    {
        AccessTransformer at = new AccessTransformer(jar);
        if (!at.isEmpty())
        {
            embedded.add(at);
        }
    }

}
