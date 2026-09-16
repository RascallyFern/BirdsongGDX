package io.github.birdsong.audio;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioTools {
    public void convertAllToWav(String path) {
        String lower = path.toLowerCase();

        if (lower.endsWith(".mp3") || lower.endsWith(".wav")) {
            File input = new File(path);
            String basePath = path.substring(0, path.lastIndexOf('.'));

            File temp = new File(basePath + "_temp.wav");
            File output = new File(basePath + ".wav");

            if (temp.exists()) {
                temp.delete();
            }

            File converted = formatToWav(path, temp.getPath());

            if (converted != null && converted.exists()) {
                if (input.delete()) {
                    if (temp.renameTo(output)) {
                        System.out.println("Converted: " + path);
                    } else {
                        System.out.println("Could not rename: " + temp.getPath());
                    }
                } else {
                    System.out.println("Could not delete: " + path);
                    temp.delete();
                }
            }
        } else if (Files.isDirectory(Path.of(path))) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(path))) {
                for (Path file : stream) {
                    convertAllToWav(file.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File formatToWav(String inputPath, String outputPath) {
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-i", inputPath, "-ac", "1", "-ar", "48000", "-c:a", "pcm_s16le", outputPath);

        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process p = pb.start();
            int exitCode = p.waitFor();

            if (exitCode != 0) {
                System.out.println("FFmpeg failed: " + inputPath);
                return null;
            }

            return new File(outputPath);

        } catch (IOException | InterruptedException e) {
            System.out.println("FFmpeg error: " + e);
            return null;
        }
    }
}
