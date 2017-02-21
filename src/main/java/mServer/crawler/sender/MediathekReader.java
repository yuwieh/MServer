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
package mServer.crawler.sender;

import mSearch.daten.DatenFilm;
import mSearch.tool.GermanStringSorter;
import mSearch.tool.Log;
import mSearch.tool.MVHttpClient;
import mServer.crawler.CrawlerTool;
import mServer.crawler.FilmeSuchen;
import mServer.crawler.RunSender;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

public class MediathekReader extends Thread {

    protected LinkedListUrl listeThemen;
    protected FilmeSuchen mSearchFilmeSuchen;
    private final String sendername; // ist der Name, den der Mediathekreader hat, der ist eindeutig
    private final int maxThreadLaufen; //4; // Anzahl der Thread die parallel Suchen
    private final int wartenSeiteLaden; //ms, Basiswert zu dem dann der Faktor multipliziert wird, Wartezeit zwischen 2 Websiten beim Absuchen der Sender
    private int threads; // aktuelle Anz. laufender Threads
    private int max; // Anz. zu suchender Themen
    private int progress; // Prograss eben
    private final int startPrio; // es gibt die Werte: 0->startet sofort, 1->später und 2->zuletzt

    public MediathekReader(FilmeSuchen aMSearchFilmeSuchen, String aSendername, int aSenderMaxThread, int aSenderWartenSeiteLaden, int aStartPrio) {
        mSearchFilmeSuchen = aMSearchFilmeSuchen;

        maxThreadLaufen = aSenderMaxThread;
        wartenSeiteLaden = aSenderWartenSeiteLaden;
        startPrio = aStartPrio;
        sendername = aSendername;

        threads = 0;
        max = 0;
        progress = 0;
        listeThemen = new LinkedListUrl();
    }

    public static boolean urlExists(String url) {
        // liefert liefert true, wenn es die URL gibt
        // brauchts, um Filmurls zu prüfen
        if (!url.toLowerCase().startsWith("http")) {
            return false;
        } else {
            Request request = new Request.Builder().url(url).head().build();
            boolean result = false;

            try (Response response = MVHttpClient.getInstance().getHttpClient().newCall(request).execute()) {
                if (response.isSuccessful())
                    result = true;
            } catch (IOException ex) {
                ex.printStackTrace();
                result = false;
            }

            return result;
        }
    }

    static void listeSort(LinkedList<String[]> liste, int stelle) {
        //Stringliste alphabetisch sortieren
        GermanStringSorter sorter = GermanStringSorter.getInstance();
        if (liste != null) {
            String str1;
            String str2;
            for (int i = 1; i < liste.size(); ++i) {
                for (int k = i; k > 0; --k) {
                    str1 = liste.get(k - 1)[stelle];
                    str2 = liste.get(k)[stelle];
                    // if (str1.compareToIgnoreCase(str2) > 0) {
                    if (sorter.compare(str1, str2) > 0) {
                        liste.add(k - 1, liste.remove(k));
                    } else {
                        break;
                    }
                }
            }
        }
    }

    static long extractDuration(String dauer) {
        long dauerInSeconds = 0;
        if (dauer.isEmpty()) {
            return 0;
        }
        try {
            if (dauer.contains("min")) {
                dauer = dauer.replace("min", "").trim();
                dauerInSeconds = Long.parseLong(dauer) * 60;
            } else {
                String[] parts = dauer.split(":");
                long power = 1;
                for (int i = parts.length - 1; i >= 0; i--) {
                    dauerInSeconds += Long.parseLong(parts[i]) * power;
                    power *= 60;
                }
            }
        } catch (Exception ex) {
            return 0;
        }
        return dauerInSeconds;
    }

    static long extractDurationSec(String dauer) {
        long dauerInSeconds;
        if (dauer.isEmpty()) {
            return 0;
        }
        try {
            dauerInSeconds = Long.parseLong(dauer);
        } catch (Exception ex) {
            return 0;
        }
        return dauerInSeconds;
    }

    public String getSendername() {
        return sendername;
    }

    public int getMaxThreadLaufen() {
        return maxThreadLaufen;
    }

    public int getWartenSeiteLaden() {
        return wartenSeiteLaden;
    }

    public int getMax() {
        return max;
    }

//    public long getWaitTime()
//    {
//        return getWartenSeiteLaden();
//    }

    public int getProgress() {
        return progress;
    }

    public int getStartPrio() {
        return startPrio;
    }

    public int getThreads() {
        return threads;
    }

    public boolean checkNameSenderFilmliste(String name) {
        // ist der Name der in der Tabelle Filme angezeigt wird
        return getSendername().equalsIgnoreCase(name);
    }

//    public String getNameSender() {
//        return getSendername();
//    }

//    public void delSenderInAlterListe(String sender) {
//        mSearchFilmeSuchen.listeFilmeAlt.deleteAllFilms(sender);
//    }

    public void clear() {
        //aufräumen
    }

    @Override
    public void run() {
        //alles laden
        try {
            threads = 0;
            addToList();
        } catch (Exception ex) {
            Log.errorLog(397543600, ex, getSendername());
        }
    }

    void addToList() {
        //wird überschrieben, hier werden die Filme gesucht
    }

    void addFilm(DatenFilm film, boolean urlPruefen) {
        // es werden die gefundenen Filme in die Liste einsortiert
        if (urlPruefen) {
            if (mSearchFilmeSuchen.listeFilmeNeu.getFilmByUrl(film.arr[DatenFilm.FILM_URL]) == null) {
                addFilm(film);
            }
        } else {
            addFilm(film);
        }
    }

    void addFilm(DatenFilm film) {
        // es werden die gefundenen Filme in die Liste einsortiert
        if (film.arr[DatenFilm.FILM_GROESSE].isEmpty()) {
            film.arr[DatenFilm.FILM_GROESSE] = mSearchFilmeSuchen.listeFilmeAlt.getFileSizeUrl(film.arr[DatenFilm.FILM_URL]);
        }

//        // zum Testen
//        if (film.isHD()) {
//            return;
//        }

        upgradeUrl(film);

//        if (!film.isHD()) {
//            return;
//        }

        film.setUrlHistory();
        CrawlerTool.setGeo(film);
        if (mSearchFilmeSuchen.listeFilmeNeu.addFilmVomSender(film)) {
            // dann ist er neu
            FilmeSuchen.listeSenderLaufen.inc(film.arr[DatenFilm.FILM_SENDER], RunSender.Count.FILME);
        }
    }

/*    DatenFilm istInFilmListe(String sender, String thema, String titel) {
        return mSearchFilmeSuchen.listeFilmeNeu.istInFilmListe(sender, thema, titel);
    }*/

    boolean istInListe(LinkedList<String[]> liste, String str, int nr) {
//        boolean ret = false;
        Optional<String[]> opt = liste.parallelStream().filter(f -> f[nr].equals(str)).findAny();
/*        for (String[] aListe : liste) {
            if (aListe[nr].equals(str)) {
                ret = true;
                break;
            }
        }*/
        return opt.isPresent();
    }

    boolean istInListe(LinkedList<String> liste, String str) {
        /*boolean ret = false;
        for (String aListe : liste) {
            if (aListe.equals(str)) {
                ret = true;
                break;
            }
        }
        return ret;*/
        Optional<String> opt = liste.parallelStream().filter(f -> f.equals(str)).findAny();
        return opt.isPresent();
    }

    synchronized void meldungStart() {
        // meldet den Start eines Suchlaufs
        max = 0;
        progress = 0;
        Log.sysLog("===============================================================");
        Log.sysLog("Starten[" + ((CrawlerTool.loadLongMax()) ? "alles" : "update") + "] " + getSendername() + ": " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        Log.sysLog("   maxThreadLaufen: " + getMaxThreadLaufen());
        Log.sysLog("   wartenSeiteLaden: " + getWartenSeiteLaden());
        Log.sysLog("");
        RunSender runSender = mSearchFilmeSuchen.melden(getSendername(), getMax(), getProgress(), "" /* text */);
        runSender.maxThreads = getMaxThreadLaufen(); //runSender ist erst jetzt angelegt
        runSender.waitOnLoad = getWartenSeiteLaden();
    }

    synchronized void meldungAddMax(int mmax) {
        max = max + mmax;
        mSearchFilmeSuchen.melden(getSendername(), getMax(), getProgress(), "" /* text */);
    }

    synchronized void meldungAddThread() {
        threads++;
        mSearchFilmeSuchen.melden(getSendername(), getMax(), getProgress(), "" /* text */);
    }

    synchronized void meldungProgress(String text) {
        progress++;
        mSearchFilmeSuchen.melden(getSendername(), getMax(), getProgress(), text);
    }

    synchronized void meldung(String text) {
        mSearchFilmeSuchen.melden(getSendername(), getMax(), getProgress(), text);
    }

    synchronized void meldungThreadUndFertig() {
        // meldet das Ende eines!!! Threads
        // der MediathekReader ist erst fertig wenn alle gestarteten Threads fertig sind!!
        threads--;
        if (getThreads() <= 0) {
            //wird erst ausgeführt wenn alle Threads beendet sind
            mSearchFilmeSuchen.meldenFertig(getSendername());
        } else {
            // läuft noch was
            mSearchFilmeSuchen.melden(getSendername(), getMax(), getProgress(), "" /* text */);
        }
    }

//    String addsUrl(String pfad1, String pfad2)
//    {
//        String ret = "";
//        if (pfad1 != null && pfad2 != null)
//        {
//            if (!pfad1.isEmpty() && !pfad2.isEmpty())
//            {
//                if (pfad1.charAt(pfad1.length() - 1) == '/')
//                {
//                    ret = pfad1.substring(0, pfad1.length() - 1);
//                } else
//                {
//                    ret = pfad1;
//                }
//                if (pfad2.charAt(0) == '/')
//                {
//                    ret += pfad2;
//                } else
//                {
//                    ret += '/' + pfad2;
//                }
//            }
//        }
//        if (ret.isEmpty())
//        {
//            Log.errorLog(469872800, pfad1 + ' ' + pfad2);
//        }
//        return ret;
//    }

    private void upgradeUrl(DatenFilm film) {
        // versuchen HD anhand der URL zu suchen, wo noch nicht vorhanden
        if (film.isHD()) {
            return;
        }

        // http://media.ndr.de/progressive/2016/0817/TV-20160817-1113-2300.hq.mp4
        // http://media.ndr.de/progressive/2016/0817/TV-20160817-1113-2300.hd.mp4 -> HD
        if (film.arr[DatenFilm.FILM_URL].startsWith("http://media.ndr.de") && film.arr[DatenFilm.FILM_URL].endsWith(".hq.mp4")) {
            String from = film.arr[DatenFilm.FILM_URL];
            String to = film.arr[DatenFilm.FILM_URL].replace(".hq.mp4", ".hd.mp4");
            updateHd(from, to, film);
        }

        // http://cdn-storage.br.de/iLCpbHJGNLT6NK9HsLo6s61luK4C_2rc571S/_AJS/_ArG_2bP_71S/583da0ef-3e92-4648-bb22-1b14d739aa91_C.mp4
        // http://cdn-storage.br.de/iLCpbHJGNLT6NK9HsLo6s61luK4C_2rc571S/_AJS/_ArG_2bP_71S/583da0ef-3e92-4648-bb22-1b14d739aa91_X.mp4 -> HD
        if (film.arr[DatenFilm.FILM_URL].startsWith("http://cdn-storage.br.de") && film.arr[DatenFilm.FILM_URL].endsWith("_C.mp4")) {
            String from = film.arr[DatenFilm.FILM_URL];
            String to = film.arr[DatenFilm.FILM_URL].replace("_C.mp4", "_X.mp4");
            updateHd(from, to, film);
        }

        // http://pd-ondemand.swr.de/das-erste/buffet/904278.l.mp4
        // http://pd-ondemand.swr.de/das-erste/buffet/904278.xl.mp4 -> HD
        if (film.arr[DatenFilm.FILM_URL].startsWith("http://pd-ondemand.swr.de") && film.arr[DatenFilm.FILM_URL].endsWith(".l.mp4")) {
            String from = film.arr[DatenFilm.FILM_URL];
            String to = film.arr[DatenFilm.FILM_URL].replace(".l.mp4", ".xl.mp4");
            updateHd(from, to, film);
        }
    }

    private void updateHd(String from, String to, DatenFilm film) {
        if (film.arr[DatenFilm.FILM_URL_HD].isEmpty() && film.arr[DatenFilm.FILM_URL].endsWith(from)) {
            String url_ = film.arr[DatenFilm.FILM_URL].substring(0, film.arr[DatenFilm.FILM_URL].lastIndexOf(from)) + to;
            // zum Testen immer machen!!
            if (urlExists(url_)) {
                CrawlerTool.addUrlHd(film, url_, "");
                //Log.sysLog("upgradeUrl: " + film.arr[DatenFilm.FILM_SENDER]);
            } else {
                Log.errorLog(945120347, "upgradeUrl: " + from);
            }
        }
    }

    class LinkedListUrl extends LinkedList<String[]> {

        private static final long serialVersionUID = 1L;

        // Hilfsklasse die das einfügen/entnehmen bei mehreren Threads unterstützt
        synchronized boolean addUrl(String[] e) {
            // e[0] ist immer die URL
            if (!istInListe(this, e[0], 0)) {
                return add(e);
            }
            return false;
        }

        synchronized String[] getListeThemen() {
            return this.pollFirst();
        }
    }
}
