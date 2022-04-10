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

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * @author cottr
 */
public class SearchWorker extends SwingWorker<SearchSummary, SearchResult> {

    private static final Logger LOGGER = Logger.getLogger( MethodHandles.lookup().lookupClass().getName() );

    private final SearchEntry entry;
    private final ContentMatch contentMatch;
    private final FileMatch matcher;
    private final SearchResultsTable table;
    private final ConcurrentLinkedQueue<SearchResult> searchResults = new ConcurrentLinkedQueue<>();
    private AtomicSearchSummary summary;

    public SearchWorker( SearchEntry entry, SearchResultsTable table ) {
        super();
        this.entry = entry;
        this.contentMatch = new ContentMatch( entry );
        this.matcher = new FileMatch( entry );
        this.table = table;
    }

    @Override
    protected SearchSummary doInBackground() {
        summary = new AtomicSearchSummary();

        summary.startTime = System.nanoTime();

        ParallelExecutor uiPool = new ParallelExecutor( 2, 2, 1.0 );
        AtomicBoolean searchToken = new AtomicBoolean( false );

        uiPool.execute( () -> {
            while ( true ) {

                if ( this.isCancelled() ) {
                    uiPool.cancel();
                    break;
                }

                if ( uiPool.isCancelled() ) {
                    break;
                }

                ArrayList<SearchResult> results = new ArrayList<>();

                while ( true ) {
                    SearchResult result = searchResults.poll();
                    if ( result == null ) {
                        break;
                    }
                    results.add( result );
                    if ( results.size() == 1000 ) {
                        break;
                    }
                }

                if(results.size() > 0){
                    table.insertRows( results );
                }

                if ( searchResults.isEmpty() ) {
                    if ( searchToken.get() ) {
                        uiPool.cancel();
                        break;
                    } else {
                        continue;
                    }
                }

                //noinspection BusyWait
                Thread.sleep( 500 );
            }

            return null;
        } );

        uiPool.execute( () -> {
            while ( true ) {

                if ( this.isCancelled() ) {
                    uiPool.cancel();
                    break;
                }

                if ( uiPool.isCancelled() ) {
                    break;
                }

                int lastResult = summary.matchFileCount.get();

                firePropertyChange( "match", lastResult, summary.toSearchSummary() );

                //noinspection BusyWait
                Thread.sleep( 500 );
            }

            return null;
        } );

        ParallelExecutor searchPool = new ParallelExecutor( 4, 64, 2.0 );

        for ( Path startingDir : entry.lookIn ) {

            if ( this.isCancelled() ) {
                searchPool.cancel();
                break;
            }

            searchPool.execute( () -> {
                visitDirectory( startingDir.toFile(), searchPool );
                return null;
            } );
        }

        searchPool.waitAll( 365L, TimeUnit.DAYS );

        searchToken.set( true );

        uiPool.waitAll( 1, TimeUnit.MINUTES );

        summary.endTime = System.nanoTime(); // We are done!

        return summary.toSearchSummary();
    }

    private void visitDirectory( File current, ParallelExecutor pool ) {

        Path currentPath = current.toPath();

        //LOGGER.info( "Visiting [" + currentPath + "]" );

        if ( this.isCancelled() ) {
            pool.cancel();
            //LOGGER.info( "Skipped [" + currentPath + "] because worker cancelled" );
            return;
        }

        if ( pool.isCancelled() ) {
            //LOGGER.info( "Skipped [" + currentPath + "] because pool cancelled" );
            return;
        }

        try {

            if ( summary.hasSeenPath( current.getCanonicalPath() ) ) {
                //LOGGER.info( "Skipped [" + currentPath + "] because seen" );
                return;
            }
            summary.seenPath( current.getCanonicalPath() );

            summary.totalFolders.incrementAndGet();

            // Use exception list to skip entire folders
            if ( matcher.isExcludedPath( currentPath ) ) {
                skipDirectory( current );
                //LOGGER.info( "Skipped [" + currentPath + "] ignored directory" );
                return;
            }

            if ( entry.flags.ignoreHiddenFolders && current.isHidden() ) {
                skipDirectory( current );
                //LOGGER.info( "Skipped [" + currentPath + "] symbolic link" );
                return;
            }

            BasicFileAttributes attrs = Files.readAttributes( currentPath, BasicFileAttributes.class );

            if ( entry.flags.ignoreSymbolicLinks && attrs.isSymbolicLink() ) {
                //LOGGER.info( "Skipped [" + currentPath + "] symbolic link" );
                return;
            }

            try ( DirectoryStream<Path> children = Files.newDirectoryStream( currentPath ) ) {
                for ( Path childPath : children ) {

                    //LOGGER.info( "Visiting [" + childPath + "]" );

                    if ( this.isCancelled() ) {
                        pool.cancel();
                        //LOGGER.info( "Skipped [" + childPath + "] because worker cancelled" );
                        return;
                    }
                    if ( pool.isCancelled() ) {
                        //LOGGER.info( "Skipped [" + childPath + "] because pool cancelled" );
                        return;
                    }

                    File child = childPath.toFile();

                    if ( !child.isDirectory() && !child.isFile() ) {
                        skipFile( child );
                        //LOGGER.info( "Skipped [" + child.getPath() + "] because not a file or directory" );
                        continue;
                    }

                    if ( !child.canRead() ) {
                        skipFile( child );
                        //LOGGER.info( "Skipped [" + child.getPath() + "] because cannot read" );
                        continue;
                    }

                    pool.execute( () -> {

                        if ( this.isCancelled() ) {
                            pool.cancel();
                            return null;
                        }

                        if ( child.isDirectory() ) {
                            visitDirectory( child, pool );
                        } else if ( child.isFile() ) {
                            visitFile( child );
                        }
                        return null;
                    } );
                }
            }

        } catch ( Exception ex ) {
            skipDirectory( current );
            //LOGGER.log( Level.INFO, "Error encountered when processing file [" + current.getPath() + "]", ex );
        }
    }

    private void visitFile( File current ) {
        try {
            matchFile( current );
        } catch ( IOException ex ) {
            skipFile( current );
            //LOGGER.log( Level.INFO, "Error encountered when processing file [" + current.getPath() + "]", ex );
        }
    }

    // Invoke the pattern matching
    // method on each file.
    private void matchFile( File current ) throws IOException {

        if ( summary.hasSeenPath( current.getCanonicalPath() ) ) {
            //LOGGER.info( "Skipped [" + current.getPath() + "] because seen" );
            return;
        }
        summary.seenPath( current.getCanonicalPath() );

        summary.totalFiles.incrementAndGet();

        Path currentPath = current.toPath();

        BasicFileAttributes attrs = Files.readAttributes( currentPath, BasicFileAttributes.class );

        if ( entry.flags.ignoreSymbolicLinks && attrs.isSymbolicLink() ) {
            skipFile( current );
            //LOGGER.info( "Skipped [" + currentPath + "] because symbolic link" );
            return;
        }

        if ( entry.flags.ignoreHiddenFiles && current.isHidden() ) {
            skipFile( current );
            //LOGGER.info( "Skipped [" + currentPath + "] because hidden" );
            return;
        }

        if ( !matcher.matches( currentPath ) ) {
            //LOGGER.info( "Skipped [" + currentPath + "] does not match pattern" );
            return;
        }

        if ( entry.greaterThan > 0 && entry.lessThan > 0 ) {
            if ( entry.greaterThan > entry.lessThan ) // Inverted search
            {
                if ( ( attrs.size() > entry.lessThan ) && ( attrs.size() < entry.greaterThan ) ) {
                    //LOGGER.info( "Skipped [" + currentPath + "] because not correct size" );
                    return;
                }
            } else if ( ( attrs.size() > entry.lessThan ) || ( attrs.size() < entry.greaterThan ) ) // Standard search
            {
                //LOGGER.info( "Skipped [" + currentPath + "] because not correct size" );
                return;
            }
        } else if ( ( ( entry.lessThan > 0 ) && ( attrs.size() > entry.lessThan ) ) || ( ( entry.greaterThan > 0 ) && ( attrs.size()
                < entry.greaterThan ) ) ) {
            //LOGGER.info( "Skipped [" + currentPath + "] because not correct size" );
            return;
        }

        if ( entry.modifiedAfter != null && entry.modifiedBefore != null ) {
            if ( entry.modifiedAfter.compareTo( entry.modifiedBefore ) > 0 ) // Inverted search
            {
                if ( ( attrs.lastModifiedTime().compareTo( entry.modifiedBefore ) > 0 ) && (
                        attrs.lastModifiedTime().compareTo( entry.modifiedAfter ) < 0 ) ) {
                    //LOGGER.info( "Skipped [" + currentPath + "] because not modified in correct time" );
                    return;
                }
            } else if ( ( attrs.lastModifiedTime().compareTo( entry.modifiedBefore ) > 0 ) || // Standard search
                    ( attrs.lastModifiedTime().compareTo( entry.modifiedAfter ) < 0 ) ) {
                //LOGGER.info( "Skipped [" + currentPath + "] because not modified in correct time" );
                return;
            }
        } else if ( ( ( entry.modifiedBefore != null ) && ( attrs.lastModifiedTime().compareTo( entry.modifiedBefore ) > 0 ) ) || (
                ( entry.modifiedAfter != null ) && ( attrs.lastModifiedTime().compareTo( entry.modifiedAfter ) < 0 ) ) ) {
            //LOGGER.info( "Skipped [" + currentPath + "] because not modified in correct time" );
            return;
        }

        if ( entry.accessedAfter != null && entry.accessedBefore != null ) {
            if ( entry.accessedAfter.compareTo( entry.accessedBefore ) > 0 ) // Inverted search
            {
                if ( ( attrs.lastAccessTime().compareTo( entry.accessedBefore ) > 0 ) && (
                        attrs.lastAccessTime().compareTo( entry.accessedAfter ) < 0 ) ) {
                    //LOGGER.info( "Skipped [" + currentPath + "] because not accessed in correct time" );
                    return;
                }
            } else if ( ( attrs.lastAccessTime().compareTo( entry.accessedBefore ) > 0 ) || // Standard search
                    ( attrs.lastAccessTime().compareTo( entry.accessedAfter ) < 0 ) ) {
                //LOGGER.info( "Skipped [" + currentPath + "] because not accessed in correct time" );
                return;
            }
        } else if ( ( ( entry.accessedBefore != null ) && ( attrs.lastAccessTime().compareTo( entry.accessedBefore ) > 0 ) ) || (
                ( entry.accessedAfter != null ) && ( attrs.lastAccessTime().compareTo( entry.accessedAfter ) < 0 ) ) ) {
            //LOGGER.info( "Skipped [" + currentPath + "] because not accessed in correct time" );
            return;
        }

        if ( entry.createdAfter != null && entry.createdBefore != null ) {
            if ( entry.createdAfter.compareTo( entry.createdBefore ) > 0 ) // Inverted search
            {
                if ( ( attrs.creationTime().compareTo( entry.createdBefore ) > 0 ) && (
                        attrs.creationTime().compareTo( entry.createdAfter ) < 0 ) ) {
                    //LOGGER.info( "Skipped [" + currentPath + "] because not created in correct time" );
                    return;
                }
            } else if ( ( attrs.creationTime().compareTo( entry.createdBefore ) > 0 ) || // Standard search
                    ( attrs.creationTime().compareTo( entry.createdAfter ) < 0 ) ) {
                //LOGGER.info( "Skipped [" + currentPath + "] because not created in correct time" );
                return;
            }
        } else if ( ( ( entry.createdBefore != null ) && ( attrs.creationTime().compareTo( entry.createdBefore ) > 0 ) ) || (
                ( entry.createdAfter != null ) && ( attrs.creationTime().compareTo( entry.createdAfter ) < 0 ) ) ) {
            //LOGGER.info( "Skipped [" + currentPath + "] because not created in correct time" );
            return;
        }

        // If required
        String mimeType = null;
        if ( entry.mime.isActive ) {
            mimeType = Files.probeContentType( currentPath );
            if ( mimeType == null || !mimeType.equals( entry.mime.mimeName ) ) {
                //LOGGER.info( "Skipped [" + currentPath + "] because not expected mime type" );
                return;
            }
        }

        long count = -1;
        if ( entry.containingText != null ) {

            count = contentMatch.checkContent( currentPath, entry.maxHits, this::isCancelled );
            if ( count <= 0 ) {
                //LOGGER.info( "Skipped [" + currentPath + "] no content matches" );
                return;
            }

            updateIfLessThan( summary.minContentMatch, count );
            updateIfMoreThan( summary.maxContentMatch, count );

            summary.totalContentMatch.addAndGet( count );
        }

        // Collect matching files
        // TODO - support long in the SearchResult
        SearchResult result = new SearchResult( currentPath, attrs, count, mimeType );
        searchResults.add( result );

        summary.matchFileCount.incrementAndGet();

        // Update stats for the file
        summary.totalMatchBytes.addAndGet( attrs.size() );

        updateIfLessThan( summary.minMatchBytes, attrs.size() );
        updateIfMoreThan( summary.maxMatchBytes, attrs.size() );

        updateIfEarlierThan( summary.firstModified, attrs.lastModifiedTime() );
        updateIfLaterThan( summary.lastModified, attrs.lastModifiedTime() );
        updateIfEarlierThan( summary.firstAccessed, attrs.lastAccessTime() );
        updateIfLaterThan( summary.lastAccessed, attrs.lastAccessTime() );
        updateIfEarlierThan( summary.firstCreated, attrs.creationTime() );
        updateIfLaterThan( summary.lastCreated, attrs.creationTime() );
    }

    private void updateIfMoreThan( AtomicLong toUpdate, Long newValue ) {
        toUpdate.getAndUpdate( ( existing ) -> existing < 0 || newValue > existing ? newValue : existing );
    }

    private void updateIfLessThan( AtomicLong toUpdate, Long newValue ) {
        toUpdate.getAndUpdate( ( existing ) -> existing < 0 || newValue < existing ? newValue : existing );
    }

    private void updateIfEarlierThan( AtomicReference<FileTime> toUpdate, FileTime newTime ) {
        toUpdate.getAndUpdate( ( existing ) -> existing == null || newTime.compareTo( existing ) < 0 ? newTime : existing );
    }

    private void updateIfLaterThan( AtomicReference<FileTime> toUpdate, FileTime newTime ) {
        toUpdate.getAndUpdate( ( existing ) -> existing == null || newTime.compareTo( existing ) > 0 ? newTime : existing );
    }

    private void skipFile( File current ) {
        summary.skippedFiles.incrementAndGet();
        summary.skippedFileList.add( current.getPath() );
    }

    private void skipDirectory( File current ) {
        summary.skippedFolders.incrementAndGet();
        summary.skippedFolderList.add( current.getPath() );
    }

    @Override
    protected void done() {
        // We are done! :-)
    }

    @Override
    public void process( List<SearchResult> results ) {
        // taken care of in a separate thread
    }

}

