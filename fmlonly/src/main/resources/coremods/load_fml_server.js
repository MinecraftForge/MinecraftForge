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

                var methOptions = ASMAPI.getMethodNode();
                methOptions.visitIntInsn(Opcodes.ALOAD, 1);
                methOptions.visitMethodInsn(Opcodes.INVOKESTATIC, 'net/minecraftforge/fmlonlyserver/ServerModLoader', 'addOptions', '(Ljoptsimple/OptionParser;)V', false)

                for (var i=0; i<methodNode.instructions.size(); i++) {
                    var ain = methodNode.instructions.get(i);
                    if (ain.getOpcode() == Opcodes.INVOKESPECIAL && ain.owner == 'joptsimple/OptionParser' && ain.name == '<init>' && ain.desc == '()V') {
                        // Inject after the STORE
                        methodNode.instructions.insert(methodNode.instructions.get(i + 1), methOptions.instructions);
                    } else if (ain.getOpcode() == Opcodes.INVOKESTATIC && ain.owner == 'net/minecraft/Util' && ain.name == ASMAPI.mapMethod('m_137584_') /* startTimerHackThread */ && ain.desc == '()V') {
                        methodNode.instructions.insert(ain, meth.instructions);
                        break; // By this point we've already reached the first injection point so break
                    }
                }
                return methodNode;
            }
        },
    }
}
