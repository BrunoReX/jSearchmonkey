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

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author cottr
 */
public class FileSizeEntry implements Serializable {
    @SerializedName("minSize")
    long minSize; // Greater than
    @SerializedName("useMinSize")
    boolean useMinSize;
    @SerializedName("maxSize")
    long maxSize; // Less than
    @SerializedName("useMaxSize")
    boolean useMaxSize;

    private final ResourceBundle rb;

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

    public FileSizeEntry() {
        this.rb = ResourceBundle.getBundle("com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault());
    }

    @Override
    public String toString()
    {
        String[] SCALAR = new String[]{
            rb.getString(RunDialogMessages.BYTES.getKey()),
            rb.getString(RunDialogMessages.KBYTES.getKey()),
            rb.getString(RunDialogMessages.MBYTES.getKey()),
            rb.getString(RunDialogMessages.GBYTES.getKey()),
            rb.getString(RunDialogMessages.TBYTES.getKey()),
        };
        String lessThan = null;
        if (useMaxSize)
        {
            int idx = getIndex(maxSize);
            lessThan = String.format("%.1f %s", 
                    ((double)maxSize / Math.pow(1024, idx)),
                    SCALAR[idx]);
        }
        String greaterThan = null;
        String xx;
        if (useMinSize)
        {
            int idx = getIndex(minSize);
            greaterThan = String.format("%.1f %s", 
                    ((double)minSize / Math.pow(1024, idx)),
                    SCALAR[idx]);
        }
        if (lessThan != null && greaterThan != null)
        {
            if (maxSize > minSize ) {
                xx = rb.getString(RunDialogMessages.BETWEENSIZES.getKey());
                return String.format(xx, greaterThan, lessThan);
            } else if (maxSize < minSize ) {
                xx = rb.getString(RunDialogMessages.NOTBETWEENSIZES.getKey());
                return String.format(xx, greaterThan, lessThan);
            } else {
                xx = rb.getString(RunDialogMessages.EQUALSSIZE.getKey());
                return String.format(xx, greaterThan);
            }
        }
        else if (lessThan != null)
        {
            xx = rb.getString(RunDialogMessages.LESSTHAN.getKey());
            return String.format(xx, lessThan);
            
        } else if (greaterThan != null)
        {
            xx = rb.getString(RunDialogMessages.GREATERTHAN.getKey());
            return String.format(xx, greaterThan);
        }
        return rb.getString(RunDialogMessages.DONTCARE.getKey());
    }
}
