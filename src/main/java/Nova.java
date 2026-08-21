import java.util.Scanner;

/**
 * Entry point for the Nova chatbot.
 * At present it only prints the startup banner.
 */
public class Nova {
    private static final String NAME = "Nova";
    private static final String BANNER = " _   _                      \n"
            + "| \\ | |  ___           __ _ \n"
            + "|  \\| | / _ \\ __   __ / _` |\n"
            + "| |\\  || (_) |\\ \\ / /| (_| |\n"
            + "|_| \\_| \\___/  \\ V /  \\__,_|\n"
            + "                \\_/         ";
    private static final String DIVIDER = "_".repeat(60);
    private static final String HELLO_MESSAGE = String.format("Hello! I'm %s.\nWhat can I do for you?", NAME);
    private static final String BYE_MESSAGE = "Bye. Hope to see you again soon!";

    public static void main(String[] args) {
        Nova.printGreeting();
        Nova.echo();
    }

    private static void printGreeting() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(HELLO_MESSAGE);
        System.out.println(DIVIDER);
    }

    private static void printFarewell() {
        System.out.println(BYE_MESSAGE);
        System.out.println(DIVIDER);
    }

    private static void echo() {
        boolean isDone = false;
        System.out.println("Say something: ");

        while (!isDone) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            System.out.println(DIVIDER);
            if (input.equals("bye")) {
                isDone = true;
                Nova.printFarewell();
            } else {
                System.out.println(input);
                System.out.println(DIVIDER);
            }
        }
    }
}
