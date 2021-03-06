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

package org.lightjason.agentspeak.action.buildin.agent;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;
import org.lightjason.agentspeak.language.instantiable.plan.IPlan;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


/**
 * adds a plan to the plan-base.
 * The actions adds all arguments which
 * are plans to the plan-base of the current
 * agent, the action never fails
 *
 * @code agent/addplan( Plan1, Plan2, [Plan3, [Plan4]] ); @endcode
 */
public final class CAddPlan extends IBuildinAction
{
    @Override
    public int minimalArgumentNumber()
    {
        return 1;
    }

    @Override
    public IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                         final List<ITerm> p_annotation
    )
    {
        CCommon.flatcollection( p_argument )
               .parallel()
               .map( ITerm::<IPlan>raw )
               .map( i -> new ImmutableTriple<>( i, new AtomicLong(), new AtomicLong(  ) ) )
               .forEach( i -> p_context.agent().plans().put( i.getLeft().getTrigger(), i ) );

        return CFuzzyValue.from( true );
    }
}
