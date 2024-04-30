var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')

function initializeCoreMod() {
    return {
        'biome': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.level.biome.Biome'
            },
            'transformer': function(classNode) {
                ASMAPI.redirectFieldToMethod(classNode, 'climateSettings', 'getModifiedClimateSettings')
                ASMAPI.redirectFieldToMethod(classNode, 'specialEffects', 'getModifiedSpecialEffects')
                return classNode;
            }
        },
        'potion': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.effect.MobEffectInstance'
            },
            'transformer': function(classNode) {
                ASMAPI.redirectFieldToMethod(classNode, 'effect', 'getEffect')
                return classNode;
            }
        },
        'flowing_fluid_block': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.level.block.LiquidBlock'
            },
            'transformer': function(classNode) {
                ASMAPI.redirectFieldToMethod(classNode, 'fluid', 'getFluid')
                return classNode;
            }
        },
        'bucketitem': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.item.BucketItem'
            },
            'transformer': function(classNode) {
                ASMAPI.redirectFieldToMethod(classNode, 'content', 'getFluid')
                return classNode;
            }
        },
        'flowerpotblock': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.level.block.FlowerPotBlock'
            },
            'transformer': function(classNode) {
                ASMAPI.redirectFieldToMethod(classNode, 'potted', 'getPotted')
                return classNode;
            }
        }
    }
}
