package net.minecraftforge.client.model;

public interface ICameraTransformations
{
    ISimpleModelTransformation getThirdPerson();

    ISimpleModelTransformation getFirstPerson();

    ISimpleModelTransformation getHead();

    ISimpleModelTransformation getGui();
}
