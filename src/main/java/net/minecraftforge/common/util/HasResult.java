/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface HasResult {
    Result getResult();
    void setResult(Result result);

    /**
     * A version of {@link HasResult} tailored for {@link RecordEvent}s.
     */
    @NullMarked
    interface Record extends HasResult {
        Result.Holder resultHolder();

        default Result getResult() {
            return resultHolder().get();
        }

        default void setResult(Result result) {
            resultHolder().set(result);
        }
    }
}
