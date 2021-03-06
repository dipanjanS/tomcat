/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.websocket.server;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestUriTemplate {

    @Test
    public void testBasic() throws Exception {
        UriTemplate t = new UriTemplate("/{a}/{b}");
        Map<String,String> result = t.match(new UriTemplate("/foo/bar"));

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.containsKey("a"));
        Assert.assertTrue(result.containsKey("b"));
        Assert.assertEquals("foo", result.get("a"));
        Assert.assertEquals("bar", result.get("b"));
    }


    @Test
    public void testOneOfTwo() throws Exception {
        UriTemplate t = new UriTemplate("/{a}/{b}");
        Map<String,String> result = t.match(new UriTemplate("/foo"));
        Assert.assertNull(result);
    }


    @Test(expected=java.lang.IllegalArgumentException.class)
    public void testBasicPrefix() throws Exception {
        @SuppressWarnings("unused")
        UriTemplate t = new UriTemplate("/x{a}/y{b}");
    }


    @Test(expected=java.lang.IllegalArgumentException.class)
    public void testPrefixOneOfTwo() throws Exception {
        UriTemplate t = new UriTemplate("/x{a}/y{b}");
        t.match(new UriTemplate("/xfoo"));
    }


    @Test(expected=java.lang.IllegalArgumentException.class)
    public void testPrefixTwoOfTwo() throws Exception {
        UriTemplate t = new UriTemplate("/x{a}/y{b}");
        t.match(new UriTemplate("/ybar"));
    }


    @Test(expected=java.lang.IllegalArgumentException.class)
    public void testQuote1() throws Exception {
        UriTemplate t = new UriTemplate("/.{a}");
        t.match(new UriTemplate("/yfoo"));
    }


    @Test(expected=java.lang.IllegalArgumentException.class)
    public void testQuote2() throws Exception {
        @SuppressWarnings("unused")
        UriTemplate t = new UriTemplate("/.{a}");
    }


    @Test
    public void testNoParams() throws Exception {
        UriTemplate t = new UriTemplate("/foo/bar");
        Map<String,String> result = t.match(new UriTemplate("/foo/bar"));

        Assert.assertEquals(0, result.size());
    }
}
