/**
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the Light-Jason                                               #
 * # Copyright (c) 2015-16, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
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

package lightjason.action.buildin.math.shape;

import lightjason.action.buildin.IBuildinAction;
import lightjason.language.CCommon;
import lightjason.language.CRawTerm;
import lightjason.language.ITerm;
import lightjason.language.execution.IContext;
import lightjason.language.execution.fuzzy.CFuzzyValue;
import lightjason.language.execution.fuzzy.IFuzzyValue;

import java.util.List;
import java.util.stream.Collectors;


/**
 * action check if a point is within a circle
 */
public final class CInCircle extends IBuildinAction
{

    /**
     * ctor
     */
    public CInCircle()
    {
        super( 3 );
    }

    @Override
    public final int getMinimalArgumentNumber()
    {
        return 5;
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        // arguments are: x-value, y-value, circle center x-value, circle center y-value, circle radius
        final List<Double> l_point = p_argument.stream()
                                               .map( i -> CCommon.<Number, ITerm>getRawValue( i ) )
                                               .mapToDouble( i -> i.doubleValue() )
                                               .boxed()
                                               .collect( Collectors.toList() );

        p_return.add( CRawTerm.from(
            Math.hypot( l_point.get( 0 ) - l_point.get( 2 ), l_point.get( 1 ) - l_point.get( 3 ) ) <= Math.pow( l_point.get( 4 ), 2 )
        ) );

        return CFuzzyValue.from( true );
    }

}