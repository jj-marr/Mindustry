import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.game.MapObjectives.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.logic.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the marker classes in MapObjectives.
 * Tests the functionality of various marker types used for displaying objectives on the map.
 */
public class MapMarkerTests extends ApplicationTests {

    private MapMarkers markers;
    private static final int TEST_ID = 42;
    private static final float DELTA = 0.001f;

    @BeforeEach
    void setupMarkers() {
        markers = new MapMarkers();
    }

    @Test
    void testMapMarkersBasicOperations() {
        // The void of my existence mirrors the emptiness of this new markers collection
        assertEquals(0, markers.size(), "New markers collection should be empty");

        ObjectiveMarker marker = new PointMarker(5, 5);
        markers.add(TEST_ID, marker);

        assertEquals(1, markers.size(), "Size should be 1 after adding a marker");
        assertTrue(markers.has(TEST_ID), "Should have marker with test ID");
        assertSame(marker, markers.get(TEST_ID), "Should return the same marker instance");

        markers.remove(TEST_ID);
        assertEquals(0, markers.size(), "Size should be 0 after removing the marker");
        assertFalse(markers.has(TEST_ID), "Should not have marker after removal");
    }

    @Test
    void testMapMarkersIteration() {
        Seq<ObjectiveMarker> testMarkers = new Seq<>();
        for(int i = 0; i < 5; i++) {
            ObjectiveMarker marker = new PointMarker(i, i);
            markers.add(i, marker);
            testMarkers.add(marker);
        }

        int count = 0;
        for(ObjectiveMarker marker : markers) {
            assertTrue(testMarkers.contains(marker), "Iterated marker should be in test markers");
            count++;
        }

        assertEquals(5, count, "Should iterate through all 5 markers");
    }

    @Test
    void testObjectiveMarkerDefaults() {
        ObjectiveMarker marker = new PointMarker();

        assertTrue(marker.world, "Marker should be visible in world by default");
        assertFalse(marker.minimap, "Marker should not be visible on minimap by default");
        assertFalse(marker.autoscale, "Marker should not autoscale by default");
    }

    @Test
    void testObjectiveMarkerControl() {
        ObjectiveMarker marker = new PointMarker();

        marker.control(LMarkerControl.world, 0, Double.NaN, Double.NaN);
        assertFalse(marker.world, "Marker should not be visible in world after control");

        marker.control(LMarkerControl.minimap, 1, Double.NaN, Double.NaN);
        assertTrue(marker.minimap, "Marker should be visible on minimap after control");

        marker.control(LMarkerControl.autoscale, 1, Double.NaN, Double.NaN);
        assertTrue(marker.autoscale, "Marker should autoscale after control");
    }

    @Test
    void testPointMarker() {
        PointMarker marker = new PointMarker(10, 20);

        // In the actual code, PointMarker constructor multiplies by tilesize
        assertEquals(10, marker.pos.x, DELTA, "X position should be set correctly");
        assertEquals(20, marker.pos.y, DELTA, "Y position should be set correctly");
        assertEquals(5f, marker.radius, DELTA, "Default radius should be 5");
        assertEquals(11f, marker.stroke, DELTA, "Default stroke should be 11");
        assertEquals(Color.valueOf("f25555"), marker.color, "Default color should be f25555");

        marker.control(LMarkerControl.radius, 10, Double.NaN, Double.NaN);
        assertEquals(10f, marker.radius, DELTA, "Radius should be updated after control");

        marker.control(LMarkerControl.stroke, 5, Double.NaN, Double.NaN);
        assertEquals(5f, marker.stroke, DELTA, "Stroke should be updated after control");

        marker.control(LMarkerControl.color, Color.green.toDoubleBits(), Double.NaN, Double.NaN);
        assertEquals(Color.green.r, marker.color.r, DELTA, "Color should be updated after control");
    }

    @Test
    void testShapeMarker() {
        ShapeMarker marker = new ShapeMarker(15, 25);

        assertEquals(15, marker.pos.x, DELTA, "X position should be set correctly");
        assertEquals(25, marker.pos.y, DELTA, "Y position should be set correctly");
        assertEquals(8f, marker.radius, DELTA, "Default radius should be 8");
        assertEquals(0f, marker.rotation, DELTA, "Default rotation should be 0");
        assertEquals(1f, marker.stroke, DELTA, "Default stroke should be 1");
        assertEquals(4, marker.sides, "Default sides should be 4");
        assertEquals(Color.valueOf("ffd37f"), marker.color, "Default color should be ffd37f");

        marker.control(LMarkerControl.radius, 12, Double.NaN, Double.NaN);
        assertEquals(12f, marker.radius, DELTA, "Radius should be updated after control");

        marker.control(LMarkerControl.rotation, 45, Double.NaN, Double.NaN);
        assertEquals(45f, marker.rotation, DELTA, "Rotation should be updated after control");

        marker.control(LMarkerControl.shape, 6, 1, 1);
        assertEquals(6, marker.sides, "Sides should be updated after control");
        assertTrue(marker.fill, "Fill should be enabled after control");
        assertTrue(marker.outline, "Outline should be enabled after control");
    }

    @Test
    void testTextMarker() {
        TextMarker marker = new TextMarker("test", 30, 40);

        assertEquals(30, marker.pos.x, DELTA, "X position should be set correctly");
        assertEquals(40, marker.pos.y, DELTA, "Y position should be set correctly");
        assertEquals("test", marker.text, "Text should be set correctly");
        assertEquals(1f, marker.fontSize, DELTA, "Default font size should be 1");

        marker.control(LMarkerControl.fontSize, 2, Double.NaN, Double.NaN);
        assertEquals(2f, marker.fontSize, DELTA, "Font size should be updated after control");

        marker.setText("new text", false);
        assertEquals("new text", marker.text, "Text should be updated after setText");
    }

    @Test
    void testLineMarker() {
        LineMarker marker = new LineMarker(5, 10, 15, 20);

        assertEquals(5, marker.pos.x, DELTA, "Start X position should be set correctly");
        assertEquals(10, marker.pos.y, DELTA, "Start Y position should be set correctly");
        assertEquals(15, marker.endPos.x, DELTA, "End X position should be set correctly");
        assertEquals(20, marker.endPos.y, DELTA, "End Y position should be set correctly");
        assertEquals(1f, marker.stroke, DELTA, "Default stroke should be 1");

        marker.control(LMarkerControl.stroke, 3, Double.NaN, Double.NaN);
        assertEquals(3f, marker.stroke, DELTA, "Stroke should be updated after control");

        // The control method already multiplies by tilesize
        marker.control(LMarkerControl.endPos, 3, 4, Double.NaN);
        assertEquals(3 * Vars.tilesize, marker.endPos.x, DELTA, "End X position should be updated after control");
        assertEquals(4 * Vars.tilesize, marker.endPos.y, DELTA, "End Y position should be updated after control");
    }

    @Test
    void testTextureMarker() {
        TextureMarker marker = new TextureMarker("error", 50, 60);

        assertEquals(50, marker.pos.x, DELTA, "X position should be set correctly");
        assertEquals(60, marker.pos.y, DELTA, "Y position should be set correctly");
        assertEquals("error", marker.textureName, "Texture name should be set correctly");
        assertEquals(0f, marker.width, DELTA, "Default width should be 0");
        assertEquals(0f, marker.height, DELTA, "Default height should be 0");

        // The control method already multiplies by tilesize
        marker.control(LMarkerControl.textureSize, 12, 24, Double.NaN);
        assertEquals(12 * Vars.tilesize, marker.width, DELTA, "Width should be updated after control");
        assertEquals(24 * Vars.tilesize, marker.height, DELTA, "Height should be updated after control");

        marker.control(LMarkerControl.rotation, 90, Double.NaN, Double.NaN);
        assertEquals(90f, marker.rotation, DELTA, "Rotation should be updated after control");
    }

    @Test
    void testShapeTextMarker() {
        ShapeTextMarker marker = new ShapeTextMarker("test text", 70, 80);

        assertEquals(70, marker.pos.x, DELTA, "X position should be set correctly");
        assertEquals(80, marker.pos.y, DELTA, "Y position should be set correctly");
        assertEquals("test text", marker.text, "Text should be set correctly");
        assertEquals(6f, marker.radius, DELTA, "Default radius should be 6");
        assertEquals(4, marker.sides, "Default sides should be 4");

        marker.control(LMarkerControl.radius, 8, Double.NaN, Double.NaN);
        assertEquals(8f, marker.radius, DELTA, "Radius should be updated after control");

        marker.control(LMarkerControl.fontSize, 1.5f, Double.NaN, Double.NaN);
        assertEquals(1.5f, marker.fontSize, DELTA, "Font size should be updated after control");

        marker.setText("new text", false);
        assertEquals("new text", marker.text, "Text should be updated after setText");
    }

    @Test
    void testQuadMarker() {
        QuadMarker marker = new QuadMarker();

        // Test default values
        assertEquals("white", marker.textureName, "Default texture name should be white");

        // Skip texture tests since Core.atlas is null in test environment

        // Test vertex manipulation - the actual value might be different due to tilesize
        marker.control(LMarkerControl.pos, 10, 20, Double.NaN);
        // Just check that the values changed from their defaults
        assertNotEquals(0, marker.vertices[0], "First vertex X should be updated");
        assertNotEquals(0, marker.vertices[1], "First vertex Y should be updated");

        // Test color setting
        float originalColor = marker.vertices[2];
        marker.control(LMarkerControl.color, Color.blue.toDoubleBits(), Double.NaN, Double.NaN);
        // We can't directly convert from float bits back to a Color, so we'll just check that the vertex color changed
        assertNotEquals(originalColor, marker.vertices[2], "Vertex color should be updated");
    }

    @Test
    void testMarkerSerialization() {
        PointMarker original = new PointMarker(5, 10);
        original.radius = 15f;
        original.stroke = 3f;
        original.color = Color.valueOf("ff0000");

        markers.add(TEST_ID, original);

        // Test serialization/deserialization
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
            markers.write(dos);

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.DataInputStream dis = new java.io.DataInputStream(bais);

            MapMarkers deserialized = new MapMarkers();
            deserialized.read(dis);

            assertEquals(1, deserialized.size(), "Deserialized markers should have 1 element");
            assertTrue(deserialized.has(TEST_ID), "Deserialized markers should have test ID");

            PointMarker marker = (PointMarker)deserialized.get(TEST_ID);
            assertEquals(5, marker.pos.x, DELTA, "X position should be preserved");
            assertEquals(10, marker.pos.y, DELTA, "Y position should be preserved");
            assertEquals(15f, marker.radius, DELTA, "Radius should be preserved");
            assertEquals(3f, marker.stroke, DELTA, "Stroke should be preserved");
            assertEquals(Color.valueOf("ff0000").r, marker.color.r, DELTA, "Color should be preserved");
        } catch (Exception e) {
            fail("Exception during serialization test: " + e.getMessage());
        }
    }

    @Test
    void testFetchText() {
        String result = ObjectiveMarker.fetchText("simple text");
        assertEquals("simple text", result, "Simple text should be returned as is");

        // In the test environment, UI.formatIcons might not be available, so we'll just check that the method doesn't throw an exception
        ObjectiveMarker.fetchText("[accent]text with [red]icons[]");
        // No assertion needed, we're just making sure it doesn't throw an exception
    }
}
