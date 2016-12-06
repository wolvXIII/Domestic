/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package sew.light.application.widget;

import ej.widget.StyledDesktop;
import ej.widget.StyledDialog;

public class CenteredDialog extends StyledDialog {

	@Override
	public void validate(int widthHint, int heightHint) {
		super.validate(widthHint, heightHint);

		// Center the dialog.
		StyledDesktop desktop = getDesktop();
		int desktopWidth = desktop.getWidth();
		int desktopHeight = desktop.getHeight();

		setLocation((desktopWidth - getPreferredWidth()) / 2, (desktopHeight - getPreferredHeight()) / 2);
	}

}
