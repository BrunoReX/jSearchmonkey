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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author cottr
 */
public class SearchSummary {
    public long startTime;
    public long endTime;
    public int matchFileCount;
    public long totalContentMatch;
    public long minContentMatch = -1;
    public long maxContentMatch = -1;
    public int totalFiles; // Number of files checked    
    public int totalFolders; // Number of folders checked
    public int skippedFolders; // Due to IOException
    public List<String> skippedFolderList = new CopyOnWriteArrayList<>(); // List of files skipped
    public int skippedFiles; // Due to IOException
    public List<String> skippedFileList = new CopyOnWriteArrayList<>(); // List of files skipped
    public long totalMatchBytes; // Size of files that matched (in bytes)
    public long minMatchBytes = -1; // Size of files that matched (in bytes)
    public long maxMatchBytes = -1; // Size of files that matched (in bytes)
    public FileTime firstModified;
    public FileTime lastModified;
    public FileTime firstCreated;
    public FileTime lastCreated;
    public FileTime firstAccessed;
    public FileTime lastAccessed;
}
