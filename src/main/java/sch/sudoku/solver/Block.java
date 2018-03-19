package sch.sudoku.solver;

import java.util.stream.IntStream;

public class Block extends View {
    private final int width;
    private final int height;

    private final int columnIndex;
    private final int rowIndex;

    Block(Model model, int rowIndex, int columnIndex, int width, int height) {
        super(model);
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.width = width;
        this.height = height;
    }

    Block(Model model, int rowIndex, int columnIndex) {
        this(model, rowIndex, columnIndex, 3, 3);
    }

    public static BlockBuilder builder() {
        return new BlockBuilder();
    }

    @Override
    protected int toModelIndex(int viewIndex) {
        int idx = viewIndex - getStartIndex();
        int column = getColumnIndex() - getStartIndex();
        int row = getRowIndex() - getStartIndex();

        int localRow = idx / getHeight();
        int localColumn = idx % getWidth();

        int modelIndex = row * 9 * getHeight() + column * getWidth() + localRow * 9 + localColumn;
        return modelIndex;
    }

    @Override
    public int toViewIndex(int modelIndex) {
        int viewIndex = IntStream.range(0, 9).filter(x -> toModelIndex(x) == modelIndex).findAny().getAsInt();
        return viewIndex;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Block)) return false;
        final Block other = (Block) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getWidth() != other.getWidth()) return false;
        if (this.getHeight() != other.getHeight()) return false;
        if (this.getColumnIndex() != other.getColumnIndex()) return false;
        if (this.getRowIndex() != other.getRowIndex()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getWidth();
        result = result * PRIME + this.getHeight();
        result = result * PRIME + this.getColumnIndex();
        result = result * PRIME + this.getRowIndex();
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Block;
    }

    public String toString() {
        return getClass().getSimpleName() + "(width=" + this.getWidth() + ", height=" + this.getHeight() + ", columnIndex=" + this.getColumnIndex() + ", rowIndex=" + this.getRowIndex() + ")";
    }

    public static class BlockBuilder {
        private Model model;
        private int rowIndex;
        private int columnIndex;

        BlockBuilder() {
        }

        public BlockBuilder model(Model model) {
            this.model = model;
            return this;
        }

        public BlockBuilder rowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
            return this;
        }

        public BlockBuilder columnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
            return this;
        }

        public Block build() {
            return new Block(model, rowIndex, columnIndex);
        }

        public String toString() {
            return "Block.BlockBuilder(model=" + this.model + ", rowIndex=" + this.rowIndex + ", columnIndex=" + this.columnIndex + ")";
        }
    }
}
