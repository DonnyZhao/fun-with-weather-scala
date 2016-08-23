An implemention of a fun weather simulator in Scala programming language.

The fun journey continues.


    Technology stack
       - Scala 
       - Apache Spark MLib 
       - Predictive modelling 
       - Random Forests (Classification and Regression) 

    Key features
       - Increased model accuracy by increaing training data size and quality 
       - Flexible for future extensions 

    Simulation methodology 
       - Struture the weather observations into training sets (in LIBSVM format) 
       - Build classification model for weather conditions 
       - Build regression models for weather sensors measurements (Temperature/Pressure/Humidity) 
       - Feed new data (time&location) the established predictive models to generate the simulated weather data

    Usage
     displayed in the console.
       - sbt test or ./activator test 
       - Users may also want to modify or add test scenarios WeatherSimulator.scala

    TODO
       - Add training set from real weather data to make the simulation more realitic 
