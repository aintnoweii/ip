/**
 * Entry point for the Nova chatbot.
 * At present it only prints the startup banner.
 */
public class Nova {
    public static void main(String[] args) {
        String banner = " _   _                      \n"
                + "| \\ | |  ___           __ _ \n"
                + "|  \\| | / _ \\ __   __ / _` |\n"
                + "| |\\  || (_) |\\ \\ / /| (_| |\n"
                + "|_| \\_| \\___/  \\ V /  \\__,_|\n"
                + "                \\_/         ";

        String horizontalLine = "____________________________________________________________\n";
        String helloMessage = "Hello! I'm Nova.\nWhat can I do for you?";
        String byeMessage = "Bye. Hope to see you again soon!";

        System.out.println(horizontalLine + banner + "\n" + helloMessage + "\n" +
                horizontalLine + byeMessage + "\n" + horizontalLine);
    }
}
