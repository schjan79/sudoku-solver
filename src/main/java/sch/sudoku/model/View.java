package sch.sudoku.model;

import com.google.common.collect.Lists;
import lombok.experimental.NonFinal;

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

@lombok.Value
@NonFinal
public abstract class View implements IntFunction<Integer>, BiConsumer<Integer, Integer>, Comparable<View> {

    private final Model model;

    private Collection<View> siblings = Lists.newLinkedList();

    private final int startIndex = 0;

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
                .collect(groupingBy(identity(), counting()));
        return multipliciy.values().stream().allMatch(m -> m == 1);
    }

    protected abstract int toModelIndex(int index);

    public abstract int toViewIndex(int index);

    @Override
    public int compareTo(View o) {
        return Integer.compare(countMissingValues(), o.countMissingValues());
    }

    public String toString() {
        return IntStream.range(0, 9).mapToObj(this::get).map(i -> null == i ? " " : i.toString()).collect(joining());
    }
}
