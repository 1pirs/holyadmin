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
		String lowerBody = body.toLowerCase(Locale.ROOT);
		String lowerNick = nick.toLowerCase(Locale.ROOT);
		String content;
		if (lowerBody.startsWith("<" + lowerNick + ">")) {
			content = body.substring(lowerNick.length() + 2).trim();
		} else if (lowerBody.startsWith(lowerNick + ":")) {
			content = body.substring(lowerNick.length() + 1).trim();
		} else {
			return null;
		}
		Style red = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.RED));
		MutableText out = Text.literal("");
		out.append(Text.literal("[Проверка] ").setStyle(red));
		out.append(Text.literal(nick + ": ").setStyle(red));
		out.append(Text.literal(content).setStyle(red));
		return out;
	}
}
