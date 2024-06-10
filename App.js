import React, { useState, useEffect } from 'react';
import Web3 from 'web3';
import './App.css'; // Import CSS file for styling

const CourseFeedbackSystemABI = [
  {
    "anonymous": false,
    "inputs": [
      {
        "indexed": true,
        "internalType": "uint256",
        "name": "uid",
        "type": "uint256"
      },
      {
        "indexed": false,
        "internalType": "address",
        "name": "sender",
        "type": "address"
      }
    ],
    "name": "FeedbackSubmitted",
    "type": "event"
  },
  {
    "inputs": [
      {
        "internalType": "uint256",
        "name": "_uid",
        "type": "uint256"
      },
      {
        "internalType": "string",
        "name": "_courseName",
        "type": "string"
      },
      {
        "internalType": "string",
        "name": "_feedbackText",
        "type": "string"
      },
      {
        "internalType": "uint256",
        "name": "_rating",
        "type": "uint256"
      }
    ],
    "name": "submitFeedback",
    "outputs": [],
    "stateMutability": "nonpayable",
    "type": "function"
  },
  {
    "inputs": [],
    "name": "feedbackCount",
    "outputs": [
      {
        "internalType": "uint256",
        "name": "",
        "type": "uint256"
      }
    ],
    "stateMutability": "view",
    "type": "function"
  },
  {
    "inputs": [
      {
        "internalType": "uint256",
        "name": "",
        "type": "uint256"
      }
    ],
    "name": "feedbacks",
    "outputs": [
      {
        "internalType": "uint256",
        "name": "uid",
        "type": "uint256"
      },
      {
        "internalType": "string",
        "name": "courseName",
        "type": "string"
      },
      {
        "internalType": "string",
        "name": "feedbackText",
        "type": "string"
      },
      {
        "internalType": "uint256",
        "name": "rating",
        "type": "uint256"
      },
      {
        "internalType": "address",
        "name": "sender",
        "type": "address"
      },
      {
        "internalType": "bool",
        "name": "isSubmitted",
        "type": "bool"
      }
    ],
    "stateMutability": "view",
    "type": "function"
  },
  {
    "inputs": [],
    "name": "getFeedbackCount",
    "outputs": [
      {
        "internalType": "uint256",
        "name": "",
        "type": "uint256"
      }
    ],
    "stateMutability": "view",
    "type": "function"
  }
];

const contractAddress = '0xbef0040b2f1e1470ab0b5754b23cf3b4066ba992';

function App() {
  const [web3, setWeb3] = useState(null);
  const [contract, setContract] = useState(null);
  const [accounts, setAccounts] = useState([]);
  const [feedbackUid, setFeedbackUid] = useState('');
  const [courseName, setCourseName] = useState('');
  const [feedbackText, setFeedbackText] = useState('');
  const [rating, setRating] = useState('');
  const [feedbacks, setFeedbacks] = useState([]);

  useEffect(() => {
    const initWeb3 = async () => {
      try {
        if (window.ethereum) {
          const web3Instance = new Web3(window.ethereum);
          await window.ethereum.enable();
          setWeb3(web3Instance);
          const contractInstance = new web3Instance.eth.Contract(CourseFeedbackSystemABI, contractAddress);
          setContract(contractInstance);
          const accounts = await web3Instance.eth.getAccounts();
          setAccounts(accounts);
        } else {
          console.log('Please install MetaMask extension!');
        }
      } catch (error) {
        console.error('Error initializing Web3:', error);
      }
    };
    initWeb3();
  }, []);

  const fetchFeedbacks = async () => {
    try {
      const count = await contract.methods.getFeedbackCount().call();
      const feedbackList = [];
      for (let i = 1; i <= count; i++) {
        const feedback = await contract.methods.feedbacks(i).call();
        feedbackList.push(feedback);
      }
      setFeedbacks(feedbackList);
    } catch (error) {
      console.error('Error fetching feedbacks:', error);
    }
  };

  useEffect(() => {
    if (contract) {
      fetchFeedbacks();
    }
  }, [contract]);

  const submitFeedback = async () => {
    try {
      await contract.methods
        .submitFeedback(parseInt(feedbackUid), courseName, feedbackText, parseInt(rating))
        .send({ from: accounts[0] });
      console.log('Feedback submitted successfully!');
      setTimeout(() => {
        fetchFeedbacks();
      }, 1000);
    } catch (error) {
      console.error('Error submitting feedback:', error);
    }
  };

  return (
    <div className="App">
      <h1 className="app-title">Course Feedback System</h1>
      <div className="add-feedback">
        <h2>Add Feedback</h2>
        <input
          type="text"
          placeholder="Enter UID"
          value={feedbackUid}
          onChange={(e) => setFeedbackUid(e.target.value)}
          className="add-feedback__input"
        />
        <input
          type="text"
          placeholder="Enter Course Name"
          value={courseName}
          onChange={(e) => setCourseName(e.target.value)}
          className="add-feedback__input"
        />
        <input
          type="text"
          placeholder="Enter Feedback Text"
          value={feedbackText}
          onChange={(e) => setFeedbackText(e.target.value)}
          className="add-feedback__input"
        />
        <input
          type="text"
          placeholder="Enter Rating"
          value={rating}
          onChange={(e) => setRating(e.target.value)}
          className="add-feedback__input"
        />
        <button onClick={submitFeedback} className="add-feedback__button">
          Submit Feedback
        </button>
      </div>
      <div className="feedback-list">
        <h2>Feedbacks</h2>
        <table className="feedback-list__table">
          <thead>
            <tr>
              <th>UID</th>
              <th>Course Name</th>
              <th>Feedback</th>
              <th>Rating</th>
            </tr>
          </thead>
          <tbody>
            {feedbacks.map((feedback, index) => (
              <tr key={index}>
                <td>{feedback.uid.toString()}</td>
                <td>{feedback.courseName}</td>
                <td>{feedback.feedbackText}</td>
                <td>{feedback.rating.toString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default App;