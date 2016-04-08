import QueueConsumer
import QueuePublisher
import pika
import uuid

QUEUE_URL="amqp://jagruti:jagruti@192.168.1.111:5672"
GET = "get"
PUT = "put"
POST = "post"
DELETE = "delete"
INBOUND_QUEUE = "inbound_queue"
GET_QUEUE = "get_queue"

class QueueRpcClient(object):
    def __init__(self):
		self.connection = pika.BlockingConnection(pika.ConnectionParameters(
                host='localhost'))

		self.channel = self.connection.channel()

		result = self.channel.queue_declare(exclusive=True)
		self.callback_queue = result.method.queue

		self.channel.basic_consume(self.on_response, no_ack=True,
                                   queue=self.callback_queue)

	def on_response(self, ch, method, props, body):
		if self.corr_id == props.correlation_id:
			self.response = body

	def get	(self,key):
	"This is GET API"
		self.response = None
        self.corr_id = str(uuid.uuid4())
        self.channel.basic_publish(exchange='',
                                   routing_key=GET_QUEUE',
                                   properties=pika.BasicProperties(
                                   		 type = GET,
                                         reply_to = self.callback_queue,
                                         correlation_id = self.corr_id,

                                         ),
                                   body=str(key))
        while self.response is None:
            self.connection.process_data_events()

        return str(self.response)



	return arr

	def post(imageByteArray):
	"This is POST API"
		self.response = None
        self.corr_id = str(uuid.uuid4())
        self.channel.basic_publish(exchange='',
                                   routing_key=INBOUND_QUEUE,
                                   properties=pika.BasicProperties(
                                   		 type = POST,
                                         reply_to = self.callback_queue,
                                         correlation_id = self.corr_id,

                                         ),
                                   body=imageByteArray)
          while self.response is None:
            self.connection.process_data_events()

        return str(self.response)
		

		return  

	def delete(key):
	"This is DELETE API"
		self.channel.basic_publish(exchange='',
                                   routing_key=INBOUND_QUEUE,
                                   properties=pika.BasicProperties(
                                   		 type = DELETE,
                                         ),
                                   body=imageByteArray)


	def put(key, imageByteArray):
	"This is PUT API"
		self.channel.basic_publish(exchange='',
                                   routing_key=INBOUND_QUEUE,
                                   properties=pika.BasicProperties(
                                   		 type = PUT,
                                         correlation_id = key,
                                         ),
                                   body=imageByteArray)





if __name__ == "__main__":
	arr =get(key="12")
	for v in arr: print(v)
	print(QUEUE_URL)

	printme("Vinit")