package examples;

import java.util.Scanner;

import org.theko.events.Event;
import org.theko.events.EventDispatcher;
import org.theko.events.Listener;

/*
 * Example of a strongly-typed event system.
 *
 * Uses explicit event classes and interfaces, providing compile-time safety,
 * better IDE support, and easier refactoring at the cost of some boilerplate.
 */

public class StrongTypedEventExample {

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
            System.out.println(String.format("Input: \"%s\" (%d chars)",
                    event.input, event.length));
        }
    }

    public static void main(String[] args) {
        EventDispatcher<InputEvent, InputListener, String> dispatcher = new EventDispatcher<>();

        dispatcher.addListener(new DefaultInputListener());

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if (input.isBlank())
                    break;

                dispatcher.dispatch("on-input",
                        new InputEvent(input, input.length()));
            }
        }
    }
}