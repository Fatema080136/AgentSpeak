/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason AgentSpeak(L++)                                #
 * # Copyright (c) 2015-16, LightJason (info@lightjason.org)                            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightjason.agentspeak.action.buildin.datetime;

import org.joda.time.Instant;
import org.joda.time.Years;

import java.util.List;
import java.util.stream.Stream;



/**
 * returns the years between two dates.
 * The actions returns the number of years between
 * two date-time objects, the action never
 * fails
 *
 * @code [Y1|Y2] = datetime/yearsbetween( DateTime1, DateTime2, DateTime3, DateTime4 ); @endcode
 */
public final class CYearsBetween extends IBetween
{

    @Override
    protected final Stream<?> apply( final Stream<List<Instant>> p_datetime )
    {
        return p_datetime
            .map( i -> Years.yearsBetween( i.get( 0 ), i.get( 1 ) ) )
            .mapToLong( Years::getYears )
            .boxed();
    }

}
