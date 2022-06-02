/**
 * Copyright (C) 2007-2022, Pavel Ponec, contact: http://ponec.net/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/gpl-2.0.txt
 */


package net.ponec.jworksheet.resources;

import java.awt.Image;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import javax.swing.ImageIcon;

/**
 * Provides Images and other resources.
 * @author Pavel Ponec
 */
public class ResourceProvider {

    /** Cascade Style Sheet */
    public static final String FILE_CSS = "style.css";

    /** Localized text properties. */
    public static final String TEXT_PROPERTIES = "text.properties";

    /** A Report XSL Style Sheet */
    public static String REPORT_BASE  = "ReportBase.xsl";
    public static String REPORT_BASE2 = "ReportBase2.xsl";
    public static String REPORT_BASE3 = "ReportBase3.xsl";

    /** Icon s */
    public static String LOGO     = "logo.png";
    public static String LOGO16   = "logo16.png";
    public static String LOGO_TRY = "logoTray.gif";
    public static String SORT     = "sort.png";

    // SILK ICONS (http://www.famfamfam.com/lab/icons/silk/)
    public static final String IMG_ADD = "_add.png";
    public static final String IMG_APPLICATION_DOUBLE = "_application_double.png";
    public static final String IMG_APPLICATION_LIST = "_application_view_list.png";
    public static final String IMG_APPLICATION_SPLIT = "_application_split.png";
    public static final String IMG_ARROW_DOWN = "_arrow_down.png";
    public static final String IMG_ARROW_UNDO = "_arrow_undo.png";
    public static final String IMG_DATE = "_date.png";
    public static final String IMG_DELETE = "_delete2.png";
    public static final String IMG_HELP = "_help.png";
    public static final String IMG_INFORMATION = "_information.png";
    public static final String IMG_REPORT = "_report.png";
    public static final String IMG_TICK = "_tick.png";     // OK
    public static final String IMG_WRENCH = "_wrench.png"; // Tools
    public static final String IMG_OK     = "_tick.png";   // Button OK
    public static final String IMG_CANCEL = "_cross.png"; // Button CANCEL
    public static final String IMG_EMPTY = "_empty.png"; // A vertical distance
    public static final String IMG_HOME_PAGE = "_page_world.png"; // Home Page
    public static final String IMG_PREV = "_resultset_previous.png"; // Go Prev
    public static final String IMG_NEXT = "_resultset_next.png";     // Go Next
    public static final String IMG_HOME = "house.png";     // Go to today


    /**
     * Creates a new instance of ResourceProvider
     */
    public ResourceProvider() {
    }

    /** Returns a URL of resource. */
    public URL getUrl(String fileName) {
        String path = getClass().getPackage().getName().replace('.', '/');
        path = "/" + path + "/" + fileName;
        URL result = getClass().getResource(path);
        return result;
    }

    /** Returns an Icon from a file name. */
    public ImageIcon getIcon(String name, boolean hideIcon) {
        return hideIcon ? null : getIcon(name);
    }

    /** Returns an Icon from a file name. */
    public ImageIcon getIcon(String name) {
        final ImageIcon result = new ImageIcon(getUrl(name));
        return result;
    }

    /** Returns an Image from a file name. */
    public Image getImage(String name) {
        final Image result = getIcon(name).getImage();
        return result;
    }

    /** Returns Localization of the key */
    public String getText(String key, Locale locale) {
        try {
            URL url = getUrl(TEXT_PROPERTIES);
            PropertyResourceBundle bundle = new PropertyResourceBundle(url.openStream());
            return bundle.getString(key);
        } catch (Exception e) {
            return "[" + key + "]";
        }
    }

}
