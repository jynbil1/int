package hanpoom.internal_cron.api.slack;

import org.springframework.stereotype.Component;

import lombok.Data;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

@Component
@Data
public class SlackAPI {
    // Real
    private static final String SHIPMENT_REPORT_HOOK_URL = "https://hooks.slack.com/services/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";

    // Testing
    // private final String SHIPMENT_REPORT_HOOK_URL =
    // "https://hooks.slack.com/services/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";

    public void sendMessageToChannel(String message, String channel) {
    }

    public void sendMessageToChannelWithFile(String message, String channel, String filePath) {
    }

    public void sendMessage(String message, String webHookUrl) {
        SlackApi slack = new SlackApi(webHookUrl);
        try {
            slack.call(new SlackMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        SlackApi slack = new SlackApi(this.SHIPMENT_REPORT_HOOK_URL);
        try {
            slack.call(new SlackMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithFile(String message, String webHookUrl, String filePath) {
    }

}