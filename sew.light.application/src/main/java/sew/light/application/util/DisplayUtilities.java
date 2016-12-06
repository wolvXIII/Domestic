/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package sew.light.application.util;

import ej.microui.display.Display;

/**
 *
 */
public class DisplayUtilities {

	public static boolean isLandscape() {
		return isLandscape(Display.getDefaultDisplay());
	}

	public static boolean isLandscape(Display display) {
		return display.getWidth() > display.getHeight();
	}

}
