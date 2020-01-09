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
        },
        'stairsblock': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.block.StairsBlock'
            },
            'transformer': function(classNode) {
                var asmapi=Java.type('net.minecraftforge.coremod.api.ASMAPI')
                var blockField = asmapi.mapField('field_150149_b') // modelBlock - remap to mcp if necessary
                asmapi.redirectFieldToMethod(classNode, blockField, 'getModelBlock') // forge added method, doesn't need mapping
                var stateField = asmapi.mapField('field_150151_M') // modelState - remap to mcp if necessary
                asmapi.redirectFieldToMethod(classNode, stateField, 'getModelState') // forge added method, doesn't need mapping
                return classNode;
            }
        },
        'flowerpotblock': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.block.FlowerPotBlock'
            },
            'transformer': function(classNode) {
                var asmapi=Java.type('net.minecraftforge.coremod.api.ASMAPI')
                var fn = asmapi.mapField('field_196452_c') // flower - remap to mcp if necessary
                asmapi.redirectFieldToMethod(classNode, fn, asmapi.mapMethod('func_220276_d'))
                return classNode;
            }
        },
        'fishbucketitem': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.FishBucketItem'
            },
            'transformer': function(classNode) {
                var asmapi=Java.type('net.minecraftforge.coremod.api.ASMAPI')
                var fn = asmapi.mapField('field_203794_a') // fishType - remap to mcp if necessary
                asmapi.redirectFieldToMethod(classNode, fn, asmapi.mapMethod('getFishType'))
                return classNode;
            }
        }
    }
}
