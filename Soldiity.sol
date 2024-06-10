// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract CourseFeedbackSystem {
    struct Feedback {
        uint256 uid; // Unique ID for the feedback
        string courseName;
        string feedbackText;
        uint256 rating;
        address sender;
    }

    mapping(uint256 => Feedback) public feedbacks;
    uint256 public feedbackCount;

    event FeedbackSubmitted(uint256 indexed uid, address sender);

    function submitFeedback(
        uint256 _uid,
        string memory _courseName,
        string memory _feedbackText,
        uint256 _rating
    ) public {
        require(_uid != 0, "Invalid UID");
        require(_rating >= 1 && _rating <= 5, "Invalid rating");

        feedbackCount++;
        feedbacks[_uid] = Feedback(_uid, _courseName, _feedbackText, _rating, msg.sender);
        emit FeedbackSubmitted(_uid, msg.sender);
    }

    // Function to get the total number of feedbacks
    function getFeedbackCount() public view returns (uint256) {
        return feedbackCount;
    }
}
