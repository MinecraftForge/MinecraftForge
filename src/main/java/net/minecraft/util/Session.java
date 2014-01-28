package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Session
{
    private final String username;
    private final String field_148257_b;
    private final String field_148258_c;
    private static final String __OBFID = "CL_00000659";

    public Session(String p_i45006_1_, String p_i45006_2_, String p_i45006_3_)
    {
        if (p_i45006_1_ == null || p_i45006_1_.isEmpty())
        {
            p_i45006_1_ = "MissingName";
            p_i45006_2_ = p_i45006_3_ = "NotValid";
            System.out.println("=========================================================");
            System.out.println("Warning the username was not set for this session, typically");
            System.out.println("this means you installed Forge incorrectly. We have set your");
            System.out.println("name to \"MissingName\" and your session to nothing. Please");
            System.out.println("check your instation and post a console log from the launcher");
            System.out.println("when asking for help!");
            System.out.println("=========================================================");
            
        }
        this.username = p_i45006_1_;
        this.field_148257_b = p_i45006_2_;
        this.field_148258_c = p_i45006_3_;
    }

    public String getSessionID()
    {
        return "token:" + this.field_148258_c + ":" + this.field_148257_b;
    }

    public String func_148255_b()
    {
        return this.field_148257_b;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String func_148254_d()
    {
        return this.field_148258_c;
    }

    public GameProfile func_148256_e()
    {
        return new GameProfile(this.func_148255_b(), this.getUsername());
    }
}