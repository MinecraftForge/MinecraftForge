var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')
var AbstractInsnNode = Java.type('org.objectweb.asm.tree.AbstractInsnNode')

function initializeCoreMod() {
    var data = ASMAPI.loadData('coremods/stack_sensitive.json')
    //ASMAPI.log('DEBUG', JSON.stringify(data, null, 2))

    var ret = {}
    for (name in data) {
        addTargets(ret, name, data[name])
    }
    return ret
}

function addTargets(ret, name, data) {
    var owner = data.cls
    var method = ASMAPI.mapMethod(data.name)
    var replacement = data.replacement
    for (x = 0; x < data.targets.length; x++) {
        var key = name + '.' + x
        var entry = data.targets[x]

        //ASMAPI.log('DEBUG', 'Entry ' + key + ' ' + JSON.stringify(entry))

        ret[key] = {
            'target': {
                'type': 'METHOD',
                'class': entry.owner,
                'methodName': entry.name,
                'methodDesc': entry.desc
            },
            'transformer': function(entry) { return function(node) {
                return transform(node, entry, owner, method, replacement)
            }}(entry) // We have to do this annoying double function imediate call, so that JS will capture the entry value
        }
    }
}

function transform(node, entry, owner, method, replacement) {
    var count = 0
    for (x = 0; x < node.instructions.size(); x++) {
        var current = node.instructions.get(x)
        if (current.getType() == AbstractInsnNode.METHOD_INSN) {
            if (current.name == method) {
                var returnType = current.desc.split(')').pop()
                node.instructions.insertBefore(current, new org.objectweb.asm.tree.VarInsnNode(Opcodes.ALOAD, entry.varIndex))
                node.instructions.set(current, new org.objectweb.asm.tree.MethodInsnNode(Opcodes.INVOKEINTERFACE, owner, replacement, "(Lnet/minecraft/world/item/ItemStack;)" + returnType))
                x++
                count++
            }
        }
    }
    //ASMAPI.log('DEBUG', 'Transforming: ' + entry.owner + '.' + node.name + node.desc)
    //ASMAPI.log('DEBUG', 'stack_sensitive: Replaced {} checks', count)
    return node
}
