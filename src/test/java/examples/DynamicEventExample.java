package examples;

import java.util.Scanner;

import org.theko.events.DynamicEvent;
import org.theko.events.DynamicEventDispatcher;

/*
 * Example of a dynamic event dispatcher with runtime-typed payloads.
 *
 * Flexible and easy to use, but type mismatches and errors are only
 * detected at runtime, which can make debugging harder.
 */

public class DynamicEventExample {

    public static void main(String[] args) {
        DynamicEventDispatcher dispatcher = new DynamicEventDispatcher();

        dispatcher.addConsumer("on-input", (type, event) -> {
            System.out.println(String.format("Input: \"%s\" (%d chars)",
                    event.get("input"), event.get("length")));
        });

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if (input.isBlank())
                    break;

                dispatcher.dispatch("on-input", new DynamicEvent()
                    .with("input", input)
                    .with("length", input.length())
                );
            }
        }
    }
}