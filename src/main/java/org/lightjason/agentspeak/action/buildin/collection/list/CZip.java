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

package org.lightjason.agentspeak.action.buildin.collection.list;

import com.codepoetics.protonpack.StreamUtils;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * creates a list of tuples with elements of two lists.
 * Creates list of tupels of both list arguments
 * \f$ \mathbb{X} \f$ and \f$ \mathbb{Y} \f$ and returns
 * a list of \f$ \langle x_i, y_i \rangle \f$, the action fails never
 *
 * @code T = collection/list/zip( [1,3,5,7], [2,4,6,8] ); @endcode
 */
public final class CZip extends IBuildinAction
{
    /**
     * ctor
     */
    public CZip()
    {
        super( 3 );
    }

    @Override
    public final int minimalArgumentNumber()
    {
        return 2;
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        // first and second argument list references
        final List<AbstractMap.Entry<?, ?>> l_result = StreamUtils.zip(
            p_argument.get( 0 ).<Collection<?>>raw().stream(),
            p_argument.get( 1 ).<Collection<?>>raw().stream(),
            AbstractMap.SimpleImmutableEntry::new
        ).collect( Collectors.toList() );

        p_return.add( CRawTerm.from(
            p_parallel ? Collections.synchronizedList( l_result ) : l_result
        ) );

        return CFuzzyValue.from( true );
    }

}
