var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')

function initializeCoreMod() {
    return {
        'minecraft': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.Minecraft',
                'methodName': '<init>',
                'methodDesc': '(Lnet/minecraft/client/main/GameConfig;)V'
            },
            'transformer': function(methodNode) {
                //print(ASMAPI.methodNodeToString(methodNode))
                var meth = ASMAPI.getMethodNode();
                var resourcePackRepository = ASMAPI.mapField("f_91038_")
                var resourceManager = ASMAPI.mapField("f_91036_")

                meth.visitVarInsn(Opcodes.ALOAD, 0);
                meth.visitVarInsn(Opcodes.ALOAD, 0);
                meth.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", resourcePackRepository, "Lnet/minecraft/server/packs/repository/PackRepository;");
                meth.visitVarInsn(Opcodes.ALOAD, 0);
                meth.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", resourceManager, "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;");
                meth.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/fmlonlyclient/ClientModLoader", "begin", "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/server/packs/resources/ReloadableResourceManager;)V", false)
                for (var i=0; i<methodNode.instructions.size(); i++) {
                    var ain = methodNode.instructions.get(i);
                    if (ain.getOpcode() == Opcodes.PUTFIELD && ain.name.equals(resourceManager)) {
                        methodNode.instructions.insert(ain, meth.instructions);
                        break;
                    }
                }
                return methodNode;
            }
        },
    }
}
