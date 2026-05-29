/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.ticket;

import java.util.Collection;

public interface ITicketGetter<T> extends ITicketManager<T>
{
    Collection<SimpleTicket<T>> getTickets();
}
