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
    <version>1.2.1</version>
</dependency>
```

## Features

### Events

- All events extend the abstract `Event` class.
- Events are immutable data carriers with a creation timestamp.
- Events support consumption via `consume()` to prevent further propagation.

### Listeners

- Implement the `Listener<E, T>` interface to handle events of type `E` with classification `T`.
- Listeners can have priorities: `HIGHEST`, `HIGH`, `NORMAL`, `LOW`.
- Can be registered through `EventDispatcher` or `ListenersManager`.

### Consumers

- `EventConsumer<E>` provides a functional interface for handling events without full listener classes.
- Consumers can be registered with priorities and specific event types.

### Event Dispatcher

- `EventDispatcher<E, L, T>` is the central component that manages listeners, consumers, and event routing.
- Supports listener and consumer priority order.
- Dispatches events to all appropriate listeners and consumers.
- Exception handling via `EventExceptionHandler<L, E, T>`.
- Thread-safe when using concurrent collections.

### Event Exception Handling

- `EventExceptionHandler<L, E, T>` allows custom exception handling during event processing.
- Supports chaining via `andThen()` method.
- Default handler prints stack traces of unhandled exceptions.

### ListenersManager

- Provides a simplified API to manage listeners and consumers.
- Delegates operations to an underlying `EventDispatcher`.

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
dispatcher.addListener(ListenerPriority.HIGH, new MyListener());
```

### Using event consumers

```java
dispatcher.addConsumer("eventType", e -> {
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
import org.theko.events.Listener;

class MyEvent extends Event {
    boolean success;

    MyEvent(boolean success) {
        this.success = success;
    }

    boolean isSuccessful() {
        return success;
    }
}

class MyListener implements Listener<MyEvent, String> {
    @Override
    public void onEvent(String type, MyEvent event) {
        if (event.isSuccessful()) {
            System.out.println("Event successful!");
        } else {
            System.err.println("Event failed!");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        EventDispatcher<MyEvent, MyListener, String> dispatcher = new EventDispatcher<>();
        dispatcher.addListener(new MyListener());
        dispatcher.dispatch(new MyEvent(true)); // dispatch event
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
* `Listener<E, T>` – Generic listener interface
* `ListenerPriority` – Enum defining listener execution priority
* `EventConsumer<E>` – Functional interface for event consumers
* `EventDispatcher<E, L, T>` – Main event dispatching system
* `EventExceptionHandler<L, E, T>` – Handles exceptions during event processing
* `ListenersManager<E, L, T>` – Simplifies listener and consumer management
* `EventMap<E, L, T>` – Optional mapping for routing events

---

## License

MIT License