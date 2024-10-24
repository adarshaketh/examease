import * as admin from 'firebase-admin';

// Initialize Firebase Admin SDK
// Use your service account file or Firebase project credentials
const serviceAccount = require('./examease-9f257-firebase-adminsdk-bvj2c-5d03f4db6c.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const firestore = admin.firestore();
interface ExamHistory{
    examId:string;
    duration:number; //seconds took
    score:number;
    noQnsAttempted:number;
    start:number;
  }
  
  interface History{
    userEmail:string;
    totalDuration:number;
    examIds:ExamHistory[];
  }
// Function to save history
async function addHistoryToFirestore(history: History) {
    try {
      const quizRef = firestore.collection('history').doc(history.userEmail);
  
      // Save the quiz document to Firestore
      await quizRef.set({
        userEmail:'',
        examIds:history.examIds.map((exam) => ({
            examId: exam.examId,
            score:exam.score,
            duration:exam.duration,
            start:exam.start, //timestamp
          })),
      });
  
      console.log(`Exam with examId: ${history.userEmail} saved successfully!`);
    } catch (error) {
      console.error('Error saving quiz to Firestore:', error);
    }
  }

  const dataHistory: History = {
    userEmail: 'adarshak103@gmail.com',
    totalDuration:0,

    examIds: [
      {
        examId: 'exam1',
        score:1,
        duration:0,
        noQnsAttempted:2,
        start:1729416320, //timestamp
      },
      {
        examId: 'exam2',
        score:1,
        noQnsAttempted:3,
        duration:55, //sec took
        start:1729416320, //timestamp
      },
    ],
  };
  

  addHistoryToFirestore(dataHistory)