/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tomgrill.gdxdialogs.iosmoe.dialogs;

import apple.foundation.struct.NSRange;
import apple.uikit.protocol.UITextFieldDelegate;
import com.badlogic.gdx.Gdx;

import org.moe.natj.general.ann.ByValue;
import org.moe.natj.general.ann.NInt;
import de.tomgrill.gdxdialogs.core.GDXDialogsVars;
import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;
import apple.uikit.UIAlertView;
import apple.uikit.UITextField;
import apple.uikit.enums.UIAlertViewStyle;
import apple.uikit.protocol.UIAlertViewDelegate;

public class IOSMOEGDXTextPrompt implements GDXTextPrompt {

	private String message = "";
	private String title = "";
	private String cancelLabel = "";
	private String confirmLabel = "";

	private TextPromptListener listener;

	private UIAlertView alertView;

	private int maxLength = 16;

	private long inputType = UIAlertViewStyle.PlainTextInput;

	public IOSMOEGDXTextPrompt () {
	}

	@Override
	public GDXTextPrompt show() {

		if (alertView == null) {
			throw new RuntimeException(GDXTextPrompt.class.getSimpleName() + " has not been build. Use build() before show().");
		}
		Gdx.app.debug(GDXDialogsVars.LOG_TAG, IOSMOEGDXTextPrompt.class.getSimpleName() + " now shown.");
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				alertView.show();
			}
		});
		return this;
	}

	@Override
	public GDXTextPrompt build() {

		if (alertView != null) {
			alertView.dealloc();
			alertView = null;
		}
		UIAlertViewDelegate delegate = new UIAlertViewDelegate() {
			@Override public void alertViewDidDismissWithButtonIndex (UIAlertView alertView, @NInt long buttonIndex) {
				if (listener != null) {
					if (buttonIndex == 0) {
						listener.cancel();
					}

					if (buttonIndex == 1) {
						UITextField uiTextField = alertView.textFieldAtIndex(0);
						listener.confirm(uiTextField.text());
					}
				}
			}
		};

		// doesnt work :/
//		alertView = UIAlertView.alloc().initWithTitleMessageDelegateCancelButtonTitleOtherButtonTitles(
//			title, message, delegate, cancelLabel, "Cancel???", confirmLabel);
		alertView = UIAlertView.alloc().init();
		alertView.setTitle(title);
		alertView.setMessage(message);
		alertView.setDelegate(delegate);
		alertView.addButtonWithTitle(cancelLabel);
		alertView.addButtonWithTitle(confirmLabel);
		alertView.setCancelButtonIndex(0);
		alertView.setAlertViewStyle(inputType);

		UITextField uiTextField = alertView.textFieldAtIndex(0);
		uiTextField.setDelegate(new UITextFieldDelegate() {
			@Override
			public boolean textFieldShouldChangeCharactersInRangeReplacementString(UITextField textField, @ByValue NSRange range, String additionalText) {
				if (textField.text().length() + additionalText.length() > maxLength) {
					String oldText = textField.text();
					String newText = oldText + additionalText;
					uiTextField.setText(newText.substring(0, maxLength));
					return false;
				}
				return true;
			}
		});


		return this;
	}

	@Override
	public GDXTextPrompt setTitle(CharSequence title) {
		this.title = (String) title;
		return this;
	}

	@Override
	public GDXTextPrompt setMaxLength(int maxLength) {
		if (maxLength < 1) {
			throw new RuntimeException("Char limit must be >= 1");
		}
		this.maxLength = maxLength;
		return this;
	}

	@Override
	public GDXTextPrompt setMessage(CharSequence message) {
		this.message = (String) message;
		return this;
	}

	@Override
	public GDXTextPrompt setValue(CharSequence inputTip) {
		return this;
	}

	@Override
	public GDXTextPrompt setCancelButtonLabel(CharSequence label) {
		this.cancelLabel = (String) label;
		return this;
	}

	@Override
	public GDXTextPrompt setConfirmButtonLabel(CharSequence label) {
		this.confirmLabel = (String) label;
		return this;
	}

	@Override
	public GDXTextPrompt setTextPromptListener(TextPromptListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public GDXTextPrompt setInputType(InputType inputType) {
		switch (inputType) {
			case PLAIN_TEXT:
				this.inputType = UIAlertViewStyle.PlainTextInput;
				break;

			case PASSWORD:
				this.inputType = UIAlertViewStyle.SecureTextInput;
				break;
		}
		return this;
	}

	@Override
	public GDXTextPrompt dismiss() {
		if (alertView == null) {
			throw new RuntimeException(GDXTextPrompt.class.getSimpleName() + " has not been build. Use build() before dismiss().");
		}
		Gdx.app.debug(GDXDialogsVars.LOG_TAG, IOSMOEGDXTextPrompt.class.getSimpleName() + " dismissed.");
		alertView.dismissWithClickedButtonIndexAnimated(0, false);
		return this;
	}

}
