package org.cqfn.astranaut.core.database.janusgraph;

import org.cqfn.astranaut.core.*;
import org.cqfn.astranaut.core.database.DbNode;

import java.util.*;

public class JGNode implements DbNode {

    final Node base;

    final Map<PName, Object> properties = new HashMap<>();

    final List<JGNode> children = new ArrayList<>();

    private Factory factory = null;

    public JGNode(Node node) {
        base = node;
        Position begin = node.getFragment().getBegin();
        Position end = node.getFragment().getEnd();
        properties.put(PName.BEGIN, String.valueOf(begin.getIndex()));
        properties.put(PName.END, String.valueOf(end.getIndex()));
        properties.put(PName.SOURCE, node.getFragment().getSource().getFragmentAsString(begin, end));
        properties.put(PName.DATA, node.getData());
        properties.put(PName.TYPE, node.getType().getName());
        properties.put(PName.CHILD_COUNT, String.valueOf(node.getChildCount()));
        properties.put(PName.UUID, UUID.randomUUID().toString()); // TODO:: think about collisions
        for (final Node child: node.getChildrenList()) {
            children.add(new JGNode(child));
        }
    }

    public JGNode(Map<PName, Object> vertexProperties, Factory factory) {
        base = null;
        properties.putAll(vertexProperties);
        this.factory = factory;
    }

    public void setMetadata(String value) {
        properties.put(PName.META, value);
    }

    @Override
    public Fragment getFragment() {
        if (base != null) {
            return base.getFragment();
        } else {
            return new CommonFragment(
                    (String) properties.get(PName.SOURCE),
                    Integer.parseInt((String) properties.get(PName.BEGIN)),
                    Integer.parseInt((String) properties.get(PName.BEGIN))
            );
        }
    }

    @Override
    public Type getType() {
        if (base != null) {
            return base.getType();
        } else {
            return factory.getType((String) properties.get(PName.TYPE));
        }
    }

    @Override
    public String getTypeName() {
        if (base != null) {
            return base.getTypeName();
        } else {
            return this.getType().getName();
        }
    }

    @Override
    public String getData() {
        if (base != null) {
            return base.getData();
        } else {
            return (String) properties.get(PName.DATA);
        }
    }

    @Override
    public int getChildCount() {
        if (base != null) {
            return base.getChildCount();
        } else {
            return Integer.parseInt((String) properties.get(PName.CHILD_COUNT));
        }
    }

    @Override
    public Node getChild(int index) {
        return children.get(index);
    }

}
