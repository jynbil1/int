package hanpoom.internal_cron.crons.dashboard.slack.enumerate;

import java.util.Arrays;
import java.util.List;

public class CustomerEvaluation {
    private static final List<Integer> goal = Arrays.asList(
            0, 0, 0, 5000, 10000,
            20000, 0, 0, 0, 0, 0);

    public static int getGoal(int month) {
        return goal.get(month - 1);
    }
}
