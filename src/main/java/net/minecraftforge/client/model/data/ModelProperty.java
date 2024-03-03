/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import java.util.function.Predicate;

import com.google.common.base.Predicates;

public class ModelProperty<T> implements Predicate<T> {
    
    private final Predicate<T> pred;
    
    public ModelProperty() {
        this(Predicates.alwaysTrue());
    }
    
    public ModelProperty(Predicate<T> pred) {
        this.pred = pred;
    }

    @Override
    public boolean test(T t) {
        return pred.test(t);
    }
}
