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

package org.lightjason.agentspeak.action.buildin.math.shape;

import com.codepoetics.protonpack.StreamUtils;
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
 * action check if a point is within a triangle.
 * The first three tuple of arguments defines the triangle
 * coordinate (x- / y-position), all other tuples are the tuples
 * of x- / y-position, the action fails on wrong input
 *
 * @code [In1|In2] = math/shape/intriangle( [[350,320], [25,375], 40,55], [160,270], 0,0 ); @endcode
 * @see https://en.wikipedia.org/wiki/Barycentric_coordinate_system
 */
public final class CInTriangle extends IBuildinAction
{

    /**
     * ctor
     */
    public CInTriangle()
    {
        super( 3 );
    }

    @Override
    public final int minimalArgumentNumber()
    {
        return 8;
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        final List<Double> l_arguments = CCommon.flatcollection( p_argument )
                                                .map( ITerm::<Number>raw )
                                                .mapToDouble( Number::doubleValue )
                                                .boxed()
                                                .collect( Collectors.toList() );
        if ( l_arguments.size() < 8 )
            return CFuzzyValue.from( false );

        StreamUtils.windowed( l_arguments.stream().skip( 6 ), 2 )
                   .map( i -> {
                       i.add(
                           l_arguments.get( 1 ) * l_arguments.get( 4 )
                           - l_arguments.get( 0 ) * l_arguments.get( 5 )
                           + ( l_arguments.get( 5 ) - l_arguments.get( 1 ) ) * i.get( 0 )
                           + ( l_arguments.get( 0 ) - l_arguments.get( 4 ) ) * i.get( 1 )
                       );

                       i.add(
                           l_arguments.get( 0 ) * l_arguments.get( 3 )
                           - l_arguments.get( 1 ) * l_arguments.get( 2 )
                           + ( l_arguments.get( 1 ) - l_arguments.get( 3 ) ) * i.get( 0 )
                           + ( l_arguments.get( 2 ) - l_arguments.get( 0 ) ) * i.get( 1 )
                       );

                       return i;
                   } )
                   .map( i -> ( i.get( 2 ) > 0 ) && ( i.get( 3 ) > 0 )
                              && ( i.get( 2 ) + i.get( 3 ) < -l_arguments.get( 3 ) * l_arguments.get( 4 )
                                                             + l_arguments.get( 1 ) * ( -l_arguments.get( 2 ) + l_arguments.get( 3 ) )
                                                             + l_arguments.get( 0 ) * ( l_arguments.get( 3 ) - l_arguments.get( 5 ) )
                                                             + l_arguments.get( 2 ) * l_arguments.get( 5 ) )
                   )
                   .map( CRawTerm::from )
                   .forEach( p_return::add );

        return CFuzzyValue.from( true );
    }

}
