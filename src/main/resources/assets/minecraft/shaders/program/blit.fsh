#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

void main(){
    gl_FragColor = texture2D(DiffuseSampler, texCoord);
}
