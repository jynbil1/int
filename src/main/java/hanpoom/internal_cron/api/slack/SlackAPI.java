package hanpoom.internal_cron.api.slack;

import org.springframework.stereotype.Component;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

@Component
public class SlackAPI {

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

    public void sendMessageWithFile(String message, String webHookUrl, String filePath) {
    }

}