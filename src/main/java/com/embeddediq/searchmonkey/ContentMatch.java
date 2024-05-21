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

import org.apache.tika.Tika;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Replace lots of separate handlers with one handler

/**
 * @author cottr
 */
public class ContentMatch {

    private static final Logger LOGGER = Logger.getLogger( MethodHandles.lookup().lookupClass().getName() );

    final private Pattern regexMatch;

    private final ResourceBundle rb;
    private final SearchEntry entry;
    private final Tika tika;

    public ContentMatch( SearchEntry entry ) {
        rb = ResourceBundle.getBundle( "com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault() );
        this.entry = entry;

        if ( entry.containingText != null ) {
            int flags = 0;
            if ( !entry.flags.useContentRegex )
                flags |= Pattern.LITERAL;
            if ( entry.flags.ignoreContentCase )
                flags |= Pattern.CASE_INSENSITIVE;
            regexMatch = Pattern.compile( entry.containingText, flags );
        } else {
            regexMatch = null;
        }

        // The dawn of a new age..
        tika = new Tika();
    }

    public long checkContent( Path path, long maxHits, Supplier<Boolean> cancelToken ) throws IOException {

        if ( !Files.exists( path ) ) {
            return 0;
        }

        AtomicLong count = new AtomicLong();
        AtomicBoolean maxHitsToken = new AtomicBoolean(false);
        Supplier<Boolean> aggregateCancelToken = () -> maxHitsToken.get() || cancelToken.get();

        forEachLineInFile( path, aggregateCancelToken, line -> {
            long totalHits = count.getAndUpdate( (existing) -> Math.min( maxHits, existing + getLineMatchCount( line ) ) );
            if(totalHits == maxHits){
                maxHitsToken.set( true );
            }
        } );

        return count.get();
    }

    /**
     * Enumerates all the lines in the file in as a stream but only up to 5 seconds of reading. Throw a InterruptedException to exit the loop early.
     */
    public void forEachLineInFileWithTimeout( Path path, Supplier<Boolean> cancelToken, Consumer<String> expression )
            throws IOException, TimeoutException {
        long startTime = System.nanoTime();
        AtomicBoolean timeoutToken = new AtomicBoolean( false );
        AtomicReference<TimeoutException> timeoutException = new AtomicReference<>( null );

        forEachLineInFile( path, () -> timeoutToken.get() || cancelToken.get(), line -> {
            expression.accept( line );

            if ( entry.FileTimeout > 0 && System.nanoTime() - startTime > entry.FileTimeout ) {
                timeoutException.set( new TimeoutException( rb.getString( RunDialogMessages.SIC.getKey() ) ) );
                timeoutToken.set( true );
            } // Early exit after 5 seconds
        } );

        TimeoutException exception = timeoutException.get();

        if ( exception != null ) {
            throw exception;
        }
    }

    /**
     * Enumerates all the lines in the file in as a stream. Throw a InterruptedException to exit the loop early.
     */
    public void forEachLineInFile( Path path, Supplier<Boolean> cancelToken,
            Consumer<String> expression ) throws IOException {

        if ( entry.flags.disablePlugins ) {
            forEachFileInFileWithFileReader( path, cancelToken, expression );
            return;
        }

        // if the user has opted to use 3rd party scanning we'll fallback on default scanning if there's a problem
        try ( BufferedReader bufferedReader = new BufferedReader( tika.parse( path.toFile() ) ) ) {
            forEachLineInFile( bufferedReader, cancelToken, expression );
        } catch ( IOException ex ) {
            /*LOGGER.log(
                Level.WARNING,
                String.format( "Encountered error while reading [%s] with Tika, falling back to default reader.", path ),
                ex
            );*/
            forEachFileInFileWithFileReader( path, cancelToken, expression );
        }
    }

    private void forEachLineInFile( BufferedReader bufferedReader, Supplier<Boolean> cancelToken, Consumer<String> expression )
            throws IOException {
        while ( true ) {

            if ( cancelToken.get() ) {
                break;
            }

            String line = bufferedReader.readLine();

            if ( line == null ) {
                break;
            }

            expression.accept( line );
        }
    }

    private int getLineMatchCount( String line ) {
        Matcher match = regexMatch.matcher( line );
        int result = 0;
        while ( match.find() ) {
            result++;
        }
        return result;
    }

    public List<MatchResult> getMatches( String line ) {
        Matcher match = regexMatch.matcher( line );
        List<MatchResult> results = new ArrayList<>();
        while ( match.find() ) {
            results.add( match.toMatchResult() );
        }
        return results;
    }

    private void forEachFileInFileWithFileReader( Path path, Supplier<Boolean> cancelToken,
            Consumer<String> expression ) {

        try ( FileChannel fis = FileChannel.open( path, StandardOpenOption.READ ) ) {

            Charset charset = null;

            if ( entry.flags.disableUnicodeDetection )
                charset = Charset.defaultCharset();

            StringBuilder sb = new StringBuilder(512);
            ByteBuffer bb = ByteBuffer.allocate( 8192 );

            while ( fis.read( bb ) >= 0 ) {

                bb.flip();

                if ( charset == null ) {
                    charset = getCharset( bb );
                }

                CharBuffer cb = charset.decode( bb );

                while ( cb.remaining() > 0 ) {

                    char current = cb.get();

                    if ( current != '\n' ) {
                        sb.append( current );
                        continue;
                    }

                    if ( cancelToken.get() ) {
                        break;
                    }

                    expression.accept( sb.toString() );
                    sb.setLength( 0 );
                }

                bb.clear();
            }

            if(sb.length() > 0){
                expression.accept(sb.toString());
            }
        } catch ( Exception ex ) {
            LOGGER.log( Level.INFO, "Unable to read file [" + path + "]", ex );
        }
    }

    private Charset getCharset( ByteBuffer buffer ) {
        try {
            byte[] sample = Arrays.copyOf(buffer.array(), 256);

            CharsetDetector detector = new CharsetDetector();
            detector.setText( sample );

            CharsetMatch charset = detector.detect();

            if(charset == null){
                return Charset.defaultCharset();
            }

            return Charset.forName( charset.getName() );
        } catch ( Exception e ) {
            return Charset.defaultCharset();
        }
    }

}

