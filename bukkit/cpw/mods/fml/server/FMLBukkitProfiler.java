package cpw.mods.fml.server;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.TreeMultiset;

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
		ImmutableMultiset<String> orderedTimings = Multisets.copyHighestCountFirst(timings);
		ArrayList<String> timeLine = new ArrayList<String>();

		double totalTime = timings.size();

		for (String hit : orderedTimings)
		{
			if (--count == 0)
			{
				break;
			}
			timeLine.add(String.format("%s : %d microseconds, %d invocations. %.2f % of overall time",hit, timings.count(hit), hitCounter.count(hit), 100.0 * timings.count(hit)/totalTime));
		}
		return timeLine.toArray(new String[0]);
	}
}
