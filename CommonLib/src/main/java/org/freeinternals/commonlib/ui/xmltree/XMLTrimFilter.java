package org.freeinternals.commonlib.ui.xmltree;

import java.io.CharArrayWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * <p>
 * A little change may be done on formatting, annotation, java doc, etc.
 * </p>
 *
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @see <a href="http://www.javalobby.org/java/forums/t19666.html">XML Viewer for Swing</a>
 */
public class XMLTrimFilter extends XMLFilterImpl{

    private CharArrayWriter contents = new CharArrayWriter();

    public XMLTrimFilter(XMLReader parent){
        super(parent);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException{
        writeContents();
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void characters(char ch[], int start, int length){
        contents.write(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException{
        writeContents();
        super.endElement(uri, localName, qName);
    }

    @Override
    public void ignorableWhitespace(char ch[], int start, int length){}

    private void writeContents() throws SAXException{
        char ch[] = contents.toCharArray();
        if(!isWhiteSpace(ch)) {
            super.characters(ch, 0, ch.length);
        }
        contents.reset();
    }

    private boolean isWhiteSpace(char ch[]){
        for(int i = 0; i<ch.length; i++){
            if(!Character.isWhitespace(ch[i])) {
                return false;
            }
        }
        return true;
    }
}

