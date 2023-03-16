var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var Handle = Java.type('org.objectweb.asm.Handle');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

function finalizeSpawnNode(node){
    return new MethodInsnNode(
        Opcodes.INVOKESTATIC, 
        "net/minecraftforge/event/ForgeEventFactory", 
        "onFinalizeSpawn", 
        "(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;", 
        false);
}

function contains(list, target) {
    for(var i = 0; i < list.length; i++) {
        if(list[i] == target) return true;
    }
    return false;
}

function search(className, node, replacements) {
    for(var i = 0; i < replacements.length; i++){
        var r = replacements[i];
        if(contains(r.targets, className) && node.getOpcode() == r.opcode && node.name == r.name && node.desc == r.desc) {
            return r;
        }
    }
    return null;
}

var replacements = [
    {
        'opcode': Opcodes.INVOKEVIRTUAL,
        'name': ASMAPI.mapMethod('m_6518_'),
        'desc': '(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;',
        'targets': 'coremods/finalize_spawn_targets.json',
        'factory': finalizeSpawnNode
    }
];

function initializeCoreMod() {
    var mergedTargets = [];
    for(var i = 0; i < replacements.length; i++){
        var r = replacements[i];
        r.targets = ASMAPI.loadData(r.targets);
        for(var k = 0; k < r.targets.length; k++){
            mergedTargets.push(r.targets[k]);
        }
    }

    return {
        'forge_method_redirector': {
            'target': {
                'type': 'CLASS',
                'names': function(listofclasses) {
                    return mergedTargets
                }
            },
            'transformer': function(classNode) {
                var methods = classNode.methods;
                var count = 0;
                for(var i = 0; i < methods.size(); i++){
                    var instr = methods.get(i).instructions;
                    for(var ix = 0; ix < instr.size(); ix++){
                        var node = instr.get(ix);
                        var temp = search(classNode.name, node, replacements);
                        if (temp != null) {
                            instr.set(node, temp.factory(node));
                        }
                    }
                }
                return classNode;
            }
        }
    }
}