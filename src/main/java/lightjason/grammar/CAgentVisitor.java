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

package lightjason.grammar;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lightjason.common.CCommon;
import lightjason.error.CIllegalArgumentException;
import lightjason.error.CIllegalStateException;
import lightjason.language.CLiteral;
import lightjason.language.CVariable;
import lightjason.language.ILiteral;
import lightjason.language.ITerm;
import lightjason.language.event.CAddBelief;
import lightjason.language.event.CAddGoal;
import lightjason.language.event.CChangeBelief;
import lightjason.language.event.CDeleteBelief;
import lightjason.language.event.CDeleteGoal;
import lightjason.language.event.IEvent;
import lightjason.language.plan.CPlan;
import lightjason.language.plan.IPlan;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * class to visit each AST node of an agent
 */
public class CAgentVisitor extends lightjason.grammar.AgentBaseVisitor<Object> implements IAgentVisitor
{
    /**
     * initial goal
     */
    private ILiteral m_InitialGoal;
    /**
     * set with initial beliefs
     */
    private final Set<ILiteral> m_InitialBeliefs = new HashSet<>();
    /**
     * map with plans
     */
    private final SetMultimap<IEvent<?>, IPlan> m_plans = HashMultimap.create();


    @Override
    public Object visitInitial_beliefs( final lightjason.grammar.AgentParser.Initial_beliefsContext p_context )
    {
        p_context.belief().parallelStream().map( i -> (ILiteral) this.visitBelief( i ) ).forEach( m_InitialBeliefs::add );
        return null;
    }

    @Override
    public Object visitInitial_goal( final lightjason.grammar.AgentParser.Initial_goalContext p_context )
    {
        m_InitialGoal = new CLiteral( p_context.atom().getText() );
        return null;
    }

    @Override
    public Object visitBelief( final lightjason.grammar.AgentParser.BeliefContext p_context )
    {
        return new CLiteral( (CLiteral) this.visitLiteral( p_context.literal() ), p_context.STRONGNEGATION() != null );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object visitPlan( final lightjason.grammar.AgentParser.PlanContext p_context )
    {
        final IPlan l_plan;
        final ILiteral l_head = (ILiteral) this.visitLiteral( p_context.literal() );

        // @bug incomplete
        //System.out.println( p_context.plancontent().parallelStream().map( i -> this.visitPlancontent( i ) ).collect( Collectors.toList() ) );

        switch ( (String) super.visitPlan_trigger( p_context.plan_trigger() ) )
        {
            case CAddBelief.ID:
                l_plan = new CPlan( new CAddBelief( l_head.getFunctor() ), l_head );
                break;
            case CDeleteBelief.ID:
                l_plan = new CPlan( new CDeleteBelief( l_head.getFunctor() ), l_head );
                break;
            case CChangeBelief.ID:
                l_plan = new CPlan( new CChangeBelief( l_head.getFunctor() ), l_head );
                break;
            case CAddGoal.ID:
                l_plan = new CPlan( new CAddGoal( l_head.getFunctor() ), l_head );
                break;
            case CDeleteGoal.ID:
                l_plan = new CPlan( new CDeleteGoal( l_head.getFunctor() ), l_head );
                break;

            default:
                throw new CIllegalStateException( CCommon.getLanguageString( this, "event", p_context.plan_trigger().getText() ) );
        }

        m_plans.put( l_plan.getTrigger(), l_plan );
        return null;
    }

    @Override
    public Object visitPlan_goal_trigger( final lightjason.grammar.AgentParser.Plan_goal_triggerContext p_context )
    {
        switch ( p_context.getText() )
        {
            case "+!":
                return CAddGoal.ID;
            case "-!":
                return CDeleteGoal.ID;

            default:
                throw new CIllegalArgumentException( CCommon.getLanguageString( this, "goaltrigger", p_context.getText() ) );
        }
    }

    @Override
    public Object visitPlan_belief_trigger( final lightjason.grammar.AgentParser.Plan_belief_triggerContext p_context )
    {
        switch ( p_context.getText() )
        {
            case "+":
                return CAddBelief.ID;
            case "-":
                return CDeleteBelief.ID;
            case "-+":
                return CChangeBelief.ID;

            default:
                throw new CIllegalArgumentException( CCommon.getLanguageString( this, "belieftrigger", p_context.getText() ) );
        }
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    @SuppressWarnings( "unchecked" )
    public Object visitLiteral( final lightjason.grammar.AgentParser.LiteralContext p_context )
    {
        switch ( p_context.list().size() )
        {
            case 1:
                return new CLiteral( this.visitAtom( p_context.atom() ).toString(), (List<ITerm>) this.visitList( p_context.list( 0 ) ) );

            case 2:
                return new CLiteral(
                        this.visitAtom( p_context.atom() ).toString(), (List<ITerm>) this.visitList( p_context.list( 0 ) ),
                        new HashSet<>( (List<ITerm>) this.visitList( p_context.list( 1 ) ) )
                );

            default:
                return new CLiteral( this.visitAtom( p_context.atom() ).toString() );
        }
    }


    @Override
    public Object visitTerm( final lightjason.grammar.AgentParser.TermContext p_context )
    {
        return super.visitTerm( p_context );
    }

    @Override
    public Object visitList( final lightjason.grammar.AgentParser.ListContext p_context )
    {
        return p_context.term().parallelStream().map( i -> super.visitTerm( i ) ).collect( Collectors.toList() );
    }

    @Override
    public Object visitAtom( final lightjason.grammar.AgentParser.AtomContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public Object visitVariable( final lightjason.grammar.AgentParser.VariableContext p_context )
    {
        return new CVariable<>( p_context.getText() );
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Object visitFloatnumber( final lightjason.grammar.AgentParser.FloatnumberContext p_context )
    {
        switch ( p_context.getText() )
        {
            case "pi":
                return ( p_context.MINUS() == null ? 1 : -1 ) * Math.PI;
            case "euler":
                return ( p_context.MINUS() == null ? 1 : -1 ) * Math.E;
            case "lightspeed":
                return (double) ( ( p_context.MINUS() == null ? 1 : -1 ) * 299792458 );
            case "avogadro":
                return ( p_context.MINUS() == null ? 1 : -1 ) * 6.0221412927e23;
            case "boltzmann":
                return ( p_context.MINUS() == null ? 1 : -1 ) * 8.617330350e-15;
            case "gravity":
                return ( p_context.MINUS() == null ? 1 : -1 ) * 6.67408e-11;
            case "electron":
                return ( p_context.MINUS() == null ? 1 : -1 ) * 9.10938356e-31;
            case "neutron":
                return ( p_context.MINUS() == null ? 1 : -1 ) * 1674927471214e-27;
            case "proton":
                return ( p_context.MINUS() == null ? 1 : -1 ) * 1.6726219e-27;

            default:
                return Double.valueOf( p_context.getText() );
        }
    }

    @Override
    public Object visitIntegernumber( final lightjason.grammar.AgentParser.IntegernumberContext p_context )
    {
        return Long.valueOf( p_context.getText() );
    }

    @Override
    public Object visitLogicalvalue( final lightjason.grammar.AgentParser.LogicalvalueContext p_context )
    {
        return p_context.TRUE() != null ? true : false;
    }

    @Override
    public Object visitString( final lightjason.grammar.AgentParser.StringContext p_context )
    {
        return p_context.getText();
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public final Set<ILiteral> getInitialBeliefs()
    {
        return m_InitialBeliefs;
    }

    @Override
    public final ILiteral getInitialGoal()
    {
        return m_InitialGoal;
    }

    @Override
    public SetMultimap<IEvent<?>, IPlan> getPlans()
    {
        return m_plans;
    }

    @Override
    public Map<String, Object> getRules()
    {
        return null;
    }

}