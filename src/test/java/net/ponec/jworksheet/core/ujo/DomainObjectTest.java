package net.ponec.jworksheet.core.ujo;

import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.bo.meta.MetaProject;
import net.ponec.jworksheet.bo.meta.MetaWorkSpace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;

/** The keys of a domain object marked by the {@link DomainObject}. */
class DomainObjectTest {

    @Test
    void keysOfTheObjectAreTheKeysOfTheDomainClass() {
        assertEquals(Keys.keysOf(WorkSpace.class), Keys.keysOf(new WorkSpace()));
        assertTrue(Keys.keysOf(new WorkSpace()).contains(MetaWorkSpace.days));
    }

    @Test
    void findKeyUsesTheExactNameOfTheXmlElement() {
        final WorkSpace workSpace = new WorkSpace();
        assertSame(MetaWorkSpace.days, Keys.findKey(workSpace, "Day").orElseThrow());
        assertTrue(Keys.findKey(workSpace, "day").isEmpty());
        assertTrue(Keys.findKey(workSpace, "Unknown").isEmpty());
        assertTrue(Keys.findKey(new Project(), "Day").isEmpty());
    }

    @Test
    void aSubclassSharesTheKeysOfItsDomainClass() {
        final Project subclass = new Project() { };
        subclass.setDescr("sub");
        assertEquals(Keys.keysOf(Project.class), Keys.keysOf(subclass));
        assertSame(MetaProject.descr, Keys.findKey(subclass, Keys.nameOf(MetaProject.descr)).orElseThrow());
        assertEquals("sub", DomainTools.deepCopy(subclass, 1).getDescr());
    }

    @Test
    void aClassWithoutTheDomainAnnotationIsReported() {
        final class NoDomain implements DomainObject { }
        assertThrows(IllegalStateException.class, () -> Keys.keysOf(new NoDomain()));
    }
}
