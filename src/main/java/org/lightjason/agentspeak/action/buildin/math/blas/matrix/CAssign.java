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

package org.lightjason.agentspeak.action.buildin.math.blas.matrix;

import cern.colt.matrix.DoubleMatrix2D;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.List;
import java.util.stream.Collectors;


/**
 * assigns a value or matrix to all elements.
 * The action assign the first argument to all
 * other arguments which must be a matrix
 *
 * @code
    math/blas/matrix/assign(2, Matrix1, [Matrix2, Matrix3] );
    math/blas/matrix/assign( AssignMatrix, Matrix1, [Matrix2, Matrix3] );
 * @endcode
 *
 */
public final class CAssign extends IBuildinAction
{

    /**
     * ctor
     */
    public CAssign()
    {
        super( 4 );
    }

    @Override
    public final int minimalArgumentNumber()
    {
        return 2;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        final List<ITerm> l_arguments = CCommon.flatcollection( p_argument ).collect( Collectors.toList() );

        return CFuzzyValue.from(
            l_arguments.stream()
                       .skip( 1 )
                       .parallel()
                       .allMatch( i -> {

                           if ( CCommon.rawvalueAssignableTo( l_arguments.get( 0 ), Number.class ) )
                           {
                               i.<DoubleMatrix2D>raw().assign( l_arguments.get( 0 ).<Number>raw().doubleValue() );
                               return true;
                           }

                           if ( CCommon.rawvalueAssignableTo( l_arguments.get( 0 ), DoubleMatrix2D.class ) )
                           {
                               i.<DoubleMatrix2D>raw().assign( l_arguments.get( 0 ).<DoubleMatrix2D>raw() );
                               return true;
                           }

                           return false;

                       } )
        );
    }
}
