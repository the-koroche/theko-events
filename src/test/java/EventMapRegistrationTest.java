import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.theko.events.Event;
import org.theko.events.EventDispatcher;
import org.theko.events.Listener;

class EventMapRegistrationTest {

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

    public static class InputEvent extends Event {
        public final String input;
        public final int length;

        public InputEvent(String input, int length) {
            this.input = input;
            this.length = length;
        }
    }

    public interface InputListener extends Listener<InputEvent> {
        default void onInput(InputEvent event) {}
    }

    public static class DefaultInputListener implements InputListener {
        @Override
        public void onInput(InputEvent event) {
            System.out.println(String.format("Input: \"%s\" (%d chars)", event.input, event.length));
        }
    }

    @Test
    void testEventMapRegistration() {
        EventDispatcher<InputEvent, InputListener, String> dispatcher = new EventDispatcher<>();
        var eventMap = dispatcher.createEventMap();
        eventMap.put("on-input", InputListener::onInput);
        dispatcher.setEventMap(eventMap);
        dispatcher.addListener(new DefaultInputListener());

        dispatcher.dispatch("on-input", new InputEvent("Hello, World!", 13));
        dispatcher.dispatch("on-input", new InputEvent("EventMap test class", 19));

        String expectedOutput = String.join(System.lineSeparator(),
                "Input: \"Hello, World!\" (13 chars)",
                "Input: \"EventMap test class\" (19 chars)"
        ) + System.lineSeparator();

        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }
}