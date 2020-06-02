package com.github.wulijie.htmlparser;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.util.ArrayDeque;

//html解析
public class HtmlParser implements Html.TagHandler, ContentHandler {
    public static final String TAG = HtmlParser.class.getSimpleName();
    public static final String HTML_START = "<inject/>";

    public interface TagHandler {
        boolean handleTag(boolean opening, String tag, Editable output, Attributes attributes);
    }

    //解析html
    public static Spanned buildSpannedText(String html, Html.ImageGetter imageGetter, TagHandler handler) {
        return Html.fromHtml(HTML_START.concat(html), imageGetter, new HtmlParser(handler));
    }

    //解析html
    public static Spanned buildSpannedText(String html, TagHandler handler) {
        return buildSpannedText(html, null, handler);
    }

    //设置html
    public static void setHtml(TextView textView, String html, Html.ImageGetter imageGetter, TagHandler handler) {
        textView.setText(HtmlParser.buildSpannedText(html, imageGetter, handler));
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //设置html
    public static TextView setHtml(TextView textView, String html, TagHandler handler) {
        setHtml(textView, html, null, handler);
        return textView;
    }

    //从attributes中根据指定key获取value
    public static String getValue(Attributes attributes, String name) {
        for (int i = 0, n = attributes.getLength(); i < n; i++) {
            if (name.equals(attributes.getLocalName(i))) {
                String value = attributes.getValue(i);
                return value;
            }
        }
        return null;
    }

    private final TagHandler handler;
    private ContentHandler wrapped;
    private Editable text;
    private ArrayDeque<Boolean> tagStatus = new ArrayDeque<>();

    private HtmlParser(TagHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (wrapped == null) {
            text = output;
            wrapped = xmlReader.getContentHandler();
            xmlReader.setContentHandler(this);
            tagStatus.addLast(Boolean.FALSE);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        boolean isHandled = handler.handleTag(true, localName, text, attributes);
        tagStatus.addLast(isHandled);
        if (!isHandled)
            wrapped.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!tagStatus.removeLast())
            wrapped.endElement(uri, localName, qName);
        handler.handleTag(false, localName, text, null);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        wrapped.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        wrapped.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        wrapped.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        wrapped.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        wrapped.endPrefixMapping(prefix);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        wrapped.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        wrapped.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        wrapped.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        wrapped.skippedEntity(name);
    }
}
