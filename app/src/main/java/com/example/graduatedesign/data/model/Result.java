package com.example.graduatedesign.data.model;

import com.example.graduatedesign.net.RetrofitExceptionResolver;

/**
 * 访问服务器得的统一格式的 NetResult 结果后，根据业务需求解析成统一的 Result 对象供视图层使用
 */
public class Result {
    // hide the private constructor to limit subclass types (Success,Fail, Error)
    private Result() {
    }

    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + RetrofitExceptionResolver.resolve(error.getError()) + "]";
        }else if (this instanceof  Fail){
            Result.Fail fail= (Result.Fail) this;
            return "Fail[info="+fail.getFailInfo()+"]";
        }
        return "";
    }

    // Success sub-class
    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public void setData(T data){
            this.data=data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Fail sub-class
    public final static class Fail extends Result {
        private String msg;

        public Fail(String msg) {
            this.msg = msg;
        }

        public String getFailInfo() {
            return this.msg;
        }
    }

    // Error sub-class
    public final static class Error extends Result {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}