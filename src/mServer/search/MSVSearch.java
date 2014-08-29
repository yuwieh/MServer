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
package mServer.search;

import java.util.ArrayList;
import mServer.daten.MSVSearchTask;
import mServer.tool.MSVDaten;
import mServer.tool.MSVKonstanten;
import mServer.tool.MSVLog;
import mServer.upload.MSVMelden;
import msearch.Search;
import msearch.daten.MSConfig;

public class MSVSearch {

    Search mSearch;

    public MSVSearch() {
        this.mSearch = null;
        MSConfig.dirFilme = MSVDaten.getVerzeichnisFilme();
        MSConfig.diffFilmlisteErstellen = !MSVDaten.system[MSVKonstanten.SYSTEM_EXPORT_FILE_FILMLISTE_DIFF_NR].isEmpty();
    }

    public boolean filmeSuchen(MSVSearchTask aktSearchTask) {
        boolean ret = true;
        MSVMelden.updateServerLoeschen(); // eigene Server-URL aus der Downloadliste löschen
        try {
            // ===========================================
            // den nächsten Suchlauf starten
            MSVLog.systemMeldung("");
            MSVLog.systemMeldung("-----------------------------------");
            MSVLog.systemMeldung("Filmsuche starten");
            mSearch = new Search();
            // was und wie
            MSConfig.senderAllesLaden = aktSearchTask.allesLaden();
            MSConfig.updateFilmliste = aktSearchTask.updateFilmliste();
            MSConfig.nurSenderLaden = arrLesen(aktSearchTask.arr[MSVSearchTask.SUCHEN_SENDER_NR].trim());
            MSConfig.orgFilmlisteErstellen = aktSearchTask.orgListeAnlegen();
            MSConfig.orgFilmliste = MSVDaten.system[MSVKonstanten.SYSTEM_FILMLISTE_ORG_NR];
            // und noch evtl. ein paar Imports von Filmlisten anderer Server
            MSConfig.importUrl__anhaengen = MSVDaten.system[MSVKonstanten.SYSTEM_IMPORT_URL_EXTEND_NR];
            MSConfig.importUrl__ersetzen = MSVDaten.system[MSVKonstanten.SYSTEM_IMPORT_URL_REPLACE_NR];
            // Rest
            MSConfig.setUserAgent(MSVDaten.getUserAgent());
            MSConfig.proxyUrl = MSVDaten.system[MSVKonstanten.SYSTEM_PROXY_URL_NR];
            MSConfig.proxyPort = MSVDaten.getProxyPort();
            MSConfig.debug = MSVDaten.debug;

            Thread t = new Thread(mSearch);
            t.start();
            MSVLog.systemMeldung("Filme suchen gestartet");
            // ===========================================
            // warten auf das Ende
            int warten = aktSearchTask.allesLaden() == true ? MSVKonstanten.WARTEZEIT_ALLES_LADEN : MSVKonstanten.WARTEZEIT_UPDATE_LADEN;
            t.join(warten);
            // ===========================================
            // erst mal schauen ob noch was läuft
            if (t != null) {
                if (t.isAlive()) {
                    MSVLog.fehlerMeldung(915147623, MSVSearch.class.getName(), "Der letzte Suchlauf läuft noch");
                    if (mSearch != null) {
                        MSVLog.systemMeldung("und wird jetzt gestoppt");
                        MSConfig.setStop();
                    }
                    t.join(5 * 60 * 1000); // 5 Minuten warten, das Erstellen/Komprimieren der Liste dauert
                    if (t.isAlive()) {
                        MSVLog.systemMeldung("und noch gekillt");
                        ret = false;
                    }
                    // jetzt ist Schicht im Schacht
                    t.stop();
                }
            }
        } catch (Exception ex) {
            MSVLog.fehlerMeldung(636987308, MSVSearch.class.getName(), "filmeSuchen", ex);
        }
        MSVLog.systemMeldung("filmeSuchen beendet");
        mSearch = null;
        return ret;
    }

    private String[] arrLesen(String s) {
        ArrayList<String> arr = new ArrayList<>();
        String tmp = "";
        s = s.trim();
        if (s.equals("")) {
            return null;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ',') {
                if (!tmp.equals("")) {
                    arr.add(tmp);
                }
                tmp = "";
            } else {
                tmp += s.charAt(i);
            }
        }
        if (!tmp.equals("")) {
            arr.add(tmp);
        }
        return arr.toArray(new String[]{});
    }
}