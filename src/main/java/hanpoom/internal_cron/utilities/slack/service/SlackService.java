package hanpoom.internal_cron.utilities.slack.service;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

public class SlackService {
    // private static final String SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/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";

    private static final String DAILY_NOTIFICATION_CHANNEL = "https://hooks.slack.com/services/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";
    public boolean sendNotification(String text) {
        SlackApi api = new SlackApi(DAILY_NOTIFICATION_CHANNEL);
        SlackMessage message = new SlackMessage();
        message.setText(text);
        try {
            api.call(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
