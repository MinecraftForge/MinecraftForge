package net.minecraftforge.debug.client.rendering;

import com.mojang.blaze3d.shaders.Program;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ShaderTransformEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ShaderTransformEventTest.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD , modid = ShaderTransformEventTest.MOD_ID)
public class ShaderTransformEventTest
{
    public static final String MOD_ID = "shader_transform_event_test";
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void transformShader(ShaderTransformEvent event)
    {
        if (ENABLED && event.getType() == Program.Type.FRAGMENT && event.getShaderName().equals("rendertype_text"))
        {
            event.transformShader((origin,transformed) ->
                    transformed.replace("}","\tfragColor *= vec4(sin(gl_FragCoord.xy/1800),0.5,0.2)*3;\n}")
            );
        }
    }

}
