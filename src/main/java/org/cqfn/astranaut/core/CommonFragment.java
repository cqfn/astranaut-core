package org.cqfn.astranaut.core;

public class CommonFragment implements Fragment {

    private final Source source;

    private final Position begin;

    private final Position end;

    public CommonFragment(Source source, Position begin, Position end) {
        this.source = source;
        this.begin = begin;
        this.end = end;
    }

    public CommonFragment(String source, int begin, int end) {
        this.source = (start, end1) -> source;
        this.begin = new CommonPosition(begin);
        this.end = new CommonPosition(end);
    }

    @Override
    public Source getSource() {
        return source;
    }

    @Override
    public Position getBegin() {
        return begin;
    }

    @Override
    public Position getEnd() {
        return end;
    }
}
