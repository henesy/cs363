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
create table Course (
CourseCode char (6) not null,
CourseName char (50),
PreReq char (6) # "None" entered for no prerequisites
);

# Item 5
create table Offering (
CourseCode char (6) not null,
SectionNo int not null,
InstructorID char (9) not null,
Foreign key (InstructorID) references Instructor(InstructorID),
Primary key (CourseCode, SectionNo)
);

# Item 6
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
Select StudentID and MentorID 
from Student
where (Classification = "Junior" or Classification = "Senior") and GPA > 3.8;

# Item 14
SELECT distinct Offering.CourseCode, Offering.SectionNo
FROM Offering
INNER JOIN Enrollment ON Offering.CourseCode = Enrollment.CourseCode
INNER JOIN Student ON Enrollment.StudentID = Student.StudentID and Student.Classification = 'Sophomore';

# Item 15
SELECT distinct Person.Name, Instructor.Salary
FROM Person
inner join Instructor on Person.ID = Instructor.InstructorID
inner JOIN Student ON Instructor.InstructorID = Student.MentorID and Student.Classification = 'Freshman';

# Item 16
Select sum(Instructor.Salary)
from Instructor
where Instructor.InstructorID not in (select Offering.InstructorID from Offering);

# Item 17
Select Person.Name, Person.DOB
from Person
inner join Student on Person.ID = Student.StudentID
where Year(Person.DOB) = 1976;

# Item 18
Select Person.Name, Instructor.Rank
from Person
inner join Instructor on Person.ID = Instructor.InstructorID
where Instructor.InstructorID not in (select Offering.InstructorID from Offering)
and 
Instructor.InstructorID not in (select Student.MentorID from Student);

# Item 19
Select Student.StudentID, Person.Name, max(Person.DOB)
from Person
inner join Student on Person.ID = Student.StudentID;

# Item 20
Select Person.ID, Person.DOB, Person.Name
from Person
where Person.ID not in (select Student.StudentID from Student) and Person.ID not in (select Instructor.InstructorID from Instructor);

# Item 21
Select Person.name, count(Student.MentorID)
From Instructor
left join Person on Instructor.InstructorID = Person.ID
left join Student on Instructor.InstructorID = MentorID
Group By Instructor.InstructorID;

# Item 22
Select count(Student.StudentID), avg(Student.GPA)
From Student
Group By Student.Classification
Order By Student.Classification;

# Item 23
Select Enrollment.CourseCode, count(Enrollment.StudentID)
From Enrollment
Group By Enrollment.CourseCode
Order By count(Enrollment.StudentID) ASC;

# Item 24
Select Student.StudentID, Student.MentorID
From Student
join Enrollment on Student.StudentID = Enrollment.StudentID
join Offering on Enrollment.CourseCode = Offering.CourseCode
Where Offering.InstructorID = Student.MentorID
AND Offering.CourseCode = Enrollment.CourseCode
AND Enrollment.StudentID = Student.StudentID;

# Item 25
Select Student.StudentID, Person.Name, Student.CreditHours
From Student
left join Person on Student.StudentID = Person.ID
Where Student.Classification = 'freshman'
AND Person.DOB >= '1976-01-01';

# Item 26
Insert Into Person (Name, ID, Address, DOB)
Values ('Briggs Jason', '480293439', '215 North Hyland Avenue', '1975-01-15'); 

Insert Into Student(StudentID, Classification, GPA, MentorID, CreditHours)
Values ('480293439', 'junior', '3.48', '201586985', 75);

Insert Into Enrollment (CourseCode, SectionNo, StudentID, Grade)
Values ('CS311', '2', '480293439', 'A');

Insert Into Enrollment (CourseCode, SectionNo, StudentID, Grade)
Values ('CS330', '1', '480293439', 'A-');

Select *
From Person P
Where P.Name= 'Briggs Jason';

Select *
From Student S
Where S.StudentID= '480293439';

Select *
From Enrollment E
Where E.StudentID = '480293439';

# Item 27
Delete From Enrollment
Where Enrollment.StudentID in 
(Select Student.StudentID 
From Student
Where GPA < 0.5);
                                
Delete From Student
Where Student.GPA < 0.5;

Select *
From Student S
Where S.GPA < 0.5;

# Item 28
Select P.Name, I.Salary
From Instructor I, Person P
Where I.InstructorID = P.ID
and P.Name = 'Ricky Ponting';

Select P.Name, I.Salary
From Instructor I, Person P
Where I.InstructorID = P.ID
and P.Name = 'Darren Lehmann';


Update Instructor
inner join Person on Instructor.InstructorID = Person.ID
Set Instructor.Salary = CASE
	WHEN 
    (
	Select count(Student.MentorID) as total_ricky
	From Person
	left join Student on Person.ID = MentorID
	Where(Person.Name = 'Ricky Ponting')
	)
    >= 5 and Person.Name = 'Ricky Ponting' then Instructor.Salary*1.10
    ELSE Instructor.Salary
END;

Update Instructor
inner join Person on Instructor.InstructorID = Person.ID
Set Instructor.Salary = CASE
	WHEN 
    (
	Select count(Student.MentorID) as total_ricky
	From Person
	left join Student on Person.ID = MentorID
	Where(Person.Name = 'Darren Lehmann')
	)
    >= 5 and Person.Name = 'Darren Lehmann' then Instructor.Salary*1.10
    ELSE Instructor.Salary
END;


Select P.Name, I.Salary
From Instructor I, Person P
Where I.InstructorID = P.ID
and P.Name = 'Ricky Ponting';

Select P.Name, I.Salary
From Instructor I, Person P
Where I.InstructorID = P.ID
and P.Name = 'Darren Lehmann';

# Item 29
Insert Into Person(Name, ID, Address, DOB)
Values ('Trevor Horns', '000957303', '23 Canberra Street', '1964-11-23');

Select *
From Person P
Where P.Name = 'Trevor Horns';

# Item 30
Delete From Student
Where StudentID in
(Select Person.ID From Person where Person.name = 'Jan Austin');

Delete From Person
Where Name = 'Jan Austin';

Select *
From Person P
Where P.Name = 'Jan Austin';