package sch.sudoku.solver;

public class Column extends View {

    private final int columnIndex;

    Column(Model model, int columnIndex) {
        super(model);
        this.columnIndex = columnIndex;
    }

    public static ColumnBuilder builder() {
        return new ColumnBuilder();
    }

    @Override
    protected int toModelIndex(int rowIndex) {
        int modelIndex = getColumnIndex() - getStartIndex() + (rowIndex - getStartIndex()) * 9;
        return modelIndex;
    }

    @Override
    public int toViewIndex(int modelIndex) {
        int rowIndex = (modelIndex - getColumnIndex() + getStartIndex()) / 9 + getStartIndex();
        return rowIndex;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Column)) return false;
        final Column other = (Column) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getColumnIndex() != other.getColumnIndex()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getColumnIndex();
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Column;
    }

    public String toString() {
        return "Column(columnIndex=" + this.getColumnIndex() + ")";
    }

    public static class ColumnBuilder {
        private Model model;
        private int columnIndex;

        ColumnBuilder() {
        }

        public Column.ColumnBuilder model(Model model) {
            this.model = model;
            return this;
        }

        public Column.ColumnBuilder columnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
            return this;
        }

        public Column build() {
            return new Column(model, columnIndex);
        }

        public String toString() {
            return "Column.ColumnBuilder(model=" + this.model + ", columnIndex=" + this.columnIndex + ")";
        }
    }
}
