package io.github.janvinas.trensminecat;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.sl.API.Variables;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.bukkit.Bukkit.getLogger;

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
        String[] infoLinia = {"", "", ""}; // 0: Complet , 1: Línia text 1 , 2: Línia text 2
        int codiEstacio = 0;
        int via = 0;

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

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
                int maxDest = 31;
                if (destination[0].length() > maxDest) {
                    //destination[1] = destination[0].substring(0, 24);
                    //destination[2] = destination[0].substring(24);

                    // Si l'últim caràcter no és un espai, buscar l'últim espai abans de 24
                    if (destination[0].charAt(maxDest-1) != ' ') {
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
                varNom = varNom.replace("%", "");
                varNom = varNom.replace("<", "");
                linia = varNom.replace(">", "");
                // Assignar marca
                if (linia.contains("TAV")) {
                    marca = "";
                } else if (linia.contains("ALV")) {
                    marca = "";
                } else if (linia.contains("RB") || linia.contains("RV")) {
                    marca = "ROD";
                } else if (linia.contains("S") || linia.contains("R") || linia.contains("LD")) {
                    marca = "FGC";
                } else if (linia.contains("TLL")) {
                    marca = "";
                } else {
                    marca = null;
                }
                // Llegir info línia
                String varInfoLinia = "INFO" + marca + linia;
                infoLinia[0] = Variables.get(varInfoLinia).getDefault();

                int maxInfo = 38;
                if (infoLinia[0].length() > maxInfo) {
                    //infoLinia[1] = infoLinia[0].substring(0, 22);
                    //infoLinia[2] = infoLinia[0].substring(22);

                    // Si l'últim caràcter no és un espai, buscar l'últim espai abans de 22
                    if (infoLinia[0].charAt(maxInfo-1) != ' ') {
                        int pos = infoLinia[0].substring(0, maxInfo).lastIndexOf(' ');
                        infoLinia[1] = infoLinia[0].substring(0, pos);
                        infoLinia[2] = infoLinia[0].substring(pos + 1);
                    } else {
                        infoLinia[1] = infoLinia[0].substring(0, maxInfo);
                        infoLinia[2] = infoLinia[0].substring(maxInfo);
                    }
                } else {
                    infoLinia[1] = infoLinia[0];
                    infoLinia[2] = "";
                }
            }

            else {
                destination[0] = "";
                destination[1] = "";
                destination[2] = "";
                linia = "";
                marca = "";
                infoLinia[0] = "";
                infoLinia[1] = "";
                infoLinia[2] = "";
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
            bi.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 206, 10+yoffset);
            bi.dispose();
            getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);

            // Via
            bi = bufferedImage.createGraphics();
            bi.setFont(fontvia);
            bi.setColor(Color.WHITE);
            if (via < 10) {
                bi.drawString(String.valueOf(via), 14, 26+yoffset);
            } else {
                bi.drawString("" + via, 5, 26+yoffset);
            }
            bi.dispose();
            getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);

            // Si marca és buida, no mostrar res
            if (marca != null) {
                // Logo Marca i Línia
                bi = bufferedImage.createGraphics();

                try {
                    // TAV
                    if (linia.contains("TAV")) {
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
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/" + linia + ".png"), 49+28+3, 30);

                        // Regional i Suburbà
                    } else if (linia.contains("R") || linia.contains("S") ) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/FGC.png"), 49, 30);
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/" + linia + ".png"), 49+28+3, 30);

                        // LD
                    } else if (linia.contains("LD")) {
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/FGC.png"), 49, 30);
                        getLayer(2).draw(Assets.getMapTexture(imgDir + "28px/LD.png"), 49+28+3, 30);
                    }
                } catch (Exception e) {
                    getLogger().info("Error al carregar imatge: valor de linia \"" + linia + "\" incorrecte." + "\n" + e.getMessage());
                }

                // Destinació
                bi = bufferedImage.createGraphics();
                bi.setFont(boldfont);
                bi.setColor(Color.BLACK);
                bi.drawString(destination[1], 50, 61+yoffset);
                bi.drawString(destination[2], 50, 75+yoffset);
                bi.dispose();
                getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);

                // Info Línia
                bi = bufferedImage.createGraphics();
                bi.setFont(plainfont);
                bi.setColor(Color.BLACK);
                bi.drawString(infoLinia[1], 50, 94+yoffset);
                bi.drawString(infoLinia[2], 50, 108+yoffset);
                bi.dispose();
                getLayer(2).draw(MapTexture.fromImage(bufferedImage), 0, 0);
            }


            super.onTick();
        }

    }

}
