/**
 * 
 */
package edu.uci.ics.crawler4j.crawler;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author ngiraud
 *
 */
public class InlineDataURIUtils {
	
	public enum Metadata {
		mimeType,
		fileType,
		encoding,
		data;
	}
	
	private static final String INLINE_REGEX = "data:(\\w+)/(\\w+);(\\w+),(.*)";
	
	public static final boolean isInlineDataUri(WebURL wurl) {
		return isInlineDataUri(wurl.getURL());
	}
	
	public static final boolean isInlineDataUri(String url) {
		return Pattern.matches(INLINE_REGEX, url);
	}
	
	public static final Map<Metadata, Object> extractMetadata(String url) {
		TreeMap<Metadata, Object> meta = new TreeMap<>();
		Matcher m = Pattern.compile(INLINE_REGEX).matcher(url);
		if (m.matches()) {
			for (Metadata md : Metadata.values()) {
				meta.put(md, m.group(md.ordinal() + 1));
			}
		}
		return meta;
	}

}
