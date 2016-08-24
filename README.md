An implementation of a fun weather simulator in Scala programming language.

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
       - The environment (atmosphere, topography, geography and oceanography) is summarised by the
         training samples with the attributes of position, time, weather conditions and sensor measurements  
       - Struture the enviroment and observations into training sets (in LIBSVM format):
           - Condition.txt
           - Temperature.txt
           - Pressure.txt
           - Humidity.txt
       - Build classification model for weather conditions 
       - Build regression models for weather sensors measurements (Temperature/Pressure/Humidity) 
       - Feed new data (time&location) the established predictive models to generate the simulated weather data

    Usage
       - sbt test or ./activator test 
       - sbt run or ./activator run
       - sbt "run-main com.funweather.WeatherSimulator"
       - sbt "run-main com.funweather.WeatherSimulator [latitude] [longitude] [elevation] [time stamp] 
       - Users may also want to modify or add test scenarios in WeatherSimulator.scala and ./activator run from terminal
       - Open the sbt project using your favorite IDE and run the tests and WeatherSimulator 

    TODO
       - Use R package rscala to integrate R scripts in fun-with-weather-R to produce training data and for
         data visualisation
       - Add training set from real weather data to make the simulation more realitic 
       - Feature engineering to extract more information from the time stamps, i.e. hour of the day, month of the 
         year to increase model accuracy
