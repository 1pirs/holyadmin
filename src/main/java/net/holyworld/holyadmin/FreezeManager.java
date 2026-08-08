package net.holyworld.holyadmin;

import net.holyworld.holyadmin.clan.ClanParser;
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
		if (lower.startsWith("clan list")) {
			ClanParser.INSTANCE.arm();
		}
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
		if (body.startsWith("[Проверка]")) {
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
		if (clean.isEmpty()) {
			return null;
		}
		String lowerClean = clean.toLowerCase(Locale.ROOT);
		String lowerNick = nick.toLowerCase(Locale.ROOT);
		int from = 0;
		while (true) {
			int idx = lowerClean.indexOf(lowerNick, from);
			if (idx < 0) {
				return null;
			}
			if (isSpeakerBoundary(clean, idx)) {
				String content = extractAfterNick(clean, idx + nick.length());
				if (content != null) {
					return content;
				}
			}
			from = idx + 1;
		}
	}

	private static String extractAfterNick(String clean, int from) {
		int k = skipSpaces(clean, from);
		if (k < clean.length() && clean.charAt(k) == '[') {
			int t = clean.indexOf(']', k);
			if (t >= 0) {
				k = skipSpaces(clean, t + 1);
			}
		}
		if (startsWithIgnoreCase(clean, k, "mode:")) {
			int colon2 = clean.indexOf(':', k + 5);
			k = skipSpaces(clean, colon2 >= 0 ? colon2 + 1 : k + 5);
			return stripCodes(clean.substring(k).trim());
		}
		if (k < clean.length() && (clean.charAt(k) == ':' || clean.charAt(k) == '>' || clean.charAt(k) == ']')) {
			char sep = clean.charAt(k);
			int msgStart;
			if (sep == ':') {
				msgStart = skipSpaces(clean, k + 1);
			} else {
				int m = skipSpaces(clean, k + 1);
				msgStart = (m < clean.length() && clean.charAt(m) == ':') ? skipSpaces(clean, m + 1) : m;
			}
			String content = stripCodes(clean.substring(msgStart).trim());
			return content.isEmpty() ? null : content;
		}
		return null;
	}

	private static boolean startsWithIgnoreCase(String s, int from, String prefix) {
		if (from < 0 || from + prefix.length() > s.length()) {
			return false;
		}
		return s.regionMatches(true, from, prefix, 0, prefix.length());
	}

	private static boolean isSpeakerBoundary(String s, int idx) {
		if (idx == 0) {
			return true;
		}
		char c = s.charAt(idx - 1);
		return !Character.isLetterOrDigit(c) && c != '_';
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
