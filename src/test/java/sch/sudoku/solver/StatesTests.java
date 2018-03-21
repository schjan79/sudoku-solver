package sch.sudoku.solver;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sch.sudoku.model.Model;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TimingExtension.class)
public class StatesTests {
    static Integer[] state_1 = {
            6, 2, null, 8, 9, 4, 1, 7, 3,
            3, 1, 4, 6, null, null, null, null, 9,
            null, null, null, null, null, 5, null, null, null,
            1, null, null, 7, null, null, null, null, null,
            null, null, 9, null, 8, null, 2, null, null,
            null, null, null, null, null, 2, null, null, 8,
            null, null, null, 2, null, null, null, null, null,
            2, null, null, null, null, 3, 6, 8, 7,
            8, 3, 7, 9, 6, 1, null, 2, 4
    };

    static Integer[] state_99 = {
            7, null, null, null, null, null, 5, 6, null,
            null, null, null, 7, null, null, null, null, null,
            9, null, null, null, 6, 1, 3, null, null,
            null, null, 7, 8, null, null, null, null, 5,
            null, null, 2, null, 7, null, 9, null, null,
            5, null, null, null, null, 6, 7, null, null,
            null, null, 9, 4, 2, null, null, null, 8,
            null, null, null, null, null, 3, null, null, null,
            null, 2, 4, null, null, null, null, null, 9
    };

    static Integer[] state_100 = {
            null, null, null, 5, null, 4, null, 9, null,
            1, null, 6, null, null, 8, null, null, null,
            null, 7, null, null, 1, null, null, 8, null,
            7, 9, null, null, 5, null, null, 4, null,
            null, null, null, null, null, null, null, null, null,
            null, 4, null, null, 2, null, null, 3, 6,
            null, 2, null, null, 3, null, null, 5, null,
            null, null, null, 1, null, null, 8, null, 3,
            null, 1, null, 4, null, 5, null, null, null
    };

    static Integer[] state_101 = {
            6, null, 3, null, null, 8, null, 7, null,
            null, null, 1, 7, null, null, null, null, 8,
            null, null, null, 5, null, null, 1, 6, null,
            null, 6, null, null, 9, 4, null, null, null,
            null, 9, null, 2, null, 5, null, 1, null,
            null, null, null, 6, 7, null, null, 2, null,
            null, 4, 7, null, null, 6, null, null, null,
            9, null, null, null, null, 7, 6, null, null,
            null, 1, null, 4, null, null, 5, null, 7
    };

    static Integer[] state_xxx = {
            2, null, null, null, 7, null, 1, null, null,
            null, null, 3, null, null, 4, null, 8, null,
            null, null, 1, null, null, 2, null, null, 3,
            null, 5, null, null, null, 1, null, null, null,
            null, 2, null, null, 6, null, 7, 3, null,
            null, null, 4, 7, null, null, null, 2, null,
            9, null, null, null, 4, null, null, 1, null,
            null, 1, 5, null, 3, null, null, null, null,
            null, null, null, null, 2, null, 6, null, 9,
    };

    static Stream<Arguments> getStates() {
        return Stream.of(
                () -> new Object[] { false, state_1 },
                () -> new Object[] { false, state_99 },
                () -> new Object[] { false, state_100 },
                () -> new Object[] { true, state_101 },
                () -> new Object[] { false, state_xxx }
        );
    }

    @ParameterizedTest
    @MethodSource("getStates")
    public void shouldSolve(boolean diagonals, Integer[] state) throws Exception {
        // given
        Model model = Model.builder().model(state).diagonals(diagonals).build();

        System.out.println(model);

        // when
        new Solver(
                // potential infinity loop ... :(
//                views -> views.stream()
//                        .filter(view -> view.countMissingValues() > 0)
//                        .sorted(Comparator.comparingInt(View::countMissingValues))
//                        .findFirst()
//                        .get()
        ).solveModel(model);

        // then
        assertTrue(model.isSolved());
        System.out.println(model);
    }


}
