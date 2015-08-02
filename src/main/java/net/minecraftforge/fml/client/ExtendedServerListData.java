package net.minecraftforge.fml.client;

import java.util.Map;

public class ExtendedServerListData {
    public final String type;
    public final boolean isCompatible;
    public final Map<String,String> modData;
    public final boolean isBlocked;

    public ExtendedServerListData(String type, boolean isCompatible, Map<String,String> modData, boolean isBlocked)
    {
        this.type = type;
        this.isCompatible = isCompatible;
        this.modData = modData;
        this.isBlocked = isBlocked;
    }
}
