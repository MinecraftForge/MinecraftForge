package net.minecraftforge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

public final class FixerRegistry
{

    private static final FixerRegistry INSTANCE = new FixerRegistry();

    private static final Set<? extends IFixType> ROOT_FIX_TYPES = EnumSet.of(FixTypes.LEVEL, FixTypes.PLAYER, FixTypes.CHUNK);
    private static final String APPLIED_FIXES_TAG = "forge:applied_fixes";
    private static final String FIXER_ORDER = "forge:fixer_order";

    private final Map<ResourceLocation, IFixableData> fixers = new HashMap<ResourceLocation, IFixableData>();
    private final Map<IFixableData, ResourceLocation> fixersRev = new IdentityHashMap<IFixableData, ResourceLocation>();
    private final List<IFixableData> orderedFixers = new ArrayList<IFixableData>();

    private FixerRegistry() { }


    public void registerFixer(ResourceLocation id, IFixableData fixer)
    {
        fixers.put(id, fixer);
        fixersRev.put(fixer, id);
        orderedFixers.add(fixer);
    }

    public static FixerRegistry instance() {
        return INSTANCE;
    }

    @Deprecated // internal API, do not call
    public IDataFixer fixerForType(IFixType type, NBTTagCompound nbt)
    {
        if (type == FixTypes.LEVEL)
        {
            orderFixers(nbt);
        }

        DataFixer vanillaFixer = FMLCommonHandler.instance().getDataFixer();
        if (ROOT_FIX_TYPES.contains(type))
        {
            return new ForgeDataFixer(vanillaFixer, getFixersToRun(nbt));
        }
        else
        {
            return vanillaFixer;
        }
    }

    private void orderFixers(NBTTagCompound levelData)
    {
        NBTTagList nbtList = levelData.getTagList(FIXER_ORDER, Constants.NBT.TAG_STRING);
        final List<String> fixerOrder = Lists.newArrayListWithCapacity(nbtList.tagCount());
        for (int i = 0, n = nbtList.tagCount(); i < n; i++)
        {
            fixerOrder.add(nbtList.getStringTagAt(i));
        }


        Collections.sort(orderedFixers, new Comparator<IFixableData>()
        {
            @Override
            public int compare(IFixableData left, IFixableData right)
            {
                int idxLeft = fixerOrder.indexOf(fixersRev.get(left).toString());
                int idxRight = fixerOrder.indexOf(fixersRev.get(right).toString());
                // fixers not in the save need to be at the end of the list
                if (idxLeft < 0)
                {
                    return idxRight < 0 ? 0 : 1;
                }
                else if (idxRight < 0)
                {
                    return 0;
                }
                else
                {
                    return Ints.compare(idxLeft, idxRight);
                }
            }
        });
    }

    private IFixableData[] getFixersToRun(NBTTagCompound nbt)
    {
        BitSet appliedFixersBits = readBitSet(nbt.getTag(APPLIED_FIXES_TAG));
        IFixableData[] fixersToRun = new IFixableData[appliedFixersBits.cardinality()];
        int idx = 0;
        for (int i = appliedFixersBits.nextClearBit(0); i < orderedFixers.size(); i = appliedFixersBits.nextClearBit(i + 1))
        {
            fixersToRun[idx++] = orderedFixers.get(i);
        }
        return fixersToRun;
    }

    // cannot use toByteArray, etc. because we are on java 6 :(
    // for <64 bits use a single long
    // otherwise a byte[]
    // 2^n being set in the output means bit n in the BitSet is set
    private static BitSet readBitSet(NBTBase nbt)
    {
        if (nbt == null)
        {
            return new BitSet();
        }
        BitSet set;
        if (nbt.getId() == Constants.NBT.TAG_LONG)
        {
            set = new BitSet(64);
            long l = ((NBTTagLong)nbt).getLong();
            while (l != 0)
            {
                int bit = Long.numberOfTrailingZeros(l);
                set.set(bit);
                l &= ~(1 << bit);
            }
        }
        else if (nbt.getId() == Constants.NBT.TAG_BYTE_ARRAY)
        {
            byte[] bytes = ((NBTTagByteArray)nbt).getByteArray();
            int bits = bytes.length << 3;
            set = new BitSet(bits);
            for (int i = 0, n = bytes.length; i < n; i++)
            {
                int b = bytes[i];
                while (b != 0)
                {
                    int bit = Integer.numberOfTrailingZeros(b);
                    set.set(bit + i << 3);
                    b &= ~(1 << bit);
                }
            }
        }
        else
        {
            set = new BitSet();
        }
        return set;
    }

    private static NBTBase writeBitSet(BitSet set)
    {
        if (set.length() <= 64)
        {
            long l = 0;
            for (int bit = set.nextSetBit(0); bit >= 0; bit = set.nextSetBit(bit + 1))
            {
                l |= 1 << bit;
            }
            return new NBTTagLong(l);
        }
        else
        {
            int nBytes = (set.length() + 7) >>> 3; // divide by 8 and round up
            byte[] bytes = new byte[nBytes];
            for (int bit = set.nextSetBit(0); bit >= 0 && bit != Integer.MAX_VALUE; bit = set.nextSetBit(bit + 1))
            {
                bytes[bit >>> 3] |= (1 << (bit & 7)); // set bit 2^(bit mod 8) in byte bit / 8
            }
            return new NBTTagByteArray(bytes);
        }
    }

}
