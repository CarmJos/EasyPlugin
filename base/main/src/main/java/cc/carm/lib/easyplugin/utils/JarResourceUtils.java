package cc.carm.lib.easyplugin.utils;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JarResourceUtils {
    public static final char JAR_SEPARATOR = '/';

    public static @Nullable String[] readResource(@Nullable InputStream resourceStream) {
        if (resourceStream == null) return null;
        try (Scanner scanner = new Scanner(resourceStream, "UTF-8")) {
            List<String> contents = new ArrayList<>();
            while (scanner.hasNextLine()) {
                contents.add(scanner.nextLine());
            }
            return contents.toArray(new String[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public static void copyFolderFromJar(String folderName, File destFolder, CopyOption option)
            throws IOException {
        copyFolderFromJar(folderName, destFolder, option, null);
    }

    public static void copyFolderFromJar(String folderName, File destFolder,
                                         CopyOption option, PathTrimmer trimmer) throws IOException {
        if (!destFolder.exists())
            destFolder.mkdirs();

        byte[] buffer = new byte[1024];

        File fullPath;
        String path = JarResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (trimmer != null)
            path = trimmer.trim(path);
        try {
            if (!path.startsWith("file"))
                path = "file://" + path;

            fullPath = new File(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        ZipInputStream zis = new ZipInputStream(new FileInputStream(fullPath));

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (!entry.getName().startsWith(folderName + JAR_SEPARATOR))
                continue;

            String fileName = entry.getName();

            if (fileName.charAt(fileName.length() - 1) == JAR_SEPARATOR) {
                File file = new File(destFolder + File.separator + fileName);
                if (file.isFile()) {
                    file.delete();
                }
                file.mkdirs();
                continue;
            }

            File file = new File(destFolder + File.separator + fileName);
            if (option == CopyOption.COPY_IF_NOT_EXIST && file.exists())
                continue;

            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            if (!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
        }

        zis.closeEntry();
        zis.close();
    }

    public enum CopyOption {
        COPY_IF_NOT_EXIST, REPLACE_IF_EXIST
    }

    @FunctionalInterface
    public interface PathTrimmer {
        String trim(String original);
    }
}