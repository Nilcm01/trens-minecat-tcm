package io.github.janvinas.trensminecat;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ManualDisplays {

    public static class ManualDisplay1 extends ManualDisplay {
        static String imgDir = "img/";
        boolean updateTime = true;

        public boolean updateInformation(String displayID, String displayName, String destination){
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            String trainLine;
            String dest;
            if(destination.equals("nopara")){
                trainLine = "info";
                dest = "sense parada";
            }else{
                trainLine = destination.substring(0, destination.indexOf(' '));
                dest = destination;
                if(destination.contains(" → "))
                    dest = destination.substring(destination.indexOf('→') + 2);
                dest = dest.toUpperCase();
            }

            getLayer(5).clear();
            getLayer(4).clear();
            MapTexture lineIcon = loadTexture(imgDir + "28px/" + trainLine + ".png");
            if(!(lineIcon.getHeight() > 1)){
                dest = displayName;
                lineIcon = loadTexture(imgDir + "28px/what.png");
            }
            getLayer(4).draw(lineIcon, 5, 14);
            getLayer(4).setAlignment(MapFont.Alignment.MIDDLE);
            getLayer(4).draw(MapFont.MINECRAFT, 74, 23,
                    MapColorPalette.getColor(255, 255, 255),
                    dest);

            updateTime = false;
            return true;
        }

        public boolean clearInformation(String displayID){
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            getLayer(4).clear();
            getLayer(4).draw(loadTexture(imgDir + "28px/rodalies.png"), 5, 14);

            getLayer(5).clear();
            getLayer(5).setAlignment(MapFont.Alignment.RIGHT);
            LocalDateTime now = LocalDateTime.now();
            getLayer(5).draw(MapFont.MINECRAFT, 119, 24, MapColorPalette.COLOR_WHITE,
                    now.format(DateTimeFormatter.ofPattern("HH:mm")));
            updateTime = true;
            return true;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            getLayer(1).clear();
            getLayer(1).fillRectangle(5, 14, 118, 28, MapColorPalette.getColor(40, 40, 40));
            getLayer(3).clear();
            getLayer(3).draw(loadTexture(imgDir + "DepartureBoard3.png"), 0, 0);
            getLayer(4).clear();
            getLayer(4).draw(loadTexture(imgDir + "28px/rodalies.png"), 5, 14);

        }

        @Override
        public void onTick() {
            super.onTick();
            if(updateTime){
                getLayer(5).clear();
                getLayer(5).setAlignment(MapFont.Alignment.RIGHT);
                LocalDateTime now = LocalDateTime.now();
                getLayer(5).draw(MapFont.MINECRAFT, 119, 24, MapColorPalette.COLOR_WHITE,
                        now.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }
    }
    /*
    public static class ManualDisplay2 extends MapDisplay{
        TreeMap<String, LocalDateTime> lastTrains = new TreeMap<>();
        HashMap<String, Duration> trainIntervals = properties.get("trainIntervals", HashMap.class);
        int tickCount = 0;

        public boolean updateInformation(String displayID, String displayName, String destination){
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            LocalDateTime now = LocalDateTime.now();
            lastTrains.put(destination, now);
            return true;
        }

        public boolean clearInformation(String displayID){
            return true;
        }

        @Override
        public void onAttached() {
            getLayer(0).draw(loadTexture(imgDir + "DepartureBoard4.png"), 0, 0);
            super.onAttached();
        }

        @Override
        public void onTick() {

            LocalDateTime now = LocalDateTime.now();
            getLayer(2).clear();
            getLayer(2).setAlignment(MapFont.Alignment.MIDDLE);
            getLayer(2).draw(MapFont.MINECRAFT, 227, 7, MapColorPalette.COLOR_BLACK,
                    now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            if( (tickCount % updateTime) == 0){
                int secondsToDisplayOnBoard = TrensMinecat.secondsToDisplayOnBoard;
                TreeMap<LocalDateTime, String> departureBoardTrains = new TreeMap<>();

                for(String train : lastTrains.keySet()){


                    departureBoardTrains.put(lastTrains.get(train).plus(interval), train);  //add first expected train
                    departureBoardTrains.put(lastTrains.get(train).plus(interval.multipliedBy(2)), train); //add the next expected train twice the duration of the first
                }

                //print train lines on screen
                getLayer(1).clear();
                getLayer(1).setAlignment(MapFont.Alignment.LEFT);
                int i = 0;
                for(LocalDateTime departureTime : departureBoardTrains.keySet()){


                    Duration untilDeparture = Duration.between(now, departureTime);
                    if(untilDeparture.minusSeconds(secondsToDisplayOnBoard).isNegative()) {
                        getLayer(1).draw(MapFont.MINECRAFT, 113, 34 + i * 14,
                                MapColorPalette.getColor(255, 0, 0), "imminent");
                    }else if(untilDeparture.minusMinutes(5).isNegative()){
                        getLayer(1).draw(MapFont.MINECRAFT, 113, 34 + i * 14,
                                MapColorPalette.getColor(0, 128, 0),
                                untilDeparture.getSeconds()/60 + "min");
                    }else{
                        getLayer(1).draw(MapFont.MINECRAFT, 113, 34 + i*14,
                                MapColorPalette.getColor(0, 0, 0),
                                departureTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    }
                    //getLayer(1).draw(loadTexture(imgDir + "11px/" +
                            //departureBoardTrains.get(departureTime).name + ".png"), 1, 33 + i*14);

                    String destination = departureBoardTrains.get(departureTime).destination;
                    if(!destination.equals("_")) getLayer(1).draw(MapFont.MINECRAFT, 21, 34 + i*14,
                            MapColorPalette.getColor(0, 0, 0),
                            departureBoardTrains.get(departureTime).destination);
                    String platform = departureBoardTrains.get(departureTime).platform;
                    if(!platform.equals("_")) getLayer(1).draw(MapFont.MINECRAFT, 99, 34 + i*14,
                            MapColorPalette.getColor(0, 0, 0),
                            departureBoardTrains.get(departureTime).platform);
                    String information = departureBoardTrains.get(departureTime).information;
                    if(!information.equals("_")){
                        getLayer(1).draw(MapFont.MINECRAFT, 162, 34 + i*14,
                                MapColorPalette.getColor(0, 0, 0),
                                departureBoardTrains.get(departureTime).information);
                    }

                    i++;
                }
            }

            tickCount++;
            super.onTick();
        }
    }
    */

    public static class ManualDisplay3 extends ManualDisplay{
        static String imgDir = MapDisplays.imgDir;

        static Font minecraftiaWide = TrensMinecat.minecraftiaJavaFont;
        static MapFont<Character> minecraftia;

        static {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.TRACKING, -0.125);
            minecraftia = MapFont.fromJavaFont(minecraftiaWide.deriveFont(attributes).deriveFont(8F));
        }

        @Override
        public void onAttached() {
            super.onAttached();
            getLayer(0).clear();
            getLayer(0).draw(loadTexture(imgDir + "ManualDisplay3.png"), 0, 0);
        }

        @Override
        public void onTick() {
            super.onTick();

            //layer0: permanent background (never updated)
            //layer1: --
            //layer2: circumstancial background (updated from signs)
            //layer3: text and icons (updated every updateTime seconds)
            //layer4: time (updated every tick)

            getLayer(4).clear();
            LocalDateTime now = LocalDateTime.now();
            getLayer(4).setAlignment(MapFont.Alignment.MIDDLE);
            getLayer(4).draw(minecraftia, 38, 10, MapColorPalette.COLOR_WHITE,
                    now.format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        public boolean updateInformation(String displayID, String displayName, String destination){
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            getLayer(2).clear();
            getLayer(3).clear();

            getLayer(2).draw(minecraftia, 58, 11, MapColorPalette.getColor(0x2B, 0x3D, 0x3F), "Tren Actual");
            getLayer(2).fillRectangle(22, 30, 212, 16, MapColorPalette.getColor(200, 200, 200));

            getLayer(2).draw(minecraftia, 24, 34, MapColorPalette.getColor(0x2B, 0x3D, 0x3F), "Destinació");
            //getLayer(2).draw(minecraftia), 138, 34, MapColorPalette.getColor(0x2B, 0x3D, 0x3F), "Via");
            getLayer(2).draw(minecraftia, 162, 34, MapColorPalette.getColor(0x2B, 0x3D, 0x3F), "Observacions");

            String trainLine;
            String dest;
            if(destination.equals("nopara")){
                trainLine = "info";
                dest = "Sense parada";
            }else{
                trainLine = destination.substring(0, destination.indexOf(' '));
                dest = destination;
                if(destination.contains(" → "))
                    dest = destination.substring(destination.indexOf('→') + 2);
            }

            getLayer(3).draw(minecraftia, 51, 49, MapColorPalette.COLOR_BLACK, dest);
            getLayer(3).draw(loadTexture(imgDir + "11px/" + trainLine + ".png"), 22, 49);

            return true;
        }

        public boolean clearInformation(String displayID){
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            getLayer(2).clear();
            getLayer(3).clear();
            return true;
        }
    }

    public static class ManualDisplay4 extends ManualDisplay{  //pantalla ADIF pròxima sortida. 2*1 blocs.

        int tickCount = 0;
        boolean sortidaImmediata = false;
        static MapTexture background = MapTexture.loadPluginResource(JavaPlugin.getPlugin(TrensMinecat.class), "img/ManualDisplay4.png");

        //layer1: black background (onAttached)
        //layer2: image (onAttached)
        //layer3: dynamic text (every tick)
        //layer4: text  (every updateTime ticks)
        //layer5: clock handles (onTick)

        @Override
        public void onAttached() {

            getLayer(1).fillRectangle(0, 10, 256, 85, MapColorPalette.getColor(0x2E, 0x2E, 0X2E));
            getLayer(2).draw(background, 0, 0);
            updatePlatformNumber();

            super.onAttached();
        }
        private void updatePlatformNumber(){
            BufferedImage platformNumber = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = platformNumber.createGraphics();
            g.setColor(new Color(255, 255, 255));
            g.setFont(TrensMinecat.helvetica46JavaFont);
            g.drawString(properties.get("platform", String.class), 159, 80); //platform number
            g.dispose();
            getLayer(4).draw(MapTexture.fromImage(platformNumber), 0, 0);
        }

        @Override
        public void onTick() {
            super.onTick();

            LocalDateTime now = LocalDateTime.now();
            getLayer(5).clear();

            getLayer(5).drawLine(222, 52,
                    222 + getX(now.getSecond(), 60, 20),
                    52 + getY(now.getSecond(), 60, 20),
                    MapColorPalette.getColor(0x76, 0x76, 0x76)
            );
            getLayer(5).drawLine(222, 52,
                    222 + getX(now.getMinute(), 60, 18),
                    52 + getY(now.getMinute(), 60, 18),
                    MapColorPalette.getColor(0x3b, 0x3b, 0x3b)
            );
            getLayer(5).drawLine(222, 52,
                    222 + getX(now.getHour() + now.getMinute() / 60F, 12, 12),
                    52 + getY(now.getHour() + now.getMinute() / 60F, 12, 12),
                    MapColorPalette.getColor(0, 0, 0)
            );

            if(sortidaImmediata){

                int n = tickCount % 20;
                Color c = new Color(255, 242, 0);

                if(n ==0) c = new Color(255, 242, 49);
                if(n == 10) c = new Color(255, 0, 0);

                if(n == 0 || n == 10){
                    getLayer(3).clear();
                    BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = layer3.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                    g.setColor(c);
                    g.setFont(TrensMinecat.minecraftiaJavaFont);
                    g.drawString("SORTIDA IMMEDIATA", 6, 88);
                    g.dispose();
                    getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
                }

            }

            tickCount++;
        }

        public boolean updateInformation(String displayID, String displayName, String destination) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            String trainLine;
            String dest;
            if(destination.equals("nopara")){
                trainLine = "info";
                dest = "sense parada";
            }else{
                trainLine = destination.substring(0, destination.indexOf(' '));
                dest = destination;
                if(destination.contains(" → "))
                    dest = destination.substring(destination.indexOf('→') + 2);
                dest = dest.toUpperCase();
            }

            getLayer(4).clear();
            BufferedImage layer4 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = layer4.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setColor(new Color(255, 242, 0));
            g.setFont(TrensMinecat.minecraftiaJavaFont);
            g.drawString(trainLine, 6, 41); //tren
            g.drawString(displayName, 78, 41); //servei
            g.drawString(dest, 6, 73); //destinació
            sortidaImmediata = true;
            g.dispose();

            updatePlatformNumber();

            getLayer(4).draw(MapTexture.fromImage(layer4), 0, 5); //global offset because the text is off (idk why)
            return true;

        }

        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            getLayer(4).clear();
            getLayer(3).clear();
            sortidaImmediata = false;
            return true;
        }

        private int getX(float angle, float divideBy, float length){
            return (int) Math.round(Math.sin(angle * 2 * Math.PI / divideBy) * length);
        }

        private int getY(float angle, int divideBy, float length){
            return - (int) Math.round(Math.cos(angle * 2 * Math.PI / divideBy) * length);
        }
    }


    public static class ManualDisplay5 extends ManualDisplay{

        static MapTexture background = MapTexture.loadPluginResource(JavaPlugin.getPlugin(TrensMinecat.class), "img/ManualDisplay5.png");

        @Override
        public boolean updateInformation(String displayID, String displayName, String destination) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            getLayer(1).clear();
            BufferedImage layer1 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = layer1.createGraphics();
            g.setColor(new Color(255, 242, 0));
            g.setFont(TrensMinecat.helvetica46JavaFont.deriveFont(10F));
            g.drawString(destination, 22, 71);
            g.drawString(displayName, 22, 94);
            g.dispose();
            getLayer(1).draw(MapTexture.fromImage(layer1),0 , 0);
            return true;
        }

        @Override
        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            getLayer(1).clear();
            return true;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            getLayer(0).clear();
            getLayer(0).draw(background, 0, 0);
        }

    }
}
