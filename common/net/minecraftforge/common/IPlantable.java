package net.minecraftforge.common;

import java.util.EnumSet;

public interface IPlantable {
    public EnumSet<EnumGroundType> getValidGround();
    public void setValidGround(EnumSet<EnumGroundType> types);
}
