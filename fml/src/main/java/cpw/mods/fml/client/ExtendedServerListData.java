package cpw.mods.fml.client;

public class ExtendedServerListData {
    public final String type;
    public final boolean isCompatible;
    public final int modCount;
    public final boolean isBlocked;

    public ExtendedServerListData(String type, boolean isCompatible, int modCount, boolean isBlocked)
    {
        this.type = type;
        this.isCompatible = isCompatible;
        this.modCount = modCount;
        this.isBlocked = isBlocked;
    }
}
