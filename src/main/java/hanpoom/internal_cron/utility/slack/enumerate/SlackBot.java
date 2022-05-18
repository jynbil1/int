package hanpoom.internal_cron.utility.slack.enumerate;

public enum SlackBot {
    UNSHIPPABLE_NOTIFICATION("https://hooks.slack.com/services/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"),
    SHIPOUT_NOTIFICATION("https://hooks.slack.com/services/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"),
    SHIPOUT_BOT("https://hooks.slack.com/services/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"),
    DATA_NOTIFICATION("https://hooks.slack.com/services/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"),
    WATCH_DHL("https://hooks.slack.com/services/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"),
    WATCH_FEDEX("https://hooks.slack.com/services/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"),
    US_SHIPPER("https://hooks.slack.com/services/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"),
    TEST("https://hooks.slack.com/services/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"),
    REVENUE_BOT("https://hooks.slack.com/services/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"),
    HANPOOM_TEAM("https://hooks.slack.com/services/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");

    private String webHookUrl;

    SlackBot(String webHookUrl) {
        this.webHookUrl = webHookUrl;
    }

    public String getWebHookUrl() {
        return this.webHookUrl; 
    }
}
