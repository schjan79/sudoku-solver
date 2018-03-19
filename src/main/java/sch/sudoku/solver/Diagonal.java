package sch.sudoku.solver;

public class Diagonal extends View {


    public Diagonal(Model model) {
        super(model);
    }

    @Override
    protected int toModelIndex(int viewIndex) {
        return 10 * viewIndex;
    }

    @Override
    public int toViewIndex(int modelIndex) {
        return modelIndex / 10;
    }
}
