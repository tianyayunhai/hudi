/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hudi.utils;

import org.apache.hudi.client.utils.LazyConcatenatingIterator;
import org.apache.hudi.common.util.collection.ClosableIterator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class TestLazyConcatenatingIterator {

  int initTimes;
  int closeTimes;

  private class MockClosableIterator implements ClosableIterator {

    Iterator<Integer> iterator;

    public MockClosableIterator(Iterator<Integer> iterator) {
      initTimes++;
      this.iterator = iterator;
    }

    @Override
    public void close() {
      closeTimes++;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public Object next() {
      return iterator.next();
    }
  }

  // Simple test for iterator concatenation
  @Test
  public void testConcatBasic() {
    Supplier<ClosableIterator<Integer>> i1 = () -> new MockClosableIterator(Arrays.asList(5, 3, 2, 1).iterator());
    Supplier<ClosableIterator<Integer>> i2 = () -> new MockClosableIterator(Collections.emptyIterator()); // empty iterator
    Supplier<ClosableIterator<Integer>> i3 = () -> new MockClosableIterator(Collections.singletonList(3).iterator());

    LazyConcatenatingIterator<Integer> ci = new LazyConcatenatingIterator<>(Arrays.asList(i1, i2, i3));

    assertEquals(0, initTimes);

    List<Integer> allElements = new ArrayList<>();
    int count = 0;
    while (ci.hasNext()) {
      count++;
      if (count == 1) {
        assertEquals(1, initTimes);
        assertEquals(0, closeTimes);
      }
      if (count == 5) {
        assertEquals(3, initTimes);
        assertEquals(2, closeTimes);
      }
      allElements.add(ci.next());
    }

    assertEquals(3, initTimes);
    assertEquals(3, closeTimes);

    assertEquals(5, allElements.size());
    assertEquals(Arrays.asList(5, 3, 2, 1, 3), allElements);
  }

  @Test
  public void testConcatError() {
    Supplier<ClosableIterator<Integer>> i1 = () -> new MockClosableIterator(Collections.emptyIterator()); // empty iterator

    LazyConcatenatingIterator<Integer> ci = new LazyConcatenatingIterator<>(Collections.singletonList(i1));
    assertFalse(ci.hasNext());
    try {
      ci.next();
      fail("expected error for empty iterator");
    } catch (IllegalStateException e) {
      //
    }
  }

}
