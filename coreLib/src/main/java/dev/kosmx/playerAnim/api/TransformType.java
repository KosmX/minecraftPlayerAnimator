package dev.kosmx.playerAnim.api;

public enum TransformType {
    /**
     * The part is shifted in 3d space into the direction
     */
    POSITION,
    /**
     * The part is rotated in 3D space using Euler angles
     */
    ROTATION,
    /**
     * Bend the part, the vector should look like this: {bend planes rotation 0-2&pi;, bend value, not defined}
     */
    BEND
}
