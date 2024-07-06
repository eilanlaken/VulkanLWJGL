package org.example.engine.core.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;

import java.util.ArrayList;
import java.util.List;

class MathUtilsTest {

    @Test
    void random() {
    }

    @Test
    void testRandom() {
    }

    @Test
    void getAreaTriangle() {
        Assertions.assertEquals(0.5f, MathUtils.getAreaTriangle(0.0f,0.0f,1.0f,0.0f,0.0f,1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.5f, MathUtils.getAreaTriangle(1.0f,0.0f,0.0f,1.0f,1.0f,1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void normalizeAngleDeg() {
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleDeg(0.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleDeg(360.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.normalizeAngleDeg(1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(359.0f, MathUtils.normalizeAngleDeg(-1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(20.0f, MathUtils.normalizeAngleDeg(380.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(20.0f, MathUtils.normalizeAngleDeg(380.0f + 360.0f * 5), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void normalizeAngleRad() {
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleRad(0.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleRad(MathUtils.PI2), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.normalizeAngleRad(1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.1f, MathUtils.normalizeAngleRad(MathUtils.PI2 + 0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI2 - 0.1f, MathUtils.normalizeAngleRad(-0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.1f, MathUtils.normalizeAngleRad(MathUtils.PI2 * 3 + 0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    public void clampFloat() {
        float v1 = MathUtils.clampFloat(0.0f, -1.0f, 1.0f);
        Assertions.assertEquals(0.0f, v1, MathUtils.FLOAT_ROUNDING_ERROR);

        float v2 = MathUtils.clampFloat(-3.0f, -1.0f, 1.0f);
        Assertions.assertEquals(-1.0f, v2, MathUtils.FLOAT_ROUNDING_ERROR);

        float v3 = MathUtils.clampFloat(2.0f, -1.0f, 1.0f);
        Assertions.assertEquals(1.0f, v3, MathUtils.FLOAT_ROUNDING_ERROR);

        float v4 = MathUtils.clampFloat(0.0f, 1.0f, -1.0f);
        Assertions.assertEquals(0.0f, v4, MathUtils.FLOAT_ROUNDING_ERROR);

        float v5 = MathUtils.clampFloat(8.0f, -1.0f, 10.0f);
        Assertions.assertEquals(8.0f, v5, MathUtils.FLOAT_ROUNDING_ERROR);

        float v6 = MathUtils.clampFloat(4.0f, 3.0f, -1.0f);
        Assertions.assertEquals(3.0f, v6, MathUtils.FLOAT_ROUNDING_ERROR);

        float v7 = MathUtils.clampFloat(2.0f, 2.0f, 2.0f);
        Assertions.assertEquals(2.0f, v7, MathUtils.FLOAT_ROUNDING_ERROR);

        float v8 = MathUtils.clampFloat(Float.POSITIVE_INFINITY, -1, 1);
        Assertions.assertEquals(1.0f, v8, MathUtils.FLOAT_ROUNDING_ERROR);

        float v9 = MathUtils.clampFloat(Float.NEGATIVE_INFINITY, -1, 1);
        Assertions.assertEquals(-1.0f, v9, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void nextPowerOfTwo() {
    }

    @Test
    void atanUnchecked() {
    }

    @Test
    void atan2() {
    }

    @Test
    void areaTriangle() {
    }

    @Test
    void testAreaTriangle() {
    }

    @Test
    void max() {
    }

    @Test
    void testMax() {
    }

    @Test
    void sin() {
    }

    @Test
    void cos() {
    }

    @Test
    void sinDeg() {
    }

    @Test
    void cosDeg() {
    }

    @Test
    void tan() {
    }

    @Test
    void acos() {
    }

    @Test
    void asin() {
    }

    @Test
    void tanDeg() {
    }

    @Test
    void atan() {
    }

    @Test
    void asinDeg() {
    }

    @Test
    void acosDeg() {
    }

    @Test
    void atanDeg() {
    }

    @Test
    void isZero() {
    }

    @Test
    void testIsZero() {
    }

    @Test
    void isEqual() {
    }

    @Test
    void testIsEqual() {
    }

    @Test
    void log() {
    }

    @Test
    void testRandom1() {
    }

    @Test
    void testRandom2() {
    }

    @Test
    void testClamp4() {
    }

    @Test
    void testClamp5() {
    }

    @Test
    void testClamp6() {
    }

    @Test
    void testClamp7() {
    }

    @Test
    void testClamp8() {
    }

    @Test
    void testNextPowerOfTwo() {
    }

    @Test
    void testAtanUnchecked() {
    }

    @Test
    void testAtan2() {
    }

    @Test
    void testAreaTriangle1() {
    }

    @Test
    void testAreaTriangle2() {
    }

    @Test
    void testMax1() {
    }

    @Test
    void testMax2() {
    }

    @Test
    void min() {
    }

    @Test
    void testMin() {
    }

    @Test
    void intervalsOverlap() {
        Assertions.assertEquals(0.0f, MathUtils.intervalsOverlap(0.0f, 1.0f, 2.0f, 4.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.intervalsOverlap(9.0f, 8.0f, 4.0f, 2.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(0.0f, 4.0f, 1.0f, 3.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(3.0f, MathUtils.intervalsOverlap(1.0f, 5.0f, 2.0f, 6.5f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.2f, MathUtils.intervalsOverlap(-1.2f, 1.2f,0.0f, 1.2f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(2.0f, 4.0f, 2.0f, 4.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.intervalsOverlap(2.0f, 4.0f, 1.0f, 3.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(2.0f, -2.0f, 0.0f, 2.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(0.0f, -2.0f, 0.0f, -2.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.intervalsOverlap(-1.0f, 1.0f, 0.0f, -3.0f), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testSegmentsIntersection() {
        Vector2 a1 = new Vector2();
        Vector2 a2 = new Vector2();
        Vector2 b1 = new Vector2();
        Vector2 b2 = new Vector2();
        Vector2 out = new Vector2();

        a1.set(0, 0);
        a2.set(1, 1);
        b1.set(0, 1);
        b2.set(1, 0);
        boolean i1 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertTrue(i1);
        Assertions.assertEquals(0.5f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.5f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        a1.set(0, 0);
        a2.set(0, 2);
        b1.set(-4, 1);
        b2.set(5, 1);
        boolean i2 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertTrue(i2);
        Assertions.assertEquals(0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        a1.set(0, 0);
        a2.set(0, 0);
        b1.set(-4, 0);
        b2.set(5, 0);
        boolean i3 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertTrue(i3);
        Assertions.assertEquals(0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        a1.set(0, 0);
        a2.set(1, 0);
        b1.set(0, 1);
        b2.set(1, 1);
        boolean i4 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertFalse(i4);
        Assertions.assertEquals(Float.NaN, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(Float.NaN, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        a1.set(0, 0);
        a2.set(0, 0);
        b1.set(-4, 0);
        b2.set(5, 0);
        boolean i5 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertTrue(i5);
        Assertions.assertEquals(0, out.x);
        Assertions.assertEquals(0, out.y);

        a1.set(0, 0);
        a2.set(1, 0);
        b1.set(4, 0);
        b2.set(5, 0);
        boolean i6 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertFalse(i6);
        Assertions.assertEquals(Float.NaN, out.x);
        Assertions.assertEquals(Float.NaN, out.y);

        a1.set(0, 0);
        a2.set(2, 0);
        b1.set(1, 0);
        b2.set(5, 0);
        boolean i7 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertTrue(i7);
        Assertions.assertEquals(Float.NaN, out.x);
        Assertions.assertEquals(Float.NaN, out.y);

        a1.set(0, 0);
        a2.set(2, 0);
        b1.set(2, 0);
        b2.set(4, 0);
        boolean i8 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertTrue(i8);
        Assertions.assertEquals(2, out.x);
        Assertions.assertEquals(0, out.y);
    }

    @Test
    void pointOnSegment() {
        Vector2 p  = new Vector2();
        Vector2 a1 = new Vector2();
        Vector2 a2 = new Vector2();

        p.set(0.4f,0);
        a1.set(0,0);
        a2.set(1,0);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.4f,0);
        a1.set(0,0);
        a2.set(1,0);
        Assertions.assertFalse(MathUtils.pointOnSegment(p, a1, a2));

        p.set(0.5f,0.5f);
        a1.set(0,0);
        a2.set(1,1);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,1.0f);
        a1.set(0,0);
        a2.set(1,1);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(-0.5f,-0.5f);
        a1.set(-0.5f,-0.5f);
        a2.set(1,1);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(0.5f,0.6f);
        a1.set(0,0);
        a2.set(1,1);
        Assertions.assertFalse(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,0.0f);
        a1.set(0,0);
        a2.set(Float.POSITIVE_INFINITY, 0);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,0.0f);
        a1.set(0,0);
        a2.set(-Float.POSITIVE_INFINITY, 0);
        Assertions.assertFalse(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,1.0f);
        a1.set(0,0);
        a2.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,-1.0f);
        a1.set(0,0);
        a2.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Assertions.assertFalse(MathUtils.pointOnSegment(p, a1, a2));
    }

    @Test
    void testIsNumeric() {
        Assertions.assertTrue(MathUtils.isNumeric(4.0f));
        Assertions.assertTrue(MathUtils.isNumeric(Float.MIN_VALUE));
        Assertions.assertTrue(MathUtils.isNumeric(Float.MAX_VALUE));
        Assertions.assertFalse(MathUtils.isNumeric(4.0f / 0f));
        Assertions.assertFalse(MathUtils.isNumeric(Float.NaN));
        Assertions.assertFalse(MathUtils.isNumeric(Float.POSITIVE_INFINITY));
        Assertions.assertFalse(MathUtils.isNumeric(Float.NEGATIVE_INFINITY));
    }

    @Test
    void testSin() {

    }

    @Test
    void testCos() {
    }

    @Test
    void testSinDeg() {
    }

    @Test
    void testCosDeg() {
    }

    @Test
    void testTan() {
    }

    @Test
    void testAcos() {
    }

    @Test
    void testAsin() {
    }

    @Test
    void testTanDeg() {
    }

    @Test
    void testAtan() {
    }

    @Test
    void testAsinDeg() {
    }

    @Test
    void testAcosDeg() {
    }

    @Test
    void testAtanDeg() {
    }

    @Test
    void testIsZero1() {
    }

    @Test
    void testIsZero2() {
    }

    @Test
    void testIsEqual1() {
    }

    @Test
    void testIsEqual2() {
    }

    @Test
    void testLog() {
    }

    @Test
    void subtractPolygons() throws ParseException {
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        // Define polygons in set P (using WKT for simplicity)
        List<Geometry> polygonSetP = new ArrayList<>();
        polygonSetP.add(reader.read("POLYGON ((0 0, 4 0, 4 4, 0 4, 0 0))"));

        // Define polygons in set S (using WKT for simplicity)
        List<Geometry> polygonSetS = new ArrayList<>();
        polygonSetS.add(reader.read("POLYGON ((2 2, 6 2, 6 6, 2 6, 2 2))")); // Example polygon
        // Add more polygons to polygonSetS as needed

        // Union all polygons in set S
        Geometry unionOfS = CascadedPolygonUnion.union(polygonSetS);

        // Subtract S from each polygon in P
        List<Geometry> resultSetA = new ArrayList<>();
        for (Geometry p : polygonSetP) {
            Geometry result = p.difference(unionOfS);
            if (!result.isEmpty()) {
                resultSetA.add(result);
            }
        }

        // Output the resulting polygons
        for (Geometry result : resultSetA) {
            System.out.println(result);
        }
    }

    @Test
    void subtractPolygons2() {
        GeometryFactory geometryFactory = new GeometryFactory();

        // Define polygon P1 using arrays of coordinates
        Coordinate[] coordinatesP1 = new Coordinate[] {
                new Coordinate(0, 0),
                new Coordinate(4, 0),
                new Coordinate(4, 4),
                new Coordinate(0, 4),
                new Coordinate(0, 0) // Closed ring
        };
        Polygon polygonP1 = geometryFactory.createPolygon(coordinatesP1);

        // Define polygon S1 using arrays of coordinates
        Coordinate[] coordinatesS1 = new Coordinate[] {
                new Coordinate(2, 2),
                new Coordinate(6, 2),
                new Coordinate(6, 6),
                new Coordinate(2, 6),
                new Coordinate(2, 2) // Closed ring
        };
        Polygon polygonS1 = geometryFactory.createPolygon(coordinatesS1);

        // Add polygons to their respective sets
        List<Geometry> polygonSetP = new ArrayList<>();
        polygonSetP.add(polygonP1);

        List<Geometry> polygonSetS = new ArrayList<>();
        polygonSetS.add(polygonS1);

        // Union all polygons in set S
        Geometry unionOfS = CascadedPolygonUnion.union(polygonSetS);

        // Subtract S from each polygon in P
        List<Geometry> resultSetA = new ArrayList<>();
        for (Geometry p : polygonSetP) {
            Geometry result = p.difference(unionOfS);
            if (!result.isEmpty()) {
                resultSetA.add(result);
            }
        }

        // Output the resulting polygons
        for (Geometry result : resultSetA) {
            System.out.println(result);
        }
    }

}