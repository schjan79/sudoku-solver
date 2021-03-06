package sch.sudoku.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Data
public class Model {
    private final Integer [] model;

    private final Deque<Command> commands = Lists.newLinkedList();
    private final List<? extends View> views;
    @Setter(AccessLevel.PROTECTED)
    private boolean isValid = true ;

    @Builder
    public Model(Integer [] model, boolean diagonals) {
        if (Objects.requireNonNull(model, "model is null").length != 81) {
            throw new IllegalArgumentException("model size doesn't fit to expectation: 81");
        }
        Stream.of(model)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .filter(i -> i < 1 || 9 < i)
                .findAny()
                .ifPresent(i -> { throw new IllegalArgumentException("Invalid value in model: " + i); });
        this.model = model;
        views = Collections.unmodifiableList(createViews(diagonals));
    }

    private List<View> createViews(boolean diagonals) {
        Set<View> columns = new LinkedHashSet<>();
        Set<View> rows = new LinkedHashSet<>();
        List<View> blocks = new LinkedList<>();
        for (int i = 0; i < 9; i++) {
            blocks.add(Block.builder().width(3).height(3).rowIndex(i / 3).columnIndex(i % 3).model(this).build());
        }
        for (int i = 0; i < 9; i++) {
            Row row = Row.builder().rowIndex(i).model(this).build();
            Column column = Column.builder().columnIndex(i).model(this).build();

            final int k = i % 3;
            IntStream.range(0, 3).map(j -> j + 3 * k).forEach(j -> row.addSibling(blocks.get(j)));
            IntStream.range(0, 3).map(j -> j * 3 + k).forEach(j -> column.addSibling(blocks.get(j)));

            rows.add(row);
            columns.add(column);
        }
        rows.forEach(row -> row.addSiblings(columns));
        columns.forEach(column -> column.addSiblings(rows));

        List<View> views = Lists.newLinkedList();
        views.addAll(rows);
        views.addAll(columns);
        views.addAll(blocks);

        if (diagonals) {
            View diagonal = new Diagonal(this);
            View diagonal2 = new Diagonal2(this);

            diagonal.addSiblings(rows);
            diagonal.addSiblings(columns);
            diagonal2.addSiblings(rows);
            diagonal2.addSiblings(columns);

            diagonal.addSiblings(ImmutableList.of(blocks.get(0), blocks.get(4), blocks.get(8)));
            diagonal2.addSiblings(ImmutableList.of(blocks.get(2), blocks.get(4), blocks.get(6)));

            views.add(diagonal);
            views.add(diagonal2);
        }

        return views;
    }

    public boolean isSolved() {
        return getViews().stream().allMatch(View::isSolved);
    }

    public void set(View view, int index, int value) {
        if (index < 0 || 80 < index) {
            throw new IllegalArgumentException("index: " + index);
        }
        if (value < 1 || 9 < value) {
            throw new IllegalArgumentException("value: " + value);
        }
        if (this != view.getModel()) {
            throw new IllegalStateException("The given view does not belong to this model.");
        }
        Command command = Command.builder()
                .model(this)
                .view(view)
                .index(index)
                .newValue(value)
                .oldValue(getModel()[index])
                .build();
        command.execute();
        getCommands().push(command);

    }

    public Integer get(int index) {
        return getModel()[index];
    }

    public Value undo() {
        if (!canUndo()) {
            throw new IllegalStateException("The command stack is empty.");
        }
        Command command = getCommands().remove();
        command.undo();
        return Value.builder().view(command.getView()).index(command.getIndex()).value(command.getNewValue()).build();
    }

    public boolean canUndo() {
        return !getCommands().isEmpty();
    }


    @lombok.Value
    @Builder
    static class Command {
        private final Model model;
        private final int index;
        private final Integer oldValue;
        private final int newValue;
        private final View view;

        private void execute() {
            model.model[index] = newValue;
            updateValidity();
        }

        private void updateValidity() {
            boolean valid = view.getSiblings().stream().allMatch(View::isValid);
            valid &= view.isValid();
            model.setValid(valid);
        }

        private void undo() {
            model.model[index] = oldValue;
            updateValidity();
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getRowSeparator(0));
        for (int i = 0; i < 9; ) {
            b.append(getColumnSeparator(0));
            for (int j = 0; j < 9; ) {
                Integer x = getModel()[9 * i + j];
                b.append(null == x ? "   " : " " + x + " ");
                b.append(getColumnSeparator(++j));
            }
            b.append('\n');
            b.append(getRowSeparator(++i));
        }
        return b.toString();
    }

    private char getColumnSeparator(int index) {
        if (0 == index % 3) {
            return 'H';
        } else {
            return '|';
        }
    }

    private String getRowSeparator(int index) {
        if (0 == index % 3) {
            return "+===+===+===*===+===+===*===+===+===+\n";
        } else {
            return "+---+---+---*---+---+---*---+---+---+\n";
        }
    }
}
