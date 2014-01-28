#version 120

attribute vec4 Position;

uniform mat4 ProjMat;
uniform vec2 InSize;
uniform vec2 OutSize;
uniform vec2 ScreenSize;

varying vec2 texCoord;

void main(){
    vec4 outPos = ProjMat * vec4(Position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);

    vec2 inOutRatio = OutSize / InSize;
    vec2 inScreenRatio = ScreenSize / InSize;
    texCoord = Position.xy / OutSize;
    texCoord.x = texCoord.x * inOutRatio.x;
    texCoord.y = texCoord.y * inOutRatio.y;
    texCoord.y -= 1.0 - inScreenRatio.y;
}
