#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D OutlineSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

void main(){
    vec4 diffuseTexel = texture2D(DiffuseSampler, texCoord);
    vec4 outlineTexel = texture2D(OutlineSampler, texCoord);
    gl_FragColor = vec4(diffuseTexel.rgb + diffuseTexel.rgb * outlineTexel.rgb * vec3(0.75), diffuseTexel.a);
}
