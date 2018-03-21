package sch.sudoku.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class Row extends View {

    private final int rowIndex;

    @Builder
    Row(Model model, int rowIndex) {
        super(model);
        this.rowIndex = rowIndex;
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

}
