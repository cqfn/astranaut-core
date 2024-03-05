package org.cqfn.astranaut.core.database.janusgraph;

import org.cqfn.astranaut.core.*;
import org.cqfn.astranaut.core.database.DbNode;

import java.util.*;

public class JGNode implements DbNode {
    final Map<PName, Object> properties = new HashMap<>();

    final List<JGNode> children = new ArrayList<>();

    private Factory factory = null;

    public enum PName {
        ROOT, BEGIN, END, SOURCE, TYPE, DATA, CHILD_COUNT, INDEX, UUID
    }

    public JGNode(Node node, boolean root) {
        Position begin = node.getFragment().getBegin();
        Position end = node.getFragment().getEnd();
        if (root) {
            properties.put(PName.ROOT, true);
        }
        properties.put(PName.BEGIN, String.valueOf(begin.getIndex()));
        properties.put(PName.END, String.valueOf(end.getIndex()));
        properties.put(PName.SOURCE, node.getFragment().getSource().getFragmentAsString(begin, end));
        properties.put(PName.DATA, node.getData());
        properties.put(PName.TYPE, node.getType().getName());
        properties.put(PName.CHILD_COUNT, String.valueOf(node.getChildCount()));
        properties.put(PName.UUID, UUID.randomUUID().toString()); // TODO:: think about collisions
        for (final Node child: node.getChildrenList()) {
            children.add(new JGNode(child, false));
        }
    }

    public JGNode(Map<PName, Object> vertexProperties, Factory factory) {
        properties.putAll(vertexProperties);
        this.factory = factory;
    }

    @Override
    public Fragment getFragment() {
        return new CommonFragment(
            (String) properties.get(PName.SOURCE),
            Integer.parseInt((String) properties.get(PName.BEGIN)),
            Integer.parseInt((String) properties.get(PName.BEGIN))
        );
    }

    @Override
    public Type getType() {
        return factory.getType((String) properties.get(PName.TYPE));
    }

    @Override
    public String getTypeName() {
        return getType().getName();
    }

    @Override
    public String getData() {
        return (String) properties.get(PName.DATA);
    }

    @Override
    public int getChildCount() {
        return Integer.parseInt((String) properties.get(PName.CHILD_COUNT));
    }

    @Override
    public Node getChild(int index) {
        return children.get(index);
    }

}
