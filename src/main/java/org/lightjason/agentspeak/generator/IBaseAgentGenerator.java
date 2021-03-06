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

package org.lightjason.agentspeak.generator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.agent.IPlanBundle;
import org.lightjason.agentspeak.agent.fuzzy.CBoolFuzzy;
import org.lightjason.agentspeak.agent.fuzzy.IFuzzy;
import org.lightjason.agentspeak.agent.unify.CUnifier;
import org.lightjason.agentspeak.configuration.CDefaultAgentConfiguration;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.grammar.CParserAgent;
import org.lightjason.agentspeak.grammar.IASTVisitorAgent;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.execution.IVariableBuilder;
import org.lightjason.agentspeak.language.execution.action.unify.IUnifier;
import org.lightjason.agentspeak.language.instantiable.plan.IPlan;
import org.lightjason.agentspeak.language.instantiable.rule.IRule;
import org.lightjason.agentspeak.language.score.IAggregation;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * agent generator
 */
@SuppressFBWarnings( "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD" )
public abstract class IBaseAgentGenerator<T extends IAgent<?>> implements IAgentGenerator<T>
{
    /**
     * unification
     */
    protected static final IUnifier UNIFIER = new CUnifier();
    /**
     * configuration of an agent
     */
    protected final IAgentConfiguration<T> m_configuration;


    /**
     * ctor
     *
     * @param p_stream input stream
     * @param p_actions set with action
     * @param p_aggregation aggregation function
     * @throws Exception thrown on error
     */
    @SuppressWarnings( "unchecked" )
    public IBaseAgentGenerator( final InputStream p_stream, final Set<IAction> p_actions, final IAggregation p_aggregation )
    throws Exception
    {
        this( p_stream, p_actions, p_aggregation,
              Collections.<IPlanBundle>emptySet(),
              IVariableBuilder.EMPTY
        );
    }

    /**
     * ctor
     *
     * @param p_stream input stream
     * @param p_actions set with action
     * @param p_aggregation aggregation function
     * @param p_variablebuilder variable builder (can be set to null)
     * @throws Exception thrown on error
     */
    public IBaseAgentGenerator( final InputStream p_stream, final Set<IAction> p_actions,
                                final IAggregation p_aggregation,
                                final IVariableBuilder p_variablebuilder
    )
    throws Exception
    {
        this( p_stream, p_actions, p_aggregation, Collections.<IPlanBundle>emptySet(), p_variablebuilder );
    }

    /**
     * ctor
     *
     * @param p_stream input stream
     * @param p_actions set with action
     * @param p_aggregation aggregation function
     * @param p_planbundle set with planbundles
     * @param p_variablebuilder variable builder (can be set to null)
     * @throws Exception thrown on error
     */
    public IBaseAgentGenerator( final InputStream p_stream, final Set<IAction> p_actions,
                                final IAggregation p_aggregation, final Set<IPlanBundle> p_planbundle,
                                final IVariableBuilder p_variablebuilder
    )
    throws Exception
    {
        final IASTVisitorAgent l_visitor = new CParserAgent( p_actions ).parse( p_stream );
        m_configuration = this.configuration(
            new CBoolFuzzy<>(),

            Stream.concat(
                l_visitor.initialbeliefs().stream(),
                p_planbundle.parallelStream().flatMap( i -> i.initialbeliefs().stream() )
            ).collect( Collectors.toSet() ),

            Stream.concat(
                l_visitor.plans().stream(),
                p_planbundle.parallelStream().flatMap( i -> i.plans().stream() )
            ).collect( Collectors.toSet() ),

            Stream.concat(
                l_visitor.rules().stream(),
                p_planbundle.parallelStream().flatMap( i -> i.rules().stream() )
            ).collect( Collectors.toSet() ),

            l_visitor.initialgoal(),

            UNIFIER,

            p_aggregation,

            p_variablebuilder
        );
    }

    /**
     * builds the configuraion, configuration runs cloning of objects if needed
     *
     * @return configuration object
     */
    protected IAgentConfiguration<T> configuration( final IFuzzy<Boolean, T> p_fuzzy, final Collection<ILiteral> p_initalbeliefs,
                                                    final Set<IPlan> p_plans, final Set<IRule> p_rules,
                                                    final ILiteral p_initialgoal, final IUnifier p_unifier,
                                                    final IAggregation p_aggregation,
                                                    final IVariableBuilder p_variablebuilder )
    {
        return new CDefaultAgentConfiguration<>(
            p_fuzzy,
            p_initalbeliefs,
            p_plans,
            p_rules,
            p_initialgoal,
            p_unifier,
            p_aggregation,
            p_variablebuilder
        );
    }

    @Override
    public final Stream<T> generatemultiple( final int p_number, final Object... p_data )
    {
        return IntStream.range( 0, p_number )
                    .parallel()
                    .mapToObj( i -> this.generatesingle( p_data ) )
                    .filter( Objects::nonNull );
    }

}
