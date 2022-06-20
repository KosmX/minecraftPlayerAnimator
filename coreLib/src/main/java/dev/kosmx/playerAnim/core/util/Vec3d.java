package dev.kosmx.playerAnim.core.util;

/**
 * Three-dimensional double vector
 */
public class Vec3d extends Vector3<Double> {

    public Vec3d(Double x, Double y, Double z) {
        super(x, y, z);
    }

    public double squaredDistanceTo(Vec3d vec3d){
        double a = this.x - vec3d.x;
        double b = this.y - vec3d.y;
        double c = this.z - vec3d.z;
        return a*a + b*b + c*c;
    }

    /**
     * Scale the vector
     * @param scalar scalar
     * @return scaled vector
     */
    public Vec3d scale(double scalar) {
        return new Vec3d(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    /**
     * Add two vectors
     * @param other other vector
     * @return sum vector
     */
    public Vec3d add(Vec3d other) {
        return new Vec3d(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
    }

    /**
     * Dot product with other vector
     * @param other rhs operand
     * @return v
     */
    public double dotProduct(Vec3d other) {
        return this.getX() * other.getX() + this.getY() * other.getY() + this.getZ() * other.getZ();
    }

    /**
     * Cross product
     * @param other rhs operand
     * @return v
     */
    public Vec3d crossProduct(Vec3d other) {
        return new Vec3d(
                this.getY()*other.getZ() - this.getZ()*other.getY(),
                this.getZ()*other.getX() - this.getX()*other.getZ(),
                this.getX()*other.getY() - this.getY()*other.getX()
        );
    }

    /**
     * Subtract a vector from this
     * @param rhs rhs operand
     * @return v
     */
    public Vec3d subtract(Vec3d rhs) {
        //You could have guessed what will happen here.
        return add(rhs.scale(-1));
    }

    public double distanceTo(Vec3d vec3d){
        return Math.sqrt(squaredDistanceTo(vec3d));
    }
}
