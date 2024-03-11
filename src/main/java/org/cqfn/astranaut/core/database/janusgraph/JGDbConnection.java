package org.cqfn.astranaut.core.database.janusgraph;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.io.binary.TypeSerializerRegistry;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyVertexProperty;
import org.apache.tinkerpop.gremlin.util.ser.GraphBinaryMessageSerializerV1;
import org.cqfn.astranaut.core.Builder;
import org.cqfn.astranaut.core.Factory;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.hash.AbsoluteHash;
import org.cqfn.astranaut.core.database.DbConnection;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class JGDbConnection implements DbConnection<JGNode, Vertex> {

    private Cluster cluster;

    private GraphTraversalSource g;

    private final AbsoluteHash hasher = new AbsoluteHash();

    public static void main(String[] args) {
        new JGDbConnection("172.28.162.16", 8182);
    }

    public JGDbConnection(String ip, int port) {
        connect(ip, port);
    }

    public GraphTraversalSource getG() {
        return g;
    }

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
    public Optional<Vertex> addVertex(JGNode node) {
        final int hash = hasher.calculate(node);
        if (!hasEqualRoot(hash)) {
            return Optional.of(this.addVertex(node, -1));
        } else
            return Optional.empty();
    }

    public Vertex addVertex(JGNode node, int index) {
        GraphTraversal<Vertex, Vertex> thisVertexGT = g.addV();
        final int hash = hasher.calculate(node);
        thisVertexGT.property(PName.HASH.name(), hash);
        for (Map.Entry<PName, Object> entry: node.properties.entrySet()) {
            thisVertexGT.property(entry.getKey().toString(), entry.getValue());
        }
        if (index != -1) {
            thisVertexGT.property(PName.INDEX.name(), index);
        } else {
            thisVertexGT.property(PName.ROOT.name(), true);
        }
        Vertex thisVertex = thisVertexGT.next();
        for (int i = 0; i < node.children.size(); i++) {
            GraphTraversal<Vertex, Edge> edgeGT = g.V(thisVertex).addE("ast");
            Vertex childVertex = addVertex(node.children.get(i), i);
            edgeGT.to(childVertex).next();
        }
        return Objects.requireNonNull(thisVertex);
    }

    public boolean hasEqual(final Node node) {
        return hasEqual(hasher.calculate(node));
    }

    public boolean hasEqual(final int hash) {
        GraphTraversal<Vertex, Vertex> hashSearch = g.V().has(PName.HASH.name(), hash);
        return !hashSearch.toList().isEmpty();
    }

    public boolean hasEqualRoot(final int hash) {
        GraphTraversal<Vertex, Vertex> hashSearch = g.V()
            .has(PName.HASH.name(), hash)
            .has("ROOT", true);
        return !hashSearch.toList().isEmpty();
    }

    @Override
    public Node getNode(Vertex vertex, Factory factory) {
        List<Vertex> childrenVertices = this.getG().V(vertex.id()).outE().inV().toList();
        Vector<Node> childrenVector = new Vector<>();
        childrenVector.setSize(childrenVertices.size());
        for (Vertex childVertex: childrenVertices) {
            Node child = getNode(childVertex, factory);
            if (childVertex.property("INDEX") instanceof EmptyVertexProperty) {
                childrenVector.add(child);
            } else {
                childrenVector.add((Integer) childVertex.property("INDEX").value(), child);
            }
        }

        final Map<PName, Object> properties = new HashMap<>();
        Iterator<VertexProperty<Object>> vpIterator = vertex.properties();
        while (vpIterator.hasNext()) {
            VertexProperty<Object> vertexProperty = vpIterator.next();
            properties.put(PName.valueOf(vertexProperty.label()), vertexProperty.value());
        }

        JGNode node = new JGNode(properties, factory);

        Builder builder = factory.createBuilder((String) vertex.property("TYPE").value());
        builder.setFragment(node.getFragment());
        builder.setData(node.getData());
        childrenVector.removeIf(Objects::isNull);

        //TODO:: this fails because of actions. need to process them separately
        builder.setChildrenList(childrenVector);

        return builder.createNode();
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
