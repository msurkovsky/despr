/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.cs.sur096.despr.controller;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
* Pomáhá se stanovením času který zbývá do konce zpracování úkolu.
*/
public class TimeLeft {

    private transient LocalizeMessages messages;

    /** Počet cyklů */
    private int cycleCount;
    /** Kolik cyklů již prběhlo.*/
    private int count;
    /** Celkový čas.*/
    private long totalTime;
    /** Průměrný čas.*/
    private long avgTime;
    /** Zbývající čas */
    private long timeLeft;

    /**
    * Iniciuje zbývající čas s celkovým počtem cyklů.
    * @param cycleCount celkový počet cyklů.
    */
    public TimeLeft(int cycleCount) {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);

        this.cycleCount = cycleCount;
        count = 0;
        totalTime = 0;
        avgTime = 0;
    }

    /**
    * Přidá čas jednoho cyklu.
    * @param oneCycleTime doba kterou trvalo zpracování jednoho cyklu.
    */
    public void addTime(long oneCycleTime) {
        count++;
        totalTime += oneCycleTime;
        avgTime = totalTime / count;
    }

    /** 
        * Poskytne informaci o zbývajícím čase.
        * @return zbývající čas.
        */
    public long getTimeLeft() {
        return timeLeft;
    }

    /**
    * Poskytne zbývající čas.
    * @return zbývající čas ve formátu "HH:mm:ss".
    */
    public String getTileLeft() {
        timeLeft = (avgTime * cycleCount) - totalTime;
        Date d = new Date(timeLeft);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String title = messages.getString("title.time_left", "Time left: %s");
        return String.format(title, df.format(d));
    }
}
