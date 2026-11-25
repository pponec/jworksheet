package net.ponec.jworksheet.core.ujo;

import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.WorkSpace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import net.ponec.jworksheet.core.ujo.MetaAliases.PR;
import net.ponec.jworksheet.core.ujo.MetaAliases.WS;

/** The keys of a domain object marked by the {@link DomainObject}. */
class DomainObjectTest {

    @Test
    void keysOfTheObjectAreTheKeysOfTheDomainClass() {
        assertEquals(Keys.keysOf(WorkSpace.class), Keys.keysOf(new WorkSpace()));
        assertTrue(Keys.keysOf(new WorkSpace()).contains(WS.days));
    }

    @Test
    void findKeyUsesTheExactNameOfTheXmlElement() {
        final WorkSpace workSpace = new WorkSpace();
        assertSame(WS.days, Keys.findKey(workSpace, "Day").orElseThrow());
        assertTrue(Keys.findKey(workSpace, "day").isEmpty());
        assertTrue(Keys.findKey(workSpace, "Unknown").isEmpty());
        assertTrue(Keys.findKey(new Project(), "Day").isEmpty());
    }

    @Test
    void aSubclassSharesTheKeysOfItsDomainClass() {
        final Project subclass = new Project() { };
        subclass.setDescr("sub");
        assertEquals(Keys.keysOf(Project.class), Keys.keysOf(subclass));
        assertSame(PR.descr, Keys.findKey(subclass, Keys.nameOf(PR.descr)).orElseThrow());
        assertEquals("sub", DomainTools.deepCopy(subclass, 1).getDescr());
    }

    @Test
    void aClassWithoutTheDomainAnnotationIsReported() {
        final class NoDomain implements DomainObject { }
        assertThrows(IllegalStateException.class, () -> Keys.keysOf(new NoDomain()));
    }
}
