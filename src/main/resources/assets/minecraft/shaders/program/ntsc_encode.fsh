#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

const float Pi2 = 6.283185307;

const vec4 A2 = vec4(1.0);
const vec4 B = vec4(0.5);
const float P = 1.0;
const float CCFrequency = 3.59754545;
const float ScanTime = 52.6;
const float Pi2ScanTime = Pi2 * ScanTime;
const vec4 YTransform = vec4(0.299, 0.587, 0.114, 0.0);
const vec4 ITransform = vec4(0.595716, -0.274453, -0.321263, 0.0);
const vec4 QTransform = vec4(0.211456, -0.522591, 0.31135, 0.0);
const vec4 MinC = vec4(-1.1183);
const vec4 InvCRange = vec4(1.0 / 3.2366);

void main() {
    vec2 InverseP = vec2(P, 0.0) * oneTexel;
    
    // UVs for four linearly-interpolated samples spread 0.25 texels apart
    vec2 C0 = texCoord;
    vec2 C1 = texCoord + InverseP * 0.25;
    vec2 C2 = texCoord + InverseP * 0.50;
    vec2 C3 = texCoord + InverseP * 0.75;
    vec4 Cx = vec4(C0.x, C1.x, C2.x, C3.x);
    vec4 Cy = vec4(C0.y, C1.y, C2.y, C3.y);
    
    vec4 Texel0 = texture2D(DiffuseSampler, C0);
    vec4 Texel1 = texture2D(DiffuseSampler, C1);
    vec4 Texel2 = texture2D(DiffuseSampler, C2);
    vec4 Texel3 = texture2D(DiffuseSampler, C3);

    // Calculate the expected time of the sample.
    vec4 T = A2 * Cy * vec4(InSize.y) + B + Cx;
    vec4 W = vec4(Pi2ScanTime * CCFrequency);
    vec4 TW = T * W;
    vec4 Y = vec4(dot(Texel0, YTransform), dot(Texel1, YTransform), dot(Texel2, YTransform), dot(Texel3, YTransform));
    vec4 I = vec4(dot(Texel0, ITransform), dot(Texel1, ITransform), dot(Texel2, ITransform), dot(Texel3, ITransform));
    vec4 Q = vec4(dot(Texel0, QTransform), dot(Texel1, QTransform), dot(Texel2, QTransform), dot(Texel3, QTransform));
    
    vec4 Encoded = Y + I * cos(TW) + Q * sin(TW);
    gl_FragColor = (Encoded - MinC) * InvCRange;
}
