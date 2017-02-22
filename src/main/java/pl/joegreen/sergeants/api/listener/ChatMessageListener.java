package pl.joegreen.sergeants.api.listener;

import pl.joegreen.sergeants.api.response.ChatMessageApiResponse;

@FunctionalInterface
public interface ChatMessageListener {
    void onEvent(String chatRoom, ChatMessageApiResponse message);
}
