#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform float Radius;

void main(){
    vec4 c  = texture2D(DiffuseSampler, texCoord);
    vec4 maxVal = c;
    for(float u = 0.0; u <= Radius; u += 1.0) {
        for(float v = 0.0; v <= Radius; v += 1.0) {
            float weight = (((sqrt(u * u + v * v) / (Radius)) > 1.0) ? 0.0 : 1.0);
            
            vec4 s0 = texture2D(DiffuseSampler, texCoord + vec2(-u * oneTexel.x, -v * oneTexel.y));
            vec4 s1 = texture2D(DiffuseSampler, texCoord + vec2( u * oneTexel.x,  v * oneTexel.y));
            vec4 s2 = texture2D(DiffuseSampler, texCoord + vec2(-u * oneTexel.x,  v * oneTexel.y));
            vec4 s3 = texture2D(DiffuseSampler, texCoord + vec2( u * oneTexel.x, -v * oneTexel.y));
            
            vec4 o0 = max(s0, s1);
            vec4 o1 = max(s2, s3);
            vec4 tempMax = max(o0, o1);
            maxVal = mix(maxVal, max(maxVal, tempMax), weight);
        }
    }

    gl_FragColor = vec4(maxVal.rgb, c.a);
}
