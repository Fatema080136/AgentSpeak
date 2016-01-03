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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 */

package lightjason.inconsistency.metric;


import lightjason.agent.IAgent;
import lightjason.common.CPath;

import java.util.Collection;


/**
 * metric interface of the inconsistency structure
 *
 * @see http://en.wikipedia.org/wiki/Metric_space
 */
public interface IMetric
{

    /**
     * calculates the metric value between two objects
     *
     * @param p_first first object
     * @param p_second second object
     * @return double metric
     */
    double calculate( final IAgent p_first, final IAgent p_second );


    /**
     * returns the selectors
     *
     * @return selector
     */
    Collection<CPath> getSelector();

}
