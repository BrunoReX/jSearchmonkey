/*
 * Copyright (C) 2017-18 cottr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.embeddediq.searchmonkey;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.mozilla.universalchardet.UniversalDetector;

// Replace lots of separate handlers with one handler

/**
 *
 * @author cottr
 */
public class ContentMatch {
    final private Pattern regexMatch;

    private final ResourceBundle rb;
    private final SearchEntry entry;
    public ContentMatch(SearchEntry entry)
    {
        rb = ResourceBundle.getBundle("com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault());
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
        
        // The dawn of a new age..
        tika = new Tika();
    }

    Tika tika;
    
    /**
     * Simple file reader with basic matching
     * @param path
     * @return 
    */
    public String GetContent(Path path)
    {
        // Let's not process empty files...
        if (path.toFile().length() == 0) {
            return "";
        }
        
        // Check to see if we're using plugins
        if (!entry.flags.disablePlugins)
        {
            try {
                return tika.parseToString(path.toFile());
            } catch (IOException | TikaException | IllegalArgumentException ex) {
                //Logger.getLogger(ContentMatch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Fallback
        return GetContentText(path);

    }

    public String GetContentText(Path path)
    {
        // TODO - make this timeout configurable
        long startTime = System.nanoTime();
        String lines = "";
        String encoding = TestFile(path);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), encoding))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines += line + "\n";
                if ((entry.FileTimeout > 0) && ((System.nanoTime() - startTime) > entry.FileTimeout)) {
                    lines += rb.getString(RunDialogMessages.SIC.getKey()); // Cut early
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

