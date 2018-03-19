package sch.sudoku.solver;

public class Row extends View {

    private final int rowIndex;

    Row(Model model, int rowIndex) {
        super(model);
        this.rowIndex = rowIndex;
    }

    public static RowBuilder builder() {
        return new RowBuilder();
    }

    @Override
    protected int toModelIndex(int columnIndex) {
        int modelIndex = getRowIndex() * 9 + columnIndex - getStartIndex();
        return modelIndex;
    }

    @Override
    public int toViewIndex(int modelIndex) {
        int columnIndex = modelIndex + getStartIndex() - 9 * getRowIndex();
        return columnIndex;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Row)) return false;
        final Row other = (Row) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getRowIndex() != other.getRowIndex()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getRowIndex();
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Row;
    }

    public String toString() {
        return "Row(rowIndex=" + this.getRowIndex() + ")";
    }

    public static class RowBuilder {
        private Model model;
        private int rowIndex;

        RowBuilder() {
        }

        public Row.RowBuilder model(Model model) {
            this.model = model;
            return this;
        }

        public Row.RowBuilder rowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
            return this;
        }

        public Row build() {
            return new Row(model, rowIndex);
        }

        public String toString() {
            return "Row.RowBuilder(model=" + this.model + ", rowIndex=" + this.rowIndex + ")";
        }
    }
}
