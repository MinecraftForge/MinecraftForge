var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')
var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')

function initializeCoreMod() {
    return {
        //TODO: Remove in 1.20.5+
        "getPosition_bouncer": addBouncer("net.minecraft.world.level.Explosion", "m_307721_", "getPosition", "()Lnet/minecraft/world/phys/Vec3;"),
        "getExploder_bouncer": addBouncer("net.minecraft.world.level.Explosion", "m_253049_", "getExploder", "()Lnet/minecraft/world/entity/Entity;"),
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