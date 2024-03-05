package org.cqfn.astranaut.core.database;

import org.cqfn.astranaut.core.Factory;
import org.cqfn.astranaut.core.Node;

public interface DbConnection<I, R> {
    void drop();

    void close();

    R addVertex(I node);

    Node getNode(R vertex, Factory factory);
}
