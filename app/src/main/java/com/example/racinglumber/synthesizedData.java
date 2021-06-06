package com.example.racinglumber;

import static com.example.racinglumber.dataStorage.dataArrayLen;

//This class contains a set of synthesized latitudal, longitudal, etc. data using sensor fusion
//builds based on current data in dataStorage
public class synthesizedData {

    public float[] lateralDataArray; //left (negative) right (positive) acceleration
    public float[] longitudalDataArray; //backward (negative) forward (positive) acceleration
    public double[] latitudeArray;
    public double[] longitudeArray;
    public long[] GPSEventTime;
    public static int GPSIndex; //index of GPS data

    public float forwardVectorX;
    public float forwardVectorY;

    public synthesizedData()
    {
        //constructor
        //TODO remove//forwardVectorX = 5.0f;
    }

    public void generateSynthDataFromDataStorage()
    {
        if (dataArrayLen > 0)
        {
            lateralDataArray = new float[dataArrayLen];
            computeForwardVector();
            computeLateralLongitudalArrays();

            /*Instantiate gps arrays*/
            latitudeArray = new double[dataStorage.getDataArrayLen()];
            longitudeArray = new double[dataStorage.getDataArrayLen()];
            GPSEventTime = new long[dataArrayLen];

            /*Copy latitude and longitude array*/
            for (int index = 0; index < dataStorage.getDataArrayLen(); index++)
            {
                latitudeArray[index] = dataStorage.latitudeArray[index];
                longitudeArray[index] = dataStorage.longitudeArray[index];
                GPSEventTime[index] = dataStorage.GPSEventTime[index];
                GPSIndex = dataStorage.GPSIndex;
            }
        }
    }

    //todo use a user selected point instead, this works for now though
    public void computeForwardVector()
    {
        /*Prototype is using 100Hz capture.  First 3 seconds is setup(putting in pocket), 3 seconds is averaging vector of kart going forward, then save*/
        float xArray = 0.0f;
        float yArray = 0.0f;
        float xAvg, yAvg;
        float magnitude;

        //todo could make delay configurable
        for (int index = 150; index < 300; index++)//start at 300 to make it start at 3 seconds, 3* 50Hz
        {
            if (index < dataStorage.getDataArrayLen())
            {
                xArray += dataStorage.getSensorValue(dataStorage.Axis.X, dataStorage.RecordType.acceleration, index);
                yArray += dataStorage.getSensorValue(dataStorage.Axis.Y, dataStorage.RecordType.acceleration, index);
            }
        }

        //todo could use moving average algorithm to be more accurate
        //todo don't need average, since magnitude is cancelled out later anyways
        xAvg = xArray/150.0f;
        yAvg = yArray/150.0f;
        magnitude = (float) Math.sqrt((xAvg*xAvg)+(yAvg*yAvg));

        //normalize vector
        forwardVectorX = xAvg/magnitude;
        forwardVectorY = yAvg/magnitude;
    }

    public void computeLateralLongitudalArrays()
    {
        float xVal, yVal, xyMag, xyDotProduct, cosTheta, theta;

        lateralDataArray = new float[dataStorage.getDataArrayLen()];
        longitudalDataArray = new float[dataStorage.getDataArrayLen()];

        //Instantiate lateral data array
        for (int index = 0; index < dataStorage.getDataArrayLen(); index++)
        {
            //use dot product to get angle:

            //new vector, get magnitude
            xVal = dataStorage.getSensorValue(dataStorage.Axis.X, dataStorage.RecordType.acceleration, index);
            yVal = dataStorage.getSensorValue(dataStorage.Axis.Y, dataStorage.RecordType.acceleration, index);
            xyMag = (float) Math.sqrt((xVal*xVal)+(yVal*yVal));
            //get dot product
            xyDotProduct = (xVal*forwardVectorX) + (yVal*forwardVectorX);
            //get cosine theta
            cosTheta = xyDotProduct/xyMag;
            //get angle between vectors
            theta = (float) Math.acos(cosTheta);

            //todo some of these signs might need to flip
            //Get the lateral component
            lateralDataArray[index] = xyMag*((float)Math.sin(theta));
            //get the longitudal component
            longitudalDataArray[index] = xyMag*cosTheta;
        }
    }

}



//        int i;
//        double latitude;
//        double latitudeNext;
//        double longitude;
//        double longitudeNext;
//        double latDelta, longDelta;


//        for (i = 0; i < dataArrayLen-1; i++)
//        {
//            //find GPS vector using current and next points
//            latitude = dataStorage.getGPSValueFromAccelDataIndex(true, i);
//            latitudeNext = dataStorage.getGPSValueFromAccelDataIndex(true, i+1);
//            longitude = dataStorage.getGPSValueFromAccelDataIndex(false, i);
//            longitudeNext = dataStorage.getGPSValueFromAccelDataIndex(false, i+1);
//
//            latDelta = latitudeNext - latitude;
//            longDelta = longitudeNext - longitude;
//
//            //find angle between XY acceleration vectors and GPS vector
//
//            //split latitude and longitude parts
//        }