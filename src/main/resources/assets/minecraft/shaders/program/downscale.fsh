#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

void main() {
    vec3 Texel0 = texture2D(DiffuseSampler, texCoord).rgb;
    vec3 Texel1 = texture2D(DiffuseSampler, texCoord + vec2(oneTexel.x, 0.0)).rgb;
    vec3 Texel2 = texture2D(DiffuseSampler, texCoord + vec2(0.0, oneTexel.y)).rgb;
    vec3 Texel3 = texture2D(DiffuseSampler, texCoord + oneTexel).rgb;
    
    gl_FragColor = vec4((Texel0 + Texel1 + Texel2 + Texel3) * 0.25, Texel0.a);
}
