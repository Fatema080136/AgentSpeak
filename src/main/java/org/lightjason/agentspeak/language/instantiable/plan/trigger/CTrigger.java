/**
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason AgentSpeak(L)                                  #
 * # Copyright (c) 2015-16, Philipp Kraus (philipp@lightjason.org)                      #
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

package org.lightjason.agentspeak.language.instantiable.plan.trigger;

import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.error.CIllegalArgumentException;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ILiteral;

import java.text.MessageFormat;


/**
 * event with literal data
 */
public final class CTrigger implements ITrigger
{
    /**
     * variable number
     */
    private final int m_variables;
    /**
     * literal with unified variables
     */
    private final ILiteral m_literal;
    /**
     * event type
     */
    private final EType m_event;
    /**
     * hashcode
     */
    private final int m_hashcode;

    /**
     * ctor
     *
     * @param p_event type
     * @param p_literal literal with unified variables
     */
    @SuppressWarnings( "unchecked" )
    public CTrigger( final EType p_event, final ILiteral p_literal )
    {
        if ( ( p_event == null ) || ( p_literal == null ) )
            throw new CIllegalArgumentException( org.lightjason.agentspeak.common.CCommon.getLanguageString( this, "empty" ) );

        m_event = p_event;
        m_literal = p_literal;
        m_variables = CCommon.getVariableFrequency( p_literal ).size();
        m_hashcode = m_event.hashCode() + m_literal.getFQNFunctor().hashCode() + (int) p_literal.values().count() + (int) p_literal.annotations().count();
    }

    /**
     * creates a trigger event
     *
     * @param p_event event
     * @param p_literal trigger literal
     * @return trigger object
     */
    public static ITrigger from( final EType p_event, final ILiteral p_literal )
    {
        return new CTrigger( p_event, p_literal );
    }

    @Override
    public final int hashCode()
    {
        return m_hashcode;
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "{0}{1}", m_event, m_literal );
    }

    @Override
    public final EType getType()
    {
        return m_event;
    }

    @Override
    public final ILiteral getLiteral()
    {
        return m_literal;
    }

    @Override
    public final int getVariableSize()
    {
        return m_variables;
    }

    @Override
    public final ITrigger shallowcopy( final IPath... p_prefix )
    {
        return new CTrigger( m_event, m_literal.shallowcopy( p_prefix ) );
    }

    @Override
    public final ITrigger shallowcopySuffix()
    {
        return new CTrigger( m_event, m_literal.shallowcopySuffix() );
    }
}