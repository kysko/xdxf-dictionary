package com.xdxf.dictionary.parser;

import com.xdxf.dictionary.sqlite.Book;
import com.xdxf.dictionary.sqlite.DBHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class XDXFParser extends DictionaryParser {

    public XDXFParser(DBHelper dbhandler) {
        super(dbhandler);
    }

    @Override
    public void parse(InputStream f, WordAddedCallback callback) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(f, new XDXFHandler(callback));
        } catch (IOException ex) {
            Logger.getLogger(XDXFParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XDXFParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (SAXStopParsing ex) {
            Logger.getLogger(XDXFParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XDXFParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    private class SAXStopParsing extends SAXException {

        public SAXStopParsing(String message) {
            super(message);
        }
    }

    private class XDXFHandler extends DefaultHandler {

        private static final String ARTICLE = "ar";
        private static final String KEY = "k";
        private static final String XDXF = "xdxf";
        private static final String FULL_NAME = "full_name";
        private static final String DESCRIPTION = "description";
        private static final String LANG_FROM_ATTR = "lang_from";
        private static final String LANG_TO_ATTR = "lang_to";
        private static final String PATTERN = "(\r\n|\r|\n|\n\r)";
        private final Pattern patternNewLine;
        String fromLang, toLang, name, description;
        String key;
        private String tag = "/<\\s*k[^>]*>(.*?)<\\s*/\\s*k>/g";
        private StringBuilder sb = new StringBuilder();
        private Book book;
        private boolean htmlBlob;
        private WordAddedCallback callback;

        public XDXFHandler(WordAddedCallback callback) {
            patternNewLine = Pattern.compile(PATTERN);
            this.callback = callback;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase(XDXF)) {
                fromLang = attributes.getValue(LANG_FROM_ATTR);
                toLang = attributes.getValue(LANG_TO_ATTR);
            } else if (qName.equalsIgnoreCase(FULL_NAME)) {
                tag = FULL_NAME;
            } else if (qName.equalsIgnoreCase(DESCRIPTION)) {
                tag = DESCRIPTION;
            } else if (qName.equalsIgnoreCase(ARTICLE)) {
                tag = ARTICLE;
            } else if (qName.equalsIgnoreCase(KEY)) {
                tag = KEY;
            }

            if (htmlBlob) {
                if (isHTMLFormattingTag(qName)) {
                    sb.append("<").append(qName).append(">");
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (tag.equalsIgnoreCase(FULL_NAME)) {
                name = new String(ch, start, length);
                tag = "";
            } else if (tag.equalsIgnoreCase(DESCRIPTION)) {
                description = new String(ch, start, length);
                tag = "";
                book = addBook(fromLang, toLang, name, description);
                //dbhandler.beginTransactionEx();
                //dbhandler.prepareForBulkInsert();
                if (book == null) {
                    throw new SAXStopParsing("");
                }
            } else if (tag.equalsIgnoreCase(ARTICLE)) {
                description = new String(ch, start, length);
                tag = "";

            } else if (tag.equalsIgnoreCase(KEY)) {
                key = new String(ch, start, length);
                tag = "";
            }
            if (htmlBlob) {
                sb.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (qName.equalsIgnoreCase(KEY)) {
                sb = new StringBuilder();
                htmlBlob = true;
            } else if (qName.equalsIgnoreCase(ARTICLE)) {
                htmlBlob = false;
                addWord(book, key, nl2br(sb.toString()));
                callback.wordAdded(key);
            }
            if (htmlBlob) {
                if (isHTMLFormattingTag(qName)) {
                    sb.append("</").append(qName).append(">");
                }
            }

        }

        @Override
        public void endDocument() throws SAXException {
            if (book != null) {
                dbhandler.commitBook(book);
                //dbhandler.commitTransaction();
                dbhandler.close();
            }
        }

        private String nl2br(String value) {
            Matcher m = patternNewLine.matcher(value);
            if (m.find()) {
                return m.replaceAll("<br />");
            } else {
                return value;
            }
        }

        private boolean isHTMLFormattingTag(String qName) {
            if (qName.equalsIgnoreCase("b") || qName.equalsIgnoreCase("i")) {
                return true;
            }
            return false;
        }
    }
}
