package e2;

import java.util.Optional;

public interface Logics {

    boolean aMineWasFound(Pair<Integer, Integer> position);

    Optional<Integer> getCounter(Pair<Integer, Integer> position);

    boolean isThereFlag(Pair<Integer, Integer> position);

    boolean isThereMine(Pair<Integer, Integer> position);

    boolean isThereVictory();

    void toggleFlag(Pair<Integer, Integer> position);
}
