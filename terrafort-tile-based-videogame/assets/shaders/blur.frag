uniform sampler2D u_texture;
varying vec4 v_color;
varying  vec2 v_texCoords;

void main() {
	vec4 sum = vec4(0.0);
	float blur = 1.0f;

	for(float i = -4.0f; i < 4.0f; i += 1.0f) {
		sum += texture2D(u_texture, vec2(v_texCoords.x - i * blur, v_texCoords.y - i * blur)) * 0.06;
	}

    gl_FragColor = v_color * vec4(sum.rgb, 1.0);
}
