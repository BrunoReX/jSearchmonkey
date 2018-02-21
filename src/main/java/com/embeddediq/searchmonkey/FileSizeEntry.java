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

/**
 *
 * @author cottr
 */
public class FileSizeEntry {
    long minSize; // Greater than
    boolean useMinSize;
    long maxSize; // Less than
    boolean useMaxSize;

    static public int getIndex(long val)
    {
        int idx = 0;
        while (val > 1024)
        {
            idx ++;
            val /= 1024;
        }
        return idx;
    }

    @Override
    public String toString()
    {
        String[] SCALAR = new String[]{
            "Bytes",
            "KBytes",
            "MBytes",
            "GBytes",
            "TBytes",
        };
        String lessThan = null;
        if (useMaxSize)
        {
            int idx = getIndex(maxSize);
            lessThan = String.format("%.1f %s", 
                    ((double)maxSize / (double)(2^idx)),
                    SCALAR[idx]);
        }
        String greaterThan = null;
        if (useMinSize)
        {
            int idx = getIndex(minSize);
            greaterThan = String.format("%.1f %s", 
                    ((double)minSize / (double)(2^idx)),
                    SCALAR[idx]);
        }
        if (lessThan != null && greaterThan != null)
        {
            return String.format("Between %s and %s", lessThan, greaterThan);
        }
        else if (lessThan != null)
        {
            return String.format("Less than %s", lessThan);
            
        } else if (greaterThan != null)
        {
            return String.format("Greater than %s", greaterThan);
        }
        return "Don't care";
    }
}
