package org.example.engine.core.math;

public class Shape3DPlane implements Shape3D {

    public Vector3 normal;
    public float d;

    public Shape3DPlane() {
        this.normal = new Vector3(0,0,1);
        this.d = 0;
    }

    // a plane with normal = normal and distance from the origin = d
    public Shape3DPlane(Vector3 normal, float d) {
        this.normal = new Vector3(normal);
        this.normal.nor();
        this.d = d;
    }

    // a plane with a normal = normal and a point on the plane pointOnPlane
    public Shape3DPlane(Vector3 normal, Vector3 pointOnPlane) {
        this.normal = new Vector3(normal);
        this.normal.nor();
        this.d = -1 * Vector3.dot(normal, pointOnPlane);
    }

    // a plane with a,b and c points on the plane.
    public Shape3DPlane(Vector3 a, Vector3 b, Vector3 c) {
        this.normal = new Vector3();
        normal.set(a).sub(b).crs(b.x - c.x, b.y - c.y, b.z - c.z).nor();
        d = -1 * Vector3.dot(a, normal);
    }

    public void set(float nx, float ny, float nz, float d) {
        normal.set(nx, ny, nz);
        this.d = d;
    }

    public void set(final Shape3DPlane plane) {
        this.normal.set(plane.normal);
        this.d = plane.d;
    }

    public void set(Vector3 point1, Vector3 point2, Vector3 point3) {
        normal.set(point1).sub(point2).crs(point2.x - point3.x, point2.y - point3.y, point2.z - point3.z).nor();
        d = -1 * Vector3.dot(point1, normal);
    }

    public float distance(final Vector3 point) {
        return Vector3.dot(normal, point) + d;
    }


    public float distance(float x, float y, float z) {
        return normal.x * x + normal.y * y + normal.z * z + d;
    }

    public short getSide(final Vector3 point) {
        float distance = Vector3.dot(normal, point) + d;
        if (distance < 0) return -1;
        if (distance == 0) return 0;
        else return 1;
    }

    public short getSide(float x, float y, float z) {
        float distance = normal.x * x + normal.y * y + normal.z * z + d;
        if (distance < 0) return -1;
        if (distance == 0) return 0;
        else return 1;
    }

    public void set(Vector3 point, Vector3 normal) {
        this.normal.set(normal);
        d =  -1 * Vector3.dot(normal, point);
    }

    public void set(float pointX, float pointY, float pointZ, float norX, float norY, float norZ) {
        this.normal.set(norX, norY, norZ);
        d = -(pointX * norX + pointY * norY + pointZ * norZ);
    }

    public String toString () {
        return "<Plane: " + normal.toString() + " | " + d + ">";
    }

    @Override
    public boolean contains(float x, float y, float z) {
        return distance(x,y,z) == 0;
    }
}
