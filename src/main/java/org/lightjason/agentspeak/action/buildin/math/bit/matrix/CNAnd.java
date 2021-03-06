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


/**
 * performs the logical not-and operation to all bit matrices.
 * The action runs the logical not-and operator, the first
 * argument is the bit matrix, that is combined with
 * all other bit matrices, so \f$ m_i = m_i \text{ && not } m_1 \f$
 * is performed, the action never fails
 *
 * @code math/bit/matrix/nand( Matrix, Matrix1, Matrix2 ); @endcode
 */
public final class CNAnd extends IOperator
{

    @Override
    protected final void apply( final BitMatrix p_target, final BitMatrix p_source )
    {
        p_target.andNot( p_source );
    }

}
