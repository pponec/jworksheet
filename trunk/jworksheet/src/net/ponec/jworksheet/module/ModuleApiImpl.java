/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ponec.jworksheet.module;

import java.util.Locale;

/**
 * An external module interface
 * @author Ponec
 */
public abstract class ModuleApiImpl implements ModuleApi {

    private JwsContext jwsContext;

    @Override
    public JwsContext getJwsContext() {
        return jwsContext;
    }

    @Override
    public void setJwsContext(JwsContext jwsContext) {
        this.jwsContext = jwsContext;
    }


    /** Listener can be overriden to registration the event. */
    @Override
    public void localeListener(Locale locale) {}

}
