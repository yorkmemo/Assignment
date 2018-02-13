package io;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class Input {
    public static String readString(String message) {

        boolean done = false;
        String value = null;

        while (!done) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Text Input");
            dialog.setHeaderText("Text Input");
            dialog.setContentText(message);


            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && result.get().trim().length() > 0) {
                done = true;
                value = result.get().trim();
            }
        }

        return value;
    }

    public static int readInt(String message) {

        boolean done = false;
        int value = 0;
        int c = 0;

        while (!done) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Integer Input");
            dialog.setHeaderText("Integer Input");
            dialog.setContentText(message);

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && result.get().trim().length() > 0) {

                String temp = result.get().trim();

                try {
                    value = Integer.parseInt(temp);
                    done = true;
                } catch (NumberFormatException e) {
                    if (c == 0) {
                        message = "Enter a valid integer\n" + message;
                    }
                }
            }
            c++;
        }

        return value;
    }

    public static double readDouble(String message) {

        boolean done = false;
        double value = 0;
        int c = 0;

        while (!done) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Double Input");
            dialog.setHeaderText("Double Input");
            dialog.setContentText(message);

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && result.get().trim().length() > 0) {

                String temp = result.get().trim();

                try {
                    value = Double.parseDouble(temp);
                    done = true;
                } catch (NumberFormatException e) {
                    if (c == 0) {
                        message = "Enter a valid double\n" + message;
                    }
                }
            }
            c++;
        }

        return value;
    }

    public static boolean readBoolean(String message) {
        return readBoolean(message, "Yes", "No");
    }

    public static boolean readBoolean(String message, String trueButtonText, String falseButtonText) {
        boolean done = false;
        boolean value = false;

        while (!done) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Choice Input");
            alert.setHeaderText("Choice Input");
            alert.setContentText(message);

            ButtonType buttonTypeTrue = new ButtonType(trueButtonText);
            ButtonType buttonTypeFalse = new ButtonType(falseButtonText);

            alert.getButtonTypes().setAll(buttonTypeTrue, buttonTypeFalse);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == buttonTypeTrue) {
                value = true;
                done = true;
            } else if (result.isPresent() && result.get() == buttonTypeFalse) {
                value = false;
                done = true;
            }
        }

        return value;
    }

}
