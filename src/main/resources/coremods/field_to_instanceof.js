var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')

function initializeCoreMod() {
    var data = ASMAPI.loadData('coremods/field_to_instanceof.json')
    //ASMAPI.log('DEBUG', JSON.stringify(data, null, 2))
    
    var ret = {}
	for (name in data) {
		addTargets(ret, name, data[name])
	}
    return ret
}

function addTargets(ret, name, data) {
	var owner = data.cls
    var field = ASMAPI.mapField(data.name)
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
                return transform(node, entry.owner, owner, field, replacement)
            }}(entry) // We have to do this annoying double function imediate call, so that JS will capture the entry value
        }
    }
}

function transform(node, parent, owner, field, replacement) {
    var count = 0
    var last = null
    for (x = 0; x < node.instructions.size(); x++) {
        var current = node.instructions.get(x)
        if (current.getOpcode() == Opcodes.IF_ACMPEQ || current.getOpcode() == Opcodes.IF_ACMPNE) {
            if (last != null && (last.getOpcode() == Opcodes.GETSTATIC || last.getOpcode() == Opcodes.GETFIELD)) {
                if (last.owner.equals(owner) && last.name.equals(field)) {
                    node.instructions.set(last, new org.objectweb.asm.tree.TypeInsnNode(Opcodes.INSTANCEOF, replacement))
                    node.instructions.set(current, new org.objectweb.asm.tree.JumpInsnNode(current.getOpcode() == Opcodes.IF_ACMPEQ ? Opcodes.IFNE: Opcodes.IFEQ, current.label))
                    count++
                }
            }
        }
        last = current
    }
    //ASMAPI.log('DEBUG', 'Transforming: ' + parent + '.' + node.name + node.desc)
    //ASMAPI.log('DEBUG', 'field_to_instance: Replaced {} checks', count)
    return node
}
