#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D OverlaySampler;

uniform vec2 InSize;

varying vec2 texCoord;

uniform float MosaicSize = 1.0;
uniform vec3 RedMatrix   = vec3(1.0, 0.0, 0.0);
uniform vec3 GreenMatrix = vec3(0.0, 1.0, 0.0);
uniform vec3 BlueMatrix  = vec3(0.0, 0.0, 1.0);

void main(){
    vec2 mosaicInSize = InSize / MosaicSize;
    vec2 fractPix = fract(texCoord * mosaicInSize) / mosaicInSize;
    
    vec4 baseTexel = texture2D(DiffuseSampler, texCoord - fractPix);
    float red = dot(baseTexel.rgb, RedMatrix);
    float green = dot(baseTexel.rgb, GreenMatrix);
    float blue = dot(baseTexel.rgb, BlueMatrix);
    
    vec4 overlayTexel = texture2D(OverlaySampler, vec2(texCoord.x, 1.0 - texCoord.y));
    gl_FragColor = mix(vec4(red, green, blue, baseTexel.a), overlayTexel, overlayTexel.a);
}
