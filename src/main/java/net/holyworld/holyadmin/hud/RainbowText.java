package net.holyworld.holyadmin.hud;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.Color;

public final class RainbowText {
	private RainbowText() {
	}

	public static Text rainbow(String s, float hueStart, float hueStep, float saturation, float brightness) {
		MutableText out = Text.literal("");
		int[] cps = s.codePoints().toArray();
		for (int i = 0; i < cps.length; i++) {
			float hue = (hueStart + i * hueStep) % 1.0f;
			if (hue < 0) {
				hue += 1.0f;
			}
			int color = Color.HSBtoRGB(hue, saturation, brightness);
			out.append(Text.literal(new String(Character.toChars(cps[i])))
				.setStyle(Style.EMPTY.withColor(color)));
		}
		return out;
	}
}
