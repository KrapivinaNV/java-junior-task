package com.mcb.javajuniortask.controller;

import com.mcb.javajuniortask.error.Error;
import lombok.Data;

@Data
class Response <T> {
    private T value;
    private Error error;

    Response(){
    }

    Response(T value){
        this.value = value;
    }
    Response(Error error){
        this.error = error;
    }
}