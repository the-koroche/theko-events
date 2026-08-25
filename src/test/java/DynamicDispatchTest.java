import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.theko.events.DynamicEvent;
import org.theko.events.DynamicEventDispatcher;

class DynamicDispatchTest {

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

    @Test
    void testDynamicDispatch() {
        DynamicEventDispatcher dispatcher = new DynamicEventDispatcher();

        dispatcher.addConsumer("on-input", (type, event) -> {
            System.out.println(String.format("Input: \"%s\" (%d chars)",
                    event.get("input"), event.get("length")));
        });

        dispatcher.dispatch("on-input", new DynamicEvent()
                .with("input", "Hello, World!")
                .with("length", 13));
        dispatcher.dispatch("on-input", new DynamicEvent()
                .with("input", "DynamicDispatch test class")
                .with("length", 28));

        String expectedOutput = String.join(System.lineSeparator(),
                "Input: \"Hello, World!\" (13 chars)",
                "Input: \"DynamicDispatch test class\" (28 chars)"
        ) + System.lineSeparator();

        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }
}