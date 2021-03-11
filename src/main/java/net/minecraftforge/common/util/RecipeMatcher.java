/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.util;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

public class RecipeMatcher
{
    /**
     * Attempts to match inputs to the specified tests. In the best way that all inputs are used by one test.
     * Will return null in any of these cases:
     *   input/test lengths don't match. This is only for matching paired outputs.
     *   any input doesn't match a test
     *   any test doesn't match a input
     *   If we are unable to determine a proper pair
     *
     * @return An array mapping inputs to tests. ret[x] = y means input[x] = test[y]
     */
    public static <T> int[] findMatches(List<T> inputs, List<? extends Predicate<T>> tests)
    {
        int elements = inputs.size();
        if (elements != tests.size())
            return null; // There will not be a 1:1 mapping of inputs -> tests

        int[] ret = new int[elements];
        for (int x = 0; x < elements; x++)
            ret[x] = -1;

        // [UnusedInputs] [UnusedIngredients] [IngredientMatchMask]...
        BitSet data = new BitSet((elements + 2) * elements);
        for (int x = 0; x < elements; x++)
        {
            int matched = 0;
            int offset = (x + 2) * elements;
            Predicate<T> test = tests.get(x);

            for (int y = 0; y < elements; y++)
            {
                if (data.get(y))
                    continue;

                if (test.test(inputs.get(y)))
                {
                    data.set(offset + y);
                    matched++;
                }
            }

            if (matched == 0)
                return null; //We have an test that matched non of the inputs

            if (matched == 1)
            {
                if (!claim(ret, data, x, elements))
                    return null; //We failed to claim this index, which means it caused something else to go to 0 matches, which makes the whole thing fail
            }
        }

        if (data.nextClearBit(0) >= elements) //All items have been used, which means all tests have a match!
            return ret;

        // We should be in a state where multiple tests are satified by multiple inputs. So we need to try a branching recursive test.
        // However for performance reasons, we should probably make that check a sub-set of the entire graph.
        if (backtrack(data, ret, 0, elements))
            return ret;

        return null; //Backtrack failed, no matches, we cry and go home now :(
    }

    // This is bad... need to think of a better cascade, recursion instead of stack?
    private static boolean claim(int[] ret, BitSet data, int claimed, int elements)
    {
        Queue<Integer> pending = new LinkedList<Integer>();
        pending.add(claimed);

        while (pending.peek() != null)
        {
            int test = pending.poll();
            int offset = (test + 2) * elements;
            int used = data.nextSetBit(offset) - offset;

            if (used >= elements || used < 0)
                throw new IllegalStateException("What? We matched something, but it wasn't set in the range of this test! Test: " + test +  " Used: " + used);

            data.set(used);
            data.set(elements + test);
            ret[used] = test;

            for (int x = 0; x < elements; x++)
            {
                offset = (x + 2) * elements;
                if (data.get(offset + used) && !data.get(elements + x))
                {
                    data.clear(offset + used);
                    int count = 0;
                    for (int y = offset; y < offset + elements; y++)
                        if (data.get(y))
                            count++;

                    if (count == 0)
                        return false; //Claiming this caused another test to lose its last match..

                    if (count == 1)
                        pending.add(x);
                }
            }
        }

        return true;
    }

    //We use recursion here, why? Because I feel like it. Also because we should only ever be working in data sets < 9
    private static boolean backtrack(BitSet data, int[] ret, int start, int elements)
    {
      int test = data.nextClearBit(elements + start) - elements;
      if (test >= elements)
        return true; //Could not find the next unused test.

      if (test < 0)
        throw new IllegalStateException("This should never happen, negative test in backtrack!");

      int offset = (test + 2) * elements;
      for (int x = 0; x < elements; x++)
      {
        if (!data.get(offset + x) || data.get(x))
            continue;

        data.set(x);

        if (backtrack(data, ret, test + 1, elements))
        {
            ret[x] = test;
            return true;
        }

        data.clear(x);
      }

      return false;
    }
}
