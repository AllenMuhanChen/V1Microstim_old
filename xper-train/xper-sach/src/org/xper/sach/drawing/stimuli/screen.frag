///varying vec4 vertColor;

#version 120

varying vec2 uv;
uniform float xCenter = 0.0;
uniform float yCenter = 0.0;

const int MaxNumHoles = 10;
const int NumSpecsPerHole = 4;  // x1, y1, innerRad1, outerRad1 ...


uniform float specs[MaxNumHoles * NumSpecsPerHole];
uniform float marginWidth;
uniform float left; 
uniform float right;
uniform float  top;
uniform float bottom;
uniform int numHoles;
uniform float alphaGain;

float smootherstep(float edge0, float edge1, float x)
{
    // Scale, and clamp x to 0..1 range
    x = clamp((x - edge0)/(edge1 - edge0), 0.0, 1.0);
    // Evaluate polynomial
    return x*x*x*(x*(x*6 - 15) + 10);
}


void main() {

	bool fragIsColored = false;
//    float border = 3.1;
//    float radius = 20.0;
    float alpha = 1.0;
	  
    float dist = 0.0;
    float outerRadius = 0.0;
    float innerRadius = 0.0;
    float x = 0.0;
    float y = 0.0;
    
    // occluder color
    float r = 0.25;
    float g = r;
    float b = r;
    
    
    int ndx = 0;
    vec2 m;
    
    // define corners
    vec2 lowerLeft = vec2(left, bottom);
    vec2 upperLeft = vec2(left, top);
    vec2 lowerRight = vec2(right, bottom);
    vec2 upperRight = vec2(right, top);
    
    gl_FragColor = vec4(r, g, b, alpha * alphaGain);
      
	fragIsColored = false;  

    // for each spec tuple
    for(int i = 0; i < numHoles; i++){
        ndx = i * NumSpecsPerHole;
        x = specs[ndx + 0];
        y = specs[ndx + 1];
        innerRadius = specs[ndx + 2];
        outerRadius = specs[ndx + 3];  
              
        m = uv - vec2(x, y);
        dist = sqrt(m.x * m.x + m.y * m.y);
        
        // holes
        if(dist < outerRadius) {
            alpha =  smootherstep(innerRadius, outerRadius, dist);
            gl_FragColor = vec4(r, g, b, alpha * alphaGain);
			fragIsColored = true;
        }  
        //if(dist >= innerRadius - 0.06 && dist <= innerRadius + 0.06 ) {
        //    gl_FragColor = vec4(0.80, 0.00, 0.0, 1.0);
        //}
         
     }
     

 
     // rectangle edges
     if(uv.y < bottom) {
        if(uv.x < left) {
            dist = distance(uv, lowerLeft);
            alpha = smoothstep(marginWidth, 0, dist);
         } else if(uv.x > right) {
            dist = distance(uv, lowerRight);
            alpha = smoothstep(marginWidth, 0, dist);
         } else { 
            alpha = smoothstep(bottom - marginWidth, bottom, uv.y);
         }
         gl_FragColor = vec4(r, g, b, alpha * alphaGain);
      } else if(uv.y > top) {
        if(uv.x < left) {
            dist = distance(uv, upperLeft);
            alpha = smoothstep(marginWidth, 0, dist);
         } else if(uv.x > right) {
            dist = distance(uv, upperRight);
            alpha = smoothstep(marginWidth, 0, dist);
         } else { 
            alpha = smoothstep(top + marginWidth, top, uv.y);
         }
         gl_FragColor = vec4(r, g, b, alpha * alphaGain);
      } else if(uv.x < left) {
        if(uv.y > top) {
            dist = distance(uv, upperLeft);
            alpha = smoothstep(marginWidth, 0, dist);
         } else if(uv.y < bottom) {
            dist = distance(uv, lowerLeft);
            alpha = smoothstep(marginWidth, 0, dist);
         } else {
            alpha = smoothstep(left - marginWidth, left, uv.x);
         }
         gl_FragColor = vec4(r, g, b, alpha * alphaGain);
      } else if(uv.x > right) {
       // alpha = smoothstep(right, right + marginWidth, uv.x);
       // gl_FragColor = vec4(0.0, 0.00, 0.0, alpha);
        if(uv.y > top) {
            dist = distance(uv, upperRight);
            alpha = smoothstep(marginWidth, 0, dist);
         } else if(uv.y < bottom) {
            dist = distance(uv, lowerRight);
            alpha = smoothstep(marginWidth, 0, dist);
         } else {
            alpha = smoothstep(right + marginWidth, right, uv.x);
         }
         gl_FragColor = vec4(r, g, b, alpha * alphaGain);
         
      }          

    
}
