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
import org.lightjason.agentspeak.action.buildin.math.blas.IAlgebra;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.List;
import java.util.stream.Collectors;


/**
 * returns the matrix power.
 * The action creates the matrix power, the
 * first argument is the exponent, all other arguments are
 * input matrix vlaues and the returns the power value for each
 * input matrix
 *
 * @code [M1|M2|M3] = blas/matrix/power(3, M1, [M2, [M3]]); @endcode
 * @see https://en.wikipedia.org/wiki/Matrix_exponential
 */
public final class CPower extends IAlgebra
{

    /**
     * ctor
     */
    public CPower()
    {
        super( 4 );
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
        final List<ITerm> l_argument = CCommon.flatcollection( p_argument ).collect( Collectors.toList() );

        l_argument.stream()
                  .skip( 1 )
                  .map( ITerm::<DoubleMatrix2D>raw )
                  .map( i -> ALGEBRA.pow( i, l_argument.get( 0 ).<Number>raw().intValue() ) )
                  .map( CRawTerm::from )
                  .forEach( p_return::add );

        return CFuzzyValue.from( true );
    }
}
