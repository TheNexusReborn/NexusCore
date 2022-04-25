package com.thenexusreborn.nexuscore.util.helper;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * A collection of helper methods for files and paths
 */
public final class FileHelper {
    
    /**
     * Creates a file if it does not exist using Java NIO. This will also create parent directories
     * This catches the IOException and outputs the stacktrace using Exception.printStackTrace(). Do not use this method if you want to handle this exception yourself
     * @param path The file path to create
     */
    public static void createFileIfNotExists(Path path) {
        createDirectoryIfNotExists(path.getParent());
        
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Creates a sub path based on a parent and as many child paths using a varargs parameter
     * @param parent The parent path
     * @param child The child path(s)
     * @return The generated path
     */
    public static Path subPath(Path parent, String... child) {
        return FileSystems.getDefault().getPath(parent.toString(), child);
    }
    
    /**
     * Creates a directory if it does not exist, it also creates parent directories
     * This catches the IOException and outputs the stacktrace using Exception.printStackTrace(). Do not use this method if you want to handle this exception yourself
     * @param path The directory path
     */
    public static void createDirectoryIfNotExists(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Downloads a file from a URL and saves it to the disk and provides the path
     * @param downloadUrl The URL to download from
     * @param downloadDir The directory to download to
     * @param fileName The name for the downloaded file
     * @param userAgent If to use the UserAgent option
     * @return The path of the downloaded file as Java NIO
     */
    public static Path downloadFile(String downloadUrl, Path downloadDir, String fileName, boolean userAgent) {
        try {
            Path tmpFile = FileSystems.getDefault().getPath(downloadDir.toString(), fileName + ".tmp");
            if (Files.exists(tmpFile)) {
                Files.delete(tmpFile);
            }
            Files.createFile(tmpFile);
            URL url = new URL(downloadUrl);
            URLConnection connection = url.openConnection();
            if (userAgent) {
                connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            }
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream()); FileOutputStream out = new FileOutputStream(tmpFile.toFile())) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer, 0, 1024)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            
            Path targetFile = FileSystems.getDefault().getPath(downloadDir.toString(), fileName);
            Files.move(tmpFile, targetFile, REPLACE_EXISTING);
            return targetFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Copies a folder and all sub files to a new destination
     * This catches the IOException and outputs the stacktrace using Exception.printStackTrace(). Do not use this method if you want to handle this exception yourself
     * @param src The source folder
     * @param dest The destination folder
     */
    public static void copyFolder(Path src, Path dest) {
        try {
            Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    Files.createDirectories(dest.resolve(src.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }
        
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.copy(file, dest.resolve(src.relativize(file)), REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Copies a file from one location to another while replacing the existing file
     * This catches the IOException and outputs the stacktrace using Exception.printStackTrace(). Do not use this method if you want to handle this exception yourself
     * @param source The source file
     * @param dest The destination file
     */
    public static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println(source);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * Deletes a directory and all of it's contents within it
     * This catches the IOException and outputs the stacktrace using Exception.printStackTrace(). Do not use this method if you want to handle this exception yourself
     * @param directory The directory in which to delete
     */
    public static void deleteDirectory(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
        
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}