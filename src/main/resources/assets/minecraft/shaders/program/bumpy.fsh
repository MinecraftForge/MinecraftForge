#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

void main(){
    vec4 c = texture2D(DiffuseSampler, texCoord);
    vec4 u = texture2D(DiffuseSampler, texCoord + vec2(        0.0, -oneTexel.y));
    vec4 d = texture2D(DiffuseSampler, texCoord + vec2(        0.0,  oneTexel.y));
    vec4 l = texture2D(DiffuseSampler, texCoord + vec2(-oneTexel.x,         0.0));
    vec4 r = texture2D(DiffuseSampler, texCoord + vec2( oneTexel.x,         0.0));

    vec4 nc = normalize(c);
    vec4 nu = normalize(u);
    vec4 nd = normalize(d);
    vec4 nl = normalize(l);
    vec4 nr = normalize(r);

    float du = dot(nc, nu);
    float dd = dot(nc, nd);
    float dl = dot(nc, nl);
    float dr = dot(nc, nr);

    float i = 64.0;

    float f = 1.0;
    f += (du * i) - (dd * i);
    f += (dr * i) - (dl * i);

    vec4 color = c * clamp(f, 0.5, 2);
    gl_FragColor = vec4(color.rgb, c.a);
}
