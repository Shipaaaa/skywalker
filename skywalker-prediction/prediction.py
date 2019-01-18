from flask import Flask, request
from flask_restful import Resource, Api
import base64

app = Flask(__name__)
api = Api(app)

class Controller(Resource):
     def post(self):
         app.logger.info("Controller.post(self)")

         request_data = request.data
         app.logger.info(request_data)

         sample_data = base64.decodebytes(request_data)
         app.logger.info(type(sample_data))
         app.logger.info(sample_data)

         return {'compression_type': self.predictMethod(sample_data)}, 200


class Model(Controller):
    def predictMethod(self, sample_data):
         app.logger.info("Model.predictMethod(self, sample_data)")

         compressionTypes = {
            'lz4.txt': 'LZ4',
            'bzip2.txt': 'BZIP2',
            'snappy.txt': 'SNAPPY'
         }

         app.logger.info(type(sample_data))

         sample_data_len = len(sample_data)
         app.logger.info("sample_data_len: " + str(sample_data_len))

         first_byte = sample_data[0]
         app.logger.info("first_byte: " + str(first_byte))

         if first_byte == 108:
            compressionType = compressionTypes['lz4.txt']
         elif first_byte == 98:
            compressionType = compressionTypes['bzip2.txt']
         elif first_byte == 115:
            compressionType = compressionTypes['snappy.txt']
         else:
            raise ValueError('Error file')

         app.logger.info(str(compressionType))
         return compressionType

api.add_resource(Model, '/skywalker/predict')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
