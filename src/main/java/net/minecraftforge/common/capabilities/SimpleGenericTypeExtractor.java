package net.minecraftforge.common.capabilities;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

class SimpleGenericTypeExtractor extends SignatureVisitor
{

    String typeArgumentClass = null;
    private boolean seenTypeArgument = false;

    public SimpleGenericTypeExtractor()
    {
        super(Opcodes.ASM9);
    }

    @Override
    public void visitClassType(String name)
    {
        if (seenTypeArgument && this.typeArgumentClass == null)
        {
            this.typeArgumentClass = name;
        }
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        if (seenTypeArgument) {
            throw new IllegalStateException("Unexpected multiple arguments for generic signature");
        }
        if (wildcard != '=') {
            throw new IllegalStateException("Unexpected non-invariant type parameter for generic signature");
        }
        seenTypeArgument = true;
        return this;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
