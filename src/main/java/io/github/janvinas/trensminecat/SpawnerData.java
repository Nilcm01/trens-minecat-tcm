package io.github.janvinas.trensminecat;

import java.time.*;
import java.util.Vector;

public class SpawnerData {

    public static final String SPAWNER_TYPE_EXACTE = "exacte";
    public static final String SPAWNER_TYPE_HORA = "hora";


    // ID of the spawner
    private String id;

    // Coordinates of the spawner
    private int x, y, z;

    // Type of the spawner (exacte or hora)
    private String tipus;

    // Start dates of the spawner, paired with dfinal (aaaa-mm-dd or mm-dd)
    private Vector<LocalDate> dinici;

    // End dates of the spawner, paired with dinici (aaaa-mm-dd or mm-dd)
    private Vector<LocalDate> dfinal;

    // Daily start hour of the spawner (hh:mm)
    private LocalTime hinici;

    // Daily end hour of the spawner (hh:mm)
    private LocalTime hfinal;

    // Times to spawn (hora: mm-ss; exacte: hh-mm-ss)
    private Vector<LocalTime> horari;

    public SpawnerData(String id, int x, int y, int z, String tipus, Vector<String> dinici, Vector<String> dfinal, String hinici, String hfinal, Vector<String> horari) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.tipus = tipus;

        this.dinici = new Vector<>();
        this.dfinal = new Vector<>();
        for (int i = 0; i < dinici.size(); i++) {
            setDataPair(dinici.get(i), dfinal.get(i));
        }

        String[] hi = hinici.split("-");
        this.hinici = LocalTime.of(Integer.parseInt(hi[0]), Integer.parseInt(hi[1]), 0);
        String[] hf = hfinal.split("-");
        this.hfinal = LocalTime.of(Integer.parseInt(hf[0]), Integer.parseInt(hf[1]), 0);

        this.horari = new Vector<>();
        setHorari(horari);
    }

    // Gets a pair of start and end dates and sets them accordingly.
    // If the dates are in the format mm-dd, the year is set to the current year.
    // If the end date is before the start date, the end date is set to the next year.
    // If the dates are in the format aaaa-mm-dd, the year is set to the given year.
    private void setDataPair(String dinici, String dfinal) {
        LocalDate now = LocalDate.now();

        // INICI
        String[] d = dinici.split("-");
        if (d.length == 2) { // If the date is in the format mm-dd, set the year to the current year
            this.dinici.add(LocalDate.of(now.getYear(), Integer.parseInt(d[0]), Integer.parseInt(d[1])));
        } else { // If the date is in the format aaaa-mm-dd, set the year to the given year
            this.dinici.add(LocalDate.parse(dinici));
        }

        // FINAL
        d = dfinal.split("-");
        if (d.length == 2) {  // If the date is in the format mm-dd, set the year to the current year
            this.dfinal.add(LocalDate.of(now.getYear(), Integer.parseInt(d[0]), Integer.parseInt(d[1])));

            // If the end date is before the start date, set the year to the next year
            if (this.dfinal.get(this.dfinal.size() - 1).isBefore(this.dinici.get(this.dinici.size() - 1))) {
                this.dfinal.set(this.dfinal.size() - 1, this.dfinal.get(this.dfinal.size() - 1).plusYears(1));
            }
        } else { // If the date is in the format aaaa-mm-dd, set the year to the given year
            this.dfinal.add(LocalDate.parse(dfinal));
        }
    }

    // Sets the times to spawn (hora: mm-ss; exacte: hh-mm-ss)
    // If the spawner is of type Hora, the times are parsed as 00-mm-ss
    // If the spawner is of type Exacte, the times are parsed as hh-mm-ss
    private void setHorari(Vector<String> horari) {
        for (String h : horari) {
            String[] t = h.split("-");
            if (tipus.equals(SPAWNER_TYPE_HORA)) {
                this.horari.add(LocalTime.of(0, Integer.parseInt(t[0]), Integer.parseInt(t[1])));
            } else if (tipus.equals(SPAWNER_TYPE_EXACTE)) {
                this.horari.add(LocalTime.of(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2])));
            }
        }
    }

    // Check if the spawner is spawning at the given time
    // Always check if between the start and end dates and if the current time is between the start and end hours
    public boolean isSpawning(LocalDateTime now) {
        LocalDate nowDate = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();

        for (int i = 0; i < dinici.size(); i++) {

            // If the current date is between the start and end dates
            if (nowDate.isAfter(dinici.get(i)) && nowDate.isBefore(dfinal.get(i))) {

                // If the current time is between the start and end hours
                if (nowTime.isAfter(hinici) && nowTime.isBefore(hfinal)) {

                    // Spawner type Hora
                    if (tipus.equals(SPAWNER_TYPE_HORA)) {
                        for (LocalTime h : horari) {

                            // Horari is in format 00-mm-ss, so we only check the minutes and seconds
                            if (    nowTime.getMinute() == h.getMinute() &&
                                    nowTime.getSecond() == h.getSecond()) {
                                return true;
                            }
                        }
                    }

                    // Spawner type Exacte
                    else if (tipus.equals(SPAWNER_TYPE_EXACTE)) {
                        for (LocalTime h : horari) {

                            // Horari is in format hh-mm-ss, so we check the whole time
                            if (    nowTime.getHour() == h.getHour() &&
                                    nowTime.getMinute() == h.getMinute() &&
                                    nowTime.getSecond() == h.getSecond()) {
                                return true;
                            }
                        }
                    }
                }
            }

        }

        return false;
    }


    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    // Returns a String with all the data formatted to be shown in console
    public String getFormattedData() {
        StringBuilder data = new StringBuilder("ID: " + id + "\n");
        data.append("Coordinates: ").append(x).append(" ").append(y).append(" ").append(z).append("\n");
        data.append("Type: ").append(tipus).append("\n");
        data.append("Start dates: ");
        for (LocalDate d : dinici) {
            data.append(d.toString()).append(" ");
        }
        data.append("\n");
        data.append("End dates: ");
        for (LocalDate d : dfinal) {
            data.append(d.toString()).append(" ");
        }
        data.append("\n");
        data.append("Start hour: ").append(hinici.toString()).append("\n");
        data.append("End hour: ").append(hfinal.toString()).append("\n");
        data.append("Times to spawn: ");
        for (LocalTime h : horari) {
            data.append(h.toString()).append(" ");
        }
        data.append("\n");
        return data.toString();
    }

}
