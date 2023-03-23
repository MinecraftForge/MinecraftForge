var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')

function initializeCoreMod() {
    return {
        'minecraft': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.server.Main',
                'methodName': 'main',
                'methodDesc': '([Ljava/lang/String;)V'
            },
            'transformer': function(methodNode) {
                var meth = ASMAPI.getMethodNode();
                meth.visitMethodInsn(Opcodes.INVOKESTATIC, 'net/minecraftforge/fmlonlyserver/ServerModLoader', 'load', '()V', false)
                for (var i=0; i<methodNode.instructions.size(); i++) {
                    var ain = methodNode.instructions.get(i);
                    if (ain.getOpcode() == Opcodes.NEW && ain.desc == 'net/minecraft/server/dedicated/DedicatedServerSettings') {
                        methodNode.instructions.insert(ain, meth.instructions);
                        break;
                    }
                }
                return methodNode;
            }
        },
    }
}
