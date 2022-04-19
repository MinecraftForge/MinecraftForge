/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.concurrent.Executor;
import java.util.function.BinaryOperator;

public enum ThreadSelector implements BinaryOperator<Executor> {
    SYNC((sync, parallel) -> sync),
    PARALLEL((sync, parallel) -> parallel);

    private final BinaryOperator<Executor> selector;

    ThreadSelector(final BinaryOperator<Executor> selector) {
        this.selector = selector;
    }

    @Override
    public Executor apply(final Executor sync, final Executor parallel) {
        return this.selector.apply(sync, parallel);
    }
}
