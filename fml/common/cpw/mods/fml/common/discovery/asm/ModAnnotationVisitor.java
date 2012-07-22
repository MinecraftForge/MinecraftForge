package cpw.mods.fml.common.discovery.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public class ModAnnotationVisitor extends AnnotationVisitor
{
    private ASMModParser discoverer;

    public ModAnnotationVisitor(ASMModParser discoverer)
    {
        super(Opcodes.ASM4);
        this.discoverer = discoverer;
    }
    
    @Override
    public void visit(String key, Object value)
    {
        discoverer.addAnnotationProperty(key, value);
    }
}
