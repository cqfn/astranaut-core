package org.cqfn.astranaut.core.database.janusgraph;

import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.Position;
import org.cqfn.astranaut.core.database.DbNode;

import java.util.*;

public class JGNode implements DbNode {
    final Map<PName, String> properties = new HashMap<>();

    final List<JGNode> children = new ArrayList<>();

    public enum PName {
        BEGIN, END, FRAGMENT, TYPE, DATA, CHILD_COUNT, UUID
    }

    public JGNode(Node node) {
        Position begin = node.getFragment().getBegin();
        Position end = node.getFragment().getEnd();
        properties.put(PName.BEGIN, String.valueOf(begin.getIndex()));
        properties.put(PName.END, String.valueOf(end.getIndex()));
        properties.put(PName.FRAGMENT, String.valueOf(node.getFragment().getSource().getFragmentAsString(begin, end)));
        properties.put(PName.DATA, node.getData());
        properties.put(PName.TYPE, node.getType().getName());
        properties.put(PName.CHILD_COUNT, String.valueOf(node.getChildCount()));
        properties.put(PName.UUID, UUID.randomUUID().toString()); // TODO:: think about collisions
        for (final Node child: node.getChildrenList()) {
            children.add(new JGNode(child));
        }
    }
}
