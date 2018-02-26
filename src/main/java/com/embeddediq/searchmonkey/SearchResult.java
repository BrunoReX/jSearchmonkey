/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.embeddediq.searchmonkey;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author cottr
 */
public class SearchResult {
    public SearchResult(Path file, BasicFileAttributes attrs, long matchCount, String mimeType)
    {
        fileName = FilenameUtils.getName(file.toString());
        fileExtension = FilenameUtils.getExtension(fileName);
        pathName = FilenameUtils.getFullPath(file.toString());
        fileSize = attrs.size();
        lastModified = attrs.lastModifiedTime();
        creationTime = attrs.creationTime();
        lastAccessTime = attrs.lastAccessTime();
        fileFlags = 0;
        if (attrs.isSymbolicLink()) fileFlags |= SYMBOLIC_LINK;
        try {
            if (Files.isHidden(file)) fileFlags |= HIDDEN_FILE;
            contentType = mimeType != null ? mimeType : Files.probeContentType(file);
        } catch (IOException ex) {
            Logger.getLogger(SearchResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.matchCount = matchCount;
        fileIcon = GetIconIndex();
    }

    public SearchResult(Path file, BasicFileAttributes attrs, String mimeType)
    {
        this(file, attrs, 0, mimeType);
    }
    
    public Object get(int column)
    {
        switch (column)
        {
            case FILENAME:
                return fileName;
            case EXTENSION:
                return fileExtension;
            case FOLDER:
                return pathName;
            case SIZE:
                return fileSize;
            case MODIFIED:
                return lastModified;
            case ACCESSED:
                return lastAccessTime;
            case CREATED:
                return creationTime;
            case FLAGS:
                return fileFlags;
            case COUNT:
                return matchCount;
            case CONTENT_TYPE:
                return contentType;
            case FILE_ICON:
                return fileIcon;
            default:
                break;
        }
        throw new IllegalArgumentException("Column ID out of bounds");
    }

    public String fileName;
    public String fileExtension;
    public String pathName;
    public long fileSize;
    public FileTime lastModified;
    public FileTime lastAccessTime;
    public FileTime creationTime;
    public int fileFlags;
    public long matchCount;
    public String contentType;
    public int fileIcon; // Icon index nr

    // Flags
    public final static int SYMBOLIC_LINK = 0x1;
    public final static int HIDDEN_FILE = 0x2;
    // public final int HIDDEN_FILE = 0x2;

    /**
     *
     */
    public final static String[] COLUMN_NAMES = new String[] {
        "File", // 0
        "Folder", // 1
        "File size", // 2
        "Count", // 3
        "Last modified", // 4
        "Created", // 5
        "Last accessed", // 6
        "Flags", // 7
        "Extension", // 8
        "Content Type", // 9
        "Icon", // 10
    };

    public final static Class[] COLUMN_CLASSES = new Class[]  {
        String.class,
        String.class,
        Long.class,
        Long.class,
        FileTime.class,
        FileTime.class,
        FileTime.class,
        Integer.class,
        String.class,
        String.class,
        Integer.class, // Icon
    };

    public final static Integer[] COLUMN_WIDTH = new Integer[]  {
        200, /* Filename */
        400, /* Folder */
        100, /* File Size */
        50, /* Count */
        100, /* Modified */
        100, /* Created */
        100, /* Accessed */
        50, /* Flags */
        80, /* Extension */
        200, /* Content Type */
        50, /* File icon */
    };

    // Forced enumeration of the column names
    public final static int FILENAME = 0;
    public final static int FOLDER = 1;
    public final static int SIZE = 2;
    public final static int COUNT = 3;
    public final static int MODIFIED = 4;
    public final static int CREATED = 5;
    public final static int ACCESSED = 6;
    public final static int FLAGS = 7;
    public final static int EXTENSION = 8;
    public final static int CONTENT_TYPE = 9;
    public final static int FILE_ICON = 10; // ICON
    
    public Object[] toArray()
    {
        Object[] def = new Object[] {
            this.fileName,
            this.pathName,
            this.fileSize,
            this.matchCount,
            this.lastModified,
            this.creationTime,
            this.lastAccessTime,
            this.fileFlags,
            this.fileExtension,
            this.contentType,
            this.fileIcon,
        };
        return def;
    }
    
    private int GetIconIndex()
    {
        if (contentType == null) return ICON_UNKNOWN;
        
        if (this.contentType.startsWith("application/"))
        {
            if (contentType.equals("application/zip") ||
                    contentType.equals("application/x-7z-compressed"))
                return ICON_ARCHIVE;
            else if (contentType.equals("application/postscript") ||
                    contentType.equals("application/pdf"))
                return ICON_PDF;
            else if (contentType.equals("application/java-archive"))
                return ICON_JAVA;
            else if (contentType.equals("application/xml"))
                return ICON_HTM;
            else if (contentType.equals("application/x-font"))
                return ICON_FONT;
            else if (contentType.equals("application/vnd.oasis.opendocument.database"))
                return ICON_ODB;
            else if (contentType.equals("application/vnd.oasis.opendocument.graphics"))
                return ICON_ODG;
            else if (contentType.equals("application/vnd.oasis.opendocument.presentation"))
                return ICON_ODP;
            else if (contentType.equals("application/vnd.oasis.opendocument.spreadsheet"))
                return ICON_ODS;
            else if (contentType.equals("application/vnd.oasis.opendocument.text"))
                return ICON_ODT;
            else if (contentType.equals("application/vnd.ms-excel") ||
                    contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"))
                return ICON_XLS;
            else if (contentType.equals("application/ms-word") || contentType.equals("application/vnd.ms-word") ||
                    contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml"))
                return ICON_DOC;
            else if (contentType.equals("application/vnd.ms-powerpoint") ||
                    contentType.equals("application/vnd.ms-powerpoint.presentation") ||
                    contentType.startsWith("application/vnd.openxmlformats-officedocument.presentationml"))
                return ICON_PPT;
            else if (contentType.equals("application/x-abiword"))
                return ICON_WORDPROC;
            
        }
        else if (this.contentType.startsWith("audio/"))
        {
            return ICON_AUDIO;            
        }
        else if (this.contentType.startsWith("chemical/"))
        {
            return ICON_TEXT;
        }
        else if (this.contentType.startsWith("image/"))
        {
            return ICON_IMAGE;
        }
        else if (this.contentType.startsWith("text/"))
        {
            if (contentType.equals("text/x-java")) {
                return ICON_JAVA;
            } else if (contentType.equals("text/html") || contentType.equals("text/css")) {
                return ICON_HTM;
            } else if (contentType.equals("text/csv")) {
                return ICON_SPREADSHEET;
            } else if (contentType.startsWith("text/x-c"))
            {
                if (contentType.equals("text/x-chdr")) {
                    return ICON_H;
                }
                return ICON_C;
            }
            return ICON_TEXT;            
        }
        else if (this.contentType.startsWith("video/"))
        {
            return ICON_VIDEO;
        }

        return ICON_UNKNOWN;
    }

    public Icon GetIcon(int lookup)
    {
        return icons[lookup];
    }
    private static int ICON_ARCHIVE = 0;
    private static int ICON_AUDIO = 1;
    private static int ICON_C = 2;
    private static int ICON_DOC = 3;
    private static int ICON_DRAWING = 4;
    private static int ICON_FONT = 5;
    private static int ICON_H = 6;
    private static int ICON_HTM = 7;
    private static int ICON_IMAGE = 8;
    private static int ICON_JAVA = 9;
    private static int ICON_ODB = 10;
    private static int ICON_ODG = 11;
    private static int ICON_ODP = 12;
    private static int ICON_ODS = 13;
    private static int ICON_ODT = 14;
    private static int ICON_PDF = 15;
    private static int ICON_PPT = 16;
    private static int ICON_SPREADSHEET = 17;
    private static int ICON_TEXT = 18;
    private static int ICON_UNKNOWN = 19;
    private static int ICON_VIDEO = 20;
    private static int ICON_WORDPROC = 21;
    private static int ICON_XLS = 22;
    
    private Icon[] icons = new Icon[] {
        getScaledIcon(getClass().getResource("/images/Icons/icon-archive.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-audio.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-c.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-doc.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-drawing.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-font.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-h.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-htm.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-image.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-java.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-odb.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-odg.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-odp.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-ods.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-odt.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-pdf.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-ppt.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-spreadsheet.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-text-generic.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-unknown.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-video.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-word-processor.png"), 12),
        getScaledIcon(getClass().getResource("/images/Icons/icon-xls.png"), 12),
    };
    
    private Icon getScaledIcon(URL srcImg, int height) {
        ImageIcon image = new ImageIcon(srcImg);

        image.setImage(image.getImage());
        //image.setImage(getScaledImage(image.getImage(), height));
        return (Icon)image;
    }
//    private Image getScaledImage(Image srcImg, int height){
//        int w = height * srcImg.getWidth(this) / srcImg.getHeight(this);
//        BufferedImage resizedImg = new BufferedImage(w, height, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = resizedImg.createGraphics();
//
//        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2.drawImage(srcImg, 0, 0, w, height, null);
//        g2.dispose();
//
//        return resizedImg;
//    }
}

