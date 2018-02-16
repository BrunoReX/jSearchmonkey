/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.embeddediq.searchmonkey;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.nanoTime;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xdgf.extractor.XDGFVisioExtractor;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.mozilla.universalchardet.UniversalDetector;

/**
 *
 * @author cottr
 */
public class ContentMatch {
    final private Pattern regexMatch;

    private final SearchEntry entry;
    public ContentMatch(SearchEntry entry)
    {
        this.entry = entry;
        
        if (entry.containingText != null)
        {
            int flags = 0;
            if (!entry.flags.useContentRegex) flags |= Pattern.LITERAL;
            if (entry.flags.ignoreContentCase) flags |= Pattern.CASE_INSENSITIVE;
            regexMatch = Pattern.compile(entry.containingText, flags);
        } else {
            regexMatch = null;
        }
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
//    public int CheckContent(Path path)
//    {
//        if (entry.flags.disablePlugins) return CheckContentText(path);
//        
//        try {
//            String contentType = Files.probeContentType(path);
//            if (contentType.matches("application/pdf")) // PDF
//            {
//                return CheckContentPDF(path);
//            }
//            else if (contentType.matches("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) // DOCX
//            {
//                return CheckContentDocx(path); // DOCX
//            }
//            else if (contentType.matches("application/vnd.oasis.opendocument.text")) // ODT
//            {
//                return CheckContentOdt(path); // ODT
//            }
//            else
//            {
//                return CheckContentText(path);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        // Fail
//        return 0;
//    }
    
    /**
     * Simple file reader with basic matching
     * @param path
     * @return 
    */
    public String GetContent(Path path)
    {
        if (entry.flags.disablePlugins) return GetContentText(path);

        try {
            String contentType = Files.probeContentType(path);
            if (contentType != null)
            {
                if (contentType.matches("application/pdf")) // PDF
                {
                    return GetContentPDF(path);
                }
                else if (contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.")) // DOCX
                {
                    return GetContentDocx(path); // DOCX
                }
                else if (contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.")) // XSLX
                {
                    return GetContentExcelx(path); // XLSX
                }
                else if (contentType.startsWith("application/vnd.openxmlformats-officedocument.presentationml.")) // PPTX
                {
                    return GetContentPptx(path); // PPTX
                }
                else if (contentType.startsWith("application/vnd.ms-visio.")) // Visio
                {
                    return GetContentVisiox(path); // VSDX
                }
                else if (contentType.startsWith("application/vnd.ms-") || contentType.startsWith("application/msword")) // DOC, XSL, PPT
                {
                    return GetContentDoc(path); // DOC, XSL, PPT or RTF (note: rtf is in fact a different handler)
                }
                else if (contentType.startsWith("application/vnd.oasis.opendocument")) // ODT, ODS, ODP, etc
                {
                    return GetContentOdt(path); // ODT, ODS, ODP, etc
                }
            }
        } catch (IOException | XmlException | OpenXML4JException | IllegalArgumentException | BadLocationException ex) {
            Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return GetContentText(path);
    }

    public String GetContentPDF(Path path) throws IOException
    {
        try (RandomAccessBufferedFileInputStream fd = new RandomAccessBufferedFileInputStream(path.toFile())){
            PDFParser parser = new PDFParser(fd);
            parser.parse();
            try (COSDocument cosDoc = parser.getDocument()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                PDDocument pdDoc = new PDDocument(cosDoc);
                return pdfStripper.getText(pdDoc);
            }
        }
    }
    
    public String GetContentOdt(Path path) throws IOException
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
        }
        
        return output;
    }
    
    public String GetContentDocx(Path path) throws IOException, XmlException, OpenXML4JException, IllegalArgumentException
    {
        String text = "";
        try (FileInputStream fs = new FileInputStream(path.toFile()))
        {
                XWPFDocument doc = new XWPFDocument(fs);
                XWPFWordExtractor ex = new XWPFWordExtractor(doc);
                // POITextExtractor po_ex = ExtractorFactory.createExtractor(fs);
                text = ex.getText();
        }
        return text;
    }
                
    public String GetContentExcelx(Path path) throws IOException, XmlException, OpenXML4JException, IllegalArgumentException
    {
        String text = "";
        try (FileInputStream fs = new FileInputStream(path.toFile()))
        {
            
                OPCPackage xlsx = OPCPackage.open(fs);
                XSSFExcelExtractor ex = new XSSFExcelExtractor(xlsx);
                // POITextExtractor po_ex = ExtractorFactory.createExtractor(fs);
                text = ex.getText();
        }
        return text;
    }
    public String GetContentPptx(Path path) throws IOException, XmlException, OpenXML4JException, IllegalArgumentException
    {
        String text = "";
        try (FileInputStream fs = new FileInputStream(path.toFile()))
        {
                OPCPackage xlsx = OPCPackage.open(fs);
                XSLFPowerPointExtractor ex = new XSLFPowerPointExtractor(xlsx);
                text = ex.getText();
        }
        return text;
    }
    public String GetContentVisiox(Path path) throws IOException, XmlException, OpenXML4JException, IllegalArgumentException
    {
        String text = "";
        try (FileInputStream fs = new FileInputStream(path.toFile()))
        {
                OPCPackage xlsx = OPCPackage.open(fs);
                XDGFVisioExtractor ex = new XDGFVisioExtractor(xlsx);
                text = ex.getText();
        }
        return text;
    }
    public String GetContentDoc(Path path) throws IOException, XmlException, OpenXML4JException, IllegalArgumentException, BadLocationException
    {
        String text = "";
        try (FileInputStream fs = new FileInputStream(path.toFile()))
        {
            if (path.toString().toLowerCase().endsWith(".rtf"))
            {
                RTFEditorKit rtfParser = new RTFEditorKit();
                Document document = rtfParser.createDefaultDocument();
                rtfParser.read(fs, document, 0);
                text = document.getText(0, document.getLength());
            } else {
                POITextExtractor po_ex = ExtractorFactory.createExtractor(fs);
                text = po_ex.getText();
            }
        }
        return text;
    }

    public String GetContentText(Path path)
    {
        // TODO - make this timeout configurable
        long startTime = nanoTime();
        String lines = "";
        String encoding = TestFile(path);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), encoding))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines += line + "\n";
                if ((entry.FileTimeout > 0) && ((nanoTime() - startTime) > entry.FileTimeout)) {
                    lines += " -- SIC -- \n";
                    break;
                } // Early exit after 5 seconds
            }
        } catch (IOException er) {
            // Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, er);
        }

        return lines;
    }
    
    public long CheckContent(Path path)
    {
        long count = 0;
        String lines = GetContent(path);
        for (String line: lines.split("\n")) {
                count += getMatchCount(line);
                if (entry.maxHits > 0 && count > entry.maxHits) {
                    count = entry.maxHits;
                    break;
                }
        }
        return count;
    }

//    /**
//     * Simple file reader with basic matching
//     * @param path
//     * @return 
//    */
//    private int CheckContentPDF(Path path)
//    {
//        int count = 0;
//        String lines = GetContentPDF(path);
//        for (String line: lines.split("\n")) {
//                count += getMatchCount(line);
//        }
//        return count;
//    }
//
//    /**
//     * Simple file reader with basic matching
//     * @param path
//     * @return 
//    */
//    private int CheckContentDocx(Path path)
//    {
//        int count = 0;
//        String lines = GetContentDocx(path);
//        for (String line: lines.split("\n")) {
//                count += getMatchCount(line);
//        }
//        return count;
//    }
//    
//    /**
//     * Simple file reader with basic matching
//     * @param path
//     * @return 
//    */
//    private int CheckContentOdt(Path path)
//    {
//        int count = 0;
//        String lines = GetContentOdt(path);
//        for (String line: lines.split("\n")) {
//                count += getMatchCount(line);
//        }
//        return count;
//    }
//
//    /**
//     * Simple file reader with basic matching
//     * @param path
//     * @return 
//    */
//    private int CheckContentText(Path path)
//    {
//        int count = 0;
//        String encoding = TestFile(path);
//        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), encoding))) {
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                count += getMatchCount(line);
//            }
//        } catch (IOException er) {
//            // Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, er);
//        }
//        return count;
//    }
    

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

    private String TestFile(Path path)
    {
        if (entry.flags.disableUnicodeDetection) return Charset.defaultCharset().name(); // UTF8

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

