/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.task;

import java.util.concurrent.CompletableFuture;

interface ICompletableFutureBuilder {

    CompletableFuture<Void> build();
}
