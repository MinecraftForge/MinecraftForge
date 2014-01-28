#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float Resolution = 4.0;
uniform float Saturation = 1.5;
uniform float MosaicSize = 8.0;

void main() {
    vec2 mosaicInSize = InSize / MosaicSize;
    vec2 fractPix = fract(texCoord * mosaicInSize) / mosaicInSize;
    
    vec4 baseTexel = texture2D(DiffuseSampler, texCoord - fractPix);
    
    baseTexel = baseTexel - fract(baseTexel * Resolution) / Resolution;
    float luma = dot(baseTexel.rgb, vec3(0.3, 0.59, 0.11));
    vec3 chroma = (baseTexel.rgb - luma) * Saturation;
    baseTexel = vec4(luma + chroma, baseTexel.a);

    gl_FragColor = baseTexel;
}
