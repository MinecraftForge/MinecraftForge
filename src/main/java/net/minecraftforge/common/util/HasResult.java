package net.minecraftforge.common.util;

import net.minecraftforge.eventbus.api.event.RecordEvent;

public interface HasResult {
    Result getResult();
    void setResult(Result result);

    /**
     * A version of {@link HasResult} tailored for {@link RecordEvent}s.
     */
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
