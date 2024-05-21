/*
 * Copyright (C) 2018 cottr
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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author cottr
 */
public class FileMatch implements PathMatcher {

    private final PathMatcher matchPattern;
    private final SearchEntry entry;
    private final List<PathMatcher> excludePatterns;
    
    public FileMatch(SearchEntry entry)
    {
        this.entry = entry;

        matchPattern = buildPattern( entry.flags.useFilenameRegex, entry.flags.strictFilenameChecks, entry.fileNameText );

        excludePatterns = entry
            .ignoreFolderSet
            .stream()
            .map( ( excludePattern ) -> buildPattern( entry.flags.useFilenameRegex, entry.flags.strictFilenameChecks, excludePattern ) )
            .collect( Collectors.toList() );
    }

    private PathMatcher buildPattern( boolean useFilenameRegex, boolean strictFilenameChecks, String pattern ) {
        String prefix = ( useFilenameRegex ? SearchEntry.PREFIX_REGEX : SearchEntry.PREFIX_GLOB );
        pattern = preparePattern( useFilenameRegex, strictFilenameChecks, pattern );
        return FileSystems.getDefault().getPathMatcher( prefix + pattern );
    }

    private String preparePattern( boolean useFilenameRegex, boolean strictFilenameChecks, String search ) {

        if ( strictFilenameChecks ) {
            return search;
        }

        if ( useFilenameRegex ) {
            return "(.*?)" + search + "(.*?)";
        }

        // Glob search
        if ( !search.startsWith( "*" ) ) {
            search = "*" + search;
        }

        if ( !search.endsWith( "*" ) ) {
            search += "*";
        }

        return search;
    }

    // TODO - make this list part of the config parms
    String[] binfile_list = new String[]{
        ".exe", ".o", ".bin", ".dat", ".raw", ".dsk",
        ".bak", ".bk", ".obj", ".db",
        ".dll", ".so", ".a", ".la", // Library files
    };
    
    @Override
    public boolean matches(Path file)
    {
        Path name = file.getFileName();
        if (name == null) return false;
        
        String fname = name.toString().toLowerCase();
        if (entry.flags.ignoreHiddenFiles)
        {
            if (name.startsWith(".")) return false;
        }
        if (entry.flags.skipBinaryFiles)
        {
            for (String binfile: binfile_list)
            {
                if (fname.endsWith(binfile)) return false;
            }
        }
        if (entry.flags.ignoreSymbolicLinks) {
            if (fname.endsWith(".lnk")) return false;
        }

        return matchPattern.matches( name );
    }

    public boolean isExcludedPath( Path path ) {
        return excludePatterns
            .stream()
            .anyMatch( ( pattern ) -> pattern.matches( path ) );
    }
}
