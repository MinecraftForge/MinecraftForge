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
                asmapi.redirectFieldToMethod(classNode, fn, 'getPotionRaw')
                return classNode;
            }
        }
    }
}
