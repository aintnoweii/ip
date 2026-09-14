package nova;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * One message in the transcript, built from DialogBox.fxml.
 *
 * <p>The conversation is between a person and an application rather than
 * between two people, so the two sides are deliberately not drawn alike.
 * A user message is a short line of speech: a compact tinted bubble on the
 * right that hugs its text, with no portrait, since the user needs no
 * reminder of who they are. A Nova message is program output: a full-width
 * panel on the left in a fixed-width font, marked with an accent stripe and
 * the app's badge. The fixed-width font is not only decoration -- a numbered
 * task list only lines up in columns if every character is the same width.
 *
 * <p>The colours and fonts live in {@code /css/main.css}; this class decides
 * only the layout differences, which CSS cannot express.
 */
public class DialogBox extends HBox {
    /** Fraction of the window width a user bubble may fill before it wraps. */
    private static final double USER_BUBBLE_WIDTH_FRACTION = 0.72;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load DialogBox.fxml", e);
        }

        // These are injected by name: the fx:id values in DialogBox.fxml must
        // match these field names. Renaming one side only leaves the field
        // null, and the NPE below would not say why.
        assert dialog != null : "DialogBox.fxml is missing fx:id=\"dialog\"";
        assert displayPicture != null : "DialogBox.fxml is missing fx:id=\"displayPicture\"";

        dialog.setText(text);
    }

    /**
     * Returns a dialog box for a command the user typed.
     *
     * @param text text the user entered.
     * @return the dialog box, styled as a right-aligned bubble.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.applyUserStyle();
        return box;
    }

    /**
     * Returns a dialog box for one of Nova's replies.
     *
     * @param text   text of Nova's reply.
     * @param avatar Nova's badge, shown beside the panel.
     * @return the dialog box, styled as a left-aligned output panel.
     */
    public static DialogBox getNovaDialog(String text, Image avatar) {
        DialogBox box = new DialogBox(text);
        box.applyNovaStyle(avatar);
        return box;
    }

    /**
     * Styles this box as something the user said: a bubble against the right
     * edge, with the avatar removed entirely rather than merely hidden, so it
     * takes up no space in the row.
     */
    private void applyUserStyle() {
        displayPicture.setVisible(false);
        displayPicture.setManaged(false);

        setAlignment(Pos.TOP_RIGHT);
        dialog.getStyleClass().add("user-bubble");

        // A short command should hug its text rather than stretch across the
        // window; a long one wraps instead of pushing the bubble off the edge.
        // Bound rather than fixed so the cap follows the window as it resizes.
        HBox.setHgrow(dialog, Priority.NEVER);
        dialog.maxWidthProperty().bind(widthProperty().multiply(USER_BUBBLE_WIDTH_FRACTION));
    }

    /**
     * Styles this box as something Nova reported: an output panel spanning the
     * window, with the badge on the left.
     *
     * @param avatar Nova's badge.
     */
    private void applyNovaStyle(Image avatar) {
        displayPicture.setImage(avatar);
        flip();
        dialog.getStyleClass().add("nova-panel");

        // Unlike the user's bubble, the panel fills whatever width is left, so
        // a task list is not needlessly re-wrapped in a wide window.
        HBox.setHgrow(dialog, Priority.ALWAYS);
        dialog.setMaxWidth(Double.MAX_VALUE);
    }

    /**
     * Flips the dialog box so the ImageView is on the left and the text on
     * the right, used to tell the two speakers apart at a glance.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }
}
