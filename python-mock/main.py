from flask import Flask, jsonify
import time
import random

app = Flask(__name__)

@app.errorhandler(RuntimeError)
def handle_runtime_error(e):
    return jsonify({"error": str(e)}), 500

@app.route("/mock")
def mock_response():
    time.sleep(0.1)  # Sabit 100 ms gecikme
    # time.sleep(0.5)  # Sabit 500 ms gecikme
    # time.sleep(1)  # Sabit 1000 ms gecikme
    if random.random() < 0.85:
        return jsonify({
            "message": "Mock 3rd party response",
            "delaySeconds": 0.1
        })
    else:
        raise RuntimeError("Simulated failure: Mock endpoint randomly failed")

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
