package net.minecraft.entity;

public interface IEntityOwnable
{
    String getOwnerName();

    Entity getOwner();
}