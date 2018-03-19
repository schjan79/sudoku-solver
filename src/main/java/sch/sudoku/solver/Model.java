package sch.sudoku.solver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Builder;

import java.util.*;
import java.util.stream.IntStream;

public class Model {
    private final Integer [] model;

    @Builder.Default
    private final Deque<Command> commands = Lists.newLinkedList();
    private final List<View> views;
    private boolean[] isValid = { true };

    public Model(Integer [] model, boolean diagonals) {
        if (Objects.requireNonNull(model, "model is null").length != 81) {
            throw new IllegalArgumentException("model size doesn't fit to expectation: 81");
        }
        for (Integer i : model) {
            if (null != i && (i < 1 || i > 9)) {
                throw new IllegalArgumentException("Invalid value in model: " + i);
            }
        }
        this.model = model;
        views = Collections.unmodifiableList(cresteViews(diagonals));
    }

    public Model(Integer [] model) {
        this(model, false);
    }

    public static ModelBuilder builder() {
        return new ModelBuilder();
    }

    public boolean isValid() { return isValid[0]; }

    private List<View> cresteViews(boolean diagonals) {
        Set<View> columns = new LinkedHashSet<>();
        Set<View> rows = new LinkedHashSet<>();
        List<View> cells = new LinkedList<>();
        for (int i = 0; i < 9; i++) {
            cells.add(Block.builder().rowIndex(i / 3).columnIndex(i % 3).model(this).build());
        }
        for (int i = 0; i < 9; i++) {
            Row row = Row.builder().rowIndex(i).model(this).build();
            Column column = Column.builder().columnIndex(i).model(this).build();

            final int k = i % 3;
            IntStream.range(0, 3).map(j -> j + 3 * k).forEach(j -> row.addSibling(cells.get(j)));
            IntStream.range(0, 3).map(j -> j * 3 + k).forEach(j -> column.addSibling(cells.get(j)));

            rows.add(row);
            columns.add(column);
        }
        rows.forEach(row -> row.addSiblings(columns));
        columns.forEach(column -> column.addSiblings(rows));

        List<View> views = Lists.newLinkedList();
        views.addAll(rows);
        views.addAll(columns);
        views.addAll(cells);

        if (diagonals) {
            View diagonal = new Diagonal(this);
            View diagonal2 = new Diagonal2(this);

            diagonal.addSiblings(rows);
            diagonal.addSiblings(columns);
            diagonal2.addSiblings(rows);
            diagonal2.addSiblings(columns);

            diagonal.addSiblings(ImmutableList.of(cells.get(0), cells.get(4), cells.get(8)));
            diagonal2.addSiblings(ImmutableList.of(cells.get(2), cells.get(4), cells.get(6)));

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

    public Integer[] getModel() {
        return this.model;
    }

    public Deque<Command> getCommands() {
        return this.commands;
    }

    public List<View> getViews() {
        return this.views;
    }

    public boolean[] getIsValid() {
        return this.isValid;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Model)) return false;
        final Model other = (Model) o;
        if (!Arrays.deepEquals(this.getModel(), other.getModel())) return false;
        final Object this$commands = this.getCommands();
        final Object other$commands = other.getCommands();
        if (this$commands == null ? other$commands != null : !this$commands.equals(other$commands)) return false;
        final Object this$views = this.getViews();
        final Object other$views = other.getViews();
        if (this$views == null ? other$views != null : !this$views.equals(other$views)) return false;
        if (!Arrays.equals(this.getIsValid(), other.getIsValid())) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + Arrays.deepHashCode(this.getModel());
        final Object $commands = this.getCommands();
        result = result * PRIME + ($commands == null ? 43 : $commands.hashCode());
        final Object $views = this.getViews();
        result = result * PRIME + ($views == null ? 43 : $views.hashCode());
        result = result * PRIME + Arrays.hashCode(this.getIsValid());
        return result;
    }

    public static class ModelBuilder {
        private Integer[] model;
        private boolean diagonals;

        ModelBuilder() {
        }

        public Model.ModelBuilder model(Integer[] model) {
            this.model = model;
            return this;
        }

        public Model build() {
            return new Model(model, diagonals);
        }

        public String toString() {
            return "Model.ModelBuilder(model=" + Arrays.deepToString(this.model) + ")";
        }

        public Model.ModelBuilder diagonals(boolean diagonals) {
            this.diagonals = diagonals;
            return this;
        }
    }

    static class Command {
        private final Model model;
        private final int index;
        private final Integer oldValue;
        private final int newValue;
        private final View view;

        @java.beans.ConstructorProperties({"model", "index", "oldValue", "newValue", "view"})
        Command(Model model, int index, Integer oldValue, int newValue, View view) {
            this.model = model;
            this.index = index;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.view = view;
        }

        public static CommandBuilder builder() {
            return new CommandBuilder();
        }

        void execute() {
            model.model[index] = newValue;
            updateValidity();
        }

        private void updateValidity() {
            boolean valid = view.getSiblings().stream().allMatch(View::isValid);
            valid &= view.isValid();
            model.isValid[0] = valid;
        }

        void undo() {
            model.model[index] = oldValue;
            updateValidity();
        }

        public int getIndex() {
            return this.index;
        }

        public Integer getOldValue() {
            return this.oldValue;
        }

        public int getNewValue() {
            return this.newValue;
        }

        public View getView() {
            return this.view;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Command)) return false;
            final Command other = (Command) o;
            if (this.getIndex() != other.getIndex()) return false;
            final Object this$oldValue = this.getOldValue();
            final Object other$oldValue = other.getOldValue();
            if (this$oldValue == null ? other$oldValue != null : !this$oldValue.equals(other$oldValue)) return false;
            if (this.getNewValue() != other.getNewValue()) return false;
            final Object this$view = this.getView();
            final Object other$view = other.getView();
            if (this$view == null ? other$view != null : !this$view.equals(other$view)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + this.getIndex();
            final Object $oldValue = this.getOldValue();
            result = result * PRIME + ($oldValue == null ? 43 : $oldValue.hashCode());
            result = result * PRIME + this.getNewValue();
            final Object $view = this.getView();
            result = result * PRIME + ($view == null ? 43 : $view.hashCode());
            return result;
        }

        public String toString() {
            return "Model.Command(index=" + this.getIndex() + ", oldValue=" + this.getOldValue() + ", newValue=" + this.getNewValue() + ", view=" + this.getView() + ")";
        }

        public static class CommandBuilder {
            private Model model;
            private int index;
            private Integer oldValue;
            private int newValue;
            private View view;

            CommandBuilder() {
            }

            public Command.CommandBuilder model(Model model) {
                this.model = model;
                return this;
            }

            public Command.CommandBuilder index(int index) {
                this.index = index;
                return this;
            }

            public Command.CommandBuilder oldValue(Integer oldValue) {
                this.oldValue = oldValue;
                return this;
            }

            public Command.CommandBuilder newValue(int newValue) {
                this.newValue = newValue;
                return this;
            }

            public Command.CommandBuilder view(View view) {
                this.view = view;
                return this;
            }

            public Command build() {
                return new Command(model, index, oldValue, newValue, view);
            }

            public String toString() {
                return "Model.Command.CommandBuilder(index=" + this.index + ", oldValue=" + this.oldValue + ", newValue=" + this.newValue + ", view=" + this.view + ")";
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addRowSeparator(b, 0);
        for (int i=0; i<9; ) {
            addColumnSeparator(b, 0);
            for (int j=0; j<9; ) {
                Integer x = getModel()[9 * i + j];
                b.append(null == x ? "   " : " " + x + " ");
                addColumnSeparator(b, ++j);
            }
            b.append('\n');
            addRowSeparator(b, ++i);
        }
        return b.toString();
    }

    private void addColumnSeparator(StringBuilder b, int index) {
        if (index % 3 == 0) {
            b.append('H');
        } else {
            b.append('|');
        }
    }

    private void addRowSeparator(StringBuilder b, int index) {
        if (index % 3 == 0) {
            b.append("+===+===+===*===+===+===*===+===+===+\n");
        } else {
            b.append("+---+---+---*---+---+---*---+---+---+\n");
        }
    }
}
