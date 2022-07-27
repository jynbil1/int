package hanpoom.internal_cron.crons.dashboard.slack.enumerate;

public enum ProductSlackType {
    INCORRECT("Incorrect"),
    None("None");

    private String slackType;

    ProductSlackType(String slackType) {
        this.slackType = slackType;
    }

    public String getSlackType() {
        return this.slackType;
    }
}
