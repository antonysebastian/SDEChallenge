# PaytmLabs SDE Challenge

## Coding Question

Write an interface for a data structure that can provide the moving average of the last N elements added, add elements to the structure and get access to the elements. Provide an efficient implementation of the interface for the data structure.

### Minimum Requirements

1. Provide a separate interface (IE `interface`/`trait`) with documentation for the data structure
2. Provide an implementation for the interface
3. Provide any additional explanation about the interface and implementation in a README file.

### Solution


## The Interface MovingAverageCalculator.

This interface provides methods for storing data elements and calculating moving average of the last n elements at any given point in time. The interface exposes methods add, movingAverage, get, getAll, size and isEmpty.

# Use of BigDecimal
The data type BigDecimal is used to store elements and to calculate moving average. BigDecimal was chosen as it is the best choice in Java to perform arithmetic operations that require exact answers. Since most real world applications of Moving Average like financial transactions use inputs with decimal points and expect precise outputs, data types like double, float etc cannot be considered to store data and calculate results, as decimal operations with them yield unpredictable results.


## The Class MovingAverageCalculatorImpl, which implements MovingAverageCalculator interface. 

  
# Storing Data
* **elements** - The class stores all elements inserted as a list of BigDecimals.


* **window** - The window size to calculate the Moving Average. Must be initialized using the available constructors and can be updated using the setter method 


* **windowElements** - A FIFO queue is used to hold all elements that fall inside the window (last N elements) for moving average calculation. This queue is updated whenever an element is inserted, by removing the first element and adding the new element to the last. This ensures that the correct elements are present inside the window after each insertion.


* **windowSum** - The sum of all elements inside the window is calculated after every insertion and stored, so that the moving average calculation can be done without any iteration.


# Scale and RoundingMode
 
Scale and Rounding mode are two options from the BigDecimal class used to ensure accuracy of the results.

* **scale** - This defines the number of decimal points required in the moving average value returned. Rounding off is done for values with number of decimal points exceeding scale. Trailing zeroes are added to values when number of decimal points are less than scale. The default scale is set to 5, but can be set using the available constructors or the setter method. 


* **roundingMode** - This defines the mode of rounding to be chosen to limit the decimal points to the scale. The default mode chosen is HALF_DOWN. Other rounding modes can be set using the available constructors
or the setter method. 
https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/RoundingMode.html

NOTE: Scale is not applied to elements on addition. The scale is applied only to the result that is returned from the moving average calculation.
eg : val1 = 2.266, val2 = 2.266, scale = 2, window = 2
	 The scale is applied, when the moving average is calculated
     ie, scale of 2 is applied on the result((2.266 + 2.266) / 2) 2.266 to return the result 2.27


#Moving Average Calculation

 
* When each element is added, the windowSum is calculated by adding the newly added element and popping the first element in the windowElements.


* When the moving average method is called, the windowSum is calculated by dividing windowElements with the window size and then setting scale.


* When the window size is updated, a recalculation is done to update windowElements and windowSum.
 	 

#Complexities
 
**Time Complexity:**

* The add, movingAverage, get, getAll, size, isEmpty methods have time complexity O(1)
The setWindow method, used to update the window size has a complexity of O(n), where n is the window size

**Space Complexity:**

* The add, movingAverage, setWindow methods have complexity O(n), where n is the window size
The get, getAll, size, isEmpty methods have complexity O(1)


## Design Question

Design A Google Analytic like Backend System.
We need to provide Google Analytic like services to our customers. Please provide a high level solution design for the backend system. Feel free to choose any open source tools as you want.

### Requirements

1. Handle large write volume: Billions of write events per day.
2. Handle large read/query volume: Millions of merchants wish to gain insight into their business. Read/Query patterns are time-series related metrics.
3. Provide metrics to customers with at most one hour delay.
4. Run with minimum downtime.
5. Have the ability to reprocess historical data in case of bugs in the processing logic.

### Solution

Diagram uploaded in Google Analytics like Backend.jpg file
 
* The system is capable to handle billions of events per day, as all components are horizontally scalable. Also, the data store used to store event data and report data is Cassandra, which is a write intensive database. In memory caching is also added in front of the report data store to make reads faster and support the scale required.


* All components used in the solution can be horizontally scaled without any downtime, by adding new nodes to the clusters.


* The persistent data store saves all events in their original unprocessed format. Hence it is easy and possible to reprocess historical data in case of bugs in the processing logic.

Below is a summary of what happens in the system, when a tracking request is received:

1. Tracking event is sent from the client
2. Request is received by one of the nodes in the Nginx cluster. Nginx redirects the request to one of the tracking code validator services.
3. The tracking code validator service validates the tracking code information by checking the configuration cache. If data is not present in the cache, the configuration storage db is queried and the data is updated in the configuration cache. Cache is configured to evict Less Recently Used elements when memory size is reached.
4. The request is pushed to the Kafka cluster, into two topics (one for writing into the db, and one for Spark to process ) for further processing.
4. One instance of the Db update service picks up the data from the Kafka topic consumed by it. This data (which is a copy of the original unprocessed event) is then updated into the persistent storage.
5. Spark picks up the data from the Kafka topic consumed by it and does the required processing.
6. The processed data is stored in Report storage database. 

Below is a summary of what happens in the system, when a report query:

1. Request sent by the client.
2. Request is received by one of the nodes in the Nginx cluster. Nginx redirects the request to one of the report query services.
3. The report query service checks the cache to fetch the available data related to the query. If any data is unavailable in the cache, the Report data store is queried to get it and is later updated to the cache. Cache is configured to evict the Less Recently Used elements when memory size is reached.
4. The requested data is returned to the client.


### Components

The solution consists of the below components and all of them are horizontally scalable:

## API Gateway and Load Balancer (Nginx)

* A cluster of nginx servers are chosen for load balancing and as the API gateway. Nginx accepts a high number of concurrent requests (~500K/sec with 30% CPU usage for each node) if clustered optimally, and can be scaled by adding more nodes (horizontal scaling). 


* In this solution, we are also configuring nginx to be the API gateway to map incoming requests to the corresponding services to be invoked. In the solution diagram, the nginx cluster maps the incoming requests to the tracking code  validator service and report generator service. Similarly, it could also be configured to front other services when required. The other advantage of nginx is its lightwieght, easy to setup and consumes lesser resources compared to other load balancers.

## Micorservices

The design has 3 microservices working in executing different tasks. Each of the can be isolated and scaled horizontally by adding more nodes.

**Tracking Code Validator Service** - The tracking code validator service cluster is used for validating the tracking code and also to get any additional meta data about analytics account or tracking code required to further process the tracking event. Whenever a request comes in, the validity of the tracker code  is checked by this service. This service connects to its own database (Configuration storage - MongoDB) and cache (Configuration Cache - Redis) to fetch the required data. The service first checks the cache for the required data. If its not found in the cache, it queries the db and later updates it in the cache.


**Db Update Service** - The Db update service cluster is used to update the tracking events sent from the client application into the persistent storage. This service updates the tracking events as received into the storage. Hence, the data from the persistent store can be used to re process historical data in case of bugs in the processing logic. 


**Report Query Service** - The report query service cluster is used to generate reports for the end user. The service fetches the reports from two sources, the report data cache (Redis) and the report data store (Cassandra). The report data cache acts as a cache for frequently queried report data. The report data store contains all the data that is generated by Spark. The service first checks the cache for the required data and if its not available in the cache, the report data store is queried and later updated in the cache. 

## Kafka Cluster

The Kafka cluster is used to stream the data for multiple functionalities. Topics are created in Kafka which are subscribed by the Db Update Service and Spark. The tracking events are streamed into Kafka by the tracking code validator service and are consumed by the Db Update service and Spark. Kafka is used here for streaming as it is horizontally scalable and has the capability to process requests in millisecond range. Also, Spark supports using Kafka as its primary data source.

## Apache Spark Stream Processing

Spark is used in the solution to process the tracking events from Kafka.  Spark will process the data and store it into the reports data store.

## Report Data Store (Cassandra)

The data that is processed from Spark is stored into a Cassandra database. Cassandra is a write intensive database and provides high performance for writes. Since the write operations coming from Spark will be a huge number, we are using Cassandra here. Also, Cassandra can be scaled horizontally by adding newer nodes.

## Persistent Storage (Cassandra)

This is the database that is used to store all the tracking events received by the system. The events are stored in Cassandra before processing and hence can be used re process historical data in case of bugs in the processing logic. Cassandra is chosen as its write intensive nature will speed up storage of the billions of tracking events received.

## Redis Caches

Redis was used as the cache as it can be used as an in memory cache that can support around 1 million transactions per second. Redis also has many useful in built data structures. 

**Report Data Cache** - This is an in memory Redis cache used to cache the processed data from Spark. The data from this cache is used to generate reports requested the end user. Data is updated into the cache by the Report query Service. The eviction policy configured is to evict less recently used elements when the memory size configured is reached.

**Configuration Cache** - This is an in memory Redis cache used to cache tracking code and configuration information. The eviction policy configured is to evict less recently used elements when the memory size configured is reached. Data is updated into the cache by the Tracking code validator Service.

## Configuration Storage (MongoDB)

This is a MongoDB database and is the persistent store for the information related to tracker codes and other analytics configurations. When a user registers/registration information changes, the tracking code and configuration data is updated in this database. MongoDB is used as the operations on this database are mostly read and since MongoDB performs well with reads. Also, tracking information and configurations are best stored in JSON format and MongoDB has inbuilt support for it.