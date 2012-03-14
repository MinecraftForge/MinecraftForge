package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class InfiGather
{
    private static final InfiGather instance = new InfiGather();
    private static List harvestinglist1;
    private static List harvestinglist2;
    private static List harvestinglist3;
    private static List harvestinglist4;
    private static List harvestinglist5;
    private static List harvestinglist6;

    public static final InfiGather getInstance()
    {
        return instance;
    }

    public InfiGather()
    {
        harvestinglist1 = new ArrayList();
        harvestinglist2 = new ArrayList();
        harvestinglist3 = new ArrayList();
        harvestinglist4 = new ArrayList();
        harvestinglist5 = new ArrayList();
        harvestinglist6 = new ArrayList();
    }

    static void addBlocks(Block ablock[], int i)
    {
        if (i == 1)
        {
            for (int j = 0; j < ablock.length; j++)
            {
                Block block = ablock[j];
                if (block instanceof Block)
                {
                    harvestinglist1.add(block);
                    continue;
                }
                if (!(block instanceof Block))
                {
                    throw new RuntimeException("Invalid block minimum-harvesting-level declared!");
                }
            }
        }
        if (i == 2)
        {
            Block ablock1[] = ablock;
            int k = ablock1.length;
            for (int l1 = 0; l1 < k; l1++)
            {
                Block block1 = ablock1[l1];
                if (block1 instanceof Block)
                {
                    harvestinglist2.add(block1);
                }
                else
                {
                    throw new RuntimeException("Invalid block minimum harvesting level declared!");
                }
            }
        }
        if (i == 3)
        {
            Block ablock2[] = ablock;
            int l = ablock2.length;
            for (int i2 = 0; i2 < l; i2++)
            {
                Block block2 = ablock2[i2];
                if (block2 instanceof Block)
                {
                    harvestinglist3.add(block2);
                }
                else
                {
                    throw new RuntimeException("Invalid block minimum harvesting level declared!");
                }
            }
        }
        if (i == 4)
        {
            Block ablock3[] = ablock;
            int i1 = ablock3.length;
            for (int j2 = 0; j2 < i1; j2++)
            {
                Block block3 = ablock3[j2];
                if (block3 instanceof Block)
                {
                    harvestinglist4.add(block3);
                }
                else
                {
                    throw new RuntimeException("Invalid block minimum harvesting level declared!");
                }
            }
        }
        if (i == 5)
        {
            Block ablock4[] = ablock;
            int j1 = ablock4.length;
            for (int k2 = 0; k2 < j1; k2++)
            {
                Block block4 = ablock4[k2];
                if (block4 instanceof Block)
                {
                    harvestinglist5.add(block4);
                }
                else
                {
                    throw new RuntimeException("Invalid block minimum harvesting level declared!");
                }
            }
        }
        if (i == 6)
        {
            Block ablock5[] = ablock;
            int k1 = ablock5.length;
            for (int l2 = 0; l2 < k1; l2++)
            {
                Block block5 = ablock5[l2];
                if (block5 instanceof Block)
                {
                    harvestinglist6.add(block5);
                }
                else
                {
                    throw new RuntimeException("Invalid block minimum harvesting level declared!");
                }
            }
        }
    }

    public static int getBlockLevel(Block block)
    {
        byte byte0 = 0;
        for (int i = 0; i < 6; i++)
        {
            if (i == 1 && harvestinglist1 != null)
            {
                for (int j = 0; j < harvestinglist1.size(); j++)
                {
                    Block block1 = (Block)harvestinglist1.get(j);
                    if (block1 == block)
                    {
                        byte0 = 1;
                    }
                }
            }
            if (i == 2 && harvestinglist2 != null)
            {
                for (int k = 0; k < harvestinglist2.size(); k++)
                {
                    Block block2 = (Block)harvestinglist2.get(k);
                    if (block2 == block)
                    {
                        byte0 = 2;
                    }
                }
            }
            if (i == 3 && harvestinglist3 != null)
            {
                for (int l = 0; l < harvestinglist3.size(); l++)
                {
                    Block block3 = (Block)harvestinglist3.get(l);
                    if (block3 == block)
                    {
                        byte0 = 3;
                    }
                }
            }
            if (i == 4 && harvestinglist4 != null)
            {
                for (int i1 = 0; i1 < harvestinglist4.size(); i1++)
                {
                    Block block4 = (Block)harvestinglist4.get(i1);
                    if (block4 == block)
                    {
                        byte0 = 4;
                    }
                }
            }
            if (i == 5 && harvestinglist5 != null)
            {
                for (int j1 = 0; j1 < harvestinglist5.size(); j1++)
                {
                    Block block5 = (Block)harvestinglist5.get(j1);
                    if (block5 == block)
                    {
                        byte0 = 5;
                    }
                }
            }
            if (i != 6 || harvestinglist6 == null)
            {
                continue;
            }
            for (int k1 = 0; k1 < harvestinglist6.size(); k1++)
            {
                Block block6 = (Block)harvestinglist6.get(k1);
                if (block6 == block)
                {
                    byte0 = 6;
                }
            }
        }

        return byte0;
    }

    public static boolean isDeclared(Block block)
    {
        int i = 0;
        if (harvestinglist1 != null)
        {
            ArrayList arraylist = new ArrayList(harvestinglist1);
            for (int j = 0; j < arraylist.size(); j++)
            {
                if (block == (Block)arraylist.get(j))
                {
                    i++;
                }
            }
        }
        if (harvestinglist2 != null)
        {
            ArrayList arraylist1 = new ArrayList(harvestinglist2);
            for (int k = 0; k < arraylist1.size(); k++)
            {
                if (block == (Block)arraylist1.get(k))
                {
                    i++;
                }
            }
        }
        if (harvestinglist3 != null)
        {
            ArrayList arraylist2 = new ArrayList(harvestinglist3);
            for (int l = 0; l < arraylist2.size(); l++)
            {
                if (block == (Block)arraylist2.get(l))
                {
                    i++;
                }
            }
        }
        if (harvestinglist4 != null)
        {
            ArrayList arraylist3 = new ArrayList(harvestinglist4);
            for (int i1 = 0; i1 < arraylist3.size(); i1++)
            {
                if (block == (Block)arraylist3.get(i1))
                {
                    i++;
                }
            }
        }
        if (harvestinglist5 != null)
        {
            ArrayList arraylist4 = new ArrayList(harvestinglist5);
            for (int j1 = 0; j1 < arraylist4.size(); j1++)
            {
                if (block == (Block)arraylist4.get(j1))
                {
                    i++;
                }
            }
        }
        if (harvestinglist6 != null)
        {
            ArrayList arraylist5 = new ArrayList(harvestinglist6);
            for (int k1 = 0; k1 < arraylist5.size(); k1++)
            {
                if (block == (Block)arraylist5.get(k1))
                {
                    i++;
                }
            }
        }
        return i > 0;
    }
}
