package cpw.mods.fml.common.asm.transformers;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.IClassTransformer;
import cpw.mods.fml.relauncher.SideOnly;

public class SideTransformer implements IClassTransformer
{
    private static String SIDE = FMLRelauncher.side();
    private static final boolean DEBUG = false;
    @SuppressWarnings("unchecked")
    @Override
    public byte[] transform(String name, byte[] bytes)
    {
    	if (bytes == null) { return null; }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        if (remove((List<AnnotationNode>)classNode.visibleAnnotations, SIDE))
        {
            if (DEBUG)
            {
                System.out.println(String.format("Attempted to load class %s for invalid side %s", classNode.name, SIDE));
            }
            throw new RuntimeException(String.format("Attempted to load class %s for invalid side %s", classNode.name, SIDE));
        }

        Iterator<FieldNode> fields = classNode.fields.iterator();
        while(fields.hasNext())
        {
            FieldNode field = fields.next();
            if (remove((List<AnnotationNode>)field.visibleAnnotations, SIDE))
            {
                if (DEBUG)
                {
                    System.out.println(String.format("Removing Field: %s.%s", classNode.name, field.name));
                }
                fields.remove();
            }
        }
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext())
        {
            MethodNode method = methods.next();
            if (remove((List<AnnotationNode>)method.visibleAnnotations, SIDE))
            {
                if (DEBUG)
                {
                    System.out.println(String.format("Removing Method: %s.%s%s", classNode.name, method.name, method.desc));
                }
                methods.remove();
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    
    private boolean remove(List<AnnotationNode> anns, String side)
    {
        if (anns == null)
        {
            return false;
        }
        for (AnnotationNode ann : anns)
        {
            if (ann.desc.equals(Type.getDescriptor(SideOnly.class)))
            {
                if (ann.values != null)
                {
                    for (int x = 0; x < ann.values.size() - 1; x += 2)
                    {
                        Object key = ann.values.get(x);
                        Object value = ann.values.get(x+1);
                        if (key instanceof String && key.equals("value"))
                        {
                            if (value instanceof String[] )
                            {
                                if (!((String[])value)[1].equals(side))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
