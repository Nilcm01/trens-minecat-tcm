package io.github.janvinas.trensminecat;

import cloud.commandframework.types.tuples.Pair;
import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.map.*;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.spawner.SpawnSign;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.github.janvinas.trensminecat.signactions.*;
import io.github.janvinas.trensminecat.trainTracker.TrackedStation;
import io.github.janvinas.trensminecat.trainTracker.TrackedTrain;
import io.github.janvinas.trensminecat.trainTracker.TrainTracker;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.map.MapPalette;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import picocli.CommandLine;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.sl.API.Variables;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CartellCarretera {

    public static class CartellCarretera1 extends MapDisplay {

        static String imgDir = "img/cc/";
        static String img_fletxa = "fletxa";
        static String imgExt = ".png";
        static int FLETXA_MIDA = 46;
        static int MARC_GRUIX = 2;
        static int MARC_OFFSET = MARC_GRUIX + 2;

        static int MAX_TEXT = 10;

        private static Font fontstd;

        private static int FONT_SIZE = 24;

        // Paràmetres: mida , color , fletxes , text
        float ample = 1;
        float alt = 1;
        int fletxes_posicio = 4;
        Vector<Integer> fletxes = new Vector<Integer>();
        Vector<String> text = new Vector<String>();

        // TIPUS:
        // buit     : En blanc
        // kms      : KMs per destinacions
        // destins  : Via a dalt i destinacions a baix
        // sortida  : Indicació de sortida
        String tipus = "nacional";



        // Colors principals
        // autovia, nacional, comarcal, urbana, municipi
        static Color COLOR_FONS_AUTOVIA = new Color(24, 64, 245, 255);
        static Color COLOR_FONS_NACIONAL = new Color(255, 255, 255, 255);
        static Color COLOR_FONS_COMARCAL = new Color(255, 255, 255, 255);
        static Color COLOR_FONS_URBANA = new Color(67, 151, 42, 255);
        static Color COLOR_FONS_MUNICIPI = new Color(128, 128, 128, 255);

        // Colors genèrics
        // groc, lila, blau, vermell, verd, taronja, rosa
        static Color COLOR_FONS_GROC = new Color(255, 204, 0, 255);
        static Color COLOR_FONS_LILA = new Color(171, 83, 241, 255);
        static Color COLOR_FONS_BLAU = new Color(0, 255, 255, 255);
        static Color COLOR_FONS_VERMELL = new Color(190, 42, 28, 255);
        static Color COLOR_FONS_VERD = new Color(154, 255, 47, 255);
        static Color COLOR_FONS_TARONJA = new Color(204, 115, 43, 255);
        static Color COLOR_FONS_ROSA = new Color(255, 104, 156, 255);

        Color color_fons = COLOR_FONS_NACIONAL;


        // Colors principals
        // autovia, nacional, comarcal, urbana, municipi
        static Color COLOR_ID_AUTOVIA = new Color(23, 62, 245, 255);
        static Color COLOR_ID_NACIONAL = new Color(234, 51, 34, 255);
        static Color COLOR_ID_COMARCAL = new Color(239, 134, 49, 255);
        static Color COLOR_ID_URBANA = new Color(67, 151, 42, 255);
        static Color COLOR_ID_MUNICIPI = new Color(128, 128, 128, 255);

        // Colors genèrics
        // groc, lila, blau, vermell, verd, taronja, rosa
        static Color COLOR_ID_GROC = new Color(255, 204, 0, 255);
        static Color COLOR_ID_LILA = new Color(171, 83, 241, 255);
        static Color COLOR_ID_BLAU = new Color(0, 255, 255, 255);
        static Color COLOR_ID_VERMELL = new Color(190, 42, 28, 255);
        static Color COLOR_ID_VERD = new Color(154, 255, 47, 255);
        static Color COLOR_ID_TARONJA = new Color(204, 115, 43, 255);
        static Color COLOR_ID_ROSA = new Color(255, 104, 156, 255);

        Color color_id = COLOR_ID_NACIONAL;


        // Colors principals
        // autovia, nacional, comarcal, urbana, municipi
        static Color COLOR_ID_TEXT_AUTOVIA = new Color(255, 255, 255, 255);
        static Color COLOR_ID_TEXT_NACIONAL = new Color(255, 255, 255, 255);
        static Color COLOR_ID_TEXT_COMARCAL = new Color(0, 0, 0, 255);
        static Color COLOR_ID_TEXT_URBANA = new Color(255, 255, 255, 255);
        static Color COLOR_ID_TEXT_MUNICIPI = new Color(255, 255, 255, 255);
        // Colors genèrics
        // groc, lila, blau, vermell, verd, taronja, rosa
        static Color COLOR_ID_TEXT_GROC =      new Color(0, 0, 0, 255);
        static Color COLOR_ID_TEXT_LILA =      new Color(255, 255, 255, 255);
        static Color COLOR_ID_TEXT_BLAU =      new Color(0, 0, 0, 255);
        static Color COLOR_ID_TEXT_VERMELL =   new Color(255, 255, 255, 255);
        static Color COLOR_ID_TEXT_VERD =      new Color(0, 0, 0, 255);
        static Color COLOR_ID_TEXT_TARONJA =   new Color(0, 0, 0, 255);
        static Color COLOR_ID_TEXT_ROSA =      new Color(255, 255, 255, 255);

        Color color_id_text = COLOR_ID_TEXT_NACIONAL;


        // Colors principals
        // autovia, nacional, comarcal, urbana, municipi
        static Color COLOR_TEXT_AUTOVIA = new Color(255, 255, 255, 255);
        static Color COLOR_TEXT_NACIONAL = new Color(0, 0, 0, 255);
        static Color COLOR_TEXT_COMARCAL = new Color(0, 0, 0, 255);
        static Color COLOR_TEXT_URBANA = new Color(255, 255, 255, 255);
        static Color COLOR_TEXT_MUNICIPI = new Color(0, 0, 0, 255);

        // Colors genèrics
        // groc, lila, blau, vermell, verd, taronja, rosa
        static Color COLOR_TEXT_GROC =      new Color(0, 0, 0, 255);
        static Color COLOR_TEXT_LILA =      new Color(255, 255, 255, 255);
        static Color COLOR_TEXT_BLAU =      new Color(0, 0, 0, 255);
        static Color COLOR_TEXT_VERMELL =   new Color(255, 255, 255, 255);
        static Color COLOR_TEXT_VERD =      new Color(0, 0, 0, 255);
        static Color COLOR_TEXT_TARONJA =   new Color(0, 0, 0, 255);
        static Color COLOR_TEXT_ROSA =      new Color(255, 255, 255, 255);

        Color color_text = COLOR_TEXT_NACIONAL;


        // Colors principals
        // autovia, nacional, comarcal, urbana, municipi
        static String COLOR_ICONES_AUTOVIA = "blanc";
        static String COLOR_ICONES_NACIONAL = "negre";
        static String COLOR_ICONES_COMARCAL = "negre";
        static String COLOR_ICONES_URBANA = "blanc";
        static String COLOR_ICONES_MUNICIPI = "negre";
        // Colors genèrics
        // groc, lila, blau, vermell, verd, taronja, rosa
        static String COLOR_ICONES_GROC =      "negre";
        static String COLOR_ICONES_LILA =      "blanc";
        static String COLOR_ICONES_BLAU =      "negre";
        static String COLOR_ICONES_VERMELL =   "blanc";
        static String COLOR_ICONES_VERD =      "negre";
        static String COLOR_ICONES_TARONJA =   "negre";
        static String COLOR_ICONES_ROSA =      "blanc";

        String color_icones = COLOR_ICONES_NACIONAL;


        @Override
        public void onAttached() {
            fontstd = new Font("transport-heavy", Font.PLAIN, FONT_SIZE);

            for (int i = 0; i < MAX_TEXT; i++) {
                text.add(null);
            }

            setUpdateWithoutViewers(false);
            super.onAttached();
        }

        private void updateProperties() {
            this.ample = properties.get("ample", Float.class, 1.0f);
            this.alt = properties.get("alt", Float.class, 1.0f);
            String color = properties.get("color", String.class, "nacional");
            this.tipus = properties.get("tipus", String.class, "buit");

            switch (color) {
                case "autovia" -> {
                    this.color_fons = COLOR_FONS_AUTOVIA;
                    this.color_id = COLOR_ID_AUTOVIA;
                    this.color_id_text = COLOR_ID_TEXT_AUTOVIA;
                    this.color_text = COLOR_TEXT_AUTOVIA;
                    this.color_icones = COLOR_ICONES_AUTOVIA;
                }

                // case "nacional" -> default

                case "comarcal" -> {
                    this.color_fons = COLOR_FONS_COMARCAL;
                    this.color_id = COLOR_ID_COMARCAL;
                    this.color_id_text = COLOR_ID_TEXT_COMARCAL;
                    this.color_text = COLOR_TEXT_COMARCAL;
                    this.color_icones = COLOR_ICONES_COMARCAL;
                }
                case "urbana" -> {
                    this.color_fons = COLOR_FONS_URBANA;
                    this.color_id = COLOR_ID_URBANA;
                    this.color_id_text = COLOR_ID_TEXT_URBANA;
                    this.color_text = COLOR_TEXT_URBANA;
                    this.color_icones = COLOR_ICONES_URBANA;
                }
                case "municipi" -> {
                    this.color_fons = COLOR_FONS_MUNICIPI;
                    this.color_id = COLOR_ID_MUNICIPI;
                    this.color_id_text = COLOR_ID_TEXT_MUNICIPI;
                    this.color_text = COLOR_TEXT_MUNICIPI;
                    this.color_icones = COLOR_ICONES_MUNICIPI;
                }
                // Colors genèrics
                // groc, lila, blau, vermell, verd, taronja, rosa
                case "groc" -> {
                    this.color_fons = COLOR_FONS_GROC;
                    this.color_id = COLOR_ID_GROC;
                    this.color_id_text = COLOR_ID_TEXT_GROC;
                    this.color_text = COLOR_TEXT_GROC;
                    this.color_icones = COLOR_ICONES_GROC;
                }
                case "lila" -> {
                    this.color_fons = COLOR_FONS_LILA;
                    this.color_id = COLOR_ID_LILA;
                    this.color_id_text = COLOR_ID_TEXT_LILA;
                    this.color_text = COLOR_TEXT_LILA;
                    this.color_icones = COLOR_ICONES_LILA;
                }
                case "blau" -> {
                    this.color_fons = COLOR_FONS_BLAU;
                    this.color_id = COLOR_ID_BLAU;
                    this.color_id_text = COLOR_ID_TEXT_BLAU;
                    this.color_text = COLOR_TEXT_BLAU;
                    this.color_icones = COLOR_ICONES_BLAU;
                }
                case "vermell" -> {
                    this.color_fons = COLOR_FONS_VERMELL;
                    this.color_id = COLOR_ID_VERMELL;
                    this.color_id_text = COLOR_ID_TEXT_VERMELL;
                    this.color_text = COLOR_TEXT_VERMELL;
                    this.color_icones = COLOR_ICONES_VERMELL;
                }
                case "verd" -> {
                    this.color_fons = COLOR_FONS_VERD;
                    this.color_id = COLOR_ID_VERD;
                    this.color_id_text = COLOR_ID_TEXT_VERD;
                    this.color_text = COLOR_TEXT_VERD;
                    this.color_icones = COLOR_ICONES_VERD;
                }
                case "taronja" -> {
                    this.color_fons = COLOR_FONS_TARONJA;
                    this.color_id = COLOR_ID_TARONJA;
                    this.color_id_text = COLOR_ID_TEXT_TARONJA;
                    this.color_text = COLOR_TEXT_TARONJA;
                    this.color_icones = COLOR_ICONES_TARONJA;
                }
                case "rosa" -> {
                    this.color_fons = COLOR_FONS_ROSA;
                    this.color_id = COLOR_ID_ROSA;
                    this.color_id_text = COLOR_ID_TEXT_ROSA;
                    this.color_text = COLOR_TEXT_ROSA;
                    this.color_icones = COLOR_ICONES_ROSA;
                }
                default -> {
                    this.color_fons = COLOR_FONS_NACIONAL;
                    this.color_id = COLOR_ID_NACIONAL;
                    this.color_id_text = COLOR_ID_TEXT_NACIONAL;
                    this.color_text = COLOR_TEXT_NACIONAL;
                    this.color_icones = COLOR_ICONES_NACIONAL;
                }
            }

            // TEXT: text_#
            // Parsing the passed text
            for (int i = 0; i < MAX_TEXT; i++) {
                String l = properties.get("text_" + (i+1), String.class, null);
                if (l != null) {
                    if (l.contains("_")) {
                        text.set(i, null);
                        properties.set("text_" + (i+1), null);
                    } else {
                        text.set(i, l);
                    }
                }
            }

            // FLETXES
            // fletxes_ubicació : 0-7, 1/8 en sentit horari
            // fletxes_num
            // fletxes_orientació : num_num_... 0-7, 1/8 en sentit horari

            fletxes_posicio = properties.get("fletxes_ubicacio", int.class, 0);
            if (fletxes_posicio == 0) properties.set("fletxes_ubicacio", 0);

            int num_fletxes = properties.get("fletxes_num", int.class, 0);
            if (num_fletxes == 0) properties.set("fletxes_num", 0);

            fletxes.clear();
            for (int i = 0; i < num_fletxes; i++) {
                int ori = Integer.parseInt(
                        properties
                                .get("fletxes_orientacio", String.class, null)
                                .split("_")[i]
                );
                if (ori < 0 || ori > 7) {
                    properties.set("fletxes_orientacio", null);
                    return;
                }
                fletxes.add(ori);
            }
        }

        private int blocksToPixels(float blocks) {
            return (int) (blocks * 128);
        }

        @Override
        public void onTick() {

            updateProperties();

            // TIPUS:
            // buit     : En blanc
            // kms      : KMs per destinacions
            // sortida  : Indicació de sortida
            switch (tipus) {
                case "buit" -> renderBuit();
                case "kms" -> renderKms();
                case "sortida" -> renderSortida();
                default -> renderGeneric();
            }



            super.onTick();
        }

        private void renderGeneric() {
            // Fons
            getLayer(1).clear();
            getLayer(1).fill(MapColorPalette.getColor(color_fons));

            // Marc
            getLayer(2).clear();
            getLayer(2).drawRectangle(0, 0, blocksToPixels(ample), blocksToPixels(alt), MapColorPalette.getColor(color_text));
            getLayer(2).drawRectangle(1, 1, blocksToPixels(ample) - 2, blocksToPixels(alt) - 2, MapColorPalette.getColor(color_text));
        }

        private void renderBuit() {
            BufferedImage bufferedImage =
                    new BufferedImage(blocksToPixels(ample), blocksToPixels(alt), BufferedImage.TYPE_INT_ARGB);

            renderGeneric();

            // Text
            getLayer(3).clear();
            Graphics2D bi = bufferedImage.createGraphics();
            bi.setFont(fontstd);
            bi.setColor(color_text);

            int offset_fletxa = 0;
            // Check fletxa position
            // If it is 2 (E) or 6 (W), then apply an offset to the text to avoid the fletxa
            if (fletxes_posicio == 2) { // E -> offset to the left
                offset_fletxa = -FLETXA_MIDA - MARC_OFFSET;
            } else if (fletxes_posicio == 6) { // W -> offset to the right
                offset_fletxa = FLETXA_MIDA + MARC_OFFSET;
            }

            for (int i = 0; i < MAX_TEXT; i++) {
                if (text.get(i) != null) {
                    int width = bi.getFontMetrics().stringWidth(text.get(i));
                    int height = bi.getFontMetrics().getHeight();

                    renderMultipleViaIdLinia(text.get(i),
                            (blocksToPixels(ample) - width) / 2 + offset_fletxa / 2,
                            (height * i) + 36);
                }
            }
            bi.dispose();
            getLayer(3).draw(MapTexture.fromImage(bufferedImage), 0, 0);

            renderFletxes();
        }

        private void renderKms() {
            BufferedImage bufferedImage =
                    new BufferedImage(blocksToPixels(ample), blocksToPixels(alt), BufferedImage.TYPE_INT_ARGB);

            renderGeneric();

            // Text
            getLayer(3).clear();
            Graphics2D bi = bufferedImage.createGraphics();
            bi.setFont(fontstd);
            bi.setColor(color_text);

            // KMs per destinacions
            // Format a mostrar:
            // | Text destinació      -- km |
            //
            // Pos parell: esquerra
            // Pos senar: dreta
            for (int i = 0; i < MAX_TEXT/2; i++) {

                int esq = i * 2;
                int dreta = esq + 1;

                // ESQUERRA
                if (text.get(esq) != null) {
                    int height = bi.getFontMetrics().getHeight();

                    renderMultipleViaIdLinia(text.get(esq),
                            MARC_OFFSET,
                            (height * i) + 24);
                }
                // DRETA
                if (text.get(dreta) != null) {
                    int width = bi.getFontMetrics().stringWidth(text.get(dreta));
                    int height = bi.getFontMetrics().getHeight();

                    renderMultipleViaIdLinia(text.get(dreta),
                            blocksToPixels(ample) - width - MARC_OFFSET,
                            (height * i) + 24);
                }
            }

            bi.dispose();
            getLayer(3).draw(MapTexture.fromImage(bufferedImage), 0, 0);
        }


        private void renderSortida() {
            BufferedImage bufferedImage =
                    new BufferedImage(blocksToPixels(ample), blocksToPixels(alt), BufferedImage.TYPE_INT_ARGB);

            renderGeneric();

            // Text
            getLayer(3).clear();
            Graphics2D bi = bufferedImage.createGraphics();
            bi.setFont(fontstd);
            bi.setColor(color_text);

            int offset_fletxa = 0;
            // Check fletxa position
            // If it is 2 (E) or 6 (W), then apply an offset to the text to avoid the fletxa
            if (fletxes_posicio == 2) { // E -> offset to the left
                offset_fletxa = -FLETXA_MIDA - MARC_OFFSET;
            } else if (fletxes_posicio == 6) { // W -> offset to the right
                offset_fletxa = FLETXA_MIDA + MARC_OFFSET;
            }

            if (text.get(0) != null) {
                int width = bi.getFontMetrics().stringWidth(text.get(0));
                int height = bi.getFontMetrics().getHeight();

                renderMultipleViaIdLinia(text.get(0),
                        (blocksToPixels(ample) - width) / 2,
                        30);
            }

            int yLines = bi.getFontMetrics().getHeight() + 11;
            getLayer(2).drawLine(0, yLines, blocksToPixels(ample), yLines, MapColorPalette.getColor(color_text));
            getLayer(2).drawLine(0, yLines + 1, blocksToPixels(ample), yLines + 1, MapColorPalette.getColor(color_text));

            for (int i = 1; i < MAX_TEXT; i++) {
                if (text.get(i) != null) {
                    int width = bi.getFontMetrics().stringWidth(text.get(i));
                    int height = bi.getFontMetrics().getHeight();

                    renderMultipleViaIdLinia(text.get(i),
                            (blocksToPixels(ample) - width) / 2 + offset_fletxa / 2,
                            (height * (i)) + 36 + 5);
                }
            }
            bi.dispose();
            getLayer(3).draw(MapTexture.fromImage(bufferedImage), 0, 0);

            renderFletxes();
        }

        private void renderMultipleViaIdLinia(String linia, int x, int y) {
            BufferedImage bufferedImage =
                    new BufferedImage(blocksToPixels(ample), blocksToPixels(alt), BufferedImage.TYPE_INT_ARGB);

            Graphics2D bi = bufferedImage.createGraphics();
            bi.setFont(fontstd);

            x -= 2;

            String[] words = linia.split(" ");
            for (String word : words) {
                String type = getViaTypeFromLinia(word);
                if (type != null) {
                    bi.setColor(getColorTextFromViaType(type));

                    String textToRender = word.replace("[", "").replace("]", "");
                    int width = bi.getFontMetrics().stringWidth(textToRender);
                    int height = bi.getFontMetrics().getHeight();

                    x = x + bi.getFontMetrics().stringWidth("[");

                    String id = word.split("\\[")[1].split("]")[0];
                    int widthMarc = bi.getFontMetrics().stringWidth(id) + 8;
                    getLayer(4).fillRectangle(
                            x - 4,
                            y - height + 8 + 2,
                            widthMarc,
                            height - 4,
                            MapColorPalette.getColor(getColorFromViaType(type)));
                    getLayer(4).drawRectangle(
                            x - 4,
                            y - height + 8 + 2,
                            widthMarc,
                            height - 4,
                            MapColorPalette.getColor(getColorTextFromViaType(type)));

                    bi.drawString(textToRender, x, y);
                    // Update x for the next word
                    x += width + bi.getFontMetrics().stringWidth(" "); // space between words
                } else {
                    // If it's not a valid ID, draw the word directly
                    int width = bi.getFontMetrics().stringWidth(word);
                    bi.setColor(color_text);
                    bi.drawString(word, x, y);
                    // Update x for the next word
                    x += width + bi.getFontMetrics().stringWidth(" "); // space between words
                }
            }

            bi.dispose();
            getLayer(5).draw(MapTexture.fromImage(bufferedImage), 0, 0);
        }

        private String getViaTypeFromLinia(String via) {
            // Input: ... [ID-num] ...

            // If it does not contain [, - and ] then it is neither
            if (!via.contains("[") || !via.contains("-") || !via.contains("]")) {
                return null;
            }

            // Erase everything before [ and after ] (both included)
            String id = via.split("\\[")[1].split("]")[0];

            switch (id.split("-")[0]) {
                case "A" -> {
                    return "autovia";
                }
                case "N" -> {
                    return "nacional";
                }
                case "C" -> {
                    return "comarcal";
                }
                case "MUN" -> {
                    return "municipi";
                }
                default -> {
                    return "urbana";
                }
            }
        }

        private Color getColorFromViaType(String tipus) {
            switch (tipus) {
                case "autovia" -> {
                    return COLOR_ID_AUTOVIA;
                }
                case "nacional" -> {
                    return COLOR_ID_NACIONAL;
                }
                case "comarcal" -> {
                    return COLOR_ID_COMARCAL;
                }
                case "municipi" -> {
                    return COLOR_ID_MUNICIPI;
                }
                default -> {
                    return COLOR_ID_URBANA;
                }
            }
        }

        private Color getColorTextFromViaType(String tipus) {
            switch (tipus) {
                case "autovia" -> {
                    return COLOR_ID_TEXT_AUTOVIA;
                }
                case "nacional" -> {
                    return COLOR_ID_TEXT_NACIONAL;
                }
                case "comarcal" -> {
                    return COLOR_ID_TEXT_COMARCAL;
                }
                case "municipi" -> {
                    return COLOR_ID_TEXT_MUNICIPI;
                }
                default -> {
                    return COLOR_ID_TEXT_URBANA;
                }
            }
        }

        private void renderFletxes() {

            if (fletxes.size() == 0) return;

            for (int i = 0; i < fletxes.size(); i++) {

                // Posició de la fletxa
                // 0-7, 1/8 en sentit horari
                // 0: N, 1: NE, 2: E, 3: SE, 4: S, 5: SW, 6: W, 7: NW
                int x = 0, y = 0;
                switch (fletxes_posicio) {
                    case 0 -> { // N
                        if (fletxes.size() > 1) {
                            x = distributeItemsEvenly(blocksToPixels(ample), FLETXA_MIDA, fletxes.size(), i);
                        } else {
                            x = blocksToPixels(ample) / 2 - FLETXA_MIDA / 2;
                        }
                        y = MARC_OFFSET;
                    }
                    case 1 -> { // NE
                        x = blocksToPixels(ample) - MARC_OFFSET - FLETXA_MIDA;
                        y = MARC_OFFSET;
                    }
                    case 2 -> { // E
                        x = blocksToPixels(ample) - MARC_OFFSET - FLETXA_MIDA;
                        y = blocksToPixels(alt) / 2 - FLETXA_MIDA / 2;
                    }
                    case 3 -> { // SE
                        x = blocksToPixels(ample) - MARC_OFFSET - FLETXA_MIDA;
                        y = blocksToPixels(alt) - MARC_OFFSET - FLETXA_MIDA;
                    }
                    case 4 -> { // S
                        if (fletxes.size() > 1) {
                            x = distributeItemsEvenly(blocksToPixels(ample), FLETXA_MIDA, fletxes.size(), i);
                        } else {
                            x = blocksToPixels(ample) / 2 - FLETXA_MIDA / 2;
                        }
                        y = blocksToPixels(alt) - MARC_OFFSET - FLETXA_MIDA;
                    }
                    case 5 -> { // SW
                        x = MARC_OFFSET;
                        y = blocksToPixels(alt) - MARC_OFFSET - FLETXA_MIDA;
                    }
                    case 6 -> { // W
                        x = MARC_OFFSET;
                        y = blocksToPixels(alt) / 2 - FLETXA_MIDA / 2;
                    }
                    case 7 -> { // NW
                        x = MARC_OFFSET;
                        y = MARC_OFFSET;
                    }
                }

                // Orientació de la fletxa
                // 0-7, rotació d'1/8 de volta en sentit horari
                // 0: N, 1: NE, 2: E, 3: SE, 4: S, 5: SW, 6: W, 7: NW
                getLayer(6).draw(Assets.getMapTexture(
                        imgDir + img_fletxa + "_" + color_icones + "_" + fletxes.get(i) + imgExt
                ), x, y);

            }

        }

        private int distributeItemsEvenly(int totalSpace, int itemWidth, int numberOfItems, int currentItemIndex) {
            // Calculate the total gap space
            int totalGapSpace = totalSpace - (itemWidth * numberOfItems);

            // Calculate the space between each item
            int spaceBetweenEachItem = totalGapSpace / (numberOfItems);

            // Calculate the start and end gaps
            int startEndGap = spaceBetweenEachItem / 2;

            // Now you can use startEndGap and spaceBetweenEachItem to position your items
            return startEndGap + (currentItemIndex * (itemWidth + spaceBetweenEachItem));
        }



    }

}
