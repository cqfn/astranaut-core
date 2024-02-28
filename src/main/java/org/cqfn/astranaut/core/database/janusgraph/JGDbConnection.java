package org.cqfn.astranaut.core.database.janusgraph;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.binary.TypeSerializerRegistry;
import org.apache.tinkerpop.gremlin.util.ser.GraphBinaryMessageSerializerV1;
import org.cqfn.astranaut.core.database.DbConnection;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;

import java.util.Map;
import java.util.Objects;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class JGDbConnection implements DbConnection<JGNode, Vertex> {

    private Cluster cluster;

    private GraphTraversalSource g;

    public static void main(String[] args) {
        new JGDbConnection("172.28.162.16", 8182);
    }

    public JGDbConnection(String ip, int port) {
        connect(ip, port);
    }

    public GraphTraversalSource getG() {
        return g;
    }

    @Override
    public void connect(String ip, int port) {
        cluster = connectToJGDatabase(ip, port);
        System.out.println("Using cluster connection: " + cluster);
        g = getGraphTraversalSource(cluster);
        System.out.println("Using traversal source: " + g.toString());
    }

    @Override
    public void drop() {
        g.V().drop().iterate();
    }

    @Override
    public void close() {
        cluster.close();
    }

    @Override
    public Vertex addVertex(JGNode node) {
        GraphTraversal<Vertex, Vertex> thisVertexGT = g.addV();
        for (Map.Entry<JGNode.PName, String> entry: node.properties.entrySet()) {
            thisVertexGT.property(entry.getKey().toString(), entry.getValue());
        }
        Vertex thisVertex = thisVertexGT.next();
        for (JGNode child: node.children) {
            GraphTraversal<Vertex, Edge> edgeGT = g.V(thisVertex).addE("ast");
            Vertex childVertex = addVertex(child);
            edgeGT.to(childVertex).next();
        }
        return Objects.requireNonNull(thisVertex);
    }

    private static Cluster connectToJGDatabase(String ip, int port) {
        TypeSerializerRegistry typeSerializerRegistry = TypeSerializerRegistry.build()
            .addRegistry(JanusGraphIoRegistry.instance())
            .create();

        Cluster.Builder builder = Cluster.build();
        builder.addContactPoint(ip);
        builder.port(port);
        builder.serializer(new GraphBinaryMessageSerializerV1(typeSerializerRegistry));
        return builder.create();
    }

    private static GraphTraversalSource getGraphTraversalSource(Cluster cluster) {
        return traversal().withRemote(DriverRemoteConnection.using(cluster));
    }
}
