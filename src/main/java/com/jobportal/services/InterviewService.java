package com.jobportal.services;

import com.jobportal.models.Interview;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InterviewService {
    // In a real application, this would interact with a database
    private static final List<Interview> interviews = new ArrayList<>();

    public List<Interview> getInterviewsByRecruiter(String recruiterEmail) {
        if (recruiterEmail == null || recruiterEmail.isEmpty()) {
            throw new IllegalArgumentException("Recruiter email is required");
        }

        // Filter interviews for the given recruiter
        return interviews.stream()
                .filter(interview -> interview.getRecruiterEmail().equals(recruiterEmail))
                .toList();
    }

    public boolean scheduleInterview(Interview interview) {
        if (interview == null) {
            throw new IllegalArgumentException("Interview cannot be null");
        }

        // Generate a unique ID for the interview
        interview.setId(UUID.randomUUID().toString());
        
        // In a real application, this would save to a database
        interviews.add(interview);
        return true;
    }

    public boolean rescheduleInterview(String interviewId, LocalDateTime newDateTime) {
        if (interviewId == null || interviewId.isEmpty() || newDateTime == null) {
            throw new IllegalArgumentException("Interview ID and new date/time are required");
        }

        // Find and update the interview
        for (Interview interview : interviews) {
            if (interview.getId().equals(interviewId)) {
                interview.setDateTime(newDateTime);
                return true;
            }
        }
        return false;
    }

    public boolean cancelInterview(String interviewId) {
        if (interviewId == null || interviewId.isEmpty()) {
            throw new IllegalArgumentException("Interview ID is required");
        }

        // Find and remove the interview
        return interviews.removeIf(interview -> interview.getId().equals(interviewId));
    }

    public boolean updateInterviewStatus(String interviewId, String newStatus) {
        if (interviewId == null || interviewId.isEmpty() || newStatus == null || newStatus.isEmpty()) {
            throw new IllegalArgumentException("Interview ID and new status are required");
        }

        // Find and update the interview status
        for (Interview interview : interviews) {
            if (interview.getId().equals(interviewId)) {
                interview.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }
} 