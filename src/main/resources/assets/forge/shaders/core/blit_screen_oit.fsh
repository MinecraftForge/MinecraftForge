#version 150 core

out vec4 frag;

uniform sampler2D accum;
uniform sampler2D reveal;

const float EPSILON = 0.00001f;
bool isApproximatelyEqual(float a, float b)
{
    return abs(a - b) <= (abs(a) < abs(b) ? abs(b) : abs(a)) * EPSILON;
}

float max3(vec3 v)
{
    return max(max(v.x, v.y), v.z);
}

void main()
{
    ivec2 coords = ivec2(gl_FragCoord.xy);
    vec4 accumulation = texelFetch(accum, coords, 0);
    float revealage = accumulation.a;

    if (isApproximatelyEqual(revealage, 1f)) {
        discard;
    }

    accumulation.a = texelFetch(reveal, coords, 0).r;

    if (isinf(max3(abs(accumulation.rgb))))
    accumulation.rgb = vec3(accumulation.a);

    frag = vec4(accumulation.rgb / clamp(accumulation.a, 1e-4, 5e4), revealage);
}