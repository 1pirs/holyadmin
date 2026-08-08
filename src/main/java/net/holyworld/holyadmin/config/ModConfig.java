package net.holyworld.holyadmin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {
	public static final ModConfig INSTANCE = new ModConfig();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private Path file;
	private String vk = "";

	private ModConfig() {
	}

	public void load() {
		file = FabricLoader.getInstance().getConfigDir().resolve("holyadmin.json");
		if (file == null || !Files.exists(file)) {
			return;
		}
		try {
			Data data = GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), Data.class);
			if (data != null && data.vk != null) {
				vk = data.vk.trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getVk() {
		return vk;
	}

	public void setVk(String vk) {
		this.vk = vk == null ? "" : vk.trim();
		save();
	}

	private void save() {
		if (file == null) {
			return;
		}
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(new Data(vk)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class Data {
		String vk;

		Data(String vk) {
			this.vk = vk;
		}
	}
}
