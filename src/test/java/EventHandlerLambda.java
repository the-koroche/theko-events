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

class EventHandlerLambdaTest {

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

    private static class ResourceEvent extends Event {
        private final boolean successfullyClosed;

        public ResourceEvent(boolean successfullyClosed) {
            this.successfullyClosed = successfullyClosed;
        }

        public boolean isSuccessful() {
            return successfullyClosed;
        }
    }

    private static class ResourceListener implements Listener<ResourceEvent> {
        public void onOpened(ResourceEvent event) {
            System.out.println("Resource opened.");
        }

        public void onClosedSuccessfully(ResourceEvent event) {
            System.out.println("Resource closed successfully.");
        }

        public void onClosedWithError(ResourceEvent event) {
            System.out.println("Resource closed with error.");
        }
    }

    @Test
    void testEventHandlerLambda() {
        EventDispatcher<ResourceEvent, ResourceListener, String> dispatcher = new EventDispatcher<>();
        EventMap<ResourceEvent, ResourceListener, String> eventMap = dispatcher.createEventMap();

        eventMap.put("opened", ResourceListener::onOpened);
        eventMap.put("closed", (l, e) -> {
            if (e.isSuccessful()) {
                l.onClosedSuccessfully(e);
            } else {
                l.onClosedWithError(e);
            }
        });

        dispatcher.setEventMap(eventMap);
        dispatcher.addListener(new ResourceListener());

        dispatcher.dispatch("opened", new ResourceEvent(false));
        dispatcher.dispatch("closed", new ResourceEvent(true));
        dispatcher.dispatch("closed", new ResourceEvent(false));

        String expectedOutput = String.join(System.lineSeparator(),
                "Resource opened.",
                "Resource closed successfully.",
                "Resource closed with error."
        ) + System.lineSeparator();

        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }
}