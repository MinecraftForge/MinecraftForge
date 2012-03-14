package net.minecraft.src;

public enum EnumRarity
{
    common(15, "Common"),
    uncommon(14, "Uncommon"),
    rare(11, "Rare"),
    epic(13, "Epic");
    public final int field_40535_e;
    public final String field_40532_f;

    private EnumRarity(int par3, String par4Str)
    {
        this.field_40535_e = par3;
        this.field_40532_f = par4Str;
    }
}
