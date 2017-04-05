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

package org.lightjason.agentspeak.action.buildin;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.lightjason.agentspeak.action.buildin.collection.list.CAdd;
import org.lightjason.agentspeak.action.buildin.collection.list.CComplement;
import org.lightjason.agentspeak.action.buildin.collection.list.CCreate;
import org.lightjason.agentspeak.action.buildin.collection.list.CFlat;
import org.lightjason.agentspeak.action.buildin.collection.list.CFlatConcat;
import org.lightjason.agentspeak.action.buildin.collection.list.CGet;
import org.lightjason.agentspeak.action.buildin.collection.list.CRange;
import org.lightjason.agentspeak.action.buildin.collection.list.CRemove;
import org.lightjason.agentspeak.action.buildin.collection.list.CReverse;
import org.lightjason.agentspeak.action.buildin.collection.list.CSet;
import org.lightjason.agentspeak.action.buildin.collection.list.CSubList;
import org.lightjason.agentspeak.action.buildin.collection.list.CUnique;
import org.lightjason.agentspeak.action.buildin.collection.list.CZip;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * test collection list
 */
public final class TestCActionCollectionList
{

    /**
     * test create empty list
     */
    @Test
    public final void testcreateempty()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CCreate().execute(
            null,
            false,
            Collections.emptyList(),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof List<?> );
        Assert.assertTrue( l_return.get( 0 ).<List<?>>raw().isEmpty() );
    }


    /**
     * test create empty synchronized list
     */
    @Test
    public final void testcreateemptysynchronize()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CCreate().execute(
            null,
            true,
            Collections.emptyList(),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof List<?> );
        Assert.assertTrue( l_return.get( 0 ).<List<?>>raw().isEmpty() );
        Assert.assertEquals( l_return.get( 0 ).raw().getClass(), Collections.synchronizedList( Collections.emptyList() ).getClass() );
    }


    /**
     * test create non-empty list
     */
    @Test
    public final void testcreate()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CCreate().execute(
            null,
            false,
            Stream.of( "a", 1, "b", true ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof List<?> );
        Assert.assertEquals( l_return.get( 0 ).<List<?>>raw().size(), 4 );

        final List<?> l_list = l_return.get( 0 ).raw();

        Assert.assertEquals( l_list.get( 0 ), "a" );
        Assert.assertEquals( l_list.get( 1 ), 1 );
        Assert.assertEquals( l_list.get( 2 ), "b" );
        Assert.assertEquals( l_list.get( 3 ), true );
    }


    /**
     * test complement action
     */
    @Test
    public final void testcomplement()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CComplement().execute(
            null,
            false,
            Stream.of(
                CRawTerm.from( Stream.of( "a", "b", 1, 2 ).collect( Collectors.toList() ) ),
                CRawTerm.from( Stream.of( "x", "y", 4, "a", 5, 1 ).collect( Collectors.toList() ) )
            ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertTrue( CCommon.rawvalueAssignableTo( l_return.get( 0 ), List.class ) );
        Assert.assertEquals( l_return.get( 0 ).<List<?>>raw().get( 0 ), "b" );
        Assert.assertEquals( l_return.get( 0 ).<List<?>>raw().get( 1 ), 2 );
    }


    /**
     * test get action
     */
    @Test
    public final void testget()
    {
        final List<ITerm> l_return = new ArrayList<>();
        final List<?> l_list = Stream.of( "a", 1, "b", true, "foobar", 56.78 ).collect( Collectors.toList() );

        new CGet().execute(
            null,
            false,
            Stream.of( CRawTerm.from( l_list ), CRawTerm.from( 1 ), CRawTerm.from( 4 ), CRawTerm.from( 5 ) ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 3 );
        Assert.assertEquals( l_return.get( 0 ).<Number>raw(), 1 );
        Assert.assertEquals( l_return.get( 1 ).raw(), "foobar" );
        Assert.assertEquals( l_return.get( 2 ).<Number>raw(), 56.78 );
    }

    /**
     * test reverse action
     */
    @Test
    public final void testreverse()
    {
        final List<ITerm> l_return = new ArrayList<>();
        final List<?> l_list = IntStream.range( 0, 10 ).mapToObj( i -> Math.random() ).collect( Collectors.toList() );

        new CReverse().execute(
            null,
            false,
            Stream.of( CRawTerm.from( l_list ) ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertArrayEquals( l_return.stream().map( ITerm::raw ).toArray(), Lists.reverse( l_list ).toArray() );
    }


    /**
     * test remove action
     */
    @Test
    public final void testremove()
    {
        final Random l_random = new Random();

        final List<?> l_elements = IntStream.range( 0, l_random.nextInt( 100 ) + 1 ).map( i -> l_random.nextInt() ).boxed().collect( Collectors.toList() );
        final List<?> l_list = new ArrayList<>( l_elements );
        final List<Integer> l_index = new ArrayList<>(
                                        IntStream.range( 0, l_list.size() / 3 )
                                                 .map( i ->  l_random.nextInt( l_list.size() ) )
                                                 .boxed()
                                                 .collect( Collectors.toSet() )
                                      );

        final int l_startsize = l_list.size();
        final List<ITerm> l_return = new ArrayList<>();

        new CRemove().execute(
            null,
            false,
            Stream.concat(
                Stream.of( l_list ),
                l_index.stream()
            ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_startsize, l_list.size() + l_index.size() );
        Assert.assertTrue(
            l_index.parallelStream()
                   .map( l_elements::get )
                   .allMatch( i -> l_return.parallelStream().map( ITerm::<Number>raw ).anyMatch( j -> j.equals( i ) ) )
        );
    }


    /**
     * test set action
     */
    @Test
    public final void testset()
    {
        final List<?> l_list1 = Stream.of( "" ).collect( Collectors.toList() );
        final List<?> l_list2 = Stream.of( "abc", 123, true ).collect( Collectors.toList() );

        new CSet().execute(
            null,
            false,
            Stream.of( CRawTerm.from( 0 ), CRawTerm.from( "xxx" ), CRawTerm.from( l_list1 ), CRawTerm.from( l_list2 ) ).collect( Collectors.toList() ),
            Collections.emptyList(),
            Collections.emptyList()
        );

        Assert.assertEquals( l_list1.size(), 1 );
        Assert.assertEquals( l_list1.get( 0 ), "xxx" );

        Assert.assertEquals( l_list2.size(), 3 );
        Assert.assertEquals( l_list2.get( 0 ), "xxx" );
    }


    /**
     * test add action
     */
    @Test
    public final void testadd()
    {
        final List<?> l_list = new ArrayList<>();

        new CAdd().execute(
            null,
            false,
            Stream.of( CRawTerm.from( "xyz" ), CRawTerm.from( l_list ) ).collect( Collectors.toList() ),
            Collections.emptyList(),
            Collections.emptyList()
        );

        Assert.assertEquals( l_list.size(), 1 );
        Assert.assertEquals( l_list.get( 0 ), "xyz" );
    }


    /**
     * test range error
     */
    @Test
    public final void testrangeerror()
    {
        Assert.assertFalse(
            new CRange().execute(
                null,
                false,
                Stream.of().map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList(),
                Collections.emptyList()
            ).value()
        );
    }


    /**
     * test range
     */
    @Test
    public final void testrange()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CRange().execute(
            null,
            false,
            Stream.of( 0, 5, 7, 9 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        new CRange().execute(
            null,
            true,
            Stream.of( 1, 7 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 3 );

        Assert.assertArrayEquals( l_return.get( 0 ).<List<?>>raw().toArray(), IntStream.range( 0, 5 ).boxed().toArray() );
        Assert.assertArrayEquals( l_return.get( 1 ).<List<?>>raw().toArray(), IntStream.range( 7, 9 ).boxed().toArray() );

        Assert.assertArrayEquals( l_return.get( 2 ).<List<?>>raw().toArray(), IntStream.range( 1, 7 ).boxed().toArray() );
        Assert.assertEquals( l_return.get( 2 ).<List<?>>raw().getClass(), Collections.synchronizedList( Collections.emptyList() ).getClass() );
    }

    /**
     * test sublist error
     */
    @Test
    public final void testsublisterror()
    {
        Assert.assertFalse(
            new CSubList().execute(
                 null,
                 false,
                 Stream.of( new ArrayList<>() ).map( CRawTerm::from ).collect( Collectors.toList() ),
                 Collections.emptyList(),
                 Collections.emptyList()
            ).value()
        );
    }

    /**
     * test sublist
     */
    @Test
    public final void testsublist()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CSubList().execute(
            null,
            false,
            Stream.of( Stream.of( "ax", "bx", "c", 1, 2, 3 ).collect( Collectors.toList() ), 0, 2, 2, 4 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        new CSubList().execute(
            null,
            true,
            Stream.of( Stream.of( 8, 9, 10 ).collect( Collectors.toList() ), 1, 2 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 3 );

        Assert.assertArrayEquals( l_return.get( 0 ).<List<?>>raw().toArray(), Stream.of( "ax", "bx" ).toArray() );
        Assert.assertArrayEquals( l_return.get( 1 ).<List<?>>raw().toArray(), Stream.of( "c", 1 ).toArray() );
        Assert.assertArrayEquals( l_return.get( 2 ).<List<?>>raw().toArray(), Stream.of( 9 ).toArray() );
    }


    /**
     * test flat action
     */
    @Test
    public final void testflat()
    {
        final Random l_random = new Random();

        final List<ITerm> l_return = new ArrayList<>();
        final List<?> l_list = IntStream.range( 0, l_random.nextInt( 100 ) + 1 )
                                        .mapToObj( i -> RandomStringUtils.random( l_random.nextInt( 100 ) + 1 ) )
                                        .collect( Collectors.toList() );

        new CFlat().execute(
            null,
            false,
            l_list.stream().map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), l_list.size() );
        Assert.assertArrayEquals( l_return.stream().map( ITerm::raw ).toArray(), l_list.toArray() );
    }


    /**
     * test flatconcat action
     */
    @Test
    public final void testflatconcat()
    {
        final Random l_random = new Random();

        final List<ITerm> l_return = new ArrayList<>();
        final List<?> l_list = IntStream.range( 0, l_random.nextInt( 100 ) + 1 )
                                        .mapToObj( i -> RandomStringUtils.random( l_random.nextInt( 100 ) + 1 ) )
                                        .collect( Collectors.toList() );

        new CFlatConcat().execute(
            null,
            false,
            l_list.stream().map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertArrayEquals( l_return.get( 0 ).<List<?>>raw().toArray(), l_list.toArray() );
    }


    /**
     * test zip action error
     */
    @Test
    public final void testziperror()
    {
        Assert.assertFalse(
            new CZip().execute(
                null,
                false,
                Stream.of( "" ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList(),
                Collections.emptyList()
            ).value()
        );
    }


    /**
     * test zip action
     */
    @Test
    public final void testzip()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CZip().execute(
            null,
            false,
            IntStream.range( 0, 6 ).boxed().map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertEquals( l_return.get( 0 ).<List<?>>raw().size(), 3 );

        Assert.assertEquals( l_return.get( 0 ).<List<AbstractMap.SimpleEntry<?, ?>>>raw().get( 0 ).getKey(), 0 );
        Assert.assertEquals( l_return.get( 0 ).<List<AbstractMap.SimpleEntry<?, ?>>>raw().get( 0 ).getValue(), 3 );

        Assert.assertEquals( l_return.get( 0 ).<List<AbstractMap.SimpleEntry<?, ?>>>raw().get( 1 ).getKey(), 1 );
        Assert.assertEquals( l_return.get( 0 ).<List<AbstractMap.SimpleEntry<?, ?>>>raw().get( 1 ).getValue(), 4 );

        Assert.assertEquals( l_return.get( 0 ).<List<AbstractMap.SimpleEntry<?, ?>>>raw().get( 2 ).getKey(), 2 );
        Assert.assertEquals( l_return.get( 0 ).<List<AbstractMap.SimpleEntry<?, ?>>>raw().get( 2 ).getValue(), 5 );



        new CZip().execute(
            null,
            true,
            Stream.of( 1, 2 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 2 );
        Assert.assertEquals( l_return.get( 1 ).<List<?>>raw().getClass(), Collections.synchronizedList( Collections.emptyList() ).getClass() );

    }


    /**
     * test unique action
     */
    @Test
    public final void testunique()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CUnique().execute(
            null,
            false,
            Stream.of( 1, 1, 3, 4, 5, 5 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertEquals( l_return.get( 0 ).<List<?>>raw().size(), 4 );
        Assert.assertArrayEquals( l_return.get( 0 ).<List<?>>raw().toArray(), Stream.of( 1, 3, 4, 5 ).toArray() );

        new CUnique().execute(
            null,
            true,
            Stream.of( 1 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return,
            Collections.emptyList()
        );

        Assert.assertEquals( l_return.size(), 2 );
        Assert.assertEquals( l_return.get( 1 ).<List<?>>raw().getClass(), Collections.synchronizedList( Collections.emptyList() ).getClass() );
    }


    /**
     * test call
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        final TestCActionCollectionList l_test = new TestCActionCollectionList();

        l_test.testcreateempty();
        l_test.testcreateemptysynchronize();
        l_test.testcreate();
        l_test.testcomplement();
        l_test.testget();
        l_test.testreverse();
        l_test.testremove();
        l_test.testset();
        l_test.testadd();
        l_test.testrangeerror();
        l_test.testrange();
        l_test.testrangeerror();
        l_test.testsublist();
        l_test.testsublisterror();
        l_test.testflat();
        l_test.testflatconcat();
        l_test.testziperror();
        l_test.testzip();
        l_test.testunique();

    }

}
