/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application;

import ej.style.font.FontProfile;
import ej.style.font.loader.AbstractFontLoader;

/**
 *
 */
public class LightFontLoader extends AbstractFontLoader {

	@Override
	protected int getFontHeight(FontProfile fontProfile) {
		switch (fontProfile.getSize()) {
		case LENGTH:
			return fontProfile.getSizeValue();
		case XX_LARGE:
		case X_LARGE:
		case LARGE:
			return 40;
		case MEDIUM:
		default:
			return 20;
		}
	}

}
