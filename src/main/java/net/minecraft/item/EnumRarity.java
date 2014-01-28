package net.minecraft.item;

import net.minecraft.util.EnumChatFormatting;

public enum EnumRarity
{
    common(EnumChatFormatting.WHITE, "Common"),
    uncommon(EnumChatFormatting.YELLOW, "Uncommon"),
    rare(EnumChatFormatting.AQUA, "Rare"),
    epic(EnumChatFormatting.LIGHT_PURPLE, "Epic");
    // JAVADOC FIELD $$ field_77937_e
    public final EnumChatFormatting rarityColor;
    // JAVADOC FIELD $$ field_77934_f
    public final String rarityName;

    private static final String __OBFID = "CL_00000056";

    private EnumRarity(EnumChatFormatting p_i45349_3_, String p_i45349_4_)
    {
        this.rarityColor = p_i45349_3_;
        this.rarityName = p_i45349_4_;
    }
}