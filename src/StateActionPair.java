public class StateActionPair {
    State state;
    Action action;

    StateActionPair(State state, Action action) {
        this.state = state;
        this.action = action;
    }

    // Override equals and hashCode for proper functioning in HashMap
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StateActionPair pair = (StateActionPair) obj;
        return state.equals(pair.state) && action == pair.action;
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }
}
