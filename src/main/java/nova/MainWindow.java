package nova;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI window: a scrolling transcript of dialog boxes
 * above a text field and a Send button.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Nova nova;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private final Image novaImage =
            new Image(this.getClass().getResourceAsStream("/images/Nova.png"));

    /**
     * Keeps the transcript scrolled to the newest message as it grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Nova instance this window talks to, and shows its
     * greeting as the first dialog box.
     *
     * @param nova the chatbot backing this window.
     */
    public void setNova(Nova nova) {
        this.nova = nova;
        dialogContainer.getChildren().add(DialogBox.getNovaDialog(nova.getGreeting(), novaImage));
    }

    /**
     * Creates two dialog boxes, one echoing the user's input and the other
     * containing Nova's reply, appends them to the transcript, and clears
     * the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = nova.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getNovaDialog(response, novaImage)
        );
        userInput.clear();
    }
}
