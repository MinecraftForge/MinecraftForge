var Opcodes = Java.type('org.objectweb.asm.Opcodes')
var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')

// TODO 1.20: Delete this whole coremod. We won't need to maintain binary compatibility then.
// This coremod exists purely because of Java limitations - 2 methods with the same parameters and name but different return types are not allowed.
// However, this restriction does not exist in the JVM.
// The two remove() methods had their return types narrowed to allow chaining calls.
// This coremod was made to maintain compatibility with mods compiled on old forge in the least invasive way.
// It adds runtime methods for the two methods in IForgeIntrinsicTagAppender that return TagsProvider.TagAppender.
// The ASM methods just delegate to the original methods.

function initializeCoreMod() {
    return {
        'intrinsic_tag_appender_binary_compat': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraftforge.common.extensions.IForgeIntrinsicHolderTagAppender'
            },
            'transformer': function (classNode) {
                var removeSingleNode = new MethodNode(
                    Opcodes.ACC_PUBLIC,
                    'remove',
                    '(Ljava/lang/Object;)Lnet/minecraft/data/tags/TagsProvider$TagAppender;',
                    null,
                    null
                );
                removeSingleNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                removeSingleNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                removeSingleNode.instructions.add(new MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    'net/minecraftforge/common/extensions/IForgeIntrinsicHolderTagAppender',
                    'remove',
                    '(Ljava/lang/Object;)Lnet/minecraft/data/tags/IntrinsicHolderTagsProvider$IntrinsicTagAppender;'
                ));
                removeSingleNode.instructions.add(new InsnNode(Opcodes.ARETURN));

                var removeMultiNode = new MethodNode(
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
                    'remove',
                    '(Ljava/lang/Object;[Ljava/lang/Object;)Lnet/minecraft/data/tags/TagsProvider$TagAppender;',
                    null,
                    null
                );
                removeMultiNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                removeMultiNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                removeMultiNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                removeMultiNode.instructions.add(new MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    'net/minecraftforge/common/extensions/IForgeIntrinsicHolderTagAppender',
                    'remove',
                    '(Ljava/lang/Object;[Ljava/lang/Object;)Lnet/minecraft/data/tags/IntrinsicHolderTagsProvider$IntrinsicTagAppender;'
                ));
                removeMultiNode.instructions.add(new InsnNode(Opcodes.ARETURN));

                classNode.methods.add(removeSingleNode);
                classNode.methods.add(removeMultiNode);

                return classNode;
            }
        }
    }
}