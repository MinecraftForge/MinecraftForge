var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')

function initializeCoreMod() {
    return {
        "getLanguage_bouncer": addBouncer("net.minecraft.network.play.client.CClientSettingsPacket", "getLanguage", "func_227987_b_ ", false, "()V"),
    }
}

// TODO: hasArg -> compute actual list of arg instructions?
function addBouncer(class, conflictingName, srgName, hasArg, descriptor, signature) {
    if (signature == null)
        signature = descriptor;
    return {
       'target': {
           'type': 'CLASS',
           'name': class
       },
       'transformer': function(node) {
            var mappedName = ASMAPI.mapMethod(srgName);
            if (mappedName == conflictingName)
                return; // No work to do!

            var method = new MethodNode(
                /* access = */ Opcodes.ACC_PUBLIC,
                /* name = */ mappedName,
                /* descriptor = */ descriptor,
                /* signature = */ signature,
                /* exceptions = */ null
            );

            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); /*this*/
            if (hasArg)
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); /*first arg*/
            method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, class.replace(".","/"), mappedName, descriptor));
            method.instructions.add(new InsnNode(Opcodes.RETURN));

            classNode.methods.add(method);

            return classNode;
       }
   }
}