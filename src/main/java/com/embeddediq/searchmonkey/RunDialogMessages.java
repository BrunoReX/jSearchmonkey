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
public enum RunDialogMessages implements MessageKeyProvider {

    SIC("ContentMatch.Sic.String"),
    DONTCARE("FileDateEntry.DontCare.String"),
    BETWEENDATES("FileDateEntry.BetweenDates.String"),
    NOTBETWEENDATES("FileDateEntry.NotBetweenDates.String"),
    EQUALSDATE("FileDateEntry.EqualsDate.String"),
    BEFOREDATE("FileDateEntry.BeforeDate.String"),
    AFTERDATE("FileDateEntry.AfterDate.String"),
    BETWEENSIZES("FileSizeEntry.BetweenSizes.String"),
    NOTBETWEENSIZES("FileSizeEntry.NotBetweenSizes.String"),
    EQUALSSIZE("FileSizeEntry.EqualsSize.String"),
    LESSTHAN("FileSizeEntry.LessThan.String"),
    GREATERTHAN("FileSizeEntry.GreaterThan.String"),
    DONTCARE2("FileSizeEntry.DontCare.String"),
    BYTES("FileSizeEntry.Bytes.String"),
    KBYTES("FileSizeEntry.KBytes.String"),
    MBYTES("FileSizeEntry.MBytes.String"),
    GBYTES("FileSizeEntry.GBytes.String"),
    TBYTES("FileSizeEntry.TBytes.String"),
    BROWSE("SearchEntryPanel.Browse.String"),
    FOLDER("SearchEntryPanel.Folder.String"),
    OTHER("SearchEntryPanel.Other.String"),
    FILETYPE("SearchEntryPanel.FileType.String"),
    FILESIZE("SearchEntryPanel.FileSize.String"),
    CREATED("SearchEntryPanel.CreatedDate.String"),
    MODIFIED("SearchEntryPanel.ModifiedDate.String"),
    ACCESSED("SearchEntryPanel.AccessedDate.String"),
    OK("SearchEntryPanel.Ok.String"),
    CONFIRM_TITLE("SearchEntryPanel.ConfirmTitle.String"),
    CONFIRM_DIALOG("SearchEntryPanel.ConfirmDialog.String"),
    DONTCARE3("FileTypeEntry.DontCare.String");
    

    private RunDialogMessages(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public String getKey() {
        return key;
    }
    
}
