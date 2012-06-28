package cpw.mods.fml.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.TreeMultiset;
import com.google.common.primitives.Ints;

public class FMLBukkitProfiler {
	private static FMLBukkitProfiler lastInstance;
	private static long endTime;
	private LinkedList<String> profiles = new LinkedList<String>();
	private TreeMultiset<String> hitCounter = TreeMultiset.create();
	private TreeMultiset<String> timings = TreeMultiset.create();
	private long timestamp;

	public static long beginProfiling(int seconds)
	{
		if (lastInstance == null)
		{
			lastInstance = new FMLBukkitProfiler();
			FMLBukkitHandler.instance().profiler = lastInstance;
			endTime = System.currentTimeMillis() + seconds;
		}
		return endTime - System.currentTimeMillis();
	}

	public static long endProfiling()
	{
		FMLBukkitHandler.instance().profiler = null;
		return endTime - System.currentTimeMillis();
	}

	public static void resetProfiling()
	{
		endProfiling();
		lastInstance = null;
	}

	public void start(String profileLabel)
	{
		profiles.push(profileLabel);
		timestamp = System.nanoTime();
	}

	public void end()
	{
		int timing = (int)((System.nanoTime() - timestamp)/1000L);
		String label = Joiner.on('.').join(profiles);
		profiles.pop();
		hitCounter.add(label);
		timings.add(label,timing);
		if (System.currentTimeMillis()>endTime)
		{
			endProfiling();
		}
	}

	public static String[] dumpProfileData(int count)
	{
		if (lastInstance == null)
		{
			return new String[0];
		}

		if (endTime > System.currentTimeMillis())
		{
			return new String[] { String.format("Timing is being gathered for another %d seconds", endTime - System.currentTimeMillis()) };
		}
		return lastInstance.profileData(count);
	}

	private String[] profileData(int count) {
		List<Entry<String>> sortedTiming = getEntriesSortedByFrequency(timings, false);
		ArrayList<String> timeLine = new ArrayList<String>();

		double totalTime = timings.size();

		for (Entry<String> hit : sortedTiming)
		{
			if (--count == 0)
			{
				break;
			}
			timeLine.add(String.format("%s : %d microseconds, %d invocations. %.2f %% of overall time",hit.getElement(), hit.getCount(), hitCounter.count(hit.getElement()), 100.0 * hit.getCount()/totalTime));
		}
		return timeLine.toArray(new String[0]);
	}

	/*
	 * Code from http://stackoverflow.com/questions/4345633/simplest-way-to-iterate-through-a-multiset-in-the-order-of-element-frequency
	 */
	private enum EntryComp implements Comparator<Multiset.Entry<?>>{
	    DESCENDING{
	        @Override
	        public int compare(final Entry<?> a, final Entry<?> b){
	            return Ints.compare(b.getCount(), a.getCount());
	        }
	    },
	    ASCENDING{
	        @Override
	        public int compare(final Entry<?> a, final Entry<?> b){
	            return Ints.compare(a.getCount(), b.getCount());
	        }
	    },
	}

	private static <E> List<Entry<E>> getEntriesSortedByFrequency(final Multiset<E> ms, final boolean ascending) {
	    final List<Entry<E>> entryList = Lists.newArrayList(ms.entrySet());
	    Collections.sort(entryList, ascending ? EntryComp.ASCENDING : EntryComp.DESCENDING);
	    return entryList;
	}

}
