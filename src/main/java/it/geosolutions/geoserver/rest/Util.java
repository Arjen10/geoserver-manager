/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2007 - 2016 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.geosolutions.geoserver.rest;

import it.geosolutions.geoserver.rest.decoder.RESTStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ETj (etj at geo-solutions.it)
 */
public class Util {

    public static final String QUIET_ON_NOT_FOUND_PARAM = "quietOnNotFound=";

    public static final boolean DEFAULT_QUIET_ON_NOT_FOUND = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    /**
     * Search for a stylename in global and in all workspaces.
     *
     * @param reader    reader
     * @param stylename stylename
     * @return {@link List}
     */
    public static List<RESTStyle> searchStyles(GeoServerRESTReader reader, String stylename) {

        List<RESTStyle> styles = new ArrayList<RESTStyle>();

        RESTStyle style = reader.getStyle(stylename);

        // We don't want geoserver to be lenient here: take only the real global style if it exists
        if (style != null) {
            if (style.getWorkspace() == null || style.getWorkspace().isEmpty()) {
                styles.add(style);
            }
        }

        for (String workspace : reader.getWorkspaceNames()) {
            style = reader.getStyle(workspace, stylename);
            if (style != null)
                styles.add(style);
        }

        return styles;
    }

    /**
     * Append the quietOnNotFound parameter to the input URL
     *
     * @param quietOnNotFound parameter
     * @param url             input url
     * @return a composed url with the parameter appended
     */
    public static String appendQuietOnNotFound(boolean quietOnNotFound, String url) {
        boolean contains = url.contains("?");
        return url + (contains ? "&" : "?") + QUIET_ON_NOT_FOUND_PARAM + quietOnNotFound;
    }

    /**
     *
     * @param url url
     * @return char
     */
    public static char getParameterSeparator(StringBuilder url) {
        char parameterSeparator = '?';
        if (url.indexOf("?") != -1) {
            parameterSeparator = '&';
        }
        return parameterSeparator;
    }

    /**
     *
     * @param url            url
     * @param parameterName  parameterName
     * @param parameterValue parameterValue
     * @return boolean
     */
    public static boolean appendParameter(StringBuilder url, String parameterName,
                                          String parameterValue) {
        boolean result = false;
        if (parameterName != null && !parameterName.isEmpty()
                && parameterValue != null && !parameterValue.isEmpty()) {
            char parameterSeparator = getParameterSeparator(url);
            url.append(parameterSeparator).append(parameterName.trim())
                    .append('=').append(parameterValue.trim());
        }
        return result;
    }

    /**
     *
     * @param url url
     * @return {@link String}
     */
    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UrlEncodeError:" + url + "  message:" + e.getMessage());
            return url;
        }
    }

}
