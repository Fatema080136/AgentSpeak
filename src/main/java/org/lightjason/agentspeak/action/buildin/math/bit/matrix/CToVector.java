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

package org.lightjason.agentspeak.action.buildin.math.bit.matrix;

import cern.colt.bitvector.BitMatrix;
import cern.colt.bitvector.BitVector;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.List;
import java.util.stream.IntStream;


/**
 * converts the bit matrix into a bit vector.
 * The action converts each bit matrix argument
 * into a bit vector with the size of the matrix,
 * the bit within the vector are row-wise copied
 * from the matrix and the action never fails
 *
 * @code [V1|V2] = math/bit/matrix/tovector( Matrix1, Matrix2 ); @endcode
 */
public final class CToVector extends IBuildinAction
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
        CCommon.flatcollection( p_argument )
               .map( ITerm::<BitMatrix>raw )
               .map( CToVector::transform )
               .map( CRawTerm::from )
               .forEach( p_return::add );

        return CFuzzyValue.from( true );
    }

    /**
     * transforms the matrix into the bit vector
     *
     * @param p_matrix matrix
     * @return vector
     */
    private static BitVector transform( final BitMatrix p_matrix )
    {
        final BitVector l_result = new BitVector( p_matrix.size() );

        IntStream.range( 0, p_matrix.rows() )
                 .boxed()
                 .forEach( i -> IntStream.range( 0, p_matrix.columns() )
                                         .boxed()
                                         .forEach( j -> l_result.putQuick( ( i + 1 ) * j, p_matrix.getQuick( j, i ) ) ) );

        return l_result;
    }
}
