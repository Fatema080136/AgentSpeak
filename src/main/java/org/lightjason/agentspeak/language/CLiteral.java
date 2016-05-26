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

package org.lightjason.agentspeak.language;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hasher;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.grammar.CASTVisitorType;
import org.lightjason.agentspeak.grammar.CErrorListener;
import org.lightjason.agentspeak.grammar.IASTVisitorType;
import org.lightjason.agentspeak.grammar.IParserBase;
import org.lightjason.agentspeak.grammar.TypeLexer;
import org.lightjason.agentspeak.grammar.TypeParser;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.variable.IVariable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * default generic literal class for agent beliefs
 * a literal consists of a functor, an optional list of values and
 * an optional set of annotations, e.g. velocity(50)[source(self)]
 */
public final class CLiteral implements ILiteral
{
    /**
     * negation symbol
     */
    protected static final String NEGATION = "~";
    /**
     * at symbol
     */
    protected static final String AT = "@";
    /**
     * literal annotations
     */
    protected final ImmutableMultimap<IPath, ILiteral> m_annotations;
    /**
     * literal values
     */
    protected final ImmutableMultimap<IPath, ITerm> m_values;
    /**
     * literal values as list
     */
    protected final List<ITerm> m_orderedvalues;
    /**
     * literals functor
     */
    protected final IPath m_functor;
    /**
     * negated option
     */
    protected final boolean m_negated;
    /**
     * @ prefix is set
     */
    protected final boolean m_at;
    /**
     * hash code
     */
    private final int m_hash;
    /**
     * hash of the annotations
     */
    private final int m_annotationhash;
    /**
     * hash of the values
     */
    private final int m_valuehash;


    /**
     * ctor
     *
     * @param p_at @ prefix is set
     * @param p_negated negated flag
     * @param p_functor functor of the literal
     * @param p_values initial list of values
     * @param p_annotations initial set of annotations
     */
    public CLiteral( final boolean p_at, final boolean p_negated, final IPath p_functor, final Collection<ITerm> p_values,
                     final Collection<ILiteral> p_annotations
    )
    {
        m_at = p_at;
        m_negated = p_negated;
        // create a full copy of the functor, because concurrency modification
        m_functor = new CPath( p_functor ).normalize();

        // create immutable structures
        final Multimap<IPath, ILiteral> l_annotations = HashMultimap.create();
        p_annotations.stream().forEach( i -> l_annotations.put( i.getFQNFunctor(), i ) );
        m_annotations = ImmutableSetMultimap.copyOf( l_annotations );

        final Multimap<IPath, ITerm> l_values = LinkedListMultimap.create();
        p_values.stream().forEachOrdered( i -> l_values.put( i.getFQNFunctor(), i ) );
        m_values = ImmutableListMultimap.copyOf( l_values );

        m_orderedvalues = Collections.unmodifiableList( new LinkedList<>( p_values ) );

        // calculates hash value
        m_hash = m_functor.hashCode()
                 + IntStream.range( 0, m_orderedvalues.size() ).boxed().mapToInt( i -> ( i + 1 ) * m_orderedvalues.get( i ).hashCode() ).sum()
                 + m_annotations.values().stream().mapToInt( i -> i.hashCode() ).sum()
                 + ( m_negated ? 17737 : 55529 )
                 + ( m_at ? 2741 : 8081 );


        // calculates the structure hash value (Murmur3) of the value and annotation definition
        // functor will be added iif no literal data exists ( hasher must be existing twice )
        final String l_functor = p_functor.getPath();

        final Hasher l_valuehasher = CCommon.getTermHashing();
        if ( m_orderedvalues.stream().filter( i -> i instanceof ILiteral ).map( i -> l_valuehasher.putInt( ( (ILiteral) i ).valuehash() ) ).count() == 0 )
        {
            l_valuehasher.putBoolean( m_negated );
            l_valuehasher.putString( l_functor, Charsets.UTF_8 );
        }

        final Hasher l_annotationhasher = CCommon.getTermHashing();
        if ( m_annotations.values().stream().map( i -> l_annotationhasher.putInt( i.valuehash() ) ).count() == 0 )
        {
            l_annotationhasher.putBoolean( m_negated );
            l_annotationhasher.putString( l_functor, Charsets.UTF_8 );
        }

        m_annotationhash = l_annotationhasher.hash().asInt();
        m_valuehash = l_valuehasher.hash().asInt();
    }

    /**
     * factory
     *
     * @param p_functor functor string
     * @return literal
     */
    public static ILiteral from( final String p_functor )
    {
        return from( p_functor, Collections.emptySet(), Collections.emptySet() );
    }

    /**
     * factory
     *
     * @param p_functor functor string
     * @param p_values value literals
     * @return literal
     */
    public static ILiteral from( final String p_functor, final Collection<ITerm> p_values )
    {
        return from( p_functor, p_values, Collections.emptySet() );
    }

    /**
     * factory
     *
     * @param p_functor functor string
     * @param p_values value literals
     * @param p_annotations annotation literals
     * @return literal
     */
    public static ILiteral from( final String p_functor, final Collection<ITerm> p_values, final Collection<ILiteral> p_annotations )
    {
        return new CLiteral(
            p_functor.contains( AT ), p_functor.contains( NEGATION ), CPath.from( p_functor.replace( AT, "" ).replace( NEGATION, "" ) ),
            p_values,
            p_annotations
        );
    }

    /**
     * stream factory
     *
     * @param p_functor functor
     * @param p_values value stream
     * @param p_annotations annotation stream
     * @return literal
     */
    public static ILiteral from( final String p_functor, final Stream<ITerm> p_values, final Stream<ILiteral> p_annotations )
    {
        return from( p_functor, p_values.collect( Collectors.toList() ), p_annotations.collect( Collectors.toList() ) );
    }

    /**
     * stream factory
     *
     * @param p_functor functor
     * @param p_values value stream
     * @return literal
     */
    public static ILiteral from( final String p_functor, final Stream<ITerm> p_values )
    {
        return from( p_functor, p_values.collect( Collectors.toList() ) );
    }

    /**
     * factory
     *
     * @param p_literal literal string
     * @return literal
     *
     * @throws Exception parsing and stream exception
     */
    public static ILiteral parse( final String p_literal ) throws Exception
    {
        return new CParser().parse( new ByteArrayInputStream( p_literal.getBytes() ) ).getLiteral();
    }

    @Override
    public final Stream<ITerm> values( final IPath... p_path )
    {
        return ( p_path == null ) || ( p_path.length < 1 )
               ? m_values.values().stream()
               : p_path.length == 1
                 ? m_values.asMap().get( p_path[0] ).stream()
                 : m_values.asMap().get( p_path[0] ).stream()
                           .filter( i -> i instanceof ILiteral )
                           .flatMap( i -> ( (ILiteral) i ).values( Arrays.copyOfRange( p_path, 1, p_path.length ) ) );
    }

    @Override
    public final Stream<ITerm> orderedvalues( final IPath... p_path )
    {
        return ( p_path == null ) || ( p_path.length < 1 )
               ? m_orderedvalues.stream().sequential()
               : p_path.length == 1
                 ? m_orderedvalues.stream()
                                  .filter( i -> i.getFQNFunctor().equals( p_path[0] ) ).sequential()
                 : m_orderedvalues.stream()
                                  .filter( i -> i.getFQNFunctor().equals( p_path[0] ) )
                                  .filter( i -> i instanceof ILiteral )
                                  .flatMap( i -> ( (ILiteral) i ).orderedvalues( Arrays.copyOfRange( p_path, 1, p_path.length ) ) );
    }

    @Override
    public final Stream<ILiteral> annotations( final IPath... p_path )
    {
        return ( p_path == null ) || ( p_path.length < 1 )
               ? m_annotations.values().stream()
               : Arrays.stream( p_path )
                       .parallel()
                       .flatMap( i -> m_annotations.asMap().get( i ).stream() );
    }

    @Override
    public final boolean emptyValues()
    {
        return m_values.isEmpty();
    }

    @Override
    public final boolean emptyAnnotations()
    {
        return m_annotations.isEmpty();
    }

    @Override
    public final int annotationhash()
    {
        return m_annotationhash;
    }

    @Override
    public final int valuehash()
    {
        return m_valuehash;
    }

    @Override
    public final boolean isNegated()
    {
        return m_negated;
    }

    @Override
    public final boolean hasAt()
    {
        return m_at;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final ILiteral unify( final IContext p_context )
    {
        return new CLiteral(
            m_at,
            m_negated,
            m_functor,
            m_orderedvalues.stream()
                           .map( i -> {
                               if ( i instanceof IVariable<?> )
                               {
                                   final IVariable<?> l_variable = p_context.getInstanceVariables().get( ( (IVariable<?>) i ).getFQNFunctor() );
                                   return ( l_variable == null ) || ( l_variable.isAllocated() ) ? CRawTerm.from( l_variable ) : l_variable;
                               }
                               if ( i instanceof ILiteral )
                                   return ( (ILiteral) i ).unify( p_context );
                               return i;
                           } )
                           .collect( Collectors.toList() ),
            m_annotations.values().stream().map( i -> i.unify( p_context ) ).collect( Collectors.toSet() )
        );
    }

    @Override
    public final ILiteral allocate( final IContext p_context )
    {
        return new CLiteral(
            m_at,
            m_negated,
            m_functor,
            m_orderedvalues.stream()
                           .map( i -> {
                               if ( i instanceof IVariable<?> )
                               {
                                   final IVariable<?> l_variable = p_context.getInstanceVariables().get( ( (IVariable<?>) i ).getFQNFunctor() );
                                   return l_variable == null
                                          ? CRawTerm.EMPTY
                                          : l_variable;
                               }
                               if ( i instanceof ILiteral )
                                   return ( (ILiteral) i ).unify( p_context );
                               return i;
                           } )
                           .collect( Collectors.toList() ),
            m_annotations.values().stream().map( i -> i.unify( p_context ) ).collect( Collectors.toSet() )
        );
    }

    @Override
    public final String getFunctor()
    {
        return m_functor.getSuffix();
    }

    @Override
    public final IPath getFunctorPath()
    {
        return m_functor.getSubPath( 0, m_functor.size() - 1 );
    }

    @Override
    public final IPath getFQNFunctor()
    {
        return m_functor;
    }

    @Override
    public final int hashCode()
    {
        return m_hash;
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public final ILiteral shallowcopy( final IPath... p_prefix )
    {
        return ( p_prefix == null ) || ( p_prefix.length == 0 )

               ? new CLiteral(
                   m_at, m_negated, m_functor,
                   m_values.values(),
                   m_annotations.values()
               )

               : new CLiteral(
                   m_at, m_negated, p_prefix[0].append( m_functor ),
                   m_values.values(),
                   m_annotations.values()
               );
    }

    @Override
    public final ILiteral shallowcopySuffix()
    {
        return new CLiteral(
            m_at, m_negated, CPath.from( m_functor.getSuffix() ),
            m_values.values(),
            m_annotations.values()
        );
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "{0}{1}{2}{3}{4}", m_negated ? NEGATION : "", m_at ? AT : "", m_functor, m_orderedvalues, m_annotations.values() );
    }

    @Override
    public final int compareTo( final ILiteral p_literal )
    {
        return Integer.compare( this.hashCode(), p_literal.hashCode() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final synchronized ITerm deepcopy( final IPath... p_prefix )
    {
        return ( p_prefix == null ) || ( p_prefix.length == 0 )

               ?
               new CLiteral(
                   m_at, m_negated, m_functor,
                   m_values.values().stream().map( i -> i.deepcopy() ).collect( Collectors.toList() ),
                   m_annotations.values().stream().map( i -> (ILiteral) i.deepcopy() ).collect( Collectors.toSet() )
               )

               :
               new CLiteral(
                   m_at, m_negated, p_prefix[0].append( m_functor ),
                   m_values.values().stream().map( i -> i.deepcopy() ).collect( Collectors.toList() ),
                   m_annotations.values().stream().map( i -> (ILiteral) i.deepcopy() ).collect( Collectors.toSet() )
               );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final synchronized ITerm deepcopySuffix()
    {
        return new CLiteral(
            m_at, m_negated, CPath.from( m_functor.getSuffix() ),
            m_values.values().stream().map( i -> i.deepcopy() ).collect( Collectors.toList() ),
            m_annotations.values().stream().map( i -> (ILiteral) i.deepcopy() ).collect( Collectors.toSet() )
        );
    }


    /**
     * literal parser
     */
    protected static final class CParser extends IParserBase<IASTVisitorType, TypeLexer, TypeParser>
    {

        /**
         * ctor
         */
        public CParser()
        {
            super( new CErrorListener() );
        }

        @Override
        public final IASTVisitorType parse( final InputStream p_stream ) throws Exception
        {
            final IASTVisitorType l_visitor = new CASTVisitorType();
            l_visitor.visit( this.getParser( p_stream ).literal_type() );
            return l_visitor;
        }

        @Override
        protected final Class<TypeLexer> getLexerClass()
        {
            return TypeLexer.class;
        }

        @Override
        protected final Class<TypeParser> getParserClass()
        {
            return TypeParser.class;
        }
    }
}