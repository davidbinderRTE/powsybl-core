package com.powsybl.triplestore.test;

/*
 * #%L
 * Triple stores for CGMES models
 * %%
 * Copyright (C) 2017 - 2018 RTE (http://rte-france.com)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.powsybl.triplestore.api.QueryCatalog;
import com.powsybl.triplestore.api.TripleStoreException;
import com.powsybl.triplestore.api.TripleStoreFactory;
import com.powsybl.triplestore.test.TripleStoreTester.Expected;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public class FoafOptionalsTest {

    private static InputStream resourceStream(String resource) {
        return ClassLoader.getSystemResourceAsStream(resource);
    }

    @BeforeClass
    public static void setUp() throws TripleStoreException, IOException {
        queries = new QueryCatalog("foaf/foaf-optionals.sparql");
        queries.load(resourceStream(queries.resource()));
        String base = "foaf";
        tester = new TripleStoreTester(TripleStoreFactory.allImplementations(), base, "foaf/abc-nicks.ttl");
        tester.load();
    }

    @Test
    public void testAllQuads() throws Exception {
        Expected expected = new Expected().expect(
                "o",
                "Alice",
                "Bob",
                "SweetCaroline",
                "Wonderland",
                "mailto:alice@example",
                "mailto:bob@example",
                "mailto:carol@example");
        tester.testQuery("SELECT * { GRAPH ?g { ?s ?p ?o }}", expected);
    }

    @Test
    public void testOptional() throws Exception {
        Expected expected = new Expected().expect("name", "Alice", "Bob", null);
        tester.testQuery(queries.get("optional"), expected);
    }

    @Test
    public void testMultipleOptionals() throws Exception {
        Expected expected = new Expected()
                .expect("name", "Alice", "Bob", null)
                .expect("nick", "SweetCaroline", "Wonderland", null);
        tester.testQuery(queries.get("multipleOptionals"), expected);
    }

    @Test
    public void testMultipleOptionalsSameVariable() throws Exception {
        Expected expected = new Expected().expect("label", "Alice", "Bob", "SweetCaroline");
        tester.testQuery(queries.get("multipleOptionalsSameVariable"), expected);
    }

    @Test
    public void testOptionalWithUnion() throws Exception {
        Expected expected = new Expected()
                .expect("label", "Alice", "Bob", "SweetCaroline", "Wonderland");
        tester.testQuery(queries.get("optionalWithUnion"), expected);
    }

    @Test
    public void testNestedOptionals() throws Exception {
        Expected expected = new Expected()
                .expect("name", "Alice", null, null)
                .expect("nick", "SweetCaroline", "Wonderland", null);
        tester.testQuery(queries.get("nestedOptionals"), expected);
    }

    @Test
    public void testOptionalAnd() throws Exception {
        Expected expected = new Expected()
                .expect("name", "Alice", null, null)
                .expect("nick", "Wonderland", null, null);
        tester.testQuery(queries.get("optionalAnd"), expected);
    }

    @Test
    public void testOptionalNestedFilter() throws Exception {
        Expected expected = new Expected().expect("name", "Bob", null, null);
        tester.testQuery(queries.get("optionalNestedFilter"), expected);
    }

    @Test
    public void testOptionalThenFilter() throws Exception {
        Expected expected = new Expected().expect("name", "Bob");
        tester.testQuery(queries.get("optionalThenFilter"), expected);
    }

    @Test
    public void testOptionalNotBound() throws Exception {
        Expected expected = new Expected().expect("mbox", "mailto:carol@example");
        tester.testQuery(queries.get("optionalNotBound"), expected);
    }

    @Test
    public void testFilterNotExists() throws Exception {
        // Equivalent to optional not bound, the syntax is allowed since SPARQL 1.1
        Expected expected = new Expected().expect("mbox", "mailto:carol@example");
        tester.testQuery(queries.get("filterNotExists"), expected);
    }

    @Test
    public void testMinus() throws Exception {
        // Similar to filter not exists, subtract friends with name from friends with
        // mailbox
        Expected expected = new Expected().expect("mbox", "mailto:carol@example");
        tester.testQuery(queries.get("minus"), expected);
    }

    private static TripleStoreTester tester;
    private static QueryCatalog queries;
}
