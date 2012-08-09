package cpw.mods.fml.common.discovery.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class ModFieldVisitor extends FieldVisitor
{

    private String fieldName;
    private ASMModParser discoverer;

    public ModFieldVisitor(String name, ASMModParser discoverer)
    {
        super(Opcodes.ASM4);
        this.fieldName = name;
        this.discoverer = discoverer;
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        discoverer.startFieldAnnotation(fieldName, annotationName);
        return new ModAnnotationVisitor(discoverer);
    }
}
