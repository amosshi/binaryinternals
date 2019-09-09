package org.freeinternals.commonlib.ui.xmltree;

import java.io.CharArrayWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Created by <code>Santhosh Kumar T</code>. Miner change may be done on
 * formatting, annotation, java doc, etc, for check style.
 *
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @see <a href="http://www.javalobby.org/java/forums/t19666.html">XML Viewer
 * for Swing</a>
 */
public final class XMLTrimFilter extends XMLFilterImpl {

    /**
     * Char array writer.
     */
    private final CharArrayWriter contents = new CharArrayWriter();

    /**
     * Constructor.
     *
     * @param parent XML parser
     */
    public XMLTrimFilter(final XMLReader parent) {
        super(parent);
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        writeContents();
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        contents.write(ch, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        writeContents();
        super.endElement(uri, localName, qName);
    }

    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) {
    }

    private void writeContents() throws SAXException {
        char[] ch = contents.toCharArray();
        if (!isWhiteSpace(ch)) {
            super.characters(ch, 0, ch.length);
        }
        contents.reset();
    }

    private boolean isWhiteSpace(final char[] ch) {
        for (int i = 0; i < ch.length; i++) {
            if (!Character.isWhitespace(ch[i])) {
                return false;
            }
        }
        return true;
    }
}
