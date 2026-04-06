public class GameState {

    public int[]   score    = {0, 0};   // [TeamA, TeamB]
    public int[]   fouls    = {0, 0};   // [TeamA, TeamB]
    public int[]   timeouts = {3, 3};   // [TeamA, TeamB] — 3 per team

    public int     quarter    = 1;
    public int     activeTeam = 0;      // 0 = Team A, 1 = Team B
    public boolean paused     = true;
    public boolean gameOver   = false;

    // ─── Protocol string broadcast to all clients ───────────────
    // Format:  STATE|scoreA|scoreB|foulsA|foulsB|toA|toB|quarter|active|status
    // Example: STATE|12|8|3|1|2|3|2|A|LIVE
    public synchronized String toProtocolString() {
        return String.format("STATE|%d|%d|%d|%d|%d|%d|%d|%s|%s",
            score[0], score[1],
            fouls[0], fouls[1],
            timeouts[0], timeouts[1],
            quarter,
            activeTeam == 0 ? "A" : "B",
            gameOver ? "GAMEOVER" : (paused ? "PAUSED" : "LIVE")
        );
    }

    // ─── Human-readable summary for server AWT log ──────────────
    public synchronized String toDisplayString() {
        String status = gameOver ? "GAME OVER"
                      : paused   ? "PAUSED"
                      :            "LIVE";
        return String.format(
            "[Q%d | %s | Active: Team %s]  " +
            "A: %dpts  F:%d  T:%d  |  " +
            "B: %dpts  F:%d  T:%d",
            quarter, status,
            activeTeam == 0 ? "A" : "B",
            score[0], fouls[0], timeouts[0],
            score[1], fouls[1], timeouts[1]
        );
    }
}
