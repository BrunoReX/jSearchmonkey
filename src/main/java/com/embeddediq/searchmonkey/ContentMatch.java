/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.embeddediq.searchmonkey;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessRead;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
/**
 *
 * @author cottr
 */
public class ContentMatch {
    final private Pattern regexMatch;
    public ContentMatch(Pattern regex)
    {
        regexMatch = regex;
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
                return CheckContentText(path); // DOCX
            }
            else if (contentType.matches("application/vnd.oasis.opendocument.text")) // ODT
            {
                return CheckContentText(path); // ODT
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
    private int CheckContentPDF(Path path)
    {
        int count = 0;
        try{
            PDFParser parser = new PDFParser((RandomAccessRead)new FileInputStream(path.toFile()));
            parser.parse();
            COSDocument cosDoc = parser.getDocument();
            PDFTextStripper pdfStripper = new PDFTextStripper();
            PDDocument pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(5);
            pdfStripper.getParagraphEnd();
            String parsedText = pdfStripper.getText(pdDoc);
            String[] lines = parsedText.split("\n");
            for (String line: lines)
            {
                count += getMatchCount(line);
            }
        } catch (IOException er) {
            // Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, er);
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
        try{
            FileReader fileReader = new FileReader(path.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
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
}

