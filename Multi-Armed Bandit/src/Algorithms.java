public class Algorithms {
    /**
     * The names of the algorithms involved.
     */
    public static enum AlgorithmNames {
        EFIRST,
        FKUBE,
        FKDE,
        UCBBV,
        LPSPLIT,
        EPROGRESSIVE,
        SOAAV
    }

    public static eFirst(Agent curAgent) {

    }

    public static run(AlgorithmNames algorithm, Agent curAgent) {
        switch (algorithm) {
            case EFIRST: eFirst(curAgent);
                         break;

        }
    }
}