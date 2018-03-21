package sch.sudoku.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class Column extends View {

    private final int columnIndex;

    @Builder
    Column(Model model, int columnIndex) {
        super(model);
        this.columnIndex = columnIndex;
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

}
