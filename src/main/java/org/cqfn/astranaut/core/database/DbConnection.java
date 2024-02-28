package org.cqfn.astranaut.core.database;

public interface DbConnection<I, R> {
    void connect(String ip, int port);

    void drop();

    void close();

    R addVertex(I node);
}
