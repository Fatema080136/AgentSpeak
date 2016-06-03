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

package org.lightjason.agentspeak.agent;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.lightjason.agentspeak.agent.fuzzy.IFuzzy;
import org.lightjason.agentspeak.beliefbase.IView;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IVariableBuilder;
import org.lightjason.agentspeak.language.execution.action.unify.IUnifier;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;
import org.lightjason.agentspeak.language.instantiable.plan.IPlan;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightjason.agentspeak.language.instantiable.rule.IRule;
import org.lightjason.agentspeak.language.score.IAggregation;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;


/**
 * agent interface
 *
 * @tparam T agent type
 */
public interface IAgent<T extends IAgent<?>> extends Callable<T>
{

    /**
     * inspector method
     *
     * @param p_inspector inspector object
     */
    void inspect( final IInspector... p_inspector );

    /**
     * trigger an event
     *
     * @param p_trigger event trigger
     * @param p_immediately run element immediately
     * @return execution finished correctly
     *
     * @note the trigger is ignored iif the agent is sleeping
     */
    IFuzzyValue<Boolean> trigger( final ITrigger p_trigger, final boolean... p_immediately );

    /**
     * returns the beliefbase
     *
     * @return beliefbase
     */
    IView<T> getBeliefBase();

    /**
     * returns a map of the current running plans
     *
     * @return map with running plans and the instance literal
     */
    Multimap<IPath, ILiteral> getRunningPlans();

    /**
     * returns sleeping state
     *
     * @return sleeping flag
     */
    boolean isSleeping();

    /**
     * pushs the agent into sleeping state
     *
     * @param p_cycles number of cycles for sleeping - if is set to maximum the sleeping time is infinity
     * @return agent reference
     */
    IAgent<T> sleep( final long p_cycles );

    /**
     * wake-up the agent by generating wakeup-goal
     *
     * @param p_value any term value which will push into the wake-up plan
     * @return agent reference
     */
    IAgent<T> wakeup( final ITerm... p_value );

    /**
     * storage access
     *
     * @return storage map
     */
    Map<String, ?> getStorage();

    /**
     * returns an unifier
     *
     * @return unification algorithm
     */
    IUnifier getUnifier();

    /**
     * returns the time in nano seconds
     * since the last cycle
     *
     * @return nano seconds
     */
    long getLastCycleTime();

    /**
     * returns the current cycle
     *
     * @return cycle
     */
    long getCycle();

    /**
     * returns the internal map of plans
     *
     * @return plan map
     */
    Multimap<ITrigger, MutableTriple<IPlan, AtomicLong, AtomicLong>> getPlans();

    /**
     * return fuzzy operator
     *
     * @return operator
     */
    IFuzzy<Boolean, T> getFuzzy();

    /**
     * returns the aggregation function
     *
     * @return aggregation function
     */
    IAggregation getAggregation();

    /**
     * returns the variable builder function
     *
     * @return variable builder function
     */
    IVariableBuilder getVariableBuilder();

    /**
     * returns amultimap with literal-rule functor
     * and rle objects
     *
     * @return multimap
     */
    Multimap<IPath, IRule> getRules();

}
