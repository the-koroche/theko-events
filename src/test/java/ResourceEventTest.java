import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.theko.events.Event;
import org.theko.events.EventDispatcher;
import org.theko.events.EventMap;
import org.theko.events.Listener;
import org.theko.events.ListenerPriority;

class ResourceEventTest {

    private class Resource {
        boolean opened;
        boolean hasUsed;

        public void open() {
            hasUsed = false;
            opened = true;
        }

        public void close() {
            opened = false;
        }

        public void use() {
            if (!opened) return;
            hasUsed = true;
        }
    }

    public class ResourceEvent extends Event {
        private final Resource resource;

        public ResourceEvent(Resource resource) {
            this.resource = resource;
        }

        public Resource getResource() {
            return resource;
        }
    }

    interface ResourceListener extends Listener<ResourceEvent> {
        default void onOpened(ResourceEvent event) {}
        default void onClosed(ResourceEvent event) {}
        default void onUsed(ResourceEvent event) {}
    }

    private EventDispatcher<ResourceEvent, ResourceListener, String> createDispatcher() {
        var dispatcher = new EventDispatcher<ResourceEvent, ResourceListener, String>();
        var eventMap = new EventMap<ResourceEvent, ResourceListener, String>();

        eventMap.put("opened", ResourceListener::onOpened);
        eventMap.put("closed", ResourceListener::onClosed);
        eventMap.put("used", ResourceListener::onUsed);

        dispatcher.setEventMap(eventMap);

        return dispatcher;
    }

    @Test
    void openedEventIsDispatched() {
        var dispatcher = createDispatcher();
        var resource = new Resource();
        resource.open();

        boolean[] called = { false };
        dispatcher.addConsumer((listener, event) -> called[0] = true);

        dispatcher.dispatch("opened", new ResourceEvent(resource));
        assertTrue(called[0]);
    }

    @Test
    void usedEventIsDispatched() {
        var dispatcher = createDispatcher();
        var resource = new Resource();
        resource.open();
        resource.use();

        boolean[] called = { false };
        dispatcher.addListener(new ResourceListener() {
            @Override
            public void onUsed(ResourceEvent event) {
                called[0] = true;
                assertSame(resource, event.getResource());
            }
        });

        dispatcher.dispatch("used", new ResourceEvent(resource));
        assertTrue(called[0]);
    }

    @Test
    void multipleListenersReceiveEvent() {
        var dispatcher = createDispatcher();
        int[] calls = { 0 };

        dispatcher.addListener(new ResourceListener() {
            @Override
            public void onOpened(ResourceEvent event) { calls[0]++; }
        });
        dispatcher.addListener(new ResourceListener() {
            @Override
            public void onOpened(ResourceEvent event) { calls[0]++; }
        });

        dispatcher.dispatch("opened", new ResourceEvent(new Resource()));
        assertEquals(2, calls[0]);
    }

    @Test
    void highPriorityListenerRunsFirst() {
        var dispatcher = createDispatcher();
        var order = new StringBuilder();

        ResourceListener normal = new ResourceListener() {
            @Override
            public void onOpened(ResourceEvent event) { order.append("normal "); }
        };

        ResourceListener high = new ResourceListener() {
            @Override
            public void onOpened(ResourceEvent event) { order.append("high "); }
        };

        dispatcher.addListener(normal);
        dispatcher.addListener(ListenerPriority.HIGH, high);

        dispatcher.dispatch("opened", new ResourceEvent(new Resource()));
        assertEquals("high normal ", order.toString());
    }

    @Test
    void consumedEventStopsFurtherProcessing() {
        var dispatcher = createDispatcher();
        int[] calls = { 0 };

        ResourceListener consumer = new ResourceListener() {
            @Override
            public void onUsed(ResourceEvent event) {
                calls[0]++;
                event.consume();
            }
        };

        ResourceListener afterConsumer = new ResourceListener() {
            @Override
            public void onUsed(ResourceEvent event) { calls[0]++; }
        };

        dispatcher.addListener(consumer);
        dispatcher.addListener(afterConsumer);

        dispatcher.dispatch("used", new ResourceEvent(new Resource()));
        assertEquals(1, calls[0]);
    }
}