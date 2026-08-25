import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.theko.events.Event;
import org.theko.events.EventDispatcher;
import org.theko.events.Listener;

class ExceptionInListenerTest {

    private static class CriticalException extends RuntimeException {
        public CriticalException(String message) {
            super(message);
        }
    }

    private static class MyEvent extends Event {}

    private static class FaultyListener implements Listener<MyEvent> {
        public void onEvent(MyEvent event) {
            throw new RuntimeException("Exception in listener");
        }
    }

    @Test
    void testExceptionHandling() {
        EventDispatcher<MyEvent, FaultyListener, String> dispatcher = new EventDispatcher<>();

        var eventMap = dispatcher.createEventMap();
        eventMap.put("event", FaultyListener::onEvent);
        dispatcher.setEventMap(eventMap);

        dispatcher.addListener(new FaultyListener());
        dispatcher.addConsumer("event", (type, event) -> {
            throw new CriticalException("Exception in consumer");
        });

        assertDoesNotThrow(() -> dispatcher.dispatch("event", new MyEvent()));
    }
}