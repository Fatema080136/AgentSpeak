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

package org.lightjason.agentspeak.action.buildin.math;

import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.error.CRuntimeException;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * action for index of minimum.
 * The action takes of the given unflatten input the minimum and
 * returns the index within the unflatten argument list, the
 * action never fails
 *
 * @code MinIndex = math/minindex( 5, 6, [7,8, [1,2,3]] ); @endcode
 */
public final class CMinIndex extends IBuildinAction
{

    @Override
    public final int minimalArgumentNumber()
    {
        return 1;
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        final List<Double> l_list = CCommon.flatcollection( p_argument )
                                           .map( ITerm::<Number>raw )
                                           .mapToDouble( Number::doubleValue )
                                           .boxed()
                                           .collect( Collectors.toList() );

        p_return.add(
            CRawTerm.from(
                (long) IntStream.range( 0, l_list.size() - 1 )
                                .parallel()
                                .reduce( ( i, j ) -> l_list.get( i ) < l_list.get( j ) ? i : j )
                                .orElseThrow( () -> new CRuntimeException( p_context ) )
            )
        );

        return CFuzzyValue.from( true );
    }
}
