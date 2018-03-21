package sch.sudoku.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.stream.IntStream;

@Value
@EqualsAndHashCode(callSuper = false)
public class Block extends View {
    private final int width;
    private final int height;

    private final int columnIndex;
    private final int rowIndex;

    @Builder
    Block(Model model, int rowIndex, int columnIndex, int width, int height) {
        super(model);
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.width = width;
        this.height = height;
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

}
