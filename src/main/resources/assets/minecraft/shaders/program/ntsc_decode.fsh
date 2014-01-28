#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D BaseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

const vec4 Zero = vec4(0.0);
const vec4 One = vec4(1.0);

const float Pi = 3.1415926535;
const float Pi2 = 6.283185307;

const vec4 A2 = vec4(1.0);
const vec4 B = vec4(0.5);
const float P = 1.0;
const float CCFrequency = 3.59754545;
const float NotchWidth = 2.0;
const float NotchUpperFrequency = 3.59754545 + NotchWidth;
const float NotchLowerFrequency = 3.59754545 - NotchWidth;
const float YFrequency = 6.0;
const float IFrequency = 1.2;
const float QFrequency = 0.6;
const float ScanTime = 52.6;
const vec3 YIQ2R = vec3(1.0, 0.956, 0.621);
const vec3 YIQ2G = vec3(1.0, -0.272, -0.647);
const vec3 YIQ2B = vec3(1.0, -1.106, 1.703);
const vec4 MinC = vec4(-1.1183);
const vec4 CRange = vec4(3.2366);
const float Pi2Length = Pi2 / 83.0;
const vec4 NotchOffset = vec4(0.0, 1.0, 2.0, 3.0);
const vec4 W = vec4(Pi2 * CCFrequency * ScanTime);

void main() {
    vec4 YAccum = Zero;
    vec4 IAccum = Zero;
    vec4 QAccum = Zero;
    float QuadXSize = InSize.x * 4.0;
    float TimePerSample = ScanTime / QuadXSize;
    
    // Frequency cutoffs for the individual portions of the signal that we extract.
    // Y1 and Y2 are the positive and negative frequency limits of the notch filter on Y.
    // Y3 is the center of the frequency response of the Y filter.
    // I is the center of the frequency response of the I filter.
    // Q is the center of the frequency response of the Q filter.
    float Fc_y1 = NotchLowerFrequency * TimePerSample;
    float Fc_y2 = NotchUpperFrequency * TimePerSample;
    float Fc_y3 = YFrequency * TimePerSample;
    float Fc_i = IFrequency * TimePerSample;
    float Fc_q = QFrequency * TimePerSample;
    float Pi2Fc_y1 = Fc_y1 * Pi2;
    float Pi2Fc_y2 = Fc_y2 * Pi2;
    float Pi2Fc_y3 = Fc_y3 * Pi2;
    float Pi2Fc_i = Fc_i * Pi2;
    float Pi2Fc_q = Fc_q * Pi2;
    float Fc_y1_2 = Fc_y1 * 2.0;
    float Fc_y2_2 = Fc_y2 * 2.0;
    float Fc_y3_2 = Fc_y3 * 2.0;
    float Fc_i_2 = Fc_i * 2.0;
    float Fc_q_2 = Fc_q * 2.0;
    vec4 CoordY = vec4(texCoord.y);
    
    vec4 BaseTexel = texture2D(DiffuseSampler, texCoord);
    // 83 composite samples wide, 4 composite pixels per texel
    for (float n = -41.0; n < 42.0; n += 4.0)
    {
        vec4 n4 = n + NotchOffset;
        vec4 CoordX = texCoord.x + oneTexel.x * n4 * 0.25;
        vec2 TexCoord = vec2(CoordX.x, CoordY.y);
        vec4 C = texture2D(DiffuseSampler, TexCoord) * CRange + MinC;
        vec4 WT = W * (CoordX + A2 * CoordY * InSize.y + B);
        vec4 Cosine = 0.54 + 0.46 * cos(Pi2Length * n4);
        
        vec4 SincYIn1 = Pi2Fc_y1 * n4;
        vec4 SincYIn2 = Pi2Fc_y2 * n4;
        vec4 SincYIn3 = Pi2Fc_y3 * n4;
        vec4 SincY1 = sin(SincYIn1) / SincYIn1;
        vec4 SincY2 = sin(SincYIn2) / SincYIn2;
        vec4 SincY3 = sin(SincYIn3) / SincYIn3;
        
        // These zero-checks could be made more efficient, but we are trying to support
        // downlevel GLSL
        if(SincYIn1.x == 0.0) SincY1.x = 1.0;
        if(SincYIn1.y == 0.0) SincY1.y = 1.0;
        if(SincYIn1.z == 0.0) SincY1.z = 1.0;
        if(SincYIn1.w == 0.0) SincY1.w = 1.0;
        if(SincYIn2.x == 0.0) SincY2.x = 1.0;
        if(SincYIn2.y == 0.0) SincY2.y = 1.0;
        if(SincYIn2.z == 0.0) SincY2.z = 1.0;
        if(SincYIn2.w == 0.0) SincY2.w = 1.0;
        if(SincYIn3.x == 0.0) SincY3.x = 1.0;
        if(SincYIn3.y == 0.0) SincY3.y = 1.0;
        if(SincYIn3.z == 0.0) SincY3.z = 1.0;
        if(SincYIn3.w == 0.0) SincY3.w = 1.0;
        vec4 IdealY = (Fc_y1_2 * SincY1 - Fc_y2_2 * SincY2) + Fc_y3_2 * SincY3;
        vec4 FilterY = Cosine * IdealY;
        
        vec4 SincIIn = Pi2Fc_i * n4;
        vec4 SincI = sin(SincIIn) / SincIIn;
        if(SincIIn.x == 0.0) SincI.x = 1.0;
        if(SincIIn.y == 0.0) SincI.y = 1.0;
        if(SincIIn.z == 0.0) SincI.z = 1.0;
        if(SincIIn.w == 0.0) SincI.w = 1.0;
        vec4 IdealI = Fc_i_2 * SincI;
        vec4 FilterI = Cosine * IdealI;

        vec4 SincQIn = Pi2Fc_q * n4;
        vec4 SincQ = sin(SincQIn) / SincQIn;
        if(SincQIn.x == 0.0) SincQ.x = 1.0;
        if(SincQIn.y == 0.0) SincQ.y = 1.0;
        if(SincQIn.z == 0.0) SincQ.z = 1.0;
        if(SincQIn.w == 0.0) SincQ.w = 1.0;
        vec4 IdealQ = Fc_q_2 * SincQ;
        vec4 FilterQ = Cosine * IdealQ;
        
        YAccum += C * FilterY;
        IAccum += C * cos(WT) * FilterI;
        QAccum += C * sin(WT) * FilterQ;
    }
    
    float Y = dot(YAccum, One);
    float I = dot(IAccum, One) * 2.0;
    float Q = dot(QAccum, One) * 2.0;
    
    vec3 YIQ = vec3(Y, I, Q);
    vec3 OutRGB = vec3(dot(YIQ, YIQ2R), dot(YIQ, YIQ2G), dot(YIQ, YIQ2B));
    
    gl_FragColor = vec4(OutRGB, BaseTexel.a);
}
