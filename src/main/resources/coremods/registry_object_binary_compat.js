var Opcodes = Java.type('org.objectweb.asm.Opcodes')
var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')

// TODO 1.19: Delete this whole coremod. We won't need to maintain binary compatibility then.
function initializeCoreMod() {
    return {
        'registry_object_binary_compat': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraftforge.registries.RegistryObject'
            },
            'transformer': function (classNode) {
                var getNode = new MethodNode(Opcodes.ACC_PUBLIC, 'get', '()Lnet/minecraftforge/registries/IForgeRegistryEntry;', null, null)
                getNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0))
                getNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, 'net/minecraftforge/registries/RegistryObject', 'get', '()Ljava/lang/Object;'))
                getNode.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, 'net/minecraftforge/registries/IForgeRegistryEntry'))
                getNode.instructions.add(new InsnNode(Opcodes.ARETURN))

                var orElseNode = new MethodNode(Opcodes.ACC_PUBLIC, 'orElse', '(Lnet/minecraftforge/registries/IForgeRegistryEntry;)Lnet/minecraftforge/registries/IForgeRegistryEntry;', null, null)
                orElseNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0))
                orElseNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1))
                orElseNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, 'net/minecraftforge/registries/RegistryObject', 'orElse', '(Ljava/lang/Object;)Ljava/lang/Object;'))
                orElseNode.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, 'net/minecraftforge/registries/IForgeRegistryEntry'))
                orElseNode.instructions.add(new InsnNode(Opcodes.ARETURN))

                var orElseGetNode = new MethodNode(Opcodes.ACC_PUBLIC, 'orElseGet', '(Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/IForgeRegistryEntry;', '(Ljava/util/function/Supplier<+TT;>;)Lnet/minecraftforge/registries/IForgeRegistryEntry;', null)
                orElseGetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0))
                orElseGetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1))
                orElseGetNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, 'net/minecraftforge/registries/RegistryObject', 'orElseGet', '(Ljava/util/function/Supplier;)Ljava/lang/Object;'))
                orElseGetNode.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, 'net/minecraftforge/registries/IForgeRegistryEntry'))
                orElseGetNode.instructions.add(new InsnNode(Opcodes.ARETURN))

                // orElseThrow
                var orElseThrowNode = new MethodNode(Opcodes.ACC_PUBLIC, 'orElseThrow', '(Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/IForgeRegistryEntry;', '\<\X:Ljava/lang/Throwable;>(Ljava/util/function/Supplier<+TX;>;)Lnet/minecraftforge/registries/IForgeRegistryEntry;^TX;', ['java/lang/Throwable'])
                orElseThrowNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0))
                orElseThrowNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1))
                orElseThrowNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, 'net/minecraftforge/registries/RegistryObject', 'orElseThrow', '(Ljava/util/function/Supplier;)Ljava/lang/Object;'))
                orElseThrowNode.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, 'net/minecraftforge/registries/IForgeRegistryEntry'))
                orElseThrowNode.instructions.add(new InsnNode(Opcodes.ARETURN))

                classNode.methods.add(getNode)
                classNode.methods.add(orElseNode)
                classNode.methods.add(orElseGetNode)
                classNode.methods.add(orElseThrowNode)

                return classNode
            }
        }
    }
}