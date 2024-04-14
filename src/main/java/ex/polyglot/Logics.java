package ex.polyglot;

import java.util.Optional;

public interface Logics {

    boolean aMineWasFound(int row, int col);

    Optional<Integer> getSweptCellCounter(int row, int col);

    boolean isThereFlag(int row, int col);

    boolean isThereMine(int row, int col);

    boolean isThereVictory();

    void toggleFlag(int row, int col);
}
