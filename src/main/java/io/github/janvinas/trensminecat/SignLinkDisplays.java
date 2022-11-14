package io.github.janvinas.trensminecat;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.sl.API.Variables;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

/**
 * Those displays contain one or more SignLink variable that are updated every tick.
 */
public class SignLinkDisplays {
    static String imgDir = "img/";

    public static class SignLinkDisplay1 extends MapDisplay {
        String signLinkVariable = "";
        String destination = "";

        @Override
        public void onAttached() {
            signLinkVariable = properties.get("variable", String.class, "");
            destination = properties.get("destination", String.class, "");

            getLayer(1).draw(Assets.getMapTexture(imgDir + "SLDisplay1.png"), 0, 0);
            setUpdateWithoutViewers(false);
            super.onAttached();
        }

        @Override
        public void onTick() {
            getLayer(2).clear();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            BufferedImage variables = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = variables.createGraphics();
            g.setFont(TrensMinecat.minecraftiaJavaFont);
            g.setColor(new Color(143, 255, 53));
            g.drawString(LocalDateTime.now().format(formatter), 24, 67);
            g.drawString("PROPER", 95, 67);
            g.drawString("TREN", 95, 77);
            g.drawString("DIRECCIÓ: " + destination.toUpperCase(), 24, 109);
            g.setFont(TrensMinecat.minecraftiaJavaFont.deriveFont(16F));
            g.drawString(Variables.get(signLinkVariable).getDefault(), 140, 79);
            g.dispose();

            getLayer(2).draw(MapTexture.fromImage(variables), 0, 12); //global offset

            super.onTick();
        }

    }

    public static class SignLinkDisplay2 extends MapDisplay {
        static String imgDir = "img/";
        String signLinkVariable = "";
        String[] destination = {"", "", ""}; // 0: Complet , 1: Línia text 1 , 2: Línia text 2
        String linia = "";
        String marca = "";
        String tipus = "";
        //String[] infoLinia = {"", "", ""}; // 0: Complet , 1: Línia text 1 , 2: Línia text 2
        Vector<String> infoLinia = new Vector<String>();
        int codiEstacio = 0;
        int via = 0;
        int tickCounter = 0;
        int cicleCounter = 0;

        private static Font fontvia;
        private static Font boldfont;
        private static Font plainfont;

        @Override
        public void onAttached() {
            signLinkVariable = properties.get("variable", String.class, "");
            destination[0] = properties.get("destination", String.class, "");

            fontvia = new Font("helvetica", Font.PLAIN, 32);
            boldfont = new Font("Helvetica", Font.BOLD, 15);
            plainfont = new Font("Helvetica", Font.PLAIN, 12);

            getLayer(1).draw(Assets.getMapTexture(imgDir + "SLDisplay2.png"), 0, 0);
            setUpdateWithoutViewers(false);
            super.onAttached();
        }

        @Override
        public void onTick() {
            getLayer(2).clear();

            /*
             * Variable: XFG000V0
             *           01234567
             *  Nom:     XFG000V0N
             *           012345678
             * Destin.:  XFG000V0D
             *           012345678
             */

            // Comprovar que és una estació de la XFG o de TLL
            if ((signLinkVariable.contains("XFG") && signLinkVariable.contains("V")) ||
                    (signLinkVariable.contains("TLL") && signLinkVariable.contains("V"))) {
                // Llegir codi de l'estació
                codiEstacio = Integer.parseInt(signLinkVariable.substring(3, 6));
                // Llegir via
                via = Integer.parseInt(signLinkVariable.substring(6).replace("V", ""));
                // Llegir destinació
                destination[0] = Variables.get(signLinkVariable + "D").getDefault();
                destination[0] = Variables.get(destination[0].replace("%", "")).getDefault();
                int maxDest = 27;
                if (destination[0].length() > maxDest) {
                    //destination[1] = destination[0].substring(0, 24);
                    //destination[2] = destination[0].substring(24);

                    // Si l'últim caràcter no és un espai, buscar l'últim espai abans de maxDest
                    if (destination[0].charAt(maxDest - 1) != ' ') {
                        int pos = destination[0].substring(0, maxDest).lastIndexOf(' ');
                        destination[1] = destination[0].substring(0, pos);
                        destination[2] = destination[0].substring(pos + 1);
                    } else {
                        destination[1] = destination[0].substring(0, maxDest);
                        destination[2] = destination[0].substring(maxDest);
                    }
                } else {
                    destination[1] = destination[0];
                    destination[2] = "";
                }
                // Llegir línia
                String varNom = Variables.get(signLinkVariable + "N").getDefault();
                linia = varNom
                        .replace("%", "")
                        .replace("<", "")
                        .replace(">", "");

                /*String numLiniaS = linia
                        .replace("ESPECIAL", "")
                        .replace("TAV", "")
                        .replace("ALV", "")
                        .replace("RB", "")
                        .replace("RV", "")
                        .replace("S", "")
                        .replace("R", "")
                        .replace("LD", "")
                        .replace("TLL", "")
                        .replace("-", "")
                        .replace("_", "");

                int numLinia = -1;
                try {
                    numLinia = Integer.parseInt(numLiniaS);
                } catch (NumberFormatException e) {
                    // No és un número
                    numLinia = -1;
                }*/

                // Assignar marca i tipus de servei
                if (linia.contains("ESPECIAL")) {
                    marca = "";
                    tipus = "";
                } else if (linia.contains("TAV") ||linia.contains("ALV") ||
                           linia.contains("S") || linia.contains("LD")) {
                    marca = "FGC";
                    tipus = "";
                } else if ((linia.contains("RB")) || (linia.contains("RV"))) {
                    marca = "ROD";
                    tipus = "";
                } else if ((linia.contains("R"))) {
                    marca = "FGC";
                    if (linia.contains("REX")) {
                        tipus = "REX";
                        linia = linia.replace("REX", "").replace("-", "");
                    } else {
                        tipus = "";
                    }
                } else if (linia.contains("TLL")) {
                    marca = "";
                    tipus = "";
                } else {
                    marca = null;
                    tipus = null;
                }

                // Llegir info línia
                String varInfoLinia = "INFO" + marca + linia + tipus;
                infoLinia.clear();
                infoLinia.add(Variables.get(varInfoLinia).getDefault());

                int maxInfo = 38;
                // Si la línia és més llarga que maxInfo, dividir-ne el restant amb la de sota. Repetir fins que la última línia sigui menor que maxInfo
                while (infoLinia.lastElement().length() > maxInfo) {
                    // Buscar l'últim espai abans de maxInfo
                    int pos = infoLinia.lastElement().substring(0, maxInfo).lastIndexOf(' ');
                    infoLinia.add(infoLinia.lastElement().substring(pos + 1));
                    infoLinia.set(infoLinia.size() - 2, infoLinia.get(infoLinia.size() - 2).substring(0, pos));
                }

                if (infoLinia.size() > 2)
                    infoLinia.add("");

            } else {
                destination[0] = "";
                destination[1] = "";
                destination[2] = "";
                linia = "";
                marca = "";
                //infoLinia.set(0, "");
                //infoLinia.set(1, "");
                //infoLinia.set(2, "");
            }

            // Si

            /*
             * Informació a mostrar:
             * - Hora (hh:mm:ss)
             * - Via
             * - Logo Marca (FGC, TAV, ALV, ROD, TLL)
             * - Linia (FGC, ROD, TLL)
             * - Destinació (dues línies)
             * - Info Linia (dues línies)
             */

            BufferedImage bufferedImage = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);

            int yoffset = 12;

            Graphics2D bi = bufferedImage.createGraphics();
            // Hora
            bi.setFont(TrensMinecat.minecraftiaJavaFont);
            bi.setColor(new Color(0, 0, 0));
            bi.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 206, 10 + yoffset);
            bi.dispose();
            getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);

            // Via
            bi = bufferedImage.createGraphics();
            bi.setFont(fontvia);
            bi.setColor(Color.WHITE);
            if (via < 10) {
                bi.drawString(String.valueOf(via), 14, 26 + yoffset);
            } else {
                bi.drawString("" + via, 5, 26 + yoffset);
            }
            bi.dispose();
            getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);

            // Si marca és buida, no mostrar res
            if (marca != null) {
                try {
                    // ESP
                    if (linia.contains("ESPECIAL")) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/info.png"), 49, 30);

                        // TAV
                    } else if (linia.contains("TAV")) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/TAV.png"), 49, 30);

                        // ALV
                    } else if (linia.contains("ALV")) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/ALV.png"), 49, 30);

                        // Tren Lleuger
                    } else if (linia.contains("TLL")) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/TLL.png"), 49, 30);

                        // Rodalies
                    } else if (linia.contains("RB") || linia.contains("RV")) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/ROD.png"), 49, 30);
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/" + linia + ".png"), 49 + 28 + 3, 30);

                        // Regional i Suburbà
                    } else if (linia.contains("R") || linia.contains("S")) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/FGC.png"), 49, 30);
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/" + linia + ".png"), 49 + 28 + 3, 30);

                        // LD
                    } else if (linia.contains("LD")) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/FGC.png"), 49, 30);
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/LD.png"), 49 + 28 + 3, 30);

                    }
                } catch (Exception e) {
                    //getLogger().info("Error al carregar imatge: valor de linia \"" + linia + "\" incorrecte." + "\n" + e.getMessage());
                }

                // Text Regional / Regional Exprés
                bi = bufferedImage.createGraphics();
                bi.setFont(plainfont);
                bi.setColor(Color.BLACK);
                if (tipus.contains("REX")) {
                    bi.drawString("Regional", 49 + (28 * 2 + 3) + 3, 30 + (28 / 2) - 3);
                    bi.drawString("Exprés", 49 + (28 * 2 + 3) + 3, 30 + (28 / 2) + 14 - 3);
                } else if (linia.contains("R")) {
                    bi.drawString("Regional", 49 + (28 * 2 + 3) + 3, 30 + (28 / 2) + 3);
                }
                bi.dispose();
                getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);

                // Destinació
                bi = bufferedImage.createGraphics();
                bi.setFont(boldfont);
                bi.setColor(Color.BLACK);
                bi.drawString(destination[1], 50, 61 + yoffset);
                bi.drawString(destination[2], 50, 75 + yoffset);
                bi.dispose();
                getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);

                // Info Línia
                bi = bufferedImage.createGraphics();
                bi.setFont(plainfont);
                bi.setColor(Color.BLACK);

                int pos1 = 0;
                if (infoLinia.size() > 0)
                    pos1 = cicleCounter % infoLinia.size();
                int pos2 = 0;

                if (infoLinia.size() > 2) {
                    bi.drawString(infoLinia.get(pos1), 50, 94 + yoffset);
                    if (((cicleCounter % infoLinia.size()) + 1) < infoLinia.size()) {
                        pos2 = (cicleCounter % infoLinia.size()) + 1;
                    }
                    if (pos1 != pos2) {
                        bi.drawString(infoLinia.get(pos2), 50, 108 + yoffset);
                    }
                } else if (infoLinia.size() > 1) {
                    bi.drawString(infoLinia.get(0), 50, 94 + yoffset);
                    bi.drawString(infoLinia.get(1), 50, 108 + yoffset);
                } else if (infoLinia.size() > 0) {
                    bi.drawString(infoLinia.get(0), 50, 94 + yoffset);
                }

                bi.dispose();
                getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);
            }

            // Cada cop que tickCounter arriba a 60 (3 segons), incrementa cicleCounter
            tickCounter++;
            if (tickCounter >= 60) {
                tickCounter = 0;
                cicleCounter++;

                // Si cicleCounter arriba a 2520 (MCM de 2, 3, 4, 5, 6, 7, 8, 9 i 10
                if (cicleCounter >= 2520) {
                    cicleCounter = 0;
                }
            }

            super.onTick();
        }

    }

}
