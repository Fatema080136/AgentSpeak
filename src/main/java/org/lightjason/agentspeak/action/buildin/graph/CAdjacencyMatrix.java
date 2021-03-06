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

package org.lightjason.agentspeak.action.buildin.graph;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import com.codepoetics.protonpack.StreamUtils;
import edu.uci.ics.jung.graph.AbstractGraph;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.action.buildin.math.blas.EType;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * creates from a graph the adjacency matrix.
 * The action converts graphs into a matrix
 * based on a cost-map, the first argument can be
 * a string with dense or sparse for defining the
 * returned matrix, after that a cost-map cann be set
 * or a fixed numerical value for defining the cost
 * of an edge, all other arguments are graphs,
 * for each graph the matrix and vertex list is returned,
 * the action never fails
 *
 * @code
    [M1|V1|M2|V2] = graph/adjacencymatrix( "dense|sparse", CostMap, Graph1, Graph2 );
    [M3|V3|M4|V4] = graph/adjacencymatrix( CostMap, Graph1, Graph2 );
    [M1|V1|M2|V2] = graph/adjacencymatrix( "dense|sparse", 1, Graph1, Graph2 );
    [M3|V3|M4|V4] = graph/adjacencymatrix( 1, Graph1, Graph2 );
 * @endcode
 * @note the cost-map does not need an entry for each edge
 * non-existing edges have got on default zero costs with 1
 * @see https://en.wikipedia.org/wiki/Adjacency_matrix
 */
public final class CAdjacencyMatrix extends IBuildinAction
{
    @Override
    public final int minimalArgumentNumber()
    {
        return 1;
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                         final List<ITerm> p_annotation )
    {
        final List<ITerm> l_arguments = CCommon.flatcollection( p_argument ).collect( Collectors.toList() );
        if ( l_arguments.size() < 2 )
            return CFuzzyValue.from( false );

        // get result matrix type
        final EType l_type;
        final int l_skip;
        if ( !CCommon.rawvalueAssignableTo( l_arguments.get( 0 ), String.class ) )
        {
            l_skip = 0;
            l_type = EType.DENSE;
        }
        else
        {
            l_skip = 1;
            l_type = l_arguments.get( 0 ).<String>raw().equalsIgnoreCase( "sparse" )
                     ? EType.SPARSE
                     : EType.DENSE;
        }


        // cost definition
        final Map<?, Number> l_cost = CCommon.rawvalueAssignableTo( l_arguments.get( l_skip ), Map.class )
                                      ? l_arguments.get( l_skip ).<Map<?, Number>>raw()
                                      : Collections.emptyMap();

        final double l_defaultcost = CCommon.rawvalueAssignableTo( l_arguments.get( l_skip ), Number.class )
                                     ? l_arguments.get( l_skip ).<Number>raw().doubleValue()
                                     : 1;


        // create adjcency matrix
        l_arguments.stream()
                   .skip( 1 + l_skip )
                   .map( ITerm::<AbstractGraph<Object, Object>>raw )
                   .map( i -> CAdjacencyMatrix.apply(
                                  i,
                                  l_arguments.get( l_skip ).<Map<?, Number>>raw(),
                                  l_defaultcost,
                                  l_type
                   ) )
                   .forEach( i -> {
                       p_return.add( CRawTerm.from( i.getLeft() ) );
                       p_return.add( CRawTerm.from( i.getRight() ) );
                   } );

        return CFuzzyValue.from( true );
    }

    /**
     * converts a graph into an adjacency matrix
     *
     * @param p_graph graph
     * @param p_cost map with edges and costs
     * @param p_defaultcost default cost value (on non-existing map values)
     * @param p_type matrix type
     * @return pair of double matrix and vertices
     */
    private static Pair<DoubleMatrix2D, Collection<?>> apply( final AbstractGraph<Object, Object> p_graph, final Map<?, Number> p_cost,
                                                              final double p_defaultcost, final EType p_type )
    {
        // index map for matching vertex to index position within matrix
        final Map<Object, Integer> l_index = new HashMap<>();

        // extract vertices from edges
        p_graph.getEdges()
            .stream()
            .map( p_graph::getEndpoints )
            .flatMap( i -> Stream.of( i.getFirst(), i.getSecond() ) )
            .forEach( i -> l_index.putIfAbsent( i, 0 ) );

        // define for each vertex an index number in [0, size)
        StreamUtils.zip(
            l_index.keySet().stream(),
            IntStream.range( 0, l_index.size() ).boxed(),
            ImmutablePair::new
        ).forEach( i -> l_index.put( i.getKey(), i.getValue() ) );

        final DoubleMatrix2D l_matrix;
        switch ( p_type )
        {
            case SPARSE:
                l_matrix = new SparseDoubleMatrix2D( l_index.size(), l_index.size() );
                break;

            default:
                l_matrix = new DenseDoubleMatrix2D( l_index.size(), l_index.size() );
        }

        // map costs to matrix
        p_graph.getEdges()
               .stream()
               .map( i -> new ImmutablePair<>( p_graph.getEndpoints( i ), p_cost.getOrDefault( i, p_defaultcost ).doubleValue() ) )
               .forEach( i -> l_matrix.setQuick(
                                  l_index.get( i.getLeft().getFirst() ), l_index.get( i.getLeft().getSecond() ),
                                  i.getRight() + l_matrix.getQuick( l_index.get( i.getLeft().getFirst() ), l_index.get( i.getLeft().getSecond() ) )
               ) );

        return new ImmutablePair<>( l_matrix, new ArrayList<>( l_index.keySet() ) );
    }
}
