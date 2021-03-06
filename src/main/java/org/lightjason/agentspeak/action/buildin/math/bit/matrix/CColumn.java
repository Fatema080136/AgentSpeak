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

import java.util.stream.IntStream;


/**
 * returns a single column of a bit matrix.
 * The action returns a column of a bit matrix as vector,
 * the first argument is the index of the column, all
 * other a matrix objects, the action never fails
 *
 * @code [V1|V2] = math/bit/matrix/column(3, Matrix1, [Matrix2]); @endcode
 */
public final class CColumn extends IRowColumn
{

    @Override
    protected final BitVector extract( final BitMatrix p_matrix, final int p_index )
    {
        final BitVector l_result = new BitVector( p_matrix.rows() );
        IntStream.range( 0, p_matrix.rows() )
                 .boxed()
                 .forEach( i -> l_result.putQuick( i, p_matrix.getQuick( p_index, i ) ) );

        return l_result;
    }

}
