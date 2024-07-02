

enum Action {
    MOVE_FORWARD, TURN_LEFT, TURN_RIGHT
}

public class State {
    Point location;
    AutoAlgo1.PixelState[][] map;

    State(Point location, AutoAlgo1.PixelState[][] map) {
        this.location = location;
        this.map = map;
    }

    double getCoverage() {
        // Calculate the percentage of the area that has been explored
        int exploredCount = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == AutoAlgo1.PixelState.explored || map[i][j] == AutoAlgo1.PixelState.visited) {
                    exploredCount++;
                }
            }
        }
        return (double) exploredCount / (map.length * map[0].length);
    }

    // Override equals and hashCode for proper functioning in HashMap
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State state = (State) obj;
        return location.equals(state.location) && map.equals(state.map);
    }

    @Override
    public int hashCode() {
        int result = location.hashCode();
        result = 31 * result + map.hashCode();
        return result;
    }
}

