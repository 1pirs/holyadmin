package net.holyworld.holyadmin.hud;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.Color;

public final class ShimmerText {
	private ShimmerText() {
	}

	public static Text shimmerBlue(String s, float t) {
		MutableText out = Text.literal("");
		int[] cps = s.codePoints().toArray();
		for (int i = 0; i < cps.length; i++) {
			float wave = (float) Math.sin((i * 0.35f) - t * 3.0f);
			float brightness = 0.75f + 0.25f * (0.5f + 0.5f * wave);
			int color = Color.HSBtoRGB(0.58f, 0.9f, brightness);
			out.append(Text.literal(new String(Character.toChars(cps[i])))
				.setStyle(Style.EMPTY.withColor(color)));
		}
		return out;
	}
}
