package net.holyworld.holyadmin;

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
}
