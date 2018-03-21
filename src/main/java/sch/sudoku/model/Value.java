package sch.sudoku.model;

import lombok.Builder;

@lombok.Value
@Builder
public class Value implements Comparable<Value> {
    private final int index;
    private final View view;
    private final Integer value;

    @java.beans.ConstructorProperties({"index", "view", "value"})
    Value(int index, View view, Integer value) {
        this.index = index;
        this.view = view;
        this.value = value;
    }

    @Override
    public int compareTo(Value o) {
        return Integer.compare(index, o.index);
    }
}
