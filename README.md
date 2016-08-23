An implemention of a fun weather simulator in Scala programming language.

The fun journey continues.


    Technology stack
       - Scala 
       - Apache Spark MLlib 
       - Predictive modelling 
       - Random Forests (Classification and Regression) 
       - LIBSVM data format for training data

    Key features
       - Increased model accuracy by increaing training data size and quality 
       - Flexible for future extensions 

    Simulation methodology 
       - Struture the weather observations into training sets (in LIBSVM format):
           - condition.txt
           - temperature.txt
           - pressure.txt
           - humidity.txt
       - Build classification model for weather conditions 
       - Build regression models for weather sensors measurements (Temperature/Pressure/Humidity) 
       - Feed new data (time&location) the established predictive models to generate the simulated weather data

    Usage
       - sbt test or ./activator test 
       - Users may also want to modify or add test scenarios in WeatherSimulator.scala

    TODO
       - Use R package rscala to integrate R scripts in fun-with-weather-R to produce training data and for
         data visualisation
       - Add training set from real weather data to make the simulation more realitic 
       - Feature engineering to extract more information from the time stamps, i.e. hour of the day, month of the 
         year to increase model accuracy
