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

package org.lightjason.agentspeak.language.execution.expression.numerical;

import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.error.CIllegalArgumentException;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.expression.EOperator;
import org.lightjason.agentspeak.language.execution.expression.IBaseBinary;
import org.lightjason.agentspeak.language.execution.expression.IExpression;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.LinkedList;
import java.util.List;


/**
 * additve binary expression
 */
public final class CAdditive extends IBaseBinary
{

    /**
     * ctor
     *
     * @param p_operator operator
     * @param p_lefthandside left-hand-side argument
     * @param p_righthandside right-hand-side
     */
    public CAdditive( final EOperator p_operator, final IExpression p_lefthandside,
                      final IExpression p_righthandside
    )
    {
        super( p_operator, p_lefthandside, p_righthandside );
        if ( !m_operator.isAdditive() )
            throw new CIllegalArgumentException( CCommon.languagestring( this, "operator", m_operator ) );
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        final List<ITerm> l_argument = new LinkedList<>();
        if ( !this.executearguments( p_context, p_parallel, l_argument ) )
            return CFuzzyValue.from( false );


        switch ( m_operator )
        {
            case PLUS:
                p_return.add( CRawTerm.from( this.add(
                    l_argument.get( 0 ).<Number>raw(),
                    l_argument.get( 1 ).<Number>raw()
                ) ) );
                return CFuzzyValue.from( true );

            case MINUS:
                p_return.add( CRawTerm.from( this.subtract(
                    l_argument.get( 0 ).<Number>raw(),
                    l_argument.get( 1 ).<Number>raw()
                ) ) );
                return CFuzzyValue.from( true );

            default:
                return CFuzzyValue.from( false );
        }

    }

    /**
     * runs the addition of number types
     *
     * @param p_left left number argument
     * @param p_right right number argument
     * @return addition value
     *
     * @tparam N any number type
     * @tparam M any number type
     */
    private <N extends Number, M extends Number> Number add( final N p_left, final M p_right )
    {
        return ( p_left instanceof Double ) || ( p_right instanceof Double )
               ? p_left.doubleValue() + p_right.doubleValue()
               : p_left.longValue() + p_right.longValue();
    }

    /**
     * runs the subtraction of number types
     *
     * @param p_left left number argument
     * @param p_right right number argument
     * @return subtraction value
     *
     * @tparam N any number type
     * @tparam M any number type
     */
    private <N extends Number, M extends Number> Number subtract( final N p_left, final M p_right )
    {
        return ( p_left instanceof Double ) || ( p_right instanceof Double )
               ? p_left.doubleValue() - p_right.doubleValue()
               : p_left.longValue() - p_right.longValue();
    }

}
