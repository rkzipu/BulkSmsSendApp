package com.zipu.bulksmssender;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Source {

@SerializedName("message")
@Expose
private String message;
@SerializedName("numbers")
@Expose
private ArrayList<String> numbers = null;

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public ArrayList<String> getNumbers() {
return numbers;
}

public void setNumbers(ArrayList<String> numbers) {
this.numbers = numbers;
}

    @Override
    public String toString() {
        return "Source{" +
                "message='" + message + '\'' +
                ", numbers=" + numbers +
                '}';
    }
}