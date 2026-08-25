import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.theko.events.Event;
import org.theko.events.EventDispatcher;
import org.theko.events.EventMap;
import org.theko.events.Listener;

class MessageEventTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    static class MyEvent extends Event {
        private final String message;

        public MyEvent(String message) { this.message = message; }
        public MyEvent() { this(null); }

        public boolean hasMessage() { return message != null; }
        public String getMessage() { return message; }
    }

    interface MyListener extends Listener<MyEvent> {
        default void onOpened(MyEvent event) {}
        default void onClosed(MyEvent event) {}
        default void onMessage(MyEvent event) {}
    }

    static class MyListenerImpl implements MyListener {
        @Override
        public void onOpened(MyEvent event) { System.out.println("Opened"); }
        @Override
        public void onClosed(MyEvent event) { System.out.println("Closed"); }
        @Override
        public void onMessage(MyEvent event) {
            if (event.hasMessage()) {
                System.out.println("Event has message");
            }
        }
    }

    @Test
    void testMessageEventExecutionOrder() {
        EventDispatcher<MyEvent, MyListener, String> dispatcher = new EventDispatcher<>();
        EventMap<MyEvent, MyListener, String> map = new EventMap<>();
        map.put("open", MyListener::onOpened);
        map.put("close", MyListener::onClosed);
        map.put("message", MyListener::onMessage);
        dispatcher.setEventMap(map);

        dispatcher.addListener(new MyListenerImpl());
        dispatcher.addConsumer("message", (type, event) -> {
            if (event.hasMessage()) {
                System.out.println("Message: " + event.getMessage());
            }
        });

        dispatcher.dispatch("open", new MyEvent());
        dispatcher.dispatch("message", new MyEvent("Hello world!"));
        dispatcher.dispatch("close", new MyEvent());

        String expectedOutput = String.join(System.lineSeparator(),
                "Opened",
                "Event has message",
                "Message: Hello world!",
                "Closed"
        ) + System.lineSeparator();

        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }
}