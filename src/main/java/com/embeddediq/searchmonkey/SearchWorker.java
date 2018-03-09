/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.embeddediq.searchmonkey;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author cottr
 */
public class SearchWorker extends SwingWorker<SearchSummary, SearchResult> implements FileVisitor<Path> { 
    private final SearchEntry entry;
    private final ContentMatch contentMatch;
    private final FileMatch matcher;
    private final SearchResultsTable table;
    
    public SearchWorker(SearchEntry entry, SearchResultsTable table)
    {
        super();
        this.entry = entry;
        this.contentMatch = new ContentMatch(entry);
        this.matcher = new FileMatch(entry);
        this.table = table;
    }

    private SearchSummary summary;
    
    @Override
    protected SearchSummary doInBackground() {            
        summary = new SearchSummary();
        Path startingDir = entry.lookIn.get(0);
        summary.startTime = System.nanoTime();
        try {
            if (entry.lookInSubFolders) {
                Files.walkFileTree(startingDir, this);
            } else {
                Files.walkFileTree(startingDir, new HashSet<>(), 0, this);
            }
        }
        catch (IOException ioe)
        {
            Logger.getLogger(SearchWorker.class.getName()).log(Level.WARNING, null, ioe);
        }
        summary.endTime = System.nanoTime(); // We are done!
        return summary;
    }
    
    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attrs) throws IOException, FileNotFoundException {
        String mimeType = null;
        summary.totalFiles ++;
        if (matcher.matches(file))
        {
            if (entry.greaterThan > 0 && entry.lessThan > 0)
            {
                if (entry.greaterThan > entry.lessThan) // Inverted search
                {
                    if ((attrs.size() > entry.lessThan) && (attrs.size() < entry.greaterThan)) 
                    {
                        return FileVisitResult.CONTINUE;
                    }
                } else if ((attrs.size() > entry.lessThan) || (attrs.size() < entry.greaterThan)) // Standard search
                    {
                        return FileVisitResult.CONTINUE;
                    }
            }
            else if (((entry.lessThan > 0) && (attrs.size() > entry.lessThan)) ||
                    ((entry.greaterThan > 0) && (attrs.size() < entry.greaterThan)))
                     
            {
                return FileVisitResult.CONTINUE;
            }
 
           if (entry.modifiedAfter != null && entry.modifiedBefore != null)
            {
                if (entry.modifiedAfter.compareTo(entry.modifiedBefore) > 0) // Inverted search
                {
                    if ((attrs.lastModifiedTime().compareTo(entry.modifiedBefore) > 0) &&
                        (attrs.lastModifiedTime().compareTo(entry.modifiedAfter) < 0)) 
                    {
                        return FileVisitResult.CONTINUE;
                    }
                } else if ((attrs.lastModifiedTime().compareTo(entry.modifiedBefore) > 0) || // Standard search
                        (attrs.lastModifiedTime().compareTo(entry.modifiedAfter) < 0))
                    {
                        return FileVisitResult.CONTINUE;
                    }
            }
            else if (((entry.modifiedBefore != null) && (attrs.lastModifiedTime().compareTo(entry.modifiedBefore) > 0)) ||
                     ((entry.modifiedAfter != null) && (attrs.lastModifiedTime().compareTo(entry.modifiedAfter) < 0)))
            {
                return FileVisitResult.CONTINUE;
            }

           if (entry.accessedAfter != null && entry.accessedBefore != null)
            {
                if (entry.accessedAfter.compareTo(entry.accessedBefore) > 0) // Inverted search
                {
                    if ((attrs.lastAccessTime().compareTo(entry.accessedBefore) > 0) &&
                        (attrs.lastAccessTime().compareTo(entry.accessedAfter) < 0)) 
                    {
                        return FileVisitResult.CONTINUE;
                    }
                } else if ((attrs.lastAccessTime().compareTo(entry.accessedBefore) > 0) || // Standard search
                        (attrs.lastAccessTime().compareTo(entry.accessedAfter) < 0))
                    {
                        return FileVisitResult.CONTINUE;
                    }
            }
            else if (((entry.accessedBefore != null) && (attrs.lastAccessTime().compareTo(entry.accessedBefore) > 0)) ||
                     ((entry.accessedAfter != null) && (attrs.lastAccessTime().compareTo(entry.accessedAfter) < 0)))
            {
                return FileVisitResult.CONTINUE;
            }

           if (entry.createdAfter != null && entry.createdBefore != null)
            {
                if (entry.createdAfter.compareTo(entry.createdBefore) > 0) // Inverted search
                {
                    if ((attrs.creationTime().compareTo(entry.createdBefore) > 0) &&
                        (attrs.creationTime().compareTo(entry.createdAfter) < 0)) 
                    {
                        return FileVisitResult.CONTINUE;
                    }
                } else if ((attrs.creationTime().compareTo(entry.createdBefore) > 0) || // Standard search
                        (attrs.creationTime().compareTo(entry.createdAfter) < 0))
                    {
                        return FileVisitResult.CONTINUE;
                    }
            }
            else if (((entry.createdBefore != null) && (attrs.creationTime().compareTo(entry.createdBefore) > 0)) ||
                     ((entry.createdAfter != null) && (attrs.creationTime().compareTo(entry.createdAfter) < 0)))
            {
                return FileVisitResult.CONTINUE;
            }
           
            if (
                    (entry.flags.ignoreSymbolicLinks && attrs.isSymbolicLink()) ||
                    (entry.flags.ignoreHiddenFiles && file.toFile().isHidden())
                )
            {
                return FileVisitResult.CONTINUE;
            }
            
            // If required
            if (entry.mime.isActive) {
                mimeType = Files.probeContentType(file);
                if (mimeType == null || !mimeType.equals(entry.mime.mimeName))
                {
                    return FileVisitResult.CONTINUE; // continue;
                }
            }
            
            long count = -1;
            if (entry.containingText != null)
            {
                count = contentMatch.CheckContent(file);
                if (count > 0)
                {
                    if (summary.minContentMatch < 0 || count < summary.minContentMatch) summary.minContentMatch = count;
                    if (summary.maxContentMatch < 0 || count > summary.maxContentMatch) summary.maxContentMatch = count;
                    summary.totalContentMatch += count;
                }
            }

            if ((entry.containingText == null) || (count > 0))
            {
                // Collect matching files
                // TODO - support long in the SearchResult
                SearchResult result = new SearchResult(file, attrs, count, mimeType);
                publish(result);
                int lastResult = summary.matchFileCount;
                summary.matchFileCount ++;
                firePropertyChange("match", lastResult, summary);
                
                // Update stats for the file
                summary.totalMatchBytes += attrs.size();
                if (summary.minMatchBytes < 0 || attrs.size() < summary.minMatchBytes) summary.minMatchBytes = attrs.size();
                if (summary.maxMatchBytes < 0 || attrs.size() > summary.maxMatchBytes) summary.maxMatchBytes = attrs.size();
                if (summary.firstModified == null || attrs.lastModifiedTime().compareTo(summary.firstModified) < 0) summary.firstModified = attrs.lastModifiedTime();
                if (summary.lastModified == null || attrs.lastModifiedTime().compareTo(summary.lastModified) > 0) summary.lastModified = attrs.lastModifiedTime();
                if (summary.firstAccessed == null || attrs.lastAccessTime().compareTo(summary.firstAccessed) < 0) summary.firstAccessed = attrs.lastAccessTime();
                if (summary.lastAccessed == null || attrs.lastAccessTime().compareTo(summary.lastAccessed) > 0) summary.lastAccessed = attrs.lastAccessTime();
                if (summary.firstCreated == null || attrs.creationTime().compareTo(summary.firstCreated) < 0) summary.firstCreated = attrs.creationTime();
                if (summary.lastCreated == null || attrs.creationTime().compareTo(summary.lastCreated) > 0) summary.lastCreated = attrs.creationTime();

            }
        }
        return FileVisitResult.CONTINUE;
    }
    
    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attrs) throws IOException {
        summary.totalFolders ++;
        // Use exception list to skip entire folders
        if (entry.ignoreFolderSet.contains(dir) || 
                (entry.flags.ignoreHiddenFolders && dir.toFile().isHidden()))
        {
            return FileVisitResult.SKIP_SUBTREE;
        }

        if (this.isCancelled()) return FileVisitResult.TERMINATE; // user has requested an early exit
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
            IOException ioe) throws IOException {
        
        // We expect the occassional failure
        if (ioe != null) {
            summary.skippedFiles ++;
            //Logger.getLogger(Searchmonkey.class.getName()).log(Level.WARNING, null, ioe);;
        }
        return FileVisitResult.CONTINUE;
   }

    @Override
    public FileVisitResult postVisitDirectory(Path t, IOException ioe) throws IOException {
        // We expect the occassional failure
        if (ioe != null) {
            summary.skippedFolders ++;
            //Logger.getLogger(Searchmonkey.class.getName()).log(Level.WARNING, null, ioe);;
        }
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    protected void done() {
        // We are done! :-)
    }

    /**
     *
     * @param results
     */
    @Override
    public void process(List<SearchResult> results)
    {
        table.insertRows(results);
    }

}

