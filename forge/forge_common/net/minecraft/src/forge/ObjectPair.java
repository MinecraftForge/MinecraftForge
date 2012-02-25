package net.minecraft.src.forge;

/**
 * A class that holds two generic values, can be used as a Key/Value pair,
 * but is used in forge as a frequency/object pair.
 *
 * @param <T1> The type of the first value
 * @param <T2> The Type of the second value
 */
public class ObjectPair<T1, T2>
{
    private T1 object1;
    private T2 object2;

    public ObjectPair(T1 obj1, T2 obj2)
    {
        this.object1 = obj1;
        this.object2 = obj2;
    }

    public T1 getValue1()
    {
        return this.object1;
    }

    public T2 getValue2()
    {
        return this.object2;
    }

    public void setValue1(T1 value)
    {
        object1 = value;
    }

    public void setValue2(T2 value)
    {
        object2 = value;
    }
}
