var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')
var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')

function initializeCoreMod() {
    return {
        "getLanguage_bouncer": addBouncer("net.minecraft.network.play.client.CClientSettingsPacket", "func_149524_c", "getLanguage", "()Ljava/lang/String;"),
        "compose_bouncer": addRedirect("net.minecraft.util.math.vector.TransformationMatrix", "func_227985_a_", "composeVanilla", "(Lnet/minecraft/util/math/vector/TransformationMatrix;)Lnet/minecraft/util/math/vector/TransformationMatrix;", 2),
        "inverse_bouncer": addRedirect("net.minecraft.util.math.vector.TransformationMatrix", "func_227987_b_", "inverseVanilla", "()Lnet/minecraft/util/math/vector/TransformationMatrix;", 1),
    }
}

function addBouncer(className, conflictedName, expectedName, descriptor, signature) {
    if (signature == null)
        signature = descriptor;
    return {
       'target': {
           'type': 'CLASS',
           'name': className
       },
       'transformer': function(node) {
            var mappedName = ASMAPI.mapMethod(conflictedName);
            if (mappedName == expectedName)
                return node; // No work to do!

            var method = new MethodNode(
                /* access = */ Opcodes.ACC_PUBLIC,
                /* name = */ expectedName,
                /* descriptor = */ descriptor,
                /* signature = */ signature,
                /* exceptions = */ null
            );

            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); /*this*/
            method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, className.replaceAll("\\.","/"), mappedName, descriptor));
            method.instructions.add(new InsnNode(Opcodes.ARETURN));

            node.methods.add(method);

            return node;
       }
   }
}

// Generate a method with name oldName that redirects to expectedName. oldName is remapped, expectedName isn't.
function addRedirect(className, oldName, expectedName, descriptor, numArgs) {
    return {
       'target': {
           'type': 'CLASS',
           'name': className
       },
       'transformer': function(node) {
            var mappedName = ASMAPI.mapMethod(oldName);

            var method = new MethodNode(
                /* access = */ Opcodes.ACC_PUBLIC,
                /* name = */ mappedName,
                /* descriptor = */ descriptor,
                /* signature = */ descriptor,
                /* exceptions = */ null
            );

            for(var i = 0; i < numArgs; i++) {
                 method.instructions.add(new VarInsnNode(Opcodes.ALOAD, i));
            }
            method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, className.replaceAll("\\.","/"), expectedName, descriptor));
            method.instructions.add(new InsnNode(Opcodes.ARETURN));

            node.methods.add(method);

            return node;
       }
   }
}