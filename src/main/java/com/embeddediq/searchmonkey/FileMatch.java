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

/**
 *
 * @author cottr
 */
public class FileMatch implements PathMatcher {

    private final PathMatcher matcher;
    private final SearchEntry entry;
    
    public FileMatch(SearchEntry entry)
    {
        this.entry = entry;
        String prefix = (entry.flags.useFilenameRegex ? SearchEntry.PREFIX_REGEX : SearchEntry.PREFIX_GLOB);
        String search = entry.fileNameText;
        if (!entry.flags.strictFilenameChecks)
        {
            if (entry.flags.useFilenameRegex)
            {
                search = "(.*?)" + search + "(.*?)";
            } else { // Glob search
                if (!search.startsWith("*"))
                {
                    search = "*" + search;
                }
                if (!search.endsWith("*"))
                {
                    search += "*";
                }
            }
        }
        matcher = FileSystems.getDefault().getPathMatcher(prefix + search);
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

        return matcher.matches(name);
    }
}
