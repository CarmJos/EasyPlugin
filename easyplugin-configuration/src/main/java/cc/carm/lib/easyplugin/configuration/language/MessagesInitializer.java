package cc.carm.lib.easyplugin.configuration.language;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class MessagesInitializer {

	private MessagesInitializer() {
		// 静态方法类，不应当实例化。
	}

	public static void initialize(FileConfig source, Class<? extends MessagesRoot> rootClazz) {
		initialize(source, rootClazz, true);
	}

	public static void initialize(FileConfig source, Class<? extends MessagesRoot> rootClazz, boolean saveDefault) {
		MessagesSection sectionAnnotation = rootClazz.getAnnotation(MessagesSection.class);

		String rootSection = null;
		if (sectionAnnotation != null && sectionAnnotation.value().length() > 1) {
			rootSection = sectionAnnotation.value();
		}

		for (Class<?> innerClass : rootClazz.getDeclaredClasses()) {
			initSection(source, rootSection, innerClass, saveDefault);
		}

		for (Field field : rootClazz.getFields()) {
			initMessage(source, rootSection, rootClazz, field, saveDefault);
		}

		if (saveDefault) {
			try {
				source.save();
			} catch (IOException ignore) {
			}
		}

	}

	private static void initSection(FileConfig source, String parentSection, Class<?> clazz, boolean saveDefault) {
		if (!Modifier.isStatic(clazz.getModifiers()) || !Modifier.isPublic(clazz.getModifiers())) return;

		String section = getSection(clazz.getSimpleName(), parentSection, clazz.getAnnotation(MessagesSection.class));

		for (Class<?> innerClass : clazz.getDeclaredClasses()) initSection(source, section, innerClass, saveDefault);
		for (Field field : clazz.getFields()) initMessage(source, section, clazz, field, saveDefault);

	}

	private static void initMessage(FileConfig source, String parentSection, Class<?> clazz, Field field, boolean saveDefault) {
		try {
			String section = getSection(field.getName(), parentSection, field.getAnnotation(MessagesSection.class));

			Object object = field.get(clazz);

			if (object instanceof EasyMessage) {
				EasyMessage message = ((EasyMessage) object);
				if (saveDefault && message.defaultValue != null && !source.getConfig().contains(section)) {
					source.getConfig().set(section, message.defaultValue);
				}
				message.initialize(source, section);
			} else if (object instanceof EasyMessageList) {
				EasyMessageList messageList = ((EasyMessageList) object);

				if (saveDefault && messageList.defaultValue != null && !source.getConfig().contains(section)) {
					source.getConfig().set(section, Arrays.asList(messageList.defaultValue));
				}

				messageList.initialize(source, section);
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	private static String getSection(@NotNull String name,
									 @Nullable String parentSection,
									 @Nullable MessagesSection sectionAnnotation) {
		String parent = parentSection != null ? parentSection + "." : "";
		if (sectionAnnotation != null && sectionAnnotation.value().length() > 0) {
			return parent + sectionAnnotation.value();
		} else {
			return parent + getSectionName(name);
		}
	}

	private static String getSectionName(String codeName) {
		return codeName.toLowerCase().replace("_", "-");
	}

}
