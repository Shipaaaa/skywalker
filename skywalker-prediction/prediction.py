from flask import Flask, request
from flask_restful import Resource, Api
import base64
import numpy as np
from keras.models import load_model


app = Flask(__name__)
api = Api(app)
model = None

def init():
    global model
    model = load_model('./models/model_k_2_1.h5')
    print("model loaded")

def vectorize_sequences(sequences, dimension):
    vectorize = np.zeros((len(sequences), dimension))
    for i, sequence in enumerate(sequences):
        vectorize[i, sequence] = 1.
    return vectorize

class PredictController(Resource):

     def post(self):
         app.logger.info("Controller.post(self)")

         request_data = request.data
         app.logger.info(request_data)

         #sample_data = base64.decodebytes(bytes(request_data, 'utf-8'))
         sample_data = base64.decodebytes(request_data)
         app.logger.info("sample_data type: " + str(type(sample_data)))
         app.logger.info(sample_data)

         int_sample_data = [byte for byte in sample_data]
         app.logger.info("int_sample_data type: " + str(type(int_sample_data)))
         app.logger.info(int_sample_data)

         int_sample_data_list = []
         int_sample_data_list.append(int_sample_data)
         samples = np.array(int_sample_data_list)
         app.logger.info("samples type: " + str(type(samples)))
         app.logger.info(samples.shape)
         app.logger.info(samples)

         vectorize_samples = vectorize_sequences(samples, samples.shape[1])
         app.logger.info("vectorize_samples type: " + str(type(vectorize_samples)))
         app.logger.info(vectorize_samples.shape)
         app.logger.info(vectorize_samples)

         return {'compression_type': self.predictMethod(vectorize_samples)}, 200


class PredictModel(PredictController):
    global model

    def predictMethod(self, vectorize_samples):
         app.logger.info("Model.predictMethod(self, vectorize_samples)")

         compressionTypes = {
            0 : 'NONE',
            1 : 'LZ4',
            2 : 'BZIP2',
            3 : 'SNAPPY'
         }

         app.logger.info(type(vectorize_samples))

         vectorize_samples_len = len(vectorize_samples)
         app.logger.info("vectorize_samples_len: " + str(vectorize_samples_len))

         predictions = model.predict(vectorize_samples)
         app.logger.info("predictions: " + str(predictions))

         compressionType = compressionTypes[np.argmax(predictions[0])]

         app.logger.info(str(compressionType))
         return compressionType

api.add_resource(PredictModel, '/skywalker/predict')

if __name__ == '__main__':
    print((
        "* Loading Keras model and Flask starting server... "
        "please wait until server has fully started"
    ))
    init()
    app.run(host='0.0.0.0', port=5000, debug=True)
