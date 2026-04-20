package net.jpkg.mvcly.utl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import org.junit.Test;

public class HttpUtilsTest {

  @Test
  public void testGetParamsNormal() {
    Map<String, String> result = HttpUtils.getParams("name=John&age=30");
    assertEquals("John", result.get("name"));
    assertEquals("30", result.get("age"));
  }

  @Test
  public void testGetParamsValueWithEquals() {
    Map<String, String> result = HttpUtils.getParams("equation=a=b&plain=c");
    assertEquals("a=b", result.get("equation"));
    assertEquals("c", result.get("plain"));
  }

  @Test
  public void testGetParamsEmptyValue() {
    Map<String, String> result = HttpUtils.getParams("key=&other=val");
    assertEquals("", result.get("key"));
    assertEquals("val", result.get("other"));
  }

  @Test
  public void testGetParamsKeyWithoutValue() {
    Map<String, String> result = HttpUtils.getParams("flag");
    assertEquals("", result.get("flag"));
  }

  @Test
  public void testGetParamsNullBody() {
    Map<String, String> result = HttpUtils.getParams(null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetUrlParams() {
    Map<String, String> result = HttpUtils.getUrlParams("/path?foo=bar&baz=qux");
    assertEquals("bar", result.get("foo"));
    assertEquals("qux", result.get("baz"));
  }

  @Test
  public void testGetUrlParamsNoQuery() {
    Map<String, String> result = HttpUtils.getUrlParams("/path");
    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetUrlParamsDecodesPercentEncoding() {
    Map<String, String> result = HttpUtils.getUrlParams("/path?name=hello%20world");
    assertEquals("hello world", result.get("name"));
  }

  @Test
  public void testBodyToForm() {
    Map<String, String> result = HttpUtils.bodyToForm("user=test&pass=123".getBytes());
    assertEquals("test", result.get("user"));
    assertEquals("123", result.get("pass"));
  }

  @Test
  public void testGetContentTypeHtml() {
    assertEquals("text/html; charset=utf-8", HttpUtils.getContentType("page.html"));
  }

  @Test
  public void testGetContentTypeJson() {
    assertEquals("application/json", HttpUtils.getContentType("data.json"));
  }

  @Test
  public void testGetContentTypeUnknownFallsBackToText() {
    assertEquals("text/plain; charset=utf-8", HttpUtils.getContentType("archive.unknown"));
  }

  @Test
  public void testGetContentTypePng() {
    assertEquals("image/png", HttpUtils.getContentType("image.png"));
  }

  @Test
  public void testCookiesToMap() {
    Map<String, String> result = HttpUtils.cookiesToMap("session=abc123; theme=dark");
    assertEquals("abc123", result.get("session"));
    assertEquals("dark", result.get("theme"));
  }

  @Test
  public void testCookiesToMapNull() {
    Map<String, String> result = HttpUtils.cookiesToMap(null);
    assertTrue(result.isEmpty());
  }
}
