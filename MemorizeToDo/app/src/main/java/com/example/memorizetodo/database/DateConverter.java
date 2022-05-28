package com.example.memorizetodo.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timeStamp)
    {
        //used by Room while reading from the database
        return (timeStamp==null)?null:new Date(timeStamp);
    }
    @TypeConverter
    public static Long toTimeStamp(Date date)
    {
        //Used by Room while writing to the database
        return date==null?null:date.getTime();
    }
}
