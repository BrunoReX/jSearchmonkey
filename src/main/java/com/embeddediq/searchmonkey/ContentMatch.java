/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.embeddediq.searchmonkey;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.mozilla.universalchardet.UniversalDetector;

/**
 *
 * @author cottr
 */
public class ContentMatch {
    final private Pattern regexMatch;
    //public ContentMatch(Pattern regex)
    //{
//        regexMatch = regex;
//    }
    public ContentMatch(SearchEntry entry)
    {
        int flags = 0;
        if (!entry.flags.useContentRegex) flags |= Pattern.LITERAL;
        if (entry.flags.ignoreContentCase) flags |= Pattern.CASE_INSENSITIVE;
        //Pattern regex = Pattern.compile(strItem, flags);
        regexMatch = Pattern.compile(entry.containingText, flags);
    }
    /*
    public ContentMatch(String pattern) throws PatternSyntaxException
    {
        regexMatch = Pattern.compile(pattern);
    }*/

    /**
     * Simple file reader with basic matching
     * @param path
     * @return 
    */
    public int CheckContent(Path path)
    {
        try {
            String contentType = Files.probeContentType(path);
            if (contentType.matches("application/pdf")) // PDF
            {
                return CheckContentPDF(path);
            }
            else if (contentType.matches("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) // DOCX
            {
                return CheckContentDocx(path); // DOCX
            }
            else if (contentType.matches("application/vnd.oasis.opendocument.text")) // ODT
            {
                return CheckContentOdt(path); // ODT
            }
            else
            {
                return CheckContentText(path);
            }
        } catch (IOException ex) {
            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Fail
        return 0;
    }
    
    /**
     * Simple file reader with basic matching
     * @param path
     * @return 
    */
    static public String GetContent(Path path)
    {
        try {
            String contentType = Files.probeContentType(path);
            if (contentType.matches("application/pdf")) // PDF
            {
                return GetContentPDF(path);
            }
            else if (contentType.matches("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) // DOCX
            {
                return GetContentDocx(path); // DOCX
            }
            else if (contentType.matches("application/vnd.oasis.opendocument.text")) // ODT
            {
                return GetContentOdt(path); // ODT
            }
            else
            {
                return GetContentText(path);
            }
        } catch (IOException ex) {
            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Fail
        return "";
    }

    static public String GetContentPDF(Path path)
    {
        try (RandomAccessBufferedFileInputStream fd = new RandomAccessBufferedFileInputStream(path.toFile())){
            PDFParser parser = new PDFParser(fd);
            parser.parse();
            try (COSDocument cosDoc = parser.getDocument()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                PDDocument pdDoc = new PDDocument(cosDoc);
                return pdfStripper.getText(pdDoc);
            }
        } catch (IOException er) {
            // Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, er);
        }
        return "";
    }
    
    static public String GetContentOdt(Path path)
    {
        String output = "";
        // OutputStream out = new StringOutputStream(path.toFile());
        // FileInputStream fin = ;
        try (ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(path.toFile())))) {
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.getName().equals("content.xml")) {
                    if (ze.getSize() == -1) continue; // skip empty files
                    
                    // TODO - Handle very large files (currently this code will not work beyond int size)
                    int sz = (int)ze.getSize();
                    if (sz < 0) sz = 64*1024*1024; // 64 MByte limit
                    byte[] buffer = new byte[sz];
                    int so = 0;
                    int eo = 0;
                    if (zin.read(buffer) != -1)
                    {
                        String tmp = new String(buffer); // Parse this file as text (XML)
                        
                        while (true)
                        {
                            so = tmp.indexOf("<text:p>", eo + 8);
                            if (so == -1) break;
                            eo = tmp.indexOf("</text:p>", so + 8); 
                            if (eo == -1) break;
                        
                            output += tmp.substring(so + 8, eo);
                        }
                    }
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return output;
    }
    
    static public String GetContentDocx(Path path)
    {
        String text = "";
        try (FileInputStream fs = new FileInputStream(path.toFile()))
        {
                XWPFDocument doc = new XWPFDocument(fs);
                XWPFWordExtractor ex = new XWPFWordExtractor(doc);
                text = ex.getText();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text;
    }
                
    static public String GetContentText(Path path)
    {
        String lines = "";
        String encoding = TestFile(path);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), encoding))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines += line + "\n";
            }
        } catch (IOException er) {
            // Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, er);
        }

        return lines;
    }
    
    /**
     * Simple file reader with basic matching
     * @param path
     * @return 
    */
    private int CheckContentPDF(Path path)
    {
        int count = 0;
        String lines = GetContentPDF(path);
        for (String line: lines.split("\n")) {
                count += getMatchCount(line);
        }
        return count;
    }

    /**
     * Simple file reader with basic matching
     * @param path
     * @return 
    */
    private int CheckContentDocx(Path path)
    {
        int count = 0;
        String lines = GetContentDocx(path);
        for (String line: lines.split("\n")) {
                count += getMatchCount(line);
        }
        return count;
    }
    
    /**
     * Simple file reader with basic matching
     * @param path
     * @return 
    */
    private int CheckContentOdt(Path path)
    {
        int count = 0;
        String lines = GetContentOdt(path);
        for (String line: lines.split("\n")) {
                count += getMatchCount(line);
        }
        return count;
    }

    /**
     * Simple file reader with basic matching
     * @param path
     * @return 
    */
    private int CheckContentText(Path path)
    {
        int count = 0;
        String encoding = TestFile(path);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), encoding))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                count += getMatchCount(line);
            }
        } catch (IOException er) {
            // Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, er);
        }
        return count;
    }

    private int getMatchCount(String line)
    {
        Matcher match = regexMatch.matcher(line);
        int result = 0;
        while (match.find())
        {
            result++;
        }
        return result;
    }

    public List<MatchResult> getMatches(String line)
    {
        Matcher match = regexMatch.matcher(line);
        List<MatchResult> results = new ArrayList<>();
        while (match.find())
        {
            results.add(match.toMatchResult());
        }
        return results;
    }

    static private String TestFile(Path path)
    {
        String encoding = null;
        
        byte[] buf = new byte[4096];
        try (java.io.InputStream fis = java.nio.file.Files.newInputStream(path)) // java.nio.file.Paths.get("test.txt"));
        {
            // (1)
            UniversalDetector detector = new UniversalDetector(null);

            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
              detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();

            // (4)
            encoding = detector.getDetectedCharset();

            // (5)
            detector.reset();
        } catch (IOException ex) {
            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (encoding == null) {
            // Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, "Unknown encoding type..");
            encoding = Charset.defaultCharset().name(); // UTF8
        }
        return encoding;
    }
}

