###### **Survey Management**

This service helps to create and submit survey. Analytics can also be performed on the submitted survey. 

**Running the application**

_On Localhost:_ Please run the following commands when running the application locally:
```
Install Java 11, maven, docker, docker desktop
mvn clean install
```
To run inside a docker container, run the following commands:
```
docker build -t survey-management .
docker run survey-management
```

_Using Minikube exposed as a service on kubernetes:_ Please run the following commands:

````
brew install minikube (If not already installed)
minikube start

Commands to be executed in the application folder

eval $(minikube docker-env) (To run it in Minikube session)
Docker build -t survey-management . (Building the docker image of the app)
Kubectl create -f deployment.yml (Using the deployment file to create kubernetes deployment)
kubectl expose deployment survey-management-deployment --type=NodePort (exposing the deployment as a service in kubernetes)
minikube service survey-management-deployment --url (Get the url where minikube exposes the service. Use it to hit the URL)

````
**Functionality**

The application currently exposes the below endpoint:

POST **/survey/create** - Create a survey which takes survey name, question number and options text details in input. Validates the input request and loads the question in H2 database. Returns surveyId as response. 

POST **/survey/submit** - Submits the answer for a survey which takes surveyId, user information, answers and answer text in input. Validates input againts the database. If valid, saves the answers for survey.

**Testing Scope**

This repository contains two types of test covering both positive and negative scenarios:
1. Unit Test Using Junit5/Mockito
2. Integration Test/Application Test Using Cucumber

````


curl --location --request POST 'http://localhost:8080/survey/create' \
--header 'Content-Type: application/json' \
--data-raw '{
  "surveyName": "testing survey",
  "questions": [
    {
      "questionOrderNumber": 1,
      "questionText": "Where does Santa lives?",
      "questionOptions": [
        {
          "optionOrder": 1,
          "optionText": "Helsinki"
        },
        {
          "optionOrder": 2,
          "optionText": "London"
        }
      ]
    },
    {
      "questionOrderNumber": 2,
      "questionText": "Where is Cricket world cup 2022?",
      "questionOptions": [
        {
          "optionOrder": 1,
          "optionText": "India"
        },
        {
          "optionOrder": 2,
          "optionText": "Germany"
        },
        {
          "optionOrder": 3,
          "optionText": "Australia"
        }
      ]
    }
  ]
}'

curl --location --request POST 'http://localhost:8080/survey/submit' \
--header 'Content-Type: application/json' \
--data-raw '{
  "surveyId": "1f38fa0b-b4cb-4ac4-b302-7628e62690af",
  "userId": "ABC",
  "userName": "MarketLogic",
  "emailId": "abc@marketlogic.com",
  "answers": [{
    "questionOrderNumber": 1,
    "answerText": "Helsinki"
  },
    {
      "questionOrderNumber": 2,
      "answerText": "Australia"
    }]


}'
