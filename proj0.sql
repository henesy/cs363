# Force being unsafe
SET SQL_SAFE_UPDATES=0; 

# Item 1
create table Person (
Name char (20),
ID char (9) not null,
Address char (30),
DOB date,
Primary key (ID)
);

# Item 2
create table Instructor (
InstructorID char (9) not null,
Rank char (12),
Salary int,
Primary key (InstructorID),
Foreign key (InstructorID) references Person(ID)
);

# Item 3
create table Student (
StudentID char (9) not null,
Classification char (10),
GPA double,
MentorID char(9),
CreditHours int,
Foreign key (MentorID) references Instructor(InstructorID)
);

# Item 4
# !! MIGHT NOT BE DONE !!
# Note that a course can have several prerequisites. This is why CourseCode alone cannot be a key. If a course has no prerequisites, the string �None� is entered. For a given course a tuple, will exist for every prerequisite for the course.
create table Course (
CourseCode char (6) not null,
CourseName char (50),
PreReq char (6) # "None" entered for no prerequisites
);

# Item 5
# !! MIGHT NOT BE DONE !!
# He might have said this is it, not sure
create table Offering (
CourseCode char (6) not null,
SectionNo int not null,
InstructorID char (9) not null,
Foreign key (InstructorID) references Instructor(InstructorID),
Primary key (CourseCode, SectionNo)
);

# Item 6
#  Note that we would expect that a CourseCode, SectionNo pair in the Offering table must occur in the Course table.
create table Enrollment (
CourseCode char(6) not null,
SectionNo int not null,
StudentID char(9) not null references Student,
Grade char(4) not null,
primary key (CourseCode, StudentID),
foreign key (CourseCode, SectionNo) references Offering(CourseCode, SectionNo)
);

# Item 7
load xml local infile 'U:\\cs363\\UniversityXML\\Person.xml'
into table Person
rows identified by '<Person>';

# Item 8
load xml local infile 'U:\\cs363\\UniversityXML\\Instructor.xml'
into table Instructor
rows identified by '<Instructor>';

# Item 9
load xml local infile 'U:\\cs363\\UniversityXML\\Student.xml'
into table Student
rows identified by '<Student>';

# Item 10
load xml local infile 'U:\\cs363\\UniversityXML\\Course.xml'
into table Course
rows identified by '<Course>';

# Item 11
load xml local infile 'U:\\cs363\\UniversityXML\\Offering.xml'
into table Offering
rows identified by '<Offering>';

# Item 12
load xml local infile 'U:\\cs363\\UniversityXML\\Enrollment.xml'
into table Enrollment
rows identified by '<Enrollment>';

# Item 13
Select StudentID and MentorID from Student
where (Classification = "Junior" or Classification = "Senior") and GPA > 3.8;

# Item 14
# !! Not sure how to determine being taken by a sophomore, check Enrollment? !!
Select distinct CourseCode from Course;

# Item 15
Select * from Student;




