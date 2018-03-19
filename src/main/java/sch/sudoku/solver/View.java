package sch.sudoku.solver;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public abstract class View implements IntFunction<Integer>, BiConsumer<Integer, Integer>, Comparable<View> {

    private final Model model;

    private Collection<View> siblings = Lists.newLinkedList();

    private final int startIndex = 0;

    @java.beans.ConstructorProperties({"model"})
    public View(Model model) {
        this.model = model;
    }

    public Collection<? extends View> getSiblings() {
        return siblings;
    }

    public void addSibling(View sibling) {
        if (!getSiblings().contains(sibling)) {
            siblings.add(sibling);
            sibling.addSibling(this);
        }
    }

    public void addSiblings(Collection<? extends View> siblings) {
        siblings.forEach(this::addSibling);
    }

    @Override
    public void accept(Integer index, Integer value) {
        getModel().set(this, toModelIndex(index), value);
    }

    @Override
    public Integer apply(int index) {
        return getModel().get(toModelIndex(index));
    }

    public Integer get(int index) { return apply(index); }

    public Value getValue(int index) { return Value.builder().view(this).index(index).value(apply(index)).build(); }

    public List<? extends Value> getMissingValues() {
        return IntStream.range(0, 9)
                .mapToObj(this::getValue)
                .filter(v -> null == v.getValue())
                .collect(toList());
    }

    public int countMissingValues() {
        return (int) IntStream.range(0, 9).filter(i -> null == get(i)).count();
    }

    public boolean hasMissingValues() {
        return IntStream.range(0, 9).mapToObj(this::get).anyMatch(Objects::isNull);
    }

    public List<? extends Number> getMissingNumbers() {
        List<Integer> missingNumbers = IntStream.range(1, 10).mapToObj(Integer::valueOf).collect(toList());
        missingNumbers.removeAll(IntStream.range(0, 9).mapToObj(this::get).filter(Objects::nonNull).collect(toSet()));
        return missingNumbers;
    }

    public void set(int index, Integer value) { accept(index, value); }

    public  boolean isSolved() {
        List<Integer> list = IntStream.range(0, 9).mapToObj(this).filter(Objects::nonNull).collect(toList());
        return 9 == list.size() && isValid();
    }

    public boolean isValid() {
        Map<Integer, Long> multipliciy = IntStream.range(0, 9)
                .mapToObj(this)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(identity(), counting()));
        return multipliciy.values().stream().allMatch(m -> m == 1);
    }

    protected abstract int toModelIndex(int index);

    public abstract int toViewIndex(int index);

    @Override
    public int compareTo(View o) {
        return Integer.compare(countMissingValues(), o.countMissingValues());
    }

    public Model getModel() {
        return this.model;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof View)) return false;
        final View other = (View) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$model = this.getModel();
        final Object other$model = other.getModel();
        if (this$model == null ? other$model != null : !this$model.equals(other$model)) return false;
        final Object this$siblings = this.getSiblings();
        final Object other$siblings = other.getSiblings();
        if (this$siblings == null ? other$siblings != null : !this$siblings.equals(other$siblings)) return false;
        if (this.getStartIndex() != other.getStartIndex()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $model = this.getModel();
        result = result * PRIME + ($model == null ? 43 : $model.hashCode());
        final Object $siblings = this.getSiblings();
        result = result * PRIME + ($siblings == null ? 43 : $siblings.hashCode());
        result = result * PRIME + this.getStartIndex();
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof View;
    }

    public String toString() {
        return IntStream.range(0, 9).mapToObj(this::get).map(i -> null == i ? " " : i.toString()).collect(joining());
    }
}
