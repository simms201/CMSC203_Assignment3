package BobCircus;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FXMainPane extends BorderPane {

	private TextField plainTextTextField, inputForEncryptionTextField, encryptedStringTextField3, decryptedTextField4;
	private Label plainTextLabel, descriptionForInputLabel, encryptedLabel3, decryptedLabel4;
	private RadioButton radioButton1, radioButton2, radioButton3;
	private Button encryption, decryption, clearButton, exitButton;

	public FXMainPane() {
		buildUI();
		addButtonActions();
	}

	private void buildUI() {
		Insets inset = new Insets(10);

		// Text Fields
		plainTextTextField = new TextField();
		inputForEncryptionTextField = new TextField();
		encryptedStringTextField3 = new TextField();
		decryptedTextField4 = new TextField();

		// Labels
		plainTextLabel = new Label("Enter plain-text string to encrypt");
		descriptionForInputLabel = new Label("Enter a key (keyword for Vigenere and Playfair; shift number for Caesar)");
		encryptedLabel3 = new Label("Encrypted string");
		decryptedLabel4 = new Label("Decrypted string");

		// Radio Buttons
		radioButton1 = new RadioButton("Vigenere Cipher");
		radioButton2 = new RadioButton("Playfair Cipher");
		radioButton3 = new RadioButton("Caesar Cipher");

		ToggleGroup group = new ToggleGroup();
		radioButton1.setToggleGroup(group);
		radioButton2.setToggleGroup(group);
		radioButton3.setToggleGroup(group);
		radioButton1.setSelected(true);

		// Cipher Selection Box
		HBox topBox = new HBox(20, radioButton1, radioButton2, radioButton3);
		topBox.setAlignment(Pos.CENTER);
		topBox.setPadding(inset);

		// Center Fields
		VBox centerBox = new VBox(10,
				plainTextLabel, plainTextTextField,
				encryptedLabel3, encryptedStringTextField3,
				decryptedLabel4, decryptedTextField4,
				descriptionForInputLabel, inputForEncryptionTextField
		);
		centerBox.setPadding(inset);

		// Buttons
		encryption = new Button("Encrypt");
		decryption = new Button("Decrypt");
		clearButton = new Button("Clear");
		exitButton = new Button("Exit");

		HBox bottomBox = new HBox(20, encryption, decryption, clearButton, exitButton);
		bottomBox.setAlignment(Pos.CENTER);
		bottomBox.setPadding(inset);

		// Set layout
		setTop(topBox);
		setCenter(centerBox);
		setBottom(bottomBox);
	}

	private void addButtonActions() {
		exitButton.setOnAction(e -> Platform.exit());

		clearButton.setOnAction(e -> {
			plainTextTextField.clear();
			inputForEncryptionTextField.clear();
			encryptedStringTextField3.clear();
			decryptedTextField4.clear();
		});

		encryption.setOnAction(e -> {
			String plain = plainTextTextField.getText();
			String key = inputForEncryptionTextField.getText();
			
			if (plain.isEmpty() || key.isEmpty()) {
				showErrorAlert("Input Validation Error", "Fields cannot be empty!", "Please enter both plain text and a key.");
				return;
			}

			// Validate bounds before doing anything
			if (!CryptoManager.isStringInBounds(plain)) {
				showErrorAlert("Bounds Error", "String contains out of bounds characters!", 
						"Please use characters within the allowed range (ASCII Space to Underline '_').");
				return;
			}

			try {
				String result = "";
				if (radioButton1.isSelected()) {
					result = CryptoManager.vigenereEncryption(plain, key);
				} else if (radioButton2.isSelected()) {
					result = CryptoManager.playfairEncryption(plain, key);
				} else {
					// Safeguard Caesar Cipher key parsing
					try {
						int shift = Integer.parseInt(key.trim());
						result = CryptoManager.caesarEncryption(plain, shift);
					} catch (NumberFormatException nfe) {
						showErrorAlert("Invalid Key Type", "Caesar Cipher Key Error", "For Caesar Cipher, the key must be an integer.");
						return;
					}
				}
				encryptedStringTextField3.setText(result);
			} catch (Exception ex) {
				encryptedStringTextField3.setText("Error: " + ex.getMessage());
			}
		});

		decryption.setOnAction(e -> {
			String encrypted = encryptedStringTextField3.getText();
			String key = inputForEncryptionTextField.getText();
			
			if (encrypted.isEmpty() || key.isEmpty()) {
				showErrorAlert("Input Validation Error", "Fields cannot be empty!", "Please ensure an encrypted string and a key exist.");
				return;
			}

			try {
				String result = "";
				if (radioButton1.isSelected()) {
					result = CryptoManager.vigenereDecryption(encrypted, key);
				} else if (radioButton2.isSelected()) {
					result = CryptoManager.playfairDecryption(encrypted, key);
				} else {
					// Safeguard Caesar Cipher key parsing
					try {
						int shift = Integer.parseInt(key.trim());
						result = CryptoManager.caesarDecryption(encrypted, shift);
					} catch (NumberFormatException nfe) {
						showErrorAlert("Invalid Key Type", "Caesar Cipher Key Error", "For Caesar Cipher, the key must be an integer.");
						return;
					}
				}
				decryptedTextField4.setText(result);
			} catch (Exception ex) {
				decryptedTextField4.setText("Error: " + ex.getMessage());
			}
		});
	}

	// Helper method to display clean popups for faulty inputs instead of application crashing
	private void showErrorAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}