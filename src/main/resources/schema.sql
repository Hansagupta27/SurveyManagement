
create table Survey
(
   surveyId uuid default random_uuid() primary key,
   surveyName varchar(250) not null,
   createdOn  TIMESTAMP,
   modifiedOn TIMESTAMP
);

create table Question
(
   questionId uuid default random_uuid() primary key,
   questionOrder tinyint,
   questionText varchar(250) not null,
   isEnabled BOOLEAN default true,
   surveyId UUID,
   foreign key (surveyId) references Survey(surveyId)
);

create table QuestionOptions
(
   questionOptionId uuid default random_uuid() primary key,
   optionOrder tinyint,
   optionText varchar(250) not null,
   questionId UUID,
   foreign key (questionId) references Question(questionId)
);

create table ResponseUser
(
   userId varchar(250) primary key,
   name varchar(250) not null,
   emailId varchar(250) not null
);

create table Response
(
   responseId uuid default random_uuid() primary key,
   responseDate TIMESTAMP,
   surveyId UUID,
   userId varchar,
   foreign key (userId) references ResponseUser(userId),
   foreign key (surveyId) references Survey(surveyId)
);

create table Answer
(
   answerId uuid default random_uuid() primary key,
   answerText varchar(250) not null,
   questionId UUID,
   responseId UUID,
   foreign key (responseId) references Response(responseId),
   foreign key (questionId) references Question(questionId)
);

create table AnswerOptions
(
   answerOptionId uuid default random_uuid() primary key,
   questionOptionId UUID,
   answerId UUID,
   foreign key (answerId) references Answer(answerId),
   foreign key (questionOptionId) references QuestionOptions(questionOptionId)
);

--DROP TABLE IF EXISTS QuestionOptions;
--DROP TABLE IF EXISTS ResponseUser;
--DROP TABLE IF EXISTS Response;
--DROP TABLE IF EXISTS Answer;
--DROP TABLE IF EXISTS AnswerOptions;
--DROP TABLE IF EXISTS Question;
--DROP TABLE IF EXISTS Survey;