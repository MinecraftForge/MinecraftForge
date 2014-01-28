#version 120

attribute vec4 Position;

uniform mat4 ProjMat;
uniform vec2 OutSize;

uniform float SubPixelShift;

varying vec2 texCoord;
varying vec4 posPos;

void main() {
    vec4 outPos = ProjMat * vec4(Position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);

    texCoord = Position.xy / OutSize;
    texCoord.y = 1.0 - texCoord.y;
    posPos.xy = texCoord.xy;
    posPos.zw = texCoord.xy - (1.0/OutSize * vec2(0.5 + SubPixelShift));
}
