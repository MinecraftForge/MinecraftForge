package net.minecraft.client.shader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.util.JsonException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL20;

@SideOnly(Side.CLIENT)
public class ShaderLinkHelper
{
    private static final Logger field_148080_a = LogManager.getLogger();
    private static ShaderLinkHelper field_148079_b;
    private static final String __OBFID = "CL_00001045";

    public static void func_148076_a()
    {
        field_148079_b = new ShaderLinkHelper();
    }

    public static ShaderLinkHelper func_148074_b()
    {
        return field_148079_b;
    }

    public void func_148077_a(ShaderManager p_148077_1_)
    {
        p_148077_1_.func_147994_f().func_148054_b(p_148077_1_);
        p_148077_1_.func_147989_e().func_148054_b(p_148077_1_);
        GL20.glDeleteProgram(p_148077_1_.func_147986_h());
    }

    public int func_148078_c() throws JsonException
    {
        int i = GL20.glCreateProgram();

        if (i <= 0)
        {
            throw new JsonException("Could not create shader program (returned program ID " + i + ")");
        }
        else
        {
            return i;
        }
    }

    public void func_148075_b(ShaderManager p_148075_1_)
    {
        p_148075_1_.func_147994_f().func_148056_a(p_148075_1_);
        p_148075_1_.func_147989_e().func_148056_a(p_148075_1_);
        GL20.glLinkProgram(p_148075_1_.func_147986_h());
        int i = GL20.glGetProgrami(p_148075_1_.func_147986_h(), 35714);

        if (i == 0)
        {
            field_148080_a.warn("Error encountered when linking program containing VS " + p_148075_1_.func_147989_e().func_148055_a() + " and FS " + p_148075_1_.func_147994_f().func_148055_a() + ". Log output:");
            field_148080_a.warn(GL20.glGetProgramInfoLog(p_148075_1_.func_147986_h(), 32768));
        }
    }
}