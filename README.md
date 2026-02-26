# Theko-Events

**Theko-Events** is a lightweight, flexible, and type-safe event handling library for Java (14+).  
It provides a comprehensive framework for event-driven architectures with support for:

- Event dispatching
- Listener and consumer registration
- Listener priorities
- Exception handling
- Event consumption
- Thread-safe operation (dispatch occurs on the caller thread)

---

## Installation
```xml
<dependency>
    <groupId>io.github.the-koroche</groupId>
    <artifactId>theko-events</artifactId>
    <version>2.0.0</version>
</dependency>
```

Or from GitHub Releases for JAR: [Releases](https://github.com/the-koroche/theko-events/releases)

## Features

### Default generic types

- `E` represents the event type, extends `Event`.
- `L` represents the listener type, extends `Listener<E>`.
- `T` represents the event classification type.
- `X` represents the exception type, extends `Throwable`.

### Events

- All events extend the abstract `Event` class.
- Events are immutable data carriers with a creation timestamp.
- Events support consumption via `consume()` to prevent further propagation.

### Listeners

- Implement the `Listener<E>` interface to handle events of type `E` with classification `T`.
- Listeners can have priorities: `HIGHEST`, `HIGH`, `NORMAL`, `LOW`, `LOWEST` using the `ListenerPriority` class.
- Can be registered through `EventDispatcher` or `ListenersManager`.

### Listener Priority 

- `ListenerPriority` provides constants for listener priorities: `HIGHEST`, `HIGH`, `NORMAL`, `LOW`, `LOWEST`.
- Supports custom priorities using the `ListenerPriority(int)` constructor.
- Lower priorities are executed first.

### Consumers

- `EventConsumer<E>` provides a functional interface for handling events without full listener classes.
- Consumers can be registered with priorities and specific event types.

### Event Dispatcher

- `EventDispatcher<E, L, T>` is the central component that responsible for event dispatching and event routing.
- Dispatches events to all appropriate listeners and consumers.
- Exception handling via `EventExceptionHandler<L, E, T>`.
- Thread-safe when using concurrent collections.

### Listeners Manager

- `ListenersManager<E, L, T>` is the main class for managing listeners and consumers.
- Controls adding and removing listeners and consumers.
- Sorts listeners and consumers by priority and event type.
- Thread-safe when using concurrent collections.

### Listeners Manageable

- An interface for classes that can provide access to a `ListenersManager`.
- Automatically adds listeners and consumers managing to the `ListenersManager` when implemented.

### Event Exception Handling

- `EventExceptionHandler<L, E, T>` allows custom exception handling during event processing.
- Supports chaining via `andThen()` method.
- Default handler prints stack traces of unhandled exceptions.

### Event Maps

- `EventMap<E, L, T>` allows mapping event types to handlers for custom routing.

---

## Usage

### Creating and dispatching an event

```java
EventDispatcher<MyEvent, MyListener, String> dispatcher = new EventDispatcher<>();

// Create an event
MyEvent event = new MyEvent();

// Dispatch the event
dispatcher.dispatch("eventType", event);
````

### Registering a listener

```java
dispatcher.addListener(new MyListener()); // with normal priority
dispatcher.addListener(ListenerPriority.HIGH, new MyListener()); // with high priority
```

### Using event consumers

```java
dispatcher.addConsumer("eventType", (type, e) -> {
    System.out.println("Event consumed: " + e);
});
```

### Exception handling

```java
dispatcher.addExceptionHandler(MyException.class, (listener, event, ex) -> {
    System.err.println("Exception in listener: " + ex.getMessage());
});
```

## Example
```java
import org.theko.events.Event;
import org.theko.events.EventDispatcher;
import org.theko.events.EventMap;
import org.theko.events.Listener;

class MyEvent extends Event {
    private final String message;

    public MyEvent(String message) { this.message = message; }
    public MyEvent() { this(null); }

    public boolean hasMessage() { return message != null; }
    public String getMessage() { return message; }
}

interface MyListener extends Listener<MyEvent> {
    default void onOpened(MyEvent event) { }
    default void onClosed(MyEvent event) { }
    default void onMessage(MyEvent event) { }
}

class MyListenerImpl implements MyListener {
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

public class Main {
    public static void main(String[] args) {
        EventDispatcher<MyEvent, MyListener, String> dispatcher = new EventDispatcher<>();
        EventMap<MyEvent, MyListener, String> map = new EventMap<>();
        map.put("open", MyListener::onOpened);
        map.put("close", MyListener::onClosed);
        map.put("message", MyListener::onMessage);
        dispatcher.setEventMap(map);

        // Listeners are processed first, then consumers
        dispatcher.addListener(new MyListenerImpl());
        dispatcher.addConsumer("message", (type, event) -> {
            if (event.hasMessage()) {
                System.out.println("Message: " + event.getMessage());
            }
        });

        dispatcher.dispatch("open", new MyEvent());
        dispatcher.dispatch("message", new MyEvent("Hello world!"));
        dispatcher.dispatch("close", new MyEvent());
    }
}
```

---

## Thread Safety

* The dispatcher and listeners are thread-safe for registration and dispatching.
* Dispatching occurs on the caller thread.
* Internal collections use `ConcurrentHashMap` and `CopyOnWriteArrayList` to avoid concurrency issues.

---

## Requirements

* Java 14 or higher

---

## Modules / Classes

* `Event` – Base class for all events
* `Listener<E>` – Generic listener interface
* `ListenerPriority` – Class defining listener execution priority
* `ListenersManager<E, L, T>` – Manages listener and consumer registration
* `ListenersManageable<E, L, T>` – Interface for classes that can provide access to a `ListenersManager`
* `EventConsumer<E>` – Functional interface for event consumers
* `EventDispatcher<E, L, T>` – Main event dispatching system
* `EventMap<E, L, T>` – Listener mapping for routing events
* `EventExceptionHandler<L, E, T>` – Handles exceptions during event processing in listeners, consumers

---

## License

MIT License 
[LICENSE](LICENSE)