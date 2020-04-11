package com.example.oldlib.old;

public interface SoftLightFilterCreator {
    GPUImageFilter createSoftLightFilter(float[] textureCoor, float[] vertexCoor);
}
