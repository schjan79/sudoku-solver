package sch.sudoku.model;

public class Diagonal2 extends View {


    public Diagonal2(Model model) {
        super(model);
    }

    @Override
    protected int toModelIndex(int viewIndex) {
        return 8 * (viewIndex + 1);
    }

    @Override
    public int toViewIndex(int modelIndex) {
        return modelIndex / 8 - 1;
    }
}
