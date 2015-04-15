package ru.kpfu.itis.cll;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class EventExtractor {
    public static final String EVENTTYPE = "EventExtractionScript.EventType";
    public static final String RELEASE = "EventExtractionScript.Release";
    public static final String ANNOUNCEMENT = "EventExtractionScript.Announcement";
    public static final String COMPANY = "EventExtractionScript.Company";
    public static final String EVENT = "EventExtractionScript.Event";
    public static final String TEXT = ("HGST выпустила жесткий диск объемом в 10 Тб.\n" +
            "Qualcomm выпустили свой собственный \"Chromecast\".\n" +
            "BlackBerry презентовала защищенный планшет Secutablet.\n" +
            "Cisco презентовала серверы нового поколения для ЦОД.");

    public static void main(String [] args) throws ResourceInitializationException, IOException, InvalidXMLException, AnalysisEngineProcessException, SQLException, ClassNotFoundException {
        final AnalysisEngine engine = AnalysisEngineFactory.createEngine("src.main.resources.EventExtractionScriptEngine");
        CAS cas = engine.newCAS();

        String text = fileToString("input/amazon1.txt");
        //cas.setDocumentText(TEXT);
        cas.setDocumentText(text);
        engine.process(cas);

        // 5 features: sofa, begin, end, participant, type, их типы: Sofa, Integer, Integer, Company, EventType
        List<Feature> list = cas.getTypeSystem().getType(EVENT).getFeatures();

        // нашли все event-ы, проходимся по ним
        for (AnnotationFS event : CasUtil.select(cas, cas.getTypeSystem().getType(EVENT))) {
            // компания
            String company = (event.getCoveredText().split(" "))[0];
            System.out.println("company: " + company);
            // release или announcement
            // event.getFeatureValue(list.get(4)) - EventType, берём тип EventType, получаем Release или Announcement
            boolean type = event.getFeatureValue(list.get(4)).getType().getName().equals(RELEASE);
            System.out.println("release?: " + type);

            // запись в БД, возвращает id
            System.out.println(DBUtils.insertEvent(company, type));
        }
    }

    public static String fileToString(String fileName) throws IOException {
        final String EoL = System.getProperty("line.separator");
        List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            stringBuilder.append(line).append(EoL);
        }
        return stringBuilder.toString();
    }
}
