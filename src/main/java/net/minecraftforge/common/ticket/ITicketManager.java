/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.ticket;

public interface ITicketManager<T>
{
    void add(SimpleTicket<T> ticket);

    void remove(SimpleTicket<T> ticket);
}
