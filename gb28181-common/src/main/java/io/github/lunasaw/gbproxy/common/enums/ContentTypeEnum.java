package io.github.lunasaw.gbproxy.common.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sip.SipFactory;
import javax.sip.header.ContentTypeHeader;

import lombok.SneakyThrows;

/**
 * 消息体类型
 * @author weidian
 */

public enum ContentTypeEnum {

    /**
     * xml
     */
    APPLICATION_XML("Application", "MANSCDP+xml"),

    /**
     * sdp
     */
    APPLICATION_SDP("APPLICATION", "SDP"),
    /**
     *
     */
    APPLICATION_MAN_SRTSP("Application", "MANSRTSP"),

    ;

    private static final Map<String, ContentTypeHeader> MAP = new ConcurrentHashMap<>();
    private final String                                type;
    private final String                                subtype;

    ContentTypeEnum(String type, String subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    public static ContentTypeEnum fromContentTypeHeader(ContentTypeHeader header) {
        for (ContentTypeEnum contentType : values()) {
            if (contentType.type.equals(header.getContentType())
                && contentType.subtype.equals(header.getContentSubType())) {
                return contentType;
            }
        }
        return null;
    }

    public static ContentTypeEnum fromString(String contentType) {
        for (ContentTypeEnum contentTypeEnum : values()) {
            if (contentTypeEnum.toString().equalsIgnoreCase(contentType)) {
                return contentTypeEnum;
            }
        }
        return null;
    }

    @SneakyThrows
    public ContentTypeHeader getContentTypeHeader() {
        String key = toString();
        if (MAP.containsKey(key)) {
            return MAP.get(key);
        } else {
            ContentTypeHeader contentTypeHeader = SipFactory.getInstance().createHeaderFactory().createContentTypeHeader(type, subtype);
            MAP.put(key, contentTypeHeader);
            return contentTypeHeader;
        }
    }

    @Override
    public String toString() {
        return type + "/" + subtype;
    }
}