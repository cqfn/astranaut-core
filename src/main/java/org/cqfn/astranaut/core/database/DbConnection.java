package org.cqfn.astranaut.core.database;

import org.cqfn.astranaut.core.Factory;
import org.cqfn.astranaut.core.Node;

import java.util.Optional;

public interface DbConnection<I, R> {
    void drop();

    void close();

    Optional<R> addVertex(I node);

    Node getNode(R vertex, Factory factory);
}
