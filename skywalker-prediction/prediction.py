from flask import Flask, request
from flask_restful import Resource, Api

app = Flask(__name__)
api = Api(app)

class Controller(Resource):
     def post(self):
         app.logger.info("Controller.post(self)")

         params = request.get_json()
         app.logger.info(str(params))

         return {'compression_type': self.predictMethod(params)}, 200

class Model(Controller):
    def predictMethod(self, params):
         app.logger.info("Model.predictMethod(self, params)")
         compressionTypes = {
            'lz4.txt': 'LZ4',
            'bzip2.txt': 'BZIP2',
            'snappy.txt': 'SNAPPY'
         }

         if params in compressionTypes:
             compressionType = compressionTypes[params]
         else:
             compressionType = compressionTypes['snappy.txt']

         app.logger.info(str(compressionType))
         return compressionType

api.add_resource(Model, '/skywalker/predict')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
