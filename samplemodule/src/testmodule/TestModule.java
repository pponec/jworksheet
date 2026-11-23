/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testmodule;

import java.time.LocalDate;
import net.ponec.jworksheet.module.ModuleApiImpl;

/**
 * Short template for a new jWorkSheet module project
 * @author Ponec
 */
public class TestModule extends ModuleApiImpl {


    public void eventListener(boolean start) {
        if (start) {
            System.out.println(toString() + ": START");
        } else {
            System.out.println(toString() + ": FINISH");
        }
    }

    /** An module Name */
    public String getName() {
        return "JwsSampleModule";
    }

    public String getDescription() {
        return "Short template for a new jWorkSheet module project";
    }

    public String getRelease() {
        return "0.02";
    }

    /** Date of creation */
    public LocalDate getCreated() {
        return LocalDate.of(2009, 1, 6);
    }

    /** An summary information */
    @Override
    public String toString() {
        return getName() + " release " + getRelease();
    }

    /** Pring an summary information */
    public static void main(String[] args) {
        System.out.println(new TestModule());
    }

}
