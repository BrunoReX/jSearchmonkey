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

import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author cottr
 */
public class AtomicSearchSummary {
    public long startTime;
    public long endTime;
    public AtomicInteger matchFileCount = new AtomicInteger( 0 );
    public AtomicLong totalContentMatch = new AtomicLong( 0L );
    public AtomicLong minContentMatch = new AtomicLong( -1L );
    public AtomicLong maxContentMatch = new AtomicLong( -1L );
    /**
     * Number of files checked
     */
    public AtomicInteger totalFiles = new AtomicInteger( 0 );
    /**
     * Number of folders checked
     */
    public AtomicInteger totalFolders = new AtomicInteger( 0 );
    /**
     * Due to IOException
     */
    public AtomicInteger skippedFolders = new AtomicInteger( 0 );
    /**
     * List of files skipped
     */
    public ConcurrentLinkedQueue<String> skippedFolderList = new ConcurrentLinkedQueue<>();
    /**
     * Due to IOException
     */
    public AtomicInteger skippedFiles = new AtomicInteger( 0 );
    /**
     * List of files skipped
     */
    public ConcurrentLinkedQueue<String> skippedFileList = new ConcurrentLinkedQueue<>();
    /**
     * Size of files that matched (in bytes)
     */
    public AtomicLong totalMatchBytes = new AtomicLong( 0L );
    /**
     * Size of files that matched (in bytes)
     */
    public AtomicLong minMatchBytes = new AtomicLong( -1L );
    /**
     * Size of files that matched (in bytes)
     */
    public AtomicLong maxMatchBytes = new AtomicLong( -1L );
    public AtomicReference<FileTime> firstModified = new AtomicReference<>();
    public AtomicReference<FileTime> lastModified = new AtomicReference<>();
    public AtomicReference<FileTime> firstCreated = new AtomicReference<>();
    public AtomicReference<FileTime> lastCreated = new AtomicReference<>();
    public AtomicReference<FileTime> firstAccessed = new AtomicReference<>();
    public AtomicReference<FileTime> lastAccessed = new AtomicReference<>();
    private final ConcurrentHashMap<String, Boolean> seenPaths = new ConcurrentHashMap<>();

    public void seenPath(String path){
        seenPaths.put( path, true );
    }

    public boolean hasSeenPath(String path){
        return seenPaths.containsKey( path );
    }

    public SearchSummary toSearchSummary() {
        SearchSummary result = new SearchSummary();
        result.startTime = this.startTime;
        result.endTime = this.endTime;
        result.matchFileCount = this.matchFileCount.get();
        result.totalContentMatch = this.totalContentMatch.get();
        result.minContentMatch = this.minContentMatch.get();
        result.maxContentMatch = this.maxContentMatch.get();
        result.totalFiles = this.totalFiles.get();
        result.totalFolders = this.totalFolders.get();
        result.skippedFolders = this.skippedFolders.get();
        result.skippedFolderList = new ArrayList<>( this.skippedFolderList );
        result.skippedFiles = this.skippedFiles.get();
        result.skippedFileList = new ArrayList<>( this.skippedFileList );
        result.totalMatchBytes = this.totalMatchBytes.get();
        result.maxMatchBytes = this.maxMatchBytes.get();
        result.firstModified = this.firstModified.get();
        result.lastModified = this.lastModified.get();
        result.firstCreated = this.firstCreated.get();
        result.lastCreated = this.lastCreated.get();
        result.firstAccessed = this.firstAccessed.get();
        result.lastAccessed = this.lastAccessed.get();
        return result;
    }
}
