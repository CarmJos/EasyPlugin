package cc.carm.lib.easyplugin.configuration.impl;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigSound extends ConfigStringCast<ConfigSound.SoundData> {

	public ConfigSound(@NotNull String configSection) {
		this(configSection, null);
	}

	public ConfigSound(@NotNull String configSection,
					   @Nullable Sound defaultValue) {
		this(null, configSection, defaultValue);
	}

	public ConfigSound(@Nullable FileConfig source, @NotNull String configSection, @Nullable Sound defaultValue) {
		super(source, configSection, string -> {
			Sound finalSound = defaultValue;
			float volume = 1;
			float pitch = 1;

			if (string != null) {
				String[] args = string.contains(":") ? string.split(":") : new String[]{string};
				try {
					if (args.length >= 1) finalSound = Sound.valueOf(args[0]);
					if (args.length >= 2) volume = Float.parseFloat(args[1]);
					if (args.length >= 3) pitch = Float.parseFloat(args[2]);
				} catch (Exception exception) {
					Bukkit.getLogger().severe("声音 " + configSection + " 配置错误，不存在 " + string + " ，请检查。");
					Bukkit.getLogger().severe("In " + configSection + " (" + string + ") doesn't match any sound name.");
				}
			}

			if (finalSound != null) {
				return new SoundData(finalSound, volume, pitch);
			} else {
				return null;
			}
		}, defaultValue == null ? null : new SoundData(defaultValue));
	}

	public void set(@Nullable SoundData value) {
		if (value == null) {
			set((String) null);
		} else if (value.pitch != 1) {
			set(value.type, value.volume, value.pitch);
		} else if (value.volume != 1) {
			set(value.type, value.volume);
		} else {
			set(value.type);
		}
	}

	public void set(Sound value) {
		set(value.name());
	}

	public void set(Sound value, float volume) {
		set(value.name() + (volume != 1 ? ":" + volume : ""));
	}

	public void set(Sound value, float volume, float pitch) {
		set(value.name() + ":" + volume + (pitch != 1 ? ":" + pitch : ""));
	}

	public void play(Player player) {
		SoundData data = get();
		if (data != null) data.play(player);
	}

	public void save() {
		if (getSource() != null) getSource().save();
	}

	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getPluginConfiguration() : source;
	}

	public static class SoundData {
		Sound type;
		float volume;
		float pitch;

		public SoundData(Sound type) {
			this(type, 1, 1);
		}

		public SoundData(Sound type, float volume) {
			this(type, volume, 1);
		}

		public SoundData(Sound type, float volume, float pitch) {
			this.type = type;
			this.volume = volume;
			this.pitch = pitch;
		}

		public void play(Player player) {
			player.playSound(player.getLocation(), type, volume, pitch);
		}

	}


}
