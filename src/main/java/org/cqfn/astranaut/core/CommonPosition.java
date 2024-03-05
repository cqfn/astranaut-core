package org.cqfn.astranaut.core;

public class CommonPosition implements Position {

    private final int index;

    public CommonPosition(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }
}
