package net.holyworld.holyadmin;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.Locale;

public final class FreezeManager {
	public static final FreezeManager INSTANCE = new FreezeManager();

	private String nick;
	private long startedAt;

	private FreezeManager() {
	}

	public void handleChatInput(String raw) {
		String command = raw == null ? "" : raw.trim();
		if (command.startsWith("/")) {
			command = command.substring(1).trim();
		}
		String lower = command.toLowerCase(Locale.ROOT);
		if (!lower.startsWith("freezing")) {
			return;
		}
		String rest = command.substring("freezing".length()).trim();
		if (rest.isEmpty()) {
			clear();
		} else {
			start(rest.split("\\s+")[0]);
		}
	}

	public void start(String nick) {
		this.nick = nick;
		this.startedAt = System.currentTimeMillis();
	}

	public void clear() {
		this.nick = null;
		this.startedAt = 0;
	}

	public String getNick() {
		return nick;
	}

	public boolean isActive() {
		return nick != null && !nick.isEmpty();
	}

	public long getElapsedMs() {
		return isActive() ? System.currentTimeMillis() - startedAt : 0;
	}

	public static String formatTime(long ms) {
		long totalSec = Math.max(0, ms) / 1000;
		long h = totalSec / 3600;
		long m = (totalSec % 3600) / 60;
		long s = totalSec % 60;
		return h > 0
			? String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s)
			: String.format(Locale.ROOT, "%02d:%02d", m, s);
	}

	public Text rewriteCheckMessage(Text message) {
		if (!isActive() || message == null) {
			return null;
		}
		String body = message.getString();
		if (body == null || body.isEmpty()) {
			return null;
		}
		String content = extractCheckMessage(body, nick);
		if (content == null) {
			return null;
		}
		Style red = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.RED));
		MutableText out = Text.literal("");
		out.append(Text.literal("[Проверка] ").setStyle(red));
		out.append(Text.literal(nick + ": ").setStyle(red));
		out.append(Text.literal(content).setStyle(red));
		return out;
	}

	private static String extractCheckMessage(String body, String nick) {
		String clean = body.replaceAll("(?s)§.", "").trim();
		String lowerNick = nick.toLowerCase(Locale.ROOT);
		String lowerBody = clean.toLowerCase(Locale.ROOT);
		if (clean.isEmpty()) {
			return null;
		}

		if (lowerBody.startsWith("<" + lowerNick + ">")) {
			return stripCodes(clean.substring(lowerNick.length() + 2).trim());
		}
		if (lowerBody.startsWith("[" + lowerNick + "]")) {
			int k = skipSpaces(clean, lowerNick.length() + 2);
			if (k < clean.length() && clean.charAt(k) == ':') {
				k++;
			}
			return stripCodes(clean.substring(k).trim());
		}
		if (lowerBody.startsWith(lowerNick)) {
			int k = skipSpaces(clean, nick.length());
			if (k < clean.length() && clean.charAt(k) == '[') {
				int t = clean.indexOf(']', k);
				if (t >= 0) {
					k = skipSpaces(clean, t + 1);
				}
			}
			if (k < clean.length() && clean.charAt(k) == ':') {
				return stripCodes(clean.substring(k + 1).trim());
			}
			return null;
		}
		if (clean.charAt(0) == '[') {
			int close = clean.indexOf(']');
			if (close >= 0) {
				int i = skipSpaces(clean, close + 1);
				int j = i;
				while (j < clean.length() && clean.charAt(j) != '[' && clean.charAt(j) != ':') {
					j++;
				}
				if (clean.substring(i, j).trim().equalsIgnoreCase(nick)) {
					int k = skipSpaces(clean, j);
					if (k < clean.length() && clean.charAt(k) == '[') {
						int t = clean.indexOf(']', k);
						if (t < 0) {
							return null;
						}
						k = skipSpaces(clean, t + 1);
					}
					if (k < clean.length() && clean.charAt(k) == ':') {
						k++;
					}
					return stripCodes(clean.substring(k).trim());
				}
			}
		}
		return null;
	}

	private static String stripCodes(String s) {
		return s.replaceAll("(?s)§.", "");
	}

	private static int skipSpaces(String s, int from) {
		int k = from;
		while (k < s.length() && s.charAt(k) == ' ') {
			k++;
		}
		return k;
	}
}
