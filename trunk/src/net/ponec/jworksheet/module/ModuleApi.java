/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ponec.jworksheet.module;

import java.util.Date;
import java.util.Locale;


/**
 * An module interface
 * @author Ponec
 */
public interface ModuleApi {

    /** The jWorkSheet context */
    public JwsContext getJwsContext();

    /** The jWorkSheet context */
    public void setJwsContext(JwsContext jwsContext);

    /** A listen in start or finish the jWorkSheet application */
    public void eventListener(boolean start);

    /** A listener for an application language change. */
    public void localeListener(Locale locale);

    /** Returns a Module Name */
    public String getName();

    /** Returns a Module Description */
    public String getDescription();

    /** Returns a Release */
    public String getRelease();

    /** Returns a Date of Creation */
    public Date getCreated();

}
