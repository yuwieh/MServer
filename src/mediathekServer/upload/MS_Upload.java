/*
 * MediathekView
 * Copyright (C) 2008 W. Xaver
 * W.Xaver[at]googlemail.com
 * http://zdfmediathk.sourceforge.net/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package mediathekServer.upload;

import java.util.Iterator;
import mediathekServer.daten.MS_DatenSuchen;
import mediathekServer.daten.MS_DatenUpload;
import mediathekServer.tool.MS_Daten;
import mediathekServer.tool.MS_Konstanten;
import mediathekServer.tool.MS_Log;

public class MS_Upload {

    public static final String UPLOAD_ART_FTP = "ftp";
    public static final String UPLOAD_ART_COPY = "copy";

    public void upload(MS_DatenSuchen aktDatenSuchen) {
        String filmDateiName = aktDatenSuchen.getZielDateiName();
        String filmDateiPfad = MS_Daten.getVerzeichnisFilme();
        Iterator<MS_DatenUpload> it = MS_Daten.listeUpload.iterator();
        while (it.hasNext()) {
            MS_DatenUpload datenUpload = it.next();
            if (datenUpload.arr[MS_Konstanten.UPLOAD_ART_NR].equals(UPLOAD_ART_COPY)) {
                // ==============================================================
                // kopieren
                if (MS_UploadCopy.copy(filmDateiPfad, filmDateiName, datenUpload)) {
                    MS_Melden.melden(datenUpload.getUrlFilmliste(filmDateiName), datenUpload.getPrio());
                }
            } else if (datenUpload.arr[MS_Konstanten.UPLOAD_ART_NR].equals(UPLOAD_ART_FTP)) {
                // ==============================================================
                // ftp
                if (new MS_UploadFtp().uploadFtp(datenUpload.arr[MS_Konstanten.UPLOAD_SERVER_NR], datenUpload.arr[MS_Konstanten.UPLOAD_PORT_NR], datenUpload.arr[MS_Konstanten.UPLOAD_USER_NR],
                        datenUpload.arr[MS_Konstanten.UPLOAD_PWD_NR], filmDateiPfad, filmDateiName,
                        datenUpload)) {
                    MS_Melden.melden(datenUpload.getUrlFilmliste(filmDateiName), datenUpload.getPrio());
                }
            }
        }
        MS_Log.systemMeldung("Upload Ok");
    }
}
