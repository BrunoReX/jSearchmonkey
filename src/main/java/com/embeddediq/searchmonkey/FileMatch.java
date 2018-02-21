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
    
    public FileMatch(SearchEntry entry)
    {
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
        
    @Override
    public boolean matches(Path file)
    {
        Path name = file.getFileName();
        if (name == null) return false;

        return matcher.matches(name);
    }
}
