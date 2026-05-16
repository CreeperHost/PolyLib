#version 330

layout(std140) uniform BezierWire {
    vec2 p0;
    vec2 p1;
    vec2 p2;
    vec2 p3;
    vec2 size;
};

layout(std140) uniform Projection {
    mat4 ProjMat;
};

in vec4 vertexColor;

out vec4 fragColor;

float length2(in vec2 v) { return dot(v, v); }

float sdSegmentSq(in vec2 p, in vec2 a, in vec2 b) {
    vec2 pa = p - a, ba = b - a;
    float h = clamp(dot(pa, ba) / dot(ba, ba), 0.0, 1.0);
    return length2(pa - ba * h);
}

// Unsigned squared distance from pos to cubic bezier p0..p3.
// Evaluated by walking 120 sub-segments on GPU.
float udBezierSq(vec2 pos) {
    const int kNum = 120;
    float best = 1e10;
    vec2 a = p0;
    for (int i = 1; i < kNum; i++) {
        float t = float(i) / float(kNum - 1);
        float s = 1.0 - t;
        vec2 b = p0*(s*s*s) + p1*(3.0*s*s*t) + p2*(3.0*s*t*t) + p3*(t*t*t);
        float d = sdSegmentSq(pos, a, b);
        if (d < best) best = d;
        a = b;
    }
    return best;
}

void main() {
    // PiP y=0 is at the top; gl_FragCoord.y=0 is at the bottom — flip y.
    vec2 fragPos = vec2(gl_FragCoord.x, size.y - gl_FragCoord.y) / size;
    float distSq = udBezierSq(fragPos);
    float alpha = 1.0 - smoothstep(0.0, 0.00002, distSq);
    if (alpha < 0.0002) discard;
    fragColor = vec4(vertexColor.rgb, vertexColor.a * alpha);
}
