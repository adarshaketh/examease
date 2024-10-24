import * as admin from 'firebase-admin';

// Initialize Firebase Admin SDK
// Use your service account file or Firebase project credentials
const serviceAccount = require('./examease-9f257-firebase-adminsdk-bvj2c-5d03f4db6c.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const firestore = admin.firestore();

// Define Quiz Types
interface QuizQuestion {
  questionId: string;
  questionText: string;
  marks:number;
  options: string[];
  answer: number;
}

interface Exam {
  examId: string;
  title:string;
  description:string;
  imageUrl:string;
  totalMarks:number;
  duration:number; //seconds
  difficulty:number;   //1 is Beginer, 2 is Intermediate, 3 is Hard
  category:number,
  questions:QuizQuestion[]
}

// Function to save quiz data
async function addExamsToFirestore(exam: Exam) {
  try {
    const quizRef = firestore.collection('exams').doc(exam.examId);

    // Save the quiz document to Firestore
    await quizRef.set({
      examId: exam.examId,
      title:exam.title,
      description:exam.description,
      totalMarks:exam.questions.reduce((total, question) => total + question.marks, 0),
      imageUrl:exam.imageUrl,
      duration:exam.duration,
      difficulty:exam.difficulty,
      category:exam.category,
      questions: exam.questions.map((question) => ({
        questionId: exam.examId+'.'+question.questionId,
        questionText: question.questionText,
        marks:question.marks,
        options: question.options,
        answer: question.answer,
      })),
    });

    console.log(`Exam with examId: ${exam.examId} saved successfully!`);
  } catch (error) {
    console.error('Error saving quiz to Firestore:', error);
  }
}

const dataExam: Exam = {
  examId: 'exam8',
  title: 'Basic DBMS Quiz - Set 3',
  description: 'A basic quiz covering foundational database management concepts.',
  totalMarks: 0, // Calculate based on questions
  imageUrl: 'exams/dbms_basic.png',
  duration: 240, // 4 minutes
  difficulty: 1,
  category: 0, // Assuming DBMS category ID is 3
  questions: [
    {
      questionId: 'q1',
      marks: 1,
      questionText: 'Which of the following is a database management system?',
      options: ['Oracle', 'Google', 'Facebook', 'Amazon'],
      answer: 0, // 'Oracle'
    },
    {
      questionId: 'q2',
      marks: 1,
      questionText: 'Which SQL statement is used to extract data from a database?',
      options: ['EXTRACT', 'GET', 'SELECT', 'RETRIEVE'],
      answer: 2, // 'SELECT'
    },
    {
      questionId: 'q3',
      marks: 1,
      questionText: 'A primary key is used to:',
      options: [
        'Ensure unique identification of rows',
        'Establish relationships between tables',
        'Encrypt data in a table',
        'Define data types for columns'
      ],
      answer: 0, // 'Ensure unique identification of rows'
    },
    {
      questionId: 'q4',
      marks: 1,
      questionText: 'Which of the following is NOT a type of database?',
      options: ['Relational', 'Hierarchical', 'Network', 'Encrypted'],
      answer: 3, // 'Encrypted'
    },
    {
      questionId: 'q5',
      marks: 2,
      questionText: 'Which of the following operations is used to delete all records from a table?',
      options: ['DELETE', 'REMOVE', 'TRUNCATE', 'ERASE'],
      answer: 2, // 'TRUNCATE'
    },
    {
      questionId: 'q6',
      marks: 1,
      questionText: 'Which of the following is a property of a relational database?',
      options: [
        'It stores data in hierarchical format',
        'It stores data in tables',
        'It uses blockchain technology',
        'It stores data in files'
      ],
      answer: 1, // 'It stores data in tables'
    },
    {
      questionId: 'q7',
      marks: 2,
      questionText: 'What is the purpose of the SQL WHERE clause?',
      options: [
        'To define a table’s structure',
        'To update records in a table',
        'To filter records based on a condition',
        'To group records in a table'
      ],
      answer: 2, // 'To filter records based on a condition'
    },
    {
      questionId: 'q8',
      marks: 1,
      questionText: 'Which command is used to remove a table in SQL?',
      options: ['DELETE', 'DROP', 'REMOVE', 'ERASE'],
      answer: 1, // 'DROP'
    },
    {
      questionId: 'q9',
      marks: 1,
      questionText: 'Which type of database key uniquely identifies each record in a table?',
      options: ['Foreign key', 'Primary key', 'Unique key', 'Composite key'],
      answer: 1, // 'Primary key'
    },
    {
      questionId: 'q10',
      marks: 2,
      questionText: 'In SQL, which keyword is used to sort records?',
      options: ['SORT', 'ORDER', 'GROUP', 'FILTER'],
      answer: 1, // 'ORDER'
    },
    {
      questionId: 'q11',
      marks: 1,
      questionText: 'What is the main purpose of normalization in DBMS?',
      options: [
        'To reduce redundancy in data',
        'To increase data replication',
        'To encrypt data',
        'To decrease performance'
      ],
      answer: 0, // 'To reduce redundancy in data'
    },
    {
      questionId: 'q12',
      marks: 2,
      questionText: 'Which SQL clause is used to group rows that have the same values in a column?',
      options: ['GROUP BY', 'ORDER BY', 'UNION', 'SELECT'],
      answer: 0, // 'GROUP BY'
    },
    {
      questionId: 'q13',
      marks: 2,
      questionText: 'Which of the following is NOT a SQL data type?',
      options: ['INT', 'VARCHAR', 'BOOLEAN', 'FILE'],
      answer: 3, // 'FILE'
    },
    {
      questionId: 'q14',
      marks: 2,
      questionText: 'What is a FOREIGN KEY in DBMS?',
      options: [
        'A key that encrypts data in the database',
        'A key that uniquely identifies each record',
        'A key that establishes a relationship between two tables',
        'A key that automatically generates numbers'
      ],
      answer: 2, // 'A key that establishes a relationship between two tables'
    },
    {
      questionId: 'q15',
      marks: 2,
      questionText: 'Which SQL function is used to count the number of rows in a table?',
      options: ['COUNT()', 'SUM()', 'ROWCOUNT()', 'CALC()'],
      answer: 0, // 'COUNT()'
    },
  ],
};
// Call the function to create new exam
addExamsToFirestore(dataExam);