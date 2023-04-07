package com.grup.repositories

// Realm
const val APP_ID = "grup-app-qpcdr"
const val MONGODB_API_ENDPOINT =
    "https://us-west-2.aws.data.mongodb-api.com/app/$APP_ID/endpoint"

// AWS
const val AWS_IMAGES_API_KEY = "YQaF9FbA8P7ZL7x6uV5G04lEf7HUOPOZ966ldOL2"

const val AWS_IMAGES_API_ID = "g9ig0cnnm2"
const val AWS_IMAGES_API_STAGE = "v1"
const val AWS_IMAGES_BUCKET_NAME = "grup-images"
const val AWS_IMAGES_API_URL =
    "https://$AWS_IMAGES_API_ID.execute-api.us-east-1.amazonaws.com" +
            "/$AWS_IMAGES_API_STAGE/$AWS_IMAGES_BUCKET_NAME"