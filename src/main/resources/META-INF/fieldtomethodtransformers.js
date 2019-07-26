function initializeCoreMod() {
    return {
        'potion': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.potion.EffectInstance'
            },
            'transformer': function(classNode) {
                var asmapi=Java.type('net.minecraftforge.coremod.api.ASMAPI')
                asmapi.redirectFieldToMethod(classNode, 'potion', 'getPotionRaw')
                return classNode;
            }
        }
    }
}
