import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.MapObjectives.*;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Dedicated tests for the MapMarkers class to improve mutation test coverage.
 * These tests focus specifically on the implementation details of MapMarkers.
 */
public class MapMarkersSpecificTests extends ApplicationTests {

    private MapMarkers markers;

    @BeforeEach
    void setup() {
        markers = new MapMarkers();
    }

    @Test
    void testAddNullMarker() {
        // Adding a null marker should be silently ignored
        int initialSize = markers.size();
        markers.add(1, null);
        assertEquals(initialSize, markers.size(), "Size should not change when adding null marker");
    }

    @Test
    void testAddReplaceMarker() {
        // Add initial marker
        ObjectiveMarker marker1 = new PointMarker(1, 1);
        markers.add(1, marker1);
        assertEquals(1, markers.size(), "Size should be 1 after adding first marker");

        // Replace with another marker with same ID
        ObjectiveMarker marker2 = new PointMarker(2, 2);
        markers.add(1, marker2);
        assertEquals(1, markers.size(), "Size should remain 1 after replacing marker");
        assertSame(marker2, markers.get(1), "Second marker should have replaced first marker");

        // Verify array index is preserved
        assertEquals(0, marker2.arrayIndex, "Array index should be preserved when replacing marker");
    }

    @Test
    void testRemoveMiddleMarker() {
        // Add 3 markers
        ObjectiveMarker marker1 = new PointMarker(1, 1);
        ObjectiveMarker marker2 = new PointMarker(2, 2);
        ObjectiveMarker marker3 = new PointMarker(3, 3);

        markers.add(1, marker1);
        markers.add(2, marker2);
        markers.add(3, marker3);
        assertEquals(3, markers.size(), "Size should be 3 after adding markers");

        // Remove middle marker
        markers.remove(2);
        assertEquals(2, markers.size(), "Size should be 2 after removing middle marker");

        // Verify marker3 has been moved to marker2's position
        assertEquals(1, marker3.arrayIndex, "Last marker should have shifted to index 1");

        // Verify we can iterate through remaining markers
        int count = 0;
        for (ObjectiveMarker marker : markers) {
            count++;
            assertTrue(marker == marker1 || marker == marker3,
                    "Only marker1 and marker3 should remain");
        }
        assertEquals(2, count, "Should iterate through exactly 2 markers");
    }

    @Test
    void testRemoveLastMarker() {
        // Add 2 markers
        markers.add(1, new PointMarker(1, 1));
        markers.add(2, new PointMarker(2, 2));
        assertEquals(2, markers.size(), "Size should be 2 after adding markers");

        // Remove last marker
        markers.remove(2);
        assertEquals(1, markers.size(), "Size should be 1 after removing last marker");
        assertTrue(markers.has(1), "First marker should still exist");
        assertFalse(markers.has(2), "Second marker should be removed");
    }

    @Test
    void testRemoveNonExistentMarker() {
        // Add a marker
        markers.add(1, new PointMarker(1, 1));
        int initialSize = markers.size();

        // Try to remove non-existent marker
        markers.remove(999); // ID that doesn't exist
        assertEquals(initialSize, markers.size(), "Size should not change when removing non-existent marker");
    }

    @Test
    void testSerializationDeserialization() throws IOException {
        // Add markers
        ObjectiveMarker marker1 = new PointMarker(5, 10);
        ObjectiveMarker marker2 = new ShapeMarker(15, 20);
        markers.add(1, marker1);
        markers.add(2, marker2);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        markers.write(dos);

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream dis = new DataInputStream(bais);

        MapMarkers deserialized = new MapMarkers();
        deserialized.read(dis);

        // Verify
        assertEquals(markers.size(), deserialized.size(), "Deserialized map should have same size");
        assertTrue(deserialized.has(1), "Deserialized map should have marker 1");
        assertTrue(deserialized.has(2), "Deserialized map should have marker 2");

        // Verify arrayIndex values were restored correctly
        ObjectiveMarker deserializedMarker1 = deserialized.get(1);
        ObjectiveMarker deserializedMarker2 = deserialized.get(2);
        assertEquals(0, deserializedMarker1.arrayIndex, "First marker should have arrayIndex 0");
        assertEquals(1, deserializedMarker2.arrayIndex, "Second marker should have arrayIndex 1");
    }

    @Test
    void testIteratorEmptyMap() {
        // Test iteration over empty map
        int count = 0;
        for (ObjectiveMarker marker : markers) {
            count++;
        }
        assertEquals(0, count, "Should not iterate over any markers in empty map");
    }
}
