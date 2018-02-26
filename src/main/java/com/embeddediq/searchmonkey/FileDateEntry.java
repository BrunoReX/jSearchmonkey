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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

/**
 *
 * @author cottr
 */
public final class FileDateEntry implements Serializable {
    @SerializedName("before")
    LocalDateTime before; // earlier than
    @SerializedName("useBefore")
    boolean useBefore;
    @SerializedName("after")
    LocalDateTime after; // later than
    @SerializedName("useAfter")
    boolean useAfter;

    public void setAfter(Date date)
    {
        useAfter = (date != null);
        if (useAfter)
        {
            this.after = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
    }
    public void setBefore(Date date)
    {
        useBefore = (date != null);
        if (useBefore) {
            this.before = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
    }
    public Date getAfter()
    {
        return Date.from(after.atZone(ZoneId.systemDefault()).toInstant());
    }
    public Date getBefore()
    {
        return Date.from(before.atZone(ZoneId.systemDefault()).toInstant());
    }
    
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
        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                
        String s_after = null;
        if (useAfter)
        {
            s_after = this.after.format(fmt);
        }
        String s_before = null;
        if (useBefore)
        {
            s_before = this.before.format(fmt);
        }
        if (s_before != null && s_after != null)
        {
            int dval = this.before.compareTo(this.after);
            if (dval > 0)
            {
                return String.format("Between %s and %s", s_after, s_before);
            } 
            else if (dval < 0)
            {
                return String.format("Not between %s and %s (inverse)", s_after, s_before);
            }
            else
            {
                return String.format("Equals %s", s_before);
            }
        }
        else if (s_before != null)
        {
            return String.format("Before %s", s_before);
            
        } else if (s_after != null)
        {
            return String.format("After %s", s_after);
        }
        return "Don't care";
    }
}
