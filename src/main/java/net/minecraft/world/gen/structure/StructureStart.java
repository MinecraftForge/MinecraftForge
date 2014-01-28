package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public abstract class StructureStart
{
    // JAVADOC FIELD $$ field_75075_a
    protected LinkedList components = new LinkedList();
    protected StructureBoundingBox boundingBox;
    private int field_143024_c;
    private int field_143023_d;
    private static final String __OBFID = "CL_00000513";

    public StructureStart() {}

    public StructureStart(int par1, int par2)
    {
        this.field_143024_c = par1;
        this.field_143023_d = par2;
    }

    public StructureBoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    public LinkedList getComponents()
    {
        return this.components;
    }

    // JAVADOC METHOD $$ func_75068_a
    public void generateStructure(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            StructureComponent structurecomponent = (StructureComponent)iterator.next();

            if (structurecomponent.getBoundingBox().intersectsWith(par3StructureBoundingBox) && !structurecomponent.addComponentParts(par1World, par2Random, par3StructureBoundingBox))
            {
                iterator.remove();
            }
        }
    }

    // JAVADOC METHOD $$ func_75072_c
    protected void updateBoundingBox()
    {
        this.boundingBox = StructureBoundingBox.getNewBoundingBox();
        Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            StructureComponent structurecomponent = (StructureComponent)iterator.next();
            this.boundingBox.expandTo(structurecomponent.getBoundingBox());
        }
    }

    public NBTTagCompound func_143021_a(int par1, int par2)
    {
        if (MapGenStructureIO.func_143033_a(this) == null) // This is just a more friendly error instead of the 'Null String' below
        {
            throw new RuntimeException("StructureStart \"" + this.getClass().getName() + "\" missing ID Mapping, Modder see MapGenStructureIO");
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", MapGenStructureIO.func_143033_a(this));
        nbttagcompound.setInteger("ChunkX", par1);
        nbttagcompound.setInteger("ChunkZ", par2);
        nbttagcompound.setTag("BB", this.boundingBox.func_151535_h());
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            StructureComponent structurecomponent = (StructureComponent)iterator.next();
            nbttaglist.appendTag(structurecomponent.func_143010_b());
        }

        nbttagcompound.setTag("Children", nbttaglist);
        this.func_143022_a(nbttagcompound);
        return nbttagcompound;
    }

    public void func_143022_a(NBTTagCompound par1NBTTagCompound) {}

    public void func_143020_a(World par1World, NBTTagCompound par2NBTTagCompound)
    {
        this.field_143024_c = par2NBTTagCompound.getInteger("ChunkX");
        this.field_143023_d = par2NBTTagCompound.getInteger("ChunkZ");

        if (par2NBTTagCompound.hasKey("BB"))
        {
            this.boundingBox = new StructureBoundingBox(par2NBTTagCompound.getIntArray("BB"));
        }

        NBTTagList nbttaglist = par2NBTTagCompound.func_150295_c("Children", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            this.components.add(MapGenStructureIO.func_143032_b(nbttaglist.func_150305_b(i), par1World));
        }

        this.func_143017_b(par2NBTTagCompound);
    }

    public void func_143017_b(NBTTagCompound par1NBTTagCompound) {}

    // JAVADOC METHOD $$ func_75067_a
    protected void markAvailableHeight(World par1World, Random par2Random, int par3)
    {
        int j = 63 - par3;
        int k = this.boundingBox.getYSize() + 1;

        if (k < j)
        {
            k += par2Random.nextInt(j - k);
        }

        int l = k - this.boundingBox.maxY;
        this.boundingBox.offset(0, l, 0);
        Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            StructureComponent structurecomponent = (StructureComponent)iterator.next();
            structurecomponent.getBoundingBox().offset(0, l, 0);
        }
    }

    protected void setRandomHeight(World par1World, Random par2Random, int par3, int par4)
    {
        int k = par4 - par3 + 1 - this.boundingBox.getYSize();
        boolean flag = true;
        int i1;

        if (k > 1)
        {
            i1 = par3 + par2Random.nextInt(k);
        }
        else
        {
            i1 = par3;
        }

        int l = i1 - this.boundingBox.minY;
        this.boundingBox.offset(0, l, 0);
        Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            StructureComponent structurecomponent = (StructureComponent)iterator.next();
            structurecomponent.getBoundingBox().offset(0, l, 0);
        }
    }

    // JAVADOC METHOD $$ func_75069_d
    public boolean isSizeableStructure()
    {
        return true;
    }

    public int func_143019_e()
    {
        return this.field_143024_c;
    }

    public int func_143018_f()
    {
        return this.field_143023_d;
    }
}