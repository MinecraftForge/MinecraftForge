package net.minecraft.src;

public class Pair
{
    private final Object left;
    private final Object right;

    public Pair(Object var1, Object var2)
    {
        this.left = var1;
        this.right = var2;
    }

    public Object getLeft()
    {
        return this.left;
    }

    public Object getRight()
    {
        return this.right;
    }

    public int hashCode()
    {
        return this.left.hashCode() ^ this.right.hashCode();
    }

    public boolean equals(Object var1)
    {
        if (var1 == null)
        {
            return false;
        }
        else if (!(var1 instanceof Pair))
        {
            return false;
        }
        else
        {
            Pair var2 = (Pair)var1;
            return this.left.equals(var2.getLeft()) && this.right.equals(var2.getRight());
        }
    }
}
