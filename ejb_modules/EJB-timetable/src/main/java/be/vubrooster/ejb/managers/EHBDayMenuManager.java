package be.vubrooster.ejb.managers;

import be.vubrooster.ejb.DayMenuServer;
import be.vubrooster.ejb.models.DayMenu;
import be.vubrooster.ejb.models.TimeTable;
import be.vubrooster.ejb.service.ServiceProvider;
import be.vubrooster.utils.HashUtils;
import be.vubrooster.utils.HtmlUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * EHBDayMenuManager
 * <p>
 * Created by maxim on 29-Oct-16.
 */
public class EHBDayMenuManager extends DayMenuManager {
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("NL"));
    private String kaaiURL = "http://www.erasmushogeschool.be/studeren-in-brussel/studentenrestos/kaai";
    private String kcbURL = "http://www.erasmushogeschool.be/studeren-in-brussel/studentenrestos/koninklijk-conservatorium-brussel";


    public EHBDayMenuManager(DayMenuServer server) {
        super(server);
    }

    @Override
    public List<DayMenu> loadMenus(List<DayMenu> dayMenuList) {
        super.loadMenus(dayMenuList);
        // Set GMT
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        loadDayMenus("Kaai", kaaiURL, "11:45", "13:45", 107, 135, 105);
        loadDayMenus("KCB", kcbURL, "08:00", "16:00", 107, 135, 105);

        return getDayMenuList();
    }


    public boolean loadDayMenus(String campus, String url, String startTime, String endTime, int offset, int xStart, int width) {
        logger.info("Loading resto menus for campus: " + campus);
        // Current timetable
        TimeTable currentTimeTable = ServiceProvider.getTimeTableServer().getCurrentTimeTable();

        List<String> pdfLinks = new ArrayList<>();
        try {
            Document restoPage = Jsoup
                    .parse(HtmlUtils.sendGetRequest(url, new HashMap<>()).getSource());
            Elements links = restoPage.getElementsByTag("a");
            for (Element link : links) {
                if (link.hasAttr("href")) {
                    String linkURL = link.attr("href");
                    if (linkURL.endsWith(".pdf")) {
                        pdfLinks.add(linkURL);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Unable to load day menus. Retrying ...", ex);
            return false;
        }

        logger.info("Download resto PDF files ...");
        List<File> files = new ArrayList<>();
        for (String link : pdfLinks) {
            // Download
            try {
                File dir = new File(BaseCore.getInstance().getDirectory() + "/resto/" + campus + "/");
                dir.mkdirs();
                File file = new File(dir, HashUtils.md5(link) + ".pdf");
                if (!file.exists()) {
                    logger.info("Downloading: " + link);
                    file = HtmlUtils.downloadFile(link, file);
                    files.add(file);
                } else {
                    files.add(file);
                }
            } catch (Exception ex) {
                logger.error("Unable to download resto pdfs. Retrying ...", ex);
                return false;
            }
        }

        logger.info("Parsing resto PDF files ...");
        for (File file : files) {
            try {
                readPDF(file, campus, startTime, endTime, currentTimeTable, offset, xStart, width);
            } catch (Exception ex) {
                logger.error("Unable to read resto pdf. Retrying ...", ex);
                return false;
            }
        }

        return true;
    }

    public void readPDF(File file, String campus, String startTime, String endTime, TimeTable timeTable, int offset, int xStart, int width) throws IOException, ParseException {
        PDDocument pdDoc = PDDocument.load(file);

        for (int day = 0; day < 5; day++) {
            PDPage page = pdDoc.getPage(0);
            int dayOffset = offset;

            PDFTextStripperByArea stripper = new PDFTextStripperByArea() {
                /**
                 * Line by line
                 * @param text text of line
                 * @param textPositions individual text positions in line
                 * @throws IOException
                 */
                @Override
                protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                    StringBuilder builder = new StringBuilder();
                    boolean title = false;
                    for (TextPosition position : textPositions) {
                        String baseFont = position.getFont().getName();
                        if (baseFont != null) {
                            if (baseFont.contains("Bold")) {
                                // title
                                title = true;
                            }
                        }
                        if (!position.getUnicode().equalsIgnoreCase("\n")) {
                            builder.append(position.getUnicode());
                        }
                    }
                    writeString((title ? "[TITLE]" : "") + builder.toString());
                }
            };
            stripper.addRegion("cropbox", new Rectangle(xStart + (day * dayOffset), 0, width, 580));
            stripper.extractRegions(page);
            String menuString = stripper.getTextForRegion("cropbox");
            String[] menuStringLines = menuString.split(System.getProperty("line.separator"));
            // Save PDF
            PDDocument newDocument = new PDDocument();
            page.setCropBox(new PDRectangle(xStart + (day * dayOffset), 0, width, 580));

            newDocument.addPage(page);
            newDocument.save(BaseCore.getInstance().getDirectory() + "/resto/" + campus + "/" + file.getName().replace(".pdf", "") + "_" + day + ".pdf");
            newDocument.close();

            page.setCropBox(null);

            // Add day menu
            DayMenu dayMenu = new DayMenu();

            String lastMenuValue = "";
            String lastMenuKey = "";
            Date menuDateStart = formatter.parse(menuStringLines[1].substring("[TITLE]".length()) + " " + startTime);
            Date menuDateEnd = formatter.parse(menuStringLines[1].substring("[TITLE]".length()) + " " + endTime);
            for (int i = 2; i < menuStringLines.length; i++) {
                String line = menuStringLines[i];
                if (line.startsWith("[TITLE]")) {
                    if (!lastMenuValue.equalsIgnoreCase("")) {
                        dayMenu.addMenu(lastMenuKey, lastMenuValue.trim());
                        lastMenuValue = "";
                    }
                    lastMenuKey = line.substring("[TITLE]".length());
                } else {
                    lastMenuValue += line + " ";
                }
            }
            if (!lastMenuValue.equalsIgnoreCase("")) {
                dayMenu.addMenu(lastMenuKey, lastMenuValue.trim());
            }

            // Set begin and end time
            dayMenu.setStartTime(startTime);
            dayMenu.setEndTime(endTime);
            dayMenu.setBeginTimeUnix(menuDateStart.getTime() / 1000);
            dayMenu.setEndTimeUnix(menuDateEnd.getTime() / 1000);

            Calendar c = Calendar.getInstance();
            c.setTime(menuDateStart);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            dayMenu.setDay(dayOfWeek);
            dayMenu.setCampus(campus);
            int week = c.get(Calendar.WEEK_OF_YEAR);

            c.setTime(new Date(timeTable.getStartTimeStamp() * 1000));
            int startWeek = c.get(Calendar.WEEK_OF_YEAR);
            dayMenu.setWeek(week - startWeek + 1);
            addMenu(dayMenu);
        }

        pdDoc.close();
    }

}
