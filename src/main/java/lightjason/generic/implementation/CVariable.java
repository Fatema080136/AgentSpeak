/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the Light-Jason                                               #
 * # Copyright (c) 2015, Philipp Kraus (philipp.kraus@tu-clausthal.de)                  #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package lightjason.generic.implementation;

import lightjason.generic.IVariable;


/**
 * default variable definition
 */
public class CVariable<T> implements IVariable<T>
{
    /**
     * variable name
     */
    protected final String m_name;
    /**
     */
    protected T m_value;


    public CVariable( final String p_name )
    {
        this( p_name, null );
    }

    public CVariable( final String p_name, final T p_value )
    {
        m_name = p_name;
        m_value = p_value;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public void set( final T p_value )
    {
        m_value = p_value;
    }

    @Override
    public boolean isAssignableFrom( final Class<?> p_class )
    {
        return p_class.isAssignableFrom( m_value.getClass() );
    }

    @Override
    public T get()
    {
        return m_value;
    }

    @Override
    public int hashCode()
    {
        return m_name.hashCode() + ( ( m_value != null ) ? m_value.hashCode() : 0 );
    }

    @Override
    public boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return new CVariable<T>( m_name, m_value );
    }

}
