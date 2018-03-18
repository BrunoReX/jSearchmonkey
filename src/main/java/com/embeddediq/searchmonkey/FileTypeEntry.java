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
public class FileTypeEntry implements Serializable {
    @SerializedName("isActive")
    boolean isActive;
    @SerializedName("mimeName")
    String mimeName; // Less than

    private final ResourceBundle rb;
    public FileTypeEntry(String name)
    {
        rb = ResourceBundle.getBundle("com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault());
        if (name != null && name.length() > 0)
        {
            mimeName = name;
            isActive = true;
        }
    }
    public FileTypeEntry(Object clone)
    {
        rb = ResourceBundle.getBundle("com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault());
        if (clone != null && clone instanceof FileTypeEntry)
        {
            isActive = ((FileTypeEntry)clone).isActive;
            mimeName = ((FileTypeEntry)clone).mimeName;
        }
    }

    public FileTypeEntry()
    {
        this(null);
    }
    
    @Override
    public String toString()
    {
        if (!isActive) return rb.getString(RunDialogMessages.DONTCARE3.getKey());
        if (mimeName.length() > 30) return mimeName.substring(0, 30) + "...";
        return mimeName;
    }
    

}
