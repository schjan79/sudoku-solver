package sch.sudoku.solver;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Solver {

    public static Solver processFirst() {
        return new Solver(Deque::peek);
    }

    private final Function<Deque<? extends View>, View> viewSelector;

    public Solver() {
        this(Deque::peek);
    }

    public Solver(Function<Deque<? extends View>, View> viewSelector) {
        this.viewSelector = viewSelector;
    }

    public void solveModel(Model model) {
        Deque<View> toProcess = new LinkedList<>();
        toProcess.addAll(model.getViews());

        while (!model.isSolved()) {

            if (model.isValid()) {
                // forward step
                View view = viewSelector.apply(toProcess);
                if (view.hasMissingValues()) {
                    forwardStep(view);
                } else {
                    toProcess.remove();
                }
            } else {
                // back step
                Value undo = model.undo();
                while (!forwardStep(undo)) {
                    undo = model.undo();
                    if (undo.getView() != toProcess.peek()) {
                        toProcess.push(undo.getView());
                    }
                }
            }
        }
    }

    private boolean forwardStep(Value value) {
        return forwardStep(value.getView(), value.getIndex(), value.getValue().intValue());
    }

    private void forwardStep(View view) {
        forwardStep(view, 0, 0);
    }

    private boolean forwardStep(View view, int minIndex, int minValue) {
        List<? extends Value> missingValues = view.getMissingValues();
        List<? extends Number> missingNumbers = view.getMissingNumbers();

        missingValues.sort(Value::compareTo);

        int lastViewIndex = 0 == minIndex ? 0 : view.toViewIndex(minIndex);
        Iterator<? extends Number> numberIterator = missingNumbers.stream().filter(n -> n.intValue() > minValue).iterator();
        Iterator<? extends Value> valueIterator = missingValues.stream().filter(v -> v.getIndex() >= lastViewIndex).iterator();

        if (numberIterator.hasNext() && valueIterator.hasNext()) {
            view.set(valueIterator.next().getIndex(), numberIterator.next().intValue());
            return true;
        }
        return false;
    }


}
