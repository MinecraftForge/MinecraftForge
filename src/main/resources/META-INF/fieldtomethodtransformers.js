function initializeCoreMod() {
    return {
        'potion': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.potion.EffectInstance'
            },
            'transformer': function(classNode) {
                var asmapi=Java.type('net.minecraftforge.coremod.api.ASMAPI')
                var fn = asmapi.mapField('field_188420_b') // potion field - remap to mcp if necessary
                asmapi.redirectFieldToMethod(classNode, fn, asmapi.mapMethod('func_188419_a'))
                return classNode;
            }
        },
        'flowingfluidblock': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.block.FlowingFluidBlock'
            },
            'transformer': function(classNode) {
                var asmapi=Java.type('net.minecraftforge.coremod.api.ASMAPI')
                var fn = asmapi.mapField('field_204517_c') // fluid field - remap to mcp if necessary
                asmapi.redirectFieldToMethod(classNode, fn, 'getFluid') // forge added method, doesn't need mapping
                return classNode;
            }
        },
        'bucketitem': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.BucketItem'
            },
            'transformer': function(classNode) {
                var asmapi=Java.type('net.minecraftforge.coremod.api.ASMAPI')
                var fn = asmapi.mapField('field_77876_a') // containerFluid (wrongly named containedBlock) field - remap to mcp if necessary
                asmapi.redirectFieldToMethod(classNode, fn, 'getFluid') // forge added method, doesn't need mapping
                return classNode;
            }
        }
    }
}
