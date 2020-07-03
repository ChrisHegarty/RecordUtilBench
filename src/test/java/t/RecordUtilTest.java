package t;

import org.junit.Test;
import p.RecordUtil;
import p.RecordUtilMH;
import static org.junit.Assert.*;

public class RecordUtilTest {

    public record R0 () { }
    public record R(int i, String s, long l, R0 r0) { }

    //public static class Test { }

    @Test
    public void testIsRecord() {
        assertTrue(RecordUtil.isRecord(R.class));
        assertTrue(RecordUtilMH.isRecord(R.class));

        assertFalse(RecordUtil.isRecord(String.class));
        assertFalse(RecordUtilMH.isRecord(String.class));
    }

    static final String[] NAMES = new String[] { "i", "s", "l", "r0" };

    @Test
    public void testRecordComponentNames() {
        var names = RecordUtil.getRecordComponents(R.class);
        assertEquals(4, names.length);
        assertEquals(NAMES, names);
        names = RecordUtilMH.getRecordComponents(R.class);
        assertEquals(4, names.length);
        assertEquals(NAMES, names);

        assertEquals(RecordUtil.getRecordComponents(String.class).length, 0);
        assertEquals(RecordUtilMH.getRecordComponents(String.class).length, 0);
    }

    static final Class<?>[] TYPES = new Class<?>[]{int.class, String.class, long.class, R0.class};

    @Test
    public void testRecordComponentTypes() {
        var types = RecordUtil.getRecordComponentTypes(R.class);
        assertEquals(4, types.length);
        assertEquals(TYPES, types);
        types = RecordUtilMH.getRecordComponentTypes(R.class);
        assertEquals(4, types.length);
        assertEquals(TYPES, types);

        assertEquals(0, RecordUtil.getRecordComponentTypes(String.class).length);
        assertEquals(0, RecordUtilMH.getRecordComponentTypes(String.class).length);
    }

    @Test
    public void testGetCanonicalConstructor() {
        var ctr = RecordUtil.getCanonicalConstructor(R.class);
        assertEquals(4, ctr.getParameterCount());
        assertEquals(TYPES, ctr.getParameterTypes());
        ctr = RecordUtilMH.getCanonicalConstructor(R.class);
        assertEquals(4, ctr.getParameterCount());
        assertEquals(TYPES, ctr.getParameterTypes());

        assertNull(RecordUtil.getCanonicalConstructor(String.class));
        assertNull(RecordUtilMH.getCanonicalConstructor(String.class));
    }
}
