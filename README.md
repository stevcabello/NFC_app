# NFC_app

This app was developed with the intention to show how to handle the communication between an android app and external web service and database, as well as to show other kind of utility NFC could have.

The app handles two kinds of users: Teacher and Student.

The idea of the app is to allow to a Teacher upload a Test (**see Note 1**) which will be activated (visible to the students) 
by the Teacher 5 or 10 minutes before the class ends, and the Students will access to the Test through the app and will complete
it in the time specified by the Teacher. 
All tests will be sent to the Teacher once the time is over. The Teacher can see the test results as a percentage. i.e. no 
indivual marks, but instead a total mark indicating how well or bad the entire course is.

***However, what happen if a student has a smartphone but no access to internet?***

Here is where we made use of the NFC technology.
A Teacher will need to carry a NFC tag and copy the Test into the tag (**see Note 2**), The Student will be able to read the 
Test from the tag (just placing the tag near to the smartphone) and start the Test. When the Test is completed, this will be 
saved into a SQlite database, and automicatically sent to the Teacher when access to internet is available.


***Note 1:***
To upload a Test (a set of 10 questions with 5 possible answers (only 1 correct answer)) 
a Teacher needs to access to this web site:

http://ncabello-001-site1.atempurl.com/

and select a Subject (**see Note 3**) for the Test, as well as provide a Title and a Description.


You can use these credentials to access to the web site

**username:** myapur  
**password:** asdfg 

*or sign up (through the app) as a Teacher and create your own user*


For access to the app as a Student you can try with these credentials

**username:** ncabello  
**password:** 123456

*or sign up (through the app) as a Student and create your own user*


**Note 2:**
To copy the Test into the NFC tag, the test needs to be active (click on the inactive test and set the time). 
Once the Test is active, a long click on the Test button will make to the Test appear in a floating window, 
place the NFC tag in the back side of the smartphone and press **Transfer test**


**Note 3:**
I have provide three dummy Subjects (the name of the subjects are in spanish)
* TEORIA ELECTROMAGNETICA I      ---> Electromagnetic Theory
* PROCESOS ESTOCASTICOS          ---> Stochastic Processes
* FUNDAMENTOS DE REDES DE DATOS  ---> Networking


## Communication ##

The app reads and writes data from/to an external SQL database by using a RESTful web service (in asp.net).

The methods for calling the web service's functions are in the files inside the folder ***businessobject*** in the *com.example.nfc* package


## Web Site and Web Service ##
The code for the web site is available on : https://github.com/stevcabello/NFCwebadmin

The code for the web service is available on: https://github.com/stevcabello/RESTAppNFC


## DB ##
The SQL 2008 R2 database is available as a backup file on: https://github.com/stevcabello/NFCdatabase





