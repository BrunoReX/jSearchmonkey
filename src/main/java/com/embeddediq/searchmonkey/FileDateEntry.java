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
import java.util.Locale;
import java.util.ResourceBundle;

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

    public FileDateEntry() {
        rb = ResourceBundle.getBundle("com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault());
    }

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
        String xx;
        if (s_before != null && s_after != null)
        {
            int dval = this.before.compareTo(this.after);
            if (dval > 0)
            {
                xx = rb.getString(RunDialogMessages.BETWEENDATES.getKey());
                return String.format(xx, s_after, s_before); // Translate
            } 
            else if (dval < 0)
            {
                xx = rb.getString(RunDialogMessages.NOTBETWEENDATES.getKey());
                return String.format(xx, s_after, s_before); // Translate
            }
            else
            {
                xx = rb.getString(RunDialogMessages.EQUALSDATE.getKey());
                return String.format(xx, s_before); // Translate
            }
        }
        else if (s_before != null)
        {
            xx = rb.getString(RunDialogMessages.BEFOREDATE.getKey());
            return String.format(xx, s_before); // Translate
            
        } else if (s_after != null)
        {
            xx = rb.getString(RunDialogMessages.AFTERDATE.getKey());
            return String.format(xx, s_after); // Translate
        }

        // messages.getString(s_after)
        return rb.getString(RunDialogMessages.DONTCARE.getKey()); // Translate
    }
    
    
    ResourceBundle rb;
}
