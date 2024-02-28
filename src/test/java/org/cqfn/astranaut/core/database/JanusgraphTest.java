package org.cqfn.astranaut.core.database;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.database.janusgraph.JGDbConnection;
import org.cqfn.astranaut.core.database.janusgraph.JGNode;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("This tests should be run manually after DB server is up and corresponding IP and port are specified")
public class JanusgraphTest {
    private static final String IP = "172.28.162.16";

    private static final int PORT = 8182;

    /**
     * Test saving of the graph to the janusgraph database based on {@link LittleTrees#createStatementListWithThreeChildren()}.
     */
    @Test
    void testJGDbVertexStatementListWithThreeChildren() {
        JGDbConnection jgDbConnection = new JGDbConnection(IP, PORT);
        jgDbConnection.drop();
        Node node = LittleTrees.createStatementListWithThreeChildren();
        JGNode jgNode = new JGNode(node);
        jgDbConnection.addVertex(jgNode);
        Assertions.assertDoesNotThrow(() -> {
            GraphTraversal<Vertex, Vertex> statementBlock = jgDbConnection.getG().V()
                .has("TYPE", "StatementBlock");

            GraphTraversal<Vertex, Vertex> sbChildren = statementBlock
                .outE().inV();

            GraphTraversal<Vertex, Vertex> return_ = sbChildren
                .asAdmin().clone()
                .has("TYPE", "Return");

            GraphTraversal<Vertex, Vertex> returnVariableX = return_
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "Variable")
                .has("DATA", "x");

            GraphTraversal<Vertex, Vertex> expStatement = sbChildren
                .asAdmin().clone()
                .has("TYPE", "ExpressionStatement");

            GraphTraversal<Vertex, Vertex> simpleAssignment = expStatement
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "SimpleAssignment");

            GraphTraversal<Vertex, Vertex> literal1 = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "IntegerLiteral")
                .has("DATA", "1");

            GraphTraversal<Vertex, Vertex> literal2 = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "IntegerLiteral")
                .has("DATA", "2");

            GraphTraversal<Vertex, Vertex> variableY = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "Variable")
                .has("DATA", "y");

            GraphTraversal<Vertex, Vertex> variableX = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "Variable")
                .has("DATA", "x");

            returnVariableX.next();
            literal1.next();
            literal2.next();
            variableY.next();
            variableX.next();
        });

        jgDbConnection.close();
    }

    /**
     * Test saving of the graph with action to the janusgraph database based on {@link LittleTrees#createTreeWithDeleteAction()}.
     */
    @Test
    void testJGDbVertexTreeWithDeleteAction() {
        JGDbConnection jgDbConnection = new JGDbConnection(IP, PORT);
        jgDbConnection.drop();
        Node node = LittleTrees.createTreeWithDeleteAction();
        JGNode jgNode = new JGNode(node);
        jgDbConnection.addVertex(jgNode);
        Assertions.assertDoesNotThrow(() -> {
            GraphTraversal<Vertex, Vertex> statementBlock = jgDbConnection.getG().V()
                .has("TYPE", "StatementBlock");

            GraphTraversal<Vertex, Vertex> sbChildren = statementBlock
                .outE().inV();

            GraphTraversal<Vertex, Vertex> delete = sbChildren
                .asAdmin().clone()
                .has("TYPE", "Delete");

            GraphTraversal<Vertex, Vertex> expStatementDeleted = delete
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "ExpressionStatement");

            GraphTraversal<Vertex, Vertex> simpleAssignmentDeleted = expStatementDeleted
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "SimpleAssignment");

            GraphTraversal<Vertex, Vertex> literal2Deleted = simpleAssignmentDeleted
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "IntegerLiteral")
                .has("DATA", "2");

            GraphTraversal<Vertex, Vertex> variableYDeleted = simpleAssignmentDeleted
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "Variable")
                .has("DATA", "y");

            GraphTraversal<Vertex, Vertex> return_ = sbChildren
                .asAdmin().clone()
                .has("TYPE", "Return");

            GraphTraversal<Vertex, Vertex> returnVariableX = return_
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "Variable")
                .has("DATA", "x");

            GraphTraversal<Vertex, Vertex> expStatement = sbChildren
                .asAdmin().clone()
                .has("TYPE", "ExpressionStatement");

            GraphTraversal<Vertex, Vertex> simpleAssignment = expStatement
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "SimpleAssignment");

            GraphTraversal<Vertex, Vertex> literal1 = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "IntegerLiteral")
                .has("DATA", "1");

            GraphTraversal<Vertex, Vertex> variableX = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has("TYPE", "Variable")
                .has("DATA", "x");

            returnVariableX.next();
            literal1.next();
            variableX.next();
            literal2Deleted.next();
            variableYDeleted.next();
        });
        jgDbConnection.close();
    }
}
