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
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.List;


/**
 * creates the real eigenvalues and eigenvectors of a matrix.
 * For each input matrix argument the eigen decomposition
 * is executed and two elements eigenvalues (as vector) and
 * eigenvectors (as matrix) are returned, the action never fails.
 *
 * @code [Values1|Vectors1|Values2|Vectors2] = math/blas/matrix/eigen( Matrix1, Matrix2 ); @endcode
 * @see https://en.wikipedia.org/wiki/Eigenvalues_and_eigenvectors
 */
public final class CEigen extends IBuildinAction
{
    /**
     * ctor
     */
    public CEigen()
    {
        super( 4 );
    }

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
               .map( ITerm::<DoubleMatrix2D>raw )
               .map( EigenvalueDecomposition::new )
               .forEach( i -> {
                   p_return.add( CRawTerm.from( i.getRealEigenvalues() ) );
                   p_return.add( CRawTerm.from( i.getV() ) );
               } );

        return CFuzzyValue.from( true );
    }
}
