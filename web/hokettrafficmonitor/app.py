import json
from flask import Flask, request, render_template

onlinebeacons = dict()
app = Flask(__name__)

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/beacons/', methods=['POST'])
def beacons():
    data = request.data.decode('utf-8')
    info = json.loads(data)
    for b in info['beacons']:
        onlinebeacons[b['alias']] = {'alias': b['alias'], 'rssi': b['rssi']}
    return 'OK', 201

@app.route('/beacons/', methods=['GET'])
def get_beacons():
    return json.dumps({'beacons': list(onlinebeacons.values())}), 200

@app.route('/position/', methods=['GET'])
def position():
    pass

if __name__ == '__main__':
    app.run(debug=True)
