import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class ViewSaxHandler extends DefaultHandler {
    private List<ViewPart> viewPartList;

    public static void main(String[] args) {
        String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "              android:orientation=\"vertical\"\n" +
                "              android:layout_width=\"fill_parent\"\n" +
                "              android:layout_height=\"fill_parent\">\n" +
                "    <TextView\n" +
                "            android:id=\"@+id/tv_hello_world\"\n" +
                "            android:layout_width=\"fill_parent\"\n" +
                "            android:layout_height=\"wrap_content\"\n" +
                "            android:text=\"Hello World, MyActivity\"/>\n" +
                "</LinearLayout>\n" +
                "\n";

        ViewSaxHandler handler = new ViewSaxHandler();
        InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        try {
            handler.createViewList(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createViewList(String string) throws ParserConfigurationException, SAXException, IOException {
        InputStream xmlStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(xmlStream, this);
    }

    public void createViewList(InputStream xmlStream) throws Exception {

    }


    @Override
    public void startDocument() throws SAXException {
        viewPartList = new ArrayList<ViewPart>();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        ViewPart viewPart = new ViewPart();
        viewPart.setType(qName);
        String id = attributes.getValue("android:id");
        if (id != null) {
            viewPart.setId(id.replace("@+id/", ""));
            viewPartList.add(viewPart);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    public List<ViewPart> getViewPartList() {
        return viewPartList;
    }

    public void setViewPartList(List<ViewPart> viewPartList) {
        this.viewPartList = viewPartList;
    }
}