/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.util;

import com.graphhopper.coll.GHIntLongHashMap;
import com.graphhopper.routing.Router;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

/**
 * @author Peter Karich
 */
public class GHUtilityTest {

    @Test
    public void testEdgeStuff() {
        assertEquals(2, GHUtility.createEdgeKey(1, false));
        assertEquals(3, GHUtility.createEdgeKey(1, true));
    }

    @Test
    public void testZeroValue() {
        GHIntLongHashMap map1 = new GHIntLongHashMap();
        assertFalse(map1.containsKey(0));
        // assertFalse(map1.containsValue(0));
        map1.put(0, 3);
        map1.put(1, 0);
        map1.put(2, 1);

        // assertTrue(map1.containsValue(0));
        assertEquals(3, map1.get(0));
        assertEquals(0, map1.get(1));
        assertEquals(1, map1.get(2));

        // instead of assertEquals(-1, map1.get(3)); with hppc we have to check before:
        assertTrue(map1.containsKey(0));

        // trove4j behaviour was to return -1 if non existing:
//        TIntLongHashMap map2 = new TIntLongHashMap(100, 0.7f, -1, -1);
//        assertFalse(map2.containsKey(0));
//        assertFalse(map2.containsValue(0));
//        map2.add(0, 3);
//        map2.add(1, 0);
//        map2.add(2, 1);
//        assertTrue(map2.containsKey(0));
//        assertTrue(map2.containsValue(0));
//        assertEquals(3, map2.get(0));
//        assertEquals(0, map2.get(1));
//        assertEquals(1, map2.get(2));
//        assertEquals(-1, map2.get(3));
    }

    @Test
    public void testGetProblems() {
        Graph g = mock(Graph.class);
        when(g.getNodes()).thenReturn(1);
        NodeAccess na = mock(NodeAccess.class);
        when(g.getNodeAccess()).thenReturn(na);
        when(na.getLat(0)).thenReturn(91.0);
        when(na.getLon(0)).thenReturn(181.0);
        EdgeExplorer explorer = mock(EdgeExplorer.class);
        when(g.createEdgeExplorer()).thenReturn(explorer);
        EdgeIterator iter = mock(EdgeIterator.class);
        when(explorer.setBaseNode(0)).thenReturn(iter);
        when(iter.next()).thenReturn(false);

        List<String> problems = GHUtility.getProblems(g);

        assertEquals(true, problems.contains("latitude is not within its bounds 91.0"));
        //assertEquals(true, problems.contains("longitude is not within its bounds 181.0"));
        //verify(iter, never()).getAdjNode();
    }   

    @Test
    public void testGetCommonNode() {
        BaseGraph baseGraph = mock(BaseGraph.class);
        int edge1 = 5;
        int edge2 = 10;
        EdgeIterator e1 = mock(EdgeIterator.class);
        EdgeIterator e2 = mock(EdgeIterator.class);
        when(baseGraph.getEdgeIteratorState(edge1, Integer.MIN_VALUE)).thenReturn(e1);
        when(baseGraph.getEdgeIteratorState(edge2, Integer.MIN_VALUE)).thenReturn(e2);
        when(e1.getBaseNode()).thenReturn(1);
        when(e1.getAdjNode()).thenReturn(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
        () -> GHUtility.getCommonNode(baseGraph, edge1, edge2));
    
        assertEquals("edge1: 5 is a loop at node 1", exception.getMessage());
    }
}

//TODO Test GHUtility getProblems()


//TODO Test Router GHResponse