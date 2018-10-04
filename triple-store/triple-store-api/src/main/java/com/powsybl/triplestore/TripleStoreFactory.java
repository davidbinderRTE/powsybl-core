package com.powsybl.triplestore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.powsybl.commons.util.ServiceLoaderCache;

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

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public final class TripleStoreFactory {

    private static final ServiceLoaderCache<TripleStoreFactoryService> LOADER = new ServiceLoaderCache(
            TripleStoreFactoryService.class);

    private TripleStoreFactory() {
    }

    public static TripleStore create() {
        return create(DEFAULT_IMPLEMENTATION);
    }

    public static TripleStore create(String impl) {
        Objects.requireNonNull(impl);
        for (TripleStoreFactoryService ts : LOADER.getServices()) {
            if (ts.implementation().equals(impl)) {
                return ts.create();
            }
        }
        return null;
    }

    public static List<String> allImplementations() {
        return LOADER.getServices().stream().map(TripleStoreFactoryService::implementation)
                .collect(Collectors.toList());
    }

    public static List<String> implementationsWorkingWithNestedGraphClauses() {
        return LOADER.getServices().stream().filter(TripleStoreFactoryService::worksWithNestedGraphClauses)
                .map(TripleStoreFactoryService::implementation).collect(Collectors.toList());
    }

    public static List<String> implementationsBadNestedGraphClauses() {
        return LOADER.getServices().stream().filter(ts -> !ts.worksWithNestedGraphClauses())
                .map(TripleStoreFactoryService::implementation).collect(Collectors.toList());
    }

    public static List<String> onlyDefaultImplementation() {
        return Arrays.asList(DEFAULT_IMPLEMENTATION);
    }

    public static String defaultImplementation() {
        return DEFAULT_IMPLEMENTATION;
    }

    private static final String DEFAULT_IMPLEMENTATION = "rdf4j";
}
