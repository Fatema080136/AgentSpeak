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

package org.lightjason.agentspeak.action.buildin.graph;

import edu.uci.ics.jung.graph.AbstractGraph;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.List;
import java.util.stream.Collectors;


/**
 * check if a graph contains a vertex.
 * The action checks for the first argument,
 * if each other graph argument contains the
 * first item as an vertex, the action fails on
 * wrong input
 *
 * @code [B1|B2] = graph/containsvertex( Element, Graph1, Graph2 ); @endcode
 */
public final class CContainsVertex extends IBuildinAction
{
    @Override
    public final int minimalArgumentNumber()
    {
        return 1;
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation )
    {
        final List<ITerm> l_arguments = CCommon.flatcollection( p_argument ).collect( Collectors.toList() );
        if ( l_arguments.size() < 2 )
            return CFuzzyValue.from( false );

        l_arguments.stream()
                   .skip( 1 )
                   .map( ITerm::<AbstractGraph<Object, Object>>raw )
                   .map( i -> i.containsVertex( l_arguments.get( 0 ).raw() ) )
                   .map( CRawTerm::from )
                   .forEach( p_return::add );

        return CFuzzyValue.from( true );
    }
}
