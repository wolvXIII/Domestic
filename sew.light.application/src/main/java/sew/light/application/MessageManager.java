package sew.light.application;

import java.util.ArrayList;
import java.util.List;

import ej.widget.StyledDesktop;
import ej.widget.StyledDialog;
import ej.widget.basic.Label;
import ej.widget.composed.Button;
import ej.widget.container.Dock;
import ej.widget.listener.OnClickListener;
import sew.light.MessageListener;
import sew.light.application.style.ClassSelectors;
import sew.light.application.widget.CenteredDialog;

class MessageManager implements MessageListener {

	private final StyledDesktop desktop;
	private final List<String> messages;
	private final List<String> discardedMessages;
	private StyledDialog dialog;

	public MessageManager(StyledDesktop desktop) {
		this.desktop = desktop;
		this.messages = new ArrayList<>();
		this.discardedMessages = new ArrayList<>();
	}

	@Override
	public void onNewMessage(final String message) {
		System.out.println("LightApplication.DialogMessageListener.onMessage()");
		if (this.messages.contains(message) || this.discardedMessages.contains(message)) {
			// Already in the messages.
			return;
		}
		boolean empty = this.messages.isEmpty();
		this.messages.add(message);
		if (empty) {
			new Thread() { // Sorry…
				@Override
				public void run() {
					showDialog(message);
				}

			}.start();
		}
	}

	@Override
	public void onDiscardMessage(String message) {
		if (this.messages.get(0).equals(message)) {
			hideDialog();
		} else {
			this.messages.remove(message);
		}
	}

	private void showDialog(final String message) {
		this.dialog = new CenteredDialog();

		Dock dock = new Dock();
		Label label = new Label(message);
		label.addClassSelector(ClassSelectors.MULTILINE);
		dock.setCenter(label);
		Button cancel = new Button("CANCEL");
		cancel.addOnClickListener(new OnClickListener() {
			@Override
			public void onClick() {
				MessageManager.this.discardedMessages.add(message);
				hideDialog();
			}

		});
		dock.addBottom(cancel);
		Button ok = new Button("OK");
		ok.addOnClickListener(new OnClickListener() {
			@Override
			public void onClick() {
				MessageManager.this.dialog.hide();
				MessageManager.this.dialog = null;
			}
		});
		dock.addBottom(ok);

		this.dialog.setWidget(dock);
		this.dialog.show(this.desktop);
		this.messages.remove(message);

		// Show stacked messages.
		if (!this.messages.isEmpty()) {
			showDialog(this.messages.get(0));
		}
	}

	private void hideDialog() {
		this.dialog.hide();
		this.dialog = null;
	}
}