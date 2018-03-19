package sch.sudoku.solver;

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

    public static ValueBuilder builder() {
        return new ValueBuilder();
    }

    @Override
    public int compareTo(Value o) {
        return Integer.compare(index, o.index);
    }

    public int getIndex() {
        return this.index;
    }

    public View getView() {
        return this.view;
    }

    public Integer getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Value)) return false;
        final Value other = (Value) o;
        if (this.getIndex() != other.getIndex()) return false;
        final Object this$view = this.getView();
        final Object other$view = other.getView();
        if (this$view == null ? other$view != null : !this$view.equals(other$view)) return false;
        final Object this$value = this.getValue();
        final Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getIndex();
        final Object $view = this.getView();
        result = result * PRIME + ($view == null ? 43 : $view.hashCode());
        final Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        return result;
    }

    public String toString() {
        return "Value(index=" + this.getIndex() + ", view=" + this.getView() + ", value=" + this.getValue() + ")";
    }

    public static class ValueBuilder {
        private int index;
        private View view;
        private Integer value;

        ValueBuilder() {
        }

        public Value.ValueBuilder index(int index) {
            this.index = index;
            return this;
        }

        public Value.ValueBuilder view(View view) {
            this.view = view;
            return this;
        }

        public Value.ValueBuilder value(Integer value) {
            this.value = value;
            return this;
        }

        public Value build() {
            return new Value(index, view, value);
        }

        public String toString() {
            return "Value.ValueBuilder(index=" + this.index + ", view=" + this.view + ", value=" + this.value + ")";
        }
    }
}
