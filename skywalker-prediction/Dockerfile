FROM python:3.5

MAINTAINER Vladislav Shipugin <vladshipugin@gmail.com>

WORKDIR /skywalker/prediction-service
COPY . /skywalker/prediction-service

RUN pip install --upgrade pip
RUN pip install --trusted-host pypi.python.org -r requirements.txt

EXPOSE 5000

CMD ["python", "prediction_1.py"]
