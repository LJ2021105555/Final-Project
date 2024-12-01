import torch
import cv2
import numpy as np
from flask import Flask, request, jsonify
from PIL import Image
from io import BytesIO


app = Flask(__name__)


model = torch.hub.load('ultralytics/yolov5', 'yolov5s') 
classes = model.names  

def detect_objects(image):
    img = np.array(image)
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR) 
    results = model(img)
    detections = results.pandas().xywh[0]
    return detections

@app.route('/detect', methods=['POST'])
def detect():
    if 'image' not in request.files:
        return jsonify({'error': 'No file part'}), 400
    file = request.files['image']
    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400

    image = Image.open(BytesIO(file.read()))
    
    detections = detect_objects(image)
    
    detections_json = detections.to_dict(orient='records')

    return jsonify(detections_json)


def change_detection(detections, from_class, to_class):
    if isinstance(classes, dict): 
        from_class_index = [key for key, value in classes.items() if value == from_class]
        to_class_index = [key for key, value in classes.items() if value == to_class]
    elif isinstance(classes, list): 
        from_class_index = [classes.index(from_class)] if from_class in classes else []
        to_class_index = [classes.index(to_class)] if to_class in classes else []
    else:
        raise TypeError("Unsupported classes type. Must be list or dict.")

    if not from_class_index or not to_class_index:
        raise ValueError(f"Invalid class names: {from_class} or {to_class}")
    
    from_class_index = from_class_index[0]
    to_class_index = to_class_index[0]

    for index, detection in detections.iterrows():
        if int(detection['class']) == from_class_index:
            detections.at[index, 'class'] = to_class_index
            detections.at[index, 'name'] = to_class
    return detections



@app.route('/change-detection', methods=['POST'])
def change_detection_route():
    if 'image' not in request.files:
        return jsonify({'error': 'No file part'}), 400
    file = request.files['image']
    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400
    from_class = request.form.get('from_class')
    to_class = request.form.get('to_class')
    if not from_class or not to_class:
        return jsonify({'error': 'from_class or to_class is missing'}), 400
    image = Image.open(BytesIO(file.read()))
    detections = detect_objects(image)
    updated_detections = change_detection(detections, from_class, to_class)
    updated_detections_json = updated_detections.to_dict(orient='records')
    return jsonify(updated_detections_json)

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
