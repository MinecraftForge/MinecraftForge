#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

uniform float InverseAmount;

void main(){
    vec4 diffuseColor = texture2D(DiffuseSampler, texCoord);
    vec4 invertColor = 1.0 - diffuseColor;
    vec4 outColor = mix(diffuseColor, invertColor, InverseAmount);
    gl_FragColor = vec4(outColor.rgb, diffuseColor.a);
}
