package com.powsybl.triplestore.api;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

import com.powsybl.commons.datasource.DataSource;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public interface TripleStore {

    void read(String base, String name, InputStream is);

    void write(DataSource ds);

    void dump(PrintStream out);

    void dump(Consumer<String> liner);

    void clear(String context);

    void defineQueryPrefix(String prefix, String cimNamespace);

    PropertyBags query(String query);

    void add(String graph, String type, PropertyBags objects);
}
