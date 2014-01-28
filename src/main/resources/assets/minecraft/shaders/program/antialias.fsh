#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

void main(){
    vec4 c  = texture2D (DiffuseSampler, texCoord);
    vec4 u1 = texture2D (DiffuseSampler, texCoord + vec2 (              0.0, -oneTexel.y      ));
    vec4 u2 = texture2D (DiffuseSampler, texCoord + vec2 (              0.0, -oneTexel.y * 2.0));
    vec4 d1 = texture2D (DiffuseSampler, texCoord + vec2 (              0.0,  oneTexel.y      ));
    vec4 d2 = texture2D (DiffuseSampler, texCoord + vec2 (              0.0,  oneTexel.y * 2.0));
    vec4 l1 = texture2D (DiffuseSampler, texCoord + vec2 (-oneTexel.x,                     0.0));
    vec4 l2 = texture2D (DiffuseSampler, texCoord + vec2 (-oneTexel.x * 2.0,               0.0));
    vec4 r1 = texture2D (DiffuseSampler, texCoord + vec2 ( oneTexel.x,                     0.0));
    vec4 r2 = texture2D (DiffuseSampler, texCoord + vec2 ( oneTexel.x * 2.0,               0.0));
    
    vec4 v1 = mix (c, mix (l1, l2, 0.667), 0.75);
    vec4 v2 = mix (c, mix (r1, r2, 0.667), 0.75);
    vec4 v3 = mix (c, mix (u1, u2, 0.667), 0.75);
    vec4 v4 = mix (c, mix (d1, d2, 0.667), 0.75);

    vec4 v5 = mix (v1, v2, 0.5);
    vec4 v6 = mix (v3, v4, 0.5);

    vec4 color = mix (v5, v6, 0.5);
    gl_FragColor = vec4(color.rgb, c.a);
}
