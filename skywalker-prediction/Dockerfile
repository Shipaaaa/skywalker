FROM python:3

MAINTAINER Vladislav Shipugin <vladshipugin@gmail.com>

WORKDIR /skywalker/prediction-service
COPY . /skywalker/prediction-service

RUN pip install --trusted-host pypi.python.org -r requirements.txt

EXPOSE 5000

CMD ["python", "prediction.py"]
