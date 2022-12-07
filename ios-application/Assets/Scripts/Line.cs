using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Linq;

public class Line : MonoBehaviour
{
	FlikittCore fc;
	DrawingManager dm;

	public LineRenderer renderer;
	List<Vector2> points;

	public Color color;
	public float width;

	private const int boundY = 130;

	void Awake(){
		dm = GameObject.Find("Drawing Manager").GetComponent<DrawingManager>();
		fc = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();
	}

	void Start(){
		color = dm.color;
		width = dm.width;
	}

	public void UpdateLine(Vector2 mousePos){
		if(points == null){
			points = new List<Vector2>();
			SetPoint(mousePos);
			return;
		}

		if(Vector2.Distance(points.Last(), mousePos) > .001f) SetPoint(mousePos);
	} 

	void SetPoint(Vector2 point){
		if(point.y > -2.75f && Input.touchCount == 1){
			points.Add(point);

			//This is where I edit the attributes of the line renderer
			renderer.SetColors(color, color);
			renderer.SetWidth(width, width);
			renderer.positionCount = points.Count;
			renderer.SetPosition(points.Count - 1, point);
		}
	}

	void OnMouseOver(){
		if(fc.drawMode == "Eraser" && !fc.isPlaying)
			Destroy(gameObject);
	}
}
